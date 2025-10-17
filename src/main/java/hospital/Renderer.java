
package hospital;

import java.awt.*;

/**
 * Verantwortlich für das Rendern aller grafischen Elemente des Spiels.
 * Kapselt alle Zeichen-Operationen auf dem Canvas.
 */
public class Renderer {
    private final Canvas canvas;
    private int personAnimationCounter = 0;
    
    /**
     * Erstellt einen neuen Renderer für das gegebene Canvas.
     * 
     * @param canvas Das Canvas zum Zeichnen
     */
    public Renderer(Canvas canvas) {
        this.canvas = canvas;
    }
    
    /**
     * Zeichnet ein gefülltes Rechteck.
     */
    public void fillRect(int x, int y, int width, int height, Color color) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet einen Rechteck-Rahmen.
     */
    public void drawRect(int x, int y, int width, int height, Color color) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            g.drawRect(x, y, width, height);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet ein gefülltes Oval.
     */
    public void fillOval(int x, int y, int width, int height, Color color) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            g.fillOval(x, y, width, height);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet einen Oval-Rahmen.
     */
    public void drawOval(int x, int y, int width, int height, Color color) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            g.drawOval(x, y, width, height);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet eine Linie.
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet Text.
     */
    public void drawText(String text, int x, int y, Color color, int size, boolean bold) {
        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.setColor(color);
            int style = bold ? Font.BOLD : Font.PLAIN;
            g.setFont(new Font("Arial", style, size));
            g.drawString(text, x, y);
            g.dispose();
        }
    }
    
    /**
     * Zeichnet eine Person mit Animation (winkende Arme).
     */
    public void drawPerson(int x, int y, int animationCounter) {
        Graphics g = canvas.getGraphics();
        if (g == null) return;
        
        g.setColor(Color.WHITE);
        
        // Animation: Arme wechseln zwischen oben und unten
        boolean armsUp = (animationCounter % 40) >= 20;
        
        if (armsUp) {
            drawPersonArms(g, x, y, true);
        } else {
            drawPersonArms(g, x, y, false);
        }
        
        // Kopf
        g.drawOval(x, y, 7, 7);
        g.drawOval(x - 1, y - 1, 8, 8);
        
        // Körper
        g.drawLine(x + 3, y + 6, x + 3, y + 16);
        
        // Beine
        g.drawLine(x + 3, y + 16, x + 8, y + 24);
        g.drawLine(x + 3, y + 16, x - 2, y + 24);
        
        g.dispose();
    }
    
    /**
     * Zeichnet die Arme der Person (oben oder unten).
     */
    private void drawPersonArms(Graphics g, int x, int y, boolean up) {
        if (up) {
            g.drawLine(x + 3, y + 13, x - 4, y + 9);
            g.drawLine(x + 3, y + 13, x + 10, y + 9);
        } else {
            g.drawLine(x + 2, y + 13, x - 4, y + 16);
            g.drawLine(x + 2, y + 13, x + 10, y + 16);
        }
    }
    
    /**
     * Zeichnet eine Person an der Paddle.
     */
    public void drawPaddlePerson(int x, int y, boolean isLeft) {
        Graphics g = canvas.getGraphics();
        if (g == null) return;
        
        g.setColor(Color.WHITE);
        
        // Kopf
        g.drawOval(x, y, 7, 7);
        g.drawOval(x - 1, y - 1, 8, 8);
        
        // Arm (nach oben)
        if (isLeft) {
            g.drawLine(x + 3, y + 12, x - 4, y + 9);
            g.drawLine(x + 3, y + 14, x - 4, y + 11);
        } else {
            g.drawLine(x + 3, y + 12, x + 10, y + 9);
            g.drawLine(x + 3, y + 14, x + 10, y + 11);
        }
        
        // Körper
        g.drawLine(x + 3, y + 6, x + 3, y + 16);
        
        // Beine
        g.drawLine(x + 3, y + 16, x + 8, y + 24);
        g.drawLine(x + 3, y + 16, x - 2, y + 24);
        
        g.dispose();
    }
    
    /**
     * Zeichnet den Trampolin-Effekt (blauer Bogen).
     */
    public void drawPaddleEffect(int x, int y, int length) {
        Graphics g = canvas.getGraphics();
        if (g == null) return;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setClip(x, y, length, 5);
        g2.setColor(Color.BLUE);
        g2.fillOval(x - 20, y - 3, 90, 10);
        g2.setColor(Color.BLACK);
        g2.fillOval(x - 20, y - 28, 90, 31);
        g2.dispose();
    }
    
    /**
     * Zeichnet einen Patienten in einem Fenster mit Clipping.
     */
    public void drawPatientInWindow(int windowX, int windowY, int patientX, int patientY) {
        Graphics g = canvas.getGraphics();
        if (g == null) return;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setClip(windowX, windowY, 16, 22);
        g2.setColor(Color.BLACK);
        
        // Kopf
        g2.drawOval(patientX - 1, patientY + 4, 8, 8);
        
        // Körper
        g2.drawLine(patientX + 3, patientY + 11, patientX + 3, patientY + 21);
        
        // Beine
        g2.drawLine(patientX + 3, patientY + 21, patientX + 8, patientY + 29);
        g2.drawLine(patientX + 3, patientY + 21, patientX - 2, patientY + 29);
        
        // Arme
        g2.drawLine(patientX + 3, patientY + 17, patientX + 10, patientY + 14);
        g2.drawLine(patientX + 3, patientY + 19, patientX + 10, patientY + 16);
        
        g2.dispose();
    }
    
    /**
     * Löscht das gesamte Canvas.
     */
    public void clearScreen() {
        fillRect(0, 0, 650, 500, Color.BLACK);
    }
    
    /**
     * Zeichnet die Punkteanzeige.
     */
    public void drawScoreboard(int points, int lives, int maxLives) {
        // Rahmen
        drawRect(500, 5, 112, 62, Color.WHITE);
        drawRect(500, 5, 111, 61, Color.WHITE);
        drawRect(500, 5, 110, 60, Color.WHITE);
        
        // Punkte-Anzeige
        fillRect(540, 42, 60, 16, new Color(174, 186, 204));
        drawText(String.valueOf(points), 545, 55, Color.RED, 14, true);
        
        // Label
        drawText("L:", 506, 28, Color.WHITE, 12, true);
        drawText("P:", 506, 53, Color.WHITE, 12, true);
        
        // Leben (Personen-Icons)
        drawPerson(525, 10, 0);
        drawPerson(555, 10, 0);
        drawPerson(585, 10, 0);
        
        // Leben-Balken übermalen
        if (lives > 490) {
            fillRect(lives, 8, 18, 30, new Color(255, 45, 52));
        }
    }
    
    /**
     * Zeichnet das Hilfe-Menü.
     */
    public void drawHelp() {
        int x = 220;
        int y = 120;
        
        // Hintergrund
        fillRect(x - 5, y - 15, 158, 158, Color.BLACK);
        
        // Rahmen
        drawRect(x - 4, y - 14, 155, 156, Color.WHITE);
        
        // Text
        drawText("HELP", x + 60, y, Color.WHITE, 12, true);
        drawText("Action                     Key", x, y + 24, Color.WHITE, 12, true);
        drawText("Start Game                   S", x, y + 40, Color.WHITE, 12, true);
        drawText("Pause                            P", x, y + 52, Color.WHITE, 12, true);
        drawText("Restart Game               N", x, y + 63, Color.WHITE, 12, true);
        drawText("End Game                  ESC", x, y + 76, Color.WHITE, 12, true);
        drawText("Switch Difficultly        1-3", x, y + 89, Color.WHITE, 12, true);
        drawText("Show Help                    H", x, y + 102, Color.WHITE, 12, true);
        drawText("Goal: Save as many lives", x, y + 125, Color.WHITE, 12, true);
        drawText("          as possible.", x, y + 138, Color.WHITE, 12, true);
    }
    
    /**
     * Zeichnet den Game Over Bildschirm.
     */
    public void drawGameOver(boolean blink) {
        drawText("Game Over", 175, 200, Color.RED, 50, true);
        
        Color restartColor = blink ? Color.RED : Color.WHITE;
        drawText("Restart Game          N", 220, 350, restartColor, 12, true);
        drawText("End Game               Esc", 220, 360, Color.WHITE, 12, true);
    }
    
    /**
     * Zeichnet den Pause-Text.
     */
    public void drawPause() {
        drawText("Pause", 260, 200, Color.WHITE, 25, true);
    }
    
    /**
     * Löscht den Pause-Text.
     */
    public void clearPause() {
        drawText("Pause", 260, 200, Color.BLACK, 25, true);
    }
    
    /**
     * Zeichnet den "Wirklich beenden?"-Dialog.
     */
    public void drawQuitDialog() {
        fillRect(241, 162, 114, 50, Color.BLACK);
        drawRect(241, 162, 114, 50, Color.WHITE);
        drawText("Really Quit", 265, 180, Color.WHITE, 12, true);
        drawText("Yes", 270, 198, Color.WHITE, 12, true);
        drawText("No", 310, 198, Color.WHITE, 12, true);
        drawLine(270, 200, 279, 200, Color.WHITE);
        drawLine(310, 200, 318, 200, Color.WHITE);
    }
    
    /**
     * Zeichnet einen Blutfleck.
     */
    public void drawBloodStain(int x, int y) {
        fillOval(x - 4, y, 16, 9, new Color(255, 45, 52));
    }
}