package frc.robot;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Health;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Lights;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.MotorChecker;

public class Robot extends TimedRobot {
	// Force construction of files first
	private GZFiles files = GZFiles.getInstance();

	// This order is crucial! it determines what order logging is added, what order
	// health is generated in, etc
	public static final GZSubsystemManager allSubsystems = new GZSubsystemManager(
			Arrays.asList(Drive.getInstance(), Elevator.getInstance(), Intake.getInstance(), Climber.getInstance(),
					Lights.getInstance(), GZOI.getInstance()));

	private Health health = Health.getInstance();
	private Auton auton = Auton.getInstance();

	private PersistentInfoManager infoManager = PersistentInfoManager.getInstance();

	// LOGGING CONTROL
	private final boolean logging = true, logToUsb = true;
	private final Folder loggingLocation = new Folder("Logging/DrivePractice121618");

	@Override
	public void robotInit() {
		health.assignSubsystems(allSubsystems.getSubsystems());

		infoManager.initialize();
		// BufferedWriter a = new BufferedWriter(new
		// FileWriter(GZFileMaker.getFile("MyName", "MyNewFolder", true, true)));

		// Gen health file
		health.generateHealth();

		allSubsystems.addLoggingValues();
		// allSubsystems.startLooping();
	}

	@Override
	public void robotPeriodic() {
		if (!GZOI.getInstance().isTest())
			allSubsystems.loop();
	}

	@Override
	public void disabledInit() {
		infoManager.printPersistentSettings();
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

		// Loop while game data is bad and timer is acceptable
		do {
		} while ((auton.gsm().equals("NOT") && auton.matchTimer.get() < 3));

		// Fill auton array and set values, regardless of good game message
		auton.startAuton();
	}

	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		enabledInits();
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void testInit() {
		enabledInits();
		
		Drive.getInstance().addMotorTestingGroups();
		Intake.getInstance().addMotorTestingGroups();
		MotorChecker.AmperageChecker.getInstance().checkMotors();
	}

	@Override
	public void testPeriodic() {
		// Scheduler.getInstance().run();
	}

	private void log(boolean startup) {
		if (logging)
			files.csvControl("Log", loggingLocation, logToUsb, TASK.Log, startup);
	}
}
