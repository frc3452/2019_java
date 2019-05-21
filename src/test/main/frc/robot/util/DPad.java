package frc.robot.util;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Button;

//COPIED FROM
//https://github.com/Aztechs157/FRC-2017/blob/master/src/org/usfirst/frc157/ProtoBot2017/HIDPOVButton.java

public class DPad extends Button {

	private GenericHID stick;
	private int povAngle;

	/**
	 * Enable POV stick as a set of buttons based on direction
	 *
	 * @param stick
	 *            - the stick with the axis to use as a button
	 * @param povAngle
	 *            - POV stick angle to treat as a button press (e.g. 0,45,90,135
	 *            etc...)
	 **/

	public DPad(GenericHID stick, int povAngle) {
		this.stick = stick;
		this.povAngle = povAngle;
	}

	/**
	 * Gets the value of the joystick button
	 *
	 * @return The value of the joystick button
	 */
	
	@Override
	public boolean get() {
		return (stick.getPOV() == povAngle);
	}
}
