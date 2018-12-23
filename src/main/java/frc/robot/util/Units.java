package frc.robot.util;


//thx 254
public class Units {
	
	public static Double ticks_to_rotations(double ticks)
	{
		return ticks / 4096;
	}
	
	public static Integer rotations_to_ticks(Double rotations)
	{
		return (int) (rotations * 4096);
	}
	
    public static Double inches_to_meters(Double inches) {
        return inches * 0.0254;
    }

    public static Double meters_to_inches(Double meters) {
        return meters / 0.0254;
    }

    public static Double feet_to_meters(Double feet) {
        return inches_to_meters(feet * 12.0);
    }

    public static Double meters_to_feet(Double meters) {
        return meters_to_inches(meters) / 12.0;
    }

    public static Double degrees_to_radians(Double degrees) {
        return Math.toRadians(degrees);
    }

    public static Double radians_to_degrees(Double radians) {
        return Math.toDegrees(radians);
    }
}
