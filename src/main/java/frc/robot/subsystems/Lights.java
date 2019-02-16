package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;

import frc.robot.Constants;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZSubsystem;
import frc.robot.util.TheBumbler;
import frc.robot.util.TheBumbler.TimeValue;
import frc.robot.util.drivers.motorcontrollers.GZSRX;

public class Lights extends GZSubsystem {
	private static CANifier canifier;

	private static Lights mInstance = null;

	private LightState mState = LightState.OFF;

	private Color mColor = Color.OFF;

	private TheBumbler<Color> mLightQueuer = new TheBumbler<Lights.Color>() {
		@Override
		public Color getDefault() {
			return new Color(0, 0, 0);
		}
	};

	public synchronized static Lights getInstance() {
		if (mInstance == null)
			mInstance = new Lights();

		return mInstance;
	}

	private Lights() {
		canifier = new CANifier(Constants.kLights.CANIFIER_ID);
		GZSRX.logError(canifier.configFactoryDefault(), this, AlertLevel.WARNING, "Canifier not found");
	}

	@Override
	public void loop() {
		handleStates();
	}

	private void handleStates() {
		if (!mLightQueuer.isQueueEmpty()) {
			mState = LightState.BLINK;
		}

		switch (mState) {
		case OFF:
			rgb(Color.OFF);
			break;
		case BLINK:
			rgb(mLightQueuer.update());
			break;
		case FADE:
			break;
		case SOLID:
			rgb(mColor);
			break;
		}
	}

	public void setSolidColor(Color color) {
		this.mState = LightState.SOLID;
		this.mColor = color;
	}

	public void blink(TimeValue<Color> color, double offTime, int times) {
		blink(color, offTime, times, false);
	}

	public void blink(TimeValue<Color> color, double offTime, int times, boolean clear) {
		if (clear)
			mLightQueuer.clear();
		mLightQueuer.addToQueue(color, new TimeValue<Color>(Color.OFF, offTime), times);
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

	private void rgb(Color color) {
		rgb(color.r, color.g, color.b);
	}

	private void rgb(double red, double green, double blue) {
		try {
			canifier.setLEDOutput(red, CANifier.LEDChannel.LEDChannelA);
			canifier.setLEDOutput(green, CANifier.LEDChannel.LEDChannelB);
			canifier.setLEDOutput(blue, CANifier.LEDChannel.LEDChannelC);
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

	public static class Color {

		public final static Color OFF = Color.get(0, 0, 0);
		// public final static Color GREEN = new Color(32, 94, 95);
		public final static Color GREEN = Color.get(0, 255, 0);
		public final static Color RED = Color.get(255, 0, 0);
		public final static Color BLUE = Color.get(0, 0, 255);

		private double r, g, b;

		public Color(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public static Color get(double r, double g, double b) {
			return new Color(r, g, b);
		}
	}

}
