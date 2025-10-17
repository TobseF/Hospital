package hospital;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.EnumSet;
import java.util.Set;

/**

Steuert den Spielablauf (Game-Loop), verarbeitet Eingaben und orchestriert
den Datenfluss zwischen Modell und Renderer.
*/
public final class GameController {
private final Canvas canvas;
private final Renderer renderer;
private final GameModel model;

// Zeitsteuerung (ersetzt Thread.sleep aus dem Altcode)
private Timeline timeline;

// Zustandsverwaltung für Tasten (einfaches "pressed" Tracking)
private final Set<KeyCode> pressed = EnumSet.noneOf(KeyCode.class);

/**

Erstellt einen neuen GameController mit einer Zeichenfläche in der gewünschten Größe.
@param width Breite des Spielfeldes.
@param height Höhe des Spielfeldes.
*/
public GameController(int width, int height) {
this.canvas = new Canvas(width, height);
this.model = new GameModel(width, height);
this.renderer = new Renderer(canvas.getGraphicsContext2D(), model);
}
/**

Gibt die Zeichenfläche zurück, die der Controller verwaltet.
@return Canvas für die Darstellung.
*/
public Canvas getCanvas() {
return canvas;
}
/**

Startet den Game-Loop (Timeline). Die Tickrate wird dynamisch aus dem Modell übernommen.
*/
public void startGameLoop() {
rebuildTimeline(model.getSpeedMillis());
timeline.play();
// initiales Rendering (z. B. Hilfe-Bildschirm)
renderer.render();
}
/**

Baut die Timeline gemäß aktueller Geschwindigkeit neu auf.
@param millis Aktualisierungsintervall in Millisekunden.
*/
private void rebuildTimeline(int millis) {
if (timeline != null) {
timeline.stop();
}
timeline = new Timeline(new KeyFrame(Duration.millis(millis), e -> onUpdate()));
timeline.setCycleCount(Timeline.INDEFINITE);
}
/**

Wird periodisch vom Game-Loop (Timeline) aufgerufen. Aktualisiert Logik und Darstellung.
*/
private void onUpdate() {
handleContinuousInput(); // kontinuierliche Eingaben (Links/Rechts)
switch (model.getMode()) {
case HELP -> {
// Hilfe wird lediglich gerendert
}
case PLAYING -> {
if (!model.isPaused()) {
updatePhysicsAndLogic();
}
}
case PAUSED -> {
// Nur Darstellung der Pause
}
case GAME_OVER -> {
model.toggleGameOverBlink();
}
case CONFIRM_EXIT -> {
// Nur Darstellung der Bestätigungs-Box
}
}
renderer.render();
// Falls sich die Geschwindigkeit geändert hat (Level-Up), Timeline neu bauen
if (model.consumeSpeedRescheduleFlag()) {
rebuildTimeline(model.getSpeedMillis());
timeline.play();
}
}

/**

Verarbeitet kontinuierlich gehaltene Tasten (z. B. Links/Rechts).
*/
private void handleContinuousInput() {
if (model.getMode() != GameMode.PLAYING || model.isPaused()) {
return;
}
if (pressed.contains(KeyCode.LEFT)) {
movePaddle(-1);
} else if (pressed.contains(KeyCode.RIGHT)) {
movePaddle(1);
}
}
/**

Aktualisiert die Spielphysik: Bewegung der fallenden Person, Kollisionserkennung, Level-Logik usw.
*/
private void updatePhysicsAndLogic() {
// Person gemäß Polynom bewegen
model.advancePerson();
// Prüfen, ob "Rettung" (Bounce auf Trampolin) erfolgt
if (model.isRescueConditionMet()) {
model.addPointAndMaybeSpeedUp();
model.selectRandomPathAndReset();
return;
}

// Prüfen, ob die Person den Boden "verfehlt" (Game-Over-Fortschritt)
if (model.isMissedGroundHit()) {
model.handleMissedRescue();
if (model.isGameOverReached()) {
model.setMode(GameMode.GAME_OVER);
} else {
model.resetAfterMiss();
}
}
}

/**

Bewegt das Trampolin (Paddle) um eine Paddle-Länge nach links oder rechts.
@param direction -1 für links, +1 für rechts.
*/
private void movePaddle(int direction) {
int step = model.getPaddleLength();
model.movePaddle(direction * step);
}
/**

Event-Handler für gedrückte Tasten.
@param e KeyEvent
*/
public void onKeyPressed(KeyEvent e) {
var code = e.getCode();
pressed.add(code);
switch (code) {
case DIGIT1 -> model.setPaddleLength(40);
case DIGIT2 -> model.setPaddleLength(50);
case DIGIT3 -> model.setPaddleLength(60);

case S -> {
     model.setMode(GameMode.PLAYING);
     model.setPaused(false);
 }
 case P -> {
     if (model.getMode() == GameMode.PLAYING) {
         model.setPaused(!model.isPaused());
     }
 }
 case H -> {
     if (model.getMode() == GameMode.HELP) {
         model.setMode(GameMode.PLAYING);
         model.setPaused(false);
     } else if (model.getMode() == GameMode.PLAYING || model.getMode() == GameMode.PAUSED) {
         model.setMode(GameMode.HELP);
         model.setPaused(true);
     }
 }
 case N -> {
     if (model.getMode() == GameMode.CONFIRM_EXIT) {
         // "N" in Bestätigen-Dialog = zurück
         model.returnFromConfirmExit();
     } else {
         // Neustart (zurück auf Defaultwerte)
         model.resetToDefaults();
         model.setMode(GameMode.PLAYING);
         model.setPaused(false);
     }
 }
 case ESCAPE -> {
     // Bestätigungsdialog anzeigen, vorherigen Modus merken
     model.enterConfirmExit();
 }
 case Y -> {
     if (model.getMode() == GameMode.CONFIRM_EXIT) {
         Platform.exit();
     }
 }
 default -> { /* keine Aktion */ }
}
}

/**

Event-Handler für losgelassene Tasten.
@param e KeyEvent
*/
public void onKeyReleased(KeyEvent e) {
pressed.remove(e.getCode());
}
}