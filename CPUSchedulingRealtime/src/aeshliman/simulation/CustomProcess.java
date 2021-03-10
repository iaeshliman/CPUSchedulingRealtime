package aeshliman.simulation;

import java.util.LinkedList;
import java.util.Queue;

import aeshliman.enumerators.DeviceType;
import aeshliman.enumerators.State;

public class CustomProcess
{
	// Instance Variables
	private int pid;
	private String name;
	private Queue<Burst> remainingBursts;
	private Queue<Burst> finishedBursts;
	private State state;
	private int priority;
	private int arrivalTime;
	private int finishTime;
	private int turnaroundTime;
	private int responseTime;
	private int cpuWaitTime;
	private int ioWaitTime;
	
	private boolean hasRan;
	
	// Constructors
	public CustomProcess(int pid, String name, Queue<Burst> bursts, int priority, int arrivalTime)
	{
		this.pid = pid;
		this.name = name;
		this.remainingBursts = bursts;
		this.finishedBursts = new LinkedList<Burst>();
		this.state = State.NEW;
		this.priority = priority;
		this.arrivalTime = arrivalTime;
	}
	
	{
		this.finishTime = -1;
		this.turnaroundTime = 0;
		this.responseTime = 0;
		this.cpuWaitTime = 0;
		this.ioWaitTime = 0;	
		this.hasRan = false;
	}
	
	// Getters and Setters
	public int getPID() { return this.pid; }
	public int getPriority() { return this.priority; }
	public State getState() { return this.state; }
	public int getArrivalTime() { return this.arrivalTime; }
	public int getResponseTime() { return this.responseTime; }
	public int getFinishTime() { return this.finishTime; }
	public int getTurnaroundTime() { return this.turnaroundTime; }
	public int getCpuWaitTime() { return this.cpuWaitTime; }
	public int getIoWaitTime() { return this.ioWaitTime; }
	
	public void setState(State state) { this.state = state; }
	public void setFinishTime(int time) { this.finishTime = time; }
	public void setHasRan(boolean hasRan) { this.hasRan = hasRan; }
	
	// Operations
	public boolean tick(boolean cpuWait, boolean ioWait, boolean active) // Returns true if burst finished
	{
		// Increments values
		turnaroundTime++;
		if(!hasRan) responseTime++;
		if(cpuWait) cpuWaitTime++;
		if(ioWait) ioWaitTime++;
		if(active)
		{
			if(decrementBurst()<=0) // Checks if burst is complete
			{
				finishedBursts.add(remainingBursts.poll()); // Moves from remaining to finished queue
				return true;
			}
		}
		return false;
	}
	
	public String cpuBursts()
	{
		String toString = "";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.CPU) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public String ioBursts()
	{
		String toString = "";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.IO) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public int decrementBurst() { return remainingBursts.peek().decrement(); }
	public int peekTime() { return remainingBursts.peek().getRemainingDuration(); }
	public boolean isEmpty() { return remainingBursts.isEmpty(); }
	
	// toString
	public String toString()
	{
		String toString  = "P" + pid + " Arrival Time: " + arrivalTime + "  CPU Bursts: rem(";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.CPU&&burst!=remainingBursts.peek()) toString += burst.getTotalDuration() + " ";
		toString += ") - fin(";
		for(Burst burst : finishedBursts) if(burst.getType()==DeviceType.CPU) toString += burst.getTotalDuration() + " ";
		toString += ")   IO Bursts: rem(";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.IO&&burst!=remainingBursts.peek()) toString += burst.getTotalDuration() + " ";
		toString += ") - fin(";
		for(Burst burst : finishedBursts) if(burst.getType()==DeviceType.IO) toString += burst.getTotalDuration() + " ";
		toString += ")   Current Burst: ";
		Burst curBurst = remainingBursts.peek();
		if(curBurst!=null) { toString += curBurst.getType() + " " + curBurst.getRemainingDuration() + "/" + curBurst.getTotalDuration(); }
		toString += "   State: " + state;
		return toString;
	}
}
