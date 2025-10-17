package hospital;

/**
 * Verwaltet den aktuellen Spielzustand.
 * Enthält alle relevanten Spielvariablen wie Leben, Punkte, Geschwindigkeit etc.
 */
public class GameState {
    private static final int INITIAL_LIVES = 490;
    private static final int INITIAL_SPEED = 10;
    private static final int MAX_LIVES = 550;
    private static final int LIVES_INCREMENT = 30;
    private static final int POINTS_PER_LEVEL = 15;
    
    private int lives;
    private int points;
    private int speed;
    private boolean paused;
    private boolean playing;
    private boolean gameOver;
    private boolean helpVisible;
    private boolean reallyEnd;
    
    /**
     * Erstellt einen neuen Spielzustand mit Standardwerten.
     */
    public GameState() {
        reset();
    }
    
    /**
     * Setzt den Spielzustand auf die Anfangswerte zurück.
     */
    public void reset() {
        this.lives = INITIAL_LIVES;
        this.points = 0;
        this.speed = INITIAL_SPEED;
        this.paused = true;
        this.playing = false;
        this.gameOver = false;
        this.helpVisible = true;
        this.reallyEnd = false;
    }
    
    /**
     * Erhöht die Punktzahl um 1 und passt ggf. die Geschwindigkeit an.
     */
    public void incrementPoints() {
        points++;
        
        // Alle 15 Punkte wird das Spiel schneller und es gibt einen Bonuspunkt
        if (points > 0 && points % POINTS_PER_LEVEL == 0) {
            speed = Math.max(1, speed - 1); // Mindestgeschwindigkeit: 1
            points++; // Bonuspunkt
        }
    }
    
    /**
     * Verringert die Anzahl der Leben.
     * 
     * @return true, wenn das Spiel vorbei ist (keine Leben mehr)
     */
    public boolean decrementLives() {
        lives += LIVES_INCREMENT;
        return lives > MAX_LIVES;
    }
    
    // Getter und Setter
    
    public int getLives() { return lives; }
    public int getPoints() { return points; }
    public int getSpeed() { return speed; }
    public boolean isPaused() { return paused; }
    public boolean isPlaying() { return playing; }
    public boolean isGameOver() { return gameOver; }
    public boolean isHelpVisible() { return helpVisible; }
    public boolean isReallyEnd() { return reallyEnd; }
    
    public void setPaused(boolean paused) { this.paused = paused; }
    public void setPlaying(boolean playing) { this.playing = playing; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void setHelpVisible(boolean helpVisible) { this.helpVisible = helpVisible; }
    public void setReallyEnd(boolean reallyEnd) { this.reallyEnd = reallyEnd; }
    
    public int getMaxLives() { return MAX_LIVES; }
}