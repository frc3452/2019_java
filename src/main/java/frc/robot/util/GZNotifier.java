package frc.robot.util;

import edu.wpi.first.wpilibj.Notifier;

public class GZNotifier extends Notifier {

    private boolean isRunning = false;
    private GZFlag hasStarted = new GZFlag();

    public GZNotifier(Runnable b)
    {
        super(b);
    }

    public void startPeriodic(double time)
    {
        this.isRunning = true;
        super.startPeriodic(time);
        hasStarted.tripFlag();
    }

    public void stop()
    {
        super.stop();
        this.isRunning = false;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

    public boolean hasStarted()
    {
        return this.hasStarted.isFlagTripped();
    }

}