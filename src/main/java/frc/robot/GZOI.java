package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kOI;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZLog;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZTimer;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.drivers.GZJoystick.Buttons;
import frc.robot.util.drivers.controllers.DeepSpaceController;
import frc.robot.util.drivers.controllers.DriverController;
import frc.robot.util.drivers.controllers.OperatorController;

public class GZOI extends GZSubsystem {
	public static DriverController driverJoy = new DriverController();
	public static OperatorController op = new OperatorController();
	private boolean mWasTele = false, mWasAuto = false, mWasTest = false;

	private LatchedBoolean mUserButton = new LatchedBoolean();
	private boolean mSafetyDisable = false;

	private Drive drive = Drive.getInstance();
	private Superstructure supe = Superstructure.getInstance();
	private Auton auton = Auton.getInstance();

	private static GZOI mInstance = null;

	private GZTimer t = new GZTimer();

	public static GZOI getInstance() {
		if (mInstance == null)
			mInstance = new GZOI();

		return mInstance;
	}

	private GZOI() {
		t.startTimer();
	}

	boolean bToggled = false;

	@Override
	public void loop() {
		// FLAGS
		if (isTele())
			mWasTele = true;
		else if (isAuto())
			mWasAuto = true;
		else if (isTest())
			mWasTest = true;

		// Disabled
		if (isDisabled())
			disabled();
		else if (auton.isAutoControl()) { // running auto command
			auton.controllerStart(driverJoy.getButtons(Buttons.A, Buttons.B));
			auton.controllerCancel(driverJoy.getButtons(Buttons.A, Buttons.X));
		} else if (isAuto() || isTele()) { // not running auto command and in sandstorm or tele
			handleSuperStructureControl(driverJoy);
			handleSuperStructureControl(op);
			handleDriverController();
			handleRumble();
		}
	}

	private void handleRumble() {
		// CONTROLLER RUMBLE
		if (GZUtil.between(getMatchTime(), 29.1, 30))
			// ENDGAME
			rumble(kOI.Rumble.ENDGAME);
		else
			rumble(0);
	}

	private void disabled() {
		if (isFMS())
			mSafetyDisable = false;
		else if (mUserButton.update(RobotController.getUserButton()))
			mSafetyDisable = !mSafetyDisable;
		Robot.allSubsystems.disable(mSafetyDisable);

		auton.toggleAutoWait(driverJoy.getButtons(Buttons.A, Buttons.Y));

		if (driverJoy.getButtons(Buttons.LB, Buttons.RB, Buttons.LEFT_CLICK, Buttons.X))
			op.setButtonBoard();
		else if (driverJoy.getButtons(Buttons.LB, Buttons.RB, Buttons.LEFT_CLICK, Buttons.Y))
			op.setXboxController();
	}

	private void handleDriverController() {
		// Velocity testing
		if (driverJoy.getButtonLatched(Buttons.B))
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

		if (driverJoy.getButtonLatched(Buttons.A))
			drive.toggleSlowSpeed();
	}

	private void handleSuperStructureControl(DeepSpaceController controller) {
		final boolean queue = controller.queueAction.get();

		if (controller.hatchPannel1.get())
			supe.runHeight(Heights.HP_1, queue);
		else if (controller.hatchPanel2.get())
			supe.runHeight(Heights.HP_2, queue);
		else if (controller.hatchPanel3.get())
			supe.runHeight(Heights.HP_3, queue);
		else if (controller.hatchFromFeed.get())
			supe.runHeight(Heights.HP_1, queue);
		else if (controller.cargo1.get())
			supe.runHeight(Heights.Cargo_1, queue);
		else if (controller.cargo2.get())
			supe.runHeight(Heights.Cargo_2, queue);
		else if (controller.cargo3.get())
			supe.runHeight(Heights.Cargo_3, queue);
		else if (controller.cargoShip.get())
			supe.runHeight(Heights.Cargo_Ship, queue);
		else
			supe.elevatorNoManual();

		if (controller.intakeDown.get())
			supe.lowerIntake(true);
		else if (controller.intakeUp.get())
			supe.raiseIntake(true);
		else
			supe.intakeDropNoManual();

		if (controller.slidesIn.get())
			supe.retractSlides(true);
		else if (controller.slidesOut.get())
			supe.extendSlides(true);
		else
			supe.slidesNoManual();

		if (controller.clawOpen.get())
			supe.openClaw(true);
		else if (controller.clawClosed.get())
			supe.closeClaw(true);
		else
			supe.clawNoManual();

		if (controller.stow.updated())
			supe.runAction(Actions.STOW, queue);
		else if (controller.stowLow.updated())
			supe.runAction(Actions.STOW_LOW, queue);
		else if (controller.intakeCargo.updated())
			supe.runAction(Actions.INTAKE_CARGO, queue);
		else if (controller.floorHatchToManip.updated())
			supe.runAction(Actions.TRNSFR_HP_FROM_FLOOR, queue);
		else if (controller.hatchFromFeed.updated())
			supe.runAction(Actions.GRAB_HP_FROM_FEED, queue);
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