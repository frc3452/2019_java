package frc.robot.util;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.auto.commands.drive.pathfollowing.ResetPoseDrivePath;

public class GZCommand {

	private String mName;
	private Command mCommand;

	private boolean mRunning = false;

	private GZFlag mHasRun = new GZFlag(), mHasBeenCancelled = new GZFlag();

	public GZCommand(String name, Command command) {
		this.mName = name;
		this.mCommand = command;
	}

	public GZCommand(ResetPoseDrivePath path)
	{
		this.mName = path.getPathContainer().getClass().getSimpleName();
		this.mCommand = path;
	}

	public synchronized boolean hasRun() {
		return mHasRun.get();
	}

	public synchronized boolean hasBeenCancelled()
	{
		return mHasBeenCancelled.get();
	}

	public synchronized void start() {

		// Update our boolean from the Scheduler
		isRunning();

		if (mCommand == null)
			return;

		mRunning = true;
		mCommand.start();
		mHasRun.tripFlag();
	}

	public synchronized boolean isRunning() {
		// if (mCommand == null)
		// 	return false;

		// if (!mCommand.isRunning() || mCommand.isCompleted() || mCommand.isCanceled()) {
		// 	mRunning = false;
		// }

		return mRunning;
	}

	public synchronized void cancel() {
		// Update value from the scheduler
		isRunning();

		if (mCommand == null)
			return;

		mCommand.cancel();
		mRunning = false;
		mHasBeenCancelled.tripFlag();
	}

	public String getName() {
		return mName;
	}

	public Command getCommand() {
		return mCommand;
	}
}
