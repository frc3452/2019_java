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
import frc.robot.util.drivers.GZJoystick.Buttons;
import frc.robot.util.drivers.controllers.DeepSpaceController;
import frc.robot.util.drivers.controllers.DriverController;
import frc.robot.util.drivers.controllers.OperatorController;

public class GZOI extends GZSubsystem {
	public static DriverController driverJoy = new DriverController(.09);
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
			// else if (drive.usingCurvature())
			// return .4;

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

		op.setXboxController();
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
			Auton.getInstance().controllerCancel(driverJoy.getButtons(Buttons.A, Buttons.X));
		} else if (isAuto() || isTele()) { // not running auto command and in sandstorm or tele
			handleControls();
		}
	}

	public void handleControls() {
		// handleSuperStructureControl(driverJoy);
		handleSuperStructureControl(op);
		handleDriverController();
		handleRumble();
		// handleElevatorTesting();
	}

	private void handleSuperStructureControl(OperatorController op) {
		if (op.cancel.get()) {
			supe.cancel();
		} else if (op.elevatorZero.get()) {
			supe.zeroElevator();
		} else if (op.hatchPanel1.updated()) {
			supe.setHeight(Heights.HP_1);
		} else if (op.hatchPanel2.updated()) {
			supe.setHeight(Heights.HP_2);
		} else if (op.hatchPanel3.updated()) {
			supe.setHeight(Heights.HP_3);
		} else if (op.cargo1.updated()) {
			supe.setHeight(Heights.Cargo_1);
		} else if (op.cargo2.updated()) {
			supe.setHeight(Heights.Cargo_2);
		} else if (op.cargo3.updated()) {
			supe.setHeight(Heights.Cargo_3);
		} else if (op.cargoShip.updated()) {
			supe.setHeight(Heights.Cargo_Ship);
		} else if (op.clawToggle.updated()) {
			supe.toggleClaw();
		} else if (op.slidesToggle.updated()) {
			supe.toggleSlides();
		} else if (op.elevatorJogDown.updated()) {
			supe.jogElevator(-1);
		} else if (op.elevatorJogUp.updated()) {
			supe.jogElevator(1);
		} else if (op.retrieve.updated()) {
			supe.retrieve();
		} else if (op.score.updated()) {
			supe.score();
		} else if (op.dropCrawler.updated()) {
			supe.dropCrawler();
		}
	}

	public void addRumble(double onTime, double offTime, int times) {
		addRumble(1.0, onTime, offTime, times, false);
	}

	public void addRumble(double onTime, double offTime, int times, boolean clear) {
		addRumble(1.0, onTime, offTime, times, clear);
	}

	public void addRumble(Double value, double onTime, double offTime, int times, boolean clearQueue) {
		if (clearQueue)
			mRumbleQueue.clear();
		mRumbleQueue.addToQueue(value, onTime, 0.0, offTime, times);
	}

	public void addRumble(Double value, double onTime) {
		mRumbleQueue.addToQueue(value, onTime, 1);
	}

	private void handleRumble() {
		// CONTROLLER RUMBLE
		rumble(mRumbleQueue.update());
	}

	private void disabled() {
		Auton.getInstance().autonChooser();
		// auton.print();

		Auton.getInstance().toggleAutoWait(driverJoy.getButtons(Buttons.A, Buttons.Y));
		Auton.getInstance().toggleAutoGamePiece(driverJoy.getButtons(Buttons.A, Buttons.X));

		rumble(0);
		// handleRumble();

		// if (driverJoy.getButtons(Buttons.LB, Buttons.LEFT_CLICK, Buttons.RIGHT_CLICK,
		// Buttons.X))
		// op.setButtonBoard();
		// else if (driverJoy.getButtons(Buttons.LB, Buttons.LEFT_CLICK,
		// Buttons.RIGHT_CLICK, Buttons.Y))
		// op.setXboxController();
	}

	private void handleElevatorTesting() {
		if (Math.abs(op.getLeftTrigger()) > .5)
			Elevator.getInstance().manual(op.getRightAnalogY() * .25);
		else if (op.getButtonLatched(Buttons.A)) {
			Elevator.getInstance().zero();
		}
	}

	// Driver variables

	private void handleDriverController() {
		if (driverJoy.getButton(Buttons.LB)) {

			if (!kDrivetrain.NO_SHIFTER) {
				if (driverJoy.getButton(Buttons.A))
					drive.wantShift(ClimbingState.NONE);
				else if (driverJoy.getButton(Buttons.B))
					drive.wantShift(ClimbingState.FRONT);
				else if (driverJoy.getButton(Buttons.X))
					drive.wantShift(ClimbingState.BOTH);
				else if (driverJoy.getButton(Buttons.Y))
					drive.wantShift(ClimbingState.REAR);
			}

		} else {
			if (driverJoy.getButtonLatched(Buttons.A) && !driverJoy.getButton(Buttons.X)) {
				drive.toggleSlowSpeed();
			}
		}

		if (driverJoy.isDDownPressed()) {
			supe.rocketHeight(RocketHeight.LOW);
		} else if (driverJoy.isDLeftPressed()) {
			supe.rocketHeight(RocketHeight.MIDDLE);
		} else if (driverJoy.isDUpPressed()) {
			supe.rocketHeight(RocketHeight.HIGH);
		} else if (driverJoy.getButtonLatched(Buttons.START)) {
			if (!Intake.getInstance().isExtended()) {
				supe.advanceFeederStage();
			} else {
				supe.handOffCargo();
			}
		} else if (driverJoy.getButtonLatched(Buttons.RB)) {
			supe.score();
		} else if (driverJoy.getButtonLatched(Buttons.LEFT_CLICK)) {
			supe.intake();
		}

		if (driverJoy.getButtonLatched(Buttons.BACK))
			elev.toggleSpeedOverride();

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

	private static void rumble(double intensity) {
		driverJoy.rumble(intensity);
		// op.rumble(intensity);
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

	public void alert(Level level) {
		addRumble(level);
		// Lights.getInstance().addAlert(level);
	}

	public void addRumble(Level r) {
		switch (r) {
		case LOW:
			addRumble(.125, .06, 1, true);
			break;
		case MEDIUM:
			addRumble(.125 / 3.0, .02, 2, true);
			break;
		case HIGH:
			addRumble(.24, .07, 3, true);
			break;
		}
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