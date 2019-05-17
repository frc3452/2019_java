package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator.RocketHeight;
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
import frc.robot.util.GZQueuer;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.GZJoystick;
import frc.robot.util.drivers.controllers.OperatorController;

public class GZOI extends GZSubsystem {
	public static GZJoystick driverJoy = new GZJoystick(0, .09);
	public static OperatorController op = new OperatorController();

	// private GZSolenoid mLeds;

	private UsbCamera mCamera;

	// private GZAnalogInput mKey = new GZAnalogInput(this, "Lockout key",
	// kOI.LOCK_OUT_KEY, kOI.LOCK_OUT_KEY_VOLT);

	private LatchedBoolean mUserButton = new LatchedBoolean();

	private boolean mWasTele = false, mWasAuto = false, mWasTest = false;

	private int mDisabledPrintOutLoops = 0;
	private boolean mSafetyDisable = false;
	private BooleanStateChange mDisabledStateChange = new BooleanStateChange();

	private Drive drive = Drive.getInstance();
	private Elevator elev = Elevator.getInstance();
	private Superstructure supe = Superstructure.getInstance();

	// private Auton auton = Auton.getInstance();

	private GZQueuer<Double> mRumbleQueue = new GZQueuer<Double>() {
		@Override
		public Double getDefault() {
			if (elev.isSpeedOverriden())
				return 0.45;
			else if (!drive.isSlow())
				return 0.10;
			else if (GZUtil.between(getMatchTime(), 29.1, 30))
				return 0.6;
			return 0.0;
		}

		@Override
		public void onEmpty() {
		}
	};

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

		// driverJoy.check();

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
		if (isFMS())
			mSafetyDisable = false;
		else if (getSafteyKey())
			mSafetyDisable = true;
		else if (mUserButton.update(RobotController.getUserButton()))
			mSafetyDisable = !mSafetyDisable;

		if (mDisabledStateChange.update(mSafetyDisable)) {
			Robot.allSubsystems.disable(mSafetyDisable);
			System.out.println("WARNING All subsystems " + (mSafetyDisable ? "disabled" : "enabled") + "!");
		}

		if (mSafetyDisable) {
			if (++mDisabledPrintOutLoops > 300) {
				System.out.println("ERROR All subsystems disabled, check Saftey Key or toggle UserButton");
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
		// handleSuperStructureControl(driverJoy);
		handleSuperStructureControl();
		handleDriverController();
	}

	private void handleSuperStructureControl() {
		if (op.xButton.isBeingPressed()) {
			supe.zeroElevator();
		} else if (op.hatchPanel1.wasActivated()) {
			supe.setHeight(Heights.HP_1);
		} else if (op.hatchPanel2.wasActivated()) {
			supe.setHeight(Heights.HP_2);
		} else if (op.hatchPanel3.wasActivated()) {
			supe.setHeight(Heights.HP_3);
		} else if (op.cargo1.wasActivated()) {
			supe.setHeight(Heights.Cargo_1);
		} else if (op.cargo2.wasActivated()) {
			supe.setHeight(Heights.Cargo_2);
		} else if (op.cargo3.wasActivated()) {
			supe.setHeight(Heights.Cargo_3);
		} else if (op.startButton.wasActivated()) {
			supe.setHeight(Heights.Cargo_Ship);
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

	private void disabled() {
		Auton.getInstance().autonChooser();
		// auton.print();

		Auton.getInstance().toggleAutoWait(driverJoy.aButton.isBeingPressed() && driverJoy.yButton.isBeingPressed());
		Auton.getInstance()
				.toggleAutoGamePiece(driverJoy.aButton.isBeingPressed() && driverJoy.xButton.isBeingPressed());

		rumble(0.0);
		// handleRumble();

		// if (driverJoy.getButtons(Buttons.LB, Buttons.LEFT_CLICK, Buttons.RIGHT_CLICK,
		// Buttons.X))
		// op.setButtonBoard();
		// else if (driverJoy.getButtons(Buttons.LB, Buttons.LEFT_CLICK,
		// Buttons.RIGHT_CLICK, Buttons.Y))
		// op.setXboxController();
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

		if (driverJoy.POV180.wasActivated()) {
			supe.rocketHeight(RocketHeight.LOW);
		} else if (driverJoy.POV270.wasActivated()) {
			supe.rocketHeight(RocketHeight.MIDDLE);
		} else if (driverJoy.POV90.wasActivated()) {
			supe.rocketHeight(RocketHeight.HIGH);
		} else if (driverJoy.startButton.wasActivated()) {
			if (!Intake.getInstance().isExtended()) {
				supe.advanceFeederStage();
			} else {
				supe.handOffCargo();
			}
		} else if (driverJoy.rightBumper.wasActivated()) {
			supe.score();
		} else if (driverJoy.leftCenterClick.wasActivated()) {
			supe.intake();
		}

		if (driverJoy.backButton.longPressed())
			elev.toggleSpeedOverride();
		else if (driverJoy.backButton.shortReleased()) {
			supe.intakeEject();
		}

		drive.handleDriving(driverJoy);

		if (driverJoy.POV90.wasActivated()) {
			drive.turnToHeading(Rotation2d.fromDegrees(180));
		}
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