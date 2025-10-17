package hospital;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Verarbeitet alle Benutzereingaben (Tastatur).
 * Implementiert das KeyListener-Interface.
 */
public class InputHandler implements KeyListener {
    private final GameEngine gameEngine;
    
    /**
     * Erstellt einen neuen InputHandler für die GameEngine.
     * 
     * @param gameEngine Die GameEngine, die gesteuert werden soll
     */
    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        GameState state = gameEngine.getState();
        Paddle paddle = gameEngine.getPaddle();
        Renderer renderer = gameEngine.getRenderer();
        
        switch (e.getKeyCode()) {
            // Schwierigkeitsgrade
            case KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3 -> 
                paddle.setDifficulty(e.getKeyCode() - KeyEvent.VK_0);
            
            // Spiel starten
            case KeyEvent.VK_S -> {
                state.setHelpVisible(false);
                state.setPaused(false);
                state.setPlaying(true);
            }
            
            // Pause
            case KeyEvent.VK_P -> {
                if (state.isPaused()) {
                    state.setPaused(false);
                    renderer.clearPause();
                } else {
                    state.setPaused(true);
                    renderer.drawPause();
                }
            }
            
            // Beenden
            case KeyEvent.VK_ESCAPE -> {
                state.setPaused(true);
                state.setReallyEnd(true);
                state.setGameOver(false);
                renderer.drawQuitDialog();
            }
            
            // Beenden bestätigen
            case KeyEvent.VK_Y -> {
                if (state.isReallyEnd()) {
                    System.exit(0);
                }
            }
            
            // Neustart / Beenden abbrechen
            case KeyEvent.VK_N -> handleRestart(state, renderer);
            
            // Hilfe ein/aus
            case KeyEvent.VK_H -> {
                if (state.isHelpVisible()) {
                    state.setPaused(false);
                    state.setHelpVisible(false);
                } else {
                    state.setPaused(true);
                    state.setHelpVisible(true);
                }
            }
            
            // Paddle bewegen
            case KeyEvent.VK_LEFT -> {
                if (!state.isPaused() && !state.isGameOver()) {
                    paddle.clear(renderer);
                    paddle.moveLeft();
                    paddle.render(renderer);
                }
            }
            
            case KeyEvent.VK_RIGHT -> {
                if (!state.isPaused() && !state.isGameOver()) {
                    paddle.clear(renderer);
                    paddle.moveRight();
                    paddle.render(renderer);
                }
            }
        }
    }
    
    /**
     * Behandelt den Neustart des Spiels oder das Abbrechen des Beenden-Dialogs.
     */
    private void handleRestart(GameState state, Renderer renderer) {
        if (state.isReallyEnd()) {
            // Dialog schließen
            renderer.fillRect(238, 158, 118, 56, java.awt.Color.BLACK);
            state.setPaused(false);
            state.setReallyEnd(false);
            
            if (!state.isGameOver() && state.getLives() >= state.getMaxLives()) {
                state.setGameOver(true);
            }
        } else {
            // Kompletter Neustart
            renderer.clearScreen();
            state.reset();
            state.setPaused(false);
            state.setPlaying(true);
            gameEngine.getFallingPerson().reset();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Nicht verwendet
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Nicht verwendet
    }
}