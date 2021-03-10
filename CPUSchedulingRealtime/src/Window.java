import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;

import aeshliman.enumerators.Algorithm;
import aeshliman.simulation.CustomProcess;
import aeshliman.simulation.Device;
import aeshliman.simulation.Simulation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class Window
{
	// Instance Variables
	protected JFrame window;
		// GUI Components
	private JLabel systemTimeLabel;
	private JLabel throughputLabel;
	private JLabel turnaroundLabel;
	private JLabel waitTimeLabel;
	private JPanel cpuPanel;
	private JPanel ioPanel;
	private JPanel cpuQueueSubpanel;
	private JPanel ioQueueSubpanel;
	private JPanel processPanel;
		// Simulation Variables
	private Simulation sim;
	private File scenarioFile;
	private Algorithm algorithm;
	private int quantum;
	private int cpuCount;
	private int ioCount;
		// Table Data
	private String[] colNames = {"PID","Arrival","Priority","CPU Bursts","IO Bursts","Start Time","Finish Time","Wait Time","IO Wait Time","State"};
	private Object[][] data;
		// Device Data
	private LinkedList<JPanel> cpus;
	private LinkedList<JPanel> ios;
	
	// Constructor
	public Window()
	{
		initialize();
		initializeSimulation();
	}
	
	{
		algorithm = Algorithm.FCFS;
		quantum = 5;
		cpuCount = 1;
		ioCount = 1;
	}
	
	// Operations
	private void initialize()
	{
			// Main Application Frame
		window = new JFrame();
		window.setTitle("Scheduling Simulation");
		window.setBounds(100, 100, 720, 405);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
			// Menu
		JMenuBar menuBar = new JMenuBar();
		window.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu, BorderLayout.NORTH);
		
		JMenuItem loadMenuItem = new JMenuItem("Load Scenario");
		loadMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				fc.setFileFilter(new FileNameExtensionFilter("Text Files","txt","text"));
				fc.setAcceptAllFileFilterUsed(false);
				switch(fc.showDialog(window,"Load Scenario"))
				{
				case JFileChooser.APPROVE_OPTION:
					scenarioFile = fc.getSelectedFile();
					break;
				default:
					break;
				}
			}
		});
		fileMenu.add(loadMenuItem);
		
		JMenuItem saveMenuItem = new JMenuItem("Save Log");
		saveMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// TODO: Implement log saving
			}
		});
		fileMenu.add(saveMenuItem);
		
		JMenuItem resetMenuItem = new JMenuItem("Reset Simulation");
		resetMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// TODO: Implement simulation reset
			}
		});
		fileMenu.add(resetMenuItem);
		
			// Controls and Simulation Statistics
		JPanel bodyPanel = new JPanel();
		window.getContentPane().add(bodyPanel, BorderLayout.CENTER);
		bodyPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel headPanel = new JPanel();
		headPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		headPanel.setPreferredSize(new Dimension(10, 75));
		bodyPanel.add(headPanel, BorderLayout.NORTH);
		headPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel controlPanel = new JPanel();
		headPanel.add(controlPanel);
		controlPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel dataPanel = new JPanel();
		headPanel.add(dataPanel);
		dataPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetGUI();
				initializeSimulation();
			}
		});
		controlPanel.add(startButton);
		
		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tick();
			}
		});
		controlPanel.add(stepButton);
		
		JLabel cpuCountLabel = new JLabel("CPU's: 1");
		cpuCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(cpuCountLabel);
		
		JLabel ioCountLabel = new JLabel("IO's: 1");
		ioCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(ioCountLabel);
		
		JLabel quantumLabel = new JLabel("Quantum: 5");
		quantumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(quantumLabel);
		
		systemTimeLabel = new JLabel("System Time: X");
		systemTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataPanel.add(systemTimeLabel);
		
		throughputLabel = new JLabel("Throughput: X");
		throughputLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataPanel.add(throughputLabel);
		
		turnaroundLabel = new JLabel("Average Turnaround: X");
		turnaroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataPanel.add(turnaroundLabel);
		
		waitTimeLabel = new JLabel("Average Wait Time: X");
		waitTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataPanel.add(waitTimeLabel);
		
			// Simulation Data
		JPanel mainPanel = new JPanel();
		bodyPanel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel contentPanel = new JPanel();
		mainPanel.add(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		
		cpuPanel = new JPanel();
		cpuPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(cpuPanel);
		cpuPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel queuePanel = new JPanel();
		contentPanel.add(queuePanel);
		queuePanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel cpuQueuePanel = new JPanel();
		cpuQueuePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		queuePanel.add(cpuQueuePanel);
		cpuQueuePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel cpuQueueLabel = new JLabel("CPU Queue");
		cpuQueueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cpuQueuePanel.add(cpuQueueLabel, BorderLayout.NORTH);
		
		cpuQueueSubpanel = new JPanel();
		cpuQueuePanel.add(cpuQueueSubpanel, BorderLayout.CENTER);
		
		JPanel ioQueuePanel = new JPanel();
		ioQueuePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		queuePanel.add(ioQueuePanel);
		ioQueuePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel ioQueueLabel = new JLabel("IO Queue");
		ioQueueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ioQueuePanel.add(ioQueueLabel, BorderLayout.NORTH);
		
		ioQueueSubpanel = new JPanel();
		ioQueuePanel.add(ioQueueSubpanel, BorderLayout.CENTER);
		
		ioPanel = new JPanel();
		ioPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(ioPanel);
		ioPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		processPanel = new JPanel();
		processPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		mainPanel.add(processPanel);
	}
	
	private void initializeSimulation()
	{
		// Initializes simulation object
		sim = new Simulation(algorithm,quantum,cpuCount,ioCount);
		sim.loadScenario(scenarioFile);
		
		// Updates the simulation statistics
		systemTimeLabel.setText("System Time: " + Integer.toString(sim.getTime()));
		throughputLabel.setText("Throughput: " + String.format("%.2f", sim.calcThroughput()));
		turnaroundLabel.setText("Average Turnaround: " + String.format("%.2f", sim.calcAvgTurnaround()));
		waitTimeLabel.setText("Average Wait Time: " + String.format("%.2f", sim.calcAvgCPUWait()));
		
		// Initialize process table
		int i = 0;
		data = new Object[sim.getProcesses().size()][10];
		for(CustomProcess process : sim.getProcesses())
		{
			data[i][0] = "P" + process.getPID();
			data[i][1] = process.getArrivalTime();
			data[i][2] = process.getPriority();
			data[i][3] = process.cpuBursts();
			data[i][4] = process.ioBursts();
			data[i][5] = process.getResponseTime() + process.getArrivalTime();
			data[i][6] = process.getFinishTime();
			data[i][7] = process.getCpuWaitTime();
			data[i][8] = process.getIoWaitTime();
			data[i][9] = process.getState();
			i++;
		}
		processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));
		JTable processTable = new JTable(data,colNames);
		processTable.setFillsViewportHeight(true);
		JTableHeader tableHead = processTable.getTableHeader();
		processPanel.add(tableHead);
		processPanel.add(processTable);
		
		// Initialize devices
		cpus = new LinkedList<JPanel>();
		for(Device cpu : sim.getCPU())
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0,0));
			cpuPanel.add(panel);
			JLabel label = new JLabel("CPU " + cpu.getID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);
			JLabel process = new JLabel();
			if(cpu.getProcess()==null) process.setText("Idle");
			else process.setText("P" + cpu.getProcess());
			process.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(process, BorderLayout.CENTER);
			cpus.add(cpu.getID(),panel);
		}
		cpuPanel.revalidate();
		cpuPanel.repaint();
		ios = new LinkedList<JPanel>();
		for(Device io : sim.getIO())
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0,0));
			ioPanel.add(panel);
			JLabel label = new JLabel("IO " + io.getID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label, BorderLayout.NORTH);
			JLabel process = new JLabel();
			if(io.getProcess()==null) process.setText("Idle");
			else process.setText("P" + io.getProcess().getPID());
			process.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(process, BorderLayout.CENTER);
			ios.add(io.getID(),panel);
		}
		ioPanel.revalidate();
		ioPanel.repaint();
	}
	
	private void updateGUI()
	{
		// Updates the simulation statistics
		systemTimeLabel.setText("System Time: " + Integer.toString(sim.getTime()));
		throughputLabel.setText("Throughput: " + String.format("%.2f", sim.calcThroughput()));
		turnaroundLabel.setText("Average Turnaround: " + String.format("%.2f", sim.calcAvgTurnaround()));
		waitTimeLabel.setText("Average Wait Time: " + String.format("%.2f", sim.calcAvgCPUWait()));
		
		for(Device cpu : sim.getCPU())
		{
			JLabel label = (JLabel)cpus.get(cpu.getID()).getComponent(1);
			if(cpu.getProcess()==null) label.setText("Idle");
			else label.setText("P" + cpu.getProcess().getPID());
		}
		for(Device io : sim.getIO())
		{
			JLabel label = (JLabel)cpus.get(io.getID()).getComponent(1);
			if(io.getProcess()==null) label.setText("Idle");
			else label.setText("P" + io.getProcess().getPID());
		}
		
		int i = 0;
		for(CustomProcess process : sim.getProcesses())
		{
			data[i][0] = "P" + process.getPID();
			data[i][1] = process.getArrivalTime();
			data[i][2] = process.getPriority();
			data[i][3] = process.cpuBursts();
			data[i][4] = process.ioBursts();
			data[i][5] = process.getResponseTime() + process.getArrivalTime();
			data[i][6] = process.getFinishTime();
			data[i][7] = process.getCpuWaitTime();
			data[i][8] = process.getIoWaitTime();
			data[i][9] = process.getState();
			i++;
		}
		
		
	}
	
	private void resetGUI()
	{
		// Resets simulation statistics
		systemTimeLabel.setText("System Time: X");
		throughputLabel.setText("Throughput: X");
		turnaroundLabel.setText("Average Turnaround: X");
		waitTimeLabel.setText("Average Wait Time: X");
		
		// Resets process table
		processPanel.removeAll();
		processPanel.revalidate();
		processPanel.repaint();
		
		// Resets Devices
		cpuPanel.removeAll();
		cpuPanel.revalidate();
		cpuPanel.repaint();
		ioPanel.removeAll();
		ioPanel.revalidate();
		ioPanel.repaint();
		
		// Resets Queues
		cpuQueueSubpanel.removeAll();
		cpuQueueSubpanel.revalidate();
		cpuQueueSubpanel.repaint();
		ioQueueSubpanel.removeAll();
		ioQueueSubpanel.revalidate();
		ioQueueSubpanel.repaint();
	}
	
	public void tick()
	{
		sim.tick();
		updateGUI();
	}
}
