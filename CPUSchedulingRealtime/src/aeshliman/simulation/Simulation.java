package aeshliman.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private String log;
	
	// Constructors
	public Simulation(Algorithm algorithm, int quantum, int cpuCount, int ioCount)
	{
		this.cpuScheduler = new Scheduler(this,DeviceType.CPU,algorithm,quantum,cpuCount);
		this.ioScheduler = new Scheduler(this,DeviceType.IO,Algorithm.FCFS,quantum,ioCount);
		this.newProcesses = new HashMap<Integer,Queue<CustomProcess>>();
	}
	
	// Instance Initializer Block
	{
		time = 0;
		processCount = 0;
		terminatedCount = 0;
		processes = new LinkedList<CustomProcess>();
		log = "==================================================\n";
		log += "\tLog Created - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
		log += "\n==================================================\n";
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
	public int getTerminatedCount() { return terminatedCount; }
	public String getLog() { return this.log; }
	
	// Operations
	public void tick()
	{
		time++;
		tickProcesses();
		cpuScheduler.tickDevices();
		ioScheduler.tickDevices();
		addNewProcesses();
		cpuScheduler.tickQueue();
		ioScheduler.tickQueue();
	}
	
	public void tickProcesses() { for(CustomProcess process : processes) { process.tick(); } }
	
	private void addNewProcesses()
	{
		// Adds processes to ready queue at the correct arrival times
		Queue<CustomProcess> newProcessQueue = newProcesses.get(time);
		if(newProcessQueue!=null)
		{
			for(CustomProcess process : newProcessQueue)
			{
				appendLog("Process " + process.getPID() + " is created at " + time);
				cpuScheduler.add(process);
			}
		}
	}
	
	public boolean loadScenario(File file)
	{
		if(file==null) return false;
		try(Scanner scan = new Scanner(file);)
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
			appendLog("Loaded scenario file " + file.getName());
			addNewProcesses();
			cpuScheduler.initialize();
		}
		catch(FileNotFoundException e) { return false; }
		return true;
	}
	
	public double calcThroughput()
	{
		if(time==0) return 0;
		return (double)terminatedCount/time;
	}
	
	public double calcAvgTurnaround()
	{
		if(time==0) return 0;
		else
		{
			double sum = 0;
			for(CustomProcess process : processes) sum += process.getTurnaroundTime();
			return sum/processes.size();
		}
	}
	
	public double calcAvgCPUWait()
	{
		if(time==0) return 0;
		else
		{
			double sum = 0;
			for(CustomProcess process : processes) sum += process.getCpuWaitTime();
			return sum/processes.size();
		}
	}
	
	public double calcAvgIOWait()
	{
		if(time==0) return 0;
		else
		{
			double sum = 0;
			for(CustomProcess process : processes) sum += process.getIoWaitTime();
			return sum/processes.size();
		}
	}
	
	public void incrementTerminatedCount() { terminatedCount++; }
	public void appendLog(String line) { log += line + "\n"; }
	
	// toString
	public String toString()
	{
		String toString = "System Time: " + time + ", Throughput: " + String.format("%.2f", calcThroughput()) + ", Avg Wait: "
				+ String.format("%.2f", calcAvgCPUWait()) + ", Avg IO Wait: " + String.format("%.2f", calcAvgIOWait()) + "\n";
		toString += cpuScheduler.getDevice() + "\n";
		toString += cpuScheduler + "\n";
		toString += ioScheduler.getDevice() + "\n";
		toString += ioScheduler + "\n";
		toString += String.format("%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s%-13s",
				"PID","Name","Priority","Current","CPU Bursts","Finished","IO Bursts","Finished","Arrival","Start","Finish","CPU Wait","IO Wait","Turnaround","State") + "\n";
		for(CustomProcess process : processes) toString += process + "\n";
		toString += "-------------------------";
		return toString;
	}
}
