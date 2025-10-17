package hospital;

/**
 * Berechnet verschiedene Flugbahnen für fallende Personen.
 * Verwendet Polynomfunktionen 4. Grades: y = a0 + a1*x + a2*x² + a3*x³ + a4*x⁴
 */
public class TrajectoryCalculator {
    private double a0, a1, a2, a3, a4;
    
    /**
     * Wählt zufällig eine von drei verschiedenen Flugbahn-Typen aus.
     * 
     * @param type Flugbahn-Typ (1-3)
     */
    public void selectRandomTrajectory(int type) {
        switch (type) {
            case 1 -> calculateLinearPath();
            case 2 -> calculateArcPath();
            case 3 -> calculateSteepArcPath();
            default -> calculateLinearPath();
        }
    }
    
    /**
     * Berechnet eine lineare Flugbahn.
     * Parameter: a0 = Anfangshöhe (160-380), a1 = Winkel (-0.5 bis -1.1)
     */
    private void calculateLinearPath() {
        a0 = 380 - (Math.random() * 220); // 160 bis 380
        a1 = -(1.1 - (Math.random() * 0.6)); // -0.5 bis -1.1
        a2 = 0;
        a3 = 0;
        a4 = 0;
    }
    
    /**
     * Berechnet eine bogenförmige Flugbahn.
     * Parameter: a0 = Anfangshöhe (25-300), a2 = Breite (-0.003)
     */
    private void calculateArcPath() {
        a0 = 300 - (Math.random() * 275); // 25 bis 300
        a1 = 1;
        a2 = -0.003;
        a3 = 0;
        a4 = 0;
    }
    
    /**
     * Berechnet eine steile bogenförmige Flugbahn.
     * Parameter: a0 = Anfangshöhe (140-380), a2 = Breite (-0.005 bis -0.002)
     */
    private void calculateSteepArcPath() {
        a0 = 380 - (Math.random() * 240); // 140 bis 380
        a1 = 0;
        a2 = -0.005 + (Math.random() * 0.003); // -0.005 bis -0.002
        a3 = 0;
        a4 = 0;
    }
    
    /**
     * Berechnet die Y-Koordinate für eine gegebene X-Koordinate basierend auf der aktuellen Flugbahn.
     * 
     * @param x X-Koordinate
     * @return Y-Koordinate auf der Flugbahn
     */
    public double calculateY(int x) {
        return a0 + (a1 * x) + (a2 * Math.pow(x, 2)) + (a3 * Math.pow(x, 3)) + (a4 * Math.pow(x, 4));
    }
}