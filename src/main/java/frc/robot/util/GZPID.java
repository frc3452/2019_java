package frc.robot.util;

public class GZPID {

    public double P, I, D, F;
    public int parameterSlot, iZone;

    public GZPID(int slot, double p, double i, double d, double f, int iZone) {
        this.parameterSlot = slot;
        this.P = p;
        this.I = i;
        this.D = d;
        this.F = f;
        this.iZone = iZone;
    }

}