package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import frc.robot.auto.commands.functions.WaitCommand;
import frc.robot.auto.commands.functions.drive.TeleDrive;

public class GZCommandGroup extends CommandGroup {

    private static final String DEFAULT_MARKER = "PrepForAction";

    public synchronized static GZCommandGroup getTeleDrive() {
        GZCommandGroup ret = new GZCommandGroup();
        ret.tele();
        return ret;
    }

    public synchronized void print(String message) {
        add(new PrintCommand(message));
    }

    public synchronized void waitTime(double delay) {
        add(new WaitCommand(delay));
    }

    public synchronized void add(ArrayList<Command> commands) {
        for (Command c : commands)
            add(c);
    }

    public synchronized void and(Command c) {
        addParallel(c);
    }

    public synchronized void add(Command c) {
        addSequential(c);
    }

    public synchronized void tele() {
        add(new TeleDrive());
    }

    public synchronized ArrayList<GZCommandGroup> toList() {
        ArrayList<GZCommandGroup> ret = new ArrayList<GZCommandGroup>();
        ret.add(this);
        return ret;
    }
}