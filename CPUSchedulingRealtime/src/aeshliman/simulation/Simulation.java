package aeshliman.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import aeshliman.enumerators.DeviceType;
import aeshliman.enumerators.Algorithm;

public class Simulation
{
	// Instance Variables
	private int time;
	private int terminatedCount;
	private int processCount;
	private LinkedList<CustomProcess> processes;
	private Scheduler cpuScheduler;
	private Scheduler ioScheduler;
	private HashMap<Integer,Queue<CustomProcess>> newProcesses;
	
	// Constructors
	public Simulation(Algorithm algorithm, int quantum, int cpuCount, int ioCount)
	{
		this.cpuScheduler = new Scheduler(this,DeviceType.CPU,algorithm,quantum);
		this.ioScheduler = new Scheduler(this,DeviceType.IO,Algorithm.FCFS,quantum);
		this.newProcesses = new HashMap<Integer,Queue<CustomProcess>>();
	}
	
	// Instance Initializer Block
	{
		time = 0;
		processCount = 0;
		terminatedCount = 0;
		processes = new LinkedList<CustomProcess>();
	}
	
	// Getters and Setters
	public int getTime() { return time; }
	public LinkedList<CustomProcess> getProcesses() { return this.processes; }
	public Scheduler getCPUScheduler() { return this.cpuScheduler; }
	public Scheduler getIOScheduler() { return this.ioScheduler; }
	public LinkedList<Device> getCPU() { return cpuScheduler.getDevice(); }
	public LinkedList<Device> getIO() { return ioScheduler.getDevice(); }
	public Queue<CustomProcess> getReadyQueue() { return cpuScheduler.getQueue(); }
	public Queue<CustomProcess> getWaitingQueue() { return ioScheduler.getQueue(); }
	
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
		cpuScheduler.tickDevice();
		ioScheduler.tickDevice();
		cpuScheduler.tick();
		ioScheduler.tick();
	}
	
	public boolean loadScenario(String path)
	{
		if(path==null) return false;
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
				CustomProcess process = new CustomProcess(processCount++,line[0],bursts,Integer.parseInt(line[2]),Integer.parseInt(line[1]));
				newProcesses.putIfAbsent(Integer.parseInt(line[1]), new LinkedList<CustomProcess>());
				processes.add(process);
				newProcesses.get(Integer.parseInt(line[1])).add(process);
			}
		}
		catch(FileNotFoundException e) { return false; }
		
		return true;
	}
	
	public boolean validScenario(String path)
	{
		if(path==null) return false;
		File file = new File(path);
		return file.isFile();
	}
	
	public double calcAvgTurnaround()
	{
		return 0;
	}
	
	public double calcAvgWait()
	{
		return 0;
	}
	
	public double calcThroughput()
	{
		if(time==0) return 0;
		return (double)terminatedCount/time;
	}
	
	public void incrementTerminatedCount() { terminatedCount++; }
	
	public CustomProcess findProcess(int pid)
	{
		for(CustomProcess process : processes) { if(process.getPID()==pid) return process; }
		return null;
	}
	
}
