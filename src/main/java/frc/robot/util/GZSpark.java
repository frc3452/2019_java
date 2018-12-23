package frc.robot.util;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

public class GZSpark extends Spark implements GZSpeedController {

    public static class Builder {
        private int mPWMPort;
        private String mName = "GZ";
        private GZSubsystem mSub;
        private int mPDPChannel;

        private int mTempSensorPort = -1;

        public Builder(int port, GZSubsystem sub, int PDPChannel, String name) {
            this.mPWMPort = port;
            this.mPDPChannel = PDPChannel;
            this.mSub = sub;
            this.mName = name;
        }

        public Builder setTempSensorPort(int port) {
            this.mTempSensorPort = port;
            return this;
        }

        public GZSpark build() {
            GZSpark s;
            s = new GZSpark(this.mPWMPort, this.mSub, this.mPDPChannel, this.mName, this.mTempSensorPort);

            return s;
        }

    }

    private int mPWMPort;
    private int mPDPChannel;
    private String mName;

    private boolean mLockedOut = false;

    private AnalogInput mTemperatureSensor = null;

    private GZSpark(int pwmPort, GZSubsystem subsystem, int PDPChannel, String name, int tempSensorPort) {
        super(pwmPort);
        this.mPWMPort = pwmPort;
        this.mName = name;
        this.mPDPChannel = PDPChannel;

        if (tempSensorPort != -1)
            mTemperatureSensor = new AnalogInput(tempSensorPort);

        subsystem.mSparks.put(this.mPWMPort, this);
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
    public void lockOutController(boolean lockedOut) {
        this.mLockedOut = lockedOut;
    }

    public Double getAmperage() {
        return GZPDP.getInstance().getPDP().getCurrent(this.mPDPChannel);
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

}