
// ©2005 by Tobias Fritz

package hospital;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class methods extends Frame implements KeyListener {

    // Variable Values______________________________________
    int position = 300; // Position of the Paddle
    int y = 250;
    int xstart = 100;
    int x = xstart;
    int xcanvas = 111; // For the conversion of the X-values of the functions to Canvas
    int ycanvas = 111; // For the conversion of the Y-values of the functions to Canvas
    int paddle_length = 50; // Length of the trampoline between the people
    //______________________________________________________

    // Game Flow____________
    int speed = 10;
    int lives = 490;
    int points = 0;
    int selection = 1; // which flight path should be selected
    static boolean help = true;
    static boolean pause = true;
    static boolean playing = false;
    static boolean really_end = false;
    static boolean game = true;
    static boolean gameover = false;

    // Colors________________
    static Color color;
    static int red = 100;
    static int yellow = 25;
    static int blue = 0;
    //______________________

    // Font___________________________________________________
    static Font font = new Font("Serif", Font.PLAIN, 12);
    //__________________________________________________________

    //draw_hospital____________________
    int y_person = 20;
    int x_person = 5;
    int pos_x_smoke = 76;
    int pos_y_smoke = 18;
    //_____________________________________

    // Functions___________
    int start = 200; // Initial X-value for the flight paths
    int stop = 600;  // Maximum X-value for the flight paths
    double a0 = 250;
    double a1 = -0.8;
    double a2 = 0;
    double a3 = 0;
    double a4 = 0;
    //_____________________

    // Animation______________
    int gameover_blink = 0;
    static int counter = 0; // People wave their arms
    //________________________

    // User Interface________
    static Canvas Sheet;
    //_______________________

    public methods() {
        Sheet = new Canvas();
        Sheet.setBounds(0, 0, 600, 400);
        Sheet.setBackground(Color.black);
        Sheet.addKeyListener(this);
        Sheet.requestFocus(); // should mark my start the Canvas
        Sheet.getFocusListeners();
        Sheet.isEnabled();
        Sheet.isFocusable();
        add(Sheet);
        color = new Color(red, yellow, blue);
    }

    static public void draw_shape(int posx, int posy, int posx2, int posy2, int red_intern, int yellow_intern, int blue_intern, int form) {
        Graphics g = Sheet.getGraphics();
        red = red_intern;
        yellow = yellow_intern;
        blue = blue_intern;
        color = new Color(red, yellow, blue);
        g.setColor(color);
        if (form == 0) { // Full rectangle
            g.fillRect(posx, posy, posx2, posy2);
        }
        if (form == 1) { // Rectangle frame
            g.drawRect(posx, posy, posx2, posy2);
        }
        if (form == 3) { // Full oval
            g.fillOval(posx, posy, posx2, posy2);
        }
        if (form == 4) { // Oval frame
            g.drawOval(posx, posy, posx2, posy2);
        }
        g.dispose();
    }

    static public void draw_line(int start_point_x, int start_point_y, int end_point_x, int end_point_y) {
        Graphics g = Sheet.getGraphics();
        g.setColor(Color.white);
        g.drawLine(start_point_x, start_point_y, end_point_x, end_point_y);
        g.dispose();
    }

    static public void draw_text(String text, int number, int posx, int posy, int red_intern, int yellow_intern, int blue_intern, int font_type, int font_size) {
        Graphics g = Sheet.getGraphics();
        red = red_intern;
        yellow = yellow_intern;
        blue = blue_intern;
        color = new Color(red, yellow, blue);
        g.setColor(color);
        if (font_type == 0) {
            font = new Font("Arial", Font.PLAIN, font_size);
        }
        if (font_type == 1) {
            font = new Font("Arial", Font.BOLD, font_size);
        }
        if (font_type == 2) {
            font = new Font("Courier New", Font.PLAIN, font_size);
        }
        if (font_type == 3) {
            font = new Font("Wingdings", Font.PLAIN, font_size);
        }
        if (font_type == 4) {
            font = new Font("Times New Roman", Font.PLAIN, font_size);
        }
        g.setFont(font);
        if (number >= 1) {
            g.drawString(String.valueOf(number), posx, posy);
        } else {
            g.drawString(String.valueOf(text), posx, posy);
            g.dispose();
        }
    }

    public void path01() { //	Linear flight: a0:initial height; a1:angle(-0.5 to -1.1)
        a0 = 380 - ((int) (Math.random() * 220)); // Random value between 160 and 380
        System.out.println("a0 " + a0);
        a1 = -(1.1 - (int) (Math.random() * 0.6)); // Random value between -0.5 and -1.1
        System.out.println("a1 " + a1);
        a2 = 0;
        a3 = 0;
        a4 = 0;
    }

    public void path02() { //	Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = 300 - ((int) (Math.random() * 275)); // Random value between 25 and 300
        a1 = 1;
        a2 = -0.003;
        a3 = 0;
        a4 = -0;
    }

    public void path03() { // Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = 380 - ((int) (Math.random() * 240)); // Random value between 140 and 380
        a2 = -0.005 + ((int) (Math.random() * 0.003)); // Random value between -0.005 and -0.002
        a1 = 0;
        a3 = 0;
        a4 = 0;
    }

    static public void draw_person(int posx, int posy) {
        Graphics g = Sheet.getGraphics();
        g.setColor(Color.white);
        counter = counter + 1; // Let people wave their arms
        if (counter >= 0) {
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16);  // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); // left arm down
            g.setColor(Color.black);
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);   // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9);  // left arm up
            g.setColor(Color.white);
        }
        if (counter >= 20) {
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);   // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9);  // left arm up
            g.setColor(Color.black);
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16);  // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); // left arm down
            g.setColor(Color.white);
            if (counter >= 40) {
                counter = 0;
            }
        }

        g.drawOval(posx, posy, 7, 7);                          // Head
        g.drawOval(posx - 1, posy - 1, 8, 8);             // Head
        g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16);  // Neck
        g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24); // right leg
        g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24); // left leg
        g.dispose();
    }

    public void draw_paddle(int position) { //Draw paddle
        Graphics g = Sheet.getGraphics();
        g.setColor(Color.white);
        int posx = position - 10;
        int posy = 380;
        int temp = 1;
        draw_shape(position, 388, paddle_length, 5, 0, 0, 109, 0); // Trampoline
        while (temp >= 0) {
            g.drawOval(posx, posy, 7, 7);                 // Head
            g.drawOval(posx - 1, posy - 1, 8, 8);    // Head
            if (temp == 0) {
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
        Graphics g2 = Sheet.getGraphics();
        g2.clipRect(position, 388, paddle_length, 5);
        g2.setColor(Color.blue);
        g2.fillOval(position - 20, 385, 90, 10);
        g2.setColor(Color.black);
        g2.fillOval(position - 20, 360, 90, 31);
        g2.dispose();
    }

    public void clear_paddle() { // Paint over paddle with black
        Graphics g = Sheet.getGraphics();
        g.setColor(Color.black);
        g.fillRect(55, 376, 575, 30);
        g.dispose();
    }

    public void draw_hospital() { // Hospital
        Graphics g = Sheet.getGraphics();
        if (x <= 105) {
            draw_shape(0, 15, 100, 405, 102, 102, 102, 0); // House
            draw_shape(50, 364, 50, 12, 214, 237, 245, 0); // Hospital sign
            draw_shape(50, 376, 50, 60, 0, 0, 0, 0);       // Door
            draw_shape(5, 20, 16, 22, 0, 206, 255, 0);     // Window
            draw_text("Hospital", 0, 52, 375, 255, 0, 0, 0, 12);
        }
        draw_shape(74, 16, 14, 14, 167, 167, 167, 3); // Smoke
        // draw_smoke_________________________
        pos_x_smoke = pos_x_smoke + 2;
        pos_y_smoke = pos_y_smoke - 1;
        int length_smoke = ((int) (Math.random() * 16) + 4);
        int height_smoke = ((int) (Math.random() * 16) + 4);
        int smoke_color = ((int) (Math.random() * 240) + 15);
        draw_shape(pos_x_smoke, pos_y_smoke, length_smoke, height_smoke, smoke_color, smoke_color, smoke_color, 3);
        if (pos_y_smoke <= 0) {
            pos_x_smoke = 52;
            pos_y_smoke = 22;
        }
        //___________________________________
        int xf = 5;
        int yf = 20;
        x_person = x_person + 1;
        if (x_person >= 80) {
            System.out.println("x_person>=80");
            y_person = y_person + 40;
            x_person = 0;
        }
        if (y_person >= 320) {
            System.out.println("y_person>=400");
            x_person = 5;
            y_person = 20;
        }
        g.dispose();
        for (int i = 7; i >= 0; i = i - 1) { // Draw windows
            draw_patient(xf, yf);
            for (int i2 = 1; i2 >= 0; i2 = i2 - 1) {
                xf = xf + 30;
                draw_patient(xf, yf);
            }
            yf = yf + 40;
            xf = 5;
        }
        ;
        draw_shape(pos_x_smoke, pos_y_smoke, length_smoke, height_smoke, smoke_color, smoke_color, smoke_color, 3);
    }

    public void draw_patient(int xf, int yf) {
        Graphics g2 = Sheet.getGraphics();
        draw_shape(xf, yf, 16, 22, 0, 206, 255, 0);
        g2.clipRect(xf, yf, 16, 22); // Area in which the shadow is painted is formed
        // g2.fillOval(x_person+2,y_person+9,16,22); // Shadow in the windows is painted
        g2.drawOval(x_person - 1, y_person + 4, 8, 8);                      // Head
        g2.drawLine(x_person + 3, y_person + 11, x_person + 3, y_person + 21);  // Neck
        g2.drawLine(x_person + 3, y_person + 21, x_person + 8, y_person + 29);  // Right leg
        g2.drawLine(x_person + 3, y_person + 21, x_person - 2, y_person + 29);  // Left leg
        g2.drawLine(x_person + 3, y_person + 17, x_person + 10, y_person + 14); // Right arm up
        g2.drawLine(x_person + 3, y_person + 19, x_person + 10, y_person + 16); // Right arm up
        g2.dispose(); // Area is deleted
    }

    public void draw_points() { //Score board
        draw_shape(540, 42, 60, 16, 174, 186, 204, 0);
        draw_text("", points, 545, 55, 255, 0, 0, 1, 14);
        draw_shape(500, 5, 112, 62, 250, 250, 250, 1);
        draw_shape(500, 5, 111, 61, 250, 250, 250, 1);
        draw_shape(500, 5, 110, 60, 255, 255, 255, 1);
        draw_person(525, 10);
        draw_person(555, 10);
        draw_person(585, 10);
        draw_text("L:", 0, 506, 28, 255, 255, 255, 1, 12);
        draw_text("P:", 0, 506, 53, 255, 255, 255, 1, 12);
        System.out.println("Points " + points);
    }

    static public void draw_help() { //Help menu
        int posx = 220;
        int posy = 120;
        draw_shape(posx -5, posy - 15, 158, 158, 0, 0, 0, 0); // paints over help field with black
        if (help == true) {
            draw_text("                  HELP", 0, posx, posy, 255, 255, 255, 1, 12);
            draw_text("    Action                     Key", 0, posx, posy + 24, 255, 255, 255, 1, 12);
            draw_text("Start Game                   S", 0, posx, posy + 40, 255, 255, 255, 1, 12);
            draw_text("Pause                            P", 0, posx, posy + 52, 255, 255, 255, 1, 12);
            draw_text("Restart Game               N", 0, posx, posy + 63, 255, 255, 255, 1, 12);
            draw_text("End Game                  ESC", 0, posx, posy + 76, 255, 255, 255, 1, 12);
            draw_text("Switch Difficultly        1-3", 0, posx, posy + 89, 255, 255, 255, 1, 12);
            draw_text("Show Help                    H", 0, posx, posy + 102, 255, 255, 255, 1, 12);
            draw_text("Goal: Save as many lives", 0, posx, posy + 125, 255, 255, 255, 1, 12);
            draw_text("          as possible.", 0, posx, posy + 138, 255, 255, 255, 1, 12);
            draw_shape(posx - 4, posy - 14, 155, 156, 255, 255, 255, 1);
        }
    }

    public void gameOver() { //Game Over Text
        draw_text("Game Over", 0, 175, 200, 255, 0, 0, 1, 50);
        System.out.println("Game Over" + gameover_blink);
        if (gameover_blink == 0) {
            System.out.println("0");
            draw_text("Restart Game          N", 0, 220, 350, 255, 0, 0, 1, 12);
            gameover_blink = gameover_blink + 1;
        } else {
            System.out.println("1");
            draw_text("Restart Game          N", 0, 220, 350, 255, 255, 255, 1, 12);
            gameover_blink = gameover_blink - 1;
        }
        draw_text("End Game               Esc", 0, 220, 360, 255, 255, 255, 1, 12);
    }

    public void keyPressed(KeyEvent v) {
        if (v.getKeyCode() == KeyEvent.VK_1) {
            paddle_length = 40;
        }
        if (v.getKeyCode() == KeyEvent.VK_2) {
            paddle_length = 50;
        }
        if (v.getKeyCode() == KeyEvent.VK_3) {
            paddle_length = 60;
        }
        if (v.getKeyCode() == KeyEvent.VK_S) {
            System.out.println("Button Start pressed");
            help = false;
            draw_help();
            pause = false;
            playing = true; // test
            System.out.println(pause);
        }
        if (v.getKeyCode() == KeyEvent.VK_P) {
            if (pause == true) {
                pause = false;
                draw_text("Pause", 0, 260, 200, 0, 0, 0, 1, 25);//clear pause
            } else {
                pause = true;
                //playing=false;
                draw_text("Pause", 0, 260, 200, 255, 255, 255, 1, 25);//draw pause
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_ESCAPE) { //QUIT
            pause = true;
            really_end = true;
            gameover = false; //so that GameOver text is not overwritten
            draw_shape(241, 162, 114, 50, 0, 0, 0, 0);
            draw_shape(241, 162, 114, 50, 255, 255, 255, 1);
            draw_text("Really Quit", 0, 265, 180, 255, 255, 255, 1, 12);
            draw_text("Yes", 0, 270, 198, 255, 255, 255, 1, 12);
            draw_text("No", 0, 310, 198, 255, 255, 255, 1, 12);
            draw_line(270, 200, 279, 200);
            draw_line(310, 200, 318, 200);
        }
        if (v.getKeyCode() == KeyEvent.VK_Y) {
            if (really_end == true) {
                System.exit(0);
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_N) { // RESTART
            if (really_end == true) {
                draw_shape(238, 158, 118, 56, 0, 0, 0, 0);
                pause = false;
                really_end = false;
                if (gameover == false && lives >= 550) { // so that GameOver text is not overwritten
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
        if (v.getKeyCode() == KeyEvent.VK_H) { // HELP
            if (help == true) {
                pause = false;
                help = false;
                draw_help();
            } else {
                pause = true;
                help = true;
                draw_help();
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_LEFT) { // LEFT
            if (position <= start - 100 || pause == true || gameover == true) { // if the trampoline goes too far to the right,
                //position=position; //or pause is pressed, it stays in place
            } else {
                position = position - paddle_length;
                clear_paddle();
                draw_paddle(position);
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_RIGHT) {  // RIGHT
            if (position >= stop - 50 || pause == true || gameover == true) {  // if the trampoline goes too far to the right,
                //position=position; //or pause is pressed, it stays in place
            } else {
                position = position + paddle_length;
                clear_paddle();
                draw_paddle(position);
            }
        }
    }

    public void keyReleased(KeyEvent arg0) {}

    public void keyTyped(KeyEvent arg0) {}
}