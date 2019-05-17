package frc.robot.poofs.util.math;

public interface ICurvature<S> extends State<S> {
    double getCurvature();

    double getDCurvatureDs();
}
