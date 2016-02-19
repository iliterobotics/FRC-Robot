package org.usfirst.frc.team1885.robot.modules;

import org.usfirst.frc.team1885.robot.common.type.MotorState;
import org.usfirst.frc.team1885.robot.common.type.RobotButtonType;
import org.usfirst.frc.team1885.robot.common.type.SensorType;
import org.usfirst.frc.team1885.robot.input.DriverInputControlSRX;
import org.usfirst.frc.team1885.robot.input.SensorInputControlSRX;
import org.usfirst.frc.team1885.robot.output.RobotControlWithSRX;

import edu.wpi.first.wpilibj.DriverStation;

public class Shooter implements Module {

    private static Shooter instance;

    private final double SHOOTER_SPEED = 1;
    private final double TWIST_SPEED = .3;
    private final double TILT_SPEED = .2;
    private final double TILT_BRAKE = .1;
    public static final double GEAR_RATIO_TILT = 1.0 / 4;
    public static final double GEAR_RATIO_TWIST = 3.0 / 7;
    private double flywheelSpeedLeft;
    private MotorState leftState;
    private double flywheelSpeedRight;
    private MotorState rightState;
    private double twistSpeed;
    private MotorState twistState;
    private double tiltSpeed;
    private MotorState tiltState;
    private DriverInputControlSRX driverInputControl;
    private boolean containerState, kickerState;
    private SensorInputControlSRX sensorControl;

    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter();
        }
        return instance;
    }
    protected Shooter() {
        this.leftState = MotorState.OFF;
        this.rightState = MotorState.OFF;
        this.twistState = this.tiltState = MotorState.OFF;
        flywheelSpeedLeft = flywheelSpeedRight = twistSpeed = tiltSpeed = 0;
        driverInputControl = DriverInputControlSRX.getInstance();
        containerState = kickerState = false;
        sensorControl = SensorInputControlSRX.getInstance();
    }
    public MotorState getLeftMotorState() {
        return leftState;
    }
    public MotorState getRightMotorState() {
        return rightState;
    }
    public double getLeftSpeed() {
        return flywheelSpeedLeft;
    }
    public double getRightSpeed() {
        return flywheelSpeedRight;
    }
    public MotorState getTwistState() {
        return twistState;
    }
    public MotorState getTiltState() {
        return tiltState;
    }
    public void updateShooter() {
        // TODO modify values after testing for direction
        if (driverInputControl.getButton(RobotButtonType.FLYWHEEL_OUT)) {
            flywheelSpeedLeft = -SHOOTER_SPEED;
            flywheelSpeedRight = SHOOTER_SPEED;
        } else if (driverInputControl.getButton(RobotButtonType.FLYWHEEL_IN)) {
            flywheelSpeedLeft = SHOOTER_SPEED * .4;
            flywheelSpeedRight = -SHOOTER_SPEED * .4;
        } else {
            flywheelSpeedLeft = 0;
            flywheelSpeedRight = 0;
        }
        updateShooter(flywheelSpeedLeft, flywheelSpeedRight);
    }
    public void updateShooter(double speedLeft, double speedRight) {
        if (speedLeft > 0) {
            leftState = MotorState.REVERSE;
        } else if (speedLeft < 0) {
            leftState = MotorState.FORWARD;
        } else {
            leftState = MotorState.OFF;
        }
        if (speedRight > 0) {
            rightState = MotorState.REVERSE;
        } else if (speedRight < 0) {
            rightState = MotorState.FORWARD;
        } else {
            rightState = MotorState.OFF;
        }
    }
    public void updateTilt() {
        tiltSpeed = TILT_BRAKE;
        int tilt = driverInputControl.getShooterTilt();
        if (tilt > 0) {
            if (sensorControl.getZeroedPotentiometer(
                    SensorType.SHOOTER_TILT_POTENTIOMETER) >= 80) {
                tiltSpeed = TILT_BRAKE;
            } else {
                tiltSpeed = TILT_SPEED + TILT_BRAKE;
            }
        } else if (tilt < 0) {
            if (sensorControl.getZeroedPotentiometer(
                    SensorType.SHOOTER_TILT_POTENTIOMETER) <= 5) {
                tiltSpeed = TILT_BRAKE;
            } else {
                tiltSpeed = -TILT_SPEED + TILT_BRAKE;
            }
        }
        updateTilt(tiltSpeed);
    }
    public void updateTilt(double speed) {
        if (speed > 0) {
            tiltState = MotorState.REVERSE;
        } else if (speed < 0) {
            tiltState = MotorState.FORWARD;
        } else {
            tiltState = MotorState.OFF;
        }
    }
    public void updateTwist() {
        twistSpeed = 0;
        int twist = driverInputControl.getShooterTwist();
        if (sensorControl.getZeroedPotentiometer(
                SensorType.SHOOTER_TILT_POTENTIOMETER) >= 20) {
            if (twist > 0) {
                //if ( sensorControl.getEncoderPos(SensorType.SHOOTER_TWIST_ENCODER) < 128 ) {
                    twistSpeed = -TWIST_SPEED;
                //}
            } else if (twist < 0) {
                //if ( sensorControl.getEncoderPos(SensorType.SHOOTER_TWIST_ENCODER) * GEAR_RATIO_TWIST > 896) {
                    twistSpeed = TWIST_SPEED;
                //}
            }
            updateTwist(twistSpeed);
        }
        
    }
    public void updateTwist(double speed) {
        if (speed > 0) {
            twistState = MotorState.REVERSE;
        } else if (speed < 0) {
            twistState = MotorState.FORWARD;
        } else {
            twistState = MotorState.OFF;
        }
    }
    public void reset() {
        rightState = leftState = MotorState.OFF;
        flywheelSpeedRight = flywheelSpeedLeft = 0;
    }
    public void updateContainer() {
        if (DriverInputControlSRX.getInstance()
                .getButton(RobotButtonType.SHOOTER_CONTAINER_TOGGLE)) {
            containerState = !containerState;
            DriverStation.reportError("Containing: " + containerState + "\n",
                    false);
        }
    }
    public void updateKicker() {
        if (DriverInputControlSRX.getInstance().getButton(
                RobotButtonType.SHOOTER_KICKER_KICK) && !kickerState) {
            kickerState = true;
            DriverStation.reportError("Kicking", false);
        } else if (DriverInputControlSRX.getInstance().getButton(
                RobotButtonType.SHOOTER_KICKER_RETRACT) && kickerState) {
            kickerState = false;
            DriverStation.reportError("Retracting", false);
        }
    }
    public void updateOutputs() {
        RobotControlWithSRX.getInstance().updateFlywheelShooter(getLeftSpeed(),
                getRightSpeed());
        RobotControlWithSRX.getInstance().updateShooterTilt(tiltSpeed);
        RobotControlWithSRX.getInstance().updateShooterTwist(twistSpeed);
        // RobotControlWithSRX.getInstance()
        // .updateSingleSolenoid(RobotPneumaticType.SHOOTER_CONTAINER,
        // containerState);
        // RobotControlWithSRX.getInstance()
        // .updateSingleSolenoid(RobotPneumaticType.SHOOTER_KICKER,
        // kickerState);
    }
    @Override
    public void update() {
        updateShooter();
        updateTilt();
        updateTwist();
        updateOutputs();
    }

}
