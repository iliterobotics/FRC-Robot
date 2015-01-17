package org.usfirst.frc.team1885.robot.input;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

public class DriverInputControl
{	
	private HashMap<Sensor, Joystick> joysticks;
	private HashMap<Sensor, Encoder> encoders;
	private HashMap<Sensor, DigitalInput> limit_switches;
	
	public static final double DEADZONE = 0.1;
	public DriverInputControl()
	{
		joysticks = new HashMap<Sensor, Joystick>();
		encoders = new HashMap<Sensor, Encoder>();
		limit_switches = new HashMap<Sensor, DigitalInput>();
	}    
    public static double deadzone(double axis)
    {
    	if(Math.abs(axis) < DEADZONE)
    	{
    		return 0;
    	}
    	return axis;
    }    
    public boolean addSensor(Sensor sensor_type, int port)
    {
    	if(joysticks.containsKey(sensor_type) || encoders.containsKey(sensor_type) 
    			|| limit_switches.containsKey(sensor_type))
    	{
    		return false;
    	}
    	if(sensor_type.name().contains("JOYSTICK"))
    	{
    		joysticks.put(sensor_type, new Joystick(port));
    	}
    	else if(sensor_type.name().contains("LIMIT"))
    	{
    		limit_switches.put(sensor_type, new DigitalInput(port));
    	}
    	else
    	{
    		return false;
    	}
    	return true;
    }
    public boolean addSensor(Sensor sensor_type, int port_one, int port_two)
    {
    	if(joysticks.containsKey(sensor_type) || encoders.containsKey(sensor_type) 
    			|| limit_switches.containsKey(sensor_type))
    	{
    		return false;
    	}
    	if(sensor_type.name().contains("ENCODER"))
    	{
    		encoders.put(sensor_type, new Encoder(port_one, port_two));
    	}
    	else
    	{
    		return false;
    	}
    	return true;
    }
    public double getLeftDrive()
    {
    	double axis = joysticks.get(Sensor.JOYSTICK_LEFT).getAxis(Joystick.AxisType.kY);
    	return deadzone(axis);
    }
    public double getRightDrive()
    {
    	double axis = joysticks.get(Sensor.JOYSTICK_RIGHT).getAxis(Joystick.AxisType.kY);
    	return deadzone(axis);
    }
}
