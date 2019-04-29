package frc.robot.util;

public class GZPrevious<T> {
    private T prev;

    public GZPrevious(T start) {
        prev = start;
    }

    public boolean update(T newValue) {
        T temp = prev;
        prev = newValue;
        return !temp.equals(newValue);
    }
}