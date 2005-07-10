/*
 * ©2005 by Tobias Fritz
 */
package hospital;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class spiel extends methoden implements Runnable {
    static Thread t;

    spiel() {
        t = new Thread(this);
        initComponents();
    }

    //Exit Form______________________________________________________
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
        spiel Fun = new spiel();
        Fun.setLocation(150, 150);
        Fun.setResizable(false);
        Fun.setSize(625, 442);
        Fun.setVisible(true);
        Fun.setTitle("Hospital 2");
        Blatt.requestFocus(); //Canvas beim starten markieren
        male_Hilfe();
        t.start();
    }

    public void run() { //Spielablauf der alle Methoden aufruft
        System.out.println(auswahl);
        System.out.println("RUN");
        while (spiel == true) {
            while (spielen == true) {
                while (y >= 5) {
                    if (pause == false) {
                        male_Form(x - 5, ycanvas - 1, 14, 28, 0, 0, 0, 0);//Männchen wird gelöscht
                        male_Form(102, 360, 575, 30, 0, 0, 0, 0);//lösche Pixelfehler über den Männchen
                        double ydouble = (((a0) + ((a1) * x)) + ((a2) * Math.pow(x, 2)) + ((a3) * Math.pow(x, 3)) + ((a4) * Math.pow(x, 4))); //Polynomfunktion 4ten grades, Demo auf: http://www.vs-c.de/vsengine/vlu/vsc/de/ma/1/mc/ma_04/ma_04_02/ma_04_02_01.vlu/Page/vsc/de/ma/1/mc/ma_04/ma_04_02/ma_04_02_09.vscml.html
                        System.out.println("x " + x);
                        System.out.println("y " + y);
                        y = (int) ydouble;
                        xcanvas = x;        //Umrechung fürs Canvas
                        ycanvas = 380 - y;    //Umrechung fürs Canvas
                        male_Maennchen(x, ycanvas);
                        male_Paddle(stelle);
                        x = x + 1; //x-Wert um 1 erhöhen
                        try {
                            Thread.sleep(geschwindigkeit);
                        } catch (InterruptedException e) {
                            System.out.println("IE");
                            t.interrupt();
                        }
                        System.out.println("stelle" + stelle);
                        if ((y <= 20) && (y <= 18) && ((x >= (stelle)) && (x <= (stelle + paddlelaenge))) || (x >= stopp) || (x <= xstart)) { //Abprallbedingung
                            punkte = punkte + 1;
                            System.out.println("Punkt");
                            System.out.println("x links" + x);
                            System.out.println("x rechts" + stelle + paddlelaenge);
                            System.out.println("stelle" + stelle);
                            auswahl = (int) ((Math.random() * 3) + 1);
                            System.out.println("Auswahl " + auswahl);
                            if (auswahl == 1) {
                                fugbahn01();
                            }
                            if (auswahl == 2) {
                                fugbahn02();
                            }
                            if (auswahl == 3) {
                                fugbahn02();
                            }
                            x = xstart;
                            y = 300;
                        }
                        male_Krankenhaus();
                        male_Punkte();

                        //_____________________________________________Levels
                        if (((Math.IEEEremainder(punkte, 15)) == 0) && (punkte > 0)) {   //alle Punkte wird die Spielgeschwindigkeit erhöht und ein Zusatzpunkt verliehen
                            geschwindigkeit = geschwindigkeit - 1;
                            punkte = punkte + 1;
                            System.out.println("geschwindigkeit " + geschwindigkeit);
                        }

                        //_____________________________________________
                    }//Pause
                }//while y>=5
                if (leben <= 550) {
                    male_Form(x - 4, 400, 16, 9, 255, 45, 52, 3);//Blutfleck
                    loesche_Paddle();
                    leben = leben + 30;
                    System.out.println("überlebt" + leben);
                    male_Form(leben, 8, 18, 30, 255, 45, 52, 3);//Leben übermahlen
                    fugbahn02();
                    x = xstart;
                    y = 250;
                } else {
                    gameover = true;
                    spielen = false;
                    System.out.println("Ende Schleife");
                }
            }//spielen
            if (wirklichbeenden == false) { //Hilfe wird solager der Esc Button nicht gedrückt wird ständig gamelt um mein starten immer geladen zu werden
                male_Hilfe();
            }
            if (gameover == true) {
                gameOver();
                System.out.println("Game Over");
                //male_Text("spiel Neu Starten          N",0,220,350,255,255,255,1,12);
                //male_Text("spiel Beenden               Esc",0,220,360,255,255,255,1,12);
                //male_Text("Game Over",0,175,200,255,0,0,1,50);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("IE");
            }
            //t.interrupt();
            //t=null;
        }//spiel
    }//Run Methode
}//Ende Klasse spiel
