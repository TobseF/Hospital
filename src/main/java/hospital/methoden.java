
// ©2005 by Tobias Fritz

package hospital;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class methoden extends Frame implements KeyListener {

    //Variable Werte______________________________________
    int stelle = 300; //Position vom Paddel
    int y = 250;
    int xstart = 100;
    int x = xstart;
    int xcanvas = 111;//Für die Umrechunf von den X-Werten der Funktionen auf Canvas
    int ycanvas = 111; //Für die Umrechunf von den Y-Werten der Funktionen auf Canvas
    int paddlelaenge = 50;//Länge des Trampulins zwischen den Männchen
    //______________________________________________________

    //Spielablauf____________
    int geschwindigkeit = 10;
    int leben = 490;
    int punkte = 0;
    int auswahl = 1; //welche Flugbahn gewaehlt werden soll
    static boolean hilfe = true;
    static boolean pause = true;
    static boolean spielen = false;
    static boolean wirklichbeenden = false;
    static boolean spiel = true;
    static boolean gameover = false;

    //Farben________________
    static Color farbe;
    static int rot = 100;
    static int gelb = 25;
    static int blau = 0;
    //______________________

    //Schrift___________________________________________________
    static Font schrift = new Font("Serif", Font.PLAIN, 12);
    //__________________________________________________________

    //male_Krankenhaus____________________
    int ymaennchen = 20;
    int xmaennchen = 5;
    int posxrauch = 76;
    int posyrauch = 18;
    int zaehler_male_krankenhaus;
    //_____________________________________

    //Funktionen___________
    int start = 200; //Anfangs X-Wert für Fie Flugbahnen
    int stopp = 600; //Maximaler X-Wert für die Flugbahnen
    double a0 = 250;
    double a1 = -0.8;
    double a2 = 0;
    double a3 = 0;
    double a4 = 0;
    //_____________________

    //Animation______________
    int gameoverblinken = 0;
    static int zaehler = 0; //Männchen wackeln mit den Armen
    //________________________

    // User Interface________
    static Canvas Blatt;
    //_______________________

    public methoden() {
        //setLayout(new FlowLayout());
        Blatt = new Canvas();
        Blatt.setBounds(0, 0, 600, 400);
        Blatt.setBackground(Color.black);
        Blatt.addKeyListener(this);
        Blatt.requestFocus(); //sollte mein start das Canvas markieren
        Blatt.getFocusListeners();
        Blatt.isEnabled();
        Blatt.isFocusable();
        add(Blatt);
        farbe = new Color(rot, gelb, blau);
    }

    static public void male_Form(int posx, int posy, int posx2, int posy2, int rot_intern, int gelb_intern, int blau_intern, int form) {
        Graphics g = Blatt.getGraphics();
        rot = rot_intern;
        gelb = gelb_intern;
        blau = blau_intern;
        farbe = new Color(rot, gelb, blau);
        g.setColor(farbe);
        if (form == 0) { //Rechteck voll
            g.fillRect(posx, posy, posx2, posy2);
        }
        if (form == 1) { //Rechteck Rahmen
            g.drawRect(posx, posy, posx2, posy2);
        }
        if (form == 3) { //Oval voll
            g.fillOval(posx, posy, posx2, posy2);
        }
        if (form == 4) { //Oval Rahmen
            g.drawOval(posx, posy, posx2, posy2);
        }
        g.dispose();
    }

    static public void male_Strich(int Startpunkt_x, int Startpunkt_y, int Endpunkt_x, int Endpunkt_y) {
        Graphics g = Blatt.getGraphics();
        g.setColor(Color.white);
        g.drawLine(Startpunkt_x, Startpunkt_y, Endpunkt_x, Endpunkt_y);
        g.dispose();
    }

    static public void male_Text(String text, int zahl, int posx, int posy, int rot_intern, int gelb_intern, int blau_intern, int schrifttyp, int schriftgroeße) {
        Graphics g = Blatt.getGraphics();
        rot = rot_intern;
        gelb = gelb_intern;
        blau = blau_intern;
        farbe = new Color(rot, gelb, blau);
        g.setColor(farbe);
        schrift = new Font("Arial", Font.PLAIN, schriftgroeße);
        schrift = new Font("Arial", Font.BOLD, schriftgroeße);
        schrift = new Font("Courier New", Font.PLAIN, schriftgroeße);
        schrift = new Font("Courier New", Font.BOLD, schriftgroeße);
        schrift = new Font("Wingdings", Font.PLAIN, schriftgroeße);
        schrift = new Font("Times New Roman", Font.PLAIN, schriftgroeße);
        if (schrifttyp == 0) {
            schrift = new Font("Arial", Font.PLAIN, schriftgroeße);
        }
        if (schrifttyp == 1) {
            schrift = new Font("Arial", Font.BOLD, schriftgroeße);
        }
        if (schrifttyp == 2) {
            schrift = new Font("Courier New", Font.PLAIN, schriftgroeße);
        }
        if (schrifttyp == 3) {
            schrift = new Font("Wingdings", Font.PLAIN, schriftgroeße);
        }
        if (schrifttyp == 4) {
            schrift = new Font("Times New Roman", Font.PLAIN, schriftgroeße);
        }
        g.setFont(schrift);
        if (zahl >= 1) {
            g.drawString(String.valueOf(zahl), posx, posy);
        } else {
            g.drawString(String.valueOf(text), posx, posy);
            g.dispose();
        }
    }

    public void fugbahn01() { //	Linearer Flug: a0:Anfangshöhe; a1:Winkel(-0,5 bis -1,1)
        a0 = 380 - ((int) (Math.random() * 220)); //Zufallswert zwischen 160 und 380
        System.out.println("a0 " + a0);
        a1 = -(1.1 - (int) (Math.random() * 0.6)); //Zufallswert zwischen -0,5 und -1,1
        System.out.println("a1 " + a1);
        a2 = 0;
        a3 = 0;
        a4 = 0;
    }

    public void fugbahn02() { //	Bogenflug: a0(50bis300) Anfangshöhe a1: weite und höhe ; a2(-0,08bis0,003): weite
        a0 = 300 - ((int) (Math.random() * 275)); //Zufallswert zwischen 25 und 300
        a1 = 1;
        a2 = -0.003;
        a3 = 0;
        a4 = -0;
    }

    public void fugbahn03() { //Bogenflug: a0(50bis300) Anfangshöhe a1: weite und höhe ; a2(-0,08bis0,003): weite
        a0 = 380 - ((int) (Math.random() * 240)); //Zufallswert zwischen 140 und 380
        a2 = -0.005 + ((int) (Math.random() * 0.003)); //Zufallswert zwischen -0,005 und -0,002
        a1 = 0;
        a3 = 0;
        a4 = 0;
    }

    static public void male_Maennchen(int posx, int posy) {
        Graphics g = Blatt.getGraphics();
        g.setColor(Color.white);
        zaehler = zaehler + 1; //damit die Männchen mit den Armen wedeln
        if (zaehler >= 0) {
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16); //rechter Arm unten
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); //linker Arm unten
            g.setColor(Color.black);
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);  //rechter Arm oben
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9); //linker Arm  oben
            g.setColor(Color.white);
        }
        if (zaehler >= 20) {
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9);  //rechter Arm oben
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9); //linker Arm  oben
            g.setColor(Color.black);
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16); //rechter Arm unten
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16); //linker Arm unten
            g.setColor(Color.white);
            if (zaehler >= 40) {
                zaehler = 0;
            }
        }

        g.drawOval(posx, posy, 7, 7);                   //Kopf
        g.drawOval(posx - 1, posy - 1, 8, 8);               //Kopf
        g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16);  //Hals
        g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24); //rechtes Bein
        g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24); //linkes Bein
        g.dispose();
    }

    public void male_Paddle(int stelle) { //Paddel malen
        Graphics g = Blatt.getGraphics();
        g.setColor(Color.white);
        int posx = stelle - 10;
        int posy = 380;
        int temp = 1;
        male_Form(stelle, 388, paddlelaenge, 5, 0, 0, 109, 0);//Trampolin
        while (temp >= 0) {
            g.drawOval(posx, posy, 7, 7);                   //Kopf
            g.drawOval(posx - 1, posy - 1, 8, 8);               //Kopf
            if (temp == 0) {
                g.drawLine(posx + 3, posy + 12, posx - 4, posy + 9);  //linker Arm oben
                g.drawLine(posx + 3, posy + 14, posx - 4, posy + 11);  //linker Arm oben
            } else {
                g.drawLine(posx + 3, posy + 12, posx + 10, posy + 9); //rechter Arm  oben
                g.drawLine(posx + 3, posy + 14, posx + 10, posy + 11); //rechter Arm  oben
            }
            g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16);  //Hals
            g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24); //rechtes Bein
            g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24); //linkes Bein
            temp = temp - 1;
            posx = stelle + paddlelaenge + 4;
        }
        g.dispose();
        Graphics g2 = Blatt.getGraphics();
        g2.clipRect(stelle, 388, paddlelaenge, 5);
        g2.setColor(Color.blue);
        g2.fillOval(stelle - 20, 385, 90, 10);
        g2.setColor(Color.black);
        g2.fillOval(stelle - 20, 360, 90, 31);
        g2.dispose();
    }

    public void loesche_Paddle() { //Paddle mit schwarz übermahlen
        Graphics g = Blatt.getGraphics();
        g.setColor(Color.black);
        g.fillRect(55, 376, 575, 30);
        g.dispose();
    }

    public void male_Krankenhaus() { //Krankenhaus
        Graphics g = Blatt.getGraphics();
        if (x <= 105) {
            male_Form(0, 15, 100, 405, 102, 102, 102, 0); //Haus
            male_Form(50, 364, 50, 12, 214, 237, 245, 0); //Hospital Tafel
            male_Form(50, 376, 50, 60, 0, 0, 0, 0); //Tür
            male_Form(5, 20, 16, 22, 0, 206, 255, 0); //Fenter
            male_Text("Hospital", 0, 52, 375, 255, 0, 0, 0, 12);
        }
        male_Form(74, 16, 14, 14, 167, 167, 167, 3); //Rauch
        //male_Rauch_________________________
        posxrauch = posxrauch + 2;
        posyrauch = posyrauch - 1;
        int laengerauch = ((int) (Math.random() * 16) + 4);
        int hoeherauch = ((int) (Math.random() * 16) + 4);
        int rauchfarbe = ((int) (Math.random() * 240) + 15);
        male_Form(posxrauch, posyrauch, laengerauch, hoeherauch, rauchfarbe, rauchfarbe, rauchfarbe, 3);
        if (posyrauch <= 0) {
            posxrauch = 52;
            posyrauch = 22;
        }
        //___________________________________
        int xf = 5;
        int yf = 20;
        xmaennchen = xmaennchen + 1;
        if (xmaennchen >= 80) {
            System.out.println("xmaennchen>=80");
            ymaennchen = ymaennchen + 40;
            xmaennchen = 0;
        }
        if (ymaennchen >= 320) {
            System.out.println("ymaennchen>=400");
            xmaennchen = 5;
            ymaennchen = 20;
        }
        g.dispose();
        for (int i = 7; i >= 0; i = i - 1) { //Male Fenster
            male_Flüchtling(xf, yf);
            for (int i2 = 1; i2 >= 0; i2 = i2 - 1) {
                xf = xf + 30;
                male_Flüchtling(xf, yf);
            }
            yf = yf + 40;
            xf = 5;
        }
        ;
        male_Form(posxrauch, posyrauch, laengerauch, hoeherauch, rauchfarbe, rauchfarbe, rauchfarbe, 3);
    }

    public void male_Flüchtling(int xf, int yf) {
        Graphics g2 = Blatt.getGraphics();
        male_Form(xf, yf, 16, 22, 0, 206, 255, 0);
        g2.clipRect(xf, yf, 16, 22); //Bereich in dem Der Schatten memalt wird wird gebildet
        //g2.fillOval(xmaennchen+2,ymaennchen+9,16,22); //Schatten in den Fenstern wird gamalt
        g2.drawOval(xmaennchen - 1, ymaennchen + 4, 8, 8);               //Kopf
        g2.drawLine(xmaennchen + 3, ymaennchen + 11, xmaennchen + 3, ymaennchen + 21);  //Hals
        g2.drawLine(xmaennchen + 3, ymaennchen + 21, xmaennchen + 8, ymaennchen + 29); //rechtes Bein
        g2.drawLine(xmaennchen + 3, ymaennchen + 21, xmaennchen - 2, ymaennchen + 29); //linkes Bein
        g2.drawLine(xmaennchen + 3, ymaennchen + 17, xmaennchen + 10, ymaennchen + 14); //rechter Arm  oben
        g2.drawLine(xmaennchen + 3, ymaennchen + 19, xmaennchen + 10, ymaennchen + 16); //rechter Arm  oben
        g2.dispose(); //Bereich wird gelöscht
    }

    public void male_Punkte() { //Punktetafel
        male_Form(540, 42, 60, 16, 174, 186, 204, 0);
        male_Text("", punkte, 545, 55, 255, 0, 0, 1, 14);
        male_Form(500, 5, 112, 62, 250, 250, 250, 1);
        male_Form(500, 5, 111, 61, 250, 250, 250, 1);
        male_Form(500, 5, 110, 60, 255, 255, 255, 1);
        male_Maennchen(525, 10);
        male_Maennchen(555, 10);
        male_Maennchen(585, 10);
        male_Text("L:", 0, 506, 28, 255, 255, 255, 1, 12);
        male_Text("P:", 0, 506, 53, 255, 255, 255, 1, 12);
        System.out.println("Punkte " + punkte);
    }

    static public void male_Hilfe() { //Hilfe Menü
        int posx = 220;
        int posy = 120;
        male_Form(posx - 4, posy - 14, 156, 158, 0, 0, 0, 0); //übermalt Hilfefeld schwarz
        if (hilfe == true) {
            male_Text("                    Hilfe", 0, posx, posy, 255, 255, 255, 1, 12);
            male_Text("   Aktion                       Taste", 0, posx, posy + 24, 255, 255, 255, 1, 12);
            male_Text("Spiel Starten                  S", 0, posx, posy + 40, 255, 255, 255, 1, 12);
            male_Text("Pause                               P", 0, posx, posy + 52, 255, 255, 255, 1, 12);
            male_Text("Spiel Neu Starten          N", 0, posx, posy + 63, 255, 255, 255, 1, 12);
            male_Text("Spiel Beenden               Esc", 0, posx, posy + 76, 255, 255, 255, 1, 12);
            male_Text("Tasten 1-3                     Netz", 0, posx, posy + 89, 255, 255, 255, 1, 12);
            male_Text("Hilfe                                   H", 0, posx, posy + 102, 255, 255, 255, 1, 12);
            male_Text("Ziel: Rette so viele Leben", 0, posx, posy + 125, 255, 255, 255, 1, 12);
            male_Text("         wie möglich.", 0, posx, posy + 138, 255, 255, 255, 1, 12);
            male_Form(posx - 4, posy - 14, 155, 156, 255, 255, 255, 1);
        }
    }

    public void gameOver() { //Game Over Text
        male_Text("Game Over", 0, 175, 200, 255, 0, 0, 1, 50);
        System.out.println("Game Over" + gameoverblinken);
        if (gameoverblinken == 0) {
            System.out.println("0");
            male_Text("Spiel Neu Starten          N", 0, 220, 350, 255, 0, 0, 1, 12);
            gameoverblinken = gameoverblinken + 1;
        } else {
            System.out.println("1");
            male_Text("Spiel Neu Starten          N", 0, 220, 350, 255, 255, 255, 1, 12);
            gameoverblinken = gameoverblinken - 1;
        }
        male_Text("Spiel Beenden               Esc", 0, 220, 360, 255, 255, 255, 1, 12);
    }

    public void keyPressed(KeyEvent v) {
        if (v.getKeyCode() == KeyEvent.VK_1) {
            paddlelaenge = 40;
        }
        if (v.getKeyCode() == KeyEvent.VK_2) {
            paddlelaenge = 50;
        }
        if (v.getKeyCode() == KeyEvent.VK_3) {
            paddlelaenge = 60;
        }
        if (v.getKeyCode() == KeyEvent.VK_S) {
            System.out.println("Button Start gedrückt");
            hilfe = false;
            male_Hilfe();
            pause = false;
            spielen = true; //test
            System.out.println(pause);
        }
        if (v.getKeyCode() == KeyEvent.VK_P) {
            if (pause == true) {
                pause = false;
                male_Text("Pause", 0, 260, 200, 0, 0, 0, 1, 25);//lösche Pause
            } else {
                pause = true;
                //spielen=false;
                male_Text("Pause", 0, 260, 200, 255, 255, 255, 1, 25);//male Pause
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_ESCAPE) { //BEENDEN
            pause = true;
            wirklichbeenden = true;
            gameover = false; //Damit GameOver Text nicht überschrieben wird
            male_Form(241, 162, 114, 50, 0, 0, 0, 0);
            male_Form(241, 162, 114, 50, 255, 255, 255, 1);
            male_Text("Wirklich beenden", 0, 250, 180, 255, 255, 255, 1, 12);
            male_Text("Ja", 0, 275, 198, 255, 255, 255, 1, 12);
            male_Text("Nein", 0, 295, 198, 255, 255, 255, 1, 12);
            male_Strich(274, 199, 279, 199);
            male_Strich(295, 199, 302, 199);
        }
        if (v.getKeyCode() == KeyEvent.VK_J) {
            if (wirklichbeenden == true) {
                System.exit(0);
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_N) { //NEUSTART
            if (wirklichbeenden == true) {
                male_Form(238, 158, 118, 56, 0, 0, 0, 0);
                pause = false;
                wirklichbeenden = false;
                if (gameover == false && leben >= 550) { //Damit GameOver Text nicht überschrieben wird
                    gameover = true;
                }
            } else { //ales wird auf Standartwerte zurückgesetzt
                pause = false;
                spielen = true;
                male_Form(0, 0, 650, 500, 0, 0, 0, 0);
                punkte = 0;
                leben = 490;
                geschwindigkeit = 10;
                gameover = false;
                auswahl = 1;
                x = xstart;
                y = 300;
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_H) { //HILFE
            if (hilfe == true) {
                pause = false;
                hilfe = false;
                male_Hilfe();
            } else {
                pause = true;
                hilfe = true;
                male_Hilfe();
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_LEFT) { //LINKS
            if (stelle <= start - 100 || pause == true || gameover == true) {    //wenn das Trampulin zu weit nach rechts kommt,
                //stelle=stelle;                //oder Pause gedrückt ist, bleibt es stehen
            } else {
                stelle = stelle - paddlelaenge;
                loesche_Paddle();
                male_Paddle(stelle);
            }
        }
        if (v.getKeyCode() == KeyEvent.VK_RIGHT) {  //RECHTS
            if (stelle >= stopp - 50 || pause == true || gameover == true) {      //wenn das Trampulin zu weit nach rechts kommt,
                //stelle=stelle;                  //oder Pause gedrückt ist, bleibt es stehen
            } else {
                stelle = stelle + paddlelaenge;
                loesche_Paddle();
                male_Paddle(stelle);
            }
        }
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void keyTyped(KeyEvent arg0) {
    }
}
