package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Arrays;

import com.ctre.phoenix.CANifier;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZQueuer;
import frc.robot.util.GZQueuer.TimeValue;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.motorcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;

public class Lights extends GZSubsystem {
	private static CANifier canifier;

	private static Lights mInstance = null;

	private LightState mState = LightState.OFF;
	private LightState mPrevState = LightState.SOLID;
	private LightState mStateOnHold = LightState.OFF;

	private Colors mCurrentColor = Colors.OFF;
	private Colors mColor = Colors.OFF;

	private ArrayList<Colors> mColorFade = new ArrayList<>();
	private double mPrevTimeStamp;
	private double mFadeTime;
	private double mTimeInFade;
	private double rIntegral, gIntegral, bIntegral;
	private int mFadeSlot, mPrevFadeSlot;

	public synchronized static Lights getInstance() {
		if (mInstance == null)
			mInstance = new Lights();

		return mInstance;
	}

	private GZQueuer<Colors> mLightQueuer = new GZQueuer<Lights.Colors>() {
		@Override
		public Colors getDefault() {
			return new Colors(0, 0, 0);
		}

		@Override
		public void onEmpty() {
		}
	};

	private Lights() {
		canifier = new CANifier(Constants.kLights.CANIFIER_ID);
		GZSRX.logError(canifier.configFactoryDefault(), this, AlertLevel.WARNING, "Canifier not found");
	}

	@Override
	public void loop() {
		handleStates();
	}

	public void setFade(double time, Colors... colors) {
		this.mFadeTime = time;
		mColorFade = (ArrayList<Colors>) Arrays.asList(colors);
		mState = LightState.FADE;
	}

	private void handleFade() {
		if (mPrevFadeSlot != mFadeSlot) {
			mTimeInFade = 0;
			rIntegral = (mColorFade.get(mFadeSlot).r - mCurrentColor.r) / mFadeTime;
			gIntegral = (mColorFade.get(mFadeSlot).g - mCurrentColor.g) / mFadeTime;
			bIntegral = (mColorFade.get(mFadeSlot).b - mCurrentColor.b) / mFadeTime;
		}
		mPrevFadeSlot = mFadeSlot;

		mCurrentColor.addR(rIntegral);
		mCurrentColor.addG(gIntegral);
		mCurrentColor.addB(bIntegral);

		final double now = Timer.getFPGATimestamp();
		mTimeInFade += Timer.getFPGATimestamp() - mPrevTimeStamp;
		mPrevTimeStamp = now;

		if (mTimeInFade > mFadeTime) {
			mFadeSlot++;
		}

		if (mFadeSlot > mColorFade.size() - 1)
			mFadeSlot = 0;
	}

	private void exitState(LightState s) {
		switch (s) {
		case BLINK:
			mState = mStateOnHold;
			break;
		}
	}

	private void enterState(LightState s) {
		switch (s) {
		case BLINK:
			mStateOnHold = mPrevState;
			break;
		}
	}

	private void handleStateChange() {
		if (mPrevState != mState) {
			exitState(mPrevState);
			enterState(mState);
		}
		mPrevState = mState;
	}

	private synchronized void handleStates() {

		if (!mLightQueuer.isQueueEmpty()) {
			mState = LightState.BLINK;
		}

		handleStateChange();

		switch (mState) {
		case OFF:
			rgb(Colors.OFF);
			break;
		case BLINK:
			rgb(mLightQueuer.update());
			break;
		case FADE:
			handleFade();
			break;
		case SOLID:
			rgb(mColor);
			break;
		}
	}

	public void setSolidColor(Colors color) {
		mState = LightState.SOLID;
		this.mColor = color;
	}

	public void blink(TimeValue<Colors> color, double offTime, int times) {
		blink(color, offTime, times, false);
	}

	public void blink(TimeValue<Colors> color, double offTime, int times, boolean clear) {
		if (clear)
			mLightQueuer.clear();
		mLightQueuer.addToQueue(color, new TimeValue<Colors>(Colors.OFF, offTime), times);
	}

	private void off() {
		rgb(0, 0, 0);
	}

	private void hsv(double hDegrees, double saturation, double value) {
		double R, G, B;
		double H = hDegrees;

		if (H < 0) {
			H += 360;
		}
		if (H >= 360) {
			H -= 360;
		}

		if (value <= 0) {
			R = G = B = 0;
		} else if (saturation <= 0) {
			R = G = B = value;
		} else {
			double hf = H / 60.0;
			int i = (int) Math.floor(hf);
			double f = hf - i;
			double pv = value * (1 - saturation);
			double qv = value * (1 - saturation * f);
			double tv = value * (1 - saturation * (1 - f));
			switch (i) {
			/* Red is dominant color */
			case 0:
				R = value;
				G = tv;
				B = pv;
				break;
			/* Green is dominant color */
			case 1:
				R = qv;
				G = value;
				B = pv;
				break;
			case 2:
				R = pv;
				G = value;
				B = tv;
				break;
			/* Blue is the dominant color */
			case 3:
				R = pv;
				G = qv;
				B = value;
				break;
			case 4:
				R = tv;
				G = pv;
				B = value;
				break;
			/* Red is the dominant color */
			case 5:
				R = value;
				G = pv;
				B = qv;
				break;
			/*
			 * Just in case we overshoot on our math by a little, we put these here. Since
			 * its a switch it won't slow us down at all to put these here
			 */
			case 6:
				R = value;
				G = tv;
				B = pv;
				break;
			case -1:
				R = value;
				G = pv;
				B = qv;
				break;
			/* The color is not defined, we should throw an error */
			default:
				/* Just pretend its black/white */
				R = G = B = value;
				break;
			}
		}
		rgb((float) R, (float) G, (float) B);
	}

	private void rgb(Colors color) {
		rgb(color.r, color.g, color.b);
	}

	private void rgb(double red, double green, double blue) {
		try {
			canifier.setLEDOutput(red, CANifier.LEDChannel.LEDChannelA);
			canifier.setLEDOutput(green, CANifier.LEDChannel.LEDChannelB);
			canifier.setLEDOutput(blue, CANifier.LEDChannel.LEDChannelC);

			mCurrentColor.set(red, green, blue);
		} catch (Exception e) {
		}
	}

	public void stop() {
	}

	public String getStateString() {
		return "NA";
	}

	protected void in() {
	}

	protected void out() {
	}

	public String getSmallString() {
		return "LGHT";
	}

	protected void initDefaultCommand() {
	}

	@Override
	public void addLoggingValues() {
	}

	public enum LightState {
		OFF, BLINK, FADE, SOLID
	}

	public static class Colors {

		public final static Colors OFF = Colors.get(0, 0, 0);
		// public final static Color GREEN = new Color(32, 94, 95);
		public final static Colors GREEN = Colors.get(0, 255, 0);
		public final static Colors RED = Colors.get(255, 0, 0);
		public final static Colors BLUE = Colors.get(0, 0, 255);

		private double r, g, b;

		public Colors(double r, double g, double b) {
			set(r, g, b);
		}

		public static Colors get(double r, double g, double b) {
			return new Colors(r, g, b);
		}

		public void set(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public void addR(double rAdd) {
			this.r += rAdd;
		}

		public void addG(double gAdd) {
			this.g += gAdd;
		}

		public void addB(double bAdd) {
			this.b += bAdd;
		}

	}

}
