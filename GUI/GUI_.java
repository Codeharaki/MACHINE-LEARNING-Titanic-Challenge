/*
 *Author: Haraki Youness
 */
package GUI;

import com.mycompany.train.DAO;
import com.mycompany.train.DecisionTree; 
import java.awt.BorderLayout; 
import java.awt.Dimension;  
import java.awt.GridLayout; 
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.util.Vector; 
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities; 
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author youne
 */
public class GUI_ {
    
    //Attributes
	private JFrame mainFrame;
	private JFrame testFrame;
	
        private JComboBox cmb;
	private JButton createModel;
	private JButton evaluateModel;
	private JButton generateResult; 
        private JTextArea textArea;
        private JLabel label;
        
    //CONSTRUCTOR
	public GUI_() {
		createView();
		createNewTestFrame();
		placeComponents();
		createController();
	}
        private void createNewTestFrame() {
		final int frameWidth = 500;
		final int frameHeight = 400;
		testFrame = new JFrame("Results validation");
		testFrame.setPreferredSize(new Dimension(frameWidth, frameHeight));
	}
        private void createView() {
                String[] petStrings = { "train.csv", "test.csv"};
                cmb = new JComboBox(petStrings);
                cmb.setSelectedIndex(0); 
                textArea = new JTextArea(); 
		mainFrame = new JFrame("Titanic - Machine " +
				"Learning from Disaster");
		mainFrame.setPreferredSize(new Dimension(500,400));
		
		createModel = new JButton("Create model");
		evaluateModel = new JButton("Evaluate model");
                generateResult = new JButton("Generate results");   
                textArea.setFont( textArea.getFont().deriveFont(14f) );
                textArea.append("Titanic- is a ML competition \n\r"
                        + "The work has been done using Decision trees\n\r"
                        + "The submission of this work gave a precision of 0.76555\n\n"
                        + "Steps: \n1) Train the Model \n2) Evaluate the model \n3) Generate results -results.csv-");
                label = new JLabel("Resources");
	}
        private void placeComponents() {
		JPanel p = new JPanel(); {
			p.add(createModel);
			p.add(evaluateModel);
                        p.add(generateResult);
		}
		mainFrame.add(p, BorderLayout.NORTH); 
                JScrollPane p_b = new JScrollPane( textArea );   
                JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,null, p_b );
                mainFrame.add( splitter, BorderLayout.CENTER );
                JPanel p_c = new JPanel(); {
                    p_c.setLayout(new GridLayout(9,1));
                    p_c.add(label);
                    p_c.add(cmb);
                }
                mainFrame.add(p_c, BorderLayout.WEST); 
        }
        
        
        private void createController() {
                DAO dao_Passengers =new DAO(); 
                //------ Train data
                dao_Passengers.Get_train(); 
                //------ Test data
                dao_Passengers.Get_test(); 
                //Decision tree
                DecisionTree D_tree= new DecisionTree();
		// mainFrame
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// testFrame		
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
		createModel.addActionListener((e) -> { 
                    JOptionPane.showMessageDialog(null, "The model is trained",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        D_tree.classify();
                    }catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
                
		evaluateModel.addActionListener((e) -> { 
                    try { 
                        textArea.setText("");  
                        textArea.append(D_tree.evaluate()); 
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
                
                generateResult.addActionListener((ActionEvent e) -> {
                    try {
                        D_tree.generate_result();
                        JOptionPane.showMessageDialog(null, "Assignement results has been generated\n\r"
                                +"Dir: "+ DAO.getRESOURCES_PATH(),
                                "Information", JOptionPane.INFORMATION_MESSAGE);
                    } catch (HeadlessException | IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                });
                
                cmb.addActionListener((ActionEvent event) -> {
                    JComboBox comboBox = (JComboBox) event.getSource();
                    Object selected = comboBox.getSelectedItem();
                    if(selected.toString().equals("train.csv"))
                        csv_table("Train.csv");
                    else if(selected.toString().equals("test.csv"))
                        csv_table("test.csv");
                });
                
	}
        public void csv_table(String FILE_NAME){ 
            //Frame     
            JFrame frame_csv=new JFrame("csv file viewer");
            frame_csv.setSize(700,500);
            frame_csv.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            //Panel
            JPanel jPanel=new JPanel();
            BoxLayout Box_l=new BoxLayout(jPanel,BoxLayout.Y_AXIS);
            jPanel.setLayout(Box_l);
            frame_csv.add(jPanel);
            File csvFile=new File(DAO.getRESOURCES_PATH()+FILE_NAME);
            DefaultTableModel csv_data=new DefaultTableModel();
            InputStreamReader streamReader = null;
            try {
                streamReader = new InputStreamReader(new FileInputStream(csvFile));
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
            CSVParser csvParser = null;
            try {
                csvParser = CSVFormat.DEFAULT.parse(streamReader);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            int index=0;
            for(CSVRecord csvRecord:csvParser){
                if(index==0){
                    for (int i=0; i<csvRecord.size();i++){
                        csv_data.addColumn(csvRecord.get(i)); 
                    }
                    index++;
                }
                else{
                    Vector line=new Vector();
                    for (int i=0; i<csvRecord.size();i++){
                        line.add(csvRecord.get(i)); 
                    }
                    csv_data.addRow(line);
                }
            }
            JTable table=new JTable();
            table.setModel(csv_data);
            JScrollPane scrollPane=new JScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getViewport().add(table);
            jPanel.add(scrollPane); 
            frame_csv.setLocationRelativeTo(null);
            frame_csv.setVisible(true);
        }
            
        public void display() {
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		testFrame.pack();
		testFrame.setLocationRelativeTo(null);
		testFrame.setVisible(false);
	}

        public static void main(String[] args) {
		SwingUtilities.invokeLater(new GUI_()::display);
	}
}
