package frc.robot.util.drivers;

import java.text.DecimalFormat;

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

	@Override
	public String toString() {
		final DecimalFormat df = new DecimalFormat("#0.000");
		return "x disp: " + df.format(this.getDisplacementX()) + "\ty disp: "
				+ df.format(this.getDisplacementY() + "\theading: " + this.getFusedHeading());
	}

}