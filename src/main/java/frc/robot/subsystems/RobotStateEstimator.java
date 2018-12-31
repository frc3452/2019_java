package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.poofs.Kinematics;
import frc.robot.poofs.RobotState;
import frc.robot.poofs.geometry.Rotation2d;
import frc.robot.poofs.geometry.Twist2d;
import frc.robot.util.GZSubsystem;

public class RobotStateEstimator extends GZSubsystem {
    static RobotStateEstimator instance_ = new RobotStateEstimator();
    private RobotState robot_state_ = RobotState.getInstance();
    private Drive drive_ = Drive.getInstance();
    private double left_encoder_prev_distance_ = 0.0;
    private double right_encoder_prev_distance_ = 0.0;

    RobotStateEstimator() {
        left_encoder_prev_distance_ = drive_.getLeftEncoderDistance();
        right_encoder_prev_distance_ = drive_.getRightEncoderDistance();
    }

    public static RobotStateEstimator getInstance() {
        return instance_;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean hasMotors() {
        return false;
    }

    @Override
    public void addMotorTestingGroups() {
    }

    @Override
    public void addLoggingValues() {
    }

    @Override
    public void loop() {
        final double timestamp = Timer.getFPGATimestamp();
        final double left_distance = drive_.getLeftEncoderDistance();
        final double right_distance = drive_.getRightEncoderDistance();
        final double delta_left = left_distance - left_encoder_prev_distance_;
        final double delta_right = right_distance - right_encoder_prev_distance_;
        final Rotation2d gyro_angle = drive_.getHeading();
        final Twist2d odometry_velocity = robot_state_.generateOdometryFromSensors(delta_left, delta_right, gyro_angle);
        final Twist2d predicted_velocity = Kinematics.forwardKinematics(drive_.getLeftLinearVelocity(),
                drive_.getRightLinearVelocity());
        robot_state_.addObservations(timestamp, odometry_velocity, predicted_velocity);
        left_encoder_prev_distance_ = left_distance;
        right_encoder_prev_distance_ = right_distance;

    }

    @Override
    public String getStateString() {
        return "N/A";
    }

    @Override
    protected void in() {
    }

    @Override
    protected void out() {
    }

    @Override
    protected void initDefaultCommand() {
    }

    // public synchronized void onStart(double timestamp) {
    // left_encoder_prev_distance_ = drive_.getLeftEncoderDistance();
    // right_encoder_prev_distance_ = drive_.getRightEncoderDistance();
    // }

    // @Override
    // public synchronized void onLoop(double timestamp) {
    // }

}
