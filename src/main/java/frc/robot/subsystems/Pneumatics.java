package frc.robot.subsystems;

import java.text.DecimalFormat;

import edu.wpi.first.wpilibj.Compressor;
import frc.robot.Constants;
import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kPneumatics;
import frc.robot.Constants.kSolenoids;
import frc.robot.GZOI;
import frc.robot.TestModeRunner;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZNotifier;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public class Pneumatics extends GZSubsystem {

    private Compressor mCompressor;
    private GZSolenoid mClimberCrawler;

    private GZAnalogInput mPressureSensor;
    private static Pneumatics mInstance = null;

    private boolean mLowPressure = false;
    private boolean mIsMotorTesting = false;

    public void setMotorTesting(boolean testing) {
        this.mIsMotorTesting = testing;
    }

    private int mCrawlerPresses = 0;

    private DecimalFormat df = new DecimalFormat("#0.00");

    private GZNotifier mPressurePrint = new GZNotifier(() -> {
        if (getPressure() < kPneumatics.LOW_PRESSURE_PRINT_SETPOINT)
            System.out.println("Warning Pressure below " + kPneumatics.LOW_PRESSURE_PRINT_SETPOINT + ": "
                    + df.format(getPressure()));
    });

    public static Pneumatics getInstance() {
        if (mInstance == null)
            mInstance = new Pneumatics();

        return mInstance;
    }

    private Pneumatics() {
        mCompressor = new Compressor(kPneumatics.COMPRESSOR_MODULE);
        mClimberCrawler = new GZSolenoid(kSolenoids.CRAWLER, this, "Climber crawler");
        mPressureSensor = new GZAnalogInput(this, "Pressure sensor", kPneumatics.PRESSURE_GUAGE_PORT,
                kPneumatics.PRESSURE_GUAGE_INFO);

        // mPressurePrint.startPeriodic(2);
    }

    public double getPressure() {
        return mPressureSensor.getTranslatedValue();
    }

    public void forceDropCrawler() {
        mClimberCrawler.on();
    }

    public void dropCrawler() {
        if (++mCrawlerPresses > kDrivetrain.CRAWLER_DROP_NECCESARY_TICKS) {
            mClimberCrawler.on();
            System.out.println("CRAWLER PRESSED: " + mCrawlerPresses);
        }
    }

    @Override
    public void loop() {
        boolean noAir = false;

        // final double pressure = getPressure();

        // if (pressure < kPneumatics.LOW_PRESSURE)
        // mLowPressure = true;

        // if (mLowPressure && pressure > kPneumatics.HIGH_PRESSURE) {
        // mLowPressure = false;
        // }

        // noAir = true;
        // if (GZOI.getInstance().isAuto() || !mLowPressure) {
        // noAir = true;
        // }

        noAir |= mIsMotorTesting;
        noAir |= GZOI.getInstance().isAuto();
        noAir |= !Constants.COMP_BOT;
        noAir |= GZOI.getInstance().isTest() && TestModeRunner.getInstance().isEnabled();

        // noAir = true;

        if (noAir) {
            mCompressor.stop();
            // System.out.println("Air is off");
        } else {
            mCompressor.start();

            // if (++counter > 30) {
            // System.out.println("Air is on");
            // counter = 0;
            // }
        }
    }

    // int counter = 0;

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
        new LogItem(getSmallString() + "-PRSSRE") {
            public String val() {
                return "" + getPressure();
            }
        };

        new LogItem(getSmallString() + "-COMP-AMP") {
            public String val() {
                return "" + mCompressor.getCompressorCurrent();
            }
        };

        new LogItem(getSmallString() + "-COMP-PS-SWT") {
            public String val() {
                return "" + mCompressor.getPressureSwitchValue();
            }
        };

        new LogItem(getSmallString() + "-COMP-ON") {
            public String val() {
                return "" + mCompressor.enabled();
            }
        };
    }

    @Override
    public String getStateString() {
        return "NA";
    }

    @Override
    protected void initDefaultCommand() {

    }

}