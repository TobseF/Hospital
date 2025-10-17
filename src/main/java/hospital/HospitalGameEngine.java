package hospital;

/**
 * Core game engine: updates game state and drives rendering in a loop.
 * Extracted from the legacy Game.run method to provide a clean separation
 * between UI (Frame/Canvas), input, state, and rendering.
 */
public final class HospitalGameEngine implements Runnable {

    private final GameState state;
    private final Renderer renderer;
    private volatile boolean running = false;
    private Thread thread;

    public HospitalGameEngine(GameState state, Renderer renderer) {
        this.state = state;
        this.renderer = renderer;
    }

    /** Starts the engine loop in a background thread. */
    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "HospitalGameEngine");
        thread.start();
    }

    /** Stops the engine loop. */
    public synchronized void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (state.game && running) {
            while (state.playing && running) {
                while (state.y >= 5 && running) {
                    if (!state.pause) {
                        // erase moving artifacts
                        renderer.drawShape(state.x - 7, state.yCanvas - 3, 16, 31, 0, 0, 0, 3);
                        renderer.drawShape(102, 360, 575, 30, 0, 0, 0, 0);

                        // update trajectory
                        state.y = state.trajectory.yAt(state.x);
                        state.xCanvas = state.x;
                        state.yCanvas = 380 - state.y;

                        renderer.drawPerson(state.x, state.yCanvas);
                        renderer.drawPaddle(state.paddleX);

                        state.x = state.x + 1;
                        sleep(state.speedMs);

                        // bounce or fail conditions
                        boolean hitsPaddle = state.y <= 18 && state.x >= state.paddleX && state.x <= (state.paddleX + state.paddleLength);
                        boolean outOfBounds = state.x >= Config.X_STOP || state.x <= Config.X_START;
                        if (state.y <= 20 && (hitsPaddle || outOfBounds)) {
                            state.points++;
                            // choose next path
                            int sel = 1 + (int)(Math.random() * 3);
                            state.selection = sel;
                            switch (sel) {
                                case 1: state.trajectory = Trajectory.randomLinear(); break;
                                case 2: state.trajectory = Trajectory.arc1(); break;
                                case 3: state.trajectory = Trajectory.randomArc2(); break;
                                default: state.trajectory = Trajectory.randomLinear();
                            }
                            state.x = Config.X_START;
                            state.y = 300;
                        }

                        renderer.drawHospital();
                        renderer.drawPoints();

                        // level-up logic: every 15 points increase speed and add bonus
                        if (state.points > 0 && Math.IEEEremainder(state.points, 15) == 0) {
                            state.speedMs = Math.max(1, state.speedMs - 1);
                            state.points = state.points + 1;
                        }
                    } else {
                        sleep(500);
                    }
                }

                // fell on the ground: lose life (visualized by moving red bar to the right)
                if (state.livesX <= 550) {
                    renderer.drawShape(state.x - 4, 400, 16, 9, 255, 45, 52, 3);// blood stain
                    renderer.clearPaddle();
                    state.livesX += 30;
                    renderer.drawShape(state.livesX, 8, 18, 30, 255, 45, 52, 3);// paint over lives
                    state.trajectory = Trajectory.arc1();
                    state.x = Config.X_START;
                    state.y = 250;
                } else {
                    state.gameOver = true;
                    state.playing = false;
                }
            }

            if (!state.reallyEnd) {
                renderer.drawHelp();
            }
            if (state.gameOver) {
                renderer.drawGameOver();
            }
            sleep(1000);
        }
    }

    // region Controls (exposed for InputHandler)

    /** Adjusts paddle length (difficulty). */
    public void setPaddleLength(int length) { state.paddleLength = length; }

    /** Starts or resumes the game. */
    public void startGame() {
        state.help = false;
        state.pause = false;
        state.playing = true;
        renderer.drawHelp();
    }

    /** Toggle pause. */
    public void togglePause() {
        state.pause = !state.pause;
        renderer.drawText("Pause", 0, 260, 200, state.pause ? 255 : 0, state.pause ? 255 : 0, state.pause ? 255 : 0, 1, 25);
    }

    /** Show quit dialog. */
    public void askQuit() {
        state.pause = true;
        state.reallyEnd = true;
        state.gameOver = false;
        renderer.drawShape(241, 162, 114, 50, 0, 0, 0, 0);
        renderer.drawShape(241, 162, 114, 50, 255, 255, 255, 1);
        renderer.drawText("Really Quit", 0, 265, 180, 255, 255, 255, 1, 12);
        renderer.drawText("Yes", 0, 270, 198, 255, 255, 255, 1, 12);
        renderer.drawText("No", 0, 310, 198, 255, 255, 255, 1, 12);
        renderer.drawLine(270, 200, 279, 200);
        renderer.drawLine(310, 200, 318, 200);
    }

    /** Confirm quit. */
    public void confirmQuit() {
        if (state.reallyEnd) System.exit(0);
    }

    /** Restart. If on confirm quit dialog: closes it; else restarts game. */
    public void restartOrCloseDialog() {
        if (state.reallyEnd) {
            renderer.drawShape(238, 158, 118, 56, 0, 0, 0, 0);
            state.pause = false;
            state.reallyEnd = false;
            if (!state.gameOver && state.livesX >= 550) {
                state.gameOver = true;
            }
        } else {
            // full reset
            state.pause = false;
            state.playing = true;
            renderer.drawShape(0, 0, 650, 500, 0, 0, 0, 0);
            state.points = 0;
            state.livesX = Config.INITIAL_LIVES_X;
            state.speedMs = Config.INITIAL_SPEED;
            state.gameOver = false;
            state.selection = 1;
            state.x = Config.X_START;
            state.y = 300;
        }
    }

    /** Toggle help overlay. */
    public void toggleHelp() {
        state.help = !state.help;
        state.pause = state.help;
        renderer.drawHelp();
    }

    /** Move paddle left by one length. */
    public void moveLeft() {
        if (state.paddleX > Config.X_START && !state.pause && !state.gameOver) {
            state.paddleX = Math.max(Config.X_START, state.paddleX - state.paddleLength);
            renderer.clearPaddle();
            renderer.drawPaddle(state.paddleX);
        }
    }

    /** Move paddle right by one length. */
    public void moveRight() {
        if (state.paddleX < Config.X_STOP - 50 && !state.pause && !state.gameOver) {
            state.paddleX = Math.min(Config.X_STOP - 50, state.paddleX + state.paddleLength);
            renderer.clearPaddle();
            renderer.drawPaddle(state.paddleX);
        }
    }

    // endregion

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
