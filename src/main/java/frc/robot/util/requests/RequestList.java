package frc.robot.util.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frc.robot.util.GZSubsystem;

public class RequestList {

	ArrayList<Request> requests;
	boolean parallel = false;

	private GZSubsystem subsystem = null;

	public RequestList() {
		this(Arrays.asList(new EmptyRequest()), false);
	}

	public RequestList(GZSubsystem subsystem) {
		this();
		this.subsystem = subsystem;
	}

	public void log(String message) {
		if (subsystem != null) {
			add(Request.log(subsystem, message));
		} else {
			System.out.println("Request list never initialized subsystem, cannot log!");
		}
	}

	public RequestList(Request request) {
		this(Arrays.asList(request), false);
	}

	public RequestList(List<Request> requests) {
		this(requests, false);
	}

	public RequestList(List<Request> requests, boolean parallel) {
		this.requests = new ArrayList<>(requests.size());
		for (Request request : requests) {
			this.requests.add(request);
		}
		this.parallel = parallel;
	}

	public static RequestList emptyList() {
		return new RequestList(new ArrayList<>(0), false);
	}

	public boolean isParallel() {
		return parallel;
	}

	public List<Request> getRequests() {
		return requests;
	}

	public void add(Request request) {
		requests.add(request);
	}

	public void addToForefront(Request request) {
		requests.add(0, request);
	}

	public Request remove() {
		return requests.remove(0);
	}

	public boolean isEmpty() {
		return requests.isEmpty();
	}

}
