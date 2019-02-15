package frc.robot.util.drivers.pneumatics;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.drivers.IGZHardware;

public class GZSolenoid extends Solenoid implements IGZHardware {

    public enum SolenoidState {
        TRANSITION, ON, OFF;
    }

    public boolean isOn()
    {
        return this.getSolenoidState() == SolenoidState.ON;
    }

    public boolean isOff()
    {
        return this.getSolenoidState() == SolenoidState.OFF;
    }

    public boolean isTransitioning()
    {
        return this.getSolenoidState() == SolenoidState.TRANSITION;
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

        /**
         * By default on PCM 1!
         */
        public SolenoidConstants(int channel, double extendTime, double retractTime) {
            this(1, channel, extendTime, retractTime);
        }
    }

    private final GZSubsystem mSub;
    private final String mName;
    private final SolenoidConstants mConstants;

    private GZTimer mOnTimer = new GZTimer();
    private GZTimer mOffTimer = new GZTimer();

    private int mChangeCounts = 0;
    private boolean mForcedOff = false;

    public SolenoidConstants getConstants() {
        return this.mConstants;
    }

    public GZSolenoid(SolenoidConstants constants, GZSubsystem subsystem, String name) {
        super(constants.module, constants.channel);

        this.mConstants = constants;
        this.mSub = subsystem;
        this.mName = name;
        this.mOffTimer.start();
        this.mOnTimer.start();
        this.mSub.mSingleSolenoids.add(this);
    }

    public int getChangeCounts() {
        return mChangeCounts;
    }

    public void shouldForceOutputOff() {
        mForcedOff = this.mSub.isSafetyDisabled() || GZOI.getInstance().isDisabled();

        if (mForcedOff)
            runSolenoid(false, true);
    }

    // when safteydisabled don't allow change
    // when disabled disabled push to default position
    @Override
    public void set(boolean on) {
        runSolenoid(on, false);
    }

    private void runSolenoid(boolean on, boolean override) {
        if (!override && (mForcedOff || this.mSub.isSafetyDisabled()))
            return;

        if (on == super.get())
            return;

        super.set(on);
        mChangeCounts++;

        if (on) {
            mOnTimer.startTimer();
        } else {
            mOffTimer.startTimer();
        }
    }

    public SolenoidState getSolenoidState() {
        if (this.mConstants.extendTime == -3452 && super.get())
            return SolenoidState.ON;
        else if (this.mConstants.retractTime == -3452 && !super.get())
            return SolenoidState.OFF;

        if (super.get() && mOnTimer.get() > this.mConstants.extendTime)
            return SolenoidState.ON;
        else if (!super.get() && mOffTimer.get() > this.mConstants.retractTime)
            return SolenoidState.OFF;

        return SolenoidState.TRANSITION;
    }

    public GZSubsystem getGZSubsystem() {
        return mSub;
    }

    public String getGZName() {
        return mName;
    }
}