package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.subsystems.Drive;

/**
 *
 */
public class ZeroEncoders extends InstantCommand {

    private Drive drive = Drive.getInstance();

    public ZeroEncoders() {
        super();
        requires(drive);
    }

    // Called once when the command executes
    protected void initialize() {
    	drive.zeroEncoders();
    }

}