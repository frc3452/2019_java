package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Constants.kElevator;
import frc.robot.commands.drive.DriveTime;
import frc.robot.commands.drive.DriveToCube;
import frc.robot.commands.drive.DriveToStop;
import frc.robot.commands.drive.EncoderDrive;
import frc.robot.commands.drive.EncoderFrom;
import frc.robot.commands.drive.GyroReset;
import frc.robot.commands.drive.ZeroEncoders;
import frc.robot.commands.elevator.ElevatorPosition;
import frc.robot.commands.elevator.ElevatorTime;
import frc.robot.commands.elevator.ElevatorWhileDrive;
import frc.robot.commands.pwm.IntakeTime;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Auton.AO;
import frc.robot.subsystems.Auton.AV;

public class MiddleAuton extends CommandGroup {

	/**
	 * @param option        AO
	 * @param switchVersion AV
	 * @see Auton
	 */
	public MiddleAuton(AO option, AV switchVersion) {
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
			// if game data bad
			defaultAuton();
		}

		addSequential(new DriveTime(0, 0, 16));
	}

	private void switchL(AV version) {
		switch (version) {
		case SEASON:

			addSequential(new ElevatorTime(.5, .1725));

			addSequential(new EncoderDrive(1, 2.35, .8, .8, .6)); // init curve

			// drive to switch
			addParallel(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, .1/* .8 */));
			addSequential(new EncoderFrom(3.8, 3.25, .6, .6, .7));

			addSequential(new ElevatorPosition(kElevator.HeightsInches.Switch));

			// hit switch
			addSequential(new DriveTime(.5, 0, .5));
			addSequential(new DriveToStop(.35)); // was .45

			addSequential(new IntakeTime(.5, .5)); // first place

			// back up
			// addParallel(new DriveTime(-.5, 0, 1.5));
			// addSequential(new ElevatorTime(-.1, 10));

			// backup
			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(-15, .75));
					addSequential(new EncoderFrom(-3.8, -3.8, .5, .5, .6));
				}
			});

			addSequential(new ZeroEncoders());

			/**
			 * //TURN AND GRAB CUBE addSequential(new CommandGroup() { { addSequential(new
			 * EncoderDrive(.35, -.4, .5, .5, .6)); //turn to switch addSequential(new
			 * DriveToCube(.35, 5)); //was .45 addParallel(new IntakeTime(-.2, .75));
			 * addSequential(new EncoderDrive(0, 0, .6, .6, .65)); } });
			 * 
			 * //DRIVE TO SWITCH addSequential(new CommandGroup() { { addParallel(new
			 * ElevatorWhileDrive(kElevator.HeightsInches.Switch, .6)); addSequential(new
			 * EncoderFrom(3, 3, .5, .5, .6)); } });
			 * 
			 * //hit switch addSequential(new DriveTime(.5, 0, .5)); addSequential(new
			 * DriveToStop(.55)); //was .45
			 * 
			 * addSequential(new IntakeTime(.5, .5)); //second place
			 * 
			 * //back up addParallel(new EncoderFrom(-2, -2, .3, .3, .5)); addSequential(new
			 * ElevatorTime(-.1, 10));
			 **/
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

			addSequential(new EncoderDrive(2.5, 1, .8, .8, .6));

			addParallel(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, .1));
			addSequential(new EncoderFrom(2.9, 4.2 + .72 /* +.38 */, .6, .6, .7));

			addSequential(new ElevatorPosition(kElevator.HeightsInches.Switch));

			addSequential(new DriveTime(.5, 0, .5));
			addSequential(new DriveToStop(.55));

			addSequential(new IntakeTime(.5, .5)); // PLACE 1

			// back up
			// addParallel(new DriveTime(-.5, 0, 1.5));
			// addSequential(new ElevatorTime(-.1, 10));

			// backup
			addSequential(new CommandGroup() {
				{
					addParallel(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, .75));
					addSequential(new EncoderFrom(-3.8, -3.8, .6, .6, .6));
				}
			});

			// lower and reset encoders
			addSequential(new ZeroEncoders());

			// TURN AND GRAB CUBE
			addSequential(new CommandGroup() {
				{
					addSequential(new EncoderFrom(-.4, .35, .5, .5, .6)); // turn to switch
					addSequential(new DriveToCube(.35, 5));
					addParallel(new IntakeTime(-.5, .75));
					addSequential(new EncoderDrive(0, 0, .6, .6, .65));
				}
			});

			// DRIVE TO SWITCH
			addSequential(new CommandGroup() {
				{
					addSequential(new ElevatorWhileDrive(kElevator.HeightsInches.Switch, 0));
					addSequential(new EncoderFrom(3, 3, .5, .5, .6));
				}
			});

			// hit switch
			addSequential(new DriveTime(.5, 0, .5));
			addSequential(new DriveToStop(.55));

			addSequential(new IntakeTime(.5, 1.5)); // second place

			// back up
			addParallel(new EncoderFrom(-2, -2, .3, .3, .5));
			addSequential(new ElevatorTime(-.1, 10));

			break;
		default:
			defaultAuton();
			break;
		}
	}

	private void defaultAuton() {
		addSequential(new ElevatorTime(.5, .1725));

		addSequential(new EncoderFrom(7.2, 2, .35, .35, .5));
		addSequential(new DriveTime(-.4, 0, 7));
	}
}
