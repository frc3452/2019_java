package frc.robot.subsystems;

import java.util.Arrays;

import frc.robot.Constants.kAuton;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator.RocketHeight;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.poofs.util.math.Pose2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.subsystems.Drive.Rocket;
import frc.robot.subsystems.Intake.IntakeState;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZLog.InstantLogItem;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;
import frc.robot.util.requests.Request;
import frc.robot.util.requests.RequestList;
import frc.robot.util.requests.RequestManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZSubsystemManager subsystems;

    private RequestManager manager = new RequestManager();

    private final Heights mDefaultHeight = Heights.HP_1;

    private static Superstructure mInstance = null;

    public static Superstructure getInstance() {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }

    private Superstructure() {
        subsystems = new GZSubsystemManager(elev, intake); // intake
    }

    @Override
    public void loop() {
        if (GZOI.getInstance().isEnabled())
            manager.update();
    }

    private Request waitForClaw(boolean open) {
        return new Request() {

            @Override
            public void act() {

            }

            @Override
            public boolean isFinished() {
                if (open)
                    return elev.isClawOpen();
                return elev.isClawClosed();
            }
        };
    }

    private Request waitForSlides(boolean extended) {
        return new Request() {

            @Override
            public void act() {

            }

            @Override
            public boolean isFinished() {
                if (extended)
                    return elev.areSlidesOut();
                return elev.areSlidesIn();
            }
        };
    }

    private Request clawRequest(boolean open, boolean waitForCompletion) {
        return new Request() {

            @Override
            public void act() {
                if (open)
                    elev.openClaw();
                else
                    elev.closeClaw();
            }

            @Override
            public boolean isFinished() {
                if (waitForCompletion)
                    if (open) {
                        return elev.isClawOpen();
                    } else {
                        return elev.isClawClosed();
                    }
                else
                    return true;
            }
        };
    }

    public void prepForFeeder() {
        RequestList list = new RequestList();
        list.add(log("Prepping for feeder station"));
        list.add(heightRequest(Heights.HP_1, true));
        list.add(intakeRequest(false, true));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(true, true));
        list.add(log("Prepped for feeder station"));

        manager.request(list);
    }

    public void zeroElevator() {
        RequestList list = new RequestList();
        list.add(new Request() {

            @Override
            public void act() {
                elev.zero();
            }
        });
        manager.request(list);
    }

    public static void main(String[] args) {
        Pose2d here = new Pose2d(new Translation2d(49, 280), new Rotation2d(180 + 10));
        Pose2d feeder = here.nearest(Arrays.asList(kAuton.Left_Feeder_Station, kAuton.Right_Feeder_Station), 100);
        Translation2d feeder_center = feeder.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
                feeder.getRotation().rotateBy(new Rotation2d(180)));

        feeder.print("Feeder");
        feeder_center.print("New Center");

        Rotation2d difference = feeder.getRotation().inverse().rotateBy(here.getRotation());

        Translation2d endpoint = feeder_center.rotateAround(feeder.getTranslation(), difference);

        kAuton.Right_Rocket_Near.getRotation().print().inverse().print();

        // endpoint.print();
        // endpoint =

        // Rotation2d difference = here.getRotation().rotateBy(new
        // Rotation2d(180)).inverse();
        // new Translation2d(1, 0).direction().print();
        // new Translation2d(.5, .5).direction().print();
        // kAuton.Right_Rocket_Far.print().translateBy(kAuton.ROBOT_LENGTH / 2.0, new
        // Rotation2d(61.25)).print();
    }

    private Request log(String message) {
        return new Request() {

            @Override
            public void act() {
                GZFiles.getInstance().addLog(Superstructure.getInstance(), message);
            }
        };
    }

    public void grabHatchFromFeeder() {

        RequestList list = new RequestList();
        list.add(log("Grabbing hatch from feeder station"));
        {
            Pose2d here = new Pose2d(new Translation2d(49, 280), new Rotation2d(180 + 10));
            Pose2d feeder = here.nearest(Arrays.asList(kAuton.Left_Feeder_Station, kAuton.Right_Feeder_Station), 100);
            if (feeder != null) {
                list.add(log(((feeder.getTranslation().y() > 27 * 6) ? "Left" : "Right")
                        + " feeder station identified, zeroing odometry"));

                Translation2d feeder_center = feeder.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
                        feeder.getRotation().rotateBy(new Rotation2d(180)));

                Rotation2d difference = feeder.getRotation().inverse().rotateBy(here.getRotation());

                Translation2d endpoint = feeder_center.rotateAround(feeder.getTranslation(), difference);
                list.add(Drive.getInstance().setOdometryRequest(endpoint));
            } else {
                list.add(log(("Too far away from feeder station to identify Rocket")));
            }
        }

        list.add(clawRequest(true, true));
        list.add(slidesRequest(false, true));
        list.add(Drive.getInstance().jogRequest(new EncoderMovement(-10)));
        list.add(Drive.getInstance().headingRequest(Rotation2d.fromDegrees(0), true));
        list.add(log("Finished grabbing hatch"));
        manager.request(list);
    }

    public void scoreHatch() {
        RequestList list = new RequestList();

        list.add(log("Scoring hatch"));
        Rocket r = Drive.getInstance().getPosition();
        double rotation = 0;
        double jog = 0.0;
        if (!r.equals(Rocket.NONE)) {
            if (r.near) {
                jog = 10;
                rotation = 180;
            } else {
                jog = 10;

                if (r.left) {
                    rotation = 90 + 45;
                } else {
                    rotation = 270 - 45;
                }
            }
            jog *= -1;

            list.add(log("Placing on rocket " + r + ". Backing up " + jog + " and turning to " + rotation));
            list.add(Drive.getInstance().setOdometryRequest(r.position));
        }

        list.add(slidesRequest(true, true));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(heightRequest(mDefaultHeight, false));

        if (!r.equals(Rocket.NONE)) {
            list.add(Drive.getInstance().jogRequest(new EncoderMovement(jog), true));
            list.add(Drive.getInstance().turnToHeadingRequest(Rotation2d.fromDegrees(rotation), false));
        }

        list.add(log("Hatch scoring completed"));

        manager.request(list);
    }

    public void intake() {
        RequestList list = new RequestList();
        list.add(log("Getting ready to intake cargo"));
        list.add(heightRequest(Heights.Home, false));
        list.add(slidesRequest(false, false));
        list.add(clawRequest(true, false));
        list.add(intakeRequest(true, false));
        list.add(waitForState(new SuperstructureState(Heights.Home.inches, false, true, true)));
        list.add(runIntakeRequest(IntakeState.INTAKING));
        list.add(log("Intaking cargo"));
        manager.request(list);
    }

    public void intakeEject() {
        RequestList list = new RequestList();
        list.add(log("Preparing to eject cargo"));
        list.add(intakeRequest(true, true));
        list.add(runIntakeRequest(IntakeState.EJECTING));
        list.add(log("Ejecting cargo"));
        manager.request(list);
    }

    public void toggleIntake() {
        RequestList list = new RequestList();
        list.add(intakeRequest(!intake.isExtended(), true));
        list.add(log("Toggling intake"));
    }

    public void toggleIntakeRoller() {
        RequestList list = new RequestList();

        IntakeState newState;

        if (intake.getWantedState() != IntakeState.NEUTRAL)
            newState = IntakeState.NEUTRAL;
        else
            newState = IntakeState.INTAKING;

        list.add(log("Turning on " + ((newState == IntakeState.NEUTRAL) ? "off" : "on")));
        list.add(runIntakeRequest(newState));
        manager.request(list);
    }

    private Request heightJogRequest(double inches) {
        return new Request() {

            @Override
            public void act() {
                elev.jogHeight(inches);
            }

            @Override
            public boolean isFinished() {
                return elev.nearTarget();
            }
        };
    }

    public void handOffCargo() {
        RequestList list = new RequestList();
        list.add(log("Handing off cargo"));
        list.add(intakeRequest(false, true));
        list.add(clawRequest(false, true));
        list.add(heightRequest(Heights.HP_1));
        list.add(new Request() {

            @Override
            public void act() {
                elev.setHasHatchPanel(false);
            }
        });

        list.add(log("Cargo handed off"));
        manager.request(list);
    }

    public void rocketHeight(RocketHeight height) {
        setHeight(Heights.getHeight(height, elev.isMovingHP()));
    }

    public void jogElevator(double jog) {
        elev.jogHeight(jog);
    }

    private Request runIntakeRequest(IntakeState state) {
        return new Request() {

            @Override
            public void act() {
                intake.setWantedState(state);
            }
        };
    }

    public boolean readyToGrabFromFeeder() {
        return SuperstructureState.isAt(new SuperstructureState(Heights.HP_1.inches, true, false, false));
    }

    private void grabCargoFromFeeder() {
        RequestList list = new RequestList();
        list.add(log("Grabbing cargo from feeder station"));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(Drive.getInstance().jogRequest(new EncoderMovement(-10)));
        list.add(heightRequest(Heights.Cargo_1));
        list.add(log("Cargo grabbed"));
        manager.request(list);
    }

    public void retrieve() {
        if (elev.isMovingHP()) {
            grabHatchFromFeeder();
        } else {
            if (intake.isExtended()) {
                handOffCargo();
            } else {
                grabCargoFromFeeder();
            }
        }
    }

    public void cancel() {
        manager.clear();
        manager.request(log("Cleared"));
    }

    public void toggleClaw() {
        manager.request(new Request() {

            @Override
            public void act() {
                elev.toggleClaw();
            }
        });
    }

    public void toggleSlides() {
        manager.request(new Request() {

            @Override
            public void act() {
                elev.toggleSlides();
            }
        });
    }

    public void setHeight(Heights h) {
        RequestList list = new RequestList();
        list.add(log("Moving to height " + h));
        list.add(heightRequest(h));
        list.add(log("At desired height"));
        manager.request(list);
    }

    public void score() {
        if (elev.isMovingHP())
            scoreHatch();
        else
            scoreCargo();
    }

    public void scoreCargo() {
        RequestList list = new RequestList();
        list.add(log("Scoring cargo"));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(clawRequest(true, false));
        list.add(Request.waitRequest(.125));
        list.add(slidesRequest(true, false));
        list.add(waitForSlides(true));
        list.add(slidesRequest(false, true));
        list.add(log("Completed scoring cargo"));
        manager.request(list);
    }

    public static class SuperstructureState {
        public final SolenoidState slides;
        public final SolenoidState claw;
        public final SolenoidState intake;
        public final double height;

        public SuperstructureState() {
            slides = Elevator.getInstance().getSlidesState();
            claw = Elevator.getInstance().getClawState();
            intake = Intake.getInstance().getSolenoidState();
            height = Elevator.getInstance().getHeightInches();
        }

        public SuperstructureState(double height, boolean slidesExtended, boolean clawOpen, boolean intakeExtended) {
            this.height = height;
            this.slides = (slidesExtended ? SolenoidState.ON : SolenoidState.OFF);
            this.claw = (clawOpen ? SolenoidState.OFF : SolenoidState.ON);
            this.intake = (intakeExtended ? SolenoidState.ON : SolenoidState.OFF);
        }

        public boolean equals(SuperstructureState other, double elevatorEpsilon) {
            boolean equals = true;
            equals &= Math.abs(other.height - this.height) < elevatorEpsilon;
            equals &= this.slides.equals(other.slides);
            equals &= this.claw.equals(other.claw);
            equals &= this.intake.equals(other.intake);
            return equals;
        }

        public boolean equals(SuperstructureState other) {
            return equals(other, kElevator.TARGET_TOLERANCE);
        }

        public static boolean isAt(SuperstructureState other) {
            return other.equals(new SuperstructureState());
        }

    }

    private Request waitForState(SuperstructureState state) {
        return new Request() {

            @Override
            public void act() {

            }

            @Override
            public boolean isFinished() {
                return SuperstructureState.isAt(state);
            }
        };
    }

    private Request slidesRequest(boolean extended, boolean waitForCompletion) {
        return new Request() {

            @Override
            public void act() {
                if (extended)
                    elev.extendSlides();
                else
                    elev.retractSlides();
            }

            @Override
            public boolean isFinished() {
                if (waitForCompletion)
                    if (extended) {
                        return elev.areSlidesOut();
                    } else {
                        return elev.areSlidesIn();
                    }
                else
                    return true;
            }
        };
    }

    private Request intakeRequest(boolean extended, boolean waitForCompletion) {
        return new Request() {

            @Override
            public void act() {
                if (extended)
                    intake.extend();
                else
                    intake.retract();
            }

            @Override
            public boolean isFinished() {
                if (waitForCompletion)
                    if (extended) {
                        return intake.isExtended();
                    } else {
                        return intake.isRetracted();
                    }
                else
                    return true;
            }
        };
    }

    private Request heightRequest(Heights height) {
        return heightRequest(height, true);
    }

    private Request heightRequest(Heights height, boolean waitForCompletion) {
        return new Request() {

            @Override
            public void act() {
                elev.setHeight(height);
            }

            @Override
            public boolean isFinished() {
                if (waitForCompletion)
                    return elev.nearTarget();
                return true;
            }
        };
    }

    // public void goToHeight(Heights h) {
    // manager.request(heightRequest(h));
    // }

    public void dropCrawler() {
        Pneumatics.getInstance().dropCrawler();
    }

    public String getStateString() {
        return "";
        // return mAction.toString() + " STEP " + mFlag.numOfFlagsTripped();
    }

    @Override
    public void stop() {
        subsystems.stop();
    }

    @Override
    public String getSmallString() {
        return "SPR-STRCT";
    }

    public void addLoggingValues() {
    }

    protected void initDefaultCommand() {
    }

    public void elevManual(Double leftAnalogY) {
        elev.manual(leftAnalogY);
    }

    public void advanceFeederStage() {
        if (!readyToGrabFromFeeder()) {
            prepForFeeder();
        } else {
            grabHatchFromFeeder();
        }
    }

}