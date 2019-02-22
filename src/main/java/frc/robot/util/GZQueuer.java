package frc.robot.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

public abstract class GZQueuer<T> {

    private double mPrevTime;

    public abstract T getDefault();

    public abstract void onEmpty();

    private ArrayList<TimeValue<T>> mQueue = new ArrayList<TimeValue<T>>();

    public GZQueuer() {
    }

    public void print() {
        if (isQueueEmpty())
            return;
        String out = "";
        int counter = 0;
        for (TimeValue<T> m : mQueue)
            out += counter++ + "\t" + m.toString() + "\n";
        System.out.println(out);
    }

    public T update() {
        final double now = Timer.getFPGATimestamp();
        // print();

        if (!isQueueEmpty()) {
            if (mQueue.get(0).done()) {
                mQueue.remove(0);
            } else {
                mQueue.get(0).deductTimeBy(now - mPrevTime);
            }
            
            mPrevTime = now;
            if (isQueueEmpty())
            {
                onEmpty();
                return getDefault();
            }
            return mQueue.get(0).value;
        }
        mPrevTime = now;
        return getDefault();
    }

    public void clear() {
        mQueue.clear();
    }

    public boolean isQueueEmpty() {
        return mQueue.size() == 0;
    }

    public void addToQueue(TimeValue<T> value) {
        mQueue.add(value);
    }

    public void addToQueue(TimeValue<T>... values) {
        for (TimeValue<T> v : values)
            mQueue.add(v);
    }

    public void addToQueue(TimeValue<T> value, TimeValue<T> valueB, int times) {
        for (int i = 0; i < times; i++) {
            mQueue.add(value);
            mQueue.add(valueB);
        }
    }

    public void addToQueue(T valueA, double time, T valueB) {
        mQueue.add(new TimeValue<T>(valueA, time));
        mQueue.add(new TimeValue<T>(valueB, time));
    }

    public void addToQueue(T valueA, double timeA, T valueB, double timeB) {
        mQueue.add(new TimeValue<T>(valueA, timeA));
        mQueue.add(new TimeValue<T>(valueB, timeB));
    }

    public void addToQueue(T valueA, double timeA, T valueB, double timeB, int times) {
        for (int i = 0; i < times; i++) {
            addToQueue(valueA, timeA, valueB, timeB);
        }
    }

    public void addToQueue(T value, double time, int times) {
        addToQueue(new TimeValue<T>(value, time), new TimeValue<T>(getDefault(), 0), times);
    }

    public static class TimeValue<T> {

        private final DecimalFormat df = new DecimalFormat("#0.00");
        private final T value;

        private double timeLeft;

        public TimeValue(T value, double time) {
            this.value = value;
            timeLeft = time;
        }

        public void deductTimeBy(double time) {
            timeLeft -= time;
        }

        public boolean done() {
            return timeLeft <= 0;
        }

        public String toString() {
            return "Value [" + value + "] Time Left [" + df.format(timeLeft) + "]";
        }

        public void print() {
            System.out.println(toString());
        }
    }

}