package frc.robot.util;

public class GZPID {

    public final double P, I, D, F;
    public final int parameterSlot, iZone;

    public GZPID() {
        this(0, 0, 0, 0, 0);
    }

    public static class GZPIDPair {
        public final GZPID pair1, pair2;

        public GZPIDPair(int parameterSlot, double p, double i, double d, double f, int iZone) {
            this(new GZPID(parameterSlot, p, i, d, f, iZone), new GZPID(parameterSlot, p, i, d, f, iZone));
        }

        public GZPIDPair(GZPID pair1, GZPID pair2) {
            this.pair1 = pair1;
            this.pair2 = pair2;
        }

        public GZPIDPair() {
            this.pair1 = new GZPID();
            this.pair2 = new GZPID();
        }

        public boolean equalTo(GZPIDPair other) {
            boolean ret = true;
            ret &= this.pair1.equals(other.pair1);
            ret &= this.pair2.equals(other.pair2);
            return ret;
        }
    }

    public GZPID(final int slot, final double p, final double i, final double d, final double f, final int iZone) {
        this.parameterSlot = slot;
        this.P = p;
        this.I = i;
        this.D = d;
        this.F = f;
        this.iZone = iZone;
    }

    public GZPID(final double p, final double i, final double d, final double f, final int iZone) {
        this(0, p, i, d, f, iZone);
    }

    public boolean equals(GZPID other) {
        boolean ret = true;

        ret &= this.P == other.P;
        ret &= this.I == other.I;
        ret &= this.D == other.D;
        ret &= this.F == other.F;
        ret &= this.iZone == other.iZone;
        ret &= this.parameterSlot == other.parameterSlot;

        return ret;
    }

}