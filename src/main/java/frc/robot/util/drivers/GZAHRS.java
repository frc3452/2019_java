package frc.robot.util.drivers;

import java.text.DecimalFormat;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI.Port;
import frc.robot.poofs.util.math.Rotation2d;

public class GZAHRS extends AHRS {

	private final DecimalFormat df = new DecimalFormat("#0.000");

    protected Rotation2d mAngleAdjustment = Rotation2d.identity();

	public GZAHRS(Port kmxp) {
		super(kmxp);
	}
	public synchronized void setAngleAdjustment(Rotation2d adjustment) {
        mAngleAdjustment = adjustment;
    }

	@Override
	public String toString() {
		return "x disp: " + df.format(this.getDisplacementX()) + "\ty disp: "
				+ df.format(this.getDisplacementY()) + "\theading: " + df.format(this.getFusedHeading());
	}
}