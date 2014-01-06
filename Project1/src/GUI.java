import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;


/**
 * This is the GUI for the path finder project.
 * @author Yao Hong Chun
 *
 */
public class GUI {
	private ExecutorService pool; //The pool of threads.

	private JFrame frmPathfinder;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField directoryField;
	private Graph graph;
	private JTextPane textPane;
	private JLabel pathsFoundLbl;
	private JLabel timeTakenLbl;
	private JLabel nodesLbl;
	private JLabel goalLbl;
	private JLabel startLbl;
	private JLabel edgesLbl;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmPathfinder.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		pool = Executors.newFixedThreadPool(1);
		initialize();
		graph= new Graph(this);
	}
	
	public GUI(Graph g){
		initialize();
		graph = g;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPathfinder = new JFrame();
		frmPathfinder.setTitle("PathFinder");
		frmPathfinder.setBounds(100, 100, 642, 313);
		frmPathfinder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPathfinder.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(149, 44, 467, 232);
		frmPathfinder.getContentPane().add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JRadioButton rdbtnSearchAllPaths = new JRadioButton("Search all paths");
		buttonGroup.add(rdbtnSearchAllPaths);
		rdbtnSearchAllPaths.setBounds(6, 7, 137, 23);
		frmPathfinder.getContentPane().add(rdbtnSearchAllPaths);
		
		JRadioButton rdbtnSearchSinglePath = new JRadioButton("Search single path");
		buttonGroup.add(rdbtnSearchSinglePath);
		rdbtnSearchSinglePath.setBounds(6, 33, 137, 23);
		frmPathfinder.getContentPane().add(rdbtnSearchSinglePath);
		
		JButton btnNewButton = new JButton("Launch");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pool.submit(graph);
			}
		});
		btnNewButton.setBounds(6, 63, 89, 23);
		frmPathfinder.getContentPane().add(btnNewButton);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textPane.setText("");
			}
		});
		btnClear.setBounds(6, 212, 89, 23);
		frmPathfinder.getContentPane().add(btnClear);
		
		pathsFoundLbl = new JLabel("Paths found:");
		pathsFoundLbl.setBounds(16, 98, 123, 14);
		frmPathfinder.getContentPane().add(pathsFoundLbl);
		
		timeTakenLbl = new JLabel("Time Taken:");
		timeTakenLbl.setBounds(16, 112, 123, 14);
		frmPathfinder.getContentPane().add(timeTakenLbl);
		
		nodesLbl = new JLabel("Nodes:");
		nodesLbl.setBounds(16, 174, 123, 14);
		frmPathfinder.getContentPane().add(nodesLbl);
		
		edgesLbl = new JLabel("Edges:");
		edgesLbl.setBounds(16, 187, 123, 14);
		frmPathfinder.getContentPane().add(edgesLbl);
		
		startLbl = new JLabel("Start:");
		startLbl.setBounds(16, 137, 123, 14);
		frmPathfinder.getContentPane().add(startLbl);
		
		goalLbl = new JLabel("Goal(s):");
		goalLbl.setBounds(16, 149, 123, 14);
		frmPathfinder.getContentPane().add(goalLbl);
		
		JButton quitBtn = new JButton("Quit");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		quitBtn.setBounds(6, 246, 89, 23);
		frmPathfinder.getContentPane().add(quitBtn);
		
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				graph.chooseGraph();
			}
		});
		btnSelectFile.setBounds(149, 7, 123, 23);
		frmPathfinder.getContentPane().add(btnSelectFile);
		
		directoryField = new JTextField();
		directoryField.setBounds(282, 8, 207, 20);
		frmPathfinder.getContentPane().add(directoryField);
		directoryField.setColumns(10);
		
		JButton btnParseGraph = new JButton("Parse Graph");
		btnParseGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				graph.parseGraph(directoryField.getText());
			}
		});
		btnParseGraph.setBounds(499, 7, 117, 23);
		frmPathfinder.getContentPane().add(btnParseGraph);
	}

	//Printing.
	public void println(String text){
		textPane.setText(textPane.getText() +text+"\n");
		textPane.selectAll();
		System.out.println(text);
	}
	public void print(String text){
		textPane.setText(textPane.getText() +text);
		textPane.selectAll();
		System.out.print(text);
	}
	
	public void setDirectory(String text){
		directoryField.setText(text);
	}

	
	//LABELS
	public void setStart(String s){
		startLbl.setText("Start: "+s);
	}

	public void setGoal(String s){
		goalLbl.setText(s);
	}
	
	public void updatePaths(String p){
		pathsFoundLbl.setText("Paths found: "+p);
	}
	
	
	public void updateTime(String t){
		timeTakenLbl.setText("Time taken: " + t+"ms");
	}
	
	public void setNodes(String n){
		nodesLbl.setText("Nodes: " +n);
	}
	
	public void setEdges(String e){
		edgesLbl.setText("Edges: " +e);
	}
	
	public int getTask(){
		String s = getSelectedButtonText(buttonGroup);
		if(s.equals("Search all paths")){
			return 0;
		}
		else{
			return 1;
		}
	}
	
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
	
	//GETTERS
	protected JTextPane getTextPane() {
		return textPane;
	}
	protected JLabel getPathsFoundLbl() {
		return pathsFoundLbl;
	}
	protected JLabel getTimeTakenLbl() {
		return timeTakenLbl;
	}
	protected JLabel getNodesLbl() {
		return nodesLbl;
	}
	protected JLabel getGoalLbl() {
		return goalLbl;
	}
	protected JLabel getStartLbl() {
		return startLbl;
	}
	protected JLabel getEdgesLbl() {
		return edgesLbl;
	}
}
