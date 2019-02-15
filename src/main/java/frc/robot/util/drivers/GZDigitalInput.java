package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.DigitalInput;

public class GZDigitalInput extends DigitalInput {

    private final boolean invert;

    public GZDigitalInput(int channel, boolean invert) {
        super(channel);
        this.invert = invert;
    }

    public GZDigitalInput(int channel) {
        this(channel, false);
    }

    @Override
    public boolean get() {
        if (invert)
            return !super.get();

        return super.get();
    }

    public boolean not() {
        return !super.get();
    }
}