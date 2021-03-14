package aeshliman.simulation;

import java.util.LinkedList;
import java.util.Queue;

import aeshliman.enumerators.Algorithm;
import aeshliman.enumerators.DeviceType;
import aeshliman.enumerators.State;

public class Scheduler
{
	// Instance Variables
	private Simulation sim;
	private DeviceType type;
	private Algorithm algorithm;
	private LinkedList<Device> devices;
	private Queue<CustomProcess> queue;
	private int quantum;
	
	// Constructors
	public Scheduler(Simulation sim, DeviceType type, Algorithm algorithm, int quantum, int deviceCount)
	{
		this.sim = sim;
		this.type = type;
		this.algorithm = algorithm;
		this.devices = new LinkedList<Device>();
		for(int i=0; i<deviceCount; i++) { this.devices.add(new Device(i,type,sim)); }
		this.queue = algorithm.getQueue();
		this.quantum = quantum;
	}
	
	// Getters and Setters
	public LinkedList<Device> getDevice() { return this.devices; }
	public Queue<CustomProcess> getQueue() { return this.queue; }
	
	// Operations
	public void tickDevices() { for(Device device : devices) device.tick(); }
	public void tickQueue()
	{
		for(Device device : devices)
		{
			if(device.isEmpty()) device.addProcess(queue.poll());
			else // Checks for the need to preempt a process
			{
				if(algorithm==Algorithm.RR)
				{
					if(device.getRunningTime()>=quantum)
					{ 
						device.preempt();
						device.addProcess(queue.poll());
					}
				}
				else if(algorithm==Algorithm.PS)
				{
					if(!queue.isEmpty()&&device.getProcess().getPriority()>queue.peek().getPriority())
					{
						device.preempt();
						device.addProcess(queue.poll());
					}	
				}
			}
		}
	}
	
	public void add(CustomProcess process)
	{
		if(process!=null)
		{
			queue.add(process);
			switch(type) // Sets state and adds to log
			{
			case CPU:
				sim.appendLog("Process " + process.getPID() + " added to CPU queue at " + sim.getTime());
				process.setState(State.READY);
				break;
			case IO:
				sim.appendLog("Process " + process.getPID() + " added to IO queue at " + sim.getTime());
				process.setState(State.WAITING);
				break;
			}
		}
	}
	
	public void initialize() { for(Device device : devices) if(device.isEmpty()) device.addProcess(queue.poll()); }
	
	// toString
	public String toString()
	{
		String toString = "";
		switch(type)
		{
		case CPU:
			toString += "Ready Queue: ";
			break;
		case IO:
			toString += "Waiting Queue: ";
			break;
		}
		for(CustomProcess process : queue) toString += "P" + process.getPID() + " ";
		return toString;
	}
}
