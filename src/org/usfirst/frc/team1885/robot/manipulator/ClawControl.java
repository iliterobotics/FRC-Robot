package org.usfirst.frc.team1885.robot.manipulator;

import org.usfirst.frc.team1885.robot.common.type.RobotButtonType;
import org.usfirst.frc.team1885.robot.input.DriverInputControl;
import org.usfirst.frc.team1885.robot.output.RobotControl;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ClawControl 
{
	private static ClawControl instance;
	private boolean rotationState;
	private boolean extensionState;
	private boolean pinchState;
	
	protected ClawControl(){
	}
	public static ClawControl getInstance(){
	    if(instance == null){
	        instance = new ClawControl();
	    }
	    return instance;
	}
	public void updateClaw(){
	        if (DriverInputControl.getInstance().getPressureButton(RobotButtonType.WRIST_ROTATION) >= .35) {
	            rotationState = true;
	    	} else{
	            rotationState = false;
	        }
	        
	        if (DriverInputControl.getInstance().getPressureButton(RobotButtonType.WRIST_EXTENSION) >= .35) {
	            extensionState = true;
	    	} else{
	            extensionState = false;
	        }
	        
	        if (DriverInputControl.getInstance().getButton(RobotButtonType.CLAW)) {
	            pinchState = true;
	    	} else{
	            pinchState = false;
	        }
	}
	public void updateClaw(boolean rotationState, boolean extensionState, boolean pinchState){
		this.rotationState = rotationState;
		this.extensionState = extensionState;
		this.pinchState = pinchState;
	}
	public boolean clawWristRotationState(){
	    return rotationState;
	}
	public boolean clawWristExtentionState(){
	    return extensionState;
	}
	public boolean clawPinchState(){
	    return pinchState;
	}
	public void updateOutputs() {
		if(extensionState) {
			RobotControl.getInstance().updateWristExtensionPneumatics(Value.kForward);
		} else {
			RobotControl.getInstance().updateWristExtensionPneumatics(Value.kReverse);
		}
		
		if(rotationState) {
			RobotControl.getInstance().updateWristRotationPneumatics(Value.kForward);
		} else {
			RobotControl.getInstance().updateWristRotationPneumatics(Value.kReverse);
		}
		
		RobotControl.getInstance().updateGrabberPneumatics(pinchState);

    }
}
