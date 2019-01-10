package frc.robot.util.drivers;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Talon;
import frc.robot.util.GZPDP;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZSRX.Breaker;

public class GZTalonSR extends Talon implements GZSpeedController {

    public static class Builder {
        private int mPWMPort;
        private String mName = "GZ";
        private GZSubsystem mSub;
        private int mPDPChannel;

        private int mTempSensorPort = -1;

        public Builder(int port, GZSubsystem sub, String name, int PDPChannel) {
            this.mPWMPort = port;
            this.mPDPChannel = PDPChannel;
            this.mSub = sub;
            this.mName = name;
        }

        public Builder setTempSensorPort(int port) {
            this.mTempSensorPort = port;
            return this;
        }

        public GZTalonSR build() {
            GZTalonSR s;
            s = new GZTalonSR(this.mPWMPort, this.mSub, this.mPDPChannel, this.mName, this.mTempSensorPort);

            return s;
        }

    }

    private int mPWMPort;
    private int mPDPChannel;
    private String mName;
    private Breaker mBreaker;

    private boolean mLockedOut = false;

    private AnalogInput mTemperatureSensor = null;
    private int mTemperatureSensorPort;

    private GZTalonSR(int pwmPort, GZSubsystem subsystem, int PDPChannel, String name, int tempSensorPort) {
        super(pwmPort);
        this.mPWMPort = pwmPort;
        this.mName = name;
        this.mPDPChannel = PDPChannel;
        this.mTemperatureSensorPort = tempSensorPort;

        this.mBreaker = GZSpeedController.setBreaker(this.mPDPChannel, this);

        if (this.mTemperatureSensorPort != -1)
            this.mTemperatureSensor = new AnalogInput(this.mTemperatureSensorPort);

        subsystem.mDumbControllers.add(this);
    }

    public Breaker getCalculatedBreaker() {
        return this.mBreaker;
    }

    public int getTemperatureSensorPort() {
        return this.mTemperatureSensorPort;
    }

    /**
     * ONLY USE FOR TESTING
     */
    public void set(double speed, boolean overrideLockout) {
        if (!mLockedOut || (mLockedOut && overrideLockout)) {
            super.set(speed);
        }
    }

    @Override
    public void set(double speed) {
        set(speed, false);
    }

    @Override
    public double getOutputPercentage()
    {
        return super.getSpeed();
    }

    @Override
    public void lockOutController(boolean lockedOut) {
        this.mLockedOut = lockedOut;
    }

    public Double getAmperage() {
        return GZPDP.getInstance().getCurrent(this.mPDPChannel);
    }

    public Double getTemperatureSensor() {
        return GZUtil.readTemperatureFromAnalogInput(this.mTemperatureSensor);
    }

    public String getGZName() {
        return this.mName;
    }

    public boolean hasTemperatureSensor() {
        return this.mTemperatureSensor != null;
    }

    public int getPort() {
        return this.mPWMPort;
    }

    public int getPDPChannel() {
        return this.mPDPChannel;
    }

}