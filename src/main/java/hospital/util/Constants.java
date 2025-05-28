package hospital.util;

/**
 * Constants used throughout the game.
 */
public class Constants {
    // Game dimensions
    public static final int GAME_WIDTH = 630;
    public static final int GAME_HEIGHT = 454;
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 400;
    
    // Game positions
    public static final int INITIAL_PADDLE_POSITION = 300;
    public static final int INITIAL_Y = 250;
    public static final int X_START = 100;
    public static final int INITIAL_X = X_START;
    public static final int PADDLE_LENGTH_SMALL = 40;
    public static final int PADDLE_LENGTH_MEDIUM = 50;
    public static final int PADDLE_LENGTH_LARGE = 60;
    
    // Game flow
    public static final int INITIAL_SPEED = 10;
    public static final int INITIAL_LIVES = 490;
    public static final int LIVES_INCREASE = 30;
    public static final int MAX_LIVES = 550;
    public static final int POINTS_PER_LEVEL = 15;
    
    // Path constants
    public static final int PATH_START = 200;
    public static final int PATH_STOP = 600;
    
    // Drawing constants
    public static final int PERSON_HEAD_SIZE = 7;
    public static final int PERSON_HEAD_OFFSET = 1;
    public static final int HOSPITAL_WIDTH = 100;
    public static final int HOSPITAL_HEIGHT = 405;
    public static final int HOSPITAL_SIGN_WIDTH = 50;
    public static final int HOSPITAL_SIGN_HEIGHT = 12;
    
    // Colors
    public static final int COLOR_WHITE_R = 255;
    public static final int COLOR_WHITE_G = 255;
    public static final int COLOR_WHITE_B = 255;
    public static final int COLOR_RED_R = 255;
    public static final int COLOR_RED_G = 0;
    public static final int COLOR_RED_B = 0;
    public static final int COLOR_BLUE_R = 0;
    public static final int COLOR_BLUE_G = 0;
    public static final int COLOR_BLUE_B = 255;
    public static final int COLOR_BLACK_R = 0;
    public static final int COLOR_BLACK_G = 0;
    public static final int COLOR_BLACK_B = 0;
    
    // Form types
    public static final int FORM_FILLED_RECTANGLE = 0;
    public static final int FORM_RECTANGLE_FRAME = 1;
    public static final int FORM_FILLED_OVAL = 3;
    public static final int FORM_OVAL_FRAME = 4;
    
    // Font types
    public static final int FONT_ARIAL_PLAIN = 0;
    public static final int FONT_ARIAL_BOLD = 1;
    public static final int FONT_COURIER_PLAIN = 2;
    public static final int FONT_WINGDINGS_PLAIN = 3;
    public static final int FONT_TIMES_PLAIN = 4;
    
    // Canvas Y conversion
    public static final int CANVAS_Y_OFFSET = 380;
    
    // Animation
    public static final int ANIMATION_COUNTER_RESET = 40;
    public static final int ANIMATION_COUNTER_THRESHOLD = 20;
    
    // Window position
    public static final int WINDOW_X = 150;
    public static final int WINDOW_Y = 150;
    
    // Sleep times
    public static final int SLEEP_TIME_LONG = 1000;
}