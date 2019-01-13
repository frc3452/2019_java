package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.util.GZUtil;

public class GZAnalogInput extends AnalogInput {
    private final double mLowVolt;
    private final double mHighVolt;

    public GZAnalogInput(int channel) {
        this(channel, -3452, -3452);
    }

    public GZAnalogInput(int channel, double lowVoltForTrip, double highVoltForTrip) {
        super(channel);
        this.mLowVolt = lowVoltForTrip;
        this.mHighVolt = highVoltForTrip;
    }

    public boolean isWithinRange() {
        if (this.mLowVolt == -3452 || this.mHighVolt == -3452)
            return false;

        return GZUtil.between(this.getVoltage(), this.mLowVolt, this.mHighVolt);
    }

}