package hospital;

import java.awt.Canvas;

/**
 * High-level rendering facade for the Hospital game.
 * Delegates to the legacy drawing routines in Game to keep behavior identical
 * while enabling modern separation of concerns.
 */
public final class Renderer {
    private final Game game; // used to access instance drawing methods

    public Renderer(Game game) {
        this.game = game;
    }

    public Canvas canvas() {
        return Game.Sheet;
    }

    // Low-level primitives
    public void drawShape(int x, int y, int w, int h, int r, int g, int b, int form) {
        Game.draw_shape(x, y, w, h, r, g, b, form);
    }

    public void drawText(String text, int number, int x, int y, int r, int g, int b, int fontType, int fontSize) {
        Game.draw_text(text, number, x, y, r, g, b, fontType, fontSize);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        Game.draw_line(x1, y1, x2, y2);
    }

    public void drawPerson(int x, int y) {
        Game.draw_person(x, y);
    }

    // Higher-level elements
    public void drawPaddle(int position) { game.draw_paddle(position); }
    public void clearPaddle() { game.clear_paddle(); }
    public void drawHospital() { game.draw_hospital(); }
    public void drawPoints() { game.draw_points(); }
    public void drawHelp() { game.draw_help(); }
    public void drawGameOver() { game.gameOver(); }
}
