package frc.robot.util;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Allows access to PDP without disturbing subsystem construction order
 */
public class GZPDP {

    private PowerDistributionPanel pdp;

    private static GZPDP mInstance;
    public static GZPDP getInstance()
    {
        if (mInstance == null)
            mInstance = new GZPDP();
        return mInstance;
    }

    private GZPDP()
    {
        pdp = new PowerDistributionPanel();
    }

    public PowerDistributionPanel getPDP()
    {
        return pdp;
    }

}