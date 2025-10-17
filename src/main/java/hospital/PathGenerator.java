package hospital;

import java.util.concurrent.ThreadLocalRandom;

/**

Erzeugt die verschiedenen Flugbahnen als Polynome 4. Ordnung.
Entspricht funktional den Altmethoden path01, path02, path03.
*/
public final class PathGenerator {
private PathGenerator() {}

/**

Lineare Flugbahn mit zufälliger Starthöhe und Gefälle.
@return Polynom
*/
public static Polynomial path01() {
double a0 = 380 - ThreadLocalRandom.current().nextInt(160, 380 + 1);
double a1 = -ThreadLocalRandom.current().nextDouble(0.5, 1.1); // -0.5 bis -1.1
return new Polynomial(a0, a1, 0, 0, 0);
}
/**

Bogenflug (feste Parameter wie im Altcode).
@return Polynom
*/
public static Polynomial path02() {
double a0 = 300 - ThreadLocalRandom.current().nextInt(25, 300 + 1);
double a1 = 1;
double a2 = -0.003;
return new Polynomial(a0, a1, a2, 0, 0);
}
/**

Bogenflug mit zufälliger Krümmung.
@return Polynom
*/
public static Polynomial path03() {
double a0 = 380 - ThreadLocalRandom.current().nextInt(140, 380 + 1);
// -0.005 bis -0.002 (wie im Altcode in int/round, hier sauber als double)
double a2 = -ThreadLocalRandom.current().nextDouble(0.002, 0.005);
return new Polynomial(a0, 0, -a2, 0, 0);
}
}