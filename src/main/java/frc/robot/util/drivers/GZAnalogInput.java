package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;

public class GZAnalogInput extends AnalogInput implements IGZHardware {
    private final String mName;

    private final GZSubsystem mSub;

    private final double mLowVolt;
    private final double mHighVolt;
    private final int mChannel;

    public GZAnalogInput(GZSubsystem sub, String name, int channel) {
        this(sub, name, channel, -3452, -3452);
    }

    public GZAnalogInput(GZSubsystem sub, String name, int channel, double lowVoltForTrip, double highVoltForTrip) {
        super(channel);
        this.mLowVolt = lowVoltForTrip;
        this.mHighVolt = highVoltForTrip;
        mChannel = channel;
        mName = name;
        this.mSub = sub;

        GZFiles.getInstance().mAllAnalogSensors.add(this);
    }

    public boolean isWithinRange() {
        if (this.mLowVolt == -3452 || this.mHighVolt == -3452)
            return false;

        return GZUtil.between(this.getVoltage(), this.mLowVolt, this.mHighVolt);
    }

    @Override
    public String getGZName() {
        return mName;
    }

    public GZSubsystem getGZSubsystem() {
        return mSub;
    }

    public int getPort()
    {
        return mChannel;
    }

}