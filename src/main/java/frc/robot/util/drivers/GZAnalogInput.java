package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZAnalogInput.AnalogInputConstants.AnalogMode;

public class GZAnalogInput extends AnalogInput implements IGZHardware {
    public static class AnalogInputConstants {

        public static enum AnalogMode {
            TRIP, RANGE, NONE;
        }

        public double mTripLowVoltage, mTripHighVoltage;
        public double mRangeLowVoltage, mRangeHighVoltage, mRangeLowValue, mRangeHighValue;
        public final AnalogMode mMode;

        public AnalogInputConstants() {
            mMode = AnalogMode.NONE;
        }

        public AnalogInputConstants(double lowVoltageTrip, double highVoltageTrip) {
            this.mTripLowVoltage = lowVoltageTrip;
            this.mTripHighVoltage = highVoltageTrip;
            mMode = AnalogMode.TRIP;
        }

        public AnalogInputConstants(double lowVoltage, double highVoltage, double lowValue, double highValue) {
            this.mRangeLowVoltage = lowVoltage;
            this.mRangeHighVoltage = highVoltage;

            this.mRangeLowValue = lowValue;
            this.mRangeHighValue = highValue;

            mMode = AnalogMode.RANGE;
        }

        public AnalogMode getMode() {
            return mMode;
        }
    }

    private final String mName;

    private final GZSubsystem mSub;

    private final int mChannel;

    private final AnalogInputConstants mTranslation;

    public GZAnalogInput(GZSubsystem sub, String name, int channel) {
        this(sub, name, channel, new AnalogInputConstants());
    }

    public GZAnalogInput(GZSubsystem sub, String name, int channel, AnalogInputConstants translation) {
        super(channel);
        this.mTranslation = translation;
        this.mChannel = channel;
        this.mName = name;
        this.mSub = sub;

        GZFiles.getInstance().mAllAnalogSensors.add(this);
    }

    public boolean get() {
        if (this.mTranslation.getMode() != AnalogMode.TRIP)
            return false;

        return GZUtil.between(this.getVoltage(), this.mTranslation.mTripLowVoltage, this.mTranslation.mTripHighVoltage);
    }

    public double getTranslatedValue() {
        if (this.mTranslation.getMode() != AnalogMode.RANGE)
            return -3452;

        return GZUtil.scaleBetween(this.getVoltage(), this.mTranslation.mRangeLowValue,
                this.mTranslation.mRangeHighValue, this.mTranslation.mRangeLowVoltage,
                this.mTranslation.mRangeHighVoltage);
    }

    @Override
    public String getGZName() {
        return mName;
    }

    public GZSubsystem getGZSubsystem() {
        return mSub;
    }

    public int getPort() {
        return mChannel;
    }

}