package org.usfirst.frc.team1885.robot.auto;

import org.usfirst.frc.team1885.robot.modules.ActiveIntake;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class AutoAdjustIntake extends AutoCommand{

    DoubleSolenoid.Value intakeState;
    
    public AutoAdjustIntake(DoubleSolenoid.Value intakeState){
        this.intakeState = intakeState;
    }
    
    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean execute() {
        ActiveIntake.getInstance().setIntakeSolenoid(this.intakeState);
        ActiveIntake.getInstance().updateOutputs();
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean updateOutputs() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }

}