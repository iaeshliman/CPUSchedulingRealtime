package aeshliman.simulation;

import aeshliman.enumerators.DeviceType;
import aeshliman.enumerators.State;

public class Device
{
	// Instance Variables
	private int id;
	private DeviceType type;
	private CustomProcess process;
	private Simulation sim;
	
	private int totalTime;
	private int activeTime;
	private int runningTime;
	
	// Constructors
	public Device(int id, DeviceType type, Simulation sim)
	{
		this.id = id;
		this.type = type;
		this.process = null;
		this.sim = sim;
	}
	
	{
		totalTime = 0;
		activeTime = 0;
		runningTime = 0;
	}
	
	// Getters and Setters
	public int getID() { return this.id; }
	public CustomProcess getProcess() { return this.process; }
	public int getRunningTime() { return this.runningTime; }
	
	// Operations
	public boolean tick()
	{
		totalTime++;
		if(process!=null) // Check device is not idle
		{
			activeTime++;
			runningTime++;
			if(process.tick(false, false, true)) // Check if process finished burst
			{
				if(process.isEmpty()) // Check if process finished all bursts
				{
					process.setState(State.TERMINATED);
					process.setFinishTime(totalTime);
					sim.incrementTerminatedCount();
					sim.appendLog("Process " + process.getPID() + " terminated at " + totalTime);
				}
				else
				{
					switch(type) // If process has more bursts add it to the correct queue
					{
					case CPU:
						sim.appendLog("Process " + process.getPID() + " added to CPU at " + totalTime);
						sim.getIOScheduler().add(process);
						break;
					case IO:
						sim.appendLog("Process " + process.getPID() + " added to IO at " + totalTime);
						sim.getCPUScheduler().add(process);
						break;
					}
				}
				runningTime = 0;
				process = null;
			}
		}
		return process==null; // Return whether device has an active process
	}
	
	public void addProcess(CustomProcess newProcess)
	{
		process = newProcess;
		if(process!=null)
		{
			switch(type) // Updates processes state depending on device type
			{
			case CPU:
				sim.appendLog("Process " + process.getPID() + " added to CPU at " + totalTime);
				process.setState(State.RUNNING);
				process.setHasRan(true);
				break;
			case IO:
				sim.appendLog("Process " + process.getPID() + " added to IO at " + totalTime);
				process.setState(State.WAITING);
				break;
			}
		}
		runningTime = 0;
	}
	
	public void preempt()
	{
		if(this.process!=null)
		{
			switch(type)
			{
			case CPU:
				sim.getCPUScheduler().add(process);
				process.setState(State.READY);
				break;
			case IO:
				sim.getIOScheduler().add(process);
				process.setState(State.WAITING);
				break;
			}
		}
	}
	
	public double calcUtilization()
	{
		if(totalTime==0) return 0;
		return (double)activeTime/totalTime;
	}
	
	// toString
	public String toString()
	{
		String toString = type + " " + id + ": ";
		if(process==null) toString += "idle";
		else toString += "P" + process.getPID();
		toString += ", Utilization: " + String.format("%.2f", calcUtilization()*100) + "%";
		return toString;
	}
}
