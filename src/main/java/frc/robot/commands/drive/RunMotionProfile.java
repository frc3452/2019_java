package frc.robot.commands.drive;

import com.ctre.phoenix.motion.MotionProfileStatus;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.motionprofiles.Path;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.drivers.GZSRX.Side;

/**
 * @author max
 * @since 4-22-2018
 */
public class RunMotionProfile extends Command {

	private GZFiles files = GZFiles.getInstance();

	private MotionProfileStatus rStat = new MotionProfileStatus();
	private MotionProfileStatus lStat = new MotionProfileStatus();

	private Path path_ = null;

	private Drive drive = Drive.getInstance();

	private boolean mParsed = false;

	private String mName;
	private Folder mFolder;
	private boolean mUsb;

	public RunMotionProfile(Path path) {
		requires(drive);

		path_ = path;
	}

	public RunMotionProfile(String name, Folder folder, boolean usb) {
		requires(drive);
		mParsed = true;
		
		mName = name;
		mFolder = folder;
		mUsb = usb;
	}

	@Override
	protected void initialize() {
		drive.zeroEncoders();
		
		// check if we are parsing or running a stored motion profile
		if (mParsed){
			files.parse(mName, mFolder, mUsb);
			drive.motionProfileToTalons();
		}else
			drive.motionProfileToTalons(path_.mpL(), path_.mpR(), path_.mpDur());
	}

	@Override
	protected void execute() {
		if (lStat.btmBufferCnt > 5 && rStat.btmBufferCnt > 5)
			drive.setWantedState(DriveState.MOTION_PROFILE);
		else
			drive.stop();

		drive.getMotionProfileStatus(true, lStat);
		drive.getMotionProfileStatus(false, rStat);
	}

	@Override
	protected boolean isFinished() {
		return false;
		// return isTimedOut();
		// System.out.println(lStat.activePointValid + "\t" + lStat.isLast + "\t\t" + rStat.isLast + "\t" + rStat.activePointValid);
		// return (lStat.activePointValid && lStat.isLast) || (rStat.activePointValid && rStat.isLast) || isTimedOut();
	}

	@Override
	protected void end() {
		drive.stop();
		System.out.println("Motion Profile Complete");
	}

	@Override
	protected void interrupted() {
		end();
	}
}
