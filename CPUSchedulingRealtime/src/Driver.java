import java.awt.EventQueue;

public class Driver
{
	public static void main(String[] args)
	{
		boolean gui = true;
		if(gui) // GUI mode
		{
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						Window window = new Window();
						window.window.setVisible(true);
					}
					catch(Exception e) { e.printStackTrace(); }
				}
			});
		}
		else // Console mode
		{
			// Console mode is mostly for debugging purposes and not entirely perfect
			SimulationAPI api = new SimulationAPI();
			api.run();
			api.saveLog();
		}
	}
}
