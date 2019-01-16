package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kFiles;
import frc.robot.Constants.kOI;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZFiles;
import frc.robot.util.GZLog;
import frc.robot.util.GZFiles.TASK;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.GZJoystick.Buttons;
import frc.robot.util.drivers.buttonboard.OperatorController;

public class GZOI extends GZSubsystem {
	public static GZJoystick driverJoy = new GZJoystick(0);
	public static OperatorController op = new OperatorController(1);

	private boolean mWasTele = false, mWasAuto = false, mWasTest = false;

	private LatchedBoolean mUserButton = new LatchedBoolean();
	private boolean mSafetyDisable = false;

	private Drive drive = Drive.getInstance();

	private static GZOI mInstance = null;

	public static GZOI getInstance() {
		if (mInstance == null)
			mInstance = new GZOI();

		return mInstance;
	}

	private GZOI() {
		driverJoy = new GZJoystick(0);
	}

	boolean bToggled = false;

	@Override
	public void loop() {
		outputSmartDashboard();

		// FLAGS
		if (isTele())
			mWasTele = true;
		else if (isAuto())
			mWasAuto = true;
		else if (isTest())
			mWasTest = true;

		// SAFETY DISABLE WITH USERBUTTON
		if (isFMS())
			mSafetyDisable = false;
		else if (mUserButton.update(RobotController.getUserButton()))
			mSafetyDisable = !mSafetyDisable;
		Robot.allSubsystems.disable(mSafetyDisable);

		if (driverJoy.getButtons(Buttons.LB, Buttons.RB, Buttons.LEFT_CLICK))
			op.setButtonBoard();
		else if (driverJoy.getButtons(Buttons.LB, Buttons.RB, Buttons.RIGHT_CLICK))
			op.setXboxController();

		if (isTele()) {

			//Velocity testing
			if (driverJoy.isBPressed())
				bToggled = !bToggled;

			if (bToggled && kDrivetrain.TUNING) {
				final double high = 1500;
				final double left = GZUtil.scaleBetween(driverJoy.getLeftAnalogY(), -high, high, -1, 1);
				final double right = -GZUtil.scaleBetween(driverJoy.getRightAnalogY(), -high, high, -1, 1);
				drive.printVelocity(left);
				drive.setVelocity(left, right);
			} else {
				drive.setWantedState(DriveState.OPEN_LOOP_DRIVER);
			}

			if (driverJoy.isAPressed())
				drive.toggleSlowSpeed();
		}

		// CONTROLLER RUMBLE
		if (GZUtil.between(getMatchTime(), 29.1, 30))
			// ENDGAME
			rumble(kOI.Rumble.ENDGAME);
		else
			rumble(0);
	}

	public String getSmallString() {
		// no motors, so not really used but
		return "GZOI";
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
				return String.valueOf(GZPDP.getInstance().getTemperature());
			}
		};
		GZLog.addAverageLeft("PDP-TEMP-AVG");

		new LogItem("PDP-AMP") {
			@Override
			public String val() {
				return String.valueOf(GZPDP.getInstance().getTotalCurrent());
			}
		};
		GZLog.addAverageLeft("PDP-AMP-AVG");


		new LogItem("PDP-VOLT") {

			@Override
			public String val() {
				return String.valueOf(GZPDP.getInstance().getVoltage());
			}
		};
		GZLog.addAverageLeft("PDP-VOLT-AVG");
	}

	public boolean hasMotors() {
		return false;
	}

	public boolean hasAir() {
		return false;
	}

	public void addPDPTestingMotors() {
	}

	public void setSafetyDisable(boolean disable) {
		this.mSafetyDisable = disable;
	}

	@Override
	public void outputSmartDashboard() {
		// SmartDashboard.putString("Selected Auton",
		// Auton.getInstance().getAutonString());
		// SmartDashboard.putString("FIELD DATA", Auton.getInstance().gsm());
	}

	private static void rumble(double intensity) {
		driverJoy.rumble(intensity);
		// opJoy.rumble(intensity);
	}

	public boolean isFMS() {
		return DriverStation.getInstance().isFMSAttached();
	}

	public Alliance getAlliance() {
		return DriverStation.getInstance().getAlliance();
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
		return DriverStation.getInstance().isAutonomous() && isEnabled();
	}

	public boolean isDisabled() {
		return DriverStation.getInstance().isDisabled();
	}

	public boolean isEnabled() {
		return DriverStation.getInstance().isEnabled();
	}

	public boolean isTele() {
		return isEnabled() && !isAuto() && !isTest();
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