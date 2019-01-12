package frc.robot.util.drivers.pneumatics;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.IGZHardware;

public class GZSolenoid extends Solenoid implements IGZHardware {

    private final GZSubsystem mSub;
    private final String mName;
    private final int mModule;
    private final int mChannel;

    public GZSolenoid(int channel, GZSubsystem subsystem, String name) {
        this(0, channel, subsystem, name);
    }

    public int getChannel() {
        return this.mChannel;
    }

    public GZSolenoid(int module, int channel, GZSubsystem subsystem, String name) {
        super(module, channel);
        this.mChannel = channel;
        this.mSub = subsystem;
        this.mName = name;
        this.mModule = module;
        this.mSub.mSingleSolenoids.add(this);
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