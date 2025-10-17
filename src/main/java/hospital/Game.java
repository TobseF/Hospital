package hospital;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Hauptklasse für das Hospital 2 Spiel.
 * Ein Geschicklichkeitsspiel, bei dem fallende Personen mit einem Trampolin aufgefangen werden müssen.
 * 
 * @author Refactored version - 2025
 * @version 2.0
 */
public class Game extends Frame {
    private static final int WINDOW_WIDTH = 625;
    private static final int WINDOW_HEIGHT = 446;
    private static final int WINDOW_X = 150;
    private static final int WINDOW_Y = 150;
    
    private final GameEngine gameEngine;
    
    /**
     * Konstruktor für das Hospital-Spiel.
     * Initialisiert das Fenster und die Spielengine.
     */
    public Game() {
        this.gameEngine = new GameEngine();
        initializeWindow();
    }
    
    /**
     * Initialisiert das Spielfenster mit allen notwendigen Einstellungen.
     */
    private void initializeWindow() {
        setTitle("Hospital 2");
        setLocation(WINDOW_X, WINDOW_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        
        add(gameEngine.getCanvas());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
        
        pack();
    }
    
    /**
     * Startet das Spiel.
     */
    public void start() {
        setVisible(true);
        gameEngine.start();
    }
    
    /**
     * Main-Methode - Entry Point der Anwendung.
     * 
     * @param args Kommandozeilenargumente (nicht verwendet)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game game = new Game();
            game.start();
        });
    }
}