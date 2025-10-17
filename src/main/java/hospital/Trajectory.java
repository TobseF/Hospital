package hospital;

/**
 * Represents a 4th-degree polynomial trajectory y = a0 + a1*x + a2*x^2 + a3*x^3 + a4*x^4
 * used to move the falling patient across the screen.
 */
public final class Trajectory {
    private final double a0, a1, a2, a3, a4;

    public Trajectory(double a0, double a1, double a2, double a3, double a4) {
        this.a0 = a0;
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.a4 = a4;
    }

    /**
     * Computes y for the given x.
     */
    public int yAt(int x) {
        double y = a0 + (a1 * x) + (a2 * Math.pow(x, 2)) + (a3 * Math.pow(x, 3)) + (a4 * Math.pow(x, 4));
        return (int) y;
    }

    /** Factory for linear path. */
    public static Trajectory randomLinear() {
        double a0 = 380 - (int) (Math.random() * 220); // 160..380
        double a1 = -(1.1 - (int) (Math.random() * 0.6)); // about -0.5..-1.1
        return new Trajectory(a0, a1, 0, 0, 0);
    }

    /** Factory for arc path 1. */
    public static Trajectory arc1() {
        double a0 = 300 - (int) (Math.random() * 275); // 25..300
        return new Trajectory(a0, 1, -0.003, 0, 0);
    }

    /** Factory for arc path 2. */
    public static Trajectory randomArc2() {
        double a0 = 380 - (int) (Math.random() * 240); // 140..380
        double a2 = -0.005 + (int) (Math.random() * 0.003); // approx -0.005..-0.002
        return new Trajectory(a0, 0, a2, 0, 0);
    }
}
