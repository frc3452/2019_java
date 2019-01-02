package frc.robot.util.drivers;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI.Port;

public class GZAHRS extends AHRS {

	public GZAHRS(Port kmxp) {
		super(kmxp);
	}

	@Override
	public float getYaw() {
		return -super.getYaw();
	}

}