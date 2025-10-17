package hospital;

/**
 * Unveränderliche Repräsentation eines Polynoms 4. Ordnung:
 * y = a0 + a1x + a2x^2 + a3x^3 + a4x^4
 */
public record Polynomial(double a0, double a1, double a2, double a3, double a4) {

/**
 Berechnet den Funktionswert für gegebenes x.
 @param x x-Wert
 @return y-Wert
 */
    public double evaluate ( double x){
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return a0 + a1 * x + a2 * x2 + a3 * x3 + a4 * x4;
    }
}