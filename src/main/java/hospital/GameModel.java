package hospital;

import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

/**

Enthält den gesamten, veränderlichen Spielzustand (Model) sowie Spiellogik,
die keine Rendering-Aufgaben umfasst.
*/
public final class GameModel {
// Spielfeld
private final int width;
private final int height;

// Koordinaten-Konvention wie im Altcode:
// yCanvas = 380 - y (y ist "mathematische Höhe", 0=unten, 380=oben)
private static final int FLOOR_Y_CANVAS = 380;

// Spiel-Modus
private GameMode mode = GameMode.HELP;
private GameMode previousMode = GameMode.HELP;
private boolean paused = true;

// Paddle/Trampolin
private int paddleX = 300; // Position des linken Randes
private int paddleLength = 50; // veränderbar via 1/2/3
private static final int PADDLE_Y_CANVAS = 388;
private static final int PADDLE_THICKNESS = 5;

// Person (fallende Figur)
private double x = 100; // x-Start (wie Altcode)
private double y = 250; // logische Höhe
private final int xStart = 100;
private final int xStop = 600;

// Polynomkoeffizienten (Bewegungskurven)
private Polynomial poly = new Polynomial(250, -0.8, 0, 0, 0);

// Punkte/Level
private int points = 0;
private int speedMillis = 10; // identisch zum Altcode (Thread.sleep)
private boolean speedRescheduleNeeded = false;

// "Leben" (hier als "Schadensbalken"): steigt bei Fehlversuch an bis Game Over
private int livesProgressX = 490; // Altcode nutzt Zeichenkoordinate als Status
private static final int LIVES_PROGRESS_MAX = 550;

// Hospital-/Deko-Animation
private int winPersonX = 5;
private int winPersonY = 20;
private int smokeX = 76, smokeY = 18;

// Game-Over-Animation
private boolean gameOverBlink = false;

/**

Erstellt das Spielmodell.
@param width Breite des Spielfeldes.
@param height Höhe des Spielfeldes.
*/
public GameModel(int width, int height) {
this.width = width;
this.height = height;
}
/**

Setzt den vollständigen Spielzustand auf Ausgangswerte zurück.
*/
public void resetToDefaults() {
mode = GameMode.HELP;
previousMode = GameMode.HELP;
paused = true;
paddleX = 300;
paddleLength = 50;

x = xStart;
y = 300;

poly = new Polynomial(250, -0.8, 0, 0, 0);

points = 0;
speedMillis = 10;
speedRescheduleNeeded = true;

livesProgressX = 490;
gameOverBlink = false;
}

/**

Wählt zufällig eine Flugbahn (Polynom) und setzt die Person zurück.
*/
public void selectRandomPathAndReset() {
int selection = ThreadLocalRandom.current().nextInt(1, 4);
poly = switch (selection) {
case 1 -> PathGenerator.path01();
case 2 -> PathGenerator.path02();
default -> PathGenerator.path03();
};
x = xStart;
y = 300;
}
/**

Aktualisiert die Position der fallenden Figur gemäß Polynom.
*/
public void advancePerson() {
// Letzte Zeichnung wird vom Renderer in jedem Frame neu überschrieben
// Bewegung: x erhöht sich pro Tick um 1, y folgt dem Polynom
x += 1;
y = poly.evaluate(x);
// Canvas-Umrechnung in Renderer
}

/**

Prüft, ob Rettungs-Bedingung vorliegt (Trampolin-Treffer oder Rand).
@return true, wenn "gerettet".
*/
public boolean isRescueConditionMet() {
// Umrechnung der "logischen" y in Canvas-Koordinaten
double yCanvas = FLOOR_Y_CANVAS - y;
boolean nearTrampoline = y <= 18; // analog zum Altcode (<=18)
boolean onTrampoline = x >= paddleX && x <= (paddleX + paddleLength);
boolean outOfHorizontalBounds = x >= xStop || x <= xStart;

return nearTrampoline && (onTrampoline || outOfHorizontalBounds);
}

/**

Prüft, ob die Person am Boden "verfehlt" (zu tief gefallen).
@return true, wenn verfehlt.
*/
public boolean isMissedGroundHit() {
return y < 5;
}
/**

Erhöht Punkte, prüft Level und Geschwindigkeit.
*/
public void addPointAndMaybeSpeedUp() {
points++;
if (points > 0 && (Math.IEEEremainder(points, 15) == 0)) {
points += 1; // Bonuspunkt wie Altcode
speedMillis = Math.max(2, speedMillis - 1);
speedRescheduleNeeded = true;
}
}
/**

Wird aufgerufen, wenn eine Rettung verfehlt wurde.
*/
public void handleMissedRescue() {
// Zeichnen des Blutflecks übernimmt der Renderer anhand aktueller x-Position
livesProgressX += 30;
}
/**

Prüft, ob der maximale "Schadensfortschritt" (Game Over) erreicht ist.
*/
public boolean isGameOverReached() {
return livesProgressX > LIVES_PROGRESS_MAX;
}
/**

Setzt die Position nach einem Fehlversuch zurück (wie im Altcode).
*/
public void resetAfterMiss() {
poly = PathGenerator.path02();
x = xStart;
y = 250;
}
/**

Bewegt das Trampolin um dx, begrenzt durch Spielfeld-Logik.
*/
public void movePaddle(int dx) {
int leftBound = 100; // analog Altcode
int rightBound = 550; // analog Altcode (stop - 50)
paddleX = MathUtil.clamp(paddleX + dx, leftBound, rightBound);
}
/**

Schaltet den Blinkzustand für "Restart"-Text im Game-Over um.
*/
public void toggleGameOverBlink() {
gameOverBlink = !gameOverBlink;
}
/**

Wechselt in den Bestätigungsmodus "Wirklich beenden?".
*/
public void enterConfirmExit() {
previousMode = mode;
mode = GameMode.CONFIRM_EXIT;
paused = true;
}
/**

Kehrt aus dem Bestätigungsmodus zum vorherigen Modus zurück.
*/
public void returnFromConfirmExit() {
mode = previousMode;
// pausiert lassen, wenn vorher pausiert
}
// Getter/Setter und Daten für Renderer

/**

Gibt die aktuelle X-Position der Person zurück.
*/
public double getPersonX() {
return x;
}
/**

Gibt die aktuelle Canvas-Y-Position der Person zurück (umgerechnet).
*/
public double getPersonCanvasY() {
return FLOOR_Y_CANVAS - y;
}
/**

X-Start der Flugbahn.
*/
public int getxStart() {
return xStart;
}
/**

X-Limit (rechts).
*/
public int getxStop() {
return xStop;
}
/**

Aktuelle Paddle-Position (links).
*/
public int getPaddleX() {
return paddleX;
}
/**

Aktuelle Paddle-Länge.
*/
public int getPaddleLength() {
return paddleLength;
}
/**

Setzt die Paddle-Länge (über Tasten 1/2/3).
*/
public void setPaddleLength(int paddleLength) {
this.paddleLength = paddleLength;
}
/**

Canvas-Y-Position des Trampolins.
*/
public int getPaddleY() {
return PADDLE_Y_CANVAS;
}
/**

Dicke des Trampolins.
*/
public int getPaddleThickness() {
return PADDLE_THICKNESS;
}
/**

Aktuelle Punktzahl.
*/
public int getPoints() {
return points;
}
/**

Aktueller Spielmodus.
*/
public GameMode getMode() {
return mode;
}
/**

Setzt den Spielmodus.
*/
public void setMode(GameMode mode) {
this.mode = mode;
}
/**

Gibt zurück, ob das Spiel pausiert ist.
*/
public boolean isPaused() {
return paused;
}
/**

Setzt den Pausenstatus.
*/
public void setPaused(boolean paused) {
this.paused = paused;
}
/**

Aktuelles Rendering-Blinken für Game-Over-Text.
*/
public boolean isGameOverBlink() {
return gameOverBlink;
}
/**

Intervall in Millisekunden für den Game-Loop.
*/
public int getSpeedMillis() {
return speedMillis;
}
/**

Flag, ob die Timeline wegen geänderter Geschwindigkeit neu gestartet werden soll.
*/
public boolean consumeSpeedRescheduleFlag() {
boolean v = speedRescheduleNeeded;
speedRescheduleNeeded = false;
return v;
}
/**

X-Position (Pixel) des "Schadensbalkens" (lebensanzeige).
*/
public int getLivesProgressX() {
return livesProgressX;
}
/**

Maximale X-Position (Pixel) des "Schadensbalkens".
*/
public int getLivesProgressMax() {
return LIVES_PROGRESS_MAX;
}
/**

Breite des Spielfeldes.
*/
public int getWidth() {
return width;
}
/**

Höhe des Spielfeldes.
*/
public int getHeight() {
return height;
}
/**

Aktuelle X-Position der animierten "Fenster-Person" (Hospital).
*/
public int getWinPersonX() {
return winPersonX;
}
/**

Aktuelle Y-Position der animierten "Fenster-Person" (Hospital).
*/
public int getWinPersonY() {
return winPersonY;
}
/**

Aktualisiert die Fenster-Animation (Hospital).
*/
public void advanceWindowAnimation() {
winPersonX += 1;
if (winPersonX >= 80) {
winPersonY += 40;
winPersonX = 0;
}
if (winPersonY >= 320) {
winPersonX = 5;
winPersonY = 20;
}
}
/**

X-Position der Rauchwolke (Hospital-Schornstein).
*/
public int getSmokeX() {
return smokeX;
}
/**

Y-Position der Rauchwolke (Hospital-Schornstein).
*/
public int getSmokeY() {
return smokeY;
}
/**

Setzt die Rauch-Position (für eine neue Wolke).
*/
public void resetSmoke(int x, int y) {
this.smokeX = x;
this.smokeY = y;
}
/**

Verschiebt die Rauchwolke leicht nach rechts/oben.
*/
public void advanceSmoke() {
smokeX += 2;
smokeY -= 1;
if (smokeY <= 0) {
smokeX = 52;
smokeY = 22;
}
}
/**

Farbe für das Trampolin.
*/
public Color trampolineColor() {
return Color.BLUE;
}
}