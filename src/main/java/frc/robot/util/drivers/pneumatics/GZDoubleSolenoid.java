package frc.robot.util.drivers.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.drivers.IGZHardware;

public class GZDoubleSolenoid extends DoubleSolenoid implements IGZHardware {

    public static class DoubleSolenoidConstants {
        public final int module;
        public final int fwd_channel;
        public final int rev_channel;
        public final double extendTime;
        public final double retractTime;

        public DoubleSolenoidConstants(int module, int fwd_channel, int rev_channel,
                double extendTime, double retractTime) {
            this.fwd_channel = fwd_channel;
            this.rev_channel = rev_channel;
            this.module = module;

            this.extendTime = extendTime;
            this.retractTime = retractTime;
        }

        public DoubleSolenoidConstants(int fwd_channel, int rev_channel, double extendTime,
                double retractTime) {
            this(0, fwd_channel, rev_channel, extendTime, retractTime);
        }
    }

    private GZTimer mExtendedTimer = new GZTimer();
    private GZTimer mRetractedTimer = new GZTimer();

    private final GZSubsystem mSub;
    private final String mName;
    private final DoubleSolenoidConstants mConstants;
    private int mChangeCounts = 0;

    public GZDoubleSolenoid(DoubleSolenoidConstants constants, GZSubsystem subsystem, String name) {
        super(constants.module, constants.fwd_channel, constants.rev_channel);
        this.mConstants = constants;
        this.mSub = subsystem;
        this.mName = name;

        this.mExtendedTimer.start();
        this.mRetractedTimer.start();
        this.mSub.mDoubleSolenoids.add(this);
    }

    public int getChangeCounts() {
        return mChangeCounts;
    }

    // when safteydisabled don't allow change
    // when disabled disabled push to default position
    @Override
    public void set(Value value) {
        runSolenoid(value, false);
    }

    private void runSolenoid(Value value, boolean override) {
        if (this.mSub.isSafetyDisabled())
            return;

        if (value == super.get())
            return;

        super.set(value);
        mChangeCounts++;

        if (value == Value.kForward) {
            mExtendedTimer.startTimer();
        } else {
            mRetractedTimer.startTimer();
        }

    }

    public boolean isExtended() {
        if (this.mConstants.extendTime == -3452)
            return true;
        return super.get() == Value.kForward && mExtendedTimer.get() > this.mConstants.extendTime;
    }

    public boolean isRetracted() {
        if (this.mConstants.retractTime == -3452)
            return true;

        return super.get() == Value.kReverse && mRetractedTimer.get() > this.mConstants.retractTime;
    }

    public DoubleSolenoidConstants getConstants() {
        return this.mConstants;
    }

    public GZSubsystem getGZSubsystem() {
        return mSub;
    }

    public String getGZName() {
        return mName;
    }
}