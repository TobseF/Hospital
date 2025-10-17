
package hospital;

import java.awt.*;

/**
 * Hauptspielengine die den Game Loop und die Spiellogik verwaltet.
 * Koordiniert alle Spielobjekte und deren Interaktionen.
 */
public class GameEngine implements Runnable {
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 400;
    
    private final Canvas canvas;
    private final GameState state;
    private final Renderer renderer;
    private final InputHandler inputHandler;
    
    private final FallingPerson fallingPerson;
    private final Paddle paddle;
    private final Hospital hospital;
    
    private Thread gameThread;
    private int gameOverBlinkCounter = 0;
    
    /**
     * Erstellt eine neue GameEngine mit allen notwendigen Komponenten.
     */
    public GameEngine() {
        this.canvas = new Canvas();
        this.state = new GameState();
        this.renderer = new Renderer(canvas);
        this.inputHandler = new InputHandler(this);
        
        this.fallingPerson = new FallingPerson();
        this.paddle = new Paddle();
        this.hospital = new Hospital();
        
        initializeCanvas();
    }
    
    /**
     * Initialisiert das Canvas mit Größe, Farbe und Event-Listenern.
     */
    private void initializeCanvas() {
        canvas.setBounds(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas.setBackground(Color.BLACK);
        canvas.addKeyListener(inputHandler);
        canvas.setFocusable(true);
        canvas.requestFocus();
    }
    
    /**
     * Startet den Game Loop in einem separaten Thread.
     */
    public void start() {
        canvas.requestFocus();
        renderer.drawHelp();
        
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    /**
     * Hauptspiel-Loop. Läuft kontinuierlich und aktualisiert das Spiel.
     */
    @Override
    public void run() {
        while (true) {
            if (state.isPlaying() && !state.isGameOver()) {
                runGameLoop();
            } else {
                handleNonGameplayState();
            }
            
            sleep(1000);
        }
    }
    
    /**
     * Führt einen Spielzyklus aus.
     */
    private void runGameLoop() {
        while (state.isPlaying() && !state.isGameOver()) {
            if (!state.isPaused()) {
                updateGame();
                renderGame();
                sleep(state.getSpeed());
            } else {
                sleep(500);
            }
        }
    }
    
    /**
     * Aktualisiert den Spielzustand.
     */
    private void updateGame() {
        // Person aktualisieren
        fallingPerson.clear(renderer);
        fallingPerson.update();
        
        // Kollisionserkennung
        if (fallingPerson.hasReachedGround()) {
            if (fallingPerson.hitsPaddle(paddle.getX(), paddle.getLength()) 
                || fallingPerson.isOutOfBounds()) {
                // Punkt erzielt
                state.incrementPoints();
                fallingPerson.reset();
            } else {
                // Leben verloren
                renderer.drawBloodStain(fallingPerson.getX(), 400);
                paddle.clear(renderer);
                
                if (state.decrementLives()) {
                    state.setGameOver(true);
                    state.setPlaying(false);
                } else {
                    fallingPerson.reset();
                }
            }
        }
        
        // Hospital aktualisieren
        hospital.update();
    }
    
    /**
     * Rendert alle Spielobjekte.
     */
    private void renderGame() {
        // Fehlerhafte Pixel löschen
        renderer.fillRect(102, 360, 575, 30, Color.BLACK);
        
        // Objekte zeichnen
        fallingPerson.render(renderer);
        paddle.render(renderer);
        hospital.render(renderer);
        renderer.drawScoreboard(state.getPoints(), state.getLives(), state.getMaxLives());
    }
    
    /**
     * Behandelt Zustände außerhalb des eigentlichen Spiels (Hilfe, Game Over).
     */
    private void handleNonGameplayState() {
        if (!state.isReallyEnd() && state.isHelpVisible()) {
            renderer.drawHelp();
        }
        
        if (state.isGameOver()) {
            renderer.drawGameOver(gameOverBlinkCounter == 0);
            gameOverBlinkCounter = (gameOverBlinkCounter + 1) % 2;
        }
    }
    
    /**
     * Lässt den Thread für die angegebene Zeit schlafen.
     * 
     * @param millis Zeit in Millisekunden
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Getter für andere Klassen
    
    public Canvas getCanvas() { return canvas; }
    public GameState getState() { return state; }
    public Renderer getRenderer() { return renderer; }
    public Paddle getPaddle() { return paddle; }
    public FallingPerson getFallingPerson() { return fallingPerson; }
}