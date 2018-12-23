package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Constants.kAuton;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kIntake;
import frc.robot.commands.drive.DriveTime;
import frc.robot.commands.drive.DriveToCube;
import frc.robot.commands.drive.DriveToStop;
import frc.robot.commands.drive.EncoderFrom;
import frc.robot.commands.drive.EncoderGyro;
import frc.robot.commands.drive.GyroPos;
import frc.robot.commands.drive.GyroReset;
import frc.robot.commands.drive.ZeroEncoders;
import frc.robot.commands.elevator.ElevatorPosition;
import frc.robot.commands.elevator.ElevatorTime;
import frc.robot.commands.elevator.ElevatorWhileDrive;
import frc.robot.commands.pwm.IntakeTime;
import frc.robot.commands.pwm.IntakeWhileDrive;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Auton.AO;
import frc.robot.subsystems.Auton.AV;

public class LeftAuton extends CommandGroup {

	/**
	 * @param option        AO
	 * @param switchVersion AV
	 * @param scaleVersion  AV
	 * @see Auton
	 */
	public LeftAuton(AO option, AV switchVersion, AV scaleVersion) {
		addSequential(new ZeroEncoders());
		addSequential(new GyroReset());

		Auton auton = Auton.getInstance();

		// IF DATA FOUND
		if (!auton.gsm().equals("NOT")) {

			switch (option) {
			case SWITCH:

				if (auton.gsm().charAt(0) == 'L') {
					switchL(switchVersion);

				} else if (auton.gsm().charAt(0) == 'R') {
					switchR(switchVersion);
				}

				break;
			case SCALE:
				if (auton.gsm().charAt(1) == 'L') {
					scaleL(scaleVersion);

				} else if (auton.gsm().charAt(1) == 'R') {
					scaleR(scaleVersion);
				}
				break;
			case SWITCH_PRIORITY_NO_CROSS:

				if (auton.gsm().charAt(0) == 'L') {
					switchL(switchVersion);
				} else if (auton.gsm().charAt(1) == 'L') {
					scaleL(scaleVersion);
				} else {
					defaultAuton();
				}

				break;
			case SCALE_PRIORITY_NO_CROSS:

				if (auton.gsm().charAt(1) == 'L') {
					scaleL(scaleVersion);
				} else if (auton.gsm().charAt(0) == 'L') {
					switchL(switchVersion);
				} else {
					defaultAuton();
				}

				break;
			case SCALE_ONLY:

				if (auton.gsm().charAt(1) == 'L') {
					scaleL(scaleVersion);
				} else {
					defaultAuton();
				}

				break;
			case SWITCH_ONLY:

				if (auton.gsm().charAt(0) == 'L') {
					switchL(switchVersion);
				} else {
					defaultAuton();
				}

				break;

			case DEFAULT:
				defaultAuton();
				break;
			default:

				System.out.println("ERROR Auto priority " + option + " not accepted; running default");
				defaultAuton();

				break;
			}
		} else {
			// game data not found
			defaultAuton();
		}
		addSequential(new DriveTime(0, 0, 16));
	}

	private void switchL(AV version) {
		switch (version) {
		case SEASON:
			addSequential(new ElevatorTime(.5, .1725));
			
			addSequential(new CommandGroup() {
				{
					// addParallel(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, .3));
					addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .5, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(70, .17, 3)); // switch

			addSequential(new ElevatorPosition(kElevator.HeightsInches.Switch));

			addSequential(new DriveTime(.5, 0, .75)); // hit switch
			addSequential(new DriveToStop(.35));

			addSequential(new IntakeTime(.6, 2)); // drop and backup
			// addParallel(new DriveTime(-.5, 0, .8));
			// addSequential(new ElevatorTime(-.1, 10));

			break;

		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward backwards to drop arm

			addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .4, 0, .017)); // drive to side of switch

			addParallel(new ElevatorPosition(3.5)); // raise arm
			addSequential(new EncoderFrom(0.75, -1.5, .5, .5, .5)); // turn to switch
			addSequential(new EncoderFrom(1.1, 1.1, .5, .5, .5)); // drive and drop
			addSequential(new IntakeTime(1, .5));
			addSequential(new EncoderFrom(-.5, -.5, .5, .5, .5));
			break;
		default:
			defaultAuton();
			break;
		}

	}

	private void switchR(AV version) {
		switch (version) {
		case SEASON:
			defaultAuton();
			break;
		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addParallel(new IntakeTime(-.2, 15));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward backwards to drop arm

			addSequential(new EncoderGyro(11.6, 11.6, .5, .5, .6, 0, .017)); // drive to side of switch

			addSequential(new EncoderFrom(0.75, -1.5, .5, .5, .6)); // turn to switch

			addSequential(new ZeroEncoders());
			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(3.5, .6));
					addSequential(new EncoderGyro(11.25, 11.25, .5, .5, .6, 90, 0.021)); // drive back of switch
				}
			});

			addSequential(new EncoderFrom(1.6, -1.2, .5, .5, .6));

			addSequential(new DriveTime(.5, 0, .5));
			addSequential(new DriveToStop(.55)); // was .45

			addSequential(new IntakeTime(.75, .25));

			addParallel(new ElevatorWhileDrive(-15, .9));
			addSequential(new EncoderFrom(-.85, -.75, .5, .5, .6));

			addSequential(new DriveToCube(.58, 5)); // was .45
			addSequential(new EncoderFrom(-.5, -.5, .5, .5, .6));

			addSequential(new ElevatorTime(1, .6));

			// hit switch
			addSequential(new DriveTime(.5, 0, .5));
			addSequential(new DriveToStop(.55)); // was .45

			addSequential(new IntakeTime(-1, 1));

			break;
		default:
			defaultAuton();
			break;
		}
	}

	private void scaleL(AV version) {
		switch (version) {
		case SEASON:
			addSequential(new ElevatorTime(.5, .1725));

			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(18, .05));
					addSequential(new EncoderGyro(15.8, 15.8, .35, .35, .4, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(70, kAuton.GYRO_TURN_SPEED, 5));

			addSequential(new ElevatorPosition(kElevator.TOP_HEIGHT_INCHES));
			addSequential(new EncoderFrom(.6, .6, .3, .3, .3));
			addSequential(new IntakeTime(.8, 1));

			addSequential(new EncoderFrom(-.8, -.8, .3, .3, .3));
			addSequential(new ElevatorTime(-.3, 20));

			break;
		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward backwards to drop arm

			// Drive to scale

			// 15.27
			addSequential(new EncoderGyro(15.8, 15.8, .4, .4, .5, 0, kAuton.CORRECTION));
			// TURN CHANGED FINALS 3
			addSequential(new EncoderFrom(1.5, -1.15, .6, .6, .6));

			addSequential(new DriveTime(-.5, 0, .5));
			addSequential(new DriveToStop(-.35)); // was .45

			addSequential(new ElevatorPosition(kElevator.TOP_HEIGHT_INCHES)); // raise and forward
			addSequential(new EncoderFrom(.5, .5, .4, .4, .6));

			addSequential(new IntakeTime(.8, 4)); // shoot, back up down and spin

			break;
		default:
			defaultAuton();
			break;
		}

	}

	private void scaleR(AV version) {
		switch (version) {
		case SEASON:
			addSequential(new ElevatorTime(.5, .1725));

			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(18, .15));
					addSequential(new EncoderGyro(11.4, 11.4, .35, .35, .4, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(80, kAuton.GYRO_TURN_SPEED, 3.5));

			addSequential(new ZeroEncoders());
			addSequential(new EncoderGyro(5.2, 5.2, .3, .3, .5, 90, kAuton.CORRECTION));

			break;

		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward backwards to drop arm

			addSequential(new EncoderGyro(11.7, 11.7, .6, .6, .65, 0, kAuton.CORRECTION)); // drive to side of switch

			addParallel(new ElevatorPosition(2)); // raise
			addSequential(new EncoderFrom(0.75, -1.5, .5, .5, .5)); // turn to switch

			addSequential(new ZeroEncoders());
			addSequential(new EncoderGyro(10.1, 10.1, .5, .5, .5, 90, kAuton.CORRECTION)); // drive front of scale

			addSequential(new EncoderFrom(-1.5, .75, .5, .5, .5));

			addSequential(new ElevatorPosition(15)); // raise and turn to switch

			addSequential(new CommandGroup() {
				{
					addParallel(new IntakeWhileDrive(kIntake.Speeds.SLOW, .92, 3));
					addSequential(new EncoderFrom(2.41, 2.61, .1, .1, .15));
				}

			});
			addSequential(new DriveTime(-.4, 0, 1.6));

			addParallel(new ElevatorTime(-.65, 10));
			addSequential(new GyroPos(225, .4, 1));

			break;
		default:
			defaultAuton();
			break;
		}
	}

	private void defaultAuton() {
		addSequential(new ElevatorTime(.5, .1725));

		addSequential(new DriveTime(.125, 0, 5));
		// addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .4, 0, kAuton.CORRECTION)); // drive to side of switch
		// addSequential(new ElevatorPosition(kElevator.HeightsInches.Switch)); // raise arm
	}

}
