package frc.robot.util.drivers.pneumatics;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.drivers.IGZHardware;

public class GZSolenoid extends Solenoid implements IGZHardware {

    public enum SolenoidState {
        TRANSITION, EXTENDED, RETRACTED;
    }

    public static class SolenoidConstants {
        public final int module;
        public final int channel;
        public final double extendTime;
        public final double retractTime;

        public SolenoidConstants(int module, int channel, double extendTime, double retractTime) {
            this.module = module;
            this.channel = channel;
            this.extendTime = extendTime;
            this.retractTime = retractTime;
        }

        public SolenoidConstants(int channel, double extendTime, double retractTime) {
            this(0, channel, extendTime, retractTime);
        }
    }

    private final GZSubsystem mSub;
    private final String mName;
    private final SolenoidConstants mConstants;

    private GZTimer mExtendedTimer = new GZTimer();
    private GZTimer mRetractedTimer = new GZTimer();

    private int mChangeCounts = 0;

    public SolenoidConstants getConstants() {
        return this.mConstants;
    }

    public GZSolenoid(SolenoidConstants constants, GZSubsystem subsystem, String name) {
        super(constants.module, constants.channel);

        this.mConstants = constants;
        this.mSub = subsystem;
        this.mName = name;
        this.mExtendedTimer.start();
        this.mRetractedTimer.start();
        this.mSub.mSingleSolenoids.add(this);
    }

    public boolean isLocked() {
        return this.mSub.areSolenoidsLocked();
    }

    public int getChangeCounts() {
        return mChangeCounts;
    }

    @Override
    public void set(boolean on) {
        if (on == super.get() || this.isLocked())
            return;

        super.set(on);
        mChangeCounts++;

        if (on) {
            mExtendedTimer.startTimer();
        } else {
            mRetractedTimer.startTimer();
        }
    }

    public SolenoidState getSolenoidState() {
        if (this.mConstants.extendTime == -3452 && super.get())
            return SolenoidState.EXTENDED;
        else if (this.mConstants.retractTime == -3452 && !super.get())
            return SolenoidState.RETRACTED;

        if (super.get() && mExtendedTimer.get() > this.mConstants.extendTime)
            return SolenoidState.EXTENDED;
        else if (!super.get() && mRetractedTimer.get() > this.mConstants.retractTime)
            return SolenoidState.RETRACTED;
        
        return SolenoidState.TRANSITION;
    }

    public GZSubsystem getGZSubsystem() {
        return mSub;
    }

    public String getGZName() {
        return mName;
    }
}