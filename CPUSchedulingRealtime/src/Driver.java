import javax.swing.SwingUtilities;

import aeshliman.gui.GUI;
import aeshliman.structure.SimulationAPI;

public class Driver
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			private final SimulationAPI api = new SimulationAPI();
			private final GUI window = new GUI("CPU Scheduling Simulation",500,500,api,5);
			
			public void run()
			{
				window.setVisible(true);
			}
        });
	}
}