package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class EncoderFrom extends CommandGroup {

	/**
	 * Encoder reset, then EncoderDrive
	 * 
	 * @author macco
	 * @param left
	 * @param right
	 * @param leftAccel
	 * @param rightAccel
	 * @param topspeed
	 * @see ZeroEncoders
	 * @see EncoderDrive
	 */
	public EncoderFrom(double left, double right, double leftAccel, double rightAccel, double topspeed) {
		addSequential(new ZeroEncoders());
		addSequential(new EncoderDrive(left, right, leftAccel, rightAccel, topspeed));
	}
}
