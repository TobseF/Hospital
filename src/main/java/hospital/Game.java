/*
 * ©2005 by Tobias Fritz
 * Modernized version with Java 24 features
 */
package hospital;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * Main game class that manages the game loop and UI components.
 * Implements modern Java practices with clean object-oriented design.
 */
public class Game extends JFrame implements Runnable, KeyListener {
    private static final long serialVersionUID = 1L;

    // Game state management
    private volatile boolean gameRunning = true;
    private volatile boolean playing = false;
    private volatile boolean pause = false;
    private volatile boolean gameover = false;
    private volatile boolean help = false;

    // Game objects and state
    private int points = 0;
    private int lives = 490;
    private int speed = 10;
    private int selection = 1;
    private int x = 100;
    private int y = 300;
    private int position = 250;
    private int start = 0;
    private int stop = 600;
    private int paddle_length = 50;
    private int xstart = 100;
    private int ycanvas = 400;

    // Thread and executor for game loop
    private ScheduledExecutorService gameExecutor;
    private Canvas canvas;

    // Constants
    private static final int CANVAS_WIDTH = 625;
    private static final int CANVAS_HEIGHT = 446;
    private static final int PADDLE_START_X = 250;
    private static final int PADDLE_LENGTH = 50;
    private static final int MAX_LIVES = 490;

    /**
     * Constructor initializes the game window and components
     */
    public Game() {
        initializeComponents();
        setupGameLoop();
    }

    /**
     * Initializes all UI components including canvas, window properties, etc.
     */
    private void initializeComponents() {
        setTitle("Hospital 2 - Modern Edition");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(150, 150);

        // Create canvas for drawing
        canvas = new Canvas() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                drawGameElements(g);
            }
        };
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        add(canvas, BorderLayout.CENTER);

        // Add key listener to the frame
        addKeyListener(this);

        // Add window listener for graceful shutdown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
    }

    /**
     * Sets up the game loop using ScheduledExecutorService for modern concurrency
     */
    private void setupGameLoop() {
        gameExecutor = Executors.newScheduledThreadPool(1);

        // Schedule the game loop to run at fixed intervals
        gameExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Main game loop that updates and renders game state
     */
    @Override
    public void run() {
        if (!gameRunning) return;

        try {
            // Update game logic
            updateGame();

            // Redraw the canvas
            SwingUtilities.invokeLater(() -> canvas.repaint());

        } catch (Exception e) {
            System.err.println("Error in game loop: " + e.getMessage());
        }
    }

    /**
     * Updates all game logic including player movement, collisions, etc.
     */
    private void updateGame() {
        if (pause || gameover || !playing) return;

        // Update player position based on input
        // Note: This would normally be handled in key processing

        // Move the character
        y = Math.max(5, y - speed);

        // Draw current state
        draw_shape(x - 7, ycanvas - 3, 16, 31, 0, 0, 0, 3); // Person is deleted
        draw_shape(102, 360, 575, 30, 0, 0, 0, 0); // delete pixel errors above the people

        // Game over condition check
        if (y < 5) {
            gameover = true;
            playing = false;
        }
    }

    /**
     * Draws all game elements on the canvas
     */
    private void drawGameElements(Graphics g) {
        // Clear the canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw game elements based on current state
        if (playing && !gameover) {
            // Draw player character
            g.setColor(Color.WHITE);
            g.fillOval(x, y, 16, 31);

            // Draw paddle
            g.setColor(Color.GREEN);
            g.fillRect(position, 400, paddle_length, 10);
        }

        // Draw help screen if needed
        if (help) {
            draw_help(g);
        }

        // Draw game over screen if needed
        if (gameover) {
            draw_game_over(g);
        }
    }

    /**
     * Draws the help screen content
     */
    private void draw_help(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("HELP SCREEN", 200, 50);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Use LEFT/RIGHT arrows to move paddle", 100, 100);
        g.drawString("Press H to toggle help screen", 100, 120);
        g.drawString("Press P to pause game", 100, 140);
    }

    /**
     * Draws the game over screen
     */
    private void draw_game_over(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("GAME OVER", 200, 200);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Final Score: " + points, 200, 250);
    }

    /**
     * Draws a shape at the specified coordinates
     * @param x X coordinate of the shape
     * @param y Y coordinate of the shape
     * @param width Width of the shape
     * @param height Height of the shape
     * @param colorR Red component (0-255)
     * @param colorG Green component (0-255)
     * @param colorB Blue component (0-255)
     * @param shapeType Type of shape to draw (0 = rectangle, etc.)
     */
    private void draw_shape(int x, int y, int width, int height,
                            int colorR, int colorG, int colorB, int shapeType) {
        // Implementation would depend on specific requirements
        // This is a simplified version that just draws a rectangle
        canvas.getGraphics().setColor(new Color(colorR, colorG, colorB));
        if (shapeType == 0) {
            canvas.getGraphics().fillRect(x, y, width, height);
        }
    }

    /**
     * Handles key press events for game controls
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                togglePause();
                break;
            case KeyEvent.VK_H:
                toggleHelp();
                break;
            case KeyEvent.VK_LEFT:
                movePaddleLeft();
                break;
            case KeyEvent.VK_RIGHT:
                movePaddleRight();
                break;
            case KeyEvent.VK_R:
                resetGame();
                break;
        }
    }

    /**
     * Toggles the pause state of the game
     */
    private void togglePause() {
        pause = !pause;
        if (pause) {
            print("Game Paused");
        } else {
            print("Game Resumed");
        }
    }

    /**
     * Toggles the help screen display
     */
    private void toggleHelp() {
        help = !help;
        if (help) {
            print("Help Screen Activated");
        } else {
            print("Help Screen Deactivated");
        }
    }

    /**
     * Moves the paddle to the left
     */
    private void movePaddleLeft() {
        if (position <= start - 100 || pause || gameover) return;

        position -= paddle_length;
        print("Paddle moved left to: " + position);
    }

    /**
     * Moves the paddle to the right
     */
    private void movePaddleRight() {
        if (position >= stop - 50 || pause || gameover) return;

        position += paddle_length;
        print("Paddle moved right to: " + position);
    }

    /**
     * Resets the game state to initial values
     */
    private void resetGame() {
        points = 0;
        lives = MAX_LIVES;
        speed = 10;
        gameover = false;
        selection = 1;
        x = xstart;
        y = 300;
        position = PADDLE_START_X;
        pause = false;
        playing = true;
        print("Game Reset");
    }

    /**
     * Handles key release events (empty implementation)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }

    /**
     * Handles key typed events (empty implementation)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed
    }

    /**
     * Prints messages to console if logging is enabled
     */
    private static void print(String message) {
        System.out.println(message);
    }

    /**
     * Shuts down the game and releases resources
     */
    private void shutdown() {
        gameRunning = false;
        if (gameExecutor != null) {
            gameExecutor.shutdown();
        }
        System.exit(0);
    }

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Failed to set system look and feel: " + e.getMessage());
            }

            Game game = new Game();
            game.setVisible(true);
            game.requestFocus();
        });
    }
}
