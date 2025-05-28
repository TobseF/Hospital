package hospital.model;

import hospital.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the hospital building and its patients.
 */
public class Hospital {
    private int width;
    private int height;
    private int signWidth;
    private int signHeight;
    private int smokeX;
    private int smokeY;
    private List<Person> patients;
    private Random random;
    
    /**
     * Creates a new hospital with default dimensions.
     */
    public Hospital() {
        this.width = Constants.HOSPITAL_WIDTH;
        this.height = Constants.HOSPITAL_HEIGHT;
        this.signWidth = Constants.HOSPITAL_SIGN_WIDTH;
        this.signHeight = Constants.HOSPITAL_SIGN_HEIGHT;
        this.smokeX = 76;
        this.smokeY = 18;
        this.patients = new ArrayList<>();
        this.random = new Random();
        initializePatients();
    }
    
    /**
     * Initializes the patients in the hospital windows.
     */
    private void initializePatients() {
        int xf = 5;
        int yf = 20;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                patients.add(new Person(xf + j * 30, yf, true));
            }
            yf += 40;
        }
    }
    
    /**
     * Gets the width of the hospital.
     *
     * @return The width of the hospital
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the hospital.
     *
     * @return The height of the hospital
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the width of the hospital sign.
     *
     * @return The width of the hospital sign
     */
    public int getSignWidth() {
        return signWidth;
    }
    
    /**
     * Gets the height of the hospital sign.
     *
     * @return The height of the hospital sign
     */
    public int getSignHeight() {
        return signHeight;
    }
    
    /**
     * Gets the x-coordinate of the smoke.
     *
     * @return The x-coordinate of the smoke
     */
    public int getSmokeX() {
        return smokeX;
    }
    
    /**
     * Gets the y-coordinate of the smoke.
     *
     * @return The y-coordinate of the smoke
     */
    public int getSmokeY() {
        return smokeY;
    }
    
    /**
     * Updates the smoke position.
     */
    public void updateSmoke() {
        smokeX += 2;
        smokeY -= 1;
        
        if (smokeY <= 0) {
            smokeX = 52;
            smokeY = 22;
        }
    }
    
    /**
     * Gets a random smoke length.
     *
     * @return A random smoke length
     */
    public int getRandomSmokeLength() {
        return random.nextInt(16) + 4;
    }
    
    /**
     * Gets a random smoke height.
     *
     * @return A random smoke height
     */
    public int getRandomSmokeHeight() {
        return random.nextInt(16) + 4;
    }
    
    /**
     * Gets a random smoke color.
     *
     * @return A random smoke color
     */
    public int getRandomSmokeColor() {
        return random.nextInt(240) + 15;
    }
    
    /**
     * Gets the list of patients in the hospital.
     *
     * @return The list of patients
     */
    public List<Person> getPatients() {
        return patients;
    }
    
    /**
     * Updates the patients' positions for animation.
     */
    public void updatePatients() {
        for (Person patient : patients) {
            // Implement patient animation logic here
        }
    }
    
    /**
     * Creates a new falling patient with a random path.
     *
     * @param pathType The type of path (1, 2, or 3)
     * @return A new falling patient
     */
    public Person createFallingPatient(int pathType) {
        Person patient = new Person(Constants.X_START, 300, true);
        return patient;
    }
    
    /**
     * Calculates the y-coordinate for a falling patient based on the path type and x-coordinate.
     *
     * @param pathType The type of path (1, 2, or 3)
     * @param x The x-coordinate
     * @param a0 The a0 coefficient
     * @param a1 The a1 coefficient
     * @param a2 The a2 coefficient
     * @param a3 The a3 coefficient
     * @param a4 The a4 coefficient
     * @return The y-coordinate
     */
    public int calculatePatientY(int pathType, int x, double a0, double a1, double a2, double a3, double a4) {
        double ydouble = a0 + (a1 * x) + (a2 * Math.pow(x, 2)) + (a3 * Math.pow(x, 3)) + (a4 * Math.pow(x, 4));
        return (int) ydouble;
    }
}