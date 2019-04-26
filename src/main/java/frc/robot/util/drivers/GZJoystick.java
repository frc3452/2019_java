package frc.robot.util.drivers;

import java.util.Arrays;
import java.util.List;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.util.DPad;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;

public class GZJoystick extends Joystick {

	private double mTriggerPress = 0.3;

	private LatchedBoolean lbA = new LatchedBoolean(), lbB = new LatchedBoolean(), lbX = new LatchedBoolean(),
			lbY = new LatchedBoolean(), lbLB = new LatchedBoolean(), lbRB = new LatchedBoolean(),
			lbBack = new LatchedBoolean(), lbStart = new LatchedBoolean(), lbLClick = new LatchedBoolean(),
			lbRClick = new LatchedBoolean();

	private DPad mUp, mDown, mRight, mLeft;
	private LatchedBoolean dUp = new LatchedBoolean(), dDown = new LatchedBoolean(), dLeft = new LatchedBoolean(),
			dRight = new LatchedBoolean();

	private double mDeadband = 0.04;

	public GZJoystick(int port, double deadband) {
		this(port);
		this.mDeadband = deadband;
	}

	public GZJoystick(int port) {
		super(port);

		mUp = new DPad(this, 0);
		mDown = new DPad(this, 180);
		mLeft = new DPad(this, 270);
		mRight = new DPad(this, 90);
	}

	public boolean isAnyButtonPressedThatIsnt(Buttons... buttons) {

		List<Buttons> temp = allButtons;

		for (Buttons b : buttons)
			temp.remove(b);

		// array to list
		return anyButtons(temp.toArray(new Buttons[temp.size()]));
	}

	public boolean getButtons(Buttons... buttons) {
		boolean retval = true;

		for (Buttons b : buttons)
			retval &= this.getRawButton(b.val);

		return retval;
	}

	public boolean anyButtons(Buttons... buttons) {
		boolean retval = false;

		for (Buttons b : buttons) {
			retval |= this.getRawButton(b.val);
			if (retval)
				return retval;
		}

		return retval;
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

	public static class AnalogAngle {
		public final double magnitude;
		public final Rotation2d angle;
		public final double x, y;

		public AnalogAngle(double x, double y) {
			this.x = x;
			this.y = y;
			magnitude = Math.hypot(x, y);
			angle = new Rotation2d(x, y, true).inverse();
		}

		@Override
		public String toString() {
			String out = "X:" + x + "\tY: " + y + "\t" + angle.toString() + "\tMagnitude [" + magnitude + "]";
			return out;
		}
	}

	public AnalogAngle getLeftAnalogAngle() {
		return new AnalogAngle(getLeftAnalogX(), getLeftAnalogY());
	}

	public AnalogAngle getRightAnalogAngle() {
		return new AnalogAngle(getRightAnalogX(), getRightAnalogY());
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

	public Boolean getLeftTriggerPressed() {
		return Math.abs(getLeftTrigger()) > mTriggerPress;
	}

	public Boolean getRightTriggerPressed() {
		return Math.abs(getRightTrigger()) > mTriggerPress;
	}

	public Boolean getButton(Buttons b) {
		return this.getRawButton(b.val);
	}

	public Boolean getButtonLatched(Buttons b) {
		final boolean v = getButton(b);

		switch (b) {
		case A:
			return lbA.update(v);
		case B:
			return lbB.update(v);
		case X:
			return lbX.update(v);
		case Y:
			return lbY.update(v);
		case LB:
			return lbLB.update(v);
		case RB:
			return lbRB.update(v);
		case BACK:
			return lbBack.update(v);
		case START:
			return lbStart.update(v);
		case LEFT_CLICK:
			return lbLClick.update(v);
		case RIGHT_CLICK:
			return lbRClick.update(v);
		default: {
			System.out.println("GZJOYSTICK LATCHED BOOLEAN FALLTHROUGH " + b);
			return false;
		}
		}
	}

	public void check() {
		String out = "";
		for (Buttons b : allButtons) {
			out += getButtonLatched(b) + "\t";
		}
		System.out.println(out);
	}

	public Boolean isDUpPressed() {
		return dUp.update(mUp.get());
	}

	public Boolean isDDownPressed() {
		return dDown.update(mDown.get());
	}

	public Boolean isDLeftPressed() {
		return dLeft.update(mLeft.get());
	}

	public Boolean isDRightPressed() {
		return dRight.update(mRight.get());
	}

	public Boolean getDUp() {
		return this.mUp.get();
	}

	public Boolean getDDown() {
		return this.mDown.get();
	}

	public Boolean getDLeft() {
		return this.mLeft.get();
	}

	public Boolean getDRight() {
		return this.mRight.get();
	}

	public static enum Axises {
		LEFT_ANALOG_X(0), LEFT_ANALOG_Y(1), RIGHT_ANALOG_X(4), RIGHT_ANALOG_Y(5), LEFT_TRIGGER(2), RIGHT_TRIGGER(3);

		public final int val;

		private Axises(int val) {
			this.val = val;
		}
	}

	private static final List<Buttons> allButtons = Arrays.asList(Buttons.A, Buttons.B, Buttons.X, Buttons.Y,
			Buttons.LB, Buttons.RB, Buttons.BACK, Buttons.START, Buttons.LEFT_CLICK, Buttons.RIGHT_CLICK);

	public static enum Buttons {
		A(1), B(2), X(3), Y(4), LB(5), RB(6), BACK(7), START(8), LEFT_CLICK(9), RIGHT_CLICK(10);

		public final int val;

		private Buttons(int val) {
			this.val = val;
		}
	}

	public void rumble(Double intensity) {
		this.setRumble(RumbleType.kLeftRumble, intensity);
		this.setRumble(RumbleType.kRightRumble, intensity);
	}

}
