package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import frc.robot.util.GZSubsystem;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.drivers.GZJoystick.Buttons;

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

    private boolean isEnabled = false;
    private boolean wasEnabled = false;

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
                motorTestingOptions.add(new Option("Amperage Test " + s.toString()) {
                    public void run() {
                        s.addMotorsForTesting();
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
                Robot.allSubsystems.enableFollower();
            }
        });

        // ArrayList<Option> pdpTestingOptions = new ArrayList<>();
        // for (GZSubsystem s : Robot.allSubsystems.getSubsystems()) {
        // if (s.hasMotors()) {
        // pdpTestingOptions.add(new Option("PDP Test " + s.toString()) {
        // public void run() {
        // s.addPDPTestingMotors();
        // }
        // });
        // }
        // }

        // optionsList.add(new OptionList("PDP Testing", pdpTestingOptions) {
        // public void run() {
        // for (Option o : this.getOptions())
        // o.run();

        // PDPChannelChecker.getInstance().runCheck(kFiles.PDPChannelCheckerWaitTime);
        // PDPChannelChecker.getInstance().clearValues();
        // Robot.allSubsystems.enableFollower();
        // }
        // });

        // STATS
        ArrayList<Option> statsOptions = new ArrayList<Option>();

        statsOptions.add(new Option("Print") {
            public void run() {
                PersistentInfoManager.getInstance().printPersistentSettings();
            }
        });
        statsOptions.add(new Option("Reset Stats") {
            public void run() {
                PersistentInfoManager.getInstance().reset();
            }
        });
        statsOptions.add(new Option("Reread Stats from new file") {
            public void run() {
                PersistentInfoManager.getInstance().replaceAndReRead();
            }
        });

        optionsList.add(new OptionList("Stats", statsOptions) {
            public void run() {
                for (Option o : this.getOptions())
                    o.run();
            }
        });
    }

    public int posInMenu() {
        return posInMenu;
    }

    public void update() {
        if (GZOI.driverJoy.getButtons(Buttons.BACK, Buttons.START)) {
            isEnabled = true;
        } else if (GZOI.driverJoy.isLClickPressed()) {
            isEnabled = false;
        }

        if (isEnabled != wasEnabled)
            System.out.println("Test mode " + (isEnabled ? "enabled" : "disabled"));

        wasEnabled = isEnabled;

        if (!isEnabled)
            return;

        // HANDLE JOYSTICK INPUT
        if (GZOI.driverJoy.isDLeftPressed())
            inMenu = -1;
        else if (inMenu == -1 && GZOI.driverJoy.isDRightPressed())
            inMenu = posInMenu;
        if (GZOI.driverJoy.isDDownPressed())
            posInMenu++;
        else if (GZOI.driverJoy.isDUpPressed())
            posInMenu--;

        // KEEP MENU AND POSITION IN MENU WITHIN BOUNDS
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

        // HIGHLIGHTING
        if (inMenu == -1) { // Main menu
            for (OptionList o : optionsList)
                o.hover(false);
            optionsList.get(posInMenu).hover(true);
        } else { // In another menu
            for (Option o : optionsList.get(inMenu).getOptions())
                o.hover(false);
            optionsList.get(inMenu).getOptions().get(posInMenu).hover(true);
        }

        // ONLY ALLOW ONE MENU TO BE SELECTED
        {
            int optionListsSelected = 0;
            for (OptionList o : optionsList)
                if (o.isSelected())
                    optionListsSelected++;
            if (optionListsSelected > 1)
                for (OptionList o : optionsList)
                    o.deselectAllOptions();
        }

        // SELECT PRESSED
        boolean selectPressed = false;
        if (inMenu != -1 && GZOI.driverJoy.isAPressed()) {
            optionsList.get(inMenu).getOptions().get(posInMenu).toggleSelected();
            selectPressed = true;
        }

        // PRINT ON CHANGE
        if (prevInMenu != inMenu || prevPosInMenu != posInMenu || selectPressed) {
            String message;
            message = "Use DPad to navigate";
            if (inMenu != -1)
                message += ", A to select, Y to start";
            print(message);
        }

        if (GZOI.driverJoy.isYPressed()) {
            for (OptionList o : optionsList)
                if (o.isSelected())
                    o.run();

            isEnabled = false;
            clearMenu();
        }

        prevInMenu = inMenu;
        prevPosInMenu = posInMenu;
    }

    private void print(String message) {
        System.out.println("\n\n");
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