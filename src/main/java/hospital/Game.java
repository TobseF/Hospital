package hospital;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**

Startpunkt der Anwendung. Initialisiert JavaFX, die Zeichenfläche (Canvas)
und den GameController, der den Spielablauf steuert.
*/
public class Game extends Application {
/**

Breite der Spiel-Fläche in Pixeln.
*/
public static final int WIDTH = 600;
/**

Höhe der Spiel-Fläche in Pixeln.
*/
public static final int HEIGHT = 400;
@Override
public void start(Stage stage) {
var controller = new GameController(WIDTH, HEIGHT);

var root = new StackPane(controller.getCanvas());
 var scene = new Scene(root, WIDTH, HEIGHT);

 // Eingaben an den Controller delegieren
 scene.setOnKeyPressed(controller::onKeyPressed);
 scene.setOnKeyReleased(controller::onKeyReleased);

 stage.setScene(scene);
 stage.setTitle("Hospital 2 - Modern");
 stage.setResizable(false);
 stage.show();

 controller.startGameLoop();
}

/**

Einstiegspunkt (falls ohne JavaFX-Launcher ausgeführt).
*/
public static void main(String[] args) {
launch(args);
}
}
