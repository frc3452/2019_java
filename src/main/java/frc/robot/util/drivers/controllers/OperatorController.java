package frc.robot.util.drivers.controllers;

public class OperatorController extends DeepSpaceController {

        private boolean firstSet = false;
        private boolean isButtonBoard = false;
        private boolean p_isButtonBoard = false;

        public OperatorController() {
                this(1);
        }

        public OperatorController(int port) {
                super(port);

                this.idle = new GZButton(this, () -> false, () -> false);
                this.queueAction = new GZButton(this, () -> false, () -> false);
                this.elevatorHome = new GZButton(this, () -> false, () -> false);
                this.cargo1 = new GZButton(this, () -> false, () -> getButton(Buttons.LB) && getButton(Buttons.A));
                this.cargo2 = new GZButton(this, () -> false, () -> getButton(Buttons.LB) && getButton(Buttons.B));
                this.cargo3 = new GZButton(this, () -> false, () -> getButton(Buttons.LB) && getButton(Buttons.Y));
                this.hatchPannel1 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.A) && !getButton(Buttons.LB));
                this.hatchPanel2 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.B) && !getButton(Buttons.LB));
                this.hatchPanel3 = new GZButton(this, () -> false,
                                () -> getButton(Buttons.Y) && !getButton(Buttons.LB));

                this.elevatorJogUp = new GZButton(this, () -> false, () -> getDUp());
                this.elevatorJogDown = new GZButton(this, () -> false, () -> getDDown());
                this.elevatorManual = new GZButton(this, () -> false, () -> getButton(Buttons.RB));

                this.cargoShip = new GZButton(this, () -> false, () -> false);

                this.cancel = new GZButton(this, () -> false, () -> false);
                this.slidesToggle = new GZButton(this, () -> false, () -> getRightTriggerPressed());
                this.clawToggle = new GZButton(this, () -> false, () -> getLeftTriggerPressed());
                this.intakeDown = new GZButton(this, () -> false, () -> false);
                this.intakeUp = new GZButton(this, () -> false, () -> getButton(Buttons.RB));

                this.stow = new GZButton(this, () -> false, () -> false);
                this.intakeCargo = new GZButton(this, () -> false, () -> getButton(Buttons.START));
                this.floorHatchToManip = new GZButton(this, () -> false, () -> false);
                this.hatchFromFeed = new GZButton(this, () -> false, () -> false);

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
