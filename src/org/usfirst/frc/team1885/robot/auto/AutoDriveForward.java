package org.usfirst.frc.team1885.robot.auto;

import org.usfirst.frc.team1885.robot.common.PID;
import org.usfirst.frc.team1885.robot.common.type.SensorType;
import org.usfirst.frc.team1885.robot.input.SensorInputControl;
import org.usfirst.frc.team1885.robot.output.RobotControl;

public class AutoDriveForward implements AutoCommand{
	
	private PID distanceControlLoop;
	private double distance;
	private double error;
	private double leftDriveOutput;
	private double rightDriveOutput; 
	private double leftDistanceTraveled;
	private double rightDistanceTraveled;
	public AutoDriveForward(double d, double e) {
		distanceControlLoop = new PID();
		distance = d;
		error = e;
		SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_LEFT_ENCODER).reset();
		SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_RIGHT_ENCODER).reset();
	}
	public boolean execute() {
		leftDistanceTraveled = SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_LEFT_ENCODER).getDistance();
		rightDistanceTraveled = SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_RIGHT_ENCODER).getDistance();
		if (Math.abs(leftDistanceTraveled  - distance) <= error && Math.abs(rightDistanceTraveled  - distance) <= error ) {
			this.reset();
			return true;
		} else {
			leftDriveOutput = distanceControlLoop.getPID(distance, SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_LEFT_ENCODER).getDistance());
			rightDriveOutput = distanceControlLoop.getPID(distance, SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_RIGHT_ENCODER).getDistance());
			RobotControl.getInstance().updateDriveSpeed(leftDriveOutput, rightDriveOutput);
			return false;
		}
	}
	public void reset() {
		distanceControlLoop.reset();
		SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_LEFT_ENCODER).reset();
		SensorInputControl.getInstance().getEncoder(SensorType.DRIVE_TRAIN_RIGHT_ENCODER).reset();
		RobotControl.getInstance().updateDriveSpeed(0, 0);
	}
}
