/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands.functions;


import edu.wpi.first.wpilibj.command.InstantCommand;

public class Print extends InstantCommand {
  
  private String mPrintMessage;
  public Print(String message) {
    super();
    this.mPrintMessage = message;
  }

  protected void initialize() {
    System.out.println(this.mPrintMessage);
  }

}
