package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.kAuton;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator.RocketHeight;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.poofs.util.math.Pose2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.subsystems.Drive.RocketIdentifcation;
import frc.robot.subsystems.Intake.IntakeState;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;
import frc.robot.util.requests.QuickCompleteRequest;
import frc.robot.util.requests.Request;
import frc.robot.util.requests.RequestList;
import frc.robot.util.requests.RequestManager;

public class Superstructure extends GZSubsystem {

    private static Elevator elev_;
    private static Intake intake_;

    private GZSubsystemManager subsystems;

    private RequestManager manager = new RequestManager();

    private final Heights mDefaultHeight = Heights.HP_1;

    private static Superstructure mInstance = null;

    private RocketHeight mQueuedRocketHeight = null;

    public void queueRocketHeight(RocketHeight h) {
        if (h == RocketHeight.CARGO_SHIP) {
            System.out.println("ERROR Cannot queue cargo ship height!");
            return;
        }

        this.mQueuedRocketHeight = h;
        double numberOfSeconds;
        double rumblesPerSecond;

        switch (mQueuedRocketHeight) {
        case LOW:
            numberOfSeconds = 1.0 / 3.0;
            rumblesPerSecond = 3.0;
            break;
        case MIDDLE:
            numberOfSeconds = 0.5;
            rumblesPerSecond = 4.0;
            break;
        case HIGH:
            numberOfSeconds = 0.5;
            rumblesPerSecond = 6.0;
            break;
        default:
            numberOfSeconds = 0.0;
            rumblesPerSecond = 0.0;
            break;
        }
        GZOI.driverJoy.rumble(rumblesPerSecond, numberOfSeconds);
    }

    public static Superstructure getInstance() {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }

    private Superstructure() {
        elev_ = Elevator.getInstance();
        intake_ = Intake.getInstance();
        subsystems = new GZSubsystemManager(elev_, intake_); // intake
    }

    @Override
    public void loop() {
        if (GZOI.getInstance().isEnabled()) {
            for (int i = 0; i < 10; i++) {
                manager.update(Timer.getFPGATimestamp());
            }
        }
    }

    private Request waitForClaw(boolean open) {
        return new Request() {

            @Override
            public void act() {

            }

            @Override
            public boolean isFinished() {
                if (open)
                    return elev_.isClawOpen();
                return elev_.isClawClosed();
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
                    return elev_.areSlidesOut();
                return elev_.areSlidesIn();
            }
        };
    }

    private Request clawRequest(boolean open, boolean waitForCompletion) {
        return new QuickCompleteRequest(!waitForCompletion) {

            @Override
            public void _act() {
                if (open)
                    elev_.openClaw();
                else
                    elev_.closeClaw();
            }

            @Override
            public boolean _isFinished() {
                if (open)
                    return elev_.isClawOpen();
                return elev_.isClawClosed();
            }
        }.generate();
    }

    public void prepToGrabHatch() {
        RequestList list = new RequestList(this);
        list.extraLog("Prepping for grabbing");

        if (elev_.getHeightInches() > Heights.Cargo_1.inches) {
            var allQueues = new ArrayList<RequestList>();

            RequestList queue = new RequestList(this);
            queue.add(heightRequest(Heights.HP_1, false));
            queue.add(intakeRequest(false, false));
            queue.add(clawRequest(false, false));
            queue.add(slidesRequest(true, false));
            queue.setParallel();
            queue.add(waitForState());

            allQueues.add(queue);
            allQueues.add(new RequestList(this).extraLog("Prepped"));

            manager.request(list);
            manager.replaceQueue(allQueues);
        } else {
            list.add(intakeRequest(false, true));
            list.add(heightRequest(Heights.HP_1, true));
            list.add(clawRequest(false, true));
            list.add(slidesRequest(true, true));
            list.extraLog("Prepped");
            manager.request(list);
        }

    }

    public void zeroElevator() {
        RequestList list = new RequestList(this);
        list.add(new Request() {

            @Override
            public void act() {
                elev_.zero();
            }
        });
        manager.request(list);
    }

    // public static void main(String[] args) {
    // Pose2d here = new Pose2d(new Translation2d(49, 280), new Rotation2d(180+10));
    // Pose2d feeder = here.nearest(Arrays.asList(kAuton.Left_Feeder_Station,
    // kAuton.Right_Feeder_Station), 100);
    // if (feeder != null) {

    // System.out.println(((feeder.getTranslation().y() > 27 * 6) ? "Left" :
    // "Right")
    // + " feeder station identified, zeroing odometry");

    // Translation2d feeder_center =
    // feeder.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
    // feeder.getRotation().rotateBy(new Rotation2d(180)));

    // Rotation2d difference =
    // feeder.getRotation().inverse().rotateBy(here.getRotation());

    // Translation2d endpoint = feeder_center.rotateAround(feeder.getTranslation(),
    // difference);
    // System.out.println(endpoint);

    // } else {
    // System.out.println("Too far away from feeder station to identify Rocket");
    // }
    // }

    // public static void main(String[] args) {
    // Rocket r = Rocket.LEFT_NEAR;
    // double rotation = 0;
    // double jog = 0.0;
    // if (!r.equals(Rocket.NONE)) {
    // if (r.near) {
    // jog = 10;
    // rotation = 180;
    // } else {
    // jog = 10;
    // if (r.left) {
    // rotation = 90 + 45;
    // } else {
    // rotation = 270 - 45;
    // }
    // }
    // jog *= -1;

    // Pose2d here = new Pose2d(178.94, 286.79,
    // new Rotation2d(r.position.getRotation().rotateBy(new Rotation2d(-10))));

    // Rotation2d angleAwayFromRocket = r.position.getRotation().rotateBy(new
    // Rotation2d(180));

    // Translation2d bot_center =
    // r.position.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
    // angleAwayFromRocket);

    // Rotation2d difference =
    // r.position.getRotation().rotateBy(here.getRotation().inverse());

    // Translation2d endpoint = bot_center.rotateAround(r.position.getTranslation(),
    // difference.inverse());
    // }
    // }

    public void grabHatchFromFeeder() {
        RequestList list = new RequestList(this);
        list.extraLog("Grabbing hatch from feeder station");
        Pose2d here = new Pose2d(Drive.getInstance().getFixedPose());

        Pose2d feeder = here.nearest(Arrays.asList(kAuton.Left_Feeder_Station, kAuton.Right_Feeder_Station), 100);

        Translation2d zeroPosition;

        // Feeder identified
        if (feeder != null) {
            list.extraLog(((feeder.getTranslation().y() > 27 * 6) ? "Left" : "Right")
                    + " feeder station identified, zeroing odometry");

            // Where the robot would be if perfectly lined up with feeder station
            Translation2d feeder_center = feeder.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
                    feeder.getRotation().rotateBy(new Rotation2d(180)));

            // If our rotation is within _ deg, (not totally messed up), adjust our odometry
            // accordingly
            if (Math.abs(here.getRotation().distanceDeg(new Rotation2d(180))) < 35) {
                Rotation2d difference = feeder.getRotation().inverse().rotateBy(here.getRotation());
                zeroPosition = feeder_center.rotateAround(feeder.getTranslation(), difference);
            } else {
                zeroPosition = feeder_center;
            }

            list.add(Drive.getInstance().setOdometryRequest(zeroPosition));
        } else {
            list.extraLog(("Too far away from feeder station to identify Rocket"));
        }

        list.add(clawRequest(true, true));
        list.add(slidesRequest(false, true));
        if (feeder != null) {
            list.add(Drive.getInstance().jogRequest(new EncoderMovement(-10)));
            list.add(Drive.getInstance().headingRequest(Rotation2d.fromDegrees(0), true));
        }
        list.extraLog("Finished grabbing hatch");
        manager.request(list);
    }

    public void scoreHatch() {
        RequestList list = new RequestList(this);

        list.extraLog("Scoring hatch");

        RocketIdentifcation r = Drive.getInstance().getRocket();
        list.extraLog(r.how);

        double rotation = 0;
        double jog = 0.0;
        // ArcadeSignal after = new ArcadeSignal();
        if (r.rocket.identified()) {
            list.add(GZOI.driverJoy.rumbleRequest(6, .5));
            if (r.rocket.near) {
                rotation = 180;
            } else {
                if (r.rocket.left) {
                    rotation = 90 + 45;
                } else {
                    rotation = 270 - 45;
                }
            }
            jog = 20;
            jog *= -1;
            // after = new ArcadeSignal(.5, 0, 1);

            Translation2d endpoint;

            // Angle to translate away from exact point
            Rotation2d angleAwayFromRocket = r.rocket.position.getRotation().rotateBy(new Rotation2d(180));

            // Scoot absolute point away by half a robot to find new center
            Translation2d bot_center = r.rocket.position.getTranslation().translateBy(kAuton.ROBOT_LENGTH / 2.0,
                    angleAwayFromRocket);

            // Should we twist our new point according to the gyro
            if (r.recalibrateWithGyro) {
                // How much we were off by
                Rotation2d difference = r.rocket.position.getRotation().rotateBy(r.here.getRotation().inverse());
                // New point
                endpoint = bot_center.rotateAround(r.rocket.position.getTranslation(), difference.inverse());
            } else {
                // Can't use gyro adjustment, use absolute point
                endpoint = bot_center;
            }

            list.extraLog("Placing on rocket " + r.rocket + ". Backing up " + jog + " and turning to " + rotation);
            list.add(Drive.getInstance().setOdometryRequest(endpoint));
        }

        list.add(slidesRequest(true, true));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));

        RequestList finalMove = new RequestList(this);

        finalMove.add(heightRequest(mDefaultHeight, !r.rocket.identified()));

        if (r.rocket.identified()) {
            finalMove.add(Drive.getInstance().jogRequest(new EncoderMovement(jog), true));
            finalMove.add(Drive.getInstance().turnToHeadingRequest(Rotation2d.fromDegrees(rotation), false));
            // finalMove.add(Drive.getInstance().openLoopRequest(after));
        }

        finalMove.extraLog("Hatch scoring completed");

        manager.request(list, finalMove);
    }

    public void operatorIntake() {
        if (Intake.getInstance().isRetracted()) {
            intake();
        } else if (Intake.getInstance().isExtended()) {
            toggleIntake();
        }
    }

    public void intake() {
        RequestList list = new RequestList(this);
        list.extraLog("Getting ready to intake cargo");
        list.add(heightRequest(Heights.Home, false));
        list.add(slidesRequest(false, false));
        list.add(clawRequest(true, false));
        list.add(intakeRequest(true, false));
        // list.add(waitForState());
        list.setParallel();

        var queue = new RequestList(this);
        queue.add(runIntakeRequest(IntakeState.INTAKING));
        queue.extraLog("Intaking cargo");
        manager.request(list, queue);
    }

    public void intakeEject() {
        RequestList list = new RequestList(this);
        list.extraLog("Preparing to eject cargo");
        list.add(intakeRequest(true, true));
        list.add(runIntakeRequest(IntakeState.EJECTING));
        list.setParallel();
        var queue = new RequestList(this);
        queue.extraLog("Ejecting cargo");
        manager.request(list, queue);
    }

    public void toggleIntake() {
        RequestList list = new RequestList(this);
        list.add(intakeRequest(!intake_.isExtended(), true));
        list.extraLog("Toggling intake");
        manager.request(list);
    }

    public void toggleIntakeRoller() {
        RequestList list = new RequestList(this);

        IntakeState newState;

        if (intake_.getWantedState() != IntakeState.NEUTRAL)
            newState = IntakeState.NEUTRAL;
        else
            newState = IntakeState.INTAKING;

        list.extraLog("Turning on " + ((newState == IntakeState.NEUTRAL) ? "off" : "on"));
        list.add(runIntakeRequest(newState));
        manager.request(list);
    }

    private Request heightJogRequest(double inches) {
        return new Request() {

            @Override
            public void act() {
                elev_.jogHeight(inches);
            }

            @Override
            public boolean isFinished() {
                return elev_.nearTarget();
            }
        };
    }

    public void handOffCargo() {
        RequestList list = new RequestList(this);
        list.extraLog("Handing off cargo");
        list.add(changeGamePieceRequest(false));
        list.add(intakeRequest(false, true));
        list.add(clawRequest(false, true));
        list.add(heightRequest(Heights.HP_1.inches, true));

        list.extraLog("Cargo handed off");
        manager.request(list);
    }

    private Request changeGamePieceRequest(boolean hatchPanel) {
        return new Request() {

            @Override
            public void act() {
                elev_.setHasHatchPanel(hatchPanel);
            }
        };
    }

    public void stow() {
        RequestList firstMove = new RequestList(this);
        firstMove.add(slidesRequest(false, true));
        firstMove.add(clawRequest(true, true));

        if (elev_.getHeightInches() > Heights.Cargo_1.inches) {
            firstMove.setParallel();
        }

        RequestList secondMove = new RequestList(this);
        secondMove.add(intakeRequest(false, true));
        secondMove.add(heightRequest(Heights.Home));
        manager.request(firstMove, secondMove);
    }

    public void rocketHeight(RocketHeight height) {
        setHeight(Heights.getHeight(height, elev_.isMovingHP()));
    }

    public void jogElevator(double jog) {
        elev_.jogHeight(jog);
    }

    private Request runIntakeRequest(IntakeState state) {
        return new Request() {

            @Override
            public void act() {
                intake_.setWantedState(state);
            }
        };
    }

    public boolean preppedForFeeder() {
        return SuperstructureState.isAt(new SuperstructureState(Heights.HP_1.inches, true, false, false));
        // return intake_.isRetracted() && elev_.near(Heights.HP_1.inches) &&
        // elev_.isClawClosed();
    }

    private void grabCargoFromFeeder() {
        RequestList list = new RequestList(this);
        list.extraLog("Grabbing cargo from feeder station");
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(Drive.getInstance().jogRequest(new EncoderMovement(-10)));
        list.add(heightRequest(Heights.Cargo_1));
        list.extraLog("Cargo grabbed");
        manager.request(list);
    }

    public void retrieve() {
        if (intake_.isExtended()) {
            handOffCargo();
        } else if (intake_.isRetracted()) {
            if (elev_.isMovingHP()) {
                grabHatchFromFeeder();
            } else {
                grabCargoFromFeeder();
            }
        }
    }

    public void driverRetrieve() {
        if (Intake.getInstance().isRetracted()) {
            advanceFeederStage();
        } else if (Intake.getInstance().isExtended()) {
            handOffCargo();
        }
    }

    public void cancel() {
        manager.clear();
        manager.request(new RequestList(this).extraLog("Superstructure cleared"));
    }

    public void toggleClaw() {
        manager.request(new Request() {

            @Override
            public void act() {
                elev_.toggleClaw();
            }
        });
    }

    public void toggleSlides() {
        manager.request(new Request() {

            @Override
            public void act() {
                elev_.toggleSlides();
            }
        });
    }

    public void setHeight(Heights h) {
        RequestList list = new RequestList(this);
        list.extraLog("Moving to height " + h);
        list.add(heightRequest(h));
        list.extraLog("At desired height");
        manager.request(list);
    }

    public void score() {
        score(false);
    }

    public void score(boolean driver) {
        if (driver) {
            if (mQueuedRocketHeight != null) {
                Request request = heightRequest(Heights.getHeight(mQueuedRocketHeight, elev_.isMovingHP()));
                manager.request(request);
                mQueuedRocketHeight = null;
            } else {
                runScore(true);
            }
        } else {
            runScore(false);
        }
    }

    public void runScore(boolean driver) {
        if (elev_.isMovingHP())
            scoreHatch();
        else {
            scoreCargo(driver);
        }
    }

    public void scoreCargo(boolean driver) {

        ArrayList<RequestList> list = new ArrayList<>();

        if (driver) {
            if (elev_.getHeightInches() < (Heights.Cargo_1.inches - 2)) {
                GZFiles.getInstance().addLog(this, "ERROR Cannot throw cargo, at bad height", true);
                return;
            }
        }

        // Prep for throw
        {
            RequestList prepForThrow = new RequestList(this);
            prepForThrow.extraLog("Scoring cargo");

            prepForThrow.add(clawRequest(false, false));
            prepForThrow.add(slidesRequest(false, false));
            prepForThrow.setParallel();
            list.add(prepForThrow);
        }

        // Wait
        {
            RequestList wait = new RequestList(this);
            wait.add(waitForSlides(false));
            wait.add(waitForClaw(false));
            wait.setParallel();
            list.add(wait);
        }

        // Shoot

        {
            RequestList shoot = new RequestList(this);
            shoot.add(clawRequest(true, false));
            shoot.add(slidesRequest(true, false));
            shoot.setParallel();
            list.add(shoot);
        }

        // // Wait
        {
            RequestList wait = new RequestList(this);
            wait.add(waitForSlides(true));
            wait.add(slidesRequest(false, true));
            wait.extraLog("Completed scoring cargo");
            list.add(wait);
        }

        if (driver) {
            list.add(new RequestList(this).add(new Request() {

                @Override
                public void act() {
                    stow();
                }
            }));
        }

        // // Pull back
        manager.clear();
        manager.replaceQueue(list);
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

        public static boolean isAtTarget() {
            return isAt(getTarget());
        }

        public static SuperstructureState getTarget() {
            return new SuperstructureState(elev_.getTarget(), elev_.doSlidesWantOut(), elev_.clawWantsOpen(),
                    intake_.wantsOut());
        }

    }

    private Request waitForState() {
        return new Request() {

            @Override
            public void act() {
            }

            @Override
            public boolean isFinished() {
                return SuperstructureState.isAtTarget();
            }
        };
    }

    private Request slidesRequest(boolean extended, boolean waitForCompletion) {
        return new QuickCompleteRequest(!waitForCompletion) {

            @Override
            public void _act() {
                if (extended) {
                    elev_.extendSlides();
                } else {
                    elev_.retractSlides();
                }
            }

            @Override
            public boolean _isFinished() {
                return elev_.slidesAtDesired();
            }
        }.generate();

    }

    private Request intakeRequest(boolean extended, boolean waitForCompletion) {
        return new QuickCompleteRequest(!waitForCompletion) {

            @Override
            public void _act() {
                if (extended)
                    intake_.extend();
                else
                    intake_.retract();
            }

            @Override
            public boolean _isFinished() {
                // System.out.println("Intake request");
                return !intake_.wantsToMove();
            }
        }.generate();
    }

    private Request heightRequest(Heights height) {
        return heightRequest(height, true);
    }

    private Request heightRequest(Heights height, boolean waitForCompletion) {
        return new QuickCompleteRequest(!waitForCompletion) {

            @Override
            public void _act() {
                elev_.setHeight(height);
            }

            @Override
            public boolean _isFinished() {
                return elev_.nearTarget();
            }
        }.generate();
    }

    private Request heightRequest(double height, boolean waitForCompletion) {
        return new QuickCompleteRequest(!waitForCompletion) {

            @Override
            public void _act() {
                elev_.setHeight(height);
            }

            @Override
            public boolean _isFinished() {
                return elev_.nearTarget();
            }
        }.generate();
    }

    public void dropCrawler() {
        Pneumatics.getInstance().dropCrawler();
    }

    public String getStateString() {
        return "";
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

    public void elevManual(Double manual) {
        elev_.manual(manual);
    }

    public void advanceFeederStage() {
        if (!preppedForFeeder()) {
            prepToGrabHatch();
        } else {
            grabHatchFromFeeder();
        }
        manager.queue(GZOI.driverJoy.rumbleRequest(8, .125));
    }

}