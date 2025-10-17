package hospital;

import java.awt.*;

/**
 * Abstrakte Basisklasse für alle Spielobjekte.
 * Definiert das grundlegende Verhalten von Objekten, die gerendert und aktualisiert werden können.
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    
    /**
     * Erstellt ein neues Spielobjekt an der angegebenen Position.
     * 
     * @param x X-Koordinate
     * @param y Y-Koordinate
     */
    protected GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Aktualisiert den Zustand des Objekts.
     */
    public abstract void update();
    
    /**
     * Rendert das Objekt auf dem Canvas.
     * 
     * @param renderer Der Renderer zum Zeichnen
     */
    public abstract void render(Renderer renderer);
    
    // Getter und Setter
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}