package frc.robot.util.requests;

import frc.robot.Constants.kSuperstructure;
import frc.robot.subsystems.Superstructure;
import frc.robot.util.GZSubsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestList {

    ArrayList<Request> requests;
    boolean parallel = false;

    boolean ignoreLogs = false;

    private GZSubsystem subsystem = null;

    public RequestList() {
        this(Arrays.asList(new EmptyRequest()), false);
    }

    public RequestList(GZSubsystem subsystem) {
        this();
        this.subsystem = subsystem;
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

    public RequestList extraLog(String message) {
        if (subsystem != null) {

            boolean print = false;

            if (subsystem instanceof Superstructure) {
                print |= kSuperstructure.EXTRA_LOGS;
            }

            log(message, print);
        }

        return this;
    }

    public RequestList printTimeStamp() {
        add(Request.printTime());
        return this;
    }

    public RequestList log(String message) {
        return log(message, false);
    }

    public RequestList log(String message, boolean print) {
        if (subsystem != null) {
            add(Request.log(subsystem, message, print));
        } else {
            System.out.println("Request list never initialized subsystem, cannot log!");
        }
        return this;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel() {
        this.parallel = true;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public RequestList add(Request request) {
        requests.add(request);
        return this;
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
