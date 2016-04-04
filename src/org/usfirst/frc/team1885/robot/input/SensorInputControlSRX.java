package org.usfirst.frc.team1885.robot.input;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team1885.robot.common.type.RobotMotorType;
import org.usfirst.frc.team1885.robot.common.type.SensorType;
import org.usfirst.frc.team1885.robot.config2016.RobotConfiguration;
import org.usfirst.frc.team1885.robot.output.RobotControlWithSRX;
import org.usfirst.frc.team1885.robot.sensor.BeamSensor;
import org.usfirst.frc.team1885.robot.sensor.LidarSensor;
import org.usfirst.frc.team1885.robot.sensor.PressureSensor;
import org.usfirst.frc.team1885.robot.sensor.RotarySwitchSensor;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;

public class SensorInputControlSRX {

    private static final int TICKS_IN_360 = 1024;
    private double INITIAL_PITCH; // Shouldn't change
    private double INITIAL_ROLL; // Shouldn't change
    private static SensorInputControlSRX instance = null;
    private static RobotControlWithSRX rsrx;
    private PowerDistributionPanel PDP;
    private LidarSensor ls;
    private BuiltInAccelerometer bia;
    private AHRS navx;
    private PressureSensor pressureSensor;
    private Map<SensorType, RotarySwitchSensor> rotarySwitchSensors;
    private BeamSensor beamSensor;

    public static final double DEADZONE = 0.1;

    private double INITIAL_POT_B_POSITION;
    private double INITIAL_POT_A_POSITION;
    private double INITIAL_TWIST_POSITION;
    private double INITIAL_TILT_POSITION;

    private static final double POTENTIOMETER_CONVERSION_FACTOR = 1024.0 / 360;
    private Map<SensorType, Integer> ticks;

    public static SensorInputControlSRX getInstance() {
        if (instance == null) {
            instance = new SensorInputControlSRX();
        }
        return instance;
    }
    private SensorInputControlSRX() {
        rsrx = RobotControlWithSRX.getInstance();
        PDP = new PowerDistributionPanel();
        ticks = new HashMap<SensorType, Integer>();
        rotarySwitchSensors = new HashMap<SensorType, RotarySwitchSensor>();
    }
    public void update() {
    }
    // Create initial sensor readings
    public void init() {
        INITIAL_POT_A_POSITION = rsrx.getTalons()
                .get(RobotMotorType.ARM_JOINT_A).getEncPosition();
        INITIAL_POT_B_POSITION = rsrx.getTalons()
                .get(RobotMotorType.ARM_JOINT_B).getEncPosition();
        // INITIAL_TILT_POSITION =
        // RobotControlWithSRX.getInstance().getTalons().get(RobotMotorType.SHOOTER_TILT).get();
        INITIAL_TILT_POSITION = -511;
        DriverStation.reportError("\nInit Tilt:: " + INITIAL_TILT_POSITION, false);
        INITIAL_TWIST_POSITION = 0;
        DriverStation.reportError("\nInit Twist:: " + INITIAL_TWIST_POSITION,
                false);
    }
    public double getInitPitch() {
        return INITIAL_PITCH;
    }
    public double getInitRoll() {
        return INITIAL_ROLL;
    }
    // Get intial reading value
    public double getInitialPotAPostition() {
        return INITIAL_POT_A_POSITION;
    }
    public double getInitialPotBPostition() {
        return INITIAL_POT_B_POSITION;
    }
    public double getInitialTiltPosition() {
        return INITIAL_TILT_POSITION;
    }
    public double getInitialTwistPosition() {
        return INITIAL_TWIST_POSITION;
    }
    // Create navx
    public void createNavX(SerialPort.Port port) {
        navx = new AHRS(port);
    }
    public AHRS getNavX() {
        return navx;
    }
    // Calibrate navx
    public void calibrateGyro() {
        navx.zeroYaw();
        Timer.delay(.3); // Time needed to calibrate gyro
        INITIAL_PITCH = navx.getPitch();
        INITIAL_ROLL = navx.getRoll();
        DriverStation.reportError("\nGyro Calibrated at: " + getYaw(), false);
    }
    // Get gyro values
    public double getPitch() {
        return navx.getPitch();
    }
    public double getYaw() {
        return navx.getYaw();
    }
    public double getRoll() {
        return navx.getRoll();
    }

    public double getCurrent(int channel) {
        return PDP.getCurrent(channel);
    }
    public double getPDPTemperature() {
        return PDP.getTemperature();
    }
    public double getAnalogGeneric(SensorType type) {
        return rsrx.getSensor().get(type).getAnalogInRaw();
    }
    public double getAnalogInPosition(SensorType type) {
        return rsrx.getSensor().get(type).getAnalogInPosition();
    }
    // Get encoder values
    public int getEncoderPos(SensorType type) {
        return rsrx.getSensor().get(type).getEncPosition();
    }

    public int getEncoderVelocity(SensorType type) {
        return rsrx.getSensor().get(type).getEncVelocity();
    }
    public double getEncoderAbsolutePosition(SensorType type) {
        return rsrx.getSensor().get(type).getPulseWidthPosition();
    }
    public double getEncoderDistance(SensorType type) {
        if (type == SensorType.LEFT_ENCODER) {
            return -RobotConfiguration.WHEEL_DIAMETER * Math.PI
                    * rsrx.getSensor().get(type).getEncPosition()
                    / TICKS_IN_360;
        }
        return RobotConfiguration.WHEEL_DIAMETER * Math.PI
                * rsrx.getSensor().get(type).getEncPosition() / TICKS_IN_360;
    }
    // Get zeroed value relative to the initial reading
    public double getZeroedPotentiometer(SensorType type) {
        switch (type) {
        case JOINT_A_POTENTIOMETER:
            return getAnalogGeneric(type) - INITIAL_POT_A_POSITION;
        case JOINT_B_POTENTIOMETER:
            return getAnalogGeneric(type) - INITIAL_POT_B_POSITION;
        case SHOOTER_TILT_POTENTIOMETER:
            return getAnalogGeneric(type) - INITIAL_TILT_POSITION;
        default:
            return getAnalogGeneric(type);
        }
    }
    public double getZeroedEncoder(SensorType type) {
        switch (type) {
        case SHOOTER_TWIST_ENCODER:
            return rsrx.getTalons().get(RobotMotorType.SHOOTER_TWIST).get() - INITIAL_TWIST_POSITION;
        default:
            return rsrx.getTalons().get(RobotMotorType.SHOOTER_TWIST).get();
        }
    }
    public boolean digitalLimitSwitch(SensorType type) {
        return rsrx.getSensor().get(type).isFwdLimitSwitchClosed();
    }
    // Add specialized sensors
    public void addLidarSensor(Port port) {
        ls = new LidarSensor(port);
    }
    public LidarSensor getLidarSensor() {
        return this.ls;

    }
    public void createAccelerometer() {
        bia = new BuiltInAccelerometer();
    }
    public BuiltInAccelerometer getAccelerometer() {
        return bia;
    }
    public void addPressureSensor(int channel) {
        pressureSensor = new PressureSensor(channel);
    }
    public double getPressureAverageVoltage() {
        return pressureSensor.getAverageVoltage();
    }
    public double getPressureVoltage() {
        return pressureSensor.getVoltage();
    }
    public double getPressure() {
        return pressureSensor.getPressure();
    }
    public void addRotarySwitchSensor(SensorType type, int channel) {
        rotarySwitchSensors.put( type, new RotarySwitchSensor(channel));
    }
    public double getRotaryPosition(SensorType type) {
        if(rotarySwitchSensors.containsKey(type)){
            return rotarySwitchSensors.get(type).getPosition();
        } 
        return 0.0;
    }
    public void addBeamSensor(int channel) {
        beamSensor = new BeamSensor(channel);
    }
    public boolean getBeam() {
        return beamSensor.get();
    }
    public void resetEncoder(SensorType type) {
        rsrx.getSensor().get(type).setEncPosition(0);
    }
    public double getInitPotBPos() {
        return INITIAL_POT_B_POSITION;
    }
    public double getInitPotAPos() {
        return INITIAL_POT_A_POSITION;
    }
}