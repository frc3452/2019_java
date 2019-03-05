package frc.robot.auto.pathadapter.fieldprofiles;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class FieldValues<T> {

    private final T mRedLeft, mBlueLeft, mRedRight, mBlueRight;

    public FieldValues(T value) {
        this(value, value, value, value);
    }

    /**
     * 
     * @param arr - RedLeft --> RedRight --> BlueLeft --> BlueRight
     */
    public FieldValues(ArrayList<T> arr) {
        this.mRedLeft = arr.get(0);
        this.mRedRight = arr.get(1);
        this.mBlueLeft = arr.get(2);
        this.mBlueRight = arr.get(3);
    }

    public FieldValues(T left, T right) {
        this(left, right, left, right);
    }

    public FieldValues(T redLeft, T redRight, T blueLeft, T blueRight) {
        this.mBlueLeft = blueLeft;
        this.mBlueRight = blueRight;

        this.mRedLeft = redLeft;
        this.mRedRight = redRight;
    }

    public T getRedLeft() {
        return this.mRedLeft;
    }

    public T getRedRight() {
        return this.mRedRight;
    }

    public T getLeft(Alliance a) {
        return get(a, true);
    }

    public T getRight(Alliance a) {
        return get(a, false);
    }

    public T getBlueLeft() {
        return this.mBlueLeft;
    }

    public T getBlueRight() {
        return this.mBlueRight;
    }

    @Override
    public String toString() {
        String out = "RedLeft [" + mRedLeft + "]\tRedRight [" + mRedRight + "]\tBlueLeft [" + mBlueLeft
                + "]\tBlueRight[" + mBlueRight + "]";

        return out;
    }

    public T get(Alliance a, boolean left) {
        switch (a) {
        case Red:
            if (left)
                return getRedLeft();
            return getRedRight();
        case Blue:
            if (left)
                return getBlueLeft();
            return getBlueRight();
        default:
            return get(Alliance.Red, left);
        }

    }

}