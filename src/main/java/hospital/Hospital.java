package hospital;

import java.awt.*;

/**
 * Repräsentiert das Krankenhaus-Gebäude mit Fenstern, Patienten und Rauch.
 */
public class Hospital extends GameObject {
    private static final int BUILDING_WIDTH = 100;
    private static final int BUILDING_HEIGHT = 405;
    
    private int smokeX;
    private int smokeY;
    private int patientX;
    private int patientY;
    
    /**
     * Erstellt ein neues Krankenhaus an Position (0, 15).
     */
    public Hospital() {
        super(0, 15);
        this.smokeX = 76;
        this.smokeY = 18;
        this.patientX = 5;
        this.patientY = 20;
    }
    
    /**
     * Aktualisiert die Animation (Rauch und Patienten in Fenstern).
     */
    @Override
    public void update() {
        updateSmoke();
        updatePatientAnimation();
    }
    
    /**
     * Aktualisiert die Rauch-Animation.
     */
    private void updateSmoke() {
        smokeX += 2;
        smokeY -= 1;
        
        if (smokeY <= 0) {
            smokeX = 52;
            smokeY = 22;
        }
    }
    
    /**
     * Aktualisiert die Animation der Patienten in den Fenstern.
     */
    private void updatePatientAnimation() {
        patientX += 1;
        
        if (patientX >= 80) {
            patientY += 40;
            patientX = 0;
        }
        
        if (patientY >= 320) {
            patientX = 5;
            patientY = 20;
        }
    }
    
    /**
     * Rendert das Krankenhaus mit allen Details.
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    @Override
    public void render(Renderer renderer) {
        // Gebäude
        renderer.fillRect(x, y, BUILDING_WIDTH, BUILDING_HEIGHT, new Color(102, 102, 102));
        
        // Schild
        renderer.fillRect(50, 364, 50, 12, new Color(214, 237, 245));
        
        // Tür
        renderer.fillRect(50, 376, 50, 60, Color.BLACK);
        
        // Fenster zeichnen (8 Zeilen, 3 Fenster pro Zeile)
        drawWindows(renderer);
        
        // Schornstein-Rauch
        renderer.fillOval(74, 16, 14, 14, new Color(167, 167, 167));
        
        // Animierter Rauch
        int smokeSize = (int) (Math.random() * 16) + 4;
        int smokeColor = (int) (Math.random() * 240) + 15;
        renderer.fillOval(smokeX, smokeY, smokeSize, smokeSize, 
                         new Color(smokeColor, smokeColor, smokeColor));
        
        // Text
        renderer.drawText("Hospital", 52, 375, Color.RED, 12, false);
    }
    
    /**
     * Zeichnet alle Fenster mit Patienten.
     * 
     * @param renderer Der Renderer
     */
    private void drawWindows(Renderer renderer) {
        int windowX = 5;
        int windowY = 20;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 3; col++) {
                drawWindow(renderer, windowX, windowY);
                windowX += 30;
            }
            windowY += 40;
            windowX = 5;
        }
    }
    
    /**
     * Zeichnet ein einzelnes Fenster mit Patient.
     * 
     * @param renderer Der Renderer
     * @param windowX X-Position des Fensters
     * @param windowY Y-Position des Fensters
     */
    private void drawWindow(Renderer renderer, int windowX, int windowY) {
        // Fenster
        renderer.fillRect(windowX, windowY, 16, 22, new Color(0, 206, 255));
        
        // Patient im Fenster (mit Clipping)
        renderer.drawPatientInWindow(windowX, windowY, patientX, patientY);
    }
}