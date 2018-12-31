package frc.robot.util;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.GZOI;
import frc.robot.Robot;
import frc.robot.Constants.kFiles;

/**
 * Allows access to PDP without disturbing subsystem construction order
 */
public class GZPDP {

    private PowerDistributionPanel pdp;
    private static GZPDP mInstance;

    final boolean sim;
    final double fakeReturnValue = -1;

    public static GZPDP getInstance() {
        if (mInstance == null)
            mInstance = new GZPDP();
        return mInstance;
    }

    public boolean isFake()
    {
        return sim;
    }

    private GZPDP() {

        boolean construct = RobotBase.isReal() && kFiles.FAKE_PDP == false;
        if (construct)
            pdp = new PowerDistributionPanel();

        sim = !construct;
    }

    public double getCurrent(int channel, double simulatedValue) {
        if (sim)
            return simulatedValue;
        return pdp.getCurrent(channel);
    }

    public double getCurrent(int channel) {
        return getCurrent(channel, fakeReturnValue);
    }

    public double getTotalPower() {
        if (sim)
            return fakeReturnValue;
        return pdp.getTotalPower();
    }

    public double getVoltage() {
        if (sim)
            return fakeReturnValue;
        return pdp.getVoltage();
    }

    public double getTotalEnergy() {
        if (sim)
            return fakeReturnValue;
        return pdp.getTotalEnergy();
    }

    public double getTotalCurrent() {
        if (sim)
            return fakeReturnValue;
        return pdp.getTotalCurrent();
    }

    public double getTemperature() {
        if (sim)
            return fakeReturnValue;

        return pdp.getTemperature();
    }

}