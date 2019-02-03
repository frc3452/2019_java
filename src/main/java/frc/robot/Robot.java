package frc.robot;

import com.sun.jdi.event.Event;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Health;
import frc.robot.subsystems.RobotStateEstimator;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZTimer;
import frc.robot.util.GZUtil;

public class Robot extends TimedRobot {
	// Force construction of files first
	private GZFiles files = GZFiles.getInstance();

	// This order is crucial! it determines what order logging is added, what order
	// // health is generated in, etc
	// public static final GZSubsystemManager allSubsystems = new
	// GZSubsystemManager(Drive.getInstance(),
	// RobotStateEstimator.getInstance(), Elevator.getInstance(),
	// Intake.getInstance(), Pneumatics.getInstance(),
	// GZOI.getInstance(), Superstructure.getInstance());

	public static final GZSubsystemManager allSubsystems = new GZSubsystemManager(Drive.getInstance(),
			RobotStateEstimator.getInstance(), GZOI.getInstance());

	private Health health = Health.getInstance();
	private Auton auton = Auton.getInstance();

	private PersistentInfoManager infoManager = PersistentInfoManager.getInstance();

	// LOGGING CONTROL
	private final boolean logging = true, logToUsb = true;
	private final Folder loggingLocation = new Folder("Logging/Offseason/" + GZUtil.getDate());

	private final GZTimer timer = new GZTimer();

	@Override
	public void robotInit() {
		infoManager.toString();

		health.assignSubsystems(allSubsystems.getSubsystems());

		infoManager.initialize();

		// Gen health file
		health.generateHealth();

		allSubsystems.addLoggingValues();

		allSubsystems.startLooping();

		files.writeHardwareReport();
		timer.startTimer();
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
