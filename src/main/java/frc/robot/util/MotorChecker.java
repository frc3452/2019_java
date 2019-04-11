package frc.robot.util;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.kFiles;
import frc.robot.Robot;
import frc.robot.subsystems.Pneumatics;
import frc.robot.util.GZFileMaker.FileExtensions;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.HTML;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;

public class MotorChecker {
    public static class AmperageChecker {

        public static class FailingValueWrapper {

            private ArrayList<FailingValue> array = new ArrayList<FailingValue>();
            private double average = -1;

            public final double epsilon;
            public final double floor;
            public final MotorTestingGroup group;

            private boolean hasFails = false;
            private boolean mFirstCheck = false;

            public FailingValueWrapper(MotorTestingGroup group, double epsilon, double floor) {
                this.epsilon = epsilon;
                this.floor = floor;
                this.group = group;
            }

            public FailingValueWrapper(double epsilon, double floor) {
                this(null, epsilon, floor);
            }

            public void add(FailingValue value) {
                this.array.add(value);
            }

            public FailingValue get(int pos) {
                return this.array.get(pos);
            }

            public int size() {
                return this.array.size();
            }

            private void average() {
                double accum = 0;
                for (FailingValue value : array) {
                    accum += value.getVal();
                }

                accum /= array.size();
                average = accum;

                for (FailingValue value : array) {
                    if ((value.getVal() < average - epsilon) || (value.getVal() > average + epsilon)) {
                        value.setFail();
                        fail();
                    }
                }
            }

            public void check() {
                mFirstCheck = true;
                average();
                floor();
                // findFails();
            }

            public boolean hasFails() {
                if (!mFirstCheck)
                    check();

                return hasFails;
            }

            private void floor() {
                for (FailingValue c : array) {
                    if (Math.abs(c.getVal()) < floor) {
                        c.setFail();
                        fail();
                    }
                }
            }

            private void fail() {
                hasFails = true;
                if (group != null)
                    group.getSubsystem().setMotorTestingFail();
            }

            // private void findFails() {
            // for (FailingValue c : array) {
            // if (!GZUtil.epsilonEquals(c.getVal(), average, epsilon)) {
            // c.setFail();
            // fail();
            // }
            // }
            // }

            public double getAverage() {
                return average;
            }

            public static boolean hasFails(FailingValueWrapper... wrappers) {
                boolean hasFail = false;
                for (FailingValueWrapper value : wrappers) {
                    hasFail |= value.hasFails();
                }
                return hasFail;
            }
        }

        public static class FailingValue {
            private final double mValue;
            private boolean mFail = false;

            public FailingValue(double mValue) {
                this.mValue = mValue;
            }

            public void setFail() {
                this.mFail = true;
            }

            public boolean getFail() {
                return this.mFail;
            }

            public Double getVal() {
                return this.mValue;
            }

            @Override
            public String toString() {
                return "" + getVal();
            }
        }

        public static class MotorTestingGroup {
            private String mName;

            private GZSubsystem mSubsystem;

            private List<GZSpeedController> controllers = new ArrayList<GZSpeedController>();
            private FailingValueWrapper forwardCurrents;
            private FailingValueWrapper reverseCurrents;

            private FailingValueWrapper forwardRPMs;
            private FailingValueWrapper reverseRPMs;

            private ArrayList<Double> forwardVoltages = new ArrayList<Double>();
            private ArrayList<Double> reverseVoltages = new ArrayList<Double>();

            private CheckerConfig mCheckerConfig;

            private GZRPMSupplier mRPMSupplier;

            public void setEpsilons(CheckerConfig config) {
                setEpsilons(config.mCurrentEpsilon, config.mCurrentFloor, config.mRPMEpsilon, config.mRPMFloor);
            }

            public void setEpsilons(double currentEpsilon, double currentFloor, double rpmEpsilon, double rpmFloor) {
                forwardCurrents = new FailingValueWrapper(this, currentEpsilon, currentFloor);
                reverseCurrents = new FailingValueWrapper(this, currentEpsilon, currentFloor);
                forwardRPMs = new FailingValueWrapper(this, rpmEpsilon, rpmFloor);
                reverseRPMs = new FailingValueWrapper(this, rpmEpsilon, rpmFloor);
            }

            public MotorTestingGroup(GZSubsystem subsystem, String name, List<GZSpeedController> controllers,
                    CheckerConfig config, GZRPMSupplier supplier) {
                this.controllers = controllers;
                this.mName = name;
                this.mSubsystem = subsystem;
                this.mCheckerConfig = config;
                this.mRPMSupplier = supplier;
                setEpsilons(this.mCheckerConfig);

                System.out.println("GROUP " + name + "\tFWD RPM [AVG,FLOOR]" + forwardRPMs.average + "\t"
                        + forwardRPMs.floor + "\tREV RPM[AVG,FLOOR]" + reverseRPMs.average + "\t" + reverseRPMs.floor);
            }

            public void check() {
                hasFail();
            }

            public boolean hasFail() {
                return FailingValueWrapper.hasFails(forwardCurrents, reverseCurrents, forwardRPMs, reverseRPMs);
            }

            public Double getRPM() {
                if (mRPMSupplier == null)
                    return -1.0;
                return mRPMSupplier.getSupplier().get();
            }

            public ArrayList<Double> getForwardVoltages() {
                return forwardVoltages;
            }

            public ArrayList<Double> getReverseVoltages() {
                return reverseVoltages;
            }

            public CheckerConfig getConfig() {
                return this.mCheckerConfig;
            }

            public String getName() {
                return this.mName;
            }

            public GZSubsystem getSubsystem() {
                return this.mSubsystem;
            }

            public List<GZSpeedController> getControllers() {
                return this.controllers;
            }

            public FailingValueWrapper getForwardCurrents() {
                return this.forwardCurrents;
            }

            public FailingValueWrapper getForwardRPMs() {
                return this.forwardRPMs;
            }

            public FailingValueWrapper getReverseRPMs() {
                return this.reverseRPMs;
            }

            public FailingValueWrapper getReverseCurrents() {
                return this.reverseCurrents;
            }
        }

        public static class CheckerConfig {
            public static List<MotorTestingGroup> getFromFile(GZSubsystem subsystem) {
                File file = kFiles.MOTOR_TESTING_CONFIG.getFile();

                List<MotorTestingGroup> ret = new ArrayList<MotorTestingGroup>();
                try {

                    Scanner scnr = new Scanner(new FileReader(file));

                    // Skip first line of headers
                    scnr.nextLine();

                    while (scnr.hasNext()) {
                        String t = scnr.nextLine();
                        String split[] = t.split(",");

                        if (split[0].equals(subsystem.toString())) {
                            List<GZSpeedController> controllers = new ArrayList<GZSpeedController>();

                            int c = 1;

                            double currentFloor = Double.valueOf(split[++c]);
                            double currentEpsilon = Double.valueOf(split[++c]);
                            double runTimeSec = Double.valueOf(split[++c]);
                            double waitTimeSec = Double.valueOf(split[++c]);
                            double outputPercentage = Double.valueOf(split[++c]);
                            double rpmFloor = Double.valueOf(split[++c]);
                            double rpmEpsilon = Double.valueOf(split[++c]);
                            String rpmSupplier = split[++c];
                            boolean reverseAfterGroup = Boolean.valueOf(split[++c]);

                            CheckerConfig config = new CheckerConfig(currentFloor, currentEpsilon, runTimeSec,
                                    waitTimeSec, outputPercentage, rpmFloor, rpmEpsilon, reverseAfterGroup);

                            GZRPMSupplier supplierVar = null;

                            for (GZRPMSupplier suppliers : subsystem.mRPMSuppliers) {
                                if (rpmSupplier.equals(suppliers.getGZName())) {
                                    supplierVar = suppliers;
                                }
                            }

                            for (int i = 10; i < split.length; i++) {
                                for (GZSpeedController controller : subsystem.mSmartControllers)
                                    if (controller.getGZName().equals(split[i]))
                                        controllers.add(controller);

                                for (GZSpeedController controller : subsystem.mDumbControllers)
                                    if (controller.getGZName().equals(split[i]))
                                        controllers.add(controller);
                            }
                            ret.add(new MotorTestingGroup(subsystem, split[1], controllers, config, supplierVar));
                        }
                    }
                    scnr.close();

                    if (ret.isEmpty())
                        return null;
                    return ret;
                } catch (Exception e) {
                    // on error
                    System.out.println("ERROR Could not load MotorTestingConfig for " + subsystem.toString() + " at "
                            + file.toPath());
                    // e.printStackTrace();
                    return null;
                }
            }

            public final double mCurrentFloor;
            public final double mCurrentEpsilon;

            public final double mRunTimeSec;
            public final double mWaitTimeSec;
            public final double mRunOutputPercentage;

            public final double mRPMFloor;
            public final double mRPMEpsilon;

            public final boolean mReverseAfterGroup;

            public CheckerConfig(double currentFloor, double currentEpsilon, double runTimeSec, double waitTimeSec,
                    double outputPercentage, double rpmFloor, double rpmEpsilon, boolean reverseAfterGroup) {
                this.mCurrentFloor = currentFloor;
                this.mCurrentEpsilon = currentEpsilon;
                this.mRunTimeSec = runTimeSec;
                this.mWaitTimeSec = waitTimeSec;
                this.mRunOutputPercentage = outputPercentage;
                this.mRPMFloor = rpmFloor;
                this.mRPMEpsilon = rpmEpsilon;
                this.mReverseAfterGroup = reverseAfterGroup;
            }

            public String configAsString() {
                String retval = "";

                retval += "Current floor: " + mCurrentFloor + "\n";
                retval += "Current epsilon: " + mCurrentEpsilon + "\n";
                retval += "Run time (seconds): " + mRunTimeSec + "\n";
                retval += "Wait time (seconds): " + mWaitTimeSec + "\n";
                retval += "Output percentage: " + mRunOutputPercentage + "\n";
                retval += "Reverse after group: " + mReverseAfterGroup + "\n";
                retval += "RPM Floor: " + mRPMFloor + "\n";
                retval += "RPM Epsilon: " + mRPMEpsilon + "\n";

                return retval;
            }

            public void print() {
                System.out.println(toString());
            }
        }

        private static AmperageChecker mInstance = null;

        public static AmperageChecker getInstance() {
            if (mInstance == null)
                mInstance = new AmperageChecker();
            return mInstance;
        }

        private double mInitialVoltage = -1;
        private double mEndVoltage = -1;

        private AmperageChecker() {
            clearValues();
        }

        private Map<GZSubsystem, ArrayList<MotorTestingGroup>> subsystemMap;

        private double mTimeNeeded = 0;

        public void clearValues() {
            subsystemMap = new HashMap<GZSubsystem, ArrayList<MotorTestingGroup>>();
        }

        public void addTalonGroup(MotorTestingGroup group) {
            if (group.getConfig() == null || group == null) {
                return;
            }

            if (!subsystemMap.containsKey(group.getSubsystem()))
                subsystemMap.put(group.getSubsystem(), new ArrayList<MotorTestingGroup>());

            subsystemMap.get(group.getSubsystem()).add(group);
        }

        public void addTalonGroups(List<MotorTestingGroup> multipleGroups) {
            if (multipleGroups == null)
                return;

            for (MotorTestingGroup g : multipleGroups)
                if (g != null)
                    addTalonGroup(g);
        }

        public void checkMotors() {
            Pneumatics.getInstance().setMotorTesting(true);

            boolean failure = false;

            mInitialVoltage = RobotController.getBatteryVoltage();

            // Clear all fails
            for (GZSubsystem s : subsystemMap.keySet())
                s.clearMotorTestingFails();

            mTimeNeeded = 0;
            for (ArrayList<MotorTestingGroup> allGroups : subsystemMap.values()) {
                for (MotorTestingGroup group : allGroups) {
                    mTimeNeeded += group.getConfig().mRunTimeSec * 2 * group.controllers.size();
                    mTimeNeeded += group.getConfig().mWaitTimeSec * 2 * group.controllers.size();
                }
            }

            System.out.println("Starting amperage checker... estimated time needed: " + mTimeNeeded + " seconds");

            // will store the current motor groups
            ArrayList<MotorTestingGroup> talonGroups;

            // loop through every subsystem
            for (GZSubsystem s : subsystemMap.keySet()) {
                // get current groups
                talonGroups = subsystemMap.get(s);

                // loop through each group in this subsystem
                for (MotorTestingGroup group : talonGroups) {
                    // group.setEpsilons(group.getConfig());

                    // Controllers
                    List<GZSpeedController> controllersToCheck = group.getControllers();

                    System.out.println("Checking subsystem " + group.getSubsystem().toString() + " group "
                            + group.getName() + " for " + controllersToCheck.size()
                            + " controllers. Estimated total time left: " + mTimeNeeded + " seconds");

                    // Dont allow other methods to control these controllers
                    for (GZSpeedController t : controllersToCheck)
                        t.lockOutController(true);

                    // Loop through controllers (running all controllers in group forwards, then all
                    // backwards)
                    if (group.getConfig().mReverseAfterGroup) {
                        // start forward
                        boolean forward = true;

                        // do this twice
                        for (int i = 0; i < 2; i++) {

                            // Check each talon
                            for (GZSpeedController individualTalonToCheck : controllersToCheck) {
                                checkController(individualTalonToCheck, group, forward);
                            }
                            // once we've checked them all, do it again but the other way
                            forward = !forward;
                        }
                    } else { // test each talon forwards, then test it backwards, then move onto the next
                             // talon
                        for (GZSpeedController individualControllerToCheck : controllersToCheck) {
                            // Test twice
                            for (int i = 0; i < 2; i++)
                                checkController(individualControllerToCheck, group, (i == 0));
                            // on the first loop when i == 0, go forwards, then go backwards
                        }
                    }

                    // We've now checked every current and recorded, run average checks

                    // This will check amperage and rpm, floor & epsilon delta
                    failure |= group.hasFail();

                    // Unlock talons so another method can control them
                    for (GZSpeedController t : controllersToCheck)
                        t.lockOutController(false);
                }
            }

            mEndVoltage = RobotController.getBatteryVoltage();

            System.out.println((failure ? "WARNING " : "") + "Amperage check " + (failure ? "failed!" : "passed!"));
            createHTMLFile();
        }

        private boolean checkController(GZSpeedController individualControllerToCheck, MotorTestingGroup group,
                boolean forward) {
            boolean failure = false;
            System.out.println(
                    "Checking: " + individualControllerToCheck.getGZName() + (forward ? " forwards" : " reverse"));

            FailingValueWrapper currents = (forward ? group.getForwardCurrents() : group.getReverseCurrents());
            ArrayList<Double> voltages = (forward ? group.getForwardVoltages() : group.getReverseVoltages());
            FailingValueWrapper rpms = (forward ? group.getForwardRPMs() : group.getReverseRPMs());

            individualControllerToCheck.set(group.getConfig().mRunOutputPercentage * (forward ? 1 : -1), true);
            Timer.delay(group.getConfig().mRunTimeSec);
            mTimeNeeded -= group.getConfig().mRunTimeSec;

            // Now poll the interesting information.
            double current = individualControllerToCheck.getAmperage();
            double rpm = group.getRPM();

            currents.add(new FailingValue(current));
            rpms.add(new FailingValue(rpm));
            voltages.add(RobotController.getBatteryVoltage());

            individualControllerToCheck.set(0.0, true);

            Timer.delay(group.getConfig().mWaitTimeSec);
            mTimeNeeded -= group.getConfig().mWaitTimeSec;

            return failure;
        }

        private DecimalFormat df = new DecimalFormat("#0.000");

        private void createHTMLFile() {
            String body = "";

            // Time at top of file
            body += HTML.paragraph("Created on " + GZUtil.dateTime(false));

            body += HTML.paragraph("Robot name [" + kFiles.ROBOT_NAME + "]");

            body += HTML.paragraph("Initial voltage: " + df.format(mInitialVoltage));

            // Loop through every subsystem
            for (GZSubsystem subsystem : subsystemMap.keySet()) {

                // groups for subsystem
                ArrayList<MotorTestingGroup> talonGroups = subsystemMap.get(subsystem);

                // header (write subsystem color in red if has error)
                String subsystemColor = subsystem.hasMotorTestingFail() ? "red" : "black";
                body += HTML.header(subsystem.toString(), 1, subsystemColor);

                // Content of entire subsystem
                String subsystemContent = "";

                for (MotorTestingGroup talonGroup : talonGroups) {

                    String groupContent = "";

                    // values for the group we are currently creating
                    final List<GZSpeedController> mtr = talonGroup.getControllers();
                    final FailingValueWrapper fwdCurrent = talonGroup.getForwardCurrents();
                    final FailingValueWrapper revCurrent = talonGroup.getReverseCurrents();
                    final ArrayList<Double> fwdVoltages = talonGroup.getForwardVoltages();
                    final ArrayList<Double> revVoltages = talonGroup.getReverseVoltages();
                    final FailingValueWrapper fwdRPMs = talonGroup.getForwardRPMs();
                    final FailingValueWrapper revRPMs = talonGroup.getReverseRPMs();

                    // Sizes
                    final int talonSize = mtr.size() - 1;
                    final int fwdSize = fwdCurrent.size() - 1;
                    final int revSize = revCurrent.size() - 1;

                    // check size
                    if (!((talonSize == fwdSize) && (fwdSize == revSize))) {
                        System.out.println("Motor size (" + talonSize + ") and forward currents size (" + fwdSize
                                + ") and reverse currents size (" + revSize + ") not equal!");
                        return;
                    }

                    // Write each group in red or black if it has a fail
                    String color = (talonGroup.hasFail() ? "red" : "black");
                    groupContent += HTML.header(talonGroup.getName(), 2, color);

                    // Write config as individual lines
                    String[] config = talonGroup.getConfig().configAsString().split("\n");
                    for (String configLine : config)
                        groupContent += HTML.paragraph(configLine);

                    String table = "";

                    // Write Average to table
                    table += HTML.tableRow(
                            HTML.tableCell("Average") + HTML.tableCell(df.format(fwdCurrent.getAverage()) + "")
                                    + HTML.tableCell("") + HTML.tableCell(df.format(fwdRPMs.getAverage()) + "")
                                    + HTML.tableCell(revCurrent.getAverage() + "") + HTML.tableCell("")
                                    + HTML.tableCell(df.format(revRPMs.getAverage()) + ""));

                    // Headers
                    table += HTML.tableRow(HTML.easyHeader("Talon", "Forward Amperage", "Forward BAT V", "Forward RPM",
                            "Reverse Amperage", "Reverse BAT V", "Reverse RPM"));

                    // Loop through every talon
                    for (int talon = 0; talon < talonSize + 1; talon++) {
                        String row = "";

                        boolean fwdAmpFail = fwdCurrent.get(talon).getFail();
                        boolean revAmpFail = revCurrent.get(talon).getFail();
                        boolean fwdRPMFail = fwdRPMs.get(talon).getFail();
                        boolean revRPMFail = revRPMs.get(talon).getFail();

                        final boolean fail = fwdAmpFail || revAmpFail || fwdRPMFail || revRPMFail;
                        {
                            // Put talon cell
                            String talonCell;
                            if (fail)
                                talonCell = HTML.tableCell(mtr.get(talon).getGZName(), "yellow", false);
                            else
                                talonCell = HTML.tableCell(mtr.get(talon).getGZName());
                            row += talonCell;
                        }

                        // Populate forward cell
                        {
                            String fwdCellAmp;
                            fwdCellAmp = HTML.tableCell(fwdCurrent.get(talon).getVal().toString(), fwdAmpFail);
                            row += fwdCellAmp;
                        }

                        {
                            String fwdCellVolt;
                            fwdCellVolt = HTML.tableCell(df.format(fwdVoltages.get(talon)).toString());
                            row += fwdCellVolt;
                        }

                        {
                            String fwdCellRPM;
                            fwdCellRPM = HTML.tableCell(df.format(fwdRPMs.get(talon).getVal()), fwdRPMFail);
                            row += fwdCellRPM;
                        }

                        // Populate reverse cell
                        {
                            String revAmpCell;
                            revAmpCell = HTML.tableCell(df.format(revCurrent.get(talon).getVal()), revAmpFail);
                            row += revAmpCell;
                        }

                        {
                            String revCellVolt;
                            revCellVolt = HTML.tableCell(df.format(revVoltages.get(talon)).toString());
                            row += revCellVolt;
                        }

                        {
                            String revCellRPM;
                            revCellRPM = HTML.tableCell(df.format(revRPMs.get(talon).getVal()), revRPMFail);
                            row += revCellRPM;
                        }

                        // Format as row
                        row = HTML.tableRow(row);

                        // add to table
                        table += row;
                    } // end of Loop through every talon

                    // group has all talons written to table
                    // format as table and add to subsystem
                    table = HTML.table(table);
                    groupContent += table;
                    // if (!talonGroup.hasFail())
                    // groupContent = HTML.button("Open " + talonGroup.getName(), groupContent);

                    subsystemContent += groupContent;
                } // end of all groups in subsystem loop

                // if the subsystem doesn't have any fails, wrap in a button so we can hide it
                // easily
                if (!subsystem.hasMotorTestingFail())
                    subsystemContent = HTML.button("Open " + subsystem.toString(), subsystemContent);

                body += subsystemContent;
            } // end of all subsystems

            body += HTML.paragraph("Ending battery voltage: " + df.format(mEndVoltage));

            boolean htmlWriteFail = false;
            GZFile file = null;
            try {
                // write to rio
                file = GZFileMaker.getFile(kFiles.ROBOT_NAME + "-MotorReport-" + GZUtil.dateTime(false),
                        new Folder("MotorReports"), FileExtensions.HTML, false, true);
                HTML.createHTMLFile(file, body);

            } catch (Exception e) {
                System.out.println("Could not write MotorReport!");
                htmlWriteFail = true;
            }

            try {
                GZFiles.copyFile(file, GZFileMaker.getFile(file, true));
            } catch (Exception copyFile) {
                System.out.println("Could not copy MotorReport to USB!");
                htmlWriteFail = true;
            }

            if (!htmlWriteFail)
                System.out.println("MotorReport successfully written!");

            clearValues();

            Robot.allSubsystems.enableFollower();

            Pneumatics.getInstance().setMotorTesting(false);
        }

    }

    public static class PDPChannelChecker {

        public static class CheckerConfig {
            private final double mRunTimeSec;
            private final double mRunSpeed;
            private final GZSubsystem mSubsystem;

            public CheckerConfig(GZSubsystem subsystem, double runtimeSec, double runSpeed) {
                this.mSubsystem = subsystem;
                this.mRunSpeed = runSpeed;

                if (runtimeSec < .1)
                    this.mRunTimeSec = .1;
                else
                    this.mRunTimeSec = runtimeSec;
            }

            public GZSubsystem getSubsystem() {
                return this.mSubsystem;
            }

            public double getRunTime() {
                return this.mRunTimeSec;
            }

            public double getRunSpeed() {
                return this.mRunSpeed;
            }
        }

        public static class CheckerMotorGroup {
            private final List<GZSpeedController> mController;
            private final CheckerConfig mConfig;
            private final String mName;

            public CheckerMotorGroup(String name, List<GZSpeedController> c, CheckerConfig config) {
                this.mController = c;
                this.mConfig = config;
                this.mName = name;
            }

            public String getName() {
                return this.mName;
            }

            public List<GZSpeedController> getControllers() {
                return this.mController;
            }

            public CheckerConfig getConfig() {
                return this.mConfig;
            }
        }

        private ArrayList<CheckerMotorGroup> groupsToTest;

        private static PDPChannelChecker mInstance = null;

        private GZPDP pdp = GZPDP.getInstance();

        public static PDPChannelChecker getInstance() {
            if (mInstance == null)
                mInstance = new PDPChannelChecker();
            return mInstance;
        }

        private PDPChannelChecker() {
            clearValues();
        }

        public void clearValues() {
            groupsToTest = new ArrayList<CheckerMotorGroup>();
        }

        public void addGroup(CheckerMotorGroup group) {
            groupsToTest.add(group);
        }

        public List<GZSubsystem> getSubsystemsBeingTested() {
            List<GZSubsystem> ret = new ArrayList<GZSubsystem>();
            for (CheckerMotorGroup c : groupsToTest) {
                if (!ret.contains(c.getConfig().getSubsystem()))
                    ret.add(c.getConfig().getSubsystem());
            }

            return ret;
        }

        public void runCheck(double waitTime) {
            String body = HTML.paragraph("File created on: " + GZUtil.dateTime(false));
            final DecimalFormat fmt = new DecimalFormat("#0.00");

            System.out.println("RUNNING PDP CHANNEL CHECK");
            {
                String print = "Subsystems being checked: ";
                for (GZSubsystem s : getSubsystemsBeingTested())
                    print += s.toString() + ",";

                System.out.println(print);
            }

            for (CheckerMotorGroup c : groupsToTest) {
                String group = HTML.header(c.getConfig().getSubsystem().toString(), 1);

                for (GZSpeedController speed_Controller : c.getControllers()) {
                    CheckControllerReturn result = checkController(speed_Controller, c.getConfig());

                    String controller = "";

                    controller += HTML.header(speed_Controller.getGZName());

                    // Potential channels
                    {
                        String potentialChannels = "Potential Channels: ";
                        for (int i = 0; i <= 15; i++)
                            if (result.pdpVals.get(i).trip)
                                potentialChannels += "[" + i + "]" + "\t";

                        controller += HTML.paragraph(potentialChannels, "red");
                    }

                    String hidden = "";
                    // AVERAGE
                    {
                        String average = "Average current: " + fmt.format(result.average);
                        hidden += HTML.paragraph(average);
                    }

                    // TABLE
                    String table = "";
                    {
                        String nextRow = "";
                        nextRow += HTML.tableHeader("PDP Channel");
                        for (int i = 0; i <= 15; i++)
                            nextRow += HTML.tableCell(String.valueOf(i));

                        nextRow = HTML.tableRow(nextRow);
                        table += nextRow;
                    }
                    {
                        String nextRow = "";
                        nextRow += HTML.tableHeader("Current");
                        for (int i = 0; i <= 15; i++) {
                            nextRow += HTML.tableCell(fmt.format(result.pdpVals.get(i).val).toString(),
                                    result.pdpVals.get(i).trip ? "red" : "white", false);
                        }
                        nextRow = HTML.tableRow(nextRow);

                        table += nextRow;
                    }

                    table = HTML.table(table);
                    hidden += table;
                    hidden = HTML.button("Values", hidden);

                    controller += hidden;

                    group += controller;

                    Timer.delay(waitTime);
                } // end of all controllers in group

                body += group;
            } // end of all groups

            boolean fail = false;
            try {
                GZFile file = GZFileMaker.getFile("PDPCheck", new Folder(""), FileExtensions.HTML, false, true);
                HTML.createHTMLFile(file, body);
                try {
                    GZFiles.copyFile(file, GZFileMaker.getFile(file, true));
                } catch (Exception e) {
                    System.out.println("Could not copy PDP check to USB!");
                }
            } catch (Exception e) {
                System.out.println("Could not write PDP check!");
                fail = true;
            }
            System.out.println(fail ? "WARNING " : "" + "PDP check written " + (fail ? "un" : "") + "successfully");

            clearValues();
        }

        public static class AmperageValue {
            final Double val;
            boolean trip = false;

            public AmperageValue(double val) {
                this.val = val;
            }

            public void trip() {
                trip = true;
            }
        }

        private static class CheckControllerReturn {
            ArrayList<AmperageValue> pdpVals;
            double average;

            public CheckControllerReturn(ArrayList<AmperageValue> pdpVals, double average) {
                this.pdpVals = pdpVals;
                this.average = average;
            }
        }

        private CheckControllerReturn checkController(GZSpeedController c,
                final PDPChannelChecker.CheckerConfig config) {
            ArrayList<AmperageValue> pdpVals = new ArrayList<AmperageValue>();
            double currentAverage = 0;
            try {
                System.out.println("Checking " + config.getSubsystem().toString() + " - " + c.getGZName());

                c.lockOutController(true);

                c.set(config.getRunSpeed(), true);
                Timer.delay(config.getRunTime());

                List<Integer> channelsToFake = null;
                if (pdp.isFake())
                    channelsToFake = Arrays.asList(GZUtil.getRandInt(0, 15), GZUtil.getRandInt(0, 15));

                for (int i = 0; i <= 15; i++) {

                    // In case we're simulating, we can still test this function
                    double fakeCurrent = 0;
                    if (channelsToFake != null)
                        if (channelsToFake.contains(i))
                            fakeCurrent = GZUtil.getRandDouble(0, 4);

                    double current = pdp.getCurrent(i, fakeCurrent);
                    pdpVals.add(new AmperageValue(current));
                    currentAverage += current;
                }

                c.set(0, true);

                currentAverage /= 16;

                for (int i = 0; i <= 15; i++) {
                    if (pdpVals.get(i).val > currentAverage)
                        pdpVals.get(i).trip();
                }

            } catch (Exception e) {

            } finally {
                c.lockOutController(false);
            }

            return new CheckControllerReturn(pdpVals, currentAverage);
        }

    }
}