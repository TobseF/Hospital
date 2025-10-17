package hospital;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Translates keyboard input into engine actions.
 */
public final class InputHandler extends KeyAdapter {
    private final HospitalGameEngine engine;

    public InputHandler(HospitalGameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1: engine.setPaddleLength(40); break;
            case KeyEvent.VK_2: engine.setPaddleLength(50); break;
            case KeyEvent.VK_3: engine.setPaddleLength(60); break;
            case KeyEvent.VK_S: engine.startGame(); break;
            case KeyEvent.VK_P: engine.togglePause(); break;
            case KeyEvent.VK_ESCAPE: engine.askQuit(); break;
            case KeyEvent.VK_Y: engine.confirmQuit(); break;
            case KeyEvent.VK_N: engine.restartOrCloseDialog(); break;
            case KeyEvent.VK_H: engine.toggleHelp(); break;
            case KeyEvent.VK_LEFT: engine.moveLeft(); break;
            case KeyEvent.VK_RIGHT: engine.moveRight(); break;
            default: /* ignore */
        }
    }
}
