package hospital.model;

import hospital.util.Constants;

import java.util.Random;

/**
 * Represents the current state of the game.
 */
public class GameState {
    private int points;
    private int lives;
    private int speed;
    private boolean isPaused;
    private boolean isPlaying;
    private boolean isGameOver;
    private boolean isHelpShown;
    private boolean isReallyEnd;
    private int gameOverBlinkState;
    private int animationCounter;
    private int selection;
    private double a0;
    private double a1;
    private double a2;
    private double a3;
    private double a4;
    private Random random;
    
    /**
     * Creates a new game state with default values.
     */
    public GameState() {
        this.points = 0;
        this.lives = Constants.INITIAL_LIVES;
        this.speed = Constants.INITIAL_SPEED;
        this.isPaused = true;
        this.isPlaying = false;
        this.isGameOver = false;
        this.isHelpShown = true;
        this.isReallyEnd = false;
        this.gameOverBlinkState = 0;
        this.animationCounter = 0;
        this.selection = 1;
        this.random = new Random();
        initializePathCoefficients();
    }
    
    /**
     * Initializes the path coefficients for the first path.
     */
    private void initializePathCoefficients() {
        this.a0 = 250;
        this.a1 = -0.8;
        this.a2 = 0;
        this.a3 = 0;
        this.a4 = 0;
    }
    
    /**
     * Gets the current points.
     *
     * @return The current points
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * Adds points to the current score.
     *
     * @param points The points to add
     */
    public void addPoints(int points) {
        this.points += points;
    }
    
    /**
     * Gets the current lives.
     *
     * @return The current lives
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * Adds lives to the current lives.
     *
     * @param lives The lives to add
     */
    public void addLives(int lives) {
        this.lives += lives;
    }
    
    /**
     * Checks if the player has lost all lives.
     *
     * @return true if the player has lost all lives, false otherwise
     */
    public boolean hasLostAllLives() {
        return lives >= Constants.MAX_LIVES;
    }
    
    /**
     * Gets the current game speed.
     *
     * @return The current game speed
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * Sets the game speed.
     *
     * @param speed The new game speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    /**
     * Increases the game speed.
     */
    public void increaseSpeed() {
        this.speed--;
    }
    
    /**
     * Checks if the game is paused.
     *
     * @return true if the game is paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Sets whether the game is paused.
     *
     * @param paused true to pause the game, false to unpause
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }
    
    /**
     * Toggles the pause state of the game.
     */
    public void togglePause() {
        this.isPaused = !this.isPaused;
    }
    
    /**
     * Checks if the game is playing.
     *
     * @return true if the game is playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Sets whether the game is playing.
     *
     * @param playing true if the game is playing, false otherwise
     */
    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }
    
    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver;
    }
    
    /**
     * Sets whether the game is over.
     *
     * @param gameOver true if the game is over, false otherwise
     */
    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }
    
    /**
     * Checks if the help screen is shown.
     *
     * @return true if the help screen is shown, false otherwise
     */
    public boolean isHelpShown() {
        return isHelpShown;
    }
    
    /**
     * Sets whether the help screen is shown.
     *
     * @param helpShown true if the help screen is shown, false otherwise
     */
    public void setHelpShown(boolean helpShown) {
        this.isHelpShown = helpShown;
    }
    
    /**
     * Toggles the help screen.
     */
    public void toggleHelp() {
        this.isHelpShown = !this.isHelpShown;
    }
    
    /**
     * Checks if the "really end" dialog is shown.
     *
     * @return true if the "really end" dialog is shown, false otherwise
     */
    public boolean isReallyEnd() {
        return isReallyEnd;
    }
    
    /**
     * Sets whether the "really end" dialog is shown.
     *
     * @param reallyEnd true if the "really end" dialog is shown, false otherwise
     */
    public void setReallyEnd(boolean reallyEnd) {
        this.isReallyEnd = reallyEnd;
    }
    
    /**
     * Gets the game over blink state.
     *
     * @return The game over blink state
     */
    public int getGameOverBlinkState() {
        return gameOverBlinkState;
    }
    
    /**
     * Toggles the game over blink state.
     */
    public void toggleGameOverBlinkState() {
        this.gameOverBlinkState = 1 - this.gameOverBlinkState;
    }
    
    /**
     * Gets the animation counter.
     *
     * @return The animation counter
     */
    public int getAnimationCounter() {
        return animationCounter;
    }
    
    /**
     * Increments the animation counter and resets it if it reaches the threshold.
     */
    public void incrementAnimationCounter() {
        this.animationCounter++;
        if (this.animationCounter >= Constants.ANIMATION_COUNTER_RESET) {
            this.animationCounter = 0;
        }
    }
    
    /**
     * Gets the current path selection.
     *
     * @return The current path selection
     */
    public int getSelection() {
        return selection;
    }
    
    /**
     * Sets the path selection.
     *
     * @param selection The new path selection
     */
    public void setSelection(int selection) {
        this.selection = selection;
    }
    
    /**
     * Selects a random path.
     */
    public void selectRandomPath() {
        this.selection = random.nextInt(3) + 1;
    }
    
    /**
     * Gets the a0 coefficient.
     *
     * @return The a0 coefficient
     */
    public double getA0() {
        return a0;
    }
    
    /**
     * Sets the a0 coefficient.
     *
     * @param a0 The new a0 coefficient
     */
    public void setA0(double a0) {
        this.a0 = a0;
    }
    
    /**
     * Gets the a1 coefficient.
     *
     * @return The a1 coefficient
     */
    public double getA1() {
        return a1;
    }
    
    /**
     * Sets the a1 coefficient.
     *
     * @param a1 The new a1 coefficient
     */
    public void setA1(double a1) {
        this.a1 = a1;
    }
    
    /**
     * Gets the a2 coefficient.
     *
     * @return The a2 coefficient
     */
    public double getA2() {
        return a2;
    }
    
    /**
     * Sets the a2 coefficient.
     *
     * @param a2 The new a2 coefficient
     */
    public void setA2(double a2) {
        this.a2 = a2;
    }
    
    /**
     * Gets the a3 coefficient.
     *
     * @return The a3 coefficient
     */
    public double getA3() {
        return a3;
    }
    
    /**
     * Sets the a3 coefficient.
     *
     * @param a3 The new a3 coefficient
     */
    public void setA3(double a3) {
        this.a3 = a3;
    }
    
    /**
     * Gets the a4 coefficient.
     *
     * @return The a4 coefficient
     */
    public double getA4() {
        return a4;
    }
    
    /**
     * Sets the a4 coefficient.
     *
     * @param a4 The new a4 coefficient
     */
    public void setA4(double a4) {
        this.a4 = a4;
    }
    
    /**
     * Sets up the path coefficients for path 1 (linear flight).
     */
    public void setupPath1() {
        a0 = 380 - (random.nextInt(220)); // Random value between 160 and 380
        a1 = -(1.1 - (random.nextInt(6) / 10.0)); // Random value between -0.5 and -1.1
        a2 = 0;
        a3 = 0;
        a4 = 0;
    }
    
    /**
     * Sets up the path coefficients for path 2 (arc flight).
     */
    public void setupPath2() {
        a0 = 300 - (random.nextInt(275)); // Random value between 25 and 300
        a1 = 1;
        a2 = -0.003;
        a3 = 0;
        a4 = -0;
    }
    
    /**
     * Sets up the path coefficients for path 3 (arc flight).
     */
    public void setupPath3() {
        a0 = 380 - (random.nextInt(240)); // Random value between 140 and 380
        a2 = -0.005 + ((random.nextInt(3) / 1000.0)); // Random value between -0.005 and -0.002
        a1 = 0;
        a3 = 0;
        a4 = 0;
    }
    
    /**
     * Resets the game state to default values.
     */
    public void reset() {
        this.points = 0;
        this.lives = Constants.INITIAL_LIVES;
        this.speed = Constants.INITIAL_SPEED;
        this.isPaused = false;
        this.isPlaying = true;
        this.isGameOver = false;
        this.selection = 1;
        initializePathCoefficients();
    }
    
    /**
     * Checks if the player should level up based on the current points.
     *
     * @return true if the player should level up, false otherwise
     */
    public boolean shouldLevelUp() {
        return (points > 0) && (points % Constants.POINTS_PER_LEVEL == 0);
    }
}