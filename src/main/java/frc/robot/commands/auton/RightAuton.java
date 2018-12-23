package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Constants.kAuton;
import frc.robot.Constants.kElevator;
import frc.robot.commands.drive.DriveTime;
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
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Auton.AO;
import frc.robot.subsystems.Auton.AV;

public class RightAuton extends CommandGroup {

	/**
	 * @param option        AO
	 * @param switchVersion AV
	 * @param scaleVersion  AV
	 * @see Auton
	 */
	public RightAuton(AO option, AV switchVersion, AV scaleVersion) {
		addSequential(new GyroReset());
		addSequential(new ZeroEncoders());

		Auton auton = Auton.getInstance();

		// IF DATA FOUND
		if (!auton.gsm().equals("NOT")) {

			switch (option) {
			case SWITCH:

				if (auton.gsm().charAt(0) == 'L') {
					switchL(switchVersion);

				} else if (auton.gsm().charAt(0) == 'R') {
					switchR(scaleVersion);
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

				if (auton.gsm().charAt(0) == 'R') {
					switchR(switchVersion);
				} else if (auton.gsm().charAt(1) == 'R') {
					scaleR(scaleVersion);
				} else {
					defaultAuton();
				}

				break;
			case SCALE_PRIORITY_NO_CROSS:

				if (auton.gsm().charAt(1) == 'R')
					scaleR(scaleVersion);
				else if (auton.gsm().charAt(0) == 'R')
					switchR(switchVersion);
				else
					defaultAuton();

				break;
			case SWITCH_ONLY:

				if (auton.gsm().charAt(0) == 'R')
					switchR(switchVersion);
				else
					defaultAuton();

				break;
			case SCALE_ONLY:

				if (auton.gsm().charAt(1) == 'R')
					switchR(scaleVersion);
				else
					defaultAuton();

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
			defaultAuton();
			break;
		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward
															// backwards to drop
															// arm

			// add .3?
			addSequential(new EncoderGyro(11, 11, .4, .4, .5, 0, kAuton.CORRECTION)); // drive
			// to
			// side
			// of
			// switch

			addSequential(new EncoderFrom(-1.5, 0.75, .5, .5, .5)); // turn to
																	// switch

			addSequential(new ZeroEncoders());
			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(3.5, .6));
					addSequential(new EncoderGyro(10.5, 10.5, .5, .5, .6, 180, kAuton.CORRECTION)); // drive
					// back
					// of
					// switch
				}
			});

			addSequential(new GyroPos(172, .35, 1));

			addSequential(new DriveTime(.5, 0, .75)); // hit switch
			addSequential(new DriveToStop(.4));

			addSequential(new IntakeTime(.5, 1));

			addParallel(new DriveTime(-.5, 0, .8));
			addSequential(new ElevatorTime(-.15, 10));

			break;
		default:
			defaultAuton();
			break;
		}
	}

	private void switchR(AV version) {
		switch (version) {

		case SEASON:
			addSequential(new ElevatorTime(.5, .1725));
															// backwards to drop
			addSequential(new CommandGroup() {
				{
					// addParallel(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, .3));
					addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .5, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(290, kAuton.GYRO_TURN_SPEED, 3));														// switch

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
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward
															// backwards to drop
															// arm

			addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .4, 0, kAuton.CORRECTION)); // drive
			// to
			// side
			// of
			// switch

			addParallel(new ElevatorPosition(5)); // raise arm
			addSequential(new EncoderFrom(-1.5, .75, .5, .5, .5)); // turn to
																	// switch
			addSequential(new EncoderFrom(.8, .8, .5, .5, .5)); // drive and
																// drop
			addSequential(new IntakeTime(1, .5));
			addSequential(new EncoderFrom(-.5, -.5, .5, .5, .5));
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
					addParallel(new ElevatorWhileDrive(18, .15));
					addSequential(new EncoderGyro(11.4, 11.4, .35, .35, .4, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(280, kAuton.GYRO_TURN_SPEED, 3.5));

			addSequential(new ZeroEncoders());
			addSequential(new EncoderGyro(5.2, 5.2, .3, .3, .5, 270, kAuton.CORRECTION));

			break;
		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward
															// backwards to drop
															// arm

			addSequential(new EncoderGyro(11.5, 11.5, .6, .6, .65, 0, kAuton.CORRECTION)); // drive
			// to
			// side
			// of
			// switch

			addParallel(new ElevatorPosition(2)); // raise
			addSequential(new EncoderFrom(-1.5, 0.75, .5, .5, .5)); // turn to
																	// switch

			addSequential(new ZeroEncoders());
			// addSequential(new EncoderGyro(10.6, 10.6, .5, .5, .5, -90,
			// kAutonSelector.CORRECTION)); // drive front of scale
			addSequential(new EncoderGyro(10.6 / 3, 10.6 / 3, .5, .5, .5, -90, kAuton.CORRECTION));

			/**
			 * addSequential(new EncoderFrom(.75, -1.5, .5, .5, .5));
			 * 
			 * addSequential(new ElevatorPosition(15)); // raise and turn to switch
			 * 
			 * addSequential(new CommandGroup() { { addParallel(new IntakeWhileDrive(.4,
			 * .92, .6)); addSequential(new EncoderFrom(2.61, 2.41, .1, .1, .15)); } });
			 * 
			 * addSequential(new DriveTime(-.4, 0, 1.6));
			 * 
			 * addParallel(new ElevatorTime(-.65, 10)); addSequential(new GyroPos(135, .4,
			 * 1));
			 */

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
					addParallel(new ElevatorWhileDrive(18, .05));
					addSequential(new EncoderGyro(15.8, 15.8, .35, .35, .4, 0, kAuton.CORRECTION));
				}
			});

			addSequential(new GyroPos(290, kAuton.GYRO_TURN_SPEED, 5));
			addSequential(new EncoderFrom(-.7, -.7, .3, .3, .5));

			addSequential(new ElevatorPosition(kElevator.TOP_HEIGHT_INCHES));

			addSequential(new EncoderFrom(.6, .6, .3, .3, .3));
			addSequential(new IntakeTime(.8, 1));

			addSequential(new EncoderFrom(-.8, -.8, .3, .3, .3));
			addSequential(new ElevatorTime(-.3, 20));

			break;
		case FOREST_HILLS:
			addParallel(new DriveTime(.55, 0, .5));
			addSequential(new ElevatorTime(.5, .1725));
			addSequential(new DriveTime(-.55, 0, .225)); // jog forward
															// backwards to drop
															// arm

			// Drive to scale
			addSequential(new EncoderGyro(15.27, 15.27, .6, .6, .7, 0, kAuton.CORRECTION));
			// TURN CHANGED FINALS 3
			addSequential(new EncoderFrom(-1.15, 1.5, .6, .6, .6)); // turn to
																	// switch

			addSequential(new DriveTime(-.5, 0, .5));
			addSequential(new DriveToStop(-.55)); // was .45

			addSequential(new ElevatorPosition(7)); // raise and forward
			addSequential(new EncoderFrom(.5, .5, .4, .4, .6));

			addSequential(new IntakeTime(.8, 4));

			break;
		default:
			defaultAuton();
			break;
		}
	}

	private void defaultAuton() {
		addSequential(new ElevatorTime(.5, .1725));

		addSequential(new EncoderGyro(7.91, 7.91, .4, .4, .4, 0, kAuton.CORRECTION)); // drive
		// to
		// side
		// of
		// switch
		addSequential(new ElevatorPosition(kElevator.HeightsInches.Switch)); // raise arm
	}

}
