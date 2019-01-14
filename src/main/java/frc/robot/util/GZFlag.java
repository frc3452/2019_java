package frc.robot.util;

public class GZFlag {

    private boolean mInitValue;
    private boolean mTripped = false;

    public GZFlag(boolean initialValue) {
        this.mInitValue = initialValue;
    }

    public GZFlag() {
    }

    public void update(boolean newVal) {
        if (newVal != mInitValue)
            mTripped = true;
    }

    public void tripFlag() {
        mTripped = true;
    }

    public void rst()
    {
        this.mTripped = false;
    }

    public boolean get() {
        return this.mTripped;
    }
}