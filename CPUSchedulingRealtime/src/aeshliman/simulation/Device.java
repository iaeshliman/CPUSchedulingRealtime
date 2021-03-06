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
		activeTime = 0;
		runningTime = 0;
	}
	
	// Getters and Setters
	public int getID() { return this.id; }
	public CustomProcess getProcess() { return this.process; }
	public int getRunningTime() { return this.runningTime; }
	
	// Operations
	public void tick()
	{
		if(process!=null)
		{
			activeTime++;
			runningTime++;
			if(process.peekTime()==0) // Checks if the active process has finished its current burst
			{
				process.finishBurst();
				if(process.isEmpty()) // Checks if active process has anymore bursts
				{
					process.setState(State.TERMINATED);
					sim.incrementTerminatedCount();
					sim.appendLog("Process " + process.getPID() + " terminated at " + sim.getTime());
				}
				else
				{
					switch(type) // If process has more bursts add it to the correct queue
					{
					case CPU:
						sim.appendLog("Process " + process.getPID() + " added to CPU " + id + " at " + sim.getTime());
						sim.getIOScheduler().add(process);
						break;
					case IO:
						sim.appendLog("Process " + process.getPID() + " added to IO " + id + " at " + sim.getTime());
						sim.getCPUScheduler().add(process);
						break;
					}
				}
				process.setInIO(false);
				process = null;
				runningTime = 0;
			}
		}
	}
	
	public void addProcess(CustomProcess newProcess)
	{
		process = newProcess;
		if(process!=null)
		{
			switch(type) // Updates processes state depending on device type
			{
			case CPU:
				sim.appendLog("Process " + process.getPID() + " added to CPU " + id + " at " + sim.getTime());
				process.setState(State.RUNNING);
				process.setHasRan(true);
				break;
			case IO:
				sim.appendLog("Process " + process.getPID() + " added to IO " + id + " at " + sim.getTime());
				process.setState(State.WAITING);
				process.setInIO(true);
				break;
			}
		}
		runningTime = 0;
	}
	
	public void preempt()
	{
		if(this.process!=null) // Places the current process back into queue to be finished later
		{
			process.setInIO(false);
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
			process = null;
		}
	}
	
	public double calcUtilization()
	{
		if(sim.getTime()==0) return 0;
		return (double)activeTime/sim.getTime();
	}
	
	public boolean isEmpty() { return this.process==null; }
	
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
