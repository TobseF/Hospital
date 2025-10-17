package hospital;

/**
 * Global immutable configuration values for the Hospital game.
 * Centralizing constants keeps the code DRY and maintainable.
 */
public final class Config {
    private Config() {}

    // Window / Canvas
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 625;
    public static final int WINDOW_HEIGHT = 446;

    // Game
    public static final String TITLE = "Hospital 2";
    public static final int INITIAL_PADDLE_LENGTH = 50;
    public static final int INITIAL_SPEED = 10; // sleep in ms per tick
    public static final int INITIAL_LIVES_X = 490; // x position used as lives bar

    // Paths (function domain)
    public static final int X_START = 100;
    public static final int X_STOP = 600;

    // Colors (defaults)
    public static final int DEFAULT_RED = 100;
    public static final int DEFAULT_GREEN = 25; // historically named yellow
    public static final int DEFAULT_BLUE = 0;
}
