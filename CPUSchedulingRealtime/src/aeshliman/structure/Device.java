package aeshliman.structure;

import aeshliman.enumerators.DeviceType;
import aeshliman.enumerators.State;

public class Device
{
	// Instance Variables
	private static int count = 0;
	private int id;
	private Simulation sim;
	private Scheduler scheduler;
	private DeviceType type;
	private CustomProcess process;
	private int runningTime;
	private int upTime;
	
	// Constructors
	public Device(Simulation sim, DeviceType type)
	{
		this.id = count++;
		this.sim = sim;
		this.type = type;
		this.process = null;
		this.runningTime = 0;
		this.upTime = 0;
	}
	
	// Getters and Setters
	public int getID() { return this.id; }
	public Simulation getSim() { return this.sim; }
	public Scheduler getScheduler() { return this.scheduler; }
	public DeviceType getType() { return this.type; }
	public CustomProcess getProcess() { return this.process; }
	public int getRunningTime() { return this.runningTime; }
	public int getUpTime() { return this.upTime; }
	
	public void setProcess(CustomProcess process) { this.process = process; }
	public void setRunningTime(int runningTime) { this.runningTime = runningTime; }
	
	// Operations
	public void tick()
	{
		if(process!=null)
		{
			upTime++;
			if(process.tick()) // Checks if process finished a burst
			{
				if(!process.hasBurst()) // If process has no more bursts terminate it
				{ 
					process.setState(State.TERMINATED);
					process.setFinishTime(sim.getTime());
					process.calculateTurnaroundTime();
					sim.incrementTerminatedCount();
				}
				else
				{
					switch(type) // Places process into correct queue
					{
					case CPU:
						sim.getIOScheduler().add(process);
						process.setState(State.WAITING);
						break;
					case IO:
						sim.getCPUScheduler().add(process);
						process.setState(State.READY);
						break;
					}
				}
				process = null;
				runningTime = 0;
			}
			else runningTime++;
		}
	}
	
	public boolean isEmpty()
	{
		return process==null;
	}
	
	public void addProcess(CustomProcess newProcess)
	{
		process = newProcess;
		switch(type)
		{
		case CPU:
			if(!process.getBeenRunning()) process.triggerBeenRunning();
			process.setState(State.RUNNING);
			break;
		case IO:
			process.setState(State.WAITING);
			break;
		}
		runningTime = 0;
	}
	
	public void preempt(CustomProcess newProcess)
	{
		if(this.process!=null)
		{
			scheduler.getQueue().add(process);
			switch(type)
			{
			case CPU:
				process.setState(State.READY);
				break;
			case IO:
				process.setState(State.WAITING);
				break;
			}
		}
		addProcess(newProcess);
	}
	
	public String calculateUtilization()
	{
		if(sim.getTime()==0) return "N/A";
		return String.format("%.2f",(double)upTime/sim.getTime()*100) + "%";
	}
	
	// toString
}
