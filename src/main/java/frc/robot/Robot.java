package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.Constants.kFiles;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Health;
import frc.robot.subsystems.RobotStateEstimator;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZFileMaker.FileExtensions;

public class Robot extends TimedRobot {
	// Force construction of files first
	private GZFiles files = GZFiles.getInstance();

	// This order is crucial! it determines what order logging is added, what order
	// health is generated in, etc
	public static final GZSubsystemManager allSubsystems = new
	GZSubsystemManager(Drive.getInstance(), GZOI.getInstance(), RobotStateEstimator.getInstance());
	// public static final GZSubsystemManager allSubsystems = new GZSubsystemManager(Drive.getInstance(),
	// 		RobotStateEstimator.getInstance(), GZOI.getInstance(), Elevator.getInstance(), Intake.getInstance(),
	// 		Pneumatics.getInstance());

	private Health health = Health.getInstance();
	private Auton auton = Auton.getInstance();
	private Drive drive = Drive.getInstance();

	private PersistentInfoManager infoManager = PersistentInfoManager.getInstance();

	// LOGGING CONTROL
	private final boolean logging = true, logToUsb = false;
	private final Folder loggingLocation = new Folder("Logging/Offseason");

	@Override
	public void robotInit() {
		health.assignSubsystems(allSubsystems.getSubsystems());

		infoManager.initialize();

		// Gen health file
		health.generateHealth();

		allSubsystems.addLoggingValues();

		allSubsystems.startLooping();

		files.writeHardwareReport();
	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void disabledInit() {
		// infoManager.printPersistentSettings();
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
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
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
