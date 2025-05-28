package hospital.model;

import hospital.util.Constants;

/**
 * Represents a person in the game, which can be either a patient or a rescuer.
 */
public class Person {
    private int x;
    private int y;
    private boolean isPatient;
    private boolean isWavingArms;
    
    /**
     * Creates a new person.
     *
     * @param x The x-coordinate of the person
     * @param y The y-coordinate of the person
     * @param isPatient Whether this person is a patient (true) or a rescuer (false)
     */
    public Person(int x, int y, boolean isPatient) {
        this.x = x;
        this.y = y;
        this.isPatient = isPatient;
        this.isWavingArms = false;
    }
    
    /**
     * Gets the x-coordinate of the person.
     *
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Sets the x-coordinate of the person.
     *
     * @param x The new x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Gets the y-coordinate of the person.
     *
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the y-coordinate of the person.
     *
     * @param y The new y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Checks if this person is a patient.
     *
     * @return true if this person is a patient, false if it's a rescuer
     */
    public boolean isPatient() {
        return isPatient;
    }
    
    /**
     * Sets whether this person is waving arms.
     *
     * @param isWavingArms true if the person is waving arms, false otherwise
     */
    public void setWavingArms(boolean isWavingArms) {
        this.isWavingArms = isWavingArms;
    }
    
    /**
     * Checks if this person is waving arms.
     *
     * @return true if the person is waving arms, false otherwise
     */
    public boolean isWavingArms() {
        return isWavingArms;
    }
    
    /**
     * Moves the person by the specified amount.
     *
     * @param dx The amount to move in the x direction
     * @param dy The amount to move in the y direction
     */
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    /**
     * Gets the head size of the person.
     *
     * @return The head size
     */
    public int getHeadSize() {
        return Constants.PERSON_HEAD_SIZE;
    }
    
    /**
     * Gets the head offset of the person.
     *
     * @return The head offset
     */
    public int getHeadOffset() {
        return Constants.PERSON_HEAD_OFFSET;
    }
}