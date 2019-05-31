package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator.QueueHeights;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.ClimbingState;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Superstructure;
import frc.robot.util.BooleanStateChange;
import frc.robot.util.GZLog;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.controllers.OperatorController;

public class GZOI extends GZSubsystem {
	public static GZJoystick driverJoy = new GZJoystick(0, .09);
	public static GZJoystick op = new GZJoystick(1);

	// private GZSolenoid mLeds;

	private UsbCamera mCamera;

	// private GZAnalogInput mKey = new GZAnalogInput(this, "Lockout key",
	// kOI.LOCK_OUT_KEY, kOI.LOCK_OUT_KEY_VOLT);

	private LatchedBoolean mUserButton = new LatchedBoolean();

	private boolean mWasTele = false, mWasAuto = false, mWasTest = false;

	private int mDisabledPrintOutLoops = 0;
	private boolean mSafetyDisable = false;

	private Drive drive = Drive.getInstance();
	private Elevator elev = Elevator.getInstance();
	private Superstructure supe = Superstructure.getInstance();

	// private Auton auton = Auton.getInstance();

	private static GZOI mInstance = null;

	public static GZOI getInstance() {
		if (mInstance == null)
			mInstance = new GZOI();

		return mInstance;
	}

	private GZOI() {
		mCamera = CameraServer.getInstance().startAutomaticCapture(0);

		cameraSettings();
		// mLeds = new GZSolenoid(kLights.PCM_LED, this, "LEDs");

		driverJoy.setLongPressDuration(0.20);
	}

	private void cameraSettings() {
		mCamera.setResolution(160, 120);
		// mCamera.setExposureAuto();
		// mCamera.setFPS(10);
		// mCamera.setExposureManual(50);
		mCamera.setBrightness(30);
		// mCamera.setExposureManual(40);
	}

	@Override
	public void loop() {
		driverJoy.update();
		op.update();

		// FLAGS
		if (isTele())
			mWasTele = true;
		else if (isAuto())
			mWasAuto = true;
		else if (isTest())
			mWasTest = true;

		// mLeds.set(true);
		// cameraSettings();

		// SAFTEY DISABLED
		boolean safteyDisable = false;
		if (isFMS())
			safteyDisable = false;
		else if (getSafteyKey())
			safteyDisable = true;
		else if (mUserButton.update(RobotController.getUserButton()))
			safteyDisable = !mSafetyDisable;

		if (mSafetyDisable != safteyDisable) {
			mSafetyDisable = safteyDisable;
			Robot.allSubsystems.disable(mSafetyDisable);
			System.out.println("WARNING All subsystems " + (mSafetyDisable ? "disabled" : "enabled") + "!");
		}

		if (mSafetyDisable) {
			if (++mDisabledPrintOutLoops > 300) {
				System.err.println("ERROR All subsystems disabled, check Saftey Key or toggle UserButton");
				mDisabledPrintOutLoops = 0;
			}
		}

		// Disabled
		if (isDisabled()) {
			disabled();
		} else if (Auton.getInstance().isAutoControl()) { // running auto command
			Auton.getInstance()
					.controllerCancel(driverJoy.aButton.isBeingPressed() && driverJoy.xButton.isBeingPressed());
		} else if (isAuto() || isTele()) { // not running auto command and in sandstorm or tele
			handleControls();
		}
	}

	public void handleControls() {
		handleRumble();
		handleSuperStructureControl();
		handleDriverController();
	}

	private void handleSuperStructureControl() {
		if (op.xButton.isBeingPressed()) {
			supe.zeroElevator();
		} else if (op.leftCenterClick.isBeingPressed() && op.aButton.wasActivatedReset()) {
			supe.queueHeight(QueueHeights.LOW);
		} else if (op.leftCenterClick.isBeingPressed() && op.bButton.wasActivatedReset()) {
			supe.queueHeight(QueueHeights.MIDDLE);
		} else if (op.leftCenterClick.isBeingPressed() && op.yButton.wasActivatedReset()) {
			supe.queueHeight(QueueHeights.HIGH);
		} else if (!op.leftCenterClick.isBeingPressed() && op.aButton.shortReleased()) {
			supe.setHeight(Heights.HP_1);
		} else if (!op.leftCenterClick.isBeingPressed() && op.bButton.shortReleased()) {
			supe.setHeight(Heights.HP_2);
		} else if (!op.leftCenterClick.isBeingPressed() && op.yButton.shortReleased()) {
			supe.setHeight(Heights.HP_3);
		} else if (!op.leftCenterClick.isBeingPressed() && op.aButton.longPressed()) {
			supe.setHeight(Heights.Cargo_1);
		} else if (!op.leftCenterClick.isBeingPressed() && op.bButton.longPressed()) {
			supe.setHeight(Heights.Cargo_2);
		} else if (!op.leftCenterClick.isBeingPressed() && op.yButton.longPressed()) {
			supe.setHeight(Heights.Cargo_3);
		} else if (!op.leftCenterClick.isBeingPressed() && op.startButton.wasActivated()) {
			supe.setHeight(Heights.Cargo_Ship);
		} else if (op.leftCenterClick.isBeingPressed() && op.startButton.wasActivated()) {
			supe.queueHeight(QueueHeights.CARGO_SHIP);
		} else if (op.rightBumper.wasActivated()) {
			supe.toggleClaw();
		} else if (op.leftBumper.wasActivated()) {
			supe.toggleSlides();
		} else if (op.POV0.wasActivated()) {
			supe.jogElevator(1);
		} else if (op.POV180.wasActivated()) {
			supe.jogElevator(-1);
		} else if (op.leftTrigger.wasActivated()) {
			supe.retrieve();
		} else if (op.rightTrigger.wasActivated()) {
			supe.score();
		} else if (op.rightCenterClick.wasActivated()) {
			supe.dropCrawler();
		} else if (op.backButton.wasActivated()) {
			supe.operatorIntake();
		} else if (op.POV270.wasActivated()) {
			supe.toggleIntakeRoller();
		} else if (op.POV90.wasActivated()) {
			supe.intakeEject();
		}
	}

	private void handleRumble() {
		double driverRumble = 0;
		double opRumble = 0;

		if (elev.isSpeedOverriden()) {
			driverRumble = Math.max(0.45, driverRumble);
		}
		if (!drive.isSlow()) {
			driverRumble = Math.max(0.1, driverRumble);
		}
		if (GZUtil.between(getMatchTime(), 29.1, 30)) {
			driverRumble = Math.max(.45, driverRumble);
			opRumble = Math.max(.45, opRumble);
		}

		if (!driverJoy.isRumbling()) {
			driverJoy.setRumble(driverRumble);
		}

		if (!op.isRumbling()) {
			op.setRumble(opRumble);
		}
	}

	private void disabled() {
		Auton.getInstance().autonChooser();
		// auton.print();

		Auton.getInstance().toggleAutoWait(driverJoy.aButton.isBeingPressed() && driverJoy.yButton.isBeingPressed());
		Auton.getInstance()
				.toggleAutoGamePiece(driverJoy.aButton.isBeingPressed() && driverJoy.xButton.isBeingPressed());

		rumble(0.0);
		// handleRumble();
	}

	private void rumble(double d) {
		driverJoy.setRumble(0);
		op.setRumble(0);
	}

	// Driver variables

	private void handleDriverController() {
		if (driverJoy.leftBumper.isBeingPressed()) {

			if (!kDrivetrain.NO_SHIFTER) {
				if (driverJoy.aButton.isBeingPressed())
					drive.wantShift(ClimbingState.NONE);
				else if (driverJoy.bButton.isBeingPressed())
					drive.wantShift(ClimbingState.FRONT);
				else if (driverJoy.xButton.isBeingPressed())
					drive.wantShift(ClimbingState.BOTH);
				else if (driverJoy.yButton.isBeingPressed())
					drive.wantShift(ClimbingState.REAR);
			}

		} else {
			if (driverJoy.aButton.wasActivated() && !driverJoy.xButton.isBeingPressed()) {
				drive.toggleSlowSpeed();
			}
		}

		if (driverJoy.POV180.shortReleased()) {
			supe.rocketHeight(QueueHeights.LOW);
		} else if (driverJoy.POV180.longPressed()) {
			supe.queueHeight(QueueHeights.LOW);
		} else if (driverJoy.POV270.shortReleased()) {
			supe.rocketHeight(QueueHeights.MIDDLE);
		} else if (driverJoy.POV270.longPressed()) {

			supe.queueHeight(QueueHeights.MIDDLE);
		} else if (driverJoy.POV0.shortReleased()) {
			supe.rocketHeight(QueueHeights.HIGH);
		} else if (driverJoy.POV0.longPressed()) {

			supe.queueHeight(QueueHeights.HIGH);
		} else if (driverJoy.xButton.wasActivated() && !driverJoy.leftBumper.isBeingPressed()) {
			supe.driverRetrieve();
		} else if (driverJoy.bButton.wasActivated()) {
			supe.setHeight(Heights.Cargo_Ship);
		} else if (driverJoy.rightBumper.wasActivated()) {
			supe.score(true);
		} else if (driverJoy.leftCenterClick.shortReleased()) {
			supe.intake();
		} else if (driverJoy.leftCenterClick.longPressed()) {
			supe.intakeEject();
		} else if (driverJoy.startButton.wasActivated()) {
			supe.stow();
		}

		if (driverJoy.backButton.longPressed()) {
			elev.toggleSpeedOverride();
		}

		drive.handleDriving(driverJoy);

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

		new LogItem("DRIVE-STATE") {
			@Override
			public String val() {
				return Drive.getInstance().getStateString();
			}
		};
		new LogItem("ELEV-STATE") {
			@Override
			public String val() {
				return Elevator.getInstance().getStateString();
			}
		};
		new LogItem("INTK-STATE") {
			@Override
			public String val() {
				return Intake.getInstance().getStateString();
			}
		};

		new LogItem("SUPR-STATE") {
			@Override
			public String val() {
				return Superstructure.getInstance().getStateString();
			}
		};

	}

	/**
	 * A physical key on the robot to shut off
	 */
	public boolean getSafteyKey() {
		return false;
		// return mKey.get();
	}

	public void setSafteyDisableForAllSystems(boolean disable) {
		this.mSafetyDisable = disable;
	}

	public boolean isFMS() {
		return DriverStation.getInstance().isFMSAttached();
	}

	public synchronized Alliance getAlliance() {
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

	public enum Level {
		LOW, MEDIUM, HIGH
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