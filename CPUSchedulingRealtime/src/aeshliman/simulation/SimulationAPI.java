package aeshliman.simulation;

import java.util.LinkedList;
import java.util.Queue;

import aeshliman.enumerators.Algorithm;

public class SimulationAPI
{
	// Instance Variables
	private Simulation sim;
	private final String timeUnit = "ms";
	
	// Constructors
	public SimulationAPI(Algorithm algorithm, int quantum, int cpuCount, int ioCount)
	{
		this.sim = new Simulation(algorithm, quantum, cpuCount, ioCount);
	}
	
	public LinkedList<CustomProcess> getProcesses() { return sim.getProcesses(); }
	public int getTime() { return sim.getTime(); }
	public String getTimeUnit() { return timeUnit; }
	public double calcAvgTurnaround() { return sim.calcAvgTurnaround(); }
	public double calcAvgWait() { return sim.calcAvgWait(); }
	public double calcThroughput() { return sim.calcThroughput(); }
	
	// Operations
	public void tick()
	{
		sim.tick();
	}
	
	public boolean loadScenario(String path)
	{
		return sim.loadScenario(path);
	}
	
	public boolean validScenario(String path)
	{
		return sim.validScenario(path);
	}
	
	public CustomProcess findProcess(int pid)
	{
		return sim.findProcess(pid);
	}
	
	public int[] getCPUIDs()
	{
		return null;
	}
	
	public int[] getIOIDs()
	{
		return null;
	}
	
	public int getCPUCount() { return 1; }
	public int getIOCount() { return 1; }
	
	public LinkedList<Device> getCPU() { return sim.getCPU(); }
	public LinkedList<Device> getIO() { return sim.getIO(); }
	
	public Queue<CustomProcess> getReadyQueue() { return sim.getReadyQueue(); }
	public Queue<CustomProcess> getWaitingQueue() { return sim.getWaitingQueue(); }
}
