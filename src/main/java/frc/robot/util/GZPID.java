package frc.robot.util;

public class GZPID {

    public final double P, I, D, F;
    public final int parameterSlot, iZone;

    public GZPID(final int slot, final double p, final double i, final double d, final double f, final int iZone) {
        this.parameterSlot = slot;
        this.P = p;
        this.I = i;
        this.D = d;
        this.F = f;
        this.iZone = iZone;
    }

    public GZPID(final double p,final double i,final double d,final double f,final int iZone) {
        this(0, p, i, d, f, iZone);
    }

}