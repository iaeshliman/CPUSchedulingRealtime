package aeshliman.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import aeshliman.enumerators.Algorithm;
import aeshliman.enumerators.DeviceType;

public class Simulation
{
	// Instance Variables
	private int time;
	private int quantum;
	private int terminatedCount;
	private LinkedList<CustomProcess> processes;
	private Scheduler cpuScheduler;
	private Scheduler ioScheduler;
	private HashMap<Integer,Queue<CustomProcess>> newProcesses; 
	
	// Constructors
	public Simulation()
	{
		this.time = 0;
		this.quantum = 5;
		this.terminatedCount = 0;
		this.processes = new LinkedList<CustomProcess>();
		this.cpuScheduler = new Scheduler(this,DeviceType.CPU,Algorithm.FCFS,quantum);
		this.ioScheduler = new Scheduler(this,DeviceType.IO,Algorithm.FCFS,quantum);
		this.newProcesses = new HashMap<Integer,Queue<CustomProcess>>();
	}
	
	// Getters and Setters
	public int getTime() { return this.time; }
	public LinkedList<CustomProcess> getProcesses() { return this.processes; }
	public Scheduler getCPUScheduler() { return this.cpuScheduler; }
	public Scheduler getIOScheduler() { return this.ioScheduler; }
	public Device getCPU() { return this.cpuScheduler.getDevice(); }
	public Device getIO() { return this.ioScheduler.getDevice(); }
	public Queue<CustomProcess> getCPUQueue() { return this.cpuScheduler.getQueue(); }
	public Queue<CustomProcess> getIOQueue() { return this.ioScheduler.getQueue(); }
	
	// Operations
	public void tick()
	{
		time++;
		// Increments the wait times of all processes currently in a queue
		cpuScheduler.incrementWaitTimes();
		ioScheduler.incrementWaitTimes();
		// Adds processes to ready queue at the correct arrival times
		Queue<CustomProcess> newProcessQueue = newProcesses.get(time);
		if(newProcessQueue!=null) for(CustomProcess process : newProcessQueue) cpuScheduler.add(process);
		// Updates simulation one time unit
		cpuScheduler.getDevice().tick();
		ioScheduler.getDevice().tick();
		cpuScheduler.tick();
		ioScheduler.tick();
	}
	
	public String calculateThroughput()
	{
		if(time==0) return "0 processes per time unit";
		return String.format("%.4f", (double)terminatedCount/time) + " processes per time unit";
	}
	
	public void incrementTerminatedCount() { terminatedCount++; }
	
	public void loadScenario(String path)
	{
		try(Scanner scan = new Scanner(new File(path));)
		{
			while(scan.hasNext())
			{
				Queue<Burst> bursts = new LinkedList<Burst>();
				String[] line = scan.nextLine().split(" ");
				for(int i=3; i<line.length; i++)
				{
					if(i%2==0) bursts.add(new Burst(Integer.parseInt(line[i]),DeviceType.IO));
					else bursts.add(new Burst(Integer.parseInt(line[i]),DeviceType.CPU));
				}
				CustomProcess process = new CustomProcess(line[0],bursts,Integer.parseInt(line[2]),Integer.parseInt(line[1]));
				newProcesses.putIfAbsent(Integer.parseInt(line[1]), new LinkedList<CustomProcess>());
				processes.add(process);
				newProcesses.get(Integer.parseInt(line[1])).add(process);
			}
		}
		catch(FileNotFoundException e) { e.printStackTrace(); }
	}
}
