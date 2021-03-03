import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFormattedTextField;
import javax.swing.JTextPane;
import javax.swing.BoxLayout;

import aeshliman.enumerators.Algorithm;
import aeshliman.simulation.CustomProcess;
import aeshliman.simulation.Device;
import aeshliman.simulation.SimulationAPI;



public class Window
{
	// Instance Variables
	protected JFrame window;
	
	private JLabel simulationDetailsLabel;
	private JPanel processInnerPanel;
	private JPanel cpuQueueInnerPanel;
	private JPanel ioQueueInnerPanel;
	private JPanel cpuInnerPanel;
	private JPanel ioInnerPanel;
	private JComboBox<Algorithm> algorithmSelect;
	private JButton startstopButton;
	private JFormattedTextField quantumField;
	private JFormattedTextField cpuField;
	private JFormattedTextField ioField;
	private JTextPane processDetailsField;
	private SimulationAPI api;
	private int width;
	private int height;
	private int currentPID;
	private String path;
	private boolean hasStarted;
	private boolean isRunning;
	private boolean willStep;
	private HashMap<Device,JPanel> cpuMap;
	private HashMap<Device,JPanel> ioMap;
	
	/**
	 * Create the application.
	 */
	public Window(int width, int height)
	{
		this.width = width;
		this.height = height;
		initialize();
	}
	
	// Instance Initializer Block
	{
		api = new SimulationAPI(Algorithm.FCFS,5,1,1);
		currentPID = -1;
		path = null;
		hasStarted = false;
		isRunning = false;
		willStep = false;
		cpuMap = new HashMap<Device,JPanel>();
		ioMap = new HashMap<Device,JPanel>();
	}
	
	// Getters and Setters
	public boolean isRunning() { return this.isRunning; }
	public boolean willStep() { return this.willStep; }
	public void toggleRunning() { this.isRunning = !isRunning; }
	public void toggleStep() { this.willStep = !willStep; }
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window = new JFrame();
		window.setTitle("Scheduling Simulation");
		window.setBounds(((int)screenSize.getWidth()/2)-width/2, ((int)screenSize.getHeight()/2)-height/2, 720, 405);
		window.setMinimumSize(new Dimension(720,405));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel headPanel = new JPanel();
		window.getContentPane().add(headPanel, BorderLayout.NORTH);
		headPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		simulationDetailsLabel = new JLabel("Time: 0   Average Turnaround Time: 0.00%   Average Wait Time: 0.00%   Throughput: 0.00000"
				+ " processes per " + api.getTimeUnit());
		simulationDetailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headPanel.add(simulationDetailsLabel);
		
		JPanel controlsPanel = new JPanel();
		headPanel.add(controlsPanel);
		
		startstopButton = new JButton("Start");
		startstopButton.setMinimumSize(new Dimension(85, 23));
		startstopButton.setMaximumSize(new Dimension(85, 23));
		startstopButton.setPreferredSize(new Dimension(85, 23));
		startstopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(path!=null)
				{
					if(!hasStarted) hasStarted = true;
					if(isRunning) startstopButton.setText("Resume");
					else startstopButton.setText("Pause");
					toggleRunning();
				}
				else JOptionPane.showMessageDialog(null, "Please select a scenario file first");
			}
		});
		controlsPanel.add(startstopButton);
		
		JButton stepButton = new JButton("Step");
		stepButton.setMinimumSize(new Dimension(85, 23));
		stepButton.setMaximumSize(new Dimension(85, 23));
		stepButton.setPreferredSize(new Dimension(85, 23));
		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(path!=null)
				{
					if(!hasStarted) hasStarted = true;
					startstopButton.setText("Resume");
					isRunning = false;
					willStep = true;
				}
				else JOptionPane.showMessageDialog(null, "Please select a scenario file first");
			}
		});
		controlsPanel.add(stepButton);
		
		JButton restartButton = new JButton("Restart");
		restartButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Resets all variables to their default state
				api = new SimulationAPI(Algorithm.FCFS,5,1,1);
				currentPID = -1;
				path = null;
				hasStarted = false;
				isRunning = false;
				willStep = false;
				cpuMap = new HashMap<Device,JPanel>();
				ioMap = new HashMap<Device,JPanel>();
				
				// Resets the GUI to its default state
				resetGUI();
				
				processInnerPanel.removeAll();
				cpuInnerPanel.removeAll();
				ioInnerPanel.removeAll();
				
				processInnerPanel.revalidate();
				cpuInnerPanel.revalidate();
				ioInnerPanel.revalidate();
				
				processInnerPanel.repaint();
				cpuInnerPanel.repaint();
				ioInnerPanel.repaint();
			}
		});
		restartButton.setMinimumSize(new Dimension(85, 23));
		restartButton.setMaximumSize(new Dimension(85, 23));
		restartButton.setPreferredSize(new Dimension(85, 23));
		controlsPanel.add(restartButton);
		
		JButton loadScenarioButton = new JButton("Scenario");
		loadScenarioButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String input = JOptionPane.showInputDialog("Please enter the path of a scenario file");
				if(!api.validScenario(input)) JOptionPane.showMessageDialog(null, "Invalid file path " + input);
				else
				{
					path = input;
					resetGUI();
					initializeAPI();
				}
			}
		});
		loadScenarioButton.setMinimumSize(new Dimension(85, 23));
		loadScenarioButton.setMaximumSize(new Dimension(85, 23));
		loadScenarioButton.setPreferredSize(new Dimension(85, 23));
		controlsPanel.add(loadScenarioButton);
		
		ActionListener updateSimulation = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetGUI();
				initializeAPI();
			}
		};
		
		algorithmSelect = new JComboBox<Algorithm>();
		algorithmSelect.addActionListener(updateSimulation);
		algorithmSelect.setPreferredSize(new Dimension(85, 23));
		algorithmSelect.setModel(new DefaultComboBoxModel<Algorithm>(Algorithm.values()));
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		algorithmSelect.setRenderer(renderer);
		controlsPanel.add(algorithmSelect);
		
		JLabel quantumLabel = new JLabel("Quantum");
		quantumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(quantumLabel);
		
		NumberFormatter inputFormat = new NumberFormatter(NumberFormat.getIntegerInstance());
		
		quantumField = new JFormattedTextField(inputFormat);
		quantumField.addActionListener(updateSimulation);
		quantumField.setText("5");
		quantumField.setPreferredSize(new Dimension(10, 23));
		quantumLabel.setLabelFor(quantumField);
		quantumField.setColumns(3);
		quantumField.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(quantumField);
		
		JLabel cpuCountLabel = new JLabel("CPU");
		cpuCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(cpuCountLabel);
		
		cpuField = new JFormattedTextField(inputFormat);
		cpuField.addActionListener(updateSimulation);
		cpuField.setText("1");
		cpuField.setPreferredSize(new Dimension(10, 23));
		cpuCountLabel.setLabelFor(cpuField);
		cpuField.setColumns(3);
		cpuField.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(cpuField);
		
		JLabel ioCountLabel = new JLabel("IO");
		ioCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(ioCountLabel);
		
		ioField = new JFormattedTextField(inputFormat);
		ioField.addActionListener(updateSimulation);
		ioField.setText("1");
		ioField.setPreferredSize(new Dimension(10, 23));
		ioCountLabel.setLabelFor(ioField);
		ioField.setColumns(3);
		ioField.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(ioField);
		
		JPanel processPanel = new JPanel();
		window.getContentPane().add(processPanel, BorderLayout.SOUTH);
		processPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel processLabel = new JLabel("All Processes");
		processLabel.setHorizontalAlignment(SwingConstants.CENTER);
		processPanel.add(processLabel, BorderLayout.NORTH);
		
		processInnerPanel = new JPanel();
		processPanel.add(processInnerPanel, BorderLayout.CENTER);
		
		JPanel cpuQueuePanel = new JPanel();
		window.getContentPane().add(cpuQueuePanel, BorderLayout.WEST);
		cpuQueuePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel cpuQueueLabel = new JLabel("CPU Queue Details");
		cpuQueueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cpuQueuePanel.add(cpuQueueLabel, BorderLayout.NORTH);
		
		cpuQueueInnerPanel = new JPanel();
		cpuQueuePanel.add(cpuQueueInnerPanel, BorderLayout.CENTER);
		cpuQueueInnerPanel.setLayout(new BoxLayout(cpuQueueInnerPanel, BoxLayout.Y_AXIS));
		
		JPanel ioQueuePanel = new JPanel();
		window.getContentPane().add(ioQueuePanel, BorderLayout.EAST);
		ioQueuePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel ioQueueLabel = new JLabel("IO Queue Details");
		ioQueueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ioQueuePanel.add(ioQueueLabel, BorderLayout.NORTH);
		
		ioQueueInnerPanel = new JPanel();
		ioQueuePanel.add(ioQueueInnerPanel, BorderLayout.CENTER);
		ioQueueInnerPanel.setLayout(new BoxLayout(ioQueueInnerPanel, BoxLayout.Y_AXIS));
		
		JPanel corePanel = new JPanel();
		window.getContentPane().add(corePanel, BorderLayout.CENTER);
		corePanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel devicePanel = new JPanel();
		corePanel.add(devicePanel);
		devicePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel deviceLabel = new JLabel("Devices Details");
		deviceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		devicePanel.add(deviceLabel, BorderLayout.NORTH);
		
		JPanel deviceInnerPanel = new JPanel();
		devicePanel.add(deviceInnerPanel, BorderLayout.CENTER);
		deviceInnerPanel.setLayout(new GridLayout(1, 2, 0, 0));
		
		JPanel cpuPanel = new JPanel();
		deviceInnerPanel.add(cpuPanel);
		cpuPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel cpuLabel = new JLabel("CPU Details");
		cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cpuPanel.add(cpuLabel, BorderLayout.NORTH);
		
		cpuInnerPanel = new JPanel();
		cpuPanel.add(cpuInnerPanel);
		
		JPanel ioPanel = new JPanel();
		deviceInnerPanel.add(ioPanel);
		ioPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel ioLabel = new JLabel("IO Details");
		ioLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ioPanel.add(ioLabel, BorderLayout.NORTH);
		
		ioInnerPanel = new JPanel();
		ioPanel.add(ioInnerPanel);
		
		JPanel detailsPanel = new JPanel();
		corePanel.add(detailsPanel);
		detailsPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel detailsLabel = new JLabel("Process Details");
		detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		detailsPanel.add(detailsLabel, BorderLayout.NORTH);
		
		JPanel detailsInnerPanel = new JPanel();
		detailsPanel.add(detailsInnerPanel, BorderLayout.CENTER);
		detailsInnerPanel.setLayout(new GridLayout(1, 1, 0, 0));
		
		processDetailsField = new JTextPane();
		processDetailsField.setOpaque(false);
		processDetailsField.setEditable(false);
		StyledDocument processDetailsFieldDoc = processDetailsField.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		processDetailsFieldDoc.setParagraphAttributes(0, processDetailsFieldDoc.getLength(), center, false);
		detailsInnerPanel.add(processDetailsField);
		
		//window.pack();
	}
	
	public void initializeAPI()
	{
		// Creates a new simulation
		api = new SimulationAPI((Algorithm)algorithmSelect.getSelectedItem(),Integer.valueOf(quantumField.getText()),
				Integer.valueOf(cpuField.getText()),Integer.valueOf(ioField.getText()));
		if(path!=null) api.loadScenario(path); // Loads the scenario file if one has been provided
		
		// Gets the devices
		LinkedList<Device> cpus = api.getCPU();
		LinkedList<Device> ios = api.getIO();
		
		// Determines the number of cells for cpu and io grid layout
		int cpuCol = (int)Math.ceil(Math.sqrt(cpus.size()));
		int cpuRow = (int)Math.ceil((double)cpus.size()/cpuCol);
		int ioCol = (int)Math.ceil(Math.sqrt(ios.size()));
		int ioRow= (int)Math.ceil((double)ios.size()/cpuCol);
		
		// Sets up grids
		cpuInnerPanel.setLayout(new GridLayout(cpuRow, cpuCol, 0, 0));
		ioInnerPanel.setLayout(new GridLayout(ioRow, ioCol, 0, 0));
		
		// Adds a panel for each device
		cpuMap.clear();
		ioMap.clear();
		for(Device cpu : cpus)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			cpuMap.put(cpu, panel);
			cpuInnerPanel.add(panel);
			JLabel label = new JLabel("CPU " + cpu.getID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);
		}
		for(Device io : ios)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			ioMap.put(io, panel);
			ioInnerPanel.add(panel);
			JLabel label = new JLabel("IO " + io.getID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);
		}
		
		// Adds each process to process panel
		processInnerPanel.setLayout(new GridLayout(1, api.getProcesses().size(), 0, 0));
		for(CustomProcess process : api.getProcesses()) { processInnerPanel.add(processButtonFactory(process)); }
	}
	
	public void resetGUI()
	{
		simulationDetailsLabel.setText("Time: 0   Average Turnaround Time: 0.00%   Average Wait Time: 0.00%   Throughput: 0.00000"
				+ " processes per " + api.getTimeUnit());
		cpuQueueInnerPanel.removeAll();
		ioQueueInnerPanel.removeAll();
		
		cpuQueueInnerPanel.revalidate();
		ioQueueInnerPanel.revalidate();
		
		
		cpuQueueInnerPanel.repaint();
		ioQueueInnerPanel.repaint();
		
		cpuMap.forEach((device,panel)->
		{
			if(panel.getComponentCount()>1)
			{
				panel.remove(1);
				panel.revalidate();
				panel.repaint();
			}
		});
		
		ioMap.forEach((device,panel)->
		{
			if(panel.getComponentCount()>1)
			{
				panel.remove(1);
				panel.revalidate();
				panel.repaint();
			}
		});
		
	}
	
	public void update()
	{
		// Updates simulation details label with simulation statistics
		simulationDetailsLabel.setText("Time: " + api.getTime() + "   Average Turnaround Time: " + String.format("%.2f", api.calcAvgTurnaround()*100)
				+ "%   Average Wait Time: " + String.format("%.2f", api.calcAvgWait()*100) + "%   Throughput: " + String.format("%.5f", api.calcThroughput())
				+ " processes per " + api.getTimeUnit());
		
		// Updates devices panels to show current process
		cpuMap.forEach((device,panel)->
		{
			if(panel.getComponentCount()>1)
			{
				panel.remove(1);
				panel.revalidate();
				panel.repaint();
			}
			if(!device.isEmpty()) panel.add(processButtonFactory(device.getProcess()));
		});
		ioMap.forEach((device,panel)->
		{
			if(panel.getComponentCount()>1)
			{
				panel.remove(1);
				panel.revalidate();
				panel.repaint();
			}
			if(!device.isEmpty()) panel.add(processButtonFactory(device.getProcess()));
		});
		
		// Updates ready and waiting queue to show current processes
		for(CustomProcess process : api.getReadyQueue())
		{
			cpuQueueInnerPanel.removeAll();
			cpuQueueInnerPanel.add(processButtonFactory(process));
			cpuQueueInnerPanel.validate();
		}
		for(CustomProcess process : api.getWaitingQueue())
		{
			ioQueueInnerPanel.removeAll();
			ioQueueInnerPanel.add(processButtonFactory(process));
			ioQueueInnerPanel.validate();
		}
		
		// Updates process details display if there is an selected process
		if(currentPID!=-1) processDetailsField.setText(String.valueOf(api.findProcess(currentPID)));
	}
	
	public JButton processButtonFactory(CustomProcess process)
	{
		ActionListener processAction = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int pid = Integer.parseInt(((JButton)e.getSource()).getText());
				processDetailsField.setText(api.findProcess(pid).toString());
				currentPID = pid;
			}
		};
		
		JButton processButton = new JButton(Integer.toString(process.getPID()));
		processButton.setPreferredSize(new Dimension(50, 50));
		processButton.addActionListener(processAction);
		return processButton;
	}
	
	public void tick()
	{
		api.tick();
		resetGUI();
		update();
	}
}
