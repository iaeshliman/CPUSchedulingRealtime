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
	private boolean inIO;
	
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
		this.finishTime = 0;
		this.turnaroundTime = 0;
		this.responseTime = 0;
		this.cpuWaitTime = 0;
		this.ioWaitTime = 0;	
		this.hasRan = false;
		this.inIO = false;
	}
	
	// Getters and Setters
	public int getPID() { return this.pid; }
	public String getName() { return this.name; }
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
	public void setInIO(boolean inIO) { this.inIO = inIO; }
	
	// Operations
	public void tick() // Returns true if burst finished
	{
		switch(state) // Increments the appropriate stats
		{
		case NEW:
			finishTime++;
			break;
		case READY:
			if(!hasRan) responseTime++;
			cpuWaitTime++;
			turnaroundTime++;
			finishTime++;
			break;
		case RUNNING:
			tickBurst();
			turnaroundTime++;
			finishTime++;
			break;
		case WAITING:
			if(inIO)
			{
				tickBurst();
				turnaroundTime++;
				finishTime++;
			}
			else
			{
				ioWaitTime++;
				turnaroundTime++;
				finishTime++;
			}
			break;
		case TERMINATED:
			break;
		}
	}
	
	public void tickBurst() { if(remainingBursts.peek()!=null) { remainingBursts.peek().decrement(); } }
	public void finishBurst() { finishedBursts.add(remainingBursts.poll()); }
	
	public boolean isEmpty() { return remainingBursts.isEmpty(); }
	public int peekTime() { return remainingBursts.peek().getRemainingDuration(); }
	
	// toString
	public String cpuRemainingBursts()
	{
		String toString = "";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.CPU) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public String cpuFinishedBursts()
	{
		String toString = "";
		for(Burst burst : finishedBursts) if(burst.getType()==DeviceType.CPU) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public String ioRemainingBursts()
	{
		String toString = "";
		for(Burst burst : remainingBursts) if(burst.getType()==DeviceType.IO) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public String ioFinishedBursts()
	{
		String toString = "";
		for(Burst burst : finishedBursts) if(burst.getType()==DeviceType.IO) toString+= burst.getTotalDuration() + " ";
		return toString.trim();
	}
	
	public String currentBurst()
	{
		Burst burst = remainingBursts.peek();
		if(burst==null) return "";
		return burst.getRemainingDuration() + "/" + burst.getTotalDuration();
	}
	
	public String toString()
	{
		return String.format("%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s","P" + pid,name,priority,currentBurst(),
				"(" + cpuRemainingBursts() + ")","(" + cpuFinishedBursts() + ")","(" + ioRemainingBursts() + ")","(" + ioFinishedBursts() +")",
				arrivalTime,(responseTime+arrivalTime),finishTime,cpuWaitTime,ioWaitTime,turnaroundTime,state);
	}
}
