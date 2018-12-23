package frc.robot.util;

import edu.wpi.first.wpilibj.command.Command;

public class GZCommand {

	private String mName;
	private Command mCommand;

	public GZCommand(String name, Command command) {
		this.mName = name;
		this.mCommand = command;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public Command getCommand()
	{
		return mCommand;
	}
}
