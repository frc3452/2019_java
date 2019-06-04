package frc.robot.poofs.util.math;

public abstract class GZGeometry<T> {

    public T print() {
        return print("");
    }

    public T print(String message) {
        String out = "";

        if (!message.equals("")) {
            out += "[" + message + "]\t";
        }

        out += toString();
        System.out.println(out);
        return (T) this;
    }
}