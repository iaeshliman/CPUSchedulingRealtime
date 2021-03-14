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
				public void run() // TODO: Fix the gui mode as its being a pain
				{
					try
					{
						Window window = new Window();
						window.window.setVisible(true);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
		else // Console mode
		{
			SimulationAPI api = new SimulationAPI(true);
			api.run();
			api.saveLog();
		}
	}
}
