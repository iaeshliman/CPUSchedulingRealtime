package aeshliman.structure;

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
	private Device device;
	private Algorithm algorithm;
	private Queue<CustomProcess> queue;
	private int quantum;
	
	// Constructors
	public Scheduler(Simulation sim, DeviceType type, Algorithm algorithm, int quantum)
	{
		this.sim = sim;
		this.type = type;
		this.device = new Device(sim,type);
		this.algorithm = algorithm;
		this.queue = new LinkedList<CustomProcess>();
		this.quantum = quantum;
	}
	
	// Getters and Setters
	public DeviceType getType() { return this.type; }
	public Device getDevice() { return this.device; }
	public Algorithm getAlgorithm() { return this.algorithm; }
	public Queue<CustomProcess> getQueue() { return this.queue; }
	public int getQuantum() { return this.quantum; }
	
	// Operations
	public void tick()
	{
		if(device.isEmpty()) { if(!queue.isEmpty()) device.addProcess(queue.poll()); }
		else if(algorithm.preempt(device,quantum)) { if(!queue.isEmpty()) device.preempt(queue.poll()); }
	}
	
	public void next()
	{
		device.setProcess(queue.poll());
	}
	
	public void add(CustomProcess process)
	{
		queue.add(process);
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
	
	public void incrementWaitTimes()
	{
		for(CustomProcess process : queue)
		{
			switch(type)
			{
			case CPU:
				process.incrementCPUWaitTime();
				break;
			case IO:
				process.incrementIOWaitTime();
				break;
			}
		}
	}
}
