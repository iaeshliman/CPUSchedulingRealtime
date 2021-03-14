import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import aeshliman.enumerators.Algorithm;
import aeshliman.simulation.CustomProcess;
import aeshliman.simulation.Simulation;

/*
 * Class was used mostly for testing purposes in console mode
 */

public class SimulationAPI
{
	// Instance Variables
	private Simulation sim;
	
	// Constructors
	public SimulationAPI()
	{
		Algorithm algorithm;
		int quantum;
		int cpuCount;
		int ioCount;
		while(true)
		{
			try
			{
				algorithm = (Algorithm) JOptionPane.showInputDialog(null,"Select algorithm",null,JOptionPane.QUESTION_MESSAGE,null,Algorithm.values(),Algorithm.values()[0]);
				quantum = Integer.parseInt(JOptionPane.showInputDialog("Please enter quantum time"));
				cpuCount = Integer.parseInt(JOptionPane.showInputDialog("Please enter cpu count"));
				ioCount = Integer.parseInt(JOptionPane.showInputDialog("Please enter io count"));
				break;
			}
			catch(Exception e) { JOptionPane.showMessageDialog(null, "Invalid Entry");}
		}
		sim = new Simulation(algorithm,quantum,cpuCount,ioCount);
		loadScenario();
	}
	
	// Operations
	public void run()
	{
		boolean manual = false;
		if(JOptionPane.showConfirmDialog(null, "Run in manual mode?",null,JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) manual = true;
		System.out.println(sim);
		while(sim.getTerminatedCount()<sim.getProcesses().size())
		{
			if(manual) if(JOptionPane.showConfirmDialog(null,"Continue?",null,JOptionPane.OK_OPTION)!=JOptionPane.YES_OPTION) break;
			sim.tick();
			System.out.println(sim);
		}
	}
	
	public void saveLog()
	{
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		if(fc.showDialog(null,"Save Log")==JFileChooser.APPROVE_OPTION)
		{
			try(FileWriter fw = new FileWriter(fc.getSelectedFile(),false);) { fw.write(sim.getLog()); }
			catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	public void loadScenario()
	{
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		if(fc.showDialog(null,"Load Scenario")==JFileChooser.APPROVE_OPTION) { sim.loadScenario(fc.getSelectedFile()); }
		else sim.appendLog("Failed to load scenario file");
	}
	
	public Queue<CustomProcess> getCPUQueue() { return sim.getCPUScheduler().getQueue(); }
	public Queue<CustomProcess> getIOQueue() { return sim.getIOScheduler().getQueue(); }
}
