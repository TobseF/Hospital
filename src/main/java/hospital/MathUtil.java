package hospital;

/**

Kleine, wiederverwendbare mathematische Hilfsfunktionen.
*/
public final class MathUtil {
private MathUtil() {}

/**

Begrenzung eines Wertes auf ein Intervall [min, max].
@param value zu begrenzender Wert
@param min Untergrenze
@param max Obergrenze
@return begrenzter Wert
*/
public static int clamp(int value, int min, int max) {
return Math.max(min, Math.min(max, value));
}
}