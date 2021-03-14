import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import aeshliman.enumerators.Algorithm;
import aeshliman.simulation.CustomProcess;
import aeshliman.simulation.Device;
import aeshliman.simulation.Simulation;
import javax.swing.border.EtchedBorder;

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
	private boolean finished;
	private boolean initialized;
	private boolean start;
	private boolean step;
	private int fps;
		// Simulation Variables
	private Simulation sim;
	private File scenarioFile;
	private Algorithm algorithm;
	private int quantum;
	private int cpuCount;
	private int ioCount;
		// Table Data
	DefaultTableModel tableModel;
	JTable processTable;
	JTableHeader tableHead;
	private String[] colNames = {"PID","Name","Priority","Current","CPU Bursts","Finished","IO Bursts","Finished",
			"Arrival","Start","Finish","CPU Wait","IO Wait","State"};
		// Device Data
	private LinkedList<JPanel> cpus;
	private LinkedList<JPanel> ios;
	
	// Constructor
	public Window()
	{
		initialize();
		initializeTimer();
	}
	
	{
		algorithm = Algorithm.FCFS;
		quantum = 5;
		cpuCount = 1;
		ioCount = 1;
		finished = false;
		initialized = false;
		start  = false;
		step = false;
		fps = 10;
	}
	
	// Operations
	private void initialize()
	{
			// Main Application Frame
		window = new JFrame();
		window.setTitle("Scheduling Simulation");
		window.setBounds(100, 100, 1008, 567);
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
				if(initialized)
				{
					resetGUI();
					finished = false;
					initialized = false;
					start = false;
					step = false;
				}
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
				if(!initialized) JOptionPane.showMessageDialog(window, "Must start simulation first");
				else
				{
					JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
					fc.setFileFilter(new FileNameExtensionFilter("Text Files","txt","text"));
					fc.setAcceptAllFileFilterUsed(false);
					switch(fc.showDialog(window, "Save Log"))
					{
					case JFileChooser.APPROVE_OPTION:
						try(FileWriter fw = new FileWriter(fc.getSelectedFile(),false);) { fw.write(sim.getLog()); }
						catch(IOException ex) { ex.printStackTrace(); }
						break;
					default:
						break;
					}
				}
			}
		});
		fileMenu.add(saveMenuItem);
		
		JMenuItem resetMenuItem = new JMenuItem("Reset Simulation");
		resetMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetGUI();
				finished = false;
				initialized = false;
				start = false;
				step = false;
			}
		});
		fileMenu.add(resetMenuItem);
		
		JMenuItem settingsMenuItem = new JMenuItem("Simulation Settings");
		settingsMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// TODO: Implement settings button
					// Should pop up a form window for setting selection
				SettingsDialog settingDialog = new SettingsDialog();
				settingDialog.setVisible(true);
			}
		});
		fileMenu.add(settingsMenuItem);
		
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
				if(!initialized) { initializeSimulation(); }
				start = true;
			}
		});
		controlPanel.add(startButton);
		
		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!initialized) { initializeSimulation(); }
				step = true;
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
		cpuPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel cpuLabel = new JLabel("CPU");
		cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cpuPanel.add(cpuLabel, BorderLayout.NORTH);
		
		JPanel queuePanel = new JPanel();
		queuePanel.setPreferredSize(new Dimension(450, 10));
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
		cpuQueueSubpanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
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
		ioPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel IO = new JLabel("IO");
		IO.setHorizontalAlignment(SwingConstants.CENTER);
		ioPanel.add(IO, BorderLayout.NORTH);
		
		processPanel = new JPanel();
		processPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		mainPanel.add(processPanel);
		processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));
		
		processTable = new JTable();
		processTable.setFillsViewportHeight(true);
		tableModel = (DefaultTableModel) processTable.getModel();
		tableModel.setColumnIdentifiers(colNames);
		tableHead = processTable.getTableHeader();
		processPanel.add(tableHead);
		processPanel.add(processTable);
	}
	
	private void initializeSimulation()
	{
		initialized = true;
		
		// Initializes simulation object
		sim = new Simulation(algorithm,quantum,cpuCount,ioCount);
		sim.loadScenario(scenarioFile);
		
		// Updates the simulation statistics
		systemTimeLabel.setText("System Time: " + Integer.toString(sim.getTime()));
		throughputLabel.setText("Throughput: " + String.format("%.2f", sim.calcThroughput()));
		turnaroundLabel.setText("Average Turnaround: " + String.format("%.2f", sim.calcAvgTurnaround()));
		waitTimeLabel.setText("Average Wait Time: " + String.format("%.2f", sim.calcAvgCPUWait()));
		
		// Initialize process table
		for(CustomProcess process : sim.getProcesses())
		{
			Object[] data = new Object[14];
			data[0] = "P" + process.getPID();
			data[1] = process.getName();
			data[2] = process.getPriority();
			data[3] = process.currentBurst();
			data[4] = process.cpuRemainingBursts();
			data[5] = process.cpuFinishedBursts();
			data[6] = process.ioRemainingBursts();
			data[7] = process.ioFinishedBursts();
			data[8] = process.getArrivalTime();
			data[9] = process.getResponseTime() + process.getArrivalTime();
			data[10] = process.getFinishTime();
			data[11] = process.getCpuWaitTime();
			data[12] = process.getIoWaitTime();
			data[13] = process.getState();
			tableModel.addRow(data);
		}
		tableModel.fireTableDataChanged();
		
		// Initialize devices
		cpus = new LinkedList<JPanel>();
		for(Device cpu : sim.getCPU())
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0,0));
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
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
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
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
	
	public void initializeTimer()
	{
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() { public void run() { if(!finished&&(start||step)) { tick();} } }, 1000/fps, 1000/fps);
	}
	
	private void updateGUI()
	{
		// Updates the simulation statistics
		systemTimeLabel.setText("System Time: " + Integer.toString(sim.getTime()));
		throughputLabel.setText("Throughput: " + String.format("%.2f", sim.calcThroughput()));
		turnaroundLabel.setText("Average Turnaround: " + String.format("%.2f", sim.calcAvgTurnaround()));
		waitTimeLabel.setText("Average Wait Time: " + String.format("%.2f", sim.calcAvgCPUWait()));
		
		// Devices
		for(Device cpu : sim.getCPU())
		{
			JLabel label = (JLabel)cpus.get(cpu.getID()).getComponent(1);
			if(cpu.getProcess()==null) label.setText("Idle");
			else label.setText("P" + cpu.getProcess().getPID());
		}
		for(Device io : sim.getIO())
		{
			JLabel label = (JLabel)ios.get(io.getID()).getComponent(1);
			if(io.getProcess()==null) label.setText("Idle");
			else label.setText("P" + io.getProcess().getPID());
		}
		
		// Queues
		cpuQueueSubpanel.removeAll();
		ioQueueSubpanel.removeAll();
		for(CustomProcess process : sim.getCPUScheduler().getQueue())
		{
			JPanel panel = new JPanel();
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panel.setPreferredSize(new Dimension(25, 25));
			cpuQueueSubpanel.add(panel);
			panel.setLayout(new BorderLayout(0, 0));
			JLabel label = new JLabel("P" + process.getPID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label);
		}
		for(CustomProcess process : sim.getIOScheduler().getQueue())
		{
			JPanel panel = new JPanel();
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panel.setPreferredSize(new Dimension(25, 25));
			ioQueueSubpanel.add(panel);
			panel.setLayout(new BorderLayout(0, 0));
			JLabel label = new JLabel("P" + process.getPID());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(label);
		}
		cpuQueueSubpanel.revalidate();
		cpuQueueSubpanel.repaint();
		ioQueueSubpanel.revalidate();
		ioQueueSubpanel.repaint();
		
		// Table data
		while(tableModel.getRowCount()>0) { for(int i=0; i<tableModel.getRowCount(); i++) tableModel.removeRow(i); }
		for(CustomProcess process : sim.getProcesses())
		{
			Object[] data = new Object[14];
			data[0] = "P" + process.getPID();
			data[1] = process.getName();
			data[2] = process.getPriority();
			data[3] = process.currentBurst();
			data[4] = process.cpuRemainingBursts();
			data[5] = process.cpuFinishedBursts();
			data[6] = process.ioRemainingBursts();
			data[7] = process.ioFinishedBursts();
			data[8] = process.getArrivalTime();
			data[9] = process.getResponseTime() + process.getArrivalTime();
			data[10] = process.getFinishTime();
			data[11] = process.getCpuWaitTime();
			data[12] = process.getIoWaitTime();
			data[13] = process.getState();
			tableModel.addRow(data);
		}
		tableModel.fireTableDataChanged();
	}
	
	private void resetGUI()
	{
		// Resets simulation statistics
		systemTimeLabel.setText("System Time: X");
		throughputLabel.setText("Throughput: X");
		turnaroundLabel.setText("Average Turnaround: X");
		waitTimeLabel.setText("Average Wait Time: X");
		
		// Resets process table
		while(tableModel.getRowCount()>0) { for(int i=0; i<tableModel.getRowCount(); i++) tableModel.removeRow(i); }
		tableModel.fireTableDataChanged();
		
		// Resets Devices
		cpuPanel.removeAll();
		JLabel cpuLabel = new JLabel("CPU");
		cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cpuPanel.add(cpuLabel, BorderLayout.NORTH);
		cpuPanel.revalidate();
		cpuPanel.repaint();
		ioPanel.removeAll();
		JLabel IO = new JLabel("IO");
		IO.setHorizontalAlignment(SwingConstants.CENTER);
		ioPanel.add(IO, BorderLayout.NORTH);
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
		step = false;
		finished = sim.getTerminatedCount()==sim.getProcesses().size();
		if(finished)
		{
			step = false;
			start = false;
		}
	}
	
	private class SettingsDialog extends JDialog
	{
		private static final long serialVersionUID = 61127048419647388L;
		private final JPanel contentPanel = new JPanel();
		private JComboBox<Algorithm> algorithmComboBox;
		private JTextField ioField;
		private JTextField cpuField;
		private JTextField quantumField;
		private JLabel algorithmLabel;
		private JLabel quantumLabel;
		private JLabel cpuLabel;
		private JLabel ioLabel;
		
		public SettingsDialog()
		{
			setModal(true);
			setType(Type.POPUP);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setResizable(false);
			setTitle("Simulation Settings");
			setBounds(100, 100, 250, 200);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			{
				JPanel algorithmPanel = new JPanel();
				contentPanel.add(algorithmPanel);
				{
					algorithmLabel = new JLabel("Algorithm");
					algorithmLabel.setMinimumSize(new Dimension(70, 20));
					algorithmLabel.setMaximumSize(new Dimension(70, 20));
					algorithmLabel.setHorizontalAlignment(SwingConstants.CENTER);
					algorithmLabel.setPreferredSize(new Dimension(70, 20));
					algorithmPanel.add(algorithmLabel);
				}
				{
					algorithmComboBox = new JComboBox<Algorithm>();
					algorithmComboBox.setMinimumSize(new Dimension(70, 20));
					algorithmComboBox.setMaximumSize(new Dimension(70, 20));
					algorithmLabel.setLabelFor(algorithmComboBox);
					algorithmComboBox.setPreferredSize(new Dimension(70, 20));
					algorithmComboBox.setModel(new DefaultComboBoxModel<Algorithm>(Algorithm.values()));
					algorithmComboBox.setSelectedIndex(0);
					algorithmPanel.add(algorithmComboBox);
				}
			}
			{
				JPanel quantumPanel = new JPanel();
				contentPanel.add(quantumPanel);
				{
					quantumLabel = new JLabel("Quantum");
					quantumLabel.setMinimumSize(new Dimension(70, 20));
					quantumLabel.setMaximumSize(new Dimension(70, 20));
					quantumLabel.setHorizontalAlignment(SwingConstants.CENTER);
					quantumLabel.setPreferredSize(new Dimension(70, 20));
					quantumPanel.add(quantumLabel);
				}
				{
					quantumField = new JTextField();
					quantumLabel.setLabelFor(quantumField);
					quantumField.setPreferredSize(new Dimension(70, 20));
					quantumPanel.add(quantumField);
					quantumField.setColumns(10);
				}
			}
			{
				JPanel cpuPanel = new JPanel();
				contentPanel.add(cpuPanel);
				{
					cpuLabel = new JLabel("CPU Count");
					cpuLabel.setMinimumSize(new Dimension(70, 20));
					cpuLabel.setMaximumSize(new Dimension(70, 20));
					cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
					cpuLabel.setPreferredSize(new Dimension(70, 20));
					cpuPanel.add(cpuLabel);
				}
				{
					cpuField = new JTextField();
					cpuLabel.setLabelFor(cpuField);
					cpuField.setPreferredSize(new Dimension(70, 20));
					cpuPanel.add(cpuField);
					cpuField.setColumns(10);
				}
			}
			{
				JPanel ioPanel = new JPanel();
				contentPanel.add(ioPanel);
				{
					ioLabel = new JLabel("IO Count");
					ioLabel.setMinimumSize(new Dimension(70, 20));
					ioLabel.setMaximumSize(new Dimension(70, 20));
					ioLabel.setHorizontalAlignment(SwingConstants.CENTER);
					ioLabel.setPreferredSize(new Dimension(70, 20));
					ioPanel.add(ioLabel);
				}
				{
					ioField = new JTextField();
					ioLabel.setLabelFor(ioField);
					ioField.setPreferredSize(new Dimension(70, 20));
					ioPanel.add(ioField);
					ioField.setColumns(10);
				}
			}
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton submitButton = new JButton("Submit");
					submitButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							boolean exit = true;
							Algorithm a = null;
							int q = 0;
							int c = 0;
							int i = 0;
							try { a = (Algorithm) algorithmComboBox.getSelectedItem(); }
							catch(NumberFormatException ex) { ex.printStackTrace(); }
							try { q = Integer.parseInt(quantumField.getText()); }
							catch(NumberFormatException ex)
							{
								quantumField.setText("Invalid Entry");
								exit = false;
							}
							try { c = Integer.parseInt(cpuField.getText()); }
							catch(NumberFormatException ex)
							{
								cpuField.setText("Invalid Entry");
								exit = false;
							}
							try { i = Integer.parseInt(ioField.getText()); }
							catch(NumberFormatException ex)
							{
								ioField.setText("Invalid Entry");
								exit = false;
							}
							if(exit)
							{
								algorithm = a;
								quantum = q;
								cpuCount = c;
								ioCount = i;
								dispose();
							}
						}
					});
					submitButton.setPreferredSize(new Dimension(100, 25));
					submitButton.setActionCommand("Submit");
					buttonPane.add(submitButton);
					getRootPane().setDefaultButton(submitButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.setPreferredSize(new Dimension(100, 25));
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
		}
	}
}
