package org.usfirst.frc.team1885.robot.modules.lift;

import org.usfirst.frc.team1885.robot.modules.Module;
import org.usfirst.frc.team1885.robot.output.RobotControl;

public class CanGrabber implements Module {

    private static CanGrabber instance;
    private boolean grabberState;

    protected CanGrabber(){
        grabberState = false;
    }
    public static CanGrabber getInstance() {
        if (instance == null) {
            instance = new CanGrabber();
        }
        return instance;
    }

    @Override
    public void update() {
        
    }
    
    public void update( boolean state ) {
        grabberState = state;
    }

    @Override
    public void updateOutputs() {
        RobotControl.getInstance().updateGrabberPneumatics( grabberState );
    }

}
