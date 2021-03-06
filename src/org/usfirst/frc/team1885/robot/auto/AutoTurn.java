package org.usfirst.frc.team1885.robot.auto;

import org.usfirst.frc.team1885.robot.common.type.RobotMotorType;
import org.usfirst.frc.team1885.robot.config2016.RobotConfiguration;
import org.usfirst.frc.team1885.robot.input.SensorInputControlSRX;
import org.usfirst.frc.team1885.robot.modules.drivetrain.DrivetrainControl;
import org.usfirst.frc.team1885.robot.output.RobotControlWithSRX;

import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class rotates the robot into it's initial facing - yaw - position.
 * 'Initial facing' position being the position at which the robot was when this
 * class was created. It rotates to that position by anchoring one side of the
 * drive train and negatively powering the other.
 * 
 * @author ILITE Robotics
 * @version 2/13/2016
 */
public class AutoTurn extends AutoCommand {

    private final double ALIGNMENT_ERROR = 6;
    private double targetDegree;
    private SensorInputControlSRX sensorInputControl;
    private double initialYaw;
    private double direction;
    private boolean aligningToZero;
    public static final double TURN_RADIUS = 16;
    
    public AutoTurn() {
        sensorInputControl = SensorInputControlSRX.getInstance();
        initialYaw = (sensorInputControl.getYaw() + 360) % 360;
        targetDegree = 0;
        direction = 1;
        aligningToZero = true;
    }

    public AutoTurn(double degree) {
        this();
        targetDegree = (Math.abs(degree) - initialYaw + 360) % 360;
        direction = degree < 0 ? -1 : 1;
        aligningToZero = false;
    }

    @Override
    public boolean init() {
        DrivetrainControl.getInstance().setControlMode(TalonControlMode.Position);
        if( targetDegree == 0 ){
            targetDegree = -sensorInputControl.getYaw();
        }
        
        double currentTicksLeft = RobotControlWithSRX.getInstance().getTalons().get(RobotMotorType.LEFT_DRIVE).get();
        double currentTicksRight = RobotControlWithSRX.getInstance().getTalons().get(RobotMotorType.RIGHT_DRIVE).get();
        RobotControlWithSRX.getInstance().getTalons().get(RobotMotorType.LEFT_DRIVE).set(direction * (Math.toRadians(targetDegree) * TURN_RADIUS) /(Math.PI * RobotConfiguration.WHEEL_DIAMETER) * DrivetrainControl.TICKS_IN_ROTATION + currentTicksLeft);
        RobotControlWithSRX.getInstance().getTalons().get(RobotMotorType.RIGHT_DRIVE).set(direction * (Math.toRadians(targetDegree) * TURN_RADIUS) /(Math.PI * RobotConfiguration.WHEEL_DIAMETER) * DrivetrainControl.TICKS_IN_ROTATION + currentTicksRight);
        return true;
    }

    @Override
    public boolean execute() {
        double yaw = sensorInputControl.getYaw();
        double difference = (yaw - targetDegree);

        if(aligningToZero){
            difference = yaw;
        }
        
        DriverStation.reportError("\n Degree to turn : " + targetDegree
                + " --- Normalized yaw: " + yaw + "\n difference:: " + difference, false);
        
        if (Math.abs(difference) < ALIGNMENT_ERROR) {
            DriverStation.reportError("\nAligned.", false);
            this.reset();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOutputs() {
        //no outputs, controlled by SRX PID
        return false;
    }

    @Override
    public void reset() {
    }
}