package org.usfirst.frc.team1885.robot.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usfirst.frc.team1885.robot.common.type.RobotMotorType;
import org.usfirst.frc.team1885.robot.common.type.RobotPneumaticType;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class RobotControl 
{
	private static RobotControl instance = null;
	private List<Talon> leftDrive;
	private List<Talon> rightDrive;
	private Map<RobotMotorType, Talon> outputTalons;
	private Map<RobotPneumaticType, Solenoid> outputSolenoids;
	/*
	private Talon leftDrive1;
	private Talon leftDrive2;
	private Talon rightDrive1;
	private Talon rightDrive2;
	private Talon toteMotor;
	private Talon recycleBinMotor;
	
	//TODO: convert to solenoid
	private Solenoid grabberPneumatic;
	private Solenoid leftShifterPneumatic;
	private Solenoid rightShifterPneumatic;
	*/
	public static synchronized RobotControl getInstance()
	{
		if(instance == null)
		{
			instance = new RobotControl();
		}
		return instance;
	}
	public RobotControl()
	{		
		outputSolenoids = new HashMap<RobotPneumaticType, Solenoid>();
		outputTalons = new HashMap<RobotMotorType,Talon>();
	}
	
	public void addTalonOutput(RobotMotorType type, int port) {
		if(type == RobotMotorType.LEFT_DRIVE) {
			leftDrive.add(new Talon(port));
		} else if(type == RobotMotorType.RIGHT_DRIVE) {
			//add to right motor
			rightDrive.add(new Talon(port));
		}
		else {
			outputTalons.put(type, new Talon(port));
		}
	}
	
	public void addPneumaticOutput(RobotPneumaticType type, int port)
	{		
		outputSolenoids.put(type,new Solenoid(port));		
	}
	public void updateDriveSpeed(double leftspeed, double rightspeed)
	{
		for(Talon leftMotor : leftDrive)
		{
			leftMotor.set(-leftspeed);
		}
		for(Talon rightMotor : rightDrive)
		{
			rightMotor.set(rightspeed);
		}		
	}
	public void updateGrabberPneumatics( boolean start )
	{
		outputSolenoids.get(RobotPneumaticType.GRABBER_PNEUMATIC).set(start);
	}
	public void updateLeftShifter( boolean start )
	{	
		outputSolenoids.get(RobotPneumaticType.LEFT_SHIFTER_PNEUMATIC).set(start);	
	}
	
	public void updateRightShifter( boolean start )
	{
		outputSolenoids.get(RobotPneumaticType.RIGHT_SHIFTER_PNEUMATIC).set(start);	
	}
	public void updateToteMotor(double speed)
	{
		outputTalons.get(RobotMotorType.TOTE_LIFT).set(speed);	
	}
	public void updateRecycleMotor(double speed)
	{
		outputTalons.get(RobotMotorType.RECYCLE_LIFT).set(speed);
	}
}