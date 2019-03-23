package frc.robot.util.drivers.controllers;

import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;

public class OperatorController extends DeepSpaceController {

        private boolean firstSet = false;
        private boolean isButtonBoard = true;
        private boolean p_isButtonBoard = false;

        public OperatorController() {
                this(1);
        }

        public OperatorController(int port) {
                super(port);

                this.idle = new GZButton(this, () -> false, () -> false);
                this.cancel = new GZButton(this, () -> false, () -> false);

                this.queueAction = new GZButton(this, () -> false, () -> false);

                this.elevatorZero = new GZButton(this, () -> false, () -> getButton(Buttons.X));

                this.hatchPanel1 = new GZButton(this, true, () -> false,
                                () -> getButton(Buttons.A) && !getButton(Buttons.LEFT_CLICK));
                this.hatchPanel2 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.B) && !getButton(Buttons.LEFT_CLICK));
                this.hatchPanel3 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.Y) && !getButton(Buttons.LEFT_CLICK));

                this.cargo1 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.LEFT_CLICK) && getButton(Buttons.A));
                this.cargo2 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.LEFT_CLICK) && getButton(Buttons.B));
                this.cargo3 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.LEFT_CLICK) && getButton(Buttons.Y));
                // this.cargoShip = new GZButton(this, () -> false, () ->
                // getButton(Buttons.START));
                this.cargoShip = new GZButton(this, () -> false, () -> false);

                this.elevatorJogUp = new GZButton(this, () -> false, () -> getDUp());
                this.elevatorJogDown = new GZButton(this, () -> false, () -> getDDown());
                this.elevatorManual = new GZButton(this, () -> false, () -> getDLeft());

                this.slidesToggle = new GZButton(this, () -> false, () -> getButton(Buttons.LB));
                this.clawToggle = new GZButton(this, () -> false, () -> getButton(Buttons.RB));

                // this.intakeCargo = new GZButton(this, () -> false, () -> false);
                this.intakeUp = new GZButton(this, () -> false, () -> getButton(Buttons.BACK));
                this.intakeCargo = new GZButton(this, () -> false, () -> getButton(Buttons.START));
                // this.intakeDown = new GZButton(this, () -> false, () ->
                // getButton(Buttons.BACK));
                this.intakeDown = new GZButton(this, () -> false, () -> false);

                this.stow = new GZButton(this, () -> false, () -> false);

                this.hatchFromFeed = new GZButton(this, () -> false,
                                () -> getLeftTriggerPressed() && Elevator.getInstance().isMovingHP()
                                                && !Superstructure.getInstance().isIntakingCargo());
                this.cargoFromFeed = new GZButton(this, () -> false,
                                () -> getLeftTriggerPressed() && !Elevator.getInstance().isMovingHP()
                                                && !Superstructure.getInstance().isIntakingCargo());

                this.cargoGrabWhileGroundIntaking = new GZButton(this, () -> false,
                                () -> getLeftTriggerPressed() && Superstructure.getInstance().isIntakingCargo());

                this.scoreHatch = new GZButton(this, () -> false,
                                () -> getRightTriggerPressed() && Elevator.getInstance().isMovingHP());
                this.shootCargo = new GZButton(this, () -> false,
                                () -> getRightTriggerPressed() && !Elevator.getInstance().isMovingHP());

                this.dropCrawler = new GZButton(this, () -> false, () -> getButton(Buttons.RIGHT_CLICK));

        }

        public void setButtonBoard(boolean isButtonBoard) {
                this.isButtonBoard = isButtonBoard;

                if (this.isButtonBoard != this.p_isButtonBoard || !firstSet) {
                        System.out.println("WARNING Operator controller selected: "
                                        + (this.isButtonBoard ? "Button board" : "Xbox controller"));
                        for (GZButton b : allButtons)
                                b.useSupplier1(isButtonBoard);
                }

                p_isButtonBoard = this.isButtonBoard;
                firstSet = true;
        }

        public void setButtonBoard() {
                setButtonBoard(true);
        }

        public boolean isButtonBoard() {
                return isButtonBoard;
        }

        public void setXboxController() {
                setButtonBoard(false);
        }
}
