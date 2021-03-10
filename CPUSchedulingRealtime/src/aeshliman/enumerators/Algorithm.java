package aeshliman.enumerators;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import aeshliman.comparators.*;
import aeshliman.simulation.CustomProcess;

public enum Algorithm
{
	FCFS
	{
		public Queue<CustomProcess> getQueue() { return new LinkedList<CustomProcess>(); }
	},
	RR
	{
		public Queue<CustomProcess> getQueue() { return new LinkedList<CustomProcess>(); }
	},
	SJF
	{
		public Queue<CustomProcess> getQueue() { return new PriorityQueue<CustomProcess>(new SortByTime()); }
	},
	PS
	{
		public Queue<CustomProcess> getQueue() { return new PriorityQueue<CustomProcess>(new SortByPriority()); }
	};
	
	public abstract Queue<CustomProcess> getQueue();
}
