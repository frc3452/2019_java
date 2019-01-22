package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.GZOI;
import frc.robot.Constants.kPneumatics;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public class Pneumatics extends GZSubsystem {

    private Compressor mCompressor;
    private GZSolenoid mClimberCrawler;

    private GZAnalogInput mPressureSensor;
    private static Pneumatics mInstance = null;

    public static Pneumatics getInstance() {
        if (mInstance == null)
            mInstance = new Pneumatics();

        return mInstance;
    }

    private Pneumatics() {
        mCompressor = new Compressor(kPneumatics.COMPRESSOR_MODULE);
        mClimberCrawler = new GZSolenoid(kPneumatics.CRAWLER, this, "Climber crawler");
        mPressureSensor = new GZAnalogInput(this, "Pressure sensor", kPneumatics.PRESSURE_GUAGE_PORT);
    }

    public void dropCrawler() {
        mClimberCrawler.set(true);
    }

    public void raiseCrawler() {
        mClimberCrawler.set(false);
    }

    @Override
    public void loop() {
        boolean noAir = false;

        if (GZOI.getInstance().isAuto()) {
            noAir = true;
        }

        if (noAir) {
            mCompressor.stop();
        } else {
            mCompressor.start();
        }

    }

    public int getDropClimberTotalCounts() {
        return mClimberCrawler.getChangeCounts();
    }

    @Override
    public void stop() {

    }

    @Override
    public String getSmallString() {
        return "PNM";
    }

    @Override
    public void addLoggingValues() {
    }

    @Override
    public String getStateString() {
        return "NA";
    }

    @Override
    protected void initDefaultCommand() {

    }

}