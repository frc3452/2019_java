package frc.robot.util;

import java.text.DecimalFormat;

public class ArcadeSignal {
    protected double move, rotate, timeout;

    public ArcadeSignal() {
        this(0, 0);
    }

    public ArcadeSignal(double move, double rotate) {
        this(move, rotate,-1);
    }

    public ArcadeSignal(double move, double rotate, double timeout) {
        this.move = move;
        this.rotate = rotate;
        this.timeout = timeout;
    }

    public double getMove() {
        return this.move;
    }

    public double getRotate() {
        return this.rotate;
    }

    public double getTimeout() {
        return this.timeout;
    }

    DecimalFormat df = new DecimalFormat("#0.00");

    public String toString() {
        return "Movement: " + df.format(move) + "\tRotation: " + df.format(rotate);
    }

}