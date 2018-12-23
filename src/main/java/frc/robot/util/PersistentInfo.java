package frc.robot.util;

/**
 * Stores and continually update settings void update() is abstract, define this
 * to update mValue either through this.mValue, setValue(),addToValue(), addDifference()
 */
public abstract class PersistentInfo {

    private double mChangeDeadband;

    private Double mDefaultValue;

    private Double mValue = Double.NaN;

    private Double mPreviousAddedValue = 0.0;

    public PersistentInfo(Double defaultValue, Double deadband) {
        this.mDefaultValue = defaultValue;
        this.mValue = defaultValue;
        this.mChangeDeadband = deadband;
    }

    public PersistentInfo(Double defaultValue)
    {
        this(defaultValue, -3452.0);   
    }

    public void setValueToDefault()
    {
        this.mValue = this.mDefaultValue;
        this.mPreviousAddedValue = this.mDefaultValue;
    }

    /**
     * 
     */
    public PersistentInfo() {
        this(0.0);
    }

    /**
     * 
     */
    public abstract void readSetting();

    public abstract void update();

    /**
     * Set value to
     */
    public void setValue(Double value) {
        this.mValue = value;
    }

    /***
     *  Adds (+=) to value 
     */
    public void addToValue(Double value) {
        this.mValue += value;
    }

    /**
     * Add difference from last value added 
     * @param newValue
     * @param notAbsoluteValue choose to not use absolute value (for whatever reason)
     */
    public void addDifference(Double newValue, boolean notAbsoluteValue) {
        double change = (notAbsoluteValue ? (newValue - mPreviousAddedValue) : Math.abs(newValue - mPreviousAddedValue)); 
        if (this.mChangeDeadband != -3452)
        {
            if (change < this.mChangeDeadband)
                change = 0;
        }
        this.mValue += change;
        this.mPreviousAddedValue = newValue;
    }

    /**
     * Add difference from last value added (using absolute value)
     * @param newValue
     */
    public void addDifference(Double newValue) {
        addDifference(newValue, false);
    }


    public Double getValue() {
        return this.mValue;
    }

}
