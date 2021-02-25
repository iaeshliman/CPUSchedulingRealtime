import javax.swing.SwingUtilities;

import aeshliman.gui.GUI;
import aeshliman.structure.SimulationAPI;

public class Driver
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			private final SimulationAPI API = new SimulationAPI();
			private final String PATH = "CPUSchedulingRealtime/scenario.dat";
			private final GUI window = new GUI("CPU Scheduling Simulation",500,500,API,5,PATH);
			
			public void run()
			{
				window.setVisible(true);
			}
        });
	}
}
