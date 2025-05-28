package hospital.controller;

import hospital.model.GameState;
import hospital.model.Hospital;
import hospital.model.Paddle;
import hospital.model.Person;
import hospital.util.Constants;
import hospital.view.GameView;

/**
 * Controls the game logic and flow.
 */
public class GameController implements Runnable {
    private Thread gameThread;
    private GameState gameState;
    private Hospital hospital;
    private Paddle paddle;
    private GameView gameView;
    private Person fallingPatient;
    private int patientX;
    private int patientY;
    private int patientCanvasX;
    private int patientCanvasY;
    
    /**
     * Creates a new game controller with the specified components.
     *
     * @param gameState The game state
     * @param hospital The hospital
     * @param paddle The paddle
     * @param gameView The game view
     */
    public GameController(GameState gameState, Hospital hospital, Paddle paddle, GameView gameView) {
        this.gameState = gameState;
        this.hospital = hospital;
        this.paddle = paddle;
        this.gameView = gameView;
        this.gameThread = new Thread(this);
        this.fallingPatient = new Person(Constants.X_START, Constants.INITIAL_Y, true);
        this.patientX = Constants.X_START;
        this.patientY = Constants.INITIAL_Y;
        this.patientCanvasX = patientX;
        this.patientCanvasY = Constants.CANVAS_Y_OFFSET - patientY;
    }
    
    /**
     * Starts the game thread.
     */
    public void startGame() {
        gameThread.start();
    }
    
    /**
     * Resets the patient position.
     */
    public void resetPatient() {
        patientX = Constants.X_START;
        patientY = 300;
        fallingPatient.setX(patientX);
        fallingPatient.setY(patientY);
        patientCanvasX = patientX;
        patientCanvasY = Constants.CANVAS_Y_OFFSET - patientY;
    }
    
    @Override
    public void run() {
        System.out.println("Game controller started");
        
        // Initial setup
        gameView.drawHelp(gameState.isHelpShown());
        
        while (true) {
            while (gameState.isPlaying()) {
                while (patientY >= 5) {
                    if (!gameState.isPaused()) {
                        // Clear previous patient position
                        gameView.drawShape(patientX - 5, patientCanvasY - 1, 14, 28, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
                        // Clear any pixel errors above the people
                        gameView.drawShape(102, 360, 575, 30, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
                        
                        // Calculate new patient position using polynomial function
                        double ydouble = (gameState.getA0() + (gameState.getA1() * patientX) + 
                                         (gameState.getA2() * Math.pow(patientX, 2)) + 
                                         (gameState.getA3() * Math.pow(patientX, 3)) + 
                                         (gameState.getA4() * Math.pow(patientX, 4)));
                        
                        patientY = (int) ydouble;
                        patientCanvasX = patientX;
                        patientCanvasY = Constants.CANVAS_Y_OFFSET - patientY;
                        
                        // Update falling patient position
                        fallingPatient.setX(patientX);
                        fallingPatient.setY(patientCanvasY);
                        
                        // Draw updated game elements
                        gameView.drawPerson(fallingPatient, gameState.getAnimationCounter());
                        gameView.drawPaddle(paddle);
                        
                        // Move patient horizontally
                        patientX++;
                        
                        // Pause for animation
                        try {
                            Thread.sleep(gameState.getSpeed());
                        } catch (InterruptedException e) {
                            System.out.println("Game thread interrupted");
                            Thread.currentThread().interrupt();
                            return;
                        }
                        
                        // Check if patient is caught or hits boundary
                        if ((patientY <= 20 && patientY <= 18 && 
                             (patientX >= paddle.getPosition() && patientX <= (paddle.getPosition() + paddle.getLength()))) || 
                            (patientX >= Constants.PATH_STOP) || (patientX <= Constants.X_START)) {
                            
                            // Patient caught, add point
                            gameState.addPoints(1);
                            
                            // Select random path for next patient
                            gameState.selectRandomPath();
                            
                            // Set up path coefficients based on selection
                            switch (gameState.getSelection()) {
                                case 1:
                                    gameState.setupPath1();
                                    break;
                                case 2:
                                    gameState.setupPath2();
                                    break;
                                case 3:
                                    gameState.setupPath3();
                                    break;
                            }
                            
                            // Reset patient position
                            resetPatient();
                        }
                        
                        // Draw hospital and points
                        gameView.drawHospital(hospital, patientX);
                        gameView.drawPoints(gameState);
                        
                        // Check if player should level up
                        if (gameState.shouldLevelUp()) {
                            gameState.increaseSpeed();
                            gameState.addPoints(1);
                        }
                        
                        // Update animation counter
                        gameState.incrementAnimationCounter();
                    }
                }
                
                // Patient hit the ground
                if (gameState.getLives() <= Constants.MAX_LIVES) {
                    // Draw blood stain
                    gameView.drawBloodStain(patientX, 400);
                    gameView.clearPaddle();
                    
                    // Add lives (which is bad in this game)
                    gameState.addLives(Constants.LIVES_INCREASE);
                    
                    // Draw lives indicator
                    gameView.drawLives(gameState.getLives());
                    
                    // Set up path for next patient
                    gameState.setupPath2();
                    
                    // Reset patient position
                    resetPatient();
                } else {
                    // Game over
                    gameState.setGameOver(true);
                    gameState.setPlaying(false);
                }
            }
            
            // Draw help if needed
            if (!gameState.isReallyEnd()) {
                gameView.drawHelp(gameState.isHelpShown());
            }
            
            // Draw game over screen if needed
            if (gameState.isGameOver()) {
                gameView.drawGameOver(gameState);
                gameState.toggleGameOverBlinkState();
            }
            
            // Pause between updates
            try {
                Thread.sleep(Constants.SLEEP_TIME_LONG);
            } catch (InterruptedException e) {
                System.out.println("Game thread interrupted");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}