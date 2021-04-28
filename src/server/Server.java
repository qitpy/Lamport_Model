package server;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import RMIHandle.RMIInterface;
import RMIHandle.RMIcore;

import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.UIManager;
import java.awt.CardLayout;
import javax.swing.border.BevelBorder;
import javax.swing.JComboBox;
import java.awt.List;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.AbstractListModel;


public class Server extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ttf_ServerInfoIP;
	private JTextField ttf_ServerInfoHostname;
	private JTextField ttf_ServerInfoPort;
	
	protected JPanel lb_Start;
	protected JPanel panel_ListServer;
	protected JPanel panel_ServerInfo;
	protected JPanel panel_MessageTo;
	protected JPanel panel_MessageEvent;
	protected JPanel panel_LogicClock;
	protected GroupLayout gl_contentPane;
	protected JScrollPane scrollPane_1;
	protected JList<String> list_MessageEvent;
	protected JScrollPane scrollPane;
	protected List list_LogicClock;
	protected JLabel lb_ServerInfoIP;
	protected JLabel lb_ServerInfoHostname;
	protected JLabel lb_ServerInfoPort;
	protected JButton btn_MessageToServer2;
	protected JButton btn_MessageToServer1;
	protected JButton btn_MessageToServer3;
	protected JButton btn_MessageToLocal;
	protected JList<String> list_ServerList = new JList<String>();
	protected JLabel lb_StartHostname;
	protected JLabel lb_StartPort;
	protected JButton btn_StartLookup;
	protected JButton btn_StartInit;
	protected JComboBox<String> cbBox_StartHostname;
	protected JComboBox<String> cbBox_StartPort;
	
	private int logicNum;
	private int[] otherServer;
	private String[] message;
	private RMIcore core;
	DefaultListModel<String> model_ListMessage;
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws RemoteException 
	 */
	public Server() {
		declareVar();
		difinitionVar();
		addActionPerform();
		otherServer = new int[5];
		for (int i = 0; i < 5; i++)
		{
			otherServer[i] = 0;
		}
		logicNum = 0;
		message = new String[] {};
		model_ListMessage = new DefaultListModel<>();
		
		try {
			core = new RMIcore() {
				private static final long serialVersionUID = 1L;

				@Override
				public void status(String port, int logicClock) throws RemoteException {
					int max = (logicClock > logicNum) ? logicClock : logicNum;
					max++;
					logicNum = max;
					setNextLogicClock(logicNum);
					addEventToMessageEvent("RECEIVE", Integer.parseInt(port));
					//Them event to Message event
				}

				@Override
				public void init() throws RemoteException {
					// TODO Auto-generated method stub
					String hostName = cbBox_StartHostname.getSelectedItem().toString();
					String port = cbBox_StartPort.getSelectedItem().toString();
					try {
						//initServer(hostName, port);
						System.out.println(hostName + " + " + port);
						LocateRegistry.createRegistry(Integer.parseInt(port));
						String nameRebind = "rmi://" + hostName + ":" + port + "/core";
						Naming.rebind(nameRebind, this);
						
						System.out.println("Init Server success!");
						ttf_ServerInfoIP.setText(hostName);
						ttf_ServerInfoPort.setText(port);
						
						updateServers(port);
						
						list_LogicClock.addItem("index     |     value");
						list_LogicClock.addItem(getLogicNumString(0, 0));
				
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					btn_StartInit.setEnabled(false);
				}
			};
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void addActionPerform() {
		btn_StartLookup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String hostName = cbBox_StartHostname.getSelectedItem().toString();
				String port = cbBox_StartPort.getSelectedItem().toString();
				if (Lookup(hostName, port))
				{
					updateServers(port);			
					if (!ttf_ServerInfoPort.getText().equals(port)) {
						if (btn_MessageToServer1.getText().equals("Server 1"))
						{
							btn_MessageToServer1.setText("Port " + port);
							if (!ttf_ServerInfoPort.getText().equals(""))
							{
								btn_MessageToServer1.setEnabled(true);
							}
							
						}
						else if (btn_MessageToServer2.getText().equals("Server 2") && !btn_MessageToServer1.getText().equals("Port " + port)) {
							btn_MessageToServer2.setText("Port " + port);
							if (!ttf_ServerInfoPort.getText().equals(""))
							{
								btn_MessageToServer2.setEnabled(true);
							}	
						}
					}
				}
				else
				{
					if (ttf_ServerInfoPort.getText().equals(""))
						btn_StartInit.setEnabled(true);
				}	
			}
		});
		this.
		
		btn_StartInit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					core.init();
					btn_MessageToLocal.setEnabled(true);
					
					if (!btn_MessageToServer2.getText().equals("Server 2")) {
						btn_MessageToServer2.setEnabled(true);
					}
					if (!btn_MessageToServer1.getText().equals("Server 1")) {
						btn_MessageToServer1.setEnabled(true);
					}
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btn_MessageToServer1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//		
				try {
					int port = Integer.parseInt(btn_MessageToServer1.getText().substring(5));
					int logicClock = getLogicClock();
					deliverTo("localhost", port, logicClock);
					increaseLogic();
					setNextLogicClock(logicNum);
					addEventToMessageEvent("SEND", port);
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btn_MessageToServer2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int port = Integer.parseInt(btn_MessageToServer2.getText().substring(5));
					int logicClock = getLogicClock();
					deliverTo("localhost", port, logicClock);
					increaseLogic();
					setNextLogicClock(logicNum);
					addEventToMessageEvent("SEND", port);
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btn_MessageToServer3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		btn_MessageToLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// tang logic len 1
				try {
					increaseLogic();
					int logicNum;
					logicNum = getLogicClock();
					setNextLogicClock(logicNum);
					addEventToMessageEvent();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	void declareVar() {
		lb_Start = new JPanel();
		panel_ListServer = new JPanel();
		panel_ServerInfo = new JPanel();
		panel_MessageTo = new JPanel();
		panel_MessageTo.setPreferredSize(new Dimension(0, 0));
		panel_MessageEvent = new JPanel();
		panel_LogicClock = new JPanel();
		scrollPane_1 = new JScrollPane();
		list_MessageEvent = new JList<String>();
		scrollPane = new JScrollPane();
		list_LogicClock = new List();
		list_LogicClock.setPreferredSize(new Dimension(10, 10));
		lb_ServerInfoIP = new JLabel("IP Address");
		lb_ServerInfoHostname = new JLabel("Hostname");
		lb_ServerInfoPort = new JLabel("Port");
		btn_MessageToServer2 = new JButton("Server 2");
		btn_MessageToServer2.setEnabled(false);
		btn_MessageToServer1 = new JButton("Server 1");
		btn_MessageToServer1.setEnabled(false);
		
		btn_MessageToServer3 = new JButton("Selected");
		btn_MessageToServer3.setEnabled(false);
		
		btn_MessageToLocal = new JButton("Local");
		btn_MessageToLocal.setEnabled(false);
		
		lb_StartHostname = new JLabel("Hostname");
		lb_StartPort = new JLabel("Port");
		btn_StartLookup = new JButton("Lookup");
		
		btn_StartInit = new JButton("Init");
		cbBox_StartHostname = new JComboBox<String>();
		cbBox_StartPort = new JComboBox<String>();
	}
	
	void difinitionVar() {	
		setResizable(false);
		setBackground(UIManager.getColor("ToolBar.floatingForeground"));
		setTitle("Lamport Server");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 639, 456);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(51, 51, 51));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		lb_Start.setForeground(UIManager.getColor("ToolBar.foreground"));
		lb_Start.setBackground(new Color(255, 255, 255));
		lb_Start.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Start port & Lookup Server ", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		panel_ListServer.setBackground(new Color(255, 255, 255));
		panel_ListServer.setForeground(UIManager.getColor("Panel.foreground"));
		panel_ListServer.setBorder(new TitledBorder(null, "Servers in the system", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_ServerInfo.setBackground(new Color(255, 255, 255));
		panel_ServerInfo.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Server Infomation", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		panel_MessageTo.setBackground(new Color(255, 255, 255));
		panel_MessageTo.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Message to", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		panel_MessageEvent.setBackground(new Color(255, 255, 255));
		panel_MessageEvent.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Message event", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		panel_LogicClock.setBackground(new Color(255, 255, 255));
		panel_LogicClock.setBorder(new TitledBorder(null, "Logic Clock ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_ServerInfo, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lb_Start, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(panel_LogicClock, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
										.addComponent(panel_ListServer, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(panel_MessageEvent, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
										.addComponent(panel_MessageTo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
					.addGap(2))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lb_Start, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_ListServer, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_MessageEvent, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel_LogicClock, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_MessageTo, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_ServerInfo, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
		);
		panel_MessageEvent.setLayout(null);
		
		scrollPane_1.setBounds(12, 23, 367, 86);
		panel_MessageEvent.add(scrollPane_1);
		
		list_MessageEvent.setForeground(Color.WHITE);
		list_MessageEvent.setBackground(new Color(0, 102, 255));
		scrollPane_1.setViewportView(list_MessageEvent);
		panel_LogicClock.setLayout(null);
		
		scrollPane.setBounds(10, 20, 208, 103);
		panel_LogicClock.add(scrollPane);
		
		list_LogicClock.setForeground(Color.WHITE);
		list_LogicClock.setBackground(new Color(0, 102, 255));
		scrollPane.setViewportView(list_LogicClock);
		panel_ServerInfo.setLayout(null);
		
		
		lb_ServerInfoIP.setBounds(12, 29, 85, 15);
		panel_ServerInfo.add(lb_ServerInfoIP);
		
		ttf_ServerInfoIP = new JTextField();
		ttf_ServerInfoIP.setFont(new Font("Dialog", Font.PLAIN, 12));
		ttf_ServerInfoIP.setHorizontalAlignment(SwingConstants.CENTER);
		ttf_ServerInfoIP.setForeground(Color.RED);
		ttf_ServerInfoIP.setEditable(false);
		ttf_ServerInfoIP.setBounds(93, 27, 114, 19);
		panel_ServerInfo.add(ttf_ServerInfoIP);
		ttf_ServerInfoIP.setColumns(10);
		
		lb_ServerInfoHostname.setBounds(234, 29, 85, 15);
		panel_ServerInfo.add(lb_ServerInfoHostname);
		
		ttf_ServerInfoHostname = new JTextField();
		ttf_ServerInfoHostname.setHorizontalAlignment(SwingConstants.CENTER);
		ttf_ServerInfoHostname.setForeground(Color.RED);
		ttf_ServerInfoHostname.setEditable(false);
		ttf_ServerInfoHostname.setBounds(312, 27, 114, 19);
		panel_ServerInfo.add(ttf_ServerInfoHostname);
		ttf_ServerInfoHostname.setColumns(10);
		
		lb_ServerInfoPort.setBounds(467, 29, 35, 15);
		panel_ServerInfo.add(lb_ServerInfoPort);
		
		ttf_ServerInfoPort = new JTextField();
		ttf_ServerInfoPort.setHorizontalAlignment(SwingConstants.CENTER);
		ttf_ServerInfoPort.setForeground(Color.RED);
		ttf_ServerInfoPort.setEditable(false);
		ttf_ServerInfoPort.setBounds(502, 27, 102, 19);
		panel_ServerInfo.add(ttf_ServerInfoPort);
		ttf_ServerInfoPort.setColumns(10);
		panel_MessageTo.setLayout(null);
		
		btn_MessageToServer2.setForeground(new Color(255, 255, 255));
		btn_MessageToServer2.setBackground(new Color(51, 51, 51));
		
		btn_MessageToServer2.setBounds(45, 84, 117, 25);
		panel_MessageTo.add(btn_MessageToServer2);
		
		btn_MessageToServer1.setForeground(new Color(255, 255, 255));
		btn_MessageToServer1.setBackground(new Color(51, 51, 51));
		btn_MessageToServer1.setBounds(45, 43, 117, 25);
		panel_MessageTo.add(btn_MessageToServer1);
		
		btn_MessageToServer3.setBackground(new Color(51, 51, 51));
		btn_MessageToServer3.setForeground(new Color(255, 255, 255));
		btn_MessageToServer3.setBounds(233, 43, 117, 25);
		panel_MessageTo.add(btn_MessageToServer3);
		
		btn_MessageToLocal.setBackground(new Color(51, 51, 51));
		btn_MessageToLocal.setForeground(new Color(255, 255, 255));
		btn_MessageToLocal.setBounds(233, 84, 117, 25);
		panel_MessageTo.add(btn_MessageToLocal);
		panel_ListServer.setLayout(new CardLayout(0, 0));	
		
		list_ServerList.setForeground(Color.WHITE);
		list_ServerList.setToolTipText("");
		list_ServerList.setValueIsAdjusting(true);
		list_ServerList.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list_ServerList.setBackground(new Color(0, 102, 255));
		panel_ListServer.add(list_ServerList, "name_24872323434441");
		lb_Start.setLayout(null);
		
		lb_StartHostname.setBounds(12, 33, 83, 15);
		lb_Start.add(lb_StartHostname);
		
		lb_StartPort.setBounds(231, 33, 37, 15);
		lb_Start.add(lb_StartPort);
		
		btn_StartLookup.setEnabled(true);
		btn_StartLookup.setBounds(400, 28, 98, 25);
		lb_Start.add(btn_StartLookup);
		
		btn_StartInit.setEnabled(false);
		btn_StartInit.setBounds(510, 28, 92, 25);
		lb_Start.add(btn_StartInit);
		
		cbBox_StartHostname.setModel(new DefaultComboBoxModel<String>(new String[] {"localhost", "asdasd", "asdasdas"}));
		cbBox_StartHostname.setEditable(true);
		cbBox_StartHostname.setBounds(95, 28, 118, 24);
		lb_Start.add(cbBox_StartHostname);
		
		cbBox_StartPort.setModel(new DefaultComboBoxModel<String>(new String[] {"3500", "3510", "3520"}));
		cbBox_StartPort.setEditable(true);
		cbBox_StartPort.setBounds(269, 28, 92, 24);
		lb_Start.add(cbBox_StartPort);
		contentPane.setLayout(gl_contentPane);
		
		//scrollPane.setViewportView(list_LogicClock);
		
	}
	
	String getLogicNumString(int index, int logicNum)
	{
		/*
		"index     |     value"
		"0         |         9"
		"07        |        12"
		*/
		String strIndex = "";
		String strlogicNum = "";
		if ((index / 10) == 0)
		{
			strIndex = index + "           ";
		}
		else 
		{
			strIndex = index + "         ";
		}
		
		if ((logicNum / 10) == 0)
		{
			strlogicNum = "           " + logicNum;
		}
		else 
		{
			strlogicNum = "         " + logicNum;
		}
		
		return strIndex + "|" + strlogicNum;
 	}
	
	void updateServers (String port) {
		System.out.println("UpdateServers!");
		addPort(port);
		
		// Update List_Server
		list_ServerList.removeAll();
		System.out.println("removeAll");
		int size = 0;
		for (int i = 0; i < 5; i++)
		{
			if (otherServer[i] != 0) {
				size++;
			}
		}
		//set model Jlist
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (int i = 0; i < size; i++)
		{
			System.out.println("add " + otherServer[i]);
			model.addElement("localhost:" + Integer.toString(otherServer[i]));
		}
		list_ServerList.setModel(model);
	}
	
	void addPort(String port)
	{
		for (int i = 0; i < 5; i++)
		{
			if (otherServer[i] == Integer.parseInt(port))
			{
				return;
			}
			if (otherServer[i] == 0)
			{
				otherServer[i] = Integer.parseInt(port);
				return;
			}
		}
		
		int[] array = { otherServer[0], otherServer[1], otherServer[2], otherServer[3], otherServer[4] };
		otherServer[4] = Integer.parseInt(port);
		for (int i = 0; i < 4; i++)
		{
			otherServer[i] = array[i + 1];
		}
	}
	
	private void setNextLogicClock(int logicNum) {
		// TODO Auto-generated method stub
		// lay size list -> tang index len 1 -> them trang thai logic
		
		int listIndex = list_LogicClock.getItemCount();
		String strLogic = getLogicNumString(--listIndex, logicNum);
		list_LogicClock.add(strLogic);
		
		//set ScrollPane auto roll
		list_LogicClock.select(++listIndex);
	}
	
	protected int getLogicClock() throws RemoteException {
		// TODO Auto-generated method stub
		return logicNum;
	}

	protected void increaseLogic() throws RemoteException {
		// TODO Auto-generated method stub
		logicNum += 1;
	}
	
	protected boolean Lookup(String IP, String port) {
		try {
			String str = "rmi://" + IP + ":" + port + "/core";
			RMIInterface myRMI = (RMIInterface) Naming.lookup(str);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("Lookup false");
			return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Lookup false");
			return false;
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Lookup false");
			return false;
		}
		System.out.println("Lookup true");
	return true;
	}
	
	protected void deliverTo(String IP, int port, int logicClock) throws MalformedURLException, RemoteException, NotBoundException {
		String nameLookup = "rmi://" + IP + ":" + port + "/core";
		RMIInterface deliver = (RMIInterface) Naming.lookup(nameLookup);
		deliver.status(ttf_ServerInfoPort.getText(), logicClock);
	}
	
	private void addEventToMessageEvent(String typeEvent, int port) {
		String mess = "";
		if (typeEvent.equals("RECEIVE")) {
			mess = "RECEIVE from localhost:" + port;
		}
		else {
			mess = "SEND to localhost:" + port;
		}
		setListMessageEvent(mess);
	}
	
	private void addEventToMessageEvent() {
		String mess = "LOCAL acttion from localhost";
		setListMessageEvent(mess);
	}
	
	private void setListMessageEvent(String var) {
		int index = model_ListMessage.getSize() + 1;
		String element = "";
		if (index > 9)
			element = index + "  -  " + var;
		else 
			element = index + "    -  " + var; 

		model_ListMessage.addElement(element);
		
		list_MessageEvent.setModel(model_ListMessage);
	}
}
