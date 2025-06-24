/*
 * ©2005 by Tobias Fritz
 */
package hospital;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Game extends Methods implements Runnable {
    static Thread t;

    Game() {
        t = new Thread(this);
        initComponents();
    }

    // Exit Form______________________________________________________
    public void initComponents() {
        addWindowListener(new WindowAdapter() {
            private void exitForm(WindowEvent ev) {
                System.exit(0);
            }

            @Override
            public void windowClosing(WindowEvent ev) {
                exitForm(ev);
            }
        });
        pack();
    }
    //	_____________________________________________________________

    public static void main(String[] args) {
        Game Fun = new Game();
        Fun.setLocation(150, 150);
        Fun.setResizable(false);
        Fun.setSize(625, 446);
        Fun.setVisible(true);
        Fun.setTitle("Hospital 2");
        Sheet.requestFocus(); // Focus Canvas when starting
        draw_help();
        t.start();
    }

    public void run() { // Game flow that calls all methods
        print(selection);
        print("RUN");
        while (game == true) {
            while (playing == true) {
                while (y >= 5) {
                    if (pause == false) {
                        draw_shape(x - 7, ycanvas - 3, 16, 31, 0, 0, 0, 3);//Person is deleted
                        draw_shape(102, 360, 575, 30, 0, 0, 0, 0);//delete pixel errors above the people
                        double ydouble = (((a0) + ((a1) * x)) + ((a2) * Math.pow(x, 2)) + ((a3) * Math.pow(x, 3)) + ((a4) * Math.pow(x, 4))); //Polynomial function of 4th degree, Demo at: http://www.vs-c.de/vsengine/vlu/vsc/de/ma/1/mc/ma_04/ma_04_02/ma_04_02_01.vlu/Page/vsc/de/ma/1/mc/ma_04/ma_04_02/ma_04_02_09.vscml.html
                        print("x " + x);
                        print("y " + y);
                        y = (int) ydouble;
                        xcanvas = x;        // Conversion for Canvas
                        ycanvas = 380 - y;  // Conversion for Canvas
                        draw_person(x, ycanvas);
                        draw_paddle(position);
                        x = x + 1; // increase x-value by 1
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e) {
                            print("IE");
                            t.interrupt();
                        }
                        print("position" + position);
                        if ((y <= 20) && (y <= 18) && ((x >= (position)) && (x <= (position + paddle_length))) || (x >= stop) || (x <= xstart)) { //Bounce condition
                            points = points + 1;
                            print("Point");
                            print("x left" + x);
                            print("x right" + position + paddle_length);
                            print("position" + position);
                            selection = (int) ((Math.random() * 3) + 1);
                            print("Selection " + selection);
                            if (selection == 1) {
                                path01();
                            }
                            if (selection == 2) {
                                path02();
                            }
                            if (selection == 3) {
                                path03();
                            }
                            x = xstart;
                            y = 300;
                        }
                        draw_hospital();
                        draw_points();

                        //________________Levels
                        if (((Math.IEEEremainder(points, 15)) == 0) && (points > 0)) {   //every 15 points the game speed is increased and an additional point is awarded
                            speed = speed - 1;
                            points = points + 1;
                            print("speed " + speed);
                        }

                        //________________
                    }// Pause
                }// while y>=5
                if (lives <= 550) {
                    draw_shape(x - 4, 400, 16, 9, 255, 45, 52, 3);//Blood stain
                    clear_paddle();
                    lives = lives + 30;
                    print("survived" + lives);
                    draw_shape(lives, 8, 18, 30, 255, 45, 52, 3);//Paint over lives
                    path02();
                    x = xstart;
                    y = 250;
                } else {
                    gameover = true;
                    playing = false;
                    print("End Loop");
                }
            }//playing
            if (really_end == false) { // Help is constantly painted as long as the Esc button is not pressed to always load my start
                draw_help();
            }
            if (gameover == true) {
                gameOver();
                print("Game Over");
                // draw_text("Restart Game          N",0,220,350,255,255,255,1,12);
                // draw_text("End Game               Esc",0,220,360,255,255,255,1,12);
                // draw_text("Game Over",0,175,200,255,0,0,1,50);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                print("IE");
            }
            //t.interrupt();
            //t=null;
        }// game
    }// Run Method
}// End class game