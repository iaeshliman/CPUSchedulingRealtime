package aeshliman.structure;

public class SimulationAPI
{
	// Instance Variables
	private Simulation sim;
	
	// Constructors
	public SimulationAPI()
	{
		this.sim = new Simulation();
	}
	
	// Getters and Setters
	public int getTime() { return sim.getTime(); }
	public String getThroughput() { return sim.calculateThroughput(); }
	
	// Operations
	public void loadScenario(String path)
	{
		sim.loadScenario(path);
	}
	
	public String cpuDetails()
	{
		return "CPU Details - Utilization " + sim.getCPU().calculateUtilization() + "  Active Process\n" + sim.getCPU().getProcess();
	}
	
	public String ioDetails()
	{
		return "IO Details - Utilization " + sim.getIO().calculateUtilization() + "  Active Process\n" + sim.getIO().getProcess();
	}
	
	public String cpuQueueDetails()
	{
		String toString = "CPU Queue Details";
		for(CustomProcess process : sim.getCPUScheduler().getQueue()) toString += "\n\t" + process;
		return toString;
	}
	
	public String ioQueueDetails()
	{
		String toString = "IO Queue Details";
		for(CustomProcess process : sim.getIOScheduler().getQueue()) toString += "\n\t" + process;
		return toString;
	}
	
	public String processDetails()
	{
		String result = "Processes Details";
		for(CustomProcess process : sim.getProcesses()) result += "\n" + process;
		return result;
	}
	
	public void tick()
	{
		sim.tick();
	}	
}
