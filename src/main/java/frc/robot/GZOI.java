package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kOI;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.ClimbingState;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.BooleanStateChange;
import frc.robot.util.GZLog;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZQueuer;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.GZJoystick.Buttons;
import frc.robot.util.drivers.controllers.DeepSpaceController;
import frc.robot.util.drivers.controllers.DriverController;
import frc.robot.util.drivers.controllers.OperatorController;

public class GZOI extends GZSubsystem {
	public static DriverController driverJoy = new DriverController(.09);
	// public static GZJoystick driverJoy = new DriverController(.09);
	public static OperatorController op = new OperatorController();

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
				return .45;
			else if (drive.isAutoClimbing())
				return .15;
			else if (GZUtil.between(getMatchTime(), 29.1, 30))
				return .6;
			// else if (drive.usingCurvature())
			// 	return .4;

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
		op.setXboxController();
	}

	@Override
	public void loop() {
		// FLAGS
		if (isTele())
			mWasTele = true;
		else if (isAuto())
			mWasAuto = true;
		else if (isTest())
			mWasTest = true;

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

		if (mSafetyDisable)
			if (++mDisabledPrintOutLoops > 300) {
				System.out.println("ERROR All subsystems disabled, check Saftey Key or toggle UserButton");
				mDisabledPrintOutLoops = 0;
			}

		// Disabled
		if (isDisabled())
			disabled();
		else if (Auton.getInstance().isAutoControl()) { // running auto command
			Auton.getInstance().controllerStart(driverJoy.getButtons(Buttons.A, Buttons.B));
			Auton.getInstance().controllerCancel(driverJoy.getButtons(Buttons.A, Buttons.X));
		} else if (isAuto() || isTele()) { // not running auto command and in sandstorm or tele
			// handleSuperStructureControl(driverJoy);
			handleSuperStructureControl(op);
			handleDriverController();
			handleRumble();
			// handleElevatorTesting();
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
		rumble(mRumbleQueue.getDefault() != 0 ? mRumbleQueue.getDefault() : mRumbleQueue.update());
	}

	private void disabled() {
		Auton.getInstance().toggleAutoWait(driverJoy.getButtons(Buttons.A, Buttons.Y));

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
	}

	private void handleDriverController() {
		if (GZOI.driverJoy.getButtons(Buttons.BACK, Buttons.START)) {
			Elevator.getInstance().zero();
		}

		if (driverJoy.getButton(Buttons.LB)) {

			if (driverJoy.getButton(Buttons.A))
				drive.wantShift(ClimbingState.NONE);
			else if (driverJoy.getButton(Buttons.B))
				drive.wantShift(ClimbingState.FRONT);
			else if (driverJoy.getButton(Buttons.X))
				drive.wantShift(ClimbingState.BOTH);
				else if (driverJoy.getButton(Buttons.Y))
				drive.wantShift(ClimbingState.REAR);


		} else {
			if (driverJoy.getButtonLatched(Buttons.A)) {
				drive.toggleSlowSpeed();
			}
		}

		if (driverJoy.getButtonLatched(Buttons.BACK) && !driverJoy.getButton(Buttons.START))
			elev.toggleSpeedOverride();

		if (drive.getState() == DriveState.CLIMB && driverJoy.getButtonLatched(Buttons.RB))
			drive.toggleStraightClimb();

		drive.handleDriving(driverJoy);
	}

	private void handleSuperStructureControl(DeepSpaceController controller) {
		final boolean queue = controller.queueAction.get();

		if (controller.idle.get())
			supe.idle();
		else if (controller.hatchPannel1.pressedFor(0.75))
			supe.runHeight(Heights.Home);
		else if (controller.hatchPannel1.get())
			supe.runHeight(Heights.HP_1, queue);
		else if (controller.hatchPanel2.get())
			supe.runHeight(Heights.HP_2, queue);
		else if (controller.hatchPanel3.get())
			supe.runHeight(Heights.HP_3, queue);
		else if (controller.cargo1.get())
			supe.runHeight(Heights.Cargo_1, queue);
		else if (controller.cargo2.get())
			supe.runHeight(Heights.Cargo_2, queue);
		else if (controller.cargo3.get())
			supe.runHeight(Heights.Cargo_3, queue);
		else if (controller.elevatorJogDown.updated())
			supe.jog(-1.0);
		else if (controller.elevatorJogUp.updated())
			supe.jog(1.0);
		else if (controller.cargoShip.get())
			supe.runHeight(Heights.Cargo_Ship, queue);
		// else if (controller.elevatorManual.get())
		// supe.elevManual(controller.getRightAnalogY() * 0.25);

		if (controller.slidesToggle.updated())
			supe.toggleSlides();
		else if (controller.clawToggle.updated())
			supe.toggleClaw();

		if (controller.intakeCargo.updated())
			supe.runAction(Actions.INTAKE_CARGO, queue);
		else if (controller.intakeDown.updated())
			supe.lowerIntake();
		else if (controller.intakeUp.updated())
			supe.raiseIntake();
		else if (controller.stow.updated())
			supe.runAction(Actions.STOW, queue);

		if (controller.floorHatchToManip.updated())
			supe.runAction(Actions.TRNSFR_HP_FROM_FLOOR, queue);
		else if (controller.hatchFromFeed.updated())
			supe.runAction(Actions.GRAB_HP_FROM_FEED, queue);
		else if (controller.shootCargo.updated())
			supe.runAction(Actions.THROW_CARGO);
		else if (controller.scoreHatch.updated())
			supe.runAction(Actions.SCORE_HATCH);

		if (controller.dropCrawler.updated())
			supe.dropCrawler();
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
			addRumble(.125, .06, 1, false);
			break;
		case MEDIUM:
			addRumble(.125 / 3.0, .02, 2, false);
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