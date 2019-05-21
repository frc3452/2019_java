package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.Constants.kFiles;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Health;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.RobotStateEstimator;
import frc.robot.subsystems.Superstructure;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZUtil;

public class Robot extends TimedRobot {
	// Force construction of files first
	private GZFiles files = GZFiles.getInstance();

	// This order is crucial! it determines what order logging is added, what order
	// // health is generated in, etc
	public static final GZSubsystemManager allSubsystems = new GZSubsystemManager(Drive.getInstance(),
			RobotStateEstimator.getInstance(), Elevator.getInstance(), Pneumatics.getInstance(), Intake.getInstance(),
			GZOI.getInstance(), Superstructure.getInstance());

	// public static final GZSubsystemManager allSubsystems = new
	// GZSubsystemManager(Drive.getInstance(),
	// RobotStateEstimator.getInstance(), GZOI.getInstance());

	private Health health = Health.getInstance();
	private Auton auton = Auton.getInstance();
	private PersistentInfoManager infoManager = PersistentInfoManager.getInstance();
	private Drive drive = Drive.getInstance();

	// LOGGING CONTROL
	private final boolean logging = true, logToUsb = true;

	private final String date = GZUtil.getDate();

	private final Folder loggingLocation = new Folder("Logging/ " + kFiles.ROBOT_NAME + "/WORLDS/" + date);

	public static double enabledTime = 0.0;

	@Override
	public void robotInit() {
		Drive.getInstance().setDefaultStartingPosition();

		auton.fillAutonArray();

		health.assignSubsystems(allSubsystems.getSubsystems());

		infoManager.initialize();

		// new GZNotifier(() -> drive.printOdometry()).startPeriodic(.25);

		// Gen health file
		health.generateHealth();
		health.printForSubsystemErrors();

		addLogValues();

		allSubsystems.startLooping();

		files.writeHardwareReport();
		System.out.println("Date reported: " + date);
	}

	private void addLogValues() {
		new LogItem("MSGs") {
			@Override
			public String val() {
				return GZFiles.getInstance().getInstantLogs();
			}
		};
		allSubsystems.addLoggingValues();
	}

	@Override
	public void robotPeriodic() {
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
