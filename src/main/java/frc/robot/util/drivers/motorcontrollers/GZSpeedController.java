package frc.robot.util.drivers.motorcontrollers;

import frc.robot.util.drivers.IGZHardware;

public interface GZSpeedController extends IGZHardware {

	public static enum Breaker {
		AMP_20, AMP_30, AMP_40, NO_INFO
	}

    //sets
    public void lockOutController(boolean lockedOut);
    public void set(double speed);
    public void set(double speed, boolean overrideLockout);

    public Double getAmperage();

    public double getOutputPercentage();

    //temperature sensor
    public Double getTemperatureSensor();
    public boolean hasTemperatureSensor();
    public int getTemperatureSensorPort();

    public int getPort();

    public Breaker getCalculatedBreaker();

    public int getPDPChannel();

    public static Breaker setBreaker(int pdpchannel, GZSpeedController controller) {
        if (pdpchannel > 15 || pdpchannel < 0) {
            System.out.println("PDP CHANNEL " + pdpchannel + " on Controller " + controller.getGZName() + " invalid!");
            return Breaker.NO_INFO;
        }

        if (pdpchannel >= 4 && pdpchannel <= 11)
            return Breaker.AMP_30;
        else
            return Breaker.AMP_40;
    }
}