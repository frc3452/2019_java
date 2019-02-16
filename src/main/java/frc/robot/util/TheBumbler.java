package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public abstract class TheBumbler<T> {

    private double mPrevTime;

    private ArrayList<TimeValue<T>> mQueue = new ArrayList<TimeValue<T>>();

    public TheBumbler() {
    }

    public T update() {
        if (isQueueEmpty())
            return getDefault();
        final double now = Timer.getFPGATimestamp();

        if (mQueue.get(0).done()) {
            mQueue.remove(0);
        } else {
            mQueue.get(0).deductTimeBy(now - mPrevTime);
        }

        mPrevTime = now;
        return mQueue.get(0).value;
    }

    public void clear() {
        mQueue.clear();
    }

    public boolean isQueueEmpty() {
        return mQueue.size() == 0;
    }

    public abstract T getDefault();

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

    public void addToQueue(T value, double time, int times) {
        addToQueue(new TimeValue<T>(value, time), new TimeValue<T>(getDefault(), time), times);
    }

    public static class TimeValue<T> {
        private final T value;
        private final double time;

        private double timeLeft;

        public TimeValue(T value, double time) {
            this.value = value;
            this.time = time;
            timeLeft = time;
        }

        public void deductTimeBy(double time) {
            timeLeft -= time;
        }

        public boolean done() {
            return timeLeft <= 0;
        }
    }

}