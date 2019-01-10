package frc.robot.util;

import java.util.ArrayList;
import java.util.Arrays;

public class MutableArray<T> {

    private ArrayList<T> variable;
    private boolean mMutable = true;

    private GZFlag mMutableFlag = null;

    public MutableArray(boolean useFlag, T... values) {
        ArrayList<T> list = new ArrayList<T>(Arrays.asList(values));
        this.variable = list;

        if (useFlag)
            this.mMutableFlag = new GZFlag();
    }

    /**
     * @returns if mutable could be set
     */
    public boolean setMutable(boolean mutable) {
        if (this.mMutableFlag == null) {
            this.mMutable = mutable;
            return true;
        } else if (mutable) {
            this.mMutableFlag.tripFlag();
            return true;
        }
        return false;
    }

    /**
     * @param value to set
     * @return if could set
     */
    public boolean set(ArrayList<T> value) {
        if (this.isMutable()) {
            this.variable = value;
            return true;
        }

        return false;
    }

    public boolean isMutable() {
        if (this.mMutableFlag == null) {
            return this.mMutable;
        } else {
            return this.mMutableFlag.isFlagTripped();
        }
    }

    public ArrayList<T> get() {
        return this.variable;
    }

}