package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.Constants.kFiles;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Health;
import frc.robot.subsystems.RobotStateEstimator;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZNotifier;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZJoystick.Buttons;

public class Robot extends TimedRobot {
	// Force construction of files first
	private GZFiles files = GZFiles.getInstance();

	// This order is crucial! it determines what order logging is added, what order
	// // health is generated in, etc
	// public static final GZSubsystemManager allSubsystems = new
	// GZSubsystemManager(Drive.getInstance(),
	// RobotStateEstimator.getInstance(), Elevator.getInstance(),
	// Pneumatics.getInstance(), Intake.getInstance(),
	// GZOI.getInstance(), Superstructure.getInstance());

	public static final GZSubsystemManager allSubsystems = new GZSubsystemManager(Drive.getInstance(),
			RobotStateEstimator.getInstance(), GZOI.getInstance());

	private Health health = Health.getInstance();
	private Auton auton = Auton.getInstance();
	private PersistentInfoManager infoManager = PersistentInfoManager.getInstance();
	private Drive drive = Drive.getInstance();

	// LOGGING CONTROL
	private final boolean logging = true, logToUsb = true;
	private final Folder loggingLocation = new Folder(
			"Logging/ " + kFiles.ROBOT_NAME + "/Offseason/" + GZUtil.getDate());

	@Override
	public void robotInit() {
		health.assignSubsystems(allSubsystems.getSubsystems());

		infoManager.initialize();

		// new GZNotifier(() -> drive.printOdometry()).startPeriodic(.25);

		// Gen health file
		health.generateHealth();
		health.printForSubsystemErrors();

		allSubsystems.addLoggingValues();

		allSubsystems.startLooping();

		files.writeHardwareReport();
	}

	@Override
	public void robotPeriodic() {
		if (GZOI.driverJoy.getButtons(Buttons.BACK, Buttons.START) && drive.driveOutputLessThan(.05))
			PersistentInfoManager.getInstance().requestRestart();
	}

	@Override
	public void disabledInit() {
		// Lights.getInstance().setFade(1, Colors.BLUE, Colors.OFF);
		infoManager.robotDisabled();
		allSubsystems.stop();
		log(false);
	}

	@Override
	public void disabledPeriodic() {
		auton.autonChooser();
	}

	private void enabledInits() {
		infoManager.robotEnabled();
		allSubsystems.enableFollower();
		log(true);
	}

	@Override
	public void autonomousInit() {
		enabledInits();

		// timer start
		auton.matchTimer.oneTimeStartTimer();

		auton.startAuton();
	}

	@Override
	public void autonomousPeriodic() {
		if (Auton.getInstance().isAutoControl())
			Scheduler.getInstance().run();
		else
			Scheduler.getInstance().removeAll();
	}

	@Override
	public void teleopInit() {
		drive.slowSpeed(true);
		auton.cancelAuton();
		enabledInits();
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void testInit() {
		enabledInits();
	}

	@Override
	public void testPeriodic() {
		TestModeRunner.getInstance().update();
	}

	private void log(boolean startup) {
		if (logging)
			files.csvControl("Log", loggingLocation, logToUsb, TASK.Log, startup);
	}
}
