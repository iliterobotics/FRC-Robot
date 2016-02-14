package org.usfirst.frc.team1885.robot.auto;

import org.usfirst.frc.team1885.robot.manipulator.UtilityArm;

import edu.wpi.first.wpilibj.DriverStation;

public class AutoUtilityArm extends AutoCommand {

    private static final double RESET_X = 0;
    private static final double RESET_Y = Math.sin(170) * UtilityArm.LENGTH_B;
    
    private UtilityArm uArm;
    private double xDistance, yDistance;
    
    public AutoUtilityArm(boolean b) {
        this(RESET_X,RESET_Y);
    }

    public AutoUtilityArm(double x, double y) {
        uArm = UtilityArm.getInstance();
        xDistance = x;
        yDistance = y;
    }

    @Override
    public boolean init() {
        uArm.goTo(xDistance, yDistance);
        return true;
    }

    @Override
    public boolean execute() {
        DriverStation.reportError("WOO", false);
        uArm.goTo(xDistance, yDistance);
        uArm.update();
        return uArm.isFinished();
    }

    @Override
    public boolean updateOutputs() {
        // Done for us in UtilityArm
        return false;
    }

    @Override
    public void reset() {
        uArm.resetPos();
    }

}
