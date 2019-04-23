package frc.robot.ConfigurableDrive;

//thx 254
public class LatchedBoolean {
    private boolean mLast = false;

    public boolean update(boolean newValue) {
        boolean ret = false;
        
        if (newValue && !mLast) {
            ret = true;
        }

        mLast = newValue;
       
        return ret;
    }
}
