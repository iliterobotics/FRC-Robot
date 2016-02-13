package org.usfirst.frc.team1885.robot.manipulator;

import org.usfirst.frc.team1885.robot.common.type.MotorState;
import org.usfirst.frc.team1885.robot.common.type.RobotButtonType;
import org.usfirst.frc.team1885.robot.common.type.SensorType;
import org.usfirst.frc.team1885.robot.input.DriverInputControlSRX;
import org.usfirst.frc.team1885.robot.input.SensorInputControlSRX;
import org.usfirst.frc.team1885.robot.modules.Module;
import org.usfirst.frc.team1885.robot.output.RobotControlWithSRX;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author ILITE Robotics
 * @version 2/12/2016
 * 
 *          UtilityArm is the code for the side arm of the 2016 Stronghold Robot
 *          The arm features a double jointed system with a potentiometer and
 *          motor attached to each joint
 */
public class UtilityArm implements Module {

    private static UtilityArm instance;

    private static final double LENGTH_A = 17.5;
    private static final double LENGTH_B = 19.5;
    public static final double CONVERSION_FACTOR = 360.0 / 1024;
    private static final double MOTOR_SPEED_A = .3;
    private static final double MOTOR_SPEED_B = .6;
    private static final double DEGREE_MARGIN_E = 6;
    private static final double JOY_DEADZONE = .1;
    private static final double JOY_CHANGE = .005;

    private double jointASpeed; // speed that joint A will go at
    private double jointBSpeed; // speed that joint B will go at
    private double jointAAngle; // storage for updating the A angle
    private double jointBAngle; // storage for updating the B angle
    private double xDirection; // what direction the system needs to move in
    private double yDirection; // what direction the system needs to move in
    
    private boolean jointAInPosition;
    private boolean jointBInPosition;

    @SuppressWarnings("unused")
    private MotorState jointAState;
    @SuppressWarnings("unused")
    private MotorState jointBState;
    private SensorInputControlSRX sensorInputControl;
    private DriverInputControlSRX driverInputControl;

    protected UtilityArm() {
        this.jointAState = MotorState.OFF;
        this.jointBState = MotorState.OFF;
        jointAAngle = getAngleA();
        jointBAngle = getAngleB();
        xDirection = 0;
        yDirection = 0;
        sensorInputControl = SensorInputControlSRX.getInstance();
        driverInputControl = DriverInputControlSRX.getInstance();
        jointAInPosition = jointBInPosition = false;
    }

    // TODO singletons cause memory leaks
    public static UtilityArm getInstance() {
        if (instance == null) {
            instance = new UtilityArm();
        }
        return instance;
    }

    @Override
    public void update() {
        changeValues();
        if (driverInputControl.isResetButtonDown()) {
            jointAAngle = 0;
            jointBAngle = 170;
        }

        updateOutputs();
    }

    @Override
    public void updateOutputs() {
        if (jointAAngle - getAngleA() > DEGREE_MARGIN_E) {
            jointASpeed = MOTOR_SPEED_A;
        } else if (jointAAngle - getAngleA() < -DEGREE_MARGIN_E) {
            jointASpeed = -MOTOR_SPEED_A;
        } else {
            jointASpeed = 0;
        }
        if (jointBAngle - getAngleB() > DEGREE_MARGIN_E) {
            jointBSpeed = -MOTOR_SPEED_B;
        } else if (jointBAngle - getAngleB() < -DEGREE_MARGIN_E) {
            jointBSpeed = MOTOR_SPEED_B;
        } else {
            jointBSpeed = 0;
        }
        RobotControlWithSRX.getInstance().updateArmMotors(jointASpeed,
                jointBSpeed);
    }

    /**
     * Gets x position for the end-point of the utility arm with respect to the
     * base joint in inches
     */
    public double getDistanceX() {
        double distanceB = (LENGTH_B * (Math.cos(Math.toRadians(getAngleB()))));
        double distanceA = (LENGTH_A * (Math.cos(Math.toRadians(getAngleA()))));
        return distanceA + distanceB;
    }

    /**
     * Gets y position for the end-point of the utility arm with respect to the
     * base joint in inches
     */
    public double getDistanceY() {
        double distanceB = (LENGTH_B * (Math.sin(Math.toRadians(getAngleB()))));
        double distanceA = (LENGTH_A * (Math.sin(Math.toRadians(getAngleA()))));
        return distanceA + distanceB;
    }

    /**
     * Gets angle in degrees for the bottom joint going clockwise from 0
     */
    public double getAngleA() {
        sensorInputControl = SensorInputControlSRX.getInstance();
        double angleA = sensorInputControl.getAnalogGeneric(
                SensorType.JOINT_A_POTENTIOMETER) * CONVERSION_FACTOR;
        double zeroedA = angleA - sensorInputControl.getInitialPotAPostition();
        return zeroedA;
    }

    /**
     * Gets angle in degrees for the top joint going clockwise from 0
     */
    public double getAngleB() {
        double angleA = getAngleA();
        double angleB = angleA + 360
                - (sensorInputControl.getAnalogGeneric(
                        SensorType.JOINT_B_POTENTIOMETER) * CONVERSION_FACTOR
                - sensorInputControl.getInitialPotBPostition() + 190);
        return angleB;
    }

    /**
     * Finds the conversion from the joystick throttle value to x movement in
     * inches
     */
    public double getJoystickXConversion() {
        double throttle = driverInputControl.getControllerThrottle();
        if (throttle > JOY_DEADZONE) {
            return xDirection = JOY_CHANGE * Math.abs(throttle);
        } else if (throttle < -JOY_DEADZONE) {
            return xDirection = -JOY_CHANGE * Math.abs(throttle);
        } else {
            return xDirection = 0.0;
        }
    }

    /**
     * Finds the conversion from the joystick twist value to y movement in
     * inches
     */
    public double getJoystickYConversion() {
        double twist = driverInputControl.getControllerTwist();
        if (twist > JOY_DEADZONE) {
            return yDirection = -JOY_CHANGE * Math.abs(twist);
        } else if (twist < -JOY_DEADZONE) {
            return yDirection = JOY_CHANGE * Math.abs(twist);
        } else {
            return yDirection = 0.0;
        }
    }

    /**
     * Takes the change in x and y movement and converts them to angular change
     */
    public void changeValues() {
        // change in Y
        double origA = jointAAngle;
        double origB = jointBAngle;

        double changeX = getJoystickXConversion();
        double changeY = getJoystickYConversion();

        if (driverInputControl.isButtonDown(RobotButtonType.INCREMENT_ARM_UP)) {
            changeY = 0.2;
        }
        if (driverInputControl
                .isButtonDown(RobotButtonType.INCREMENT_ARM_DOWN)) {
            changeY = -0.2;
        }
        if (driverInputControl
                .isButtonDown(RobotButtonType.INCREMENT_ARM_LEFT)) {
            changeX = -0.2;
        }
        if (driverInputControl
                .isButtonDown(RobotButtonType.INCREMENT_ARM_RIGHT)) {
            changeX = 0.2;
        }
        
        double sinY;
        if(Math.abs(changeY) >= Math.abs(changeX)){
            sinY = Math.sin(Math.toRadians(origB)) + changeY;
            if (sinY <= 1 && sinY >= -1) {
                jointBAngle = 180 - Math.toDegrees(Math.asin(sinY));
                double cosX = Math.cos(Math.toRadians(origA))
                    - (Math.cos(Math.toRadians(jointBAngle))
                            - Math.cos(Math.toRadians(origB)));
                if (cosX >= -1 && cosX <= 1) {
                // TODO set cosx to the limit that it is passing
                    jointAAngle = Math.toDegrees(Math.acos(cosX));
                }
            }
        }else{
        // change in X
            origA = jointAAngle;
            origB = jointBAngle;
            double cosX = Math.cos(Math.toRadians(origA)) + changeX;
            if (cosX >= -1 && cosX <= 1) {
                jointAAngle = Math.toDegrees(Math.acos(cosX));
                sinY = Math.sin(Math.toRadians(origB))
                    - (Math.sin(Math.toRadians(jointAAngle))
                            - Math.cos(Math.toRadians(origA)));
                if (sinY <= 1 && sinY >= -1) {
                    jointBAngle = 180 - Math.toDegrees(Math.asin(sinY));
                }
            }
        }
        if(jointBAngle > 170 - jointAAngle) {
            jointBAngle = 170 - jointAAngle;
        }
        if(jointBAngle < jointAAngle){
            jointBAngle = jointAAngle;
        }
     
    }

    public double getJointASpeed() {
        return jointASpeed;
    }
    public double getJointBSpeed(){
        return jointBSpeed;
    }

    public void updateArm(double jointAAngle, double jointBAngle) {
        this.jointAAngle = jointAAngle;
        this.jointBAngle = jointBAngle;
        update();
    }
}
