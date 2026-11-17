/*
 * Hospital - The Game
 * ©2005 by Tobias Fritz
 *
 * JavaScript port of the original Java/AWT version (src/main/java/hospital).
 *
 * The original draws directly onto an AWT Canvas without double buffering:
 * nothing is ever cleared automatically, every method paints over what is
 * already there. To reproduce this look (smoke trails, blood stains, flicker
 * free stick figures, aliased text) the port renders into a persistent pixel
 * buffer with its own rasterizer that mimics the AWT primitives
 * (fillRect, drawRect, fillOval, drawOval, drawLine, drawString, clipRect).
 *
 * The game thread of the original (Game.run with Thread.sleep) is emulated by
 * a generator function that yields the sleep durations to a scheduler.
 */
(function () {
    'use strict';

    // ---------------------------------------------------------------------
    // Framebuffer & AWT like rasterizer
    // ---------------------------------------------------------------------

    // The Java frame has a size of 625x446 incl. window decorations,
    // which leaves this area for the canvas.
    const WIDTH = 620;
    const HEIGHT = 412;

    const canvas = document.getElementById('sheet');
    canvas.width = WIDTH;
    canvas.height = HEIGHT;
    const ctx = canvas.getContext('2d');
    const image = ctx.createImageData(WIDTH, HEIGHT);
    const pixels = new Uint32Array(image.data.buffer);
    let dirty = true;

    function rgb(r, g, b) {
        return ((255 << 24) | (b << 16) | (g << 8) | r) >>> 0;
    }

    const BLACK = rgb(0, 0, 0);
    const WHITE = rgb(255, 255, 255);
    const BLUE = rgb(0, 0, 255);

    pixels.fill(BLACK); // Sheet.setBackground(Color.black)

    // Offscreen canvas used to rasterize glyphs (later blitted without anti aliasing)
    const textCanvas = document.createElement('canvas');
    const textCtx = textCanvas.getContext('2d', {willReadFrequently: true});
    const textCache = new Map();

    /** Mimics java.awt.Graphics as returned by Canvas.getGraphics() */
    class Graphics {
        constructor() {
            this.color = BLACK; // default foreground color of the component
            this.clip = {x0: 0, y0: 0, x1: WIDTH, y1: HEIGHT}; // exclusive end
            this.font = {name: 'Dialog', bold: false, size: 12};
        }

        setColor(c) {
            this.color = c;
        }

        setFont(font) {
            this.font = font;
        }

        clipRect(x, y, w, h) {
            const c = this.clip;
            c.x0 = Math.max(c.x0, x);
            c.y0 = Math.max(c.y0, y);
            c.x1 = Math.min(c.x1, x + w);
            c.y1 = Math.min(c.y1, y + h);
        }

        dispose() {
        }

        plot(x, y) {
            const c = this.clip;
            if (x >= c.x0 && x < c.x1 && y >= c.y0 && y < c.y1) {
                pixels[y * WIDTH + x] = this.color;
            }
        }

        hline(x0, x1, y) {
            const c = this.clip;
            if (y < c.y0 || y >= c.y1) return;
            if (x0 < c.x0) x0 = c.x0;
            if (x1 >= c.x1) x1 = c.x1 - 1;
            if (x1 < x0) return;
            const row = y * WIDTH;
            pixels.fill(this.color, row + x0, row + x1 + 1);
        }

        fillRect(x, y, w, h) {
            if (w <= 0 || h <= 0) return;
            for (let j = y; j < y + h; j++) {
                this.hline(x, x + w - 1, j);
            }
            dirty = true;
        }

        drawRect(x, y, w, h) {
            if (w < 0 || h < 0) return;
            this.hline(x, x + w, y);
            this.hline(x, x + w, y + h);
            for (let j = y + 1; j < y + h; j++) {
                this.plot(x, j);
                this.plot(x + w, j);
            }
            dirty = true;
        }

        drawLine(x0, y0, x1, y1) { // Bresenham, both end points included
            let dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
            let dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
            let err = dx + dy;
            for (; ;) {
                this.plot(x0, y0);
                if (x0 === x1 && y0 === y1) break;
                const e2 = 2 * err;
                if (e2 >= dy) {
                    err += dy;
                    x0 += sx;
                }
                if (e2 <= dx) {
                    err += dx;
                    y0 += sy;
                }
            }
            dirty = true;
        }

        fillOval(x, y, w, h) { // covers the pixels x..x+w-1, y..y+h-1
            if (w <= 0 || h <= 0) return;
            const rx = w / 2, ry = h / 2, cx = x + rx, cy = y + ry;
            for (let j = 0; j < h; j++) {
                const dy = (y + j + 0.5 - cy) / ry;
                const t = 1 - dy * dy;
                if (t <= 0) continue;
                const hw = rx * Math.sqrt(t);
                const x0 = Math.ceil(cx - hw - 0.5);
                const x1 = Math.floor(cx + hw - 0.5);
                if (x1 >= x0) this.hline(x0, x1, y + j);
            }
            dirty = true;
        }

        drawOval(x, y, w, h) { // outline covering the pixels x..x+w, y..y+h
            if (w < 0 || h < 0) return;
            const rx = w / 2, ry = h / 2, cx = x + rx, cy = y + ry;
            if (rx === 0 || ry === 0) {
                this.drawLine(x, y, x + w, y + h);
                return;
            }
            for (let px = x; px <= x + w; px++) { // for every column top & bottom pixel
                const dx = (px - cx) / rx;
                const d = ry * Math.sqrt(Math.max(0, 1 - dx * dx));
                const yb = Math.floor(cy + d + 0.5);
                this.plot(px, yb);
                this.plot(px, 2 * y + h - yb);
            }
            for (let py = y; py <= y + h; py++) { // for every row left & right pixel
                const dy = (py - cy) / ry;
                const d = rx * Math.sqrt(Math.max(0, 1 - dy * dy));
                const xr = Math.floor(cx + d + 0.5);
                this.plot(xr, py);
                this.plot(2 * x + w - xr, py);
            }
            dirty = true;
        }

        drawString(text, x, y) { // (x,y) is the left end of the baseline
            if (text.length === 0) return;
            const glyphs = rasterizeText(text, this.font);
            for (let i = 0; i < glyphs.points.length; i += 2) {
                this.plot(x + glyphs.points[i], y + glyphs.points[i + 1]);
            }
            dirty = true;
        }
    }

    const FONT_FAMILIES = {
        'Arial': 'Arial, "Liberation Sans", Arimo, Helvetica, sans-serif',
        'Courier New': '"Courier New", "Liberation Mono", Cousine, monospace',
        'Times New Roman': '"Times New Roman", "Liberation Serif", Tinos, Times, serif',
        'Wingdings': 'Wingdings, sans-serif',
        'Serif': 'serif',
        'Dialog': 'sans-serif'
    };

    /** Renders a text once and keeps the opaque pixels (AWT draws text without anti aliasing) */
    function rasterizeText(text, font) {
        const css = (font.bold ? 'bold ' : '') + font.size + 'px ' + (FONT_FAMILIES[font.name] || 'sans-serif');
        const key = css + '|' + text;
        let entry = textCache.get(key);
        if (entry) return entry;
        textCtx.font = css;
        const width = Math.ceil(textCtx.measureText(text).width) + 4;
        const height = font.size * 2;
        const baseline = Math.ceil(font.size * 1.5);
        textCanvas.width = Math.max(1, width);
        textCanvas.height = height;
        textCtx.font = css; // resizing the canvas resets the context
        textCtx.textBaseline = 'alphabetic';
        textCtx.fillStyle = '#fff';
        textCtx.fillText(text, 0, baseline);
        const data = textCtx.getImageData(0, 0, width, height).data;
        const points = [];
        for (let j = 0; j < height; j++) {
            for (let i = 0; i < width; i++) {
                if (data[(j * width + i) * 4 + 3] >= 110) {
                    points.push(i, j - baseline);
                }
            }
        }
        entry = {points};
        textCache.set(key, entry);
        return entry;
    }

    function getGraphics() {
        return new Graphics();
    }

    // ---------------------------------------------------------------------
    // Methods.java
    // ---------------------------------------------------------------------

    // Variable Values______________________________________
    let position = 300; // Position of the Paddle
    let y = 250;
    const xstart = 100;
    let x = xstart;
    let xcanvas = 111; // For the conversion of the X-values of the functions to Canvas
    let ycanvas = 111; // For the conversion of the Y-values of the functions to Canvas
    let paddle_length = 50; // Length of the trampoline between the people

    // Game Flow____________
    let speed = 10;
    let lives = 490;
    let points = 0;
    let selection = 1; // which flight path should be selected
    let help = true;
    let pause = true;
    let playing = false;
    let really_end = false;
    let game = true;
    let gameover = false;

    //draw_hospital____________________
    let y_person = 20;
    let x_person = 5;
    let pos_x_smoke = 76;
    let pos_y_smoke = 18;

    // Functions___________
    const start = 200; // Initial X-value for the flight paths
    const stop = 600;  // Maximum X-value for the flight paths
    let a0 = 250;
    let a1 = -0.8;
    let a2 = 0;
    let a3 = 0;
    let a4 = 0;

    // Animation______________
    let gameover_blink = 0;
    let counter = 0; // People wave their arms

    /** Java: (int) cast of a double */
    function int(v) {
        return Math.trunc(v);
    }

    function draw_shape(posx, posy, posx2, posy2, red, yellow, blue, form) {
        const g = getGraphics();
        g.setColor(rgb(red, yellow, blue));
        if (form === 0) { // Full rectangle
            g.fillRect(posx, posy, posx2, posy2);
        }
        if (form === 1) { // Rectangle frame
            g.drawRect(posx, posy, posx2, posy2);
        }
        if (form === 3) { // Full oval
            g.fillOval(posx, posy, posx2, posy2);
        }
        if (form === 4) { // Oval frame
            g.drawOval(posx, posy, posx2, posy2);
        }
        g.dispose();
    }

    function draw_line(start_point_x, start_point_y, end_point_x, end_point_y) {
        const g = getGraphics();
        g.setColor(WHITE);
        g.drawLine(start_point_x, start_point_y, end_point_x, end_point_y);
        g.dispose();
    }

    function draw_text(text, number, posx, posy, red, yellow, blue, font_type, font_size) {
        const g = getGraphics();
        g.setColor(rgb(red, yellow, blue));
        let font = {name: 'Serif', bold: false, size: 12};
        if (font_type === 0) font = {name: 'Arial', bold: false, size: font_size};
        if (font_type === 1) font = {name: 'Arial', bold: true, size: font_size};
        if (font_type === 2) font = {name: 'Courier New', bold: false, size: font_size};
        if (font_type === 3) font = {name: 'Wingdings', bold: false, size: font_size};
        if (font_type === 4) font = {name: 'Times New Roman', bold: false, size: font_size};
        g.setFont(font);
        if (number >= 1) {
            g.drawString(String(number), posx, posy);
        } else {
            g.drawString(String(text), posx, posy);
            g.dispose();
        }
    }

    function path01() { // Linear flight: a0:initial height; a1:angle(-0.5 to -1.1)
        a0 = 380 - int(Math.random() * 220); // Random value between 160 and 380
        a1 = -(1.1 - int(Math.random() * 0.6)); // Random value between -0.5 and -1.1
        a2 = 0;
        a3 = 0;
        a4 = 0;
    }

    function path02() { // Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = 300 - int(Math.random() * 275); // Random value between 25 and 300
        a1 = 1;
        a2 = -0.003;
        a3 = 0;
        a4 = -0;
    }

    function path03() { // Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = 380 - int(Math.random() * 240); // Random value between 140 and 380
        a2 = -0.005 + int(Math.random() * 0.003); // Random value between -0.005 and -0.002
        a1 = 0;
        a3 = 0;
        a4 = 0;
    }

    function draw_person(posx, posy) {
        const g = getGraphics();
        g.setColor(WHITE);
        counter = counter + 1; // Let people wave their arms
        if (counter >= 0) {
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16);  // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); // left arm down
            g.setColor(BLACK);
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);   // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9);  // left arm up
            g.setColor(WHITE);
        }
        if (counter >= 20) {
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);   // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9);  // left arm up
            g.setColor(BLACK);
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16);  // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); // left arm down
            g.setColor(WHITE);
            if (counter >= 40) {
                counter = 0;
            }
        }

        g.drawOval(posx, posy, 7, 7);                         // Head
        g.drawOval(posx - 1, posy - 1, 8, 8);                 // Head
        g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16);  // Neck
        g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24); // right leg
        g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24); // left leg
        g.dispose();
    }

    function draw_paddle(position) { // Draw paddle
        const g = getGraphics();
        g.setColor(WHITE);
        let posx = position - 10;
        const posy = 380;
        let temp = 1;
        draw_shape(position, 388, paddle_length, 5, 0, 0, 109, 0); // Trampoline
        while (temp >= 0) {
            g.drawOval(posx, posy, 7, 7);                 // Head
            g.drawOval(posx - 1, posy - 1, 8, 8);         // Head
            if (temp === 0) {
                g.drawLine(posx + 3, posy + 12, posx - 4, posy + 9);   // left arm up
                g.drawLine(posx + 3, posy + 14, posx - 4, posy + 11);  // left arm up
            } else {
                g.drawLine(posx + 3, posy + 12, posx + 10, posy + 9);  // right arm up
                g.drawLine(posx + 3, posy + 14, posx + 10, posy + 11); // right arm up
            }
            g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16);   // Neck
            g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24);  // right leg
            g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24);  // left leg
            temp = temp - 1;
            posx = position + paddle_length + 4;
        }
        g.dispose();
        const g2 = getGraphics();
        g2.clipRect(position, 388, paddle_length, 5);
        g2.setColor(BLUE);
        g2.fillOval(position - 20, 385, 90, 10);
        g2.setColor(BLACK);
        g2.fillOval(position - 20, 360, 90, 31);
        g2.dispose();
    }

    function clear_paddle() { // Paint over paddle with black
        const g = getGraphics();
        g.setColor(BLACK);
        g.fillRect(55, 376, 575, 30);
        g.dispose();
    }

    function draw_hospital() { // Hospital
        if (x <= 105) {
            draw_shape(0, 15, 100, 405, 102, 102, 102, 0); // House
            draw_shape(50, 364, 50, 12, 214, 237, 245, 0); // Hospital sign
            draw_shape(50, 376, 50, 60, 0, 0, 0, 0);       // Door
            draw_shape(5, 20, 16, 22, 0, 206, 255, 0);     // Window
            draw_text('Hospital', 0, 52, 375, 255, 0, 0, 0, 12);
        }
        draw_shape(74, 16, 14, 14, 167, 167, 167, 3); // Smoke
        // draw_smoke_________________________
        pos_x_smoke = pos_x_smoke + 2;
        pos_y_smoke = pos_y_smoke - 1;
        const length_smoke = int(Math.random() * 16) + 4;
        const height_smoke = int(Math.random() * 16) + 4;
        const smoke_color = int(Math.random() * 240) + 15;
        draw_shape(pos_x_smoke, pos_y_smoke, length_smoke, height_smoke, smoke_color, smoke_color, smoke_color, 3);
        if (pos_y_smoke <= 0) {
            pos_x_smoke = 52;
            pos_y_smoke = 22;
        }
        //___________________________________
        let xf = 5;
        let yf = 20;
        x_person = x_person + 1;
        if (x_person >= 80) {
            y_person = y_person + 40;
            x_person = 0;
        }
        if (y_person >= 320) {
            x_person = 5;
            y_person = 20;
        }
        for (let i = 7; i >= 0; i = i - 1) { // Draw windows
            draw_patient(xf, yf);
            for (let i2 = 1; i2 >= 0; i2 = i2 - 1) {
                xf = xf + 30;
                draw_patient(xf, yf);
            }
            yf = yf + 40;
            xf = 5;
        }
        draw_shape(pos_x_smoke, pos_y_smoke, length_smoke, height_smoke, smoke_color, smoke_color, smoke_color, 3);
    }

    function draw_patient(xf, yf) {
        const g2 = getGraphics();
        draw_shape(xf, yf, 16, 22, 0, 206, 255, 0);
        g2.clipRect(xf, yf, 16, 22); // Area in which the shadow is painted is formed
        g2.drawOval(x_person - 1, y_person + 4, 8, 8);                          // Head
        g2.drawLine(x_person + 3, y_person + 11, x_person + 3, y_person + 21);  // Neck
        g2.drawLine(x_person + 3, y_person + 21, x_person + 8, y_person + 29);  // Right leg
        g2.drawLine(x_person + 3, y_person + 21, x_person - 2, y_person + 29);  // Left leg
        g2.drawLine(x_person + 3, y_person + 17, x_person + 10, y_person + 14); // Right arm up
        g2.drawLine(x_person + 3, y_person + 19, x_person + 10, y_person + 16); // Right arm up
        g2.dispose();
    }

    function draw_points() { // Score board
        draw_shape(540, 42, 60, 16, 174, 186, 204, 0);
        draw_text('', points, 545, 55, 255, 0, 0, 1, 14);
        draw_shape(500, 5, 112, 62, 250, 250, 250, 1);
        draw_shape(500, 5, 111, 61, 250, 250, 250, 1);
        draw_shape(500, 5, 110, 60, 255, 255, 255, 1);
        draw_person(525, 10);
        draw_person(555, 10);
        draw_person(585, 10);
        draw_text('L:', 0, 506, 28, 255, 255, 255, 1, 12);
        draw_text('P:', 0, 506, 53, 255, 255, 255, 1, 12);
    }

    function draw_help() { // Help menu
        const posx = 220;
        const posy = 120;
        draw_shape(posx - 5, posy - 15, 158, 158, 0, 0, 0, 0); // paints over help field with black
        if (help === true) {
            draw_text('                  HELP', 0, posx, posy, 255, 255, 255, 1, 12);
            draw_text('    Action                     Key', 0, posx, posy + 24, 255, 255, 255, 1, 12);
            draw_text('Start Game                   S', 0, posx, posy + 40, 255, 255, 255, 1, 12);
            draw_text('Pause                            P', 0, posx, posy + 52, 255, 255, 255, 1, 12);
            draw_text('Restart Game               N', 0, posx, posy + 63, 255, 255, 255, 1, 12);
            draw_text('End Game                  ESC', 0, posx, posy + 76, 255, 255, 255, 1, 12);
            draw_text('Switch Difficultly        1-3', 0, posx, posy + 89, 255, 255, 255, 1, 12);
            draw_text('Show Help                    H', 0, posx, posy + 102, 255, 255, 255, 1, 12);
            draw_text('Goal: Save as many lives', 0, posx, posy + 125, 255, 255, 255, 1, 12);
            draw_text('          as possible.', 0, posx, posy + 138, 255, 255, 255, 1, 12);
            draw_shape(posx - 4, posy - 14, 155, 156, 255, 255, 255, 1);
        }
    }

    function gameOver() { // Game Over Text
        draw_text('Game Over', 0, 175, 200, 255, 0, 0, 1, 50);
        if (gameover_blink === 0) {
            draw_text('Restart Game          N', 0, 220, 350, 255, 0, 0, 1, 12);
            gameover_blink = gameover_blink + 1;
        } else {
            draw_text('Restart Game          N', 0, 220, 350, 255, 255, 255, 1, 12);
            gameover_blink = gameover_blink - 1;
        }
        draw_text('End Game               Esc', 0, 220, 360, 255, 255, 255, 1, 12);
    }

    function exitGame() { // System.exit(0): the window closes. In the browser: start over.
        game = false;
        pixels.fill(BLACK);
        dirty = true;
        setTimeout(function () {
            window.location.reload();
        }, 300);
    }

    function keyPressed(key) {
        if (!game) return;
        if (key === '1') {
            paddle_length = 40;
        }
        if (key === '2') {
            paddle_length = 50;
        }
        if (key === '3') {
            paddle_length = 60;
        }
        if (key === 'S') {
            help = false;
            draw_help();
            pause = false;
            playing = true;
        }
        if (key === 'P') {
            if (pause === true) {
                pause = false;
                draw_text('Pause', 0, 260, 200, 0, 0, 0, 1, 25); // clear pause
            } else {
                pause = true;
                draw_text('Pause', 0, 260, 200, 255, 255, 255, 1, 25); // draw pause
            }
        }
        if (key === 'ESC') { // QUIT
            pause = true;
            really_end = true;
            gameover = false; // so that GameOver text is not overwritten
            draw_shape(241, 162, 114, 50, 0, 0, 0, 0);
            draw_shape(241, 162, 114, 50, 255, 255, 255, 1);
            draw_text('Really Quit', 0, 265, 180, 255, 255, 255, 1, 12);
            draw_text('Yes', 0, 270, 198, 255, 255, 255, 1, 12);
            draw_text('No', 0, 310, 198, 255, 255, 255, 1, 12);
            draw_line(270, 200, 279, 200);
            draw_line(310, 200, 318, 200);
        }
        if (key === 'Y') {
            if (really_end === true) {
                exitGame();
                return;
            }
        }
        if (key === 'N') { // RESTART
            if (really_end === true) {
                draw_shape(238, 158, 118, 56, 0, 0, 0, 0);
                pause = false;
                really_end = false;
                if (gameover === false && lives >= 550) { // so that GameOver text is not overwritten
                    gameover = true;
                }
            } else { // everything is reset to default values
                pause = false;
                playing = true;
                draw_shape(0, 0, 650, 500, 0, 0, 0, 0);
                points = 0;
                lives = 490;
                speed = 10;
                gameover = false;
                selection = 1;
                x = xstart;
                y = 300;
            }
        }
        if (key === 'H') { // HELP
            if (help === true) {
                pause = false;
                help = false;
                draw_help();
            } else {
                pause = true;
                help = true;
                draw_help();
            }
        }
        if (key === 'LEFT') {
            if (position <= start - 100 || pause === true || gameover === true) {
                // if the trampoline goes too far to the left, or pause is pressed, it stays in place
            } else {
                position = position - paddle_length;
                clear_paddle();
                draw_paddle(position);
            }
        }
        if (key === 'RIGHT') {
            if (position >= stop - 50 || pause === true || gameover === true) {
                // if the trampoline goes too far to the right, or pause is pressed, it stays in place
            } else {
                position = position + paddle_length;
                clear_paddle();
                draw_paddle(position);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Game.java - game flow (the original game thread)
    // Every "yield n" corresponds to a Thread.sleep(n)
    // ---------------------------------------------------------------------

    function* run() {
        while (game === true) {
            while (playing === true) {
                while (y >= 5) {
                    if (pause === false) {
                        draw_shape(x - 7, ycanvas - 3, 16, 31, 0, 0, 0, 3); // Person is deleted
                        draw_shape(102, 360, 575, 30, 0, 0, 0, 0); // delete pixel errors above the people
                        const ydouble = a0 + a1 * x + a2 * Math.pow(x, 2) + a3 * Math.pow(x, 3) + a4 * Math.pow(x, 4); // Polynomial function of 4th degree
                        y = int(ydouble);
                        xcanvas = x;        // Conversion for Canvas
                        ycanvas = 380 - y;  // Conversion for Canvas
                        draw_person(x, ycanvas);
                        draw_paddle(position);
                        x = x + 1; // increase x-value by 1
                        yield speed;
                        if ((y <= 20) && (y <= 18) && ((x >= position) && (x <= (position + paddle_length))) || (x >= stop) || (x <= xstart)) { // Bounce condition
                            points = points + 1;
                            selection = int(Math.random() * 3 + 1);
                            if (selection === 1) {
                                path01();
                            }
                            if (selection === 2) {
                                path02();
                            }
                            if (selection === 3) {
                                path03();
                            }
                            x = xstart;
                            y = 300;
                        }
                        draw_hospital();
                        draw_points();

                        //________________Levels
                        if (ieeeRemainder(points, 15) === 0 && points > 0) { // every 15 points the game speed is increased and an additional point is awarded
                            speed = speed - 1;
                            points = points + 1;
                        }
                    } else {
                        // Pause
                        yield 500;
                    }
                }// while y>=5
                if (lives <= 550) {
                    draw_shape(x - 4, 400, 16, 9, 255, 45, 52, 3); // Blood stain
                    clear_paddle();
                    lives = lives + 30;
                    draw_shape(lives, 8, 18, 30, 255, 45, 52, 3); // Paint over lives
                    path02();
                    x = xstart;
                    y = 250;
                } else {
                    gameover = true;
                    playing = false;
                }
            }// playing
            if (really_end === false) { // Help is constantly painted as long as the Esc button is not pressed
                draw_help();
            }
            if (gameover === true) {
                gameOver();
            }
            yield 1000;
        }// game
    }

    function ieeeRemainder(a, b) {
        return a - b * Math.round(a / b);
    }

    // ---------------------------------------------------------------------
    // Scheduler: runs the game "thread" in real time & presents the buffer
    // ---------------------------------------------------------------------

    const thread = run();
    let nextWake = 0;

    function frame(now) {
        if (now - nextWake > 250) {
            nextWake = now; // the tab was in background: don't fast forward
        }
        let steps = 0;
        while (game && nextWake <= now && steps < 400) {
            const r = thread.next();
            if (r.done) {
                game = false;
                break;
            }
            // Thread.sleep(): negative values are not allowed, 0 still takes a little time
            nextWake += Math.max(1, r.value);
            steps++;
        }
        if (dirty) {
            ctx.putImageData(image, 0, 0);
            dirty = false;
        }
        requestAnimationFrame(frame);
    }

    // Game.main()
    draw_help();
    requestAnimationFrame(function (now) {
        nextWake = now;
        frame(now);
    });

    // ---------------------------------------------------------------------
    // Input: keyboard & on screen buttons
    // ---------------------------------------------------------------------

    const KEY_MAP = {
        '1': '1', '2': '2', '3': '3',
        's': 'S', 'p': 'P', 'n': 'N', 'h': 'H', 'y': 'Y',
        'escape': 'ESC', 'esc': 'ESC',
        'arrowleft': 'LEFT', 'left': 'LEFT',
        'arrowright': 'RIGHT', 'right': 'RIGHT'
    };

    window.addEventListener('keydown', function (e) {
        if (e.ctrlKey || e.metaKey || e.altKey) return;
        const key = KEY_MAP[e.key.toLowerCase()];
        if (key) {
            e.preventDefault();
            keyPressed(key); // like AWT, auto repeat of held keys is passed through
        }
    });

    // On screen buttons (holding a button repeats the key like a keyboard)
    document.querySelectorAll('[data-key]').forEach(function (button) {
        const key = button.getAttribute('data-key');
        const repeat = button.hasAttribute('data-repeat');
        let delayTimer = null;
        let repeatTimer = null;

        function release() {
            clearTimeout(delayTimer);
            clearInterval(repeatTimer);
            delayTimer = repeatTimer = null;
            button.classList.remove('down');
        }

        button.addEventListener('pointerdown', function (e) {
            e.preventDefault();
            release();
            button.classList.add('down');
            if (button.setPointerCapture) {
                try {
                    button.setPointerCapture(e.pointerId);
                } catch (ignored) {
                }
            }
            keyPressed(key);
            if (repeat) {
                delayTimer = setTimeout(function () {
                    repeatTimer = setInterval(function () {
                        keyPressed(key);
                    }, 60);
                }, 400);
            }
        });
        ['pointerup', 'pointercancel', 'lostpointercapture'].forEach(function (type) {
            button.addEventListener(type, release);
        });
        button.addEventListener('contextmenu', function (e) {
            e.preventDefault();
        });
    });

    // Show the on screen controls on touch devices (or with ?touch / ?touch=0)
    const params = new URLSearchParams(window.location.search);
    let touch = window.matchMedia('(pointer: coarse)').matches || 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    if (params.has('touch')) {
        touch = params.get('touch') !== '0';
    }
    document.body.classList.toggle('touch', touch);
    window.addEventListener('touchstart', function () {
        document.body.classList.add('touch');
    }, {once: true, passive: true});

    const fullscreen = document.getElementById('fullscreen');
    if (fullscreen) {
        const el = document.documentElement;
        const request = el.requestFullscreen || el.webkitRequestFullscreen;
        if (!request) {
            fullscreen.hidden = true;
        }
        fullscreen.addEventListener('click', function () {
            if (document.fullscreenElement || document.webkitFullscreenElement) {
                (document.exitFullscreen || document.webkitExitFullscreen).call(document);
                return;
            }
            const p = request && request.call(el);
            if (p && p.then) {
                p.then(function () {
                    if (screen.orientation && screen.orientation.lock) {
                        screen.orientation.lock('landscape').catch(function () {
                        });
                    }
                }).catch(function () {
                });
            }
        });
    }
})();
