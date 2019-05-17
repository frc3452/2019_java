package frc.robot.subsystems;

import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kIntake;
import frc.robot.GZOI;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;
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
        list.add(heightRequest(Heights.HP_1, true));
        list.add(clawRequest(true, true));
        manager.request(list);
    }

    public void grabFromFeeder() {
        RequestList list = new RequestList();
        list.add(clawRequest(true, true));
        list.add(slidesRequest(false, true));
        manager.request(list);
    }

    public void scoreHatch() {
        RequestList list = new RequestList();
        list.add(slidesRequest(true, true));
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(heightRequest(mDefaultHeight, false));
    }

    public void intake() {
        RequestList list = new RequestList();
        list.add(intakeRequest(true, true));
        list.add(runIntakeRequest(kIntake.INTAKE_SPEED));
        // list.add()
        // list.add()
    }

    private Request runIntakeRequest(double percentage) {
        return new Request() {

            @Override
            public void act() {
                intake.runIntake(percentage);
            }
        };
    }

    public void score() {
        if (elev.isMovingHP())
            scoreHatch();
        else
            scoreCargo();
    }

    public void scoreCargo() {
        RequestList list = new RequestList();
        list.add(clawRequest(false, true));
        list.add(slidesRequest(false, true));
        list.add(slidesRequest(true, false));
        list.add(clawRequest(true, false));
        list.add(waitForClaw(true));
        list.add(waitForSlides(true));
        list.add(Request.waitRequest(.25));
        list.add(slidesRequest(false, true));
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

    public void pauseIntake() {
        intake.pauseIntake();
    }

}