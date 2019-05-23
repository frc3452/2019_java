package frc.robot.util.drivers;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.util.GZUtil;
import frc.robot.util.requests.Request;

public class GZJoystick extends Joystick {

	private double mTriggerPress = 0.3;

	private double mDeadband;

	public boolean rumbling;

	public GZJoystick(int port, double deadband) {
		super(port);
		this.mDeadband = deadband;
		aButton = new ButtonCheck(A_BUTTON);
		bButton = new ButtonCheck(B_BUTTON);
		xButton = new ButtonCheck(X_BUTTON);
		yButton = new ButtonCheck(Y_BUTTON);
		startButton = new ButtonCheck(START_BUTTON);
		backButton = new ButtonCheck(BACK_BUTTON);
		leftBumper = new ButtonCheck(LEFT_BUMPER);
		rightBumper = new ButtonCheck(RIGHT_BUMPER);
		leftCenterClick = new ButtonCheck(LEFT_CENTER_CLICK);
		rightCenterClick = new ButtonCheck(RIGHT_CENTER_CLICK);
		leftTrigger = new ButtonCheck(LEFT_TRIGGER);
		rightTrigger = new ButtonCheck(RIGHT_TRIGGER);
		POV0 = new ButtonCheck(POV_0);
		POV90 = new ButtonCheck(POV_90);
		POV180 = new ButtonCheck(POV_180);
		POV270 = new ButtonCheck(POV_270);
	}

	public GZJoystick(int port) {
		this(port, 0.04);
	}

	public Double getLeftAnalogY() {
		return GZUtil.applyDeadband(-this.getRawAxis(Axises.LEFT_ANALOG_Y.val), mDeadband);
	}

	public Double getLeftAnalogX() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.LEFT_ANALOG_X.val), mDeadband);
	}

	public Double getRightAnalogY() {
		return GZUtil.applyDeadband(-this.getRawAxis(Axises.RIGHT_ANALOG_Y.val), mDeadband);
	}

	public Translation2d getLeftAnalogAngle() {
		return new Translation2d(getLeftAnalogX(), getLeftAnalogY());
	}

	public Translation2d getRightAnalogAngle() {
		return new Translation2d(getRightAnalogX(), getRightAnalogY());
	}

	public Double getRightAnalogX() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_ANALOG_X.val), mDeadband);
	}

	public Double getLeftTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.LEFT_TRIGGER.val), mDeadband);
	}

	public Double getRightTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_TRIGGER.val), mDeadband);
	}

	public ButtonCheck aButton, bButton, xButton, yButton, startButton, backButton, leftBumper, rightBumper,
			leftCenterClick, rightCenterClick, leftTrigger, rightTrigger, POV0, POV90, POV180, POV270;
	public static final int A_BUTTON = 1;
	public static final int B_BUTTON = 2;
	public static final int X_BUTTON = 3;
	public static final int Y_BUTTON = 4;
	public static final int LEFT_BUMPER = 5;
	public static final int RIGHT_BUMPER = 6;
	public static final int BACK_BUTTON = 7;
	public static final int START_BUTTON = 8;
	public static final int LEFT_CENTER_CLICK = 9;
	public static final int RIGHT_CENTER_CLICK = 10;
	public static final int LEFT_TRIGGER = -2;
	public static final int RIGHT_TRIGGER = -3;
	public static final int POV_0 = -4;
	public static final int POV_90 = -5;
	public static final int POV_180 = -6;
	public static final int POV_270 = -7;
	public static final int SUPPLIER = -10;

	public static enum Axises {
		LEFT_ANALOG_X(0), LEFT_ANALOG_Y(1), RIGHT_ANALOG_X(4), RIGHT_ANALOG_Y(5), LEFT_TRIGGER(2), RIGHT_TRIGGER(3);

		public final int val;

		private Axises(int val) {
			this.val = val;
		}
	}

	public void rumble(double rumblesPerSecond, double numberOfSeconds) {
		if (!rumbling) {
			RumbleThread r = new RumbleThread(rumblesPerSecond, numberOfSeconds);
			r.start();
		}
	}

	public boolean isRumbling() {
		return rumbling;
	}

	public Request rumbleRequest(double rumblesPerSecond, double numberOfSeconds) {
		return new Request() {

			@Override
			public void act() {
				rumble(rumblesPerSecond, numberOfSeconds);
			}
		};
	}

	public class RumbleThread extends Thread {
		public double rumblesPerSec = 1;
		public long interval = 500;
		public double seconds = 1;
		public double startTime = 0;

		public RumbleThread(double rumblesPerSecond, double numberOfSeconds) {
			rumblesPerSec = rumblesPerSecond;
			seconds = numberOfSeconds;
			interval = (long) (1 / (rumblesPerSec * 2) * 1000);
		}

		public void run() {
			rumbling = true;
			startTime = Timer.getFPGATimestamp();
			try {
				while ((Timer.getFPGATimestamp() - startTime) < seconds) {
					setRumble(RumbleType.kLeftRumble, 1);
					setRumble(RumbleType.kRightRumble, 1);
					sleep(interval);
					setRumble(RumbleType.kLeftRumble, 0);
					setRumble(RumbleType.kRightRumble, 0);
					sleep(interval);
				}
			} catch (InterruptedException e) {
				rumbling = false;
				e.printStackTrace();
			}
			rumbling = false;
		}
	}

	public void setRumble(double intensity) {
		this.setRumble(RumbleType.kLeftRumble, intensity);
		this.setRumble(RumbleType.kRightRumble, intensity);
	}

	public class ButtonCheck {
		boolean buttonCheck = false;
		boolean buttonActive = false;
		boolean activationReported = false;
		boolean longPressed = false;
		boolean longPressActivated = false;
		boolean hasBeenPressed = false;
		boolean longReleased = false;
		private double buttonStartTime = 0;
		private double longPressDuration = 0.25;

		public void setLongPressDuration(double seconds) {
			longPressDuration = seconds;
		}

		private int buttonNumber;

		public ButtonCheck(int id) {
			buttonNumber = id;
		}

		private Supplier<Boolean> supplier;

		public ButtonCheck(Supplier<Boolean> supplier) {
			this.supplier = supplier;
			buttonNumber = SUPPLIER;
		}

		public void update() {
			if (buttonNumber > 0) {
				buttonCheck = getRawButton(buttonNumber);
			} else {
				switch (buttonNumber) {
				case SUPPLIER:
					buttonCheck = supplier.get();
					break;
				case LEFT_TRIGGER:
					buttonCheck = getLeftTrigger() > 0;
					break;
				case RIGHT_TRIGGER:
					buttonCheck = getRightTrigger() > 0;
					break;
				case POV_0:
					buttonCheck = (getPOV() == 0);
					break;
				case POV_90:
					buttonCheck = (getPOV() == 90);
					break;
				case POV_180:
					buttonCheck = (getPOV() == 180);
					break;
				case POV_270:
					buttonCheck = (getPOV() == 270);
					break;
				default:
					buttonCheck = false;
					break;
				}
			}
			if (buttonCheck) {
				if (buttonActive) {
					if (((Timer.getFPGATimestamp() - buttonStartTime) > longPressDuration) && !longPressActivated) {
						longPressActivated = true;
						longPressed = true;
						longReleased = false;
					}
				} else {
					buttonActive = true;
					activationReported = false;
					buttonStartTime = Timer.getFPGATimestamp();
				}
			} else {
				if (buttonActive) {
					buttonActive = false;
					activationReported = true;
					if (longPressActivated) {
						hasBeenPressed = false;
						longPressActivated = false;
						longPressed = false;
						longReleased = true;
					} else {
						hasBeenPressed = true;
					}
				}
			}
		}

		/**
		 * Returns true once the button is pressed, regardless of the activation
		 * duration. Only returns true one time per button press, and is reset upon
		 * release.
		 */
		public boolean wasActivated() {
			if (buttonActive && !activationReported) {
				activationReported = true;
				return true;
			}
			return false;
		}

		/**
		 * Returns true once the button is released after being held for 0.25 seconds or
		 * less. Only returns true one time per button press.
		 */
		public boolean shortReleased() {
			if (hasBeenPressed) {
				hasBeenPressed = false;
				return true;
			}
			return false;
		}

		/**
		 * Returns true once if the button is pressed for more than 0.25 seconds. Only
		 * true while the button is still depressed; it becomes false once the button is
		 * released.
		 */
		public boolean longPressed() {
			if (longPressed) {
				longPressed = false;
				return true;
			}
			return false;
		}

		public boolean isBeingLongPressed() {
			return longPressed;
		}

		/**
		 * Returns true one time once the button is released after being held for more
		 * than 0.25 seconds.
		 */
		public boolean longReleased() {
			if (longReleased) {
				longReleased = false;
				return true;
			}
			return false;
		}

		/**
		 * Returns true once the button is released, regardless of activation duration.
		 */
		public boolean wasReleased() {
			return shortReleased() || longReleased();
		}

		/** Returns true if the button is currently being pressed. */
		public boolean isBeingPressed() {
			return buttonActive;
		}
	}

	public void update() {
		aButton.update();
		bButton.update();
		xButton.update();
		yButton.update();
		startButton.update();
		backButton.update();
		leftBumper.update();
		rightBumper.update();
		leftCenterClick.update();
		rightCenterClick.update();
		leftTrigger.update();
		rightTrigger.update();
		POV0.update();
		POV90.update();
		POV180.update();
		POV270.update();
	}
}
