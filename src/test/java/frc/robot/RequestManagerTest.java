package frc.robot;

import org.junit.Test;

import frc.robot.util.requests.Request;
import frc.robot.util.requests.RequestList;
import frc.robot.util.requests.RequestManager;

public class RequestManagerTest {

    @Test
    public void test() {
        RequestManager m = new RequestManager();

        RequestList list = new RequestList();
        list.add(Request.printRequest("Starting"));
        list.add(new Request() {

            double waitTime = 3;
            double startTime;

            @Override
            public void act() {
                startTime = t;
                
                System.out.println("First act");
            }

            @Override
            public boolean isFinished() {
                boolean done = (t - startTime) > waitTime;
                if (done) {
                    System.out.println("First flag done");
                }
                return done;
            }
        });
        list.add(new Request() {

            double waitTime = 5;
            double startTime;

            @Override
            public void act() {
                startTime = t;
                System.out.println("Second act");
            }

            @Override
            public boolean isFinished() {
                boolean done = (t - startTime) > waitTime;
                if (done) {
                    System.out.println("Second flag done");
                }
                return done;
            }
        });
        list.setParallel();

        m.request(list, RequestList.emptyList().add(Request.printRequest("Done")));

        while (t < 10) {
            m.update(t);
            t += 1;
        }

    }

   double t = 0.0;

}