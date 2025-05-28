package hospital;

import hospital.controller.GameController;
import hospital.controller.InputController;
import hospital.model.GameState;
import hospital.model.Hospital;
import hospital.model.Paddle;
import hospital.util.Constants;
import hospital.view.GameView;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main class for the Hospital game.
 * This class coordinates all the components of the game.
 */
public class Game extends Frame {
    private Canvas canvas;
    private GameState gameState;
    private Hospital hospital;
    private Paddle paddle;
    private GameView gameView;
    private InputController inputController;
    private GameController gameController;

    /**
     * Creates a new game instance.
     */
    public Game() {
        // Initialize UI components
        initializeUI();

        // Initialize game components
        initializeGameComponents();

        // Set up input controller
        setupInputController();

        // Start the game
        gameController.startGame();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        // Set up window close handler
        addWindowListener(new WindowAdapter() {
            private void exitForm(WindowEvent ev) {
                System.exit(0);
            }

            @Override
            public void windowClosing(WindowEvent ev) {
                exitForm(ev);
            }
        });

        // Set up canvas
        canvas = new Canvas();
        canvas.setBounds(0, 0, Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
        canvas.setBackground(Color.black);
        add(canvas);

        // Set up frame
        setLocation(Constants.WINDOW_X, Constants.WINDOW_Y);
        setResizable(false);
        setTitle("Hospital 2");
        pack();
        setSize(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
    }

    /**
     * Initializes the game components.
     */
    private void initializeGameComponents() {
        // Create model objects
        gameState = new GameState();
        hospital = new Hospital();
        paddle = new Paddle(Constants.INITIAL_PADDLE_POSITION, Constants.PADDLE_LENGTH_MEDIUM);

        // Create view objects
        gameView = new GameView(canvas);

        // Create controller objects
        gameController = new GameController(gameState, hospital, paddle, gameView);
    }

    /**
     * Sets up the input controller.
     */
    private void setupInputController() {
        inputController = new InputController(gameState, paddle, gameView);
        inputController.setGameController(gameController);
        canvas.addKeyListener(inputController);
        canvas.requestFocus();
    }

    /**
     * Main method to start the game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Game gameInstance = new Game();
        gameInstance.setVisible(true);
    }
}
