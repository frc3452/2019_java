package frc.robot.util.drivers;

import frc.robot.util.drivers.GZSRX.Breaker;

public interface GZSpeedController {

    public void lockOutController(boolean lockedOut);

    public void set(double speed);

    public void set(double speed, boolean overrideLockout);

    public Double getAmperage();

    public String getGZName();

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