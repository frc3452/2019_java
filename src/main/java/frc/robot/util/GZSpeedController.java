package frc.robot.util;

public interface GZSpeedController {
    
    public void lockOutController(boolean lockedOut);
    
    public void set(double speed);
    public void set(double speed, boolean overrideLockout);

    public Double getAmperage();

    public String getGZName();

    public Double getTemperatureSensor();
    public boolean hasTemperatureSensor();
}