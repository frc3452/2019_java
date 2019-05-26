package frc.robot.util.requests;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;

public abstract class Request {

	public abstract void act();

	public boolean isFinished() {
		return true;
	}

	public static Request log(GZSubsystem subsystem, String message) {
		return log(subsystem, message, true);
	}

	DecimalFormat df = new DecimalFormat("#0.00");

	public static Request log(GZSubsystem subsystem, String message, boolean print) {
		return new Request() {

			@Override
			public void act() {
				String out = message;

				GZFiles.getInstance().addLog(subsystem, out, print);
			}
		};
	}

	public List<Prerequisite> prerequisites = new ArrayList<>();

	public void withPrerequisites(List<Prerequisite> reqs) {
		for (Prerequisite req : reqs) {
			prerequisites.add(req);
		}
	}

	public void withPrerequisite(Prerequisite req) {
		prerequisites.add(req);
	}

	public boolean allowed() {
		boolean reqsMet = true;
		for (Prerequisite req : prerequisites) {
			reqsMet &= req.met();
		}
		return reqsMet;
	}

	public static Request waitThenPrint(String firstPrint, double seconds, String message) {
		return new Request() {

			double startTime = 0.0;
			double waitTime = 1.0;

			@Override
			public void act() {
				startTime = Timer.getFPGATimestamp();
				waitTime = seconds;
				System.out.println(firstPrint);
			}

			public boolean isFinished() {
				boolean done = (Timer.getFPGATimestamp() - startTime) > waitTime;
				if (done)
					System.out.println(message);
				return done;
			}
		};
	}

	public static Request waitRequest(double seconds) {
		return new Request() {
			double startTime = 0.0;
			double waitTime = 1.0;

			@Override
			public void act() {
				startTime = Timer.getFPGATimestamp();
				waitTime = seconds;
			}

			@Override
			public boolean isFinished() {
				return (Timer.getFPGATimestamp() - startTime) > waitTime;
			}
		};
	}

	public static Request printTime() {
		return new Request() {

			@Override
			public void act() {
				System.out.println(Timer.getFPGATimestamp());
			}
		};
	}

	public static Request printRequest(String message) {
		return new Request() {

			@Override
			public void act() {
				System.out.println(message);
			}
		};
	}
}
