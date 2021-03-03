import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

public class Driver
{
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Window window = new Window(720,405);
					window.window.setVisible(true);
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask()
					{
					    public void run()
					    {
					    	// TODO: Call API methods to update simulation
					    	if(window.isRunning()||window.willStep())
					    	{
					    		window.tick();
					    	   	window.toggleStep();
					    	}
					    }
					},1000/5,1000/5);
					
				}
				catch(Exception e) { e.printStackTrace(); }
			}
		});
	}
}
