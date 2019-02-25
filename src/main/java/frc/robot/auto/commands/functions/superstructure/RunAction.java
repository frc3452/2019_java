package frc.robot.auto.commands.functions.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Superstructure.Actions;

public class RunAction extends Command {
    Actions mAction;
    public RunAction(Actions a) {
        mAction = a;
    }

    protected void initialize() {
        Superstructure.getInstance().runAction(mAction);
    }

    protected void execute() {
    }

    protected boolean isFinished() {
        return Superstructure.getInstance().isActionDone();
    }

    protected void end() {
    }

    protected void interrupted() {
        end();
    }
}
