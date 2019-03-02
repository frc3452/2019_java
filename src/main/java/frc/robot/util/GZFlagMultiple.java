package frc.robot.util;

import java.util.ArrayList;

public class GZFlagMultiple {
    private ArrayList<GZFlag> mFlags = new ArrayList<>();

    private int lastGet = 0;

    private final int numOfFlags;

    public GZFlagMultiple(int numOfFlags) {
        this.numOfFlags = numOfFlags;
        reset();
    }

    /**
     * Trip a specific flag (starting at 1, not 0!!!!!)
     */
    public void trip(int flagToTrip) {
        if (isFlagInvalid(flagToTrip)) {
            return;
        }

        boolean isFirstFlag = flagToTrip == 1;
        boolean prevFlagsTripped = true;
        // Remember, here we are using i as array index but flagToTrip starts at 1
        for (int i = 0; i < flagToTrip - 1; i++)
            prevFlagsTripped &= mFlags.get(i).get();

        if (prevFlagsTripped || isFirstFlag)
            mFlags.get(flagToTrip - 1).tripFlag();
    }

    public boolean getNext() {
        return get(lastGet + 1);
    }

    public void tripNext() {
        trip(lastGet);
    }

    public void tripLast() {
        trip(mFlags.size());
    }

    public boolean getLast() {
        return get(mFlags.size());
    }

    public void print() {
        String out = "";

        for (int i = 0; i < mFlags.size() - 1; i++)
            out += i + ": " + get(i) + "\t";

        System.out.println(out);
    }

    public boolean get(int flag) {
        if (isFlagInvalid(flag))
            return false;

        lastGet = flag;

        return mFlags.get(flag - 1).get();
    }

    private boolean isFlagInvalid(int flag) {
        if (flag > mFlags.size() || flag < 1) {
            System.out.println("Invalid flag: " + flag);
            return true;
        }

        return false;
    }

    public void reset() {
        mFlags.clear();
        for (int i = 0; i < numOfFlags; i++)
            mFlags.add(new GZFlag());
    }

    public boolean allFlagsTripped() {
        boolean allFlagsTripped = true;

        for (GZFlag f : mFlags)
            allFlagsTripped &= f.get();

        return allFlagsTripped;
    }
}