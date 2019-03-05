package frc.robot.auto.commands.functions.drive;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.subsystems.Drive;
import frc.robot.util.drivers.GZJoystick.Buttons;

public class TeleDrive extends Command {

    private final boolean mDrive;
    private Supplier<Boolean> mSupplier;
    private Drive drive = Drive.getInstance();

    public TeleDrive(Supplier<Boolean> supplier, boolean shouldDrive) {
        requires(drive);
        this.mSupplier = supplier;
        this.mDrive = shouldDrive;
    }

    public TeleDrive(boolean shouldDrive) {
        this(() -> GZOI.driverJoy.getButton(Buttons.B), true);
    }

    public TeleDrive() {
        this(true);
    }

    protected void initialize() {
    }

    protected void execute() {
        if (mDrive)
            drive.handleDriving();
    }

    protected boolean isFinished() {
        return mSupplier.get();
    }

    protected void end() {
        drive.brake(false);
        // drive.stop();
    }

    protected void interrupted() {
        end();
    }
}
