package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kFiles;
import frc.robot.Constants.kIntake;
import frc.robot.Constants.kOI;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Intake.IntakeState;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZJoystick;
import frc.robot.util.GZJoystick.Buttons;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;

public class GZOI extends GZSubsystem {
	public static GZJoystick driverJoy = new GZJoystick(0);
	public static GZJoystick opJoy = new GZJoystick(1);

	private boolean mWasTele = false, mWasAuto = false, mWasTest = false;

	private LatchedBoolean mUserButton = new LatchedBoolean();
	private boolean mSafetyDisable = false;

	private PowerDistributionPanel pdp = GZPDP.getInstance().getPDP();

	private Climber climber = Climber.getInstance();
	private Intake intake = Intake.getInstance();

	private static GZOI mInstance = null;

	public static GZOI getInstance() {
		if (mInstance == null)
			mInstance = new GZOI();

		return mInstance;
	}

	private GZOI() {
		driverJoy = new GZJoystick(0);
		opJoy = new GZJoystick(1);
	}

	boolean recording = false;
	boolean prevRecording = recording;

	@Override
	public void loop() {
		outputSmartDashboard();

		// FLAGS
		if (isTele())
			mWasTele = true;
		if (isAuto())
			mWasAuto = true;
		if (isTest())
			mWasTest = true;

		// SAFETY DISABLE WITH USERBUTTON
		if (isFMS())
			mSafetyDisable = false;
		else if (mUserButton.update(RobotController.getUserButton()))
			mSafetyDisable = !mSafetyDisable;

		Robot.allSubsystems.disable(mSafetyDisable);

		// if (driverJoy.areButtonsHeld(Arrays.asList(Buttons.A, Buttons.RB,
		// Buttons.LEFT_CLICK)))
		// Robot.auton.crash();

		// RECORDING
		// recordingUpdates();

		if (isTele()) {

			// CLIMBER
			if (driverJoy.isDUpPressed())
				climber.addClimberCounter();
			if (driverJoy.getDUp())
				climber.runClimber(1, 5);
			else
				climber.stop();

			// INTAKE OPERATOR
			if (opJoy.getRawButton(Buttons.X))
				intake.manual(kIntake.Speeds.INTAKE);
			else if (opJoy.getRawButton(Buttons.Y))
				intake.manual(kIntake.Speeds.SLOW);
			else if (opJoy.getRawButton(Buttons.B))
				intake.manual(kIntake.Speeds.SHOOT);
			else if (opJoy.getRawButton(Buttons.A))
				intake.manual(kIntake.Speeds.PLACE);
			else if (opJoy.getRawButton(Buttons.BACK))
				intake.spin(false);
			else if (opJoy.getRawButton(Buttons.START))
				intake.spin(true);
			// INTAKE DRIVER
			else if (driverJoy.getRawButton(Buttons.X))
				intake.manual(kIntake.Speeds.INTAKE);
			else if (driverJoy.getRawButton(Buttons.Y))
				intake.manual(kIntake.Speeds.PLACE);
			else if (driverJoy.getRawButton(Buttons.B))
				intake.manual(kIntake.Speeds.SHOOT);
			else if (driverJoy.getRawButton(Buttons.RIGHT_CLICK))
				intake.spin(true);
			else
				intake.stop();
		} else if (isTest())
		{
			
		}

		// CONTROLLER RUMBLE

		if (GZUtil.between(getMatchTime(), 29.1, 30))
			// ENDGAME
			rumble(kOI.Rumble.ENDGAME);

		else if (intake.stateNot(IntakeState.NEUTRAL) && !isAuto())
			rumble(kOI.Rumble.INTAKE);
		else
			rumble(0);

		prevRecording = recording;
	}

	private void recordingUpdates() {
		if (driverJoy.isLClickPressed())
			recording = !recording;
		if (recording != prevRecording)
			GZFiles.getInstance().csvControl(kFiles.MP_NAME, kFiles.MP_FOLDER, kFiles.MP_USB, TASK.Record, recording);
	}

	public void addLoggingValues() {
		new LogItem("BATTERY-VOLTAGE") {
			@Override
			public String val() {
				return String.valueOf(RobotController.getBatteryVoltage());
			}
		};

		new LogItem("BROWNED-OUT") {
			@Override
			public String val() {
				return String.valueOf(RobotController.isBrownedOut());
			}
		};

		new LogItem("PDP-TEMP") {
			@Override
			public String val() {
				return String.valueOf(pdp.getTemperature());
			}
		};

		new LogItem("PDP-TEMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("PDP-AMP") {
			@Override
			public String val() {
				return String.valueOf(pdp.getTotalCurrent());
			}
		};

		new LogItem("PDP-AMP-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("PDP-VOLT") {

			@Override
			public String val() {
				return String.valueOf(pdp.getVoltage());
			}
		};

		new LogItem("PDP-VOLT-AVG", true) {
			@Override
			public String val() {
				return LogItem.Average_Left_Formula;
			}
		};

		new LogItem("INTAKE-STATE") {
			@Override
			public String val() {
				return Intake.getInstance().getStateString() + "-" + Intake.getInstance().isSafetyDisabled();
			}
		};

		new LogItem("CLIMB-STATE") {
			@Override
			public String val() {
				return Climber.getInstance().getStateString() + "-" + Climber.getInstance().isSafetyDisabled();
			}
		};
	}

	public boolean hasMotors()
	{
		return false;
	}
	public void addPDPTestingMotors(){}
	public void addMotorTestingGroups(){}

	public void setSafetyDisable(boolean disable) {
		this.mSafetyDisable = disable;
	}

	@Override
	public void outputSmartDashboard() {
		// SmartDashboard.putString("Selected Auton", Auton.getInstance().getAutonString());
		// SmartDashboard.putString("FIELD DATA", Auton.getInstance().gsm());
	}

	private static void rumble(double intensity) {
		driverJoy.rumble(intensity);
		opJoy.rumble(intensity);
	}

	public boolean isFMS() {
		return DriverStation.getInstance().isFMSAttached();
	}

	public boolean isRed() {
		if (DriverStation.getInstance().getAlliance() == Alliance.Red)
			return true;

		return false;
	}

	public double getMatchTime() {
		return DriverStation.getInstance().getMatchTime();
	}

	public boolean isAuto() {
		return DriverStation.getInstance().isAutonomous() && DriverStation.getInstance().isEnabled();
	}

	public boolean isDisabled() {
		return DriverStation.getInstance().isDisabled();
	}

	public boolean isEnabled() {
		return DriverStation.getInstance().isEnabled();
	}

	public boolean isTele() {
		return DriverStation.getInstance().isEnabled() && !isAuto() && !isTest();
	}

	public boolean isTest() {
		return DriverStation.getInstance().isTest();
	}

	public boolean wasTele() {
		return mWasTele;
	}

	public boolean wasAuto() {
		return mWasAuto;
	}

	public boolean wasTest() {
		return mWasTest;
	}

	@Override
	public String getStateString() {
		return "NA";
	}

	public void stop() {
	}

	protected void in() {
	}

	protected void out() {
	}

	protected void initDefaultCommand() {
	}
}