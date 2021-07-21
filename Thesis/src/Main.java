import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main implements ActionListener {

	private static JTextArea sqlText;
	private static JScrollPane scroll;
	private static JTextArea schemas;
	private static JScrollPane scroll2;
	private static JTextArea output;
	private static JScrollPane scroll3;
	private static JLabel sqlInput;
	private static JLabel schema;
	private static JLabel outputLabel;
	private static JButton runButton;
	private static JCheckBox printTransactions;
	private static JCheckBox printDetails;
	private static JLabel shadow1;
	private static JLabel shadow2;

	public static void printTransactions(ArrayList<Transaction> t) {
		System.out.println("-----------------------------------");
		System.out.println("The used set of transactions is:");
		System.out.println();
		if (t.size() == 0)
			System.out.println("The set of transaction is empty");
		for (int i = 0; i < t.size(); i++) {
			t.get(i).Print();
		}
		System.out.println();
		System.out.println("-----------------------------------");
	}

	// This method is exactly the same as printTransactions() but it returns the
	// printed result as a string instead of printing it on the console.
	public static String getTransactions2Print(ArrayList<Transaction> t) {
		String result = "";

		result += "--------------------------------------------- \n";
		result += "The used set of transactions is: \n";
		result += " \n";
		if (t.size() == 0) {
			return "The set of transaction is empty \n";
		}

		for (int i = 0; i < t.size(); i++) {
			result += t.get(i).getTransaction2Print();
		}
		result += " \n";
		result += "--------------------------------------------- \n";

		return result;
	}

	public static void printDetails(ArrayList<Transaction> t, ArrayList<Schema> schemas) {
		System.out.println("The entered schemas are: ");
		System.out.println();
		for (int i = 0; i < schemas.size(); i++) {
			schemas.get(i).Print();
		}
		System.out.println();
		System.out.println();
		System.out.println("The used set of transactions is:");
		System.out.println();
		System.out.println("======================");
		if (t.size() == 0)
			System.out.println("The set of transaction is empty");

		for (int i = 0; i < t.size(); i++) {
			t.get(i).Print();
			System.out.println("This transaction consists of the following operations:");
			System.out.println();
			ArrayList<Operation> operations = t.get(i).getOperations();

			for (int j = 0; j < operations.size(); j++) {
				Operation op = operations.get(j);
				int opNumber = j + 1;
				System.out.println("operation number: " + opNumber);
				op.print();
				String opType = "Read";
				if (op.getType() == 'W')
					opType = "Write";
				System.out.println("Operation type: " + opType);
				System.out.println("Operation ID: " + op.getId());
				System.out.println("Object code: " + op.getObject());
				System.out.println("The details of the real object are:");
				op.getObj().print();
				System.out.println();

			}
			System.out.println("======================");
		}
	}

	// This method is exactly the same as printDetails() but it returns the printed
	// result as a string instead of printing it on the console.
	public static String getDetails2Print(ArrayList<Transaction> t, ArrayList<Schema> schemas) {
		String result = "";

		result += "The entered schemas are:  \n";
		result += " \n";
		for (int i = 0; i < schemas.size(); i++)
			result += schemas.get(i).getSchema2Print();
		result += " \n";
		result += " \n";
		result += "The used set of transactions is: \n";
		result += " \n";
		result += "========================= \n";
		if (t.size() == 0)
			result += "The set of transaction is empty \n";

		for (int i = 0; i < t.size(); i++) {
			result += t.get(i).getTransaction2Print();
			result += "This transaction consists of the following operations: \n";
			result += " \n";
			ArrayList<Operation> operations = t.get(i).getOperations();

			for (int j = 0; j < operations.size(); j++) {
				Operation op = operations.get(j);
				int opNumber = j + 1;
				result += "operation number: " + Integer.toString(opNumber) + " \n";
				result += op.getOperation2Print();
				String opType = "Read";
				if (op.getType() == 'W')
					opType = "Write";
				result += "Operation type: " + opType + " \n";
				result += "Operation ID: " + Integer.toString(op.getId()) + " \n";
				result += "Object code: " + op.getObject() + " \n";
				result += "The details of the real object are: \n";
				result += op.getObj().getObj2Print();
				result += " \n";
			}
			result += "======================== \n";
		}
		return result;
	}

	// This method gets the schemas automatically from a text file.
	public static ArrayList<Schema> getSchemas() throws FileNotFoundException {
		ArrayList<Schema> result = new ArrayList<Schema>();

		// create a file opener to open the schema file.
		File file = new File("./schema.txt");
		// read the input by scanning the file.
		Scanner myScanner = new Scanner(file);

		while (myScanner.hasNextLine()) {
			String line = myScanner.nextLine();
			// replace() doesn't modify the old string. it creates a new one because String
			// is Immutable.
			line = line.replace('(', ' ');
			line = line.replace(',', ' ');
			line = line.replace(')', ' ');

			ArrayList<String> tokens = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreTokens())
				tokens.add(st.nextToken());

			ArrayList<String> attributes = new ArrayList<String>();
			for (int i = 2; i < tokens.size(); i++)
				attributes.add(tokens.get(i));

			if (!tokens.isEmpty()) {
				Schema schema = new Schema(tokens.get(0), tokens.get(1), attributes);
				result.add(schema);
			}
		}
		myScanner.close();
		return result;
	}

	// This method gets the schemas manually from the user.
	public static ArrayList<Schema> getSchemas2() {
		ArrayList<Schema> result = new ArrayList<Schema>();
		boolean finish = false;
		// read the input by scanning the console.
		Scanner myScanner = new Scanner(System.in);

		while (!finish) {
			System.out.println("Enter The Schema name:");
			String name = myScanner.nextLine();
			System.out.println("Enter The Primary Key:");
			String pKey = myScanner.nextLine();

			ArrayList<String> attributes = new ArrayList<String>();
			System.out.println("Enter the first attribute:");
			String att = myScanner.nextLine();
			attributes.add(att);
			while (true) {
				System.out.println("Enter the next attribute/ Enter done to finish this schema:");
				att = myScanner.nextLine();
				if (att.equalsIgnoreCase("done"))
					break;
				attributes.add(att);
			}
			Schema schema = new Schema(name, pKey, attributes);
			result.add(schema);
			System.out.println("Finish Yes/No?");
			String end = myScanner.nextLine();
			if (end.equalsIgnoreCase("yes"))
				finish = true;
		}
		myScanner.close();
		System.out.println();
		System.out.println();
		return result;
	}

	public static void runInConsole() {
		try {
			ArrayList<Schema> schemas = getSchemas();
			ArrayList<Transaction> example = Translator.translate("./input.txt", schemas);

			if (example.isEmpty())
				System.out.println("Empty Or Wrong Input !");
			else {
				printTransactions(example);
				printDetails(example, schemas);
				Tools.DecideIsolationLevel(example);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void runInGUI() {

		// create the frame or the window of the gui.
		JFrame frame = new JFrame();
		// create a panal, which is a container that adds object "layout" on the frame
		JPanel panel = new JPanel();

		frame.setSize(1280, 800);
		// To close the frame properly.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// To set the title
		frame.setTitle("Decide Isolation Level");
//		frame.setForeground(new Color(100, 0, 0));
		// To pack the components within the window based on the component’s preferred
		// sizes.
//		frame.pack();

		// ================ Creating a scrollable text area =================

		sqlText = new JTextArea();
		sqlText.setMargin(new Insets(10, 10, 10, 10));
		sqlText.setFont(new Font("Consolas", Font.BOLD, 13));
		sqlText.setBackground(new Color(210, 210, 210));
		scroll = new JScrollPane(sqlText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		JScrollPane scroll = new JScrollPane(sqlText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(60, 70, 500, 230);
		frame.add(scroll);

		schemas = new JTextArea();
		schemas.setMargin(new Insets(10, 10, 10, 10));
		schemas.setFont(new Font("Calibri", Font.BOLD, 14));
		schemas.setForeground(new Color(160, 0, 0));
		schemas.setBackground(new Color(210, 210, 210));
		scroll2 = new JScrollPane(schemas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll2.setBounds(690, 70, 500, 230);
		frame.add(scroll2);

		output = new JTextArea();
		output.setMargin(new Insets(10, 10, 10, 10));
		output.setFont(new Font("Arial", Font.BOLD, 12));
		output.setBackground(new Color(210, 210, 210));
		scroll3 = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		JScrollPane scroll = new JScrollPane(sqlText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll3.setBounds(60, 380, 700, 360);
		frame.add(scroll3);

		// ==================================================================

		// To put the panel on the frame.
		frame.add(panel);
//	    LayoutManager layout = new FlowLayout();  
		panel.setLayout(null);
		panel.setBackground(new Color(105, 0, 143));
//		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 10));
//		panel.setLayout(new GridLayout(0, 1));

		// =============================

		sqlInput = new JLabel("SQL Input", SwingConstants.CENTER);
		sqlInput.setBorder(new LineBorder(Color.BLACK, 2, false));
		sqlInput.setFont(new Font("Arial", Font.BOLD, 15)); // fornt names: Courier - Arial
		sqlInput.setForeground(Color.black);
		sqlInput.setBackground(new Color(255, 112, 112));
		sqlInput.setOpaque(true);
		sqlInput.setBounds(60, 20, 100, 40);
		panel.add(sqlInput);

		schema = new JLabel("Schema", SwingConstants.CENTER);
		schema.setBorder(new LineBorder(Color.BLACK, 2, false));
		schema.setFont(new Font("Arial", Font.BOLD, 15)); // fornt names: Courier - Arial
		schema.setForeground(Color.black);
		schema.setBackground(new Color(255, 112, 112));
		schema.setOpaque(true);
		schema.setBounds(690, 20, 100, 40);
		panel.add(schema);

		outputLabel = new JLabel("Output", SwingConstants.CENTER);
		outputLabel.setBorder(new LineBorder(Color.BLACK, 2, false));
		outputLabel.setFont(new Font("Arial", Font.BOLD, 15)); // fornt names: Courier - Arial
		outputLabel.setForeground(Color.black);
		outputLabel.setBackground(new Color(255, 112, 112));
		outputLabel.setOpaque(true);
		outputLabel.setBounds(60, 330, 100, 40);
		panel.add(outputLabel);

		// =============================

		runButton = new JButton("RUN");
		runButton.setFont(new Font("Arial", Font.BOLD, 18));
		runButton.setBorder(new LineBorder(Color.BLACK, 3, false));
		runButton.setBackground(new Color(255, 117, 26));
		runButton.setBounds(940, 640, 140, 60);
		runButton.addActionListener(new Main());
		panel.add(runButton);

		printTransactions = new JCheckBox("Print Transactions");
		printTransactions.setFont(new Font("Arial", Font.BOLD, 18));
		printTransactions.setBackground(new Color(7, 230, 185));
		printTransactions.setBounds(870, 400, 280, 60);
		panel.add(printTransactions);

		printDetails = new JCheckBox("Print Details");
		printDetails.setFont(new Font("Arial", Font.BOLD, 18));
		printDetails.setBackground(new Color(51, 204, 255));
		printDetails.setBounds(870, 520, 280, 60);
		panel.add(printDetails);

		// To add a shadow to the check boxs.
		shadow1 = new JLabel("", SwingConstants.CENTER);
		shadow1.setBorder(new LineBorder(Color.BLACK, 4, false));
		shadow1.setBounds(872, 402, 282, 62);
		panel.add(shadow1);

		shadow2 = new JLabel("", SwingConstants.CENTER);
		shadow2.setBorder(new LineBorder(Color.BLACK, 4, false));
		shadow2.setBounds(872, 522, 282, 62);
		panel.add(shadow2);

		// =============================

		// To set the window to be visible and in focus.
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String result = "";

		// ================ Getting the entered SQL code & Schemas =================
		
		String sqlCode = "SELECT * FROM table;\n";
		// To make sure that the first line is a correct SQL code.
		// This will help creating the ParseTree in case the user started the code with
		// something that is not SQL. which in turn, will help reading all SQL codes
		// after this non SQL input.
		sqlCode += sqlText.getText();
		String insertedSchemas = schemas.getText();

		File inputFile = new File("./input.txt");
		try {
			FileWriter fw = new FileWriter(inputFile);
			PrintWriter pWriter = new PrintWriter(fw);
			pWriter.write(sqlCode);
			pWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File schemaFile = new File("./schema.txt");
		try {
			FileWriter fw2 = new FileWriter(schemaFile);
			PrintWriter pWriter2 = new PrintWriter(fw2);
			pWriter2.write(insertedSchemas);
			pWriter2.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// =========================================================================

		try {
			ArrayList<Schema> schemas = getSchemas();
			ArrayList<Transaction> example = Translator.translate("./input.txt", schemas);
			if (example.isEmpty())
				result += "Empty Or Wrong Input !";
			else {
				if (printTransactions.isSelected())
					result += getTransactions2Print(example);
				if (printDetails.isSelected())
					result += getDetails2Print(example, schemas);
				result += Tools.DecideIsolationLevel2(example);
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		output.setText(result);
	}

	public static void main(String[] args) throws FileNotFoundException {

//		runInConsole();

		runInGUI();

	}

}
