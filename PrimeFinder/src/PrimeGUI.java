import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class PrimeGUI extends JFrame {

	private JPanel contentPane;
	private JTextField numberOfThreadsField;
	private JTextField fromField;
	private JTextField toField;
	private JTextPane textPane;
	private JButton launchButton;
	/**
	 * Create the frame.
	 */
	public PrimeGUI() {
		setTitle("Prime Finder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		numberOfThreadsField = new JTextField();
		numberOfThreadsField.setText("4");
		numberOfThreadsField.setBounds(145, 13, 116, 22);
		contentPane.add(numberOfThreadsField);
		numberOfThreadsField.setColumns(10);
		
		JLabel lblNumberOfThreads = new JLabel("Number of Threads:");
		lblNumberOfThreads.setBounds(12, 16, 121, 16);
		contentPane.add(lblNumberOfThreads);
		
		JLabel lblSearchFrom = new JLabel("Search from:");
		lblSearchFrom.setBounds(12, 45, 121, 16);
		contentPane.add(lblSearchFrom);
		
		JLabel lblSearchTo = new JLabel("Search to:");
		lblSearchTo.setBounds(12, 74, 121, 16);
		contentPane.add(lblSearchTo);
		
		fromField = new JTextField();
		fromField.setText("1");
		fromField.setColumns(10);
		fromField.setBounds(145, 42, 116, 22);
		contentPane.add(fromField);
		
		toField = new JTextField();
		toField.setText("100000");
		toField.setColumns(10);
		toField.setBounds(145, 71, 116, 22);
		contentPane.add(toField);
		
		launchButton = new JButton("Launch");
		launchButton.setBounds(12, 103, 97, 25);
		contentPane.add(launchButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 141, 408, 101);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);

		this.setVisible(true);
	}
	
	
	public void report(String words){
		textPane.setText(textPane.getText()+"\n"+words);
		textPane.selectAll();
	}
	public JButton getLaunchButton() {
		return launchButton;
	}
	
	public int getNumberOfThreads(){
		try{
			return Integer.valueOf(numberOfThreadsField.getText());
		}
		catch(NumberFormatException e){this.report("Please enter a valid number of threads: Must be > 0");}
		return 0;
	}
	public int getSearchFrom(){
		try{
			return Integer.valueOf(fromField.getText());
		}
		catch(NumberFormatException e){this.report("Please enter a valid from value: Must be > 0");}
		return 0;
	}
	public int getSearchTo(){
		try{
			return Integer.valueOf(toField.getText());
		}
		catch(NumberFormatException e){this.report("Please enter a valid to value: Must be > from");}
		return 0;
	}
		
}
