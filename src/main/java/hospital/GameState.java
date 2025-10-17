package hospital;

/**
 * Mutable game state container.
 */
public final class GameState {
    // Position and motion
    int x = Config.X_START;
    int y = 250;
    int xCanvas = 111;
    int yCanvas = 111;

    // Paddle
    int paddleX = 300;
    int paddleLength = Config.INITIAL_PADDLE_LENGTH;

    // Progress
    int speedMs = Config.INITIAL_SPEED;
    int livesX = Config.INITIAL_LIVES_X; // this game uses a moving X as visual lives bar
    int points = 0;

    // Flow flags
    boolean help = true;
    boolean pause = true;
    boolean playing = false;
    boolean reallyEnd = false;
    boolean game = true;
    boolean gameOver = false;

    // Animation
    int gameOverBlink = 0;
    int waveCounter = 0; // people wave their arms

    // Path
    int selection = 1;
    Trajectory trajectory = new Trajectory(250, -0.8, 0, 0, 0);
}
