package frc.robot.util;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;

public class GZCommand {

	private String mName;
	private Supplier<Command> mCommandSupplier;
	private Command mCommand;

	private boolean mRunning = false;

	private GZFlag mHasRun = new GZFlag(), mHasBeenCancelled = new GZFlag();

	public GZCommand(String name, Supplier<Command> commandSupplier) {
		this.mName = name;
		this.mCommandSupplier = commandSupplier;
		setCommand();
	}

	public void setCommand() {
		this.mCommand = this.mCommandSupplier.get();
	}

	public GZCommand(Supplier<Command> commandSupplier) {
		this(commandSupplier.get().getClass().getSimpleName(), commandSupplier);
	}

	public synchronized boolean hasRun() {
		return mHasRun.get();
	}

	public synchronized boolean hasBeenCancelled() {
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
		// return false;

		// if (!mCommand.isRunning() || mCommand.isCompleted() || mCommand.isCanceled())
		// {
		// mRunning = false;
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
