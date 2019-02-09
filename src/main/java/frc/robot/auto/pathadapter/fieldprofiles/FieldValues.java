package frc.robot.auto.pathadapter.fieldprofiles;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class FieldValues<T> {

    private final T mRedLeft, mBlueLeft, mRedRight, mBlueRight;

    public FieldValues(T value) {
        this(value, value, value, value);
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