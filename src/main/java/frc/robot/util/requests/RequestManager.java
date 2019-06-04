package frc.robot.util.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RequestManager {

    private RequestList activeRequests = new RequestList();
    private ArrayList<RequestList> queuedRequests = new ArrayList<>(0);;
    private Request currentRequest = new EmptyRequest();

    private boolean newRequests = false;
    private boolean activeRequestsCompleted = false;
    private boolean allRequestsCompleted = false;

    public boolean requestsCompleted() {
        return allRequestsCompleted;
    }

    private double lastTimeStamp = Double.NaN;
    private double dt = Double.NaN;

    public double getTimeDelta() {
        return dt;
    }

    public void update(double timestamp) {
        dt = timestamp - lastTimeStamp;
        lastTimeStamp = timestamp;

        if (!activeRequestsCompleted) {
            if (newRequests) {
                if (activeRequests.isParallel()) {
                    boolean allActivated = true;
                    for (Iterator<Request> iterator = activeRequests.getRequests().iterator(); iterator.hasNext();) {
                        Request request = iterator.next();
                        boolean allowed = request.allowed();
                        allActivated &= allowed;
                        if (allowed)
                            request.act();
                    }
                    newRequests = !allActivated;
                } else {
                    if (activeRequests.isEmpty()) {
                        activeRequestsCompleted = true;
                        return;
                    }
                    currentRequest = activeRequests.remove();
                    currentRequest.act();
                    newRequests = false;
                }
            }
            if (activeRequests.isParallel()) {
                boolean done = true;
                for (Request request : activeRequests.getRequests()) {
                    done &= request.isFinished();
                }
                activeRequestsCompleted = done;
            } else if (currentRequest.isFinished()) {
                if (activeRequests.isEmpty()) {
                    activeRequestsCompleted = true;
                } else if (activeRequests.getRequests().get(0).allowed()) {
                    newRequests = true;
                    activeRequestsCompleted = false;
                }
            }
        } else {
            if (!queuedRequests.isEmpty()) {
                setActiveRequests(queuedRequests.remove(0));
            } else {
                allRequestsCompleted = true;
            }
        }

    }

    private void setQueuedRequests(RequestList requests) {
        queuedRequests.clear();
        queuedRequests.add(requests);
    }

    private void setQueuedRequests(List<RequestList> requests) {
        queuedRequests.clear();
        queuedRequests = new ArrayList<>(requests.size());
        for (RequestList list : requests) {
            queuedRequests.add(list);
        }
    }

    public void request(Request r) {
        setActiveRequests(new RequestList(Arrays.asList(r), false));
        setQueuedRequests(new RequestList());
    }

    public void request(Request active, Request queue) {
        setActiveRequests(new RequestList(Arrays.asList(active), false));
        setQueuedRequests(new RequestList(Arrays.asList(queue), false));
    }

    public void request(RequestList list) {
        setActiveRequests(list);
        setQueuedRequests(new RequestList());
    }

    public void request(RequestList activeList, RequestList queuedList) {
        setActiveRequests(activeList);
        setQueuedRequests(queuedList);
    }

    public void addActiveRequest(Request request) {
        activeRequests.add(request);
        newRequests = true;
        activeRequestsCompleted = false;
        allRequestsCompleted = false;
    }

    /** Ill-advised */
    public void addForemostActiveRequest(Request request) {
        activeRequests.addToForefront(request);
        newRequests = true;
        activeRequestsCompleted = false;
        allRequestsCompleted = false;
    }

    public void queue(Request request) {
        queuedRequests.add(new RequestList(Arrays.asList(request), false));
    }

    public void queue(RequestList list) {
        queuedRequests.add(list);
    }

    public void replaceQueue(Request request) {
        setQueuedRequests(new RequestList(Arrays.asList(request), false));
    }

    public void replaceQueue(RequestList list) {
        setQueuedRequests(list);
    }

    public void replaceQueue(List<RequestList> lists) {
        setQueuedRequests(lists);
    }

    public void setActiveRequests(RequestList requests) {
        activeRequests = requests;
        newRequests = true;
        activeRequestsCompleted = false;
        allRequestsCompleted = false;
    }

    public void clear() {
        request(new EmptyRequest());
    }
}