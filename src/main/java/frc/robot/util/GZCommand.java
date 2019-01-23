package frc.robot.util;

import edu.wpi.first.wpilibj.command.Command;

public class GZCommand {

	private String mName;
	private Command mCommand;

	private boolean mRunning = false;

	public GZCommand(String name, Command command) {
		this.mName = name;
		this.mCommand = command;
	}

	public synchronized boolean start() {

		//Update our boolean from the Scheduler
		isRunning();

		if (mCommand == null)
			return false;

		if (!mRunning) {
			mRunning = true;
			mCommand.start();
			return true;
		}

		return false;
	}

	public synchronized boolean isRunning() {
		if (mCommand == null)
			return false;

		if (!mCommand.isRunning() || mCommand.isCompleted() || mCommand.isCanceled()) {
			mRunning = false;
		}

		return mRunning;
	}

	public synchronized boolean cancel() {
		//Update value from the scheduler
		isRunning();

		if (mCommand == null)
			return false;

		mCommand.cancel();
		if (mRunning) {
			mRunning = false;
			return true;
		}

		return false;
	}

	public String getName() {
		return mName;
	}

	public Command getCommand() {
		return mCommand;
	}
}
