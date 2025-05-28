package hospital.model;

import hospital.util.Constants;

/**
 * Represents the rescue mesh (paddle) that catches falling patients.
 */
public class Paddle {
    private int position;
    private int length;
    private Person leftRescuer;
    private Person rightRescuer;
    
    /**
     * Creates a new paddle with the specified position and length.
     *
     * @param position The x-coordinate of the left edge of the paddle
     * @param length The length of the paddle
     */
    public Paddle(int position, int length) {
        this.position = position;
        this.length = length;
        this.leftRescuer = new Person(position - 10, 380, false);
        this.rightRescuer = new Person(position + length + 4, 380, false);
    }
    
    /**
     * Gets the position of the paddle.
     *
     * @return The x-coordinate of the left edge of the paddle
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Sets the position of the paddle and updates the rescuers' positions.
     *
     * @param position The new x-coordinate of the left edge of the paddle
     */
    public void setPosition(int position) {
        this.position = position;
        updateRescuersPositions();
    }
    
    /**
     * Gets the length of the paddle.
     *
     * @return The length of the paddle
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Sets the length of the paddle.
     *
     * @param length The new length of the paddle
     */
    public void setLength(int length) {
        this.length = length;
        updateRescuersPositions();
    }
    
    /**
     * Gets the left rescuer.
     *
     * @return The left rescuer
     */
    public Person getLeftRescuer() {
        return leftRescuer;
    }
    
    /**
     * Gets the right rescuer.
     *
     * @return The right rescuer
     */
    public Person getRightRescuer() {
        return rightRescuer;
    }
    
    /**
     * Moves the paddle to the left by its length.
     *
     * @return true if the paddle was moved, false if it reached the left boundary
     */
    public boolean moveLeft() {
        if (position <= Constants.PATH_START - 100) {
            return false;
        }
        position -= length;
        updateRescuersPositions();
        return true;
    }
    
    /**
     * Moves the paddle to the right by its length.
     *
     * @return true if the paddle was moved, false if it reached the right boundary
     */
    public boolean moveRight() {
        if (position >= Constants.PATH_STOP - 50) {
            return false;
        }
        position += length;
        updateRescuersPositions();
        return true;
    }
    
    /**
     * Checks if a falling patient is caught by the paddle.
     *
     * @param patient The falling patient
     * @param minY The minimum y-coordinate for a catch
     * @return true if the patient is caught, false otherwise
     */
    public boolean catchesPatient(Person patient, int minY) {
        int patientX = patient.getX();
        int patientY = patient.getY();
        return (patientY <= minY) && (patientX >= position) && (patientX <= position + length);
    }
    
    /**
     * Updates the positions of the rescuers based on the paddle position.
     */
    private void updateRescuersPositions() {
        leftRescuer.setX(position - 10);
        rightRescuer.setX(position + length + 4);
    }
}