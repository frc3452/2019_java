package frc.robot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//thx 254
public class GZSubsystemManager {

	private final List<GZSubsystem> mAllSystems;

	public GZSubsystemManager(GZSubsystem... allSubsystems) {
		mAllSystems = Arrays.asList(allSubsystems);
	}

	private GZNotifier looper_ = new GZNotifier(() -> loop());

	public void startLooping() {
		looper_.startPeriodic(.02);
	}

	public void addLoggingValues() {
		mAllSystems.forEach((s) -> s.addLoggingValues());
	}

	public void loop() {
		mAllSystems.forEach((s) -> s.superLoop());
	}

	public List<GZSubsystem> getSubsystems() {
		return mAllSystems;
	}

	public void stop() {
		mAllSystems.forEach((s) -> s.stop());
	}

	public void disable(boolean toDisable) {
		mAllSystems.forEach((s) -> s.safetyDisable(toDisable));
	}

	public void enableFollower() {
		mAllSystems.forEach((s) -> s.enableFollower());
	}

	public void whatIsDisabled(boolean printHeader) {
		if (printHeader)
			System.out.println("~~~SUBSYSTEMS DISABLED~~~");

		for (int i = 0; i < mAllSystems.size() - 1; i++)
			System.out.println(mAllSystems.get(i).getName() + ": " + mAllSystems.get(i).isSafetyDisabled());
	}

	public void printStates(boolean printHeader) {
		if (printHeader)
			System.out.println("~~~SUBSYSTEM STATES~~~");

		for (int i = 0; i < mAllSystems.size() - 1; i++)
			System.out.println(mAllSystems.get(i).getName() + ": " + mAllSystems.get(i).getStateString());

	}

	public void zeroSensors() {
		mAllSystems.forEach((s) -> s.zeroSensors());
	}
}
