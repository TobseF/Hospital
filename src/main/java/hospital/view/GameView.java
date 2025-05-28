package hospital.view;

import hospital.model.GameState;
import hospital.model.Hospital;
import hospital.model.Paddle;
import hospital.model.Person;
import hospital.util.Constants;

import java.awt.*;

/**
 * Handles rendering of the game elements.
 */
public class GameView {
    private Canvas canvas;
    private Font font;
    
    /**
     * Creates a new game view with the specified canvas.
     *
     * @param canvas The canvas to draw on
     */
    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.font = new Font("Serif", Font.PLAIN, 12);
    }
    
    /**
     * Draws a shape on the canvas.
     *
     * @param posX The x-coordinate of the shape
     * @param posY The y-coordinate of the shape
     * @param width The width of the shape
     * @param height The height of the shape
     * @param red The red component of the color
     * @param green The green component of the color
     * @param blue The blue component of the color
     * @param formType The type of shape to draw
     */
    public void drawShape(int posX, int posY, int width, int height, int red, int green, int blue, int formType) {
        Graphics g = canvas.getGraphics();
        Color color = new Color(red, green, blue);
        g.setColor(color);
        
        switch (formType) {
            case Constants.FORM_FILLED_RECTANGLE:
                g.fillRect(posX, posY, width, height);
                break;
            case Constants.FORM_RECTANGLE_FRAME:
                g.drawRect(posX, posY, width, height);
                break;
            case Constants.FORM_FILLED_OVAL:
                g.fillOval(posX, posY, width, height);
                break;
            case Constants.FORM_OVAL_FRAME:
                g.drawOval(posX, posY, width, height);
                break;
        }
        
        g.dispose();
    }
    
    /**
     * Draws a line on the canvas.
     *
     * @param startX The x-coordinate of the start point
     * @param startY The y-coordinate of the start point
     * @param endX The x-coordinate of the end point
     * @param endY The y-coordinate of the end point
     */
    public void drawLine(int startX, int startY, int endX, int endY) {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.white);
        g.drawLine(startX, startY, endX, endY);
        g.dispose();
    }
    
    /**
     * Draws text on the canvas.
     *
     * @param text The text to draw
     * @param number The number to draw (if text is empty)
     * @param posX The x-coordinate of the text
     * @param posY The y-coordinate of the text
     * @param red The red component of the color
     * @param green The green component of the color
     * @param blue The blue component of the color
     * @param fontType The type of font to use
     * @param fontSize The size of the font
     */
    public void drawText(String text, int number, int posX, int posY, int red, int green, int blue, int fontType, int fontSize) {
        Graphics g = canvas.getGraphics();
        Color color = new Color(red, green, blue);
        g.setColor(color);
        
        switch (fontType) {
            case Constants.FONT_ARIAL_PLAIN:
                font = new Font("Arial", Font.PLAIN, fontSize);
                break;
            case Constants.FONT_ARIAL_BOLD:
                font = new Font("Arial", Font.BOLD, fontSize);
                break;
            case Constants.FONT_COURIER_PLAIN:
                font = new Font("Courier New", Font.PLAIN, fontSize);
                break;
            case Constants.FONT_WINGDINGS_PLAIN:
                font = new Font("Wingdings", Font.PLAIN, fontSize);
                break;
            case Constants.FONT_TIMES_PLAIN:
                font = new Font("Times New Roman", Font.PLAIN, fontSize);
                break;
        }
        
        g.setFont(font);
        
        if (number >= 1) {
            g.drawString(String.valueOf(number), posX, posY);
        } else {
            g.drawString(text, posX, posY);
        }
        
        g.dispose();
    }
    
    /**
     * Draws a person on the canvas.
     *
     * @param person The person to draw
     * @param animationCounter The animation counter for arm waving
     */
    public void drawPerson(Person person, int animationCounter) {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.white);
        
        int posX = person.getX();
        int posY = person.getY();
        
        // Draw arms based on animation counter
        if (animationCounter >= 0 && animationCounter < Constants.ANIMATION_COUNTER_THRESHOLD) {
            g.drawLine(posX + 2, posY + 13, posX - 4, posY + 16);  // right arm down
            g.drawLine(posX + 2, posY + 13, posX + 10, posY + 16); // left arm down
            g.setColor(Color.black);
            g.drawLine(posX + 3, posY + 13, posX - 4, posY + 9);   // right arm up
            g.drawLine(posX + 3, posY + 13, posX + 10, posY + 9);  // left arm up
            g.setColor(Color.white);
        } else {
            g.drawLine(posX + 3, posY + 13, posX - 4, posY + 9);   // right arm up
            g.drawLine(posX + 3, posY + 13, posX + 10, posY + 9);  // left arm up
            g.setColor(Color.black);
            g.drawLine(posX + 2, posY + 13, posX - 4, posY + 16);  // right arm down
            g.drawLine(posX + 2, posY + 13, posX + 10, posY + 16); // left arm down
            g.setColor(Color.white);
        }
        
        // Draw head and body
        g.drawOval(posX, posY, Constants.PERSON_HEAD_SIZE, Constants.PERSON_HEAD_SIZE);                          // Head
        g.drawOval(posX - Constants.PERSON_HEAD_OFFSET, posY - Constants.PERSON_HEAD_OFFSET, 
                  Constants.PERSON_HEAD_SIZE + 2, Constants.PERSON_HEAD_SIZE + 2);             // Head outline
        g.drawLine(posX + 3, posY + 6, posX + 3, posY + 16);  // Neck
        g.drawLine(posX + 3, posY + 16, posX + 8, posY + 24); // right leg
        g.drawLine(posX + 3, posY + 16, posX - 2, posY + 24); // left leg
        
        g.dispose();
    }
    
    /**
     * Draws the paddle on the canvas.
     *
     * @param paddle The paddle to draw
     */
    public void drawPaddle(Paddle paddle) {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.white);
        
        int position = paddle.getPosition();
        int length = paddle.getLength();
        Person leftRescuer = paddle.getLeftRescuer();
        Person rightRescuer = paddle.getRightRescuer();
        
        // Draw trampoline
        drawShape(position, 388, length, 5, 0, 0, 109, Constants.FORM_FILLED_RECTANGLE);
        
        // Draw left rescuer
        int posX = leftRescuer.getX();
        int posY = leftRescuer.getY();
        
        g.drawOval(posX, posY, Constants.PERSON_HEAD_SIZE, Constants.PERSON_HEAD_SIZE);                 // Head
        g.drawOval(posX - Constants.PERSON_HEAD_OFFSET, posY - Constants.PERSON_HEAD_OFFSET, 
                  Constants.PERSON_HEAD_SIZE + 2, Constants.PERSON_HEAD_SIZE + 2);    // Head outline
        g.drawLine(posX + 3, posY + 12, posX - 4, posY + 9);   // left arm up
        g.drawLine(posX + 3, posY + 14, posX - 4, posY + 11);  // left arm up
        g.drawLine(posX + 3, posY + 6, posX + 3, posY + 16);   // Neck
        g.drawLine(posX + 3, posY + 16, posX + 8, posY + 24);  // right leg
        g.drawLine(posX + 3, posY + 16, posX - 2, posY + 24);  // left leg
        
        // Draw right rescuer
        posX = rightRescuer.getX();
        posY = rightRescuer.getY();
        
        g.drawOval(posX, posY, Constants.PERSON_HEAD_SIZE, Constants.PERSON_HEAD_SIZE);                 // Head
        g.drawOval(posX - Constants.PERSON_HEAD_OFFSET, posY - Constants.PERSON_HEAD_OFFSET, 
                  Constants.PERSON_HEAD_SIZE + 2, Constants.PERSON_HEAD_SIZE + 2);    // Head outline
        g.drawLine(posX + 3, posY + 12, posX + 10, posY + 9);  // right arm up
        g.drawLine(posX + 3, posY + 14, posX + 10, posY + 11); // right arm up
        g.drawLine(posX + 3, posY + 6, posX + 3, posY + 16);   // Neck
        g.drawLine(posX + 3, posY + 16, posX + 8, posY + 24);  // right leg
        g.drawLine(posX + 3, posY + 16, posX - 2, posY + 24);  // left leg
        
        g.dispose();
        
        // Draw trampoline details
        Graphics g2 = canvas.getGraphics();
        g2.clipRect(position, 388, length, 5);
        g2.setColor(Color.blue);
        g2.fillOval(position - 20, 385, 90, 10);
        g2.setColor(Color.black);
        g2.fillOval(position - 20, 360, 90, 31);
        g2.dispose();
    }
    
    /**
     * Clears the paddle from the canvas.
     */
    public void clearPaddle() {
        drawShape(55, 376, 575, 30, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
    }
    
    /**
     * Draws the hospital on the canvas.
     *
     * @param hospital The hospital to draw
     * @param patientX The x-coordinate of the current patient
     */
    public void drawHospital(Hospital hospital, int patientX) {
        if (patientX <= 105) {
            // Draw hospital building
            drawShape(0, 15, hospital.getWidth(), hospital.getHeight(), 102, 102, 102, Constants.FORM_FILLED_RECTANGLE);
            // Draw hospital sign
            drawShape(50, 364, hospital.getSignWidth(), hospital.getSignHeight(), 214, 237, 245, Constants.FORM_FILLED_RECTANGLE);
            // Draw door
            drawShape(50, 376, 50, 60, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
            // Draw window
            drawShape(5, 20, 16, 22, 0, 206, 255, Constants.FORM_FILLED_RECTANGLE);
            // Draw hospital text
            drawText("Hospital", 0, 52, 375, 255, 0, 0, Constants.FONT_ARIAL_PLAIN, 12);
        }
        
        // Draw smoke
        drawShape(74, 16, 14, 14, 167, 167, 167, Constants.FORM_FILLED_OVAL);
        
        // Draw smoke animation
        int smokeX = hospital.getSmokeX();
        int smokeY = hospital.getSmokeY();
        int smokeLength = hospital.getRandomSmokeLength();
        int smokeHeight = hospital.getRandomSmokeHeight();
        int smokeColor = hospital.getRandomSmokeColor();
        
        drawShape(smokeX, smokeY, smokeLength, smokeHeight, smokeColor, smokeColor, smokeColor, Constants.FORM_FILLED_OVAL);
        
        // Draw patients in windows
        int xf = 5;
        int yf = 20;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                drawPatientInWindow(xf + j * 30, yf, hospital);
            }
            yf += 40;
        }
        
        // Draw final smoke
        drawShape(smokeX, smokeY, smokeLength, smokeHeight, smokeColor, smokeColor, smokeColor, Constants.FORM_FILLED_OVAL);
    }
    
    /**
     * Draws a patient in a hospital window.
     *
     * @param xf The x-coordinate of the window
     * @param yf The y-coordinate of the window
     * @param hospital The hospital
     */
    private void drawPatientInWindow(int xf, int yf, Hospital hospital) {
        // Draw window
        drawShape(xf, yf, 16, 22, 0, 206, 255, Constants.FORM_FILLED_RECTANGLE);
        
        // Draw patient in window
        Graphics g2 = canvas.getGraphics();
        g2.clipRect(xf, yf, 16, 22); // Area in which the shadow is painted is formed
        
        int x_person = hospital.getSmokeX() - 24; // Using smoke position as a proxy for patient animation
        int y_person = hospital.getSmokeY() + 2;
        
        g2.drawOval(x_person - 1, y_person + 4, 8, 8);                      // Head
        g2.drawLine(x_person + 3, y_person + 11, x_person + 3, y_person + 21);  // Neck
        g2.drawLine(x_person + 3, y_person + 21, x_person + 8, y_person + 29);  // Right leg
        g2.drawLine(x_person + 3, y_person + 21, x_person - 2, y_person + 29);  // Left leg
        g2.drawLine(x_person + 3, y_person + 17, x_person + 10, y_person + 14); // Right arm up
        g2.drawLine(x_person + 3, y_person + 19, x_person + 10, y_person + 16); // Right arm up
        
        g2.dispose();
    }
    
    /**
     * Draws the score board on the canvas.
     *
     * @param gameState The current game state
     */
    public void drawPoints(GameState gameState) {
        // Draw score background
        drawShape(540, 42, 60, 16, 174, 186, 204, Constants.FORM_FILLED_RECTANGLE);
        // Draw score
        drawText("", gameState.getPoints(), 545, 55, 255, 0, 0, Constants.FONT_ARIAL_BOLD, 14);
        // Draw score board frame
        drawShape(500, 5, 112, 62, 250, 250, 250, Constants.FORM_RECTANGLE_FRAME);
        drawShape(500, 5, 111, 61, 250, 250, 250, Constants.FORM_RECTANGLE_FRAME);
        drawShape(500, 5, 110, 60, 255, 255, 255, Constants.FORM_RECTANGLE_FRAME);
        // Draw people icons
        drawPerson(new Person(525, 10, true), 0);
        drawPerson(new Person(555, 10, true), 0);
        drawPerson(new Person(585, 10, true), 0);
        // Draw labels
        drawText("L:", 0, 506, 28, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
        drawText("P:", 0, 506, 53, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
    }
    
    /**
     * Draws the help screen on the canvas.
     *
     * @param isHelpShown Whether the help screen should be shown
     */
    public void drawHelp(boolean isHelpShown) {
        int posX = 220;
        int posY = 120;
        
        // Clear help area
        drawShape(posX - 5, posY - 15, 158, 158, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
        
        if (isHelpShown) {
            // Draw help text
            drawText("                  HELP", 0, posX, posY, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("    Action                     Key", 0, posX, posY + 24, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Start Game                   S", 0, posX, posY + 40, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Pause                            P", 0, posX, posY + 52, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Restart Game               N", 0, posX, posY + 63, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("End Game                  ESC", 0, posX, posY + 76, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Switch Difficultly        1-3", 0, posX, posY + 89, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Show Help                    H", 0, posX, posY + 102, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("Goal: Save as many lives", 0, posX, posY + 125, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            drawText("          as possible.", 0, posX, posY + 138, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
            // Draw help frame
            drawShape(posX - 4, posY - 14, 155, 156, 255, 255, 255, Constants.FORM_RECTANGLE_FRAME);
        }
    }
    
    /**
     * Draws the game over screen on the canvas.
     *
     * @param gameState The current game state
     */
    public void drawGameOver(GameState gameState) {
        // Draw game over text
        drawText("Game Over", 0, 175, 200, 255, 0, 0, Constants.FONT_ARIAL_BOLD, 50);
        
        // Draw restart game text with blinking effect
        if (gameState.getGameOverBlinkState() == 0) {
            drawText("Restart Game          N", 0, 220, 350, 255, 0, 0, Constants.FONT_ARIAL_BOLD, 12);
        } else {
            drawText("Restart Game          N", 0, 220, 350, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
        }
        
        // Draw end game text
        drawText("End Game               Esc", 0, 220, 360, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
    }
    
    /**
     * Draws the "really end" dialog on the canvas.
     */
    public void drawReallyEndDialog() {
        // Clear area
        drawShape(241, 162, 114, 50, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
        // Draw frame
        drawShape(241, 162, 114, 50, 255, 255, 255, Constants.FORM_RECTANGLE_FRAME);
        // Draw text
        drawText("Really Quit", 0, 265, 180, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
        drawText("Yes", 0, 270, 198, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
        drawText("No", 0, 310, 198, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 12);
        // Draw underlines
        drawLine(270, 200, 279, 200);
        drawLine(310, 200, 318, 200);
    }
    
    /**
     * Draws a blood stain on the canvas.
     *
     * @param x The x-coordinate of the blood stain
     * @param y The y-coordinate of the blood stain
     */
    public void drawBloodStain(int x, int y) {
        drawShape(x - 4, 400, 16, 9, 255, 45, 52, Constants.FORM_FILLED_OVAL);
    }
    
    /**
     * Draws a lives indicator on the canvas.
     *
     * @param lives The current lives
     */
    public void drawLives(int lives) {
        drawShape(lives, 8, 18, 30, 255, 45, 52, Constants.FORM_FILLED_OVAL);
    }
    
    /**
     * Clears the entire canvas.
     */
    public void clearCanvas() {
        drawShape(0, 0, 650, 500, 0, 0, 0, Constants.FORM_FILLED_RECTANGLE);
    }
    
    /**
     * Draws the pause text on the canvas.
     */
    public void drawPauseText() {
        drawText("Pause", 0, 260, 200, 255, 255, 255, Constants.FONT_ARIAL_BOLD, 25);
    }
    
    /**
     * Clears the pause text from the canvas.
     */
    public void clearPauseText() {
        drawText("Pause", 0, 260, 200, 0, 0, 0, Constants.FONT_ARIAL_BOLD, 25);
    }
}