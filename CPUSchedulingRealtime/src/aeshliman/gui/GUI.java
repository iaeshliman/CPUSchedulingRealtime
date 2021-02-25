package aeshliman.gui;

import java.awt.*;
import javax.swing.*;
import java.util.TimerTask;
import java.util.Timer;
import java.awt.event.*;

import aeshliman.structure.SimulationAPI;

public class GUI extends JFrame
{
	private static final long serialVersionUID = -5862526100090927093L;

	// Instance Variables
	private SimulationAPI api;
	private int fps;
	private Container controlsContainer;
	private Container cpuContainer;
	private Container ioContainer;
	private Container processContainer;
	private JLabel label;
	private JButton startButton;
	private JButton stepButton;
	private JTextArea cpuDisplay;
	private JTextArea ioDisplay;
	private JTextArea cpuQueueDisplay;
	private JTextArea ioQueueDisplay;
	private JTextArea processesDisplay;
	private boolean running = false;
	private boolean step = false;
	
	// Constructors
	public GUI(String title,int w, int h, SimulationAPI api, int fps)
	{
		// Initialize instance variables
		super(title); // Specifies the title of the window
		this.api = api;
		this.fps = fps;
		
		// Loads simulation scenario
		this.api.loadScenario("scenario.dat");
		
		// Initialize GUI components
		this.controlsContainer = new Container();
		this.cpuContainer = new Container();
		this.ioContainer = new Container();
		this.processContainer = new Container();
		this.label = new JLabel("Current Time " + api.getTime() + " - Throughput " + api.getThroughput());
		this.startButton = new JButton("Start");
		this.stepButton = new JButton("Step");
		this.cpuDisplay = new JTextArea(api.cpuDetails());
		this.ioDisplay = new JTextArea(api.ioDetails());
		this.cpuQueueDisplay = new JTextArea(api.cpuQueueDetails());
		this.ioQueueDisplay = new JTextArea(api.ioQueueDetails());
		this.processesDisplay = new JTextArea(api.processDetails());
		
		// Specifies behavior of the main window
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				GUI.this.setVisible(true);
				GUI.this.dispose();
			}
		});
		
		// Action listener declarations		
		startButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				running = !running;
				if(running) startButton.setText("Stop");
				else startButton.setText("Start");
			}
		});
		stepButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { step = true; } });
		
		// Setting components properties
			// TODO: Modify properties
		
		// Adds components to the window
		this.setLayout(new GridLayout(5,1));
		this.setPreferredSize(new Dimension(w,h));
		this.add(label);
		// Adds containers
		this.add(controlsContainer);
		this.add(cpuContainer);
		this.add(ioContainer);
		this.add(processContainer);
		// Sets layout manager for containers
		controlsContainer.setLayout(new GridBagLayout());
		cpuContainer.setLayout(new GridBagLayout());
		ioContainer.setLayout(new GridBagLayout());
		processContainer.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		// Adds components to appropriate containers
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		controlsContainer.add(startButton,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		controlsContainer.add(stepButton,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		cpuContainer.add(cpuDisplay,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		cpuContainer.add(cpuQueueDisplay,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		ioContainer.add(ioDisplay,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		ioContainer.add(ioQueueDisplay,gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		processContainer.add(processesDisplay,gbc);
		this.pack();
		
		// Refreshes gui and moves simulation forward one step
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
		    public void run()
		    {
		    	// TODO: Call API methods to update simulation
		    	if(running||step)
		    	{
		    		api.tick();
		    		label.setText("Current Time " + api.getTime() + " - Throughput " + api.getThroughput());
		    		cpuDisplay.setText(api.cpuDetails());
		    		ioDisplay.setText(api.ioDetails());
		    		cpuQueueDisplay.setText(api.cpuQueueDetails());
		    		ioQueueDisplay.setText(api.ioQueueDetails());
		    	   	processesDisplay.setText(api.processDetails());
		    	   	step = false;
		    	}
		    }
		},1000/fps,1000/fps);
	}
	
	// Getter and Setters
	
	
	// Operations
}
