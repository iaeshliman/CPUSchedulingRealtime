package aeshliman.simulation;

import java.util.Queue;

import aeshliman.enumerators.State;

public class CustomProcess
{
	// Instance Variables
	private int pid;
	private String name;
	private Queue<Burst> bursts;
	private State state;
	private int priority;
	private int arrivalTime;
	private int finishTime;
	private int turnaroundTime;
	private int responseTime;
	private int cpuWaitTime;
	private int ioWaitTime;
	private boolean beenRunning;
	private boolean inIO;
	
	// Constructors
	public CustomProcess(int pid, String name, Queue<Burst> bursts, int priority, int arrivalTime)
	{
		this.pid = pid;
		this.name = name;
		this.bursts = bursts;
		this.state = State.NEW;
		this.priority = priority;
		this.arrivalTime = arrivalTime;
		this.finishTime = 0;
		this.turnaroundTime = 0;
		this.responseTime = 0;
		this.cpuWaitTime = 0;
		this.ioWaitTime = 0;
		this.beenRunning = false;
		this.inIO = false;
	}
	
	// Getters and Setters
	public int getPID() { return this.pid; }
	public String getName() { return this.name; }
	public Queue<Burst> getBurts() { return this.bursts; }
	public State getState() { return this.state; }
	public int getPriority() { return this.priority; }
	public int getArrivalTime() { return this.arrivalTime; }
	public int getFinishTime() { return this.finishTime; }
	public int getTurnaroundTime() { return this.turnaroundTime; }
	public int getCpuWaitTime() { return this.cpuWaitTime; }
	public int getIoWaitTime() { return this.ioWaitTime; }
	public boolean getBeenRunning() { return this.beenRunning; }
	public boolean isInIO() { return this.inIO; }
	
	public void setState(State state) { this.state = state; }
	public void setFinishTime(int finishTime) { this.finishTime = finishTime; }
	public void triggerBeenRunning() { this.beenRunning = true; }
	public void setInIO(boolean inIO) { this.inIO = inIO; }
	
	// Operations
	public boolean tick() // Returns true if burst finished
	{
		// Checks if burst is complete
		decrementBurst();
		if(peekTime()<=0)
		{
			bursts.poll(); // Removes burst from burst queue
			return true;
		}
		return false;
	}
	
	public int peekTime()
	{
		return bursts.peek().getDuration();
	}
	
	public int decrementBurst()
	{
		return bursts.peek().decrement();
	}
	
	public boolean hasBurst()
	{
		return !bursts.isEmpty();
	}
	
	public void calculateTurnaroundTime()
	{
		turnaroundTime = finishTime - arrivalTime;
	}
	
	public void incrementCPUWaitTime()
	{
		cpuWaitTime++;
	}
	
	public void incrementIOWaitTime()
	{
		ioWaitTime++;
	}
	
	public void incrementResponseTime()
	{
		responseTime++;
	}
	
	// toString
	public String toString()
	{
		String toString = "Process " + pid + " - Name: " + name + "  Priority: " + priority + "  State: " + state
				+ "\nArrival Time: " + arrivalTime + "  Finish Time: " + finishTime + "  Turnaround Time: " + turnaroundTime
				+ "  Response Time: " + responseTime + "  CPU Wait Time: " + cpuWaitTime + "  IO Wait Time: " + ioWaitTime;
		for(Burst burst : bursts) toString += "\n\t" + burst;
		return toString;
	}
}
