package frc.robot.auto.commands.functions.drive;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;
import frc.robot.util.GZFile;
import frc.robot.util.GZFiles;

public class FollowPath extends Command {

    GZFiles files = GZFiles.getInstance();
    Drive drive = Drive.getInstance();

    int mPos = 0;

    GZFile file;

    boolean mFinished = false;

    public FollowPath(GZFile file) {
        this.file = file;
    }

    // Setting timer, starts recording, sets a notifier
    protected void initialize() {
        setTimeout(10);

        files.parse(file);

        tickListValue.startPeriodic((double) files.mpDur / 1000);
    }

    // Checks if list mpMove and mpRotate are greater than mPos
    // If they both are true, mFinished is left as false
    // If one or both are not true, mFinished is set to true
    private Notifier tickListValue = new Notifier(new Runnable() {
        @Override
        public void run() {
            if (mPos < files.mpL.size() - 1 && mPos < files.mpR.size() - 1) {
                mPos++;
                mFinished = false;
            } else {
                mFinished = true;
            }
        }
    });

    // Sets arcade values to position in lists mpMove and mpRotate using variable
    // mPos
    protected void execute() {
        double left;
        double right;

        left = files.mpL.get(mPos);
        right = files.mpR.get(mPos);

        drive.tank(left, right);
    }

    // Command returns true if either Initalize's timer runs out or if mFinished is
    // set to true
    protected boolean isFinished() {
        return isTimedOut() || mFinished;
    }

    // Changes drive from arcade to tank and stops
    protected void end() {
        drive.arcade(0, 0);
    }

    // Does the same as end
    protected void interrupted() {
        end();
    }
}