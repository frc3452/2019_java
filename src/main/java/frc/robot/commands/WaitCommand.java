package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

public class WaitCommand extends Command {

    private final double mTime;

    public WaitCommand(double time)
    {
        mTime = time;
    }

    @Override
    protected void initialize() {
        setTimeout(mTime);
    }

    @Override
    protected boolean isFinished() {
        return isTimedOut();
    }

}   