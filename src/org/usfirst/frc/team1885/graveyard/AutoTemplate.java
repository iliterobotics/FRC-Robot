package org.usfirst.frc.team1885.graveyard;

import java.util.LinkedList;

import edu.wpi.first.wpilibj.Timer;

public class AutoTemplate extends AutoCommand{

	private LinkedList<AutoCommand> commands;
	
	public boolean init() {
		commands = new LinkedList<AutoCommand>();
		return false; 
	}

	public void add(AutoCommand command) {
		this.commands.add(command);
	}
	
	public boolean execute() {
		 AutoCommand currCommand = commands.peek();


         if(currCommand.isInit()) {
             boolean commandState = currCommand.execute();
             currCommand.updateOutputs();
             if(commandState) {
                 System.out.println("Finished command " + commands.size());
                 commands.poll();
             }
         } else {
             currCommand.setInit(currCommand.init());
         }

         Timer.delay(.005);
		return commands.isEmpty();
	}

	
	public boolean updateOutputs() {
	     
		return false;
	}

	
	public void reset() {
		commands.clear();
		RobotControl.getInstance().updateDriveSpeed(0, 0);
	}
	
	public static AutoTemplate automate2Totes() {
		AutoTemplate template = new AutoTemplate();
		template.init();
		template.reset();
		template.add(new AutoNudge());
		template.add(new AutoToteLift(1 * 1210, 10));
		template.add(new AutoNudge());
		template.add(new AutoToteLift(1 * 1210, 10));
//		template.add(new AutoDriveForward(3 * -12, 1, 2));
		
		return template;
	}
	
	public static AutoTemplate autoFourTote() {
		AutoTemplate template = new AutoTemplate();
		template.init();
		template.reset();
		template.add(new AutoNudge());
		template.add(new AutoToteLift(1 * 1210, 10));
		template.add(new AutoNudge());
		template.add(new AutoToteLift(1 * 1210, 10));
		template.add(new AutoDriveForward(-2 * 12, 1, 2));
		template.add(new AutoTurn(-90, 5));
		template.add(new AutoDriveForward(3 * 12, 1, 2));
		template.add(new AutoTurn(90, 5));
		template.add(new AutoNudge());
		template.add(new AutoToteLift(1 * 1210, 10));
		template.add(new AutoDriveForward(-2.5 * 12, 1, 2));
		template.add(new AutoTurn (90, 5));
		template.add(new AutoDriveForward(3 * 12, 1, 2));
	    template.add(new AutoTurn(-90, 5));
	    template.add(new AutoNudge());
	    template.add(new AutoToteLift(1 * 1210, 10));
	    return template;
	}

}