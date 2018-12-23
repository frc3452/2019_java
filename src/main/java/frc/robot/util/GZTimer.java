package frc.robot.util;

import edu.wpi.first.wpilibj.Timer;

public class GZTimer extends Timer {

	private boolean mHasOneTimeStarted = false;
	private String mName = "";

	private boolean mTiming = false;
	private double mAccumulatedTimeRunning = 0.0;

	/**
	 * Constructor
	 */
	public GZTimer(String name) {
		super();
		mName = name;
	}

	/**
	 * Constructs timer with name "Unspecified"
	 */
	public GZTimer() {
		this("Unspecified");
	}

	/**
	 * Starts timer
	 */
	public void startTimer() {
		if (!this.mHasOneTimeStarted) {
			super.stop();
			super.reset();
			super.start();
			this.mTiming = true;
		}
	}

	/**
	 * Only allows timer to be started once, never restarted
	 */
	public void oneTimeStartTimer() {
		if (!mHasOneTimeStarted) {
			super.stop();
			super.reset();
			super.start();
			this.mHasOneTimeStarted = true;
			this.mTiming = true;
		} else {
			System.out.println(this.getClass().getSimpleName() + " [" + getName() + "] cannot be started.");
		}
	}

	/**
	 * Stops timer, accumulates total runtime
	 */
	public void stopTimer() {
		if (this.isTiming()){
			this.mAccumulatedTimeRunning += this.get();
			this.mTiming = false;
			super.stop();
		}
	}

	/**
	 * @return total running time, only valid accumulation when timer is stopped 
	 */
	public double getTotalTimeRunning()
	{
		return this.mAccumulatedTimeRunning;
	}

	public void clearTotalTimeRunning()
	{
		this.mAccumulatedTimeRunning = 0;
	}

	/**
	 * @return if timer is currently running
	 */
	public boolean isTiming() {
		return this.mTiming;
	}

	/**
	 * @return timer name
	 */
	public String getName() {
		return this.mName;
	}

}