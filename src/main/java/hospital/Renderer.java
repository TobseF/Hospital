package hospital;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.concurrent.ThreadLocalRandom;

/**

Verantwortlich für alle Zeichenoperationen.
Nutzt JavaFX GraphicsContext für Canvas-Zeichnungen.
*/
public final class Renderer {
private final GraphicsContext g;
private final GameModel model;

// Schriftarten (wiederverwendet)
private final Font fontNormal = Font.font("Arial", 12);
private final Font fontBold = Font.font("Arial", 14);
private final Font fontTitle = Font.font("Arial", 50);
private final Font fontPause = Font.font("Arial", 25);

/**

Erstellt einen Renderer.
@param g GraphicsContext der Canvas.
@param model Spielmodell.
*/
public Renderer(GraphicsContext g, GameModel model) {
this.g = g;
this.model = model;
}
/**

Haupt-Render-Methode. Zeichnet vollständigen Frame entsprechend dem Spielmodus.
*/
public void render() {
clearBackground();
// Spielfeld (Hospital, Rauch, Fensteranimation, Scoreboard etc.)
drawHospitalAndDecorations();
drawScoreboard();

// Paddle/Trampolin
drawPaddle();

// Fallende Person
drawFallingPerson();

// Overlays (Hilfe / Pause / Game Over / Bestätigungsdialog)
switch (model.getMode()) {
case HELP -> drawHelpOverlay();
case PAUSED -> drawPauseOverlay();
case GAME_OVER -> drawGameOverOverlay();
case CONFIRM_EXIT -> drawConfirmExitOverlay();
case PLAYING -> {
if (model.isPaused()) {
drawPauseOverlay();
}
}
}
}

/**

Malt den Hintergrund schwarz.
*/
private void clearBackground() {
g.setFill(Color.BLACK);
g.fillRect(0, 0, model.getWidth(), model.getHeight());
}
/**

Zeichnet Hospital, Rauch und animierte Fensterpersonen.
*/
private void drawHospitalAndDecorations() {
// Linkes Gebäude (nur wenn Person noch sehr links: wie im Altcode, vereinfacht optional)
if (model.getPersonX() <= 105) {
fillRect(0, 15, 100, 405, Color.rgb(102, 102, 102));
fillRect(50, 364, 50, 12, Color.rgb(214, 237, 245));
fillRect(50, 376, 50, 60, Color.BLACK);
fillRect(5, 20, 16, 22, Color.rgb(0, 206, 255));
drawText("Hospital", 52, 375, Color.WHITE, fontNormal);
}
// Rauch
fillOval(74, 16, 14, 14, Color.rgb(167, 167, 167));

// zufällige Rauchwolken
model.advanceSmoke();
int len = rnd(4, 20);
int hei = rnd(4, 20);
int gray = rnd(15, 255);
fillOval(model.getSmokeX(), model.getSmokeY(), len, hei, Color.rgb(gray, gray, gray));
if (model.getSmokeY() <= 0) {
model.resetSmoke(52, 22);
}

// Fenster-Animation
model.advanceWindowAnimation();
int xf = 5;
int yf = 20;
for (int row = 0; row < 8; row++) {
for (int col = 0; col < 3; col++) {
drawWindowWithPerson(xf + col * 30, yf);
}
yf += 40;
}

// Falls verfehlt: zeichne "Blut" nahe Boden (an aktueller X-Position)
if (model.isMissedGroundHit()) {
fillOval((int) model.getPersonX() - 4, 400, 16, 9, Color.rgb(255, 45, 52));
}
}

/**

Zeichnet ein Fenster mit animierter Person im Fenster.
@param x X-Position (Fenster links).
@param y Y-Position (Fenster oben).
*/
private void drawWindowWithPerson(int x, int y) {
fillRect(x, y, 16, 22, Color.rgb(0, 206, 255));
// Clip simulieren: wir zeichnen nur innerhalb des Fensters
g.save();
g.beginPath();
g.rect(x, y, 16, 22);
g.closePath();
g.clip();
// kleine Strichmännchenfigur
int px = model.getWinPersonX();
int py = model.getWinPersonY();
g.setStroke(Color.WHITE);
g.strokeOval(px - 1, py + 4, 8, 8); // Kopf
g.strokeLine(px + 3, py + 11, px + 3, py + 21);
g.strokeLine(px + 3, py + 21, px + 8, py + 29);
g.strokeLine(px + 3, py + 21, px - 2, py + 29);
g.strokeLine(px + 3, py + 17, px + 10, py + 14);
g.strokeLine(px + 3, py + 19, px + 10, py + 16);
g.restore();
}
/**

Zeichnet das Scoreboard (Punkte und "Lebens/Schaden"-Balken).
*/
private void drawScoreboard() {
// Außenrahmen
strokeRect(500, 5, 112, 62, Color.WHITE);
strokeRect(500, 5, 111, 61, Color.WHITE);
strokeRect(500, 5, 110, 60, Color.WHITE);
// Punktefeld
fillRect(540, 42, 60, 16, Color.rgb(174, 186, 204));
drawText(Integer.toString(model.getPoints()), 545, 55, Color.RED, fontBold);

// Drei Köpfe (wie Altcode)
drawSmallPerson(525, 10);
drawSmallPerson(555, 10);
drawSmallPerson(585, 10);

drawText("L:", 506, 28, Color.WHITE, fontBold);
drawText("P:", 506, 53, Color.WHITE, fontBold);

// "Schadens"-Balken (livesProgressX wächst nach rechts)
int from = 490;
int to = Math.min(model.getLivesProgressX(), model.getLivesProgressMax());
if (to > from) {
fillRect(from, 8, to - from, 30, Color.rgb(255, 45, 52));
}
}

/**

Zeichnet die fallende Person (Strichmännchen) inklusive "Wink"-Animation.
*/
private void drawFallingPerson() {
int posx = (int) model.getPersonX();
int posy = (int) model.getPersonCanvasY();
// einfaches Armwinken durch alternierende Linien
boolean up = (System.currentTimeMillis() / 500) % 2 == 0;

g.setStroke(Color.WHITE);
// Arme (wechselnd)
if (up) {
g.strokeLine(posx + 3, posy + 13, posx - 4, posy + 9);
g.strokeLine(posx + 3, posy + 13, posx + 10, posy + 9);
} else {
g.strokeLine(posx + 2, posy + 13, posx - 4, posy + 16);
g.strokeLine(posx + 2, posy + 13, posx + 10, posy + 16);
}
// Kopf und Körper
g.strokeOval(posx, posy, 7, 7);
g.strokeOval(posx - 1, posy - 1, 8, 8);
g.strokeLine(posx + 3, posy + 6, posx + 3, posy + 16);
g.strokeLine(posx + 3, posy + 16, posx + 8, posy + 24);
g.strokeLine(posx + 3, posy + 16, posx - 2, posy + 24);
}

/**

Zeichnet das Trampolin mit zwei Figuren an den Enden.
*/
private void drawPaddle() {
int x = model.getPaddleX();
int y = model.getPaddleY();
// Trampolin-Balken
fillRect(x, y, model.getPaddleLength(), model.getPaddleThickness(), model.trampolineColor());

// Figuren links/rechts
drawGroundPerson(x - 10, 380, true);
drawGroundPerson(x + model.getPaddleLength() + 4, 380, false);

// "elastische" Schattierung
g.save();
g.beginPath();
g.rect(x, 388, model.getPaddleLength(), 5);
g.closePath();
g.clip();
g.setFill(Color.BLUE);
g.fillOval(x - 20, 385, 90, 10);
g.setFill(Color.BLACK);
g.fillOval(x - 20, 360, 90, 31);
g.restore();
}

/**

Zeichnet eine stehende Person auf Bodenniveau, stilisiert.
@param posx X-Position des Kopfes.
@param posy Y-Position des Kopfes.
@param left true für linke Armhaltung, false für rechte.
*/
private void drawGroundPerson(int posx, int posy, boolean left) {
g.setStroke(Color.WHITE);
g.strokeOval(posx, posy, 7, 7);
g.strokeOval(posx - 1, posy - 1, 8, 8);
if (left) {
g.strokeLine(posx + 3, posy + 12, posx - 4, posy + 9);
g.strokeLine(posx + 3, posy + 14, posx - 4, posy + 11);
} else {
g.strokeLine(posx + 3, posy + 12, posx + 10, posy + 9);
g.strokeLine(posx + 3, posy + 14, posx + 10, posy + 11);
}
g.strokeLine(posx + 3, posy + 6, posx + 3, posy + 16);
g.strokeLine(posx + 3, posy + 16, posx + 8, posy + 24);
g.strokeLine(posx + 3, posy + 16, posx - 2, posy + 24);
}
/**

Zeichnet das Hilfe-Overlay.
*/
private void drawHelpOverlay() {
int posx = 220;
int posy = 120;
fillRect(posx - 5, posy - 15, 158, 158, Color.rgb(0, 0, 0, 0.65));
drawText("HELP", posx + 35, posy, Color.WHITE, fontBold);
drawText("Aktion Taste", posx, posy + 24, Color.WHITE, fontBold);
drawText("Spiel starten S", posx, posy + 40, Color.WHITE, fontNormal);
drawText("Pause umschalten P", posx, posy + 52, Color.WHITE, fontNormal);
drawText("Spiel neu starten N", posx, posy + 64, Color.WHITE, fontNormal);
drawText("Spiel beenden ESC", posx, posy + 76, Color.WHITE, fontNormal);
drawText("Schwierigkeit 1 - 3", posx, posy + 88, Color.WHITE, fontNormal);
drawText("Hilfe ein/aus H", posx, posy + 100, Color.WHITE, fontNormal);
drawText("Ziel: Rette so viele wie möglich!", posx, posy + 125, Color.WHITE, fontNormal);

strokeRect(posx - 4, posy - 14, 155, 156, Color.WHITE);
}

/**

Zeichnet das Pause-Overlay.
*/
private void drawPauseOverlay() {
drawText("Pause", 260, 200, Color.WHITE, fontPause);
}
/**

Zeichnet das Game-Over-Overlay mit blinkendem "Restart"-Hinweis.
*/
private void drawGameOverOverlay() {
drawText("Game Over", 175, 200, Color.RED, fontTitle);
var color = model.isGameOverBlink() ? Color.RED : Color.WHITE;
drawText("Spiel neu starten: N", 220, 350, color, fontBold);
drawText("Beenden: ESC", 220, 370, Color.WHITE, fontBold);
}
/**

Zeichnet das Bestätigungs-Overlay zum Beenden.
*/
private void drawConfirmExitOverlay() {
int x = 241, y = 162, w = 114, h = 50;
fillRect(x, y, w, h, Color.rgb(0, 0, 0, 0.85));
strokeRect(x, y, w, h, Color.WHITE);
drawText("Wirklich beenden?", x + 12, y + 18, Color.WHITE, fontBold);
drawText("Ja (Y)", x + 12, y + 36, Color.WHITE, fontNormal);
drawText("Nein (N)", x + 60, y + 36, Color.WHITE, fontNormal);
}
// Kleine Zeichenhilfen (DRY)

private void fillRect(int x, int y, int w, int h, Color c) {
g.setFill(c);
g.fillRect(x, y, w, h);
}

private void strokeRect(int x, int y, int w, int h, Color c) {
g.setStroke(c);
g.strokeRect(x, y, w, h);
}

private void fillOval(int x, int y, int w, int h, Color c) {
g.setFill(c);
g.fillOval(x, y, w, h);
}

private void drawText(String text, int x, int y, Color c, Font f) {
g.setFill(c);
g.setFont(f);
g.fillText(text, x, y);
}

private void drawSmallPerson(int x, int y) {
g.setStroke(Color.WHITE);
g.strokeOval(x, y, 7, 7);
g.strokeOval(x - 1, y - 1, 8, 8);
g.strokeLine(x + 3, y + 6, x + 3, y + 16);
g.strokeLine(x + 3, y + 16, x + 8, y + 24);
g.strokeLine(x + 3, y + 16, x - 2, y + 24);
}

private int rnd(int min, int max) {
return ThreadLocalRandom.current().nextInt(min, max + 1);
}
}
