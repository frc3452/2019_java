package frc.robot.util;

import java.text.DecimalFormat;

public class ArcadeSignal {
    public double move, rotate;

    public ArcadeSignal() {
        this(0, 0);
    }

    public ArcadeSignal(double move, double rotate) {
        this.move = move;
        this.rotate = rotate;
    }

    public double getMove()
    {
        return this.move;
    }

    public double getRotate()
    {
        return this.rotate;
    }

    DecimalFormat df = new DecimalFormat("#0.00");

    public String toString() {
        return "Movement: " + df.format(move) + "\tRotation: " + df.format(rotate);
    }

}