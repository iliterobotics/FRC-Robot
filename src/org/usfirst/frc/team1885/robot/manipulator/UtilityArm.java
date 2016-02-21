package org.usfirst.frc.team1885.robot.manipulator;

import org.usfirst.frc.team1885.robot.common.PID;
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

    public static final double LENGTH_A = 17.5;
    public static final double LENGTH_B = 18;
    public static final double CONVERSION_FACTOR = 360.0 / 1024;
    public static final double DEF_A_ANGLE = .3, DEF_B_ANGLE = 170;
    private static final double MOTOR_SPEED_A = .2;
    private static final double MOTOR_SPEED_B = .2;
    private static final double DEGREE_MARGIN_E = 2;
    private static final double JOY_DEADZONE = .1;
    private static final double JOY_CHANGE = .005;

    private double jointASpeed; // speed that joint A will go at
    private double jointBSpeed; // speed that joint B will go at
    private double jointAAngle; // storage for updating the A angle
    private double jointBAngle; // storage for updating the B angle
    private double xDirection; // what direction the system needs to move in
    private double yDirection; // what direction the system needs to move in
    private double aP, aI, aD; // values for the PID to move joint A
    private double bP, bI, bD; // values for the PID to move joint B

    private double goingToX;
    private double goingToY;

    private PID jointAControlLoop; // PID to control movement of joint A
    private PID jointBControlLoop; // PID to control movement of joint B

    @SuppressWarnings("unused")
    private MotorState jointAState;
    @SuppressWarnings("unused")
    private MotorState jointBState;
    private SensorInputControlSRX sensorInputControl;
    private DriverInputControlSRX driverInputControl;

    protected UtilityArm() {
        aP = 0.5;
        aI = 0.005;
        aD = 0;
        bP = 1;
        bI = 0;
        bD = 0;
        this.jointAState = MotorState.OFF;
        this.jointBState = MotorState.OFF;
        jointAAngle = getAngleA();
        jointBAngle = getAngleB();
        xDirection = 0;
        yDirection = 0;
        sensorInputControl = SensorInputControlSRX.getInstance();
        driverInputControl = DriverInputControlSRX.getInstance();
        jointAControlLoop = new PID(aP, aI, aD);
        jointBControlLoop = new PID(bP, bI, bD);
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
        // changeValues();
        if (driverInputControl.isResetButtonDown()) {
            jointAAngle = DEF_A_ANGLE;
            jointBAngle = DEF_B_ANGLE;
        }
        if (driverInputControl.isButtonDown(RobotButtonType.INCREMENT_ARM_UP)) {
            goTo(-20, 10);
        }
        // DriverStation.reportError("desired A angle:" + jointAAngle + "\n",
        // false);
        // DriverStation.reportError("desired B angle:" + jointBAngle + "\n",
        // false);
        // DriverStation.reportError("current A angle:" + getAngleA() + "\n",
        // false);
        // DriverStation.reportError("current B angle:" + getAngleB() + "\n",
        // false);
        updateOutputs();
        // DriverStation.reportError("\n\nX Distance = " + getDistanceX(),
        // false);
        // DriverStation.reportError("\nY Distance = " + getDistanceY(), false);
    }

    @Override
    public void updateOutputs() {
        jointASpeed = jointAControlLoop.getPID(jointAAngle, getAngleA());
        jointBSpeed = -jointBControlLoop.getPID(jointBAngle, getAngleB());
        //
        // if (jointBAngle - getAngleB() > DEGREE_MARGIN_E) {
        // jointBSpeed = -MOTOR_SPEED_B;
        // } else if (jointBAngle - getAngleB() < -DEGREE_MARGIN_E) {
        // jointBSpeed = MOTOR_SPEED_B;
        // }

        if (jointAAngle - getAngleA() < DEGREE_MARGIN_E
                && jointAAngle - getAngleA() > -DEGREE_MARGIN_E) {
            jointASpeed = 0;
        }
        if (jointBAngle - getAngleB() < DEGREE_MARGIN_E
                && jointBAngle - getAngleB() > -DEGREE_MARGIN_E) {
            jointBSpeed = 0;
        }

        DriverStation.reportError("\n\nJoint B:: Speed: " + jointBSpeed
                + " Goal: " + jointBAngle + " Current: " + getAngleB() + " P: "
                + jointBControlLoop.getP() + " I: "
                + jointBControlLoop.getI(1.0) + " D: "
                + jointBControlLoop.getD(), false);

        DriverStation.reportError("\n\nJoint A:: Speed: " + jointASpeed
                + " Goal: " + jointAAngle + " Current: " + getAngleA() + " P: "
                + jointBControlLoop.getP() + " I: "
                + jointBControlLoop.getI(1.0) + " D: "
                + jointBControlLoop.getD(), false);

        RobotControlWithSRX.getInstance().updateArmMotors(jointASpeed,
                jointBSpeed);
    }

    /*
     * Gets x position for the end-point of the utility arm with respect to the
     * base joint in inches
     */
    public double getDistanceX() {
        double distanceB = (LENGTH_B * (Math.cos(Math.toRadians(getAngleB()))));
        double distanceA = (LENGTH_A * (Math.cos(Math.toRadians(getAngleA()))));
        return distanceA + distanceB;
    }

    /*
     * Gets y position for the end-point of the utility arm with respect to the
     * base joint in inches
     */
    public double getDistanceY() {
        double distanceB = (LENGTH_B * (Math.sin(Math.toRadians(getAngleB()))));
        double distanceA = (LENGTH_A * (Math.sin(Math.toRadians(getAngleA()))));
        return distanceA + distanceB;
    }

    /*
     * Gets angle in degrees for the bottom joint going clockwise from 0
     */
    public double getAngleA() {
        sensorInputControl = SensorInputControlSRX.getInstance();
        double angleA = sensorInputControl.getAnalogGeneric(
                SensorType.JOINT_A_POTENTIOMETER) * CONVERSION_FACTOR;
        double zeroedA = angleA - sensorInputControl.getInitialPotAPostition();
        return zeroedA;
    }

    /*
     * Gets angle in degrees for the top joint going clockwise from 0
     */
    public double getAngleB() {
        double angleA = getAngleA();
        double angleB = angleA + 360
                - (sensorInputControl.getAnalogGeneric(
                        SensorType.JOINT_B_POTENTIOMETER) * CONVERSION_FACTOR
                - sensorInputControl.getInitialPotBPostition() + 190);

        DriverStation.reportError(
                "\n getAngleB() --- " + angleB + " --- Initial position B: "
                        + sensorInputControl.getInitialPotBPostition(),
                false);

        return angleB;
    }

    /*
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

    /*
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
     * A simple down to earth equation for calculating the angles required to
     * acieve a point
     * 
     * @param x
     *            the x coordinate of the new end-point
     * @param y
     *            the y coordinate of the new end-point
     */
    public void goTo(double x, double y) {

        if (x > LENGTH_A - 2 && y < LENGTH_B - 2) {
            x = LENGTH_A - 2;
            y = LENGTH_B - 2;
        }

        double p = Math.sqrt((x * x) + (y * y));
        double k = ((p * p) + LENGTH_A * LENGTH_A - LENGTH_B * LENGTH_B)
                / (2 * p);

        double x1 = (x * k) / p
                + (y / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);
        double y1 = (y * k) / p
                - (x / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);

        double x2 = (x * k) / p
                - (y / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);
        double y2 = (y * k) / p
                + (x / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);

        double finaly = 0;
        double finalx = 0;
        if (y1 < 0) {
            finaly = y2;
            finalx = x2;
        } else {
            finaly = y1;
            finalx = x1;
        }

        // DriverStation.reportError("\nfinalX:" + finalx, false);
        // DriverStation.reportError("\nfinalY:" + finaly, false);
        //
        // DriverStation.reportError("\ntanA:" + finalx / finaly, false);
        // DriverStation.reportError("\ntanB:" + (y - finaly) / (x - finalx),
        // false);

        jointAAngle = Math.toDegrees(Math.atan2(finaly, finalx));

        double transformedX = (x - finalx);
        double transformedY = (y - finaly);
        jointBAngle = Math.toDegrees(Math.atan2(transformedY, transformedX));
        if (jointBAngle < 0) {
            jointBAngle += 360;
        }

        if (jointAAngle < DEF_A_ANGLE) {
            jointAAngle = DEF_A_ANGLE;
        }
        if (jointAAngle > 180) {
            jointAAngle = 180;
        }
        if (jointBAngle > (160 + jointAAngle)) {
            jointBAngle = (160 + jointAAngle);
        }
        if (jointBAngle < jointAAngle) {
            jointBAngle = jointAAngle;
        }

        // DriverStation.reportError("\nJointAAngle:" + jointAAngle, false);
        // DriverStation.reportError("\nJointBAngle:" + jointBAngle, false);

        jointAControlLoop.setScalingValue(jointAAngle);
        jointBControlLoop.setScalingValue(jointBAngle);

        goingToX = x;
        goingToY = y;

    }

    /*
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
        if (Math.abs(changeY) >= Math.abs(changeX)) {
            sinY = Math.sin(Math.toRadians(origB)) + changeY;
            if (sinY <= 1 && sinY >= -1) {
                jointBAngle = 180 - Math.toDegrees(Math.asin(sinY));
                double cosX = Math.cos(Math.toRadians(origA))
                        - (Math.cos(Math.toRadians(jointBAngle))
                                - Math.cos(Math.toRadians(origB)));
                // DriverStation.reportError("cos: " + cosX + "\n", false);
                // DriverStation.reportError("sin: " + sinY + "\n", false);
                if (cosX >= -1 && cosX <= 1) {
                    // TODO set cosx to the limit that it is passing
                    jointAAngle = Math.toDegrees(Math.acos(cosX));
                }
            }
        } else {
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
        if (jointBAngle > DEF_B_ANGLE - jointAAngle) {
            jointBAngle = DEF_B_ANGLE - jointAAngle;
        }
        if (jointBAngle < jointAAngle) {
            jointBAngle = jointAAngle;
        }
    }
    public boolean isFinished() {
        boolean isJointAFinished = jointAAngle - getAngleA() < DEGREE_MARGIN_E
                && jointAAngle - getAngleA() > -DEGREE_MARGIN_E;
        boolean isJointBFinished = jointBAngle - getAngleB() < DEGREE_MARGIN_E
                && jointBAngle - getAngleB() > -DEGREE_MARGIN_E;

        // DriverStation.reportError("\nAre finished? ("
        // + (isJointAFinished ? "true" : "false") + ", "
        // + (isJointBFinished ? "true" : "false") + ")\n Joint Angle A: "
        // + jointAAngle + " -- Joint Angle B: " + jointBAngle
        // + "\n Get Angle A: " + getAngleA() + " -- Get Angle B: "
        // + getAngleB() + "\nInitial B value: "
        // + sensorInputControl.INITIAL_POT_B_POSITION +
        // "\nRaw angle B:" + sensorInputControl.getAnalogGeneric(
        // SensorType.JOINT_B_POTENTIOMETER) * CONVERSION_FACTOR +
        // "\nRaw pot val:" + sensorInputControl.getAnalogGeneric(
        // SensorType.JOINT_B_POTENTIOMETER), false);
        if (isJointAFinished && isJointBFinished) {
            jointAControlLoop.reset();
            jointBControlLoop.reset();
            jointASpeed = 0;
            jointBSpeed = 0;
            RobotControlWithSRX.getInstance().updateArmMotors(jointASpeed,
                    jointBSpeed);
        }
        return isJointAFinished && isJointBFinished;
    }
    public void resetPos() {
        jointAAngle = DEF_A_ANGLE;
        jointBAngle = DEF_B_ANGLE;
    }

}
