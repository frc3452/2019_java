package frc.robot.util;

public class Mutable<T> {

    private T variable;
    private boolean mMutable = true;

    private GZFlag mMutableFlag = null;

    public Mutable(T value, boolean useFlag) {
        this.variable = value;

        if (useFlag)
            this.mMutableFlag = new GZFlag();
    }

    /**
     * @returns if mutable could be set
     */
    public boolean setMutable(boolean mutable) {
        if (this.mMutableFlag == null){
            this.mMutable = mutable;
            return true;
        }
        else if (mutable){
            this.mMutableFlag.tripFlag();
            return true;
        }
        return false;
    }

    /**
     * @param value to set 
     * @return if could set
     */
    public boolean set(T value) {
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
            return this.mMutableFlag.get();
        }
    }

    public T get() {
        return this.variable;
    }

}