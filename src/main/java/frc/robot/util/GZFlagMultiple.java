package frc.robot.util;

import java.util.ArrayList;

public class GZFlagMultiple {
    private ArrayList<GZFlag> mFlags = new ArrayList<>();

    public GZFlagMultiple(int numOfFlags) {
        for (int i = 0; i < numOfFlags; i++)
            mFlags.add(new GZFlag());
    }

    /**
     * Trip a specific flag (starting at 1, not 0!!!!!)
     */
    public void tripFlag(int flagToTrip) {
        if (isFlagInvalid(flagToTrip)){
            return;
        }

        boolean isFirstFlag = flagToTrip == 1;
        boolean prevFlagsTripped = true;
        // Remember, here we are using i as array index but flagToTrip starts at 1
        for (int i = 0; i < flagToTrip - 1; i++)
            prevFlagsTripped &= mFlags.get(i).isFlagTripped();

        if (prevFlagsTripped || isFirstFlag)
            mFlags.get(flagToTrip - 1).tripFlag();
    }

    public boolean isFlagTripped(int flag) {
        if (isFlagInvalid(flag))
            return false;

        return mFlags.get(flag - 1).isFlagTripped();
    }

    private boolean isFlagInvalid(int flag) {
        if (flag > mFlags.size() || flag < 1) {
            System.out.println("Invalid flag: " + flag);
            return true;
        }

        return false;
    }

    public boolean allFlagsTripped() {
        boolean allFlagsTripped = true;

        for (GZFlag f : mFlags)
            allFlagsTripped &= f.isFlagTripped();

        return allFlagsTripped;
    }
}