package aeshliman.enumerators;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import aeshliman.structure.CustomProcess;
import aeshliman.structure.Device;

public enum Algorithm
{
	FCFS
	{
		public Queue<CustomProcess> getQueue() { return new LinkedList<CustomProcess>(); }
		public boolean preempt(Device device, int quantum) { return false; }
	},
	RR
	{
		public Queue<CustomProcess> getQueue() { return new LinkedList<CustomProcess>(); }
		public boolean preempt(Device device, int quantum) { return device.getRunningTime()>=quantum; }
	},
	SJF
	{
		public Queue<CustomProcess> getQueue() { return new PriorityQueue<CustomProcess>(); }
		public boolean preempt(Device device, int quantum) { return false; }
	},
	PS
	{
		public Queue<CustomProcess> getQueue() { return new PriorityQueue<CustomProcess>(); }
		public boolean preempt(Device device, int quantum) { return device.getScheduler().getQueue().peek().getPriority()<device.getProcess().getPriority(); }
	};
	
	public abstract Queue<CustomProcess> getQueue();
	public abstract boolean preempt(Device device, int quantum);
}
