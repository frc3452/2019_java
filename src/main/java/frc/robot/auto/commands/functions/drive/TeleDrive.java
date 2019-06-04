package frc.robot.auto.commands.functions.drive;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.subsystems.Drive;

public class TeleDrive extends Command {

    private final boolean mDrive;
    private Supplier<Boolean> mSupplier;
    private Drive drive = Drive.getInstance();

    private boolean mWasOff = false;

    public TeleDrive(Supplier<Boolean> supplier, boolean shouldDrive) {
        requires(drive);
        this.mSupplier = supplier;
        this.mDrive = shouldDrive;
    }

    public TeleDrive(boolean shouldDrive) {
        this(() -> GZOI.driverJoy.bButton.isBeingPressed(), true);
    }

    public TeleDrive() {
        this(true);
    }

    protected void initialize() {
    }

    protected void execute() {
        if (mDrive) {
            GZOI.getInstance().handleControls();
        }
    }

    protected boolean isFinished() {
        if (!mSupplier.get())
            mWasOff = true;

        return mSupplier.get() && mWasOff;
    }

    protected void end() {
        drive.brake(false);
        // drive.stop();
    }

    protected void interrupted() {
        end();
    }
}
