package frc.robot.util.drivers;

import java.util.Arrays;
import java.util.List;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.util.DPad;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;

public class GZJoystick extends Joystick {

	private DPad mUp, mDown, mRight, mLeft;
	private LatchedBoolean dUp = new LatchedBoolean(), dDown = new LatchedBoolean(), dLeft = new LatchedBoolean(),
			dRight = new LatchedBoolean();

	private double mDeadband = .04;

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

	public Double getRightAnalogX() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_ANALOG_X.val), mDeadband);
	}

	public Double getLeftTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.LEFT_TRIGGER.val), mDeadband);
	}

	public Double getRightTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_TRIGGER.val), mDeadband);
	}

	public Boolean getLeftTriggerPressed()
	{
		return Math.abs(getLeftTrigger()) > .3;
	}

	public Boolean getRightTriggerPressed()
	{
		return Math.abs(getRightTrigger()) > .3;
	}

	public Boolean getButton(Buttons b) {
		return this.getRawButton(b.val);
	}

	public Boolean getButtonLatched(Buttons b)
	{
		return this.getRawButtonPressed(b.val);
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
