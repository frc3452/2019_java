package frc.robot.subsystems;

import java.util.Arrays;

import com.ctre.phoenix.CANifier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.Constants.kAuton;
import frc.robot.Constants.kLights;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZJoystick.Buttons;
import frc.robot.util.drivers.GZSRX;

public class Lights extends GZSubsystem {
	private static CANifier canifier;

	private boolean readyForMatch = false;

	public int m_hue = 225;
	private double pulseBrightness = 0;
	private boolean pulseDirection = true;

	private Timer lightTimer = new Timer();

	private NetworkTableEntry centerX;
	private NetworkTableEntry centerY;

	private double tempArray[] = new double[10];

	private static Lights mInstance = null;

	public synchronized static Lights getInstance() {
		if (mInstance == null)
			mInstance = new Lights();

		return mInstance;
	}
	public void addLoggingValues()
	{
		// D:
	}

	private Lights() {

		for (int i = 0; i < 10; i++)
			tempArray[i] = 3452;

		canifier = new CANifier(Constants.kLights.CANIFIER_ID);
		GZSRX.logError(canifier.configFactoryDefault(), this, AlertLevel.WARNING, "Canifier not found");

		lightTimer.stop();
		lightTimer.reset();
		lightTimer.start();

		NetworkTableInstance inst = NetworkTableInstance.getDefault();
		NetworkTable table = inst.getTable("/GRIP/vision");

		centerX = table.getEntry("centerX");
		centerY = table.getEntry("centerY");
	}

	@Override
	public void loop() {
		GZOI gzOI = GZOI.getInstance();

		outputSmartDashboard();

		if (!gzOI.isSafetyDisabled()) {

			if (GZOI.driverJoy.getButtons(Buttons.A, Buttons.B, Buttons.BACK))
				readyForMatch = true;

			if (GZOI.driverJoy.getButtons(Buttons.A, Buttons.B, Buttons.START))
				readyForMatch = false;

			if (gzOI.isTele()) {

				hsv(kLights.GREEN, 1, .5);

			} else if (gzOI.isAuto()) {

				hsv(gzOI.isRed() ? kLights.RED : kLights.BLUE, 1, .5);

			} else if (gzOI.isDisabled()) {

				switch (Auton.getInstance().uglyAnalog()) {
				case 100:

					// OFF
					off();

					break;

				case kAuton.SAFTEY_SWITCH:

					// FADE
					hsv(m_hue, 1, .25);
					m_hue++;

					break;
				case 97:

					// POLICE
					if (m_hue > 180)
						hsv(kLights.RED, 1, 1);
					else
						hsv(kLights.BLUE, 1, 1);
					m_hue += 30;

					break;
				default:

					// IF CONNECTED LOW GREEN
					if (DriverStation.getInstance().isDSAttached()) {

						if (readyForMatch)
							pulse(kLights.GREEN, 1, 0.1, .4, 0.025 / 3.5);
						else
							pulse(kLights.YELLOW, 1, 0.1, .4, 0.025 / 3.5);

					} else {
						// IF NOT CONNECTED DO AGGRESSIVE RED PULSE
						pulse(kLights.RED, 1, 0.2, .8, 0.025 / 3.5);
					}
					break;
				}
			}
		} else {
			pulse(kLights.RED, 1, 0, 1, Double.POSITIVE_INFINITY);
		}
	}

	public double centerX(int whichCube) {
		if (visionLength() > 0)
			return centerX.getDoubleArray(tempArray)[whichCube];
		else
			return 3452;
	}

	public double centerY(int whichCube) {
		if (visionLength() > 0)
			return centerY.getDoubleArray(tempArray)[whichCube];
		else
			return 3452;
	}

	private int visionLength() {
		return centerX.getDoubleArray(tempArray).length;
	}

	public void off() {
		hsv(0, 0, 0);
	}

	public boolean hasMotors()
	{
		return false;
	}
	public void addPDPTestingMotors(){}

	public void addMotorsForTesting() {
	}

	public void hsv(double hDegrees, double saturation, double value) {
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

	private void rgb(float red, float green, float blue) {
		if (m_hue > 360)
			m_hue = 0;

		try {
			canifier.setLEDOutput(red, CANifier.LEDChannel.LEDChannelA);
			canifier.setLEDOutput(green, CANifier.LEDChannel.LEDChannelB);
			canifier.setLEDOutput(blue, CANifier.LEDChannel.LEDChannelC);
		} catch (Exception e) {
		}
	}

	public void pulse(int hue, double saturation, double lowBounePoint, double highBouncePoint, double pulseIntensity) {
		if (pulseIntensity > highBouncePoint / 15)
			pulseIntensity = highBouncePoint / 15;

		if (pulseDirection)
			pulseBrightness += pulseIntensity;
		else
			pulseBrightness -= pulseIntensity;

		if (pulseBrightness >= highBouncePoint)
			pulseDirection = false;

		if (pulseBrightness <= lowBounePoint)
			pulseDirection = true;

		hsv(hue, saturation, pulseBrightness);
		m_hue = hue;
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

	protected void initDefaultCommand() {
	}

}
