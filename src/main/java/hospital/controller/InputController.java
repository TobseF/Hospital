package hospital.controller;

import hospital.model.GameState;
import hospital.model.Paddle;
import hospital.util.Constants;
import hospital.view.GameView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input for the game.
 */
public class InputController implements KeyListener {
    private GameState gameState;
    private Paddle paddle;
    private GameView gameView;
    private GameController gameController;
    
    /**
     * Creates a new input controller with the specified game state, paddle, and game view.
     *
     * @param gameState The game state
     * @param paddle The paddle
     * @param gameView The game view
     */
    public InputController(GameState gameState, Paddle paddle, GameView gameView) {
        this.gameState = gameState;
        this.paddle = paddle;
        this.gameView = gameView;
    }
    
    /**
     * Sets the game controller.
     *
     * @param gameController The game controller
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_1:
                paddle.setLength(Constants.PADDLE_LENGTH_SMALL);
                break;
                
            case KeyEvent.VK_2:
                paddle.setLength(Constants.PADDLE_LENGTH_MEDIUM);
                break;
                
            case KeyEvent.VK_3:
                paddle.setLength(Constants.PADDLE_LENGTH_LARGE);
                break;
                
            case KeyEvent.VK_S:
                // Start game
                gameState.setHelpShown(false);
                gameView.drawHelp(false);
                gameState.setPaused(false);
                gameState.setPlaying(true);
                break;
                
            case KeyEvent.VK_P:
                // Toggle pause
                if (gameState.isPaused()) {
                    gameState.setPaused(false);
                    gameView.clearPauseText();
                } else {
                    gameState.setPaused(true);
                    gameView.drawPauseText();
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                // Quit game
                gameState.setPaused(true);
                gameState.setReallyEnd(true);
                // Don't overwrite game over text
                if (!gameState.isGameOver()) {
                    gameView.drawReallyEndDialog();
                }
                break;
                
            case KeyEvent.VK_Y:
                // Confirm quit
                if (gameState.isReallyEnd()) {
                    System.exit(0);
                }
                break;
                
            case KeyEvent.VK_N:
                if (gameState.isReallyEnd()) {
                    // Cancel quit
                    gameView.drawShape(238, 158, 118, 56, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
                    gameState.setPaused(false);
                    gameState.setReallyEnd(false);
                    
                    // Show game over if needed
                    if (!gameState.isGameOver() && gameState.hasLostAllLives()) {
                        gameState.setGameOver(true);
                    }
                } else {
                    // Restart game
                    gameState.setPaused(false);
                    gameState.setPlaying(true);
                    gameView.clearCanvas();
                    gameState.reset();
                    gameState.setGameOver(false);
                    
                    // Reset patient position
                    if (gameController != null) {
                        gameController.resetPatient();
                    }
                }
                break;
                
            case KeyEvent.VK_H:
                // Toggle help
                if (gameState.isHelpShown()) {
                    gameState.setPaused(false);
                    gameState.setHelpShown(false);
                } else {
                    gameState.setPaused(true);
                    gameState.setHelpShown(true);
                }
                gameView.drawHelp(gameState.isHelpShown());
                break;
                
            case KeyEvent.VK_LEFT:
                // Move paddle left
                if (!gameState.isPaused() && !gameState.isGameOver()) {
                    if (paddle.moveLeft()) {
                        gameView.clearPaddle();
                        gameView.drawPaddle(paddle);
                    }
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                // Move paddle right
                if (!gameState.isPaused() && !gameState.isGameOver()) {
                    if (paddle.moveRight()) {
                        gameView.clearPaddle();
                        gameView.drawPaddle(paddle);
                    }
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent event) {
        // Not used
    }
    
    @Override
    public void keyTyped(KeyEvent event) {
        // Not used
    }
}