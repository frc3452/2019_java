package frc.robot.util;

import java.util.ArrayList;

public class GZArrayList<T> {
    private ArrayList<T> mArr = new ArrayList<T>();

    public GZArrayList<T> add(T input) {
        mArr.add(input);
        return this;
    }

    public ArrayList<T> done() {
        return mArr;
    }

}