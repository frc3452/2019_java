package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.util.GZSubsystem;

public class GZDoubleSolenoid extends DoubleSolenoid implements IGZHardware {

    public static class DoubleSolenoidChannel {

        public final int fwd_channel;
        public final int rev_channel;
        public DoubleSolenoidChannel(int fwd_channel, int rev_channel) {
            this.fwd_channel = fwd_channel;
            this.rev_channel = rev_channel;
        }
    }

    private final GZSubsystem mSub;
    private final String mName;
    private final int mModule;
    private final DoubleSolenoidChannel mChannel;

    public GZDoubleSolenoid(DoubleSolenoidChannel channel, GZSubsystem subsystem, String name) {
        this(0, channel, subsystem, name);
    }

    public GZDoubleSolenoid(int module, DoubleSolenoidChannel channel, GZSubsystem subsystem, String name) {
        super(module, channel.fwd_channel, channel.rev_channel);
        this.mChannel = channel;
        this.mSub = subsystem;
        this.mName = name;
        this.mModule = module;
    }

    public DoubleSolenoidChannel getChannel()
    {
        return mChannel;
    }
    public int getPCM()
    {
        return this.mModule;
    }

    public GZSubsystem getGZSubsystem()
    {
        return mSub;
    }

    public String getGZName()
    {
        return mName;
    }
}