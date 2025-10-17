package hospital;

import java.awt.*;

/**
 * Repräsentiert eine fallende Person.
 * Die Person folgt einer durch den TrajectoryCalculator bestimmten Flugbahn.
 */
public class FallingPerson extends GameObject {
    private static final int START_X = 100;
    private static final int START_Y = 250;
    private static final int STOP_X = 600;
    private static final int PERSON_WIDTH = 16;
    private static final int PERSON_HEIGHT = 31;
    
    private final TrajectoryCalculator trajectory;
    private int animationCounter;
    
    /**
     * Erstellt eine neue fallende Person mit zufälliger Flugbahn.
     */
    public FallingPerson() {
        super(START_X, START_Y);
        this.trajectory = new TrajectoryCalculator();
        this.animationCounter = 0;
        selectRandomTrajectory();
    }
    
    /**
     * Wählt eine zufällige Flugbahn (1-3) aus.
     */
    public void selectRandomTrajectory() {
        int type = (int) (Math.random() * 3) + 1;
        trajectory.selectRandomTrajectory(type);
    }
    
    /**
     * Aktualisiert die Position der Person basierend auf der Flugbahn.
     */
    @Override
    public void update() {
        x++;
        y = (int) trajectory.calculateY(x);
        animationCounter++;
    }
    
    /**
     * Setzt die Person auf die Startposition zurück und wählt eine neue Flugbahn.
     */
    public void reset() {
        x = START_X;
        y = START_Y;
        selectRandomTrajectory();
    }
    
    /**
     * Rendert die Person mit Animation (winkende Arme).
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    @Override
    public void render(Renderer renderer) {
        int canvasY = 380 - y;
        renderer.drawPerson(x, canvasY, animationCounter);
    }
    
    /**
     * Prüft, ob die Person den Boden erreicht hat.
     * 
     * @return true, wenn y <= 5
     */
    public boolean hasReachedGround() {
        return y <= 5;
    }
    
    /**
     * Prüft, ob die Person die Paddle getroffen hat.
     * 
     * @param paddleX X-Position der Paddle
     * @param paddleLength Länge der Paddle
     * @return true, wenn die Person die Paddle getroffen hat
     */
    public boolean hitsPaddle(int paddleX, int paddleLength) {
        return y <= 20 && y >= 18 && x >= paddleX && x <= (paddleX + paddleLength);
    }
    
    /**
     * Prüft, ob die Person die Grenzen erreicht hat.
     * 
     * @return true, wenn die Person außerhalb der Grenzen ist
     */
    public boolean isOutOfBounds() {
        return x >= STOP_X || x <= START_X;
    }
    
    /**
     * Löscht die Person vom Canvas.
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    public void clear(Renderer renderer) {
        int canvasY = 380 - y;
        renderer.fillOval(x - 7, canvasY - 3, PERSON_WIDTH, PERSON_HEIGHT, Color.BLACK);
    }
}