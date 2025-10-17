
package hospital;

import java.awt.*;

/**
 * Repräsentiert das Trampolin (Paddle) mit zwei Personen, das die fallenden Personen auffängt.
 */
public class Paddle extends GameObject {
    private static final int DEFAULT_LENGTH = 50;
    private static final int MIN_LENGTH = 40;
    private static final int MAX_LENGTH = 60;
    private static final int Y_POSITION = 388;
    private static final int START_X = 200;
    private static final int STOP_X = 600;
    
    private int length;
    
    /**
     * Erstellt eine neue Paddle mit Standardlänge.
     */
    public Paddle() {
        super(300, Y_POSITION);
        this.length = DEFAULT_LENGTH;
    }
    
    /**
     * Bewegt die Paddle nach links.
     */
    public void moveLeft() {
        if (x > START_X - 100) {
            x -= length;
        }
    }
    
    /**
     * Bewegt die Paddle nach rechts.
     */
    public void moveRight() {
        if (x < STOP_X - 50) {
            x += length;
        }
    }
    
    /**
     * Setzt die Schwierigkeitsstufe (ändert die Paddle-Länge).
     * 
     * @param difficulty 1 (schwer), 2 (mittel), 3 (leicht)
     */
    public void setDifficulty(int difficulty) {
        length = switch (difficulty) {
            case 1 -> MIN_LENGTH;  // Schwer
            case 2 -> DEFAULT_LENGTH;  // Mittel
            case 3 -> MAX_LENGTH;  // Leicht
            default -> DEFAULT_LENGTH;
        };
    }
    
    @Override
    public void update() {
        // Paddle bewegt sich nur durch Benutzereingaben
    }
    
    /**
     * Rendert die Paddle mit zwei Personen und dem Trampolin.
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    @Override
    public void render(Renderer renderer) {
        // Trampolin zeichnen
        renderer.fillRect(x, y, length, 5, new Color(0, 0, 109));
        
        // Linke Person
        drawPaddlePerson(renderer, x - 10, true);
        
        // Rechte Person
        drawPaddlePerson(renderer, x + length + 4, false);
        
        // Trampolin-Effekt (blauer Bogen)
        renderer.drawPaddleEffect(x, y, length);
    }
    
    /**
     * Zeichnet eine Person an der Paddle.
     * 
     * @param renderer Der Renderer
     * @param posX X-Position der Person
     * @param isLeft true für linke Person, false für rechte
     */
    private void drawPaddlePerson(Renderer renderer, int posX, boolean isLeft) {
        int posY = 380;
        renderer.drawPaddlePerson(posX, posY, isLeft);
    }
    
    /**
     * Löscht die Paddle vom Canvas.
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    public void clear(Renderer renderer) {
        renderer.fillRect(55, 376, 575, 30, Color.BLACK);
    }
    
    public int getLength() { return length; }
}