package frc.robot.util;

public class BooleanStateChange {
    private boolean mLast;

    public boolean update(boolean newValue) {
        boolean ret = mLast != newValue;
        mLast = newValue;
        return ret;
    }
}
