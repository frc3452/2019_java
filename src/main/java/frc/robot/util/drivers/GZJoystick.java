package frc.robot.util.drivers;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.util.DPad;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;

public class GZJoystick extends Joystick {

	private DPad mUp, mDown, mRight, mLeft;
	private LatchedBoolean a = new LatchedBoolean(), b = new LatchedBoolean(), x = new LatchedBoolean(),
			y = new LatchedBoolean(), lb = new LatchedBoolean(), rb = new LatchedBoolean(), back = new LatchedBoolean(),
			start = new LatchedBoolean(), lclick = new LatchedBoolean(), rclick = new LatchedBoolean(),
			dUp = new LatchedBoolean(), dDown = new LatchedBoolean(), dLeft = new LatchedBoolean(),
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

	public boolean areButtonsHeld(int ...buttons) {
		boolean retval = true;

		for (Integer b : buttons)
			retval &= this.getRawButton(b);

		return retval;
	}

	public boolean isAnyButtonPressedThatIsnt(List <Integer> buttons)
	{
		List<Integer> temp = new ArrayList<Integer>();

		for (int i = Buttons.LOWEST_BUTTON; i <= Buttons.HIGHEST_BUTTON; i++)
			if (!buttons.contains(i))
				temp.add(i);

		return isAnyButtonPressed(temp);
	}

	public boolean isAnyButtonPressed(List<Integer> buttons) {
		boolean retval = false;

		for (Integer b : buttons) {
			retval |= this.getRawButton(b);
			if (retval)
				return retval;
		}

		return retval;
	}

	public Double getLeftAnalogY() {
		return GZUtil.applyDeadband(-this.getRawAxis(Axises.LEFT_ANALOG_Y), mDeadband);
	}

	public Double getLeftAnalogX() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.LEFT_ANALOG_X), mDeadband);
	}

	public Double getRightAnalogY() {
		return GZUtil.applyDeadband(-this.getRawAxis(Axises.RIGHT_ANALOG_Y), mDeadband);
	}

	public Double getRightAnalogX() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_ANALOG_X), mDeadband);
	}

	public Double getLeftTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.LEFT_TRIGGER), mDeadband);
	}

	public Double getRightTrigger() {
		return GZUtil.applyDeadband(this.getRawAxis(Axises.RIGHT_TRIGGER), mDeadband);
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

	public Boolean isAPressed() {
		return a.update(this.getRawButton(Buttons.A));
	}

	public Boolean isBPressed() {
		return b.update(this.getRawButton(Buttons.B));
	}

	public Boolean isXPressed() {
		return x.update(this.getRawButton(Buttons.X));
	}

	public Boolean isYPressed() {
		return y.update(this.getRawButton(Buttons.Y));
	}

	public Boolean isLBPressed() {
		return lb.update(this.getRawButton(Buttons.LB));
	}

	public Boolean isRBPressed() {
		return rb.update(this.getRawButton(Buttons.RB));
	}

	public Boolean isBackPressed() {
		return back.update(this.getRawButton(Buttons.BACK));
	}

	public Boolean isStartPressed() {
		return start.update(this.getRawButton(Buttons.START));
	}

	public Boolean isLClickPressed() {
		return lclick.update(this.getRawButton(Buttons.LEFT_CLICK));
	}

	public Boolean isRClickPressed() {
		return rclick.update(this.getRawButton(Buttons.RIGHT_CLICK));
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

	public static class Axises {
		public static int LEFT_ANALOG_X = 0;
		public static int LEFT_ANALOG_Y = 1;
		public static int RIGHT_ANALOG_X = 4;
		public static int RIGHT_ANALOG_Y = 5;
		public static int LEFT_TRIGGER = 2;
		public static int RIGHT_TRIGGER = 3;
	}

	public static class Buttons {
		public static int A = 1;
		public static int B = 2;
		public static int X = 3;
		public static int Y = 4;
		public static int LB = 5;
		public static int RB = 6;
		public static int BACK = 7;
		public static int START = 8;
		public static int LEFT_CLICK = 9;
		public static int RIGHT_CLICK = 10;

		private static int LOWEST_BUTTON = A;
		private static int HIGHEST_BUTTON = RIGHT_CLICK;
	}

	public void rumble(Double intensity) {
		this.setRumble(RumbleType.kLeftRumble, intensity);
		this.setRumble(RumbleType.kRightRumble, intensity);
	}

}
