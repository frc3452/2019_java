package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import frc.robot.Constants.kFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZJoystick.Buttons;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.MotorChecker.PDPChannelChecker;

public class TestModeRunner {
    private static TestModeRunner mInstance = null;

    public static TestModeRunner getInstance() {
        if (mInstance == null)
            mInstance = new TestModeRunner();

        return mInstance;
    }

    private ArrayList<OptionList> optionsList = new ArrayList<OptionList>();

    private int inMenu = -1;
    private int prevInMenu = inMenu - 1;

    private int posInMenu = 0;
    private int prevPosInMenu = posInMenu - 1;

    private boolean selectPressed = false;

    private boolean isEnabled = false;

    private TestModeRunner() {
        clearMenu();
    }

    private void clearMenu() {
        inMenu = -1;
        posInMenu = 0;

        optionsList.clear();

        // ADD MOTOR TESTING INTERNAL MENU
        ArrayList<Option> motorTestingOptions = new ArrayList<>();
        for (GZSubsystem s : Robot.allSubsystems.getSubsystems()) {
            if (s.hasMotors()) {
                motorTestingOptions.add(new Option("Amperage Test " + s.getClass().getSimpleName()) {
                    public void run() {
                        s.addMotorTestingGroups();
                    }
                });
            }
        }

        // ADD MENU FOR MOTOR TESTING TO BIG MENU
        optionsList.add(new OptionList("Motor testing", motorTestingOptions) {
            public void run() {
                for (Option o : this.getOptions())
                    o.run();

                AmperageChecker.getInstance().checkMotors();
                AmperageChecker.getInstance().clearValues();
            }
        });

        ArrayList<Option> pdpTestingOptions = new ArrayList<>();
        for (GZSubsystem s : Robot.allSubsystems.getSubsystems()) {
            if (s.hasMotors()) {
                pdpTestingOptions.add(new Option("PDP Test " + s.getClass().getSimpleName()) {
                    public void run() {
                        s.addPDPTestingMotors();
                    }
                });
            }
        }

        optionsList.add(new OptionList("PDP Testing", pdpTestingOptions) {
            public void run() {
                for (Option o : this.getOptions())
                    o.run();

                PDPChannelChecker.getInstance().runCheck(kFiles.PDPChannelCheckerWaitTime);
                PDPChannelChecker.getInstance().clearValues();
            }
        });
    }

    public int posInMenu() {
        return posInMenu;
    }

    private void handleJoystickInput() {
        // Moving between different menus
        if (GZOI.driverJoy.isXPressed())
            inMenu = -1;
        else if (inMenu == -1 && GZOI.driverJoy.isBPressed())
            inMenu = posInMenu;
        if (GZOI.driverJoy.isAPressed())
            posInMenu++;
        else if (GZOI.driverJoy.isYPressed())
            posInMenu--;
    }

    private void sanityCheckJoystickInputs() {
        // Check position
        if (inMenu == -1) {
            if (posInMenu > optionsList.size() - 1)
                posInMenu = optionsList.size() - 1;
            else if (posInMenu < 0)
                posInMenu = 0;
        } else {
            int size = optionsList.get(inMenu).getOptions().size() - 1;
            if (posInMenu > size) {
                posInMenu = size;
            } else if (posInMenu < 0) {
                posInMenu = 0;
            }
        }
    }

    private void highlight() {
        // Highlighting
        // Main menu
        if (inMenu == -1) {
            for (OptionList o : optionsList)
                o.hover(false);
            optionsList.get(posInMenu).hover(true);
        } else { // In another menu
            for (Option o : optionsList.get(inMenu).getOptions())
                o.hover(false);
            optionsList.get(inMenu).getOptions().get(posInMenu).hover(true);
        }
    }

    private void onlyAllowOneMenuSelected() {
        int optionListsSelected = 0;
        for (OptionList o : optionsList)
            if (o.isSelected())
                optionListsSelected++;
        if (optionListsSelected > 1)
            for (OptionList o : optionsList)
                o.deselectAllOptions();
    }

    private void handleJoystickSelect() {
        // Select
        if (inMenu != -1 && GZOI.driverJoy.isLBPressed()) {
            optionsList.get(inMenu).getOptions().get(posInMenu).toggleSelected();
            selectPressed = true;
        }
    }

    private void conditionallyPrint(boolean force) {
        // Print
        if (prevInMenu != inMenu || prevPosInMenu != posInMenu || selectPressed || force) {
            String message;
            message = "Use DPad to navigate";
            if (inMenu != -1)
                message += ", A to select, Y to start";
            print(message);
        }
    }

    public void handleJoystickRun() {
        if (GZOI.driverJoy.isRBPressed()) {
            for (OptionList o : optionsList)
                if (o.isSelected())
                    o.run();

            isEnabled = false;
            clearMenu();
        }
    }

    public void update() {
        if (GZOI.driverJoy.areButtonsHeld(Arrays.asList(Buttons.BACK, Buttons.START))) {
            isEnabled = true;
        } else if (GZOI.driverJoy.isLClickPressed()) {
            isEnabled = false;
            System.out.println("Test menu disabled.");
        }
        if (!isEnabled)
            return;

        selectPressed = false;

        handleJoystickInput();

        sanityCheckJoystickInputs();

        highlight();

        onlyAllowOneMenuSelected();

        handleJoystickSelect();

        conditionallyPrint(false);

        handleJoystickRun();

        prevInMenu = inMenu;
        prevPosInMenu = posInMenu;
    }

    private void print(String message) {
        if (inMenu == -1) {
            System.out.println("~~~" + "Testing Menu" + "~~~");
            for (OptionList o : optionsList) {
                o.print();
            }
        } else {
            System.out.println("~~~" + optionsList.get(inMenu).getName() + "~~~");
            for (Option o : optionsList.get(inMenu).getOptions()) {
                o.print();
            }
        }
        System.out.println("~~~ " + message + " ~~~");
    }

    public abstract static class OptionList extends Option {
        private ArrayList<Option> mOptions;

        public OptionList(String name, ArrayList<Option> options) {
            this(name, options, false);
        }

        public OptionList(String name, ArrayList<Option> options, boolean selectedByDefault) {
            super(name, selectedByDefault);
            this.mOptions = options;
        }

        @Override
        public boolean isSelected() {
            boolean retval = false;

            for (Option o : mOptions)
                retval |= o.isSelected();

            return retval;
        }

        public ArrayList<Option> getOptions() {
            return mOptions;
        }

        public void deselectAllOptions() {
            for (Option o : mOptions)
                o.selected(false);
        }

        public abstract void run();

    }

    public abstract static class Option {
        private boolean mHovering = false;
        private boolean mSelected;

        private String mName;

        public Option(String name) {
            this(name, false);
        }

        public Option(String name, boolean selectedByDefault) {
            this.mName = name;
            this.mSelected = selectedByDefault;
        }

        public void hover(boolean isHovering) {
            this.mHovering = isHovering;
        }

        public void print() {
            String print = "";

            String check;
            if (isSelected())
                check = "XX";
            else if (isBeingHovered())
                check = "<>";
            else
                check = "  ";

            print += "[ " + check + " ] " + mName;

            System.out.println(print);
        }

        public String getName() {
            return this.mName;
        }

        public void selected(boolean selected) {
            this.mSelected = selected;
        }

        public void toggleSelected() {
            selected(!isSelected());
        }

        public boolean isBeingHovered() {
            return mHovering;
        }

        public boolean isSelected() {
            return mSelected;
        }

        public abstract void run();
    }
}