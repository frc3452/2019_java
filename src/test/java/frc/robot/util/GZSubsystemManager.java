package frc.robot.util;

import java.util.Arrays;
import java.util.List;

//thx 254
public class GZSubsystemManager {

	private final List<GZSubsystem> mAllSystems;
	private final boolean mHasSingleSolenoids;
	private final boolean mHasDoubleSolenoids;

	public GZSubsystemManager(GZSubsystem... allSubsystems) {
		mAllSystems = Arrays.asList(allSubsystems);

		boolean singles = false;
		boolean doubles = false;

		for (GZSubsystem s : mAllSystems) {
			singles |= s.mSingleSolenoids.size() != 0;
			doubles |= s.mDoubleSolenoids.size() != 0;
		}

		mHasSingleSolenoids = singles;
		mHasDoubleSolenoids = doubles;
	}

	private GZNotifier looper_ = new GZNotifier(() -> loop());

	public void startLooping() {
		looper_.startPeriodic(.02);
	}

	public void addLoggingValues() {
		mAllSystems.forEach((s) -> s.addLogItems());
	}

	public boolean hasAir()
	{
		return hasSingles() || hasDoubles();
	}

	public boolean hasSingles()
	{
		return this.mHasSingleSolenoids;
	}

	public boolean hasDoubles()
	{
		return this.mHasDoubleSolenoids;
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

		for (GZSubsystem s : mAllSystems)
			System.out.println(s.getName() + ": " + s.isSafetyDisabled());
	}

	public void printStates(boolean printHeader) {
		if (printHeader)
			System.out.println("~~~SUBSYSTEM STATES~~~");

		for (GZSubsystem s : mAllSystems)
			System.out.println(s.getName() + ": " + s.getStateString());

	}

	public void zeroSensors() {
		mAllSystems.forEach((s) -> s.zeroSensors());
	}
}
