import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import aeshliman.enumerators.Algorithm;
import aeshliman.simulation.Simulation;

public class SimulationAPI
{
	// Instance Variables
	private Simulation sim;
	
	// Constructors
	public SimulationAPI()
	{
		Algorithm algorithm = Algorithm.FCFS;
		int quantum = 5;
		int cpuCount = 1;
		int ioCount = 1;
		sim = new Simulation(algorithm,quantum,cpuCount,ioCount);
		loadScenario(new File("scenario.txt"));
	}
	
	// Operations
	public void run()
	{
		System.out.println(sim);
		while(sim.getTerminatedCount()<sim.getProcesses().size())
		{
			sim.tick();
			System.out.println(sim);
		}
	}
	
	public void saveLog()
	{
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
		{
			try(FileWriter fw = new FileWriter(fc.getSelectedFile(),false);) { fw.write(sim.getLog()); }
			catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	public void loadScenario(File file) { sim.loadScenario(file); }
}
