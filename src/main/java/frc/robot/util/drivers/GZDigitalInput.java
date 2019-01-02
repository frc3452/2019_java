package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.DigitalInput;

public class GZDigitalInput extends DigitalInput
{
    public GZDigitalInput(int channel)
    {
        super(channel);
    }

    public boolean not()
    {
        return !super.get();
    }
}