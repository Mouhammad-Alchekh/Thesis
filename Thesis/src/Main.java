import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

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

	// This method gets the schemas automatically from a the gui interface.
	public static ArrayList<Schema> getSchemas4gui(String insertedSchemas) {
		ArrayList<Schema> result = new ArrayList<Schema>();

		// Tokenize the inserted schemas as a string and convert it to a list in which
		// each line is a string element in this list.
		ArrayList<String> lines = new ArrayList<String>();
		StringTokenizer st1 = new StringTokenizer(insertedSchemas, "\n)");
		while (st1.hasMoreTokens())
			lines.add(st1.nextToken());

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			// replace() doesn't modify the old string. it creates a new one because String
			// is Immutable.
			line = line.replace('(', ' ');
			line = line.replace(',', ' ');
			line = line.replace(')', ' ');

			ArrayList<String> tokens = new ArrayList<String>();
			StringTokenizer st2 = new StringTokenizer(line, " ");
			while (st2.hasMoreTokens())
				tokens.add(st2.nextToken());

			ArrayList<String> attributes = new ArrayList<String>();
			for (int j = 2; j < tokens.size(); j++)
				attributes.add(tokens.get(j));

			if (!tokens.isEmpty()) {
				Schema schema = new Schema(tokens.get(0), tokens.get(1), attributes);
				result.add(schema);
			}
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

	// This method gets the schemas manually from the user using the console.
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
		// create a panel, which is a container that adds object "layout" on the frame
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
		frame.getContentPane().add(scroll);

		schemas = new JTextArea();
		schemas.setMargin(new Insets(10, 10, 10, 10));
		schemas.setFont(new Font("Calibri", Font.BOLD, 14));
		schemas.setForeground(new Color(160, 0, 0));
		schemas.setBackground(new Color(210, 210, 210));
		scroll2 = new JScrollPane(schemas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll2.setBounds(690, 70, 500, 230);
		frame.getContentPane().add(scroll2);

		output = new JTextArea();
		output.setMargin(new Insets(10, 10, 10, 10));
		output.setFont(new Font("Arial", Font.BOLD, 12));
		output.setBackground(new Color(210, 210, 210));
		scroll3 = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		JScrollPane scroll = new JScrollPane(sqlText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll3.setBounds(60, 380, 700, 360);
		frame.getContentPane().add(scroll3);

		// ==================================================================

		// To put the panel on the frame.
		frame.getContentPane().add(panel);

//	    LayoutManager layout = new FlowLayout();  
		panel.setLayout(null);
		panel.setBackground(new Color(50, 78, 78));
//		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 10));
//		panel.setLayout(new GridLayout(0, 1));

		// =============================

		sqlInput = new JLabel("SQL Input", SwingConstants.CENTER);
		sqlInput.setBorder(new LineBorder(Color.BLACK, 2, false));
		sqlInput.setFont(new Font("Arial", Font.BOLD, 15)); // font names: Courier - Arial
		sqlInput.setForeground(Color.black);
		sqlInput.setBackground(new Color(204, 204, 179));
		sqlInput.setOpaque(true);
		sqlInput.setBounds(60, 20, 100, 40);
		panel.add(sqlInput);

		schema = new JLabel("Schema", SwingConstants.CENTER);
		schema.setBorder(new LineBorder(Color.BLACK, 2, false));
		schema.setFont(new Font("Arial", Font.BOLD, 15)); // font names: Courier - Arial
		schema.setForeground(Color.black);
		schema.setBackground(new Color(204, 204, 179));
		schema.setOpaque(true);
		schema.setBounds(690, 20, 100, 40);
		panel.add(schema);

		outputLabel = new JLabel("Output", SwingConstants.CENTER);
		outputLabel.setBorder(new LineBorder(Color.BLACK, 2, false));
		outputLabel.setFont(new Font("Arial", Font.BOLD, 15)); // font names: Courier - Arial
		outputLabel.setForeground(Color.black);
		outputLabel.setBackground(new Color(204, 204, 179));
		outputLabel.setOpaque(true);
		outputLabel.setBounds(60, 330, 100, 40);
		panel.add(outputLabel);

		// =============================

		runButton = new JButton("RUN");
		runButton.setFont(new Font("Arial", Font.BOLD, 18));
		runButton.setBorder(new LineBorder(Color.BLACK, 3, false));
		runButton.setBackground(new Color(194, 194, 163));
		runButton.setBounds(940, 640, 140, 60);
		runButton.addActionListener(new Main());
		panel.add(runButton);

		printTransactions = new JCheckBox("Print Transactions");
		printTransactions.setFont(new Font("Arial", Font.BOLD, 18));
		printTransactions.setBackground(new Color(178, 178, 102));
		printTransactions.setBounds(870, 400, 280, 60);
		panel.add(printTransactions);

		printDetails = new JCheckBox("Print Details");
		printDetails.setFont(new Font("Arial", Font.BOLD, 18));
		printDetails.setBackground(new Color(198, 140, 83));
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

	public static void runInGUIv2() {

		// create the frame or the window of the gui.
		JFrame frame = new JFrame();
		// create a panel, which is a container that adds object "layout" on the frame
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
//        scroll.setBounds(60, 70, 500, 230);
//        frame.getContentPane().add(scroll);

		schemas = new JTextArea();
		schemas.setMargin(new Insets(10, 10, 10, 10));
		schemas.setFont(new Font("Calibri", Font.BOLD, 14));
		schemas.setForeground(new Color(160, 0, 0));
		schemas.setBackground(new Color(210, 210, 210));
		scroll2 = new JScrollPane(schemas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scroll2.setBounds(690, 70, 500, 230);
//        frame.add(scroll2);

		output = new JTextArea();
		output.setMargin(new Insets(10, 10, 10, 10));
		output.setFont(new Font("Arial", Font.BOLD, 12));
		output.setBackground(new Color(210, 210, 210));
		scroll3 = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		JScrollPane scroll = new JScrollPane(sqlText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scroll3.setBounds(60, 380, 700, 360);
//        frame.add(scroll3);

		// ==================================================================
		// To put the panel on the frame.
//        frame.add(panel);
//	    LayoutManager layout = new FlowLayout();  
//        panel.setLayout(null);
		panel.setBackground(new Color(50, 78, 78));
//        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
//		panel.setLayout(new GridLayout(0, 1));

		// =============================
		sqlInput = new JLabel("SQL Input", SwingConstants.CENTER);
		sqlInput.setBorder(new LineBorder(Color.BLACK, 2, false));
		sqlInput.setFont(new Font("Arial", Font.BOLD, 15));
		sqlInput.setForeground(Color.black);
		sqlInput.setBackground(new Color(204, 204, 179));
		sqlInput.setOpaque(true);
//        
		sqlInput.setPreferredSize(new Dimension(100, 40));
		sqlInput.setMinimumSize(new Dimension(100, 40));
		sqlInput.setMaximumSize(new Dimension(100, 40));
//        sqlInput.setBounds(60, 20, 100, 40);
//        panel.add(sqlInput);

		schema = new JLabel("Schema", SwingConstants.CENTER);
		schema.setBorder(new LineBorder(Color.BLACK, 2, false));
		schema.setFont(new Font("Arial", Font.BOLD, 15));
		schema.setForeground(Color.black);
		schema.setBackground(new Color(204, 204, 179));
		schema.setOpaque(true);
//        
		schema.setPreferredSize(new Dimension(100, 40));
		schema.setMinimumSize(new Dimension(100, 40));
		schema.setMaximumSize(new Dimension(100, 40));
//        schema.setBounds(690, 20, 100, 40);
//        panel.add(schema);

		outputLabel = new JLabel("Output", SwingConstants.CENTER);
		outputLabel.setBorder(new LineBorder(Color.BLACK, 2, false));
		outputLabel.setFont(new Font("Arial", Font.BOLD, 15));
		outputLabel.setForeground(Color.black);
		outputLabel.setBackground(new Color(204, 204, 179));
		outputLabel.setOpaque(true);
//        
		outputLabel.setPreferredSize(new Dimension(100, 40));
		outputLabel.setMinimumSize(new Dimension(100, 40));
		outputLabel.setMaximumSize(new Dimension(100, 40));
//        outputLabel.setBounds(60, 330, 100, 40);
//        panel.add(outputLabel);

		// =============================
		runButton = new JButton("RUN");
		runButton.setFont(new Font("Arial", Font.BOLD, 18));
		runButton.setBorder(new LineBorder(Color.BLACK, 3, false));
		runButton.setBackground(new Color(194, 194, 163));
//        runButton.setBounds(940, 640, 140, 60);
//        runButton.addActionListener(new Main());
//        panel.add(runButton);
		runButton.setPreferredSize(new Dimension(140, 60));

		printTransactions = new JCheckBox("Print Transactions");
		printTransactions.setFont(new Font("Arial", Font.BOLD, 18));
		printTransactions.setBorder(new LineBorder(Color.BLACK, 3, false));
		printTransactions.setBackground(new Color(178, 178, 102));
//        
		printTransactions.setPreferredSize(new Dimension(280, 60));

//        printTransactions.setBounds(870, 400, 280, 60);
//        panel.add(printTransactions);
		printDetails = new JCheckBox("Print Details");
		printDetails.setFont(new Font("Arial", Font.BOLD, 18));
		printDetails.setBorder(new LineBorder(Color.BLACK, 3, false));
		printDetails.setBackground(new Color(198, 140, 83));
//        printDetails.setBounds(870, 520, 280, 60);
//        panel.add(printDetails);

		// To add a shadow to the check boxs.
//        shadow1 = new JLabel("", SwingConstants.CENTER);
//        shadow1.setBorder(new LineBorder(Color.BLACK, 4, false));
//        shadow1.setBounds(872, 402, 282, 62);
//        panel.add(shadow1);

//        shadow2 = new JLabel("", SwingConstants.CENTER);
//        shadow2.setBorder(new LineBorder(Color.BLACK, 4, false));
//        shadow2.setBounds(872, 522, 282, 62);
//        panel.add(shadow2);

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(60, 60, 60).addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(outputLabel).addGap(450, 450, 450))
						.addGroup(layout.createSequentialGroup().addGroup(layout
								.createParallelGroup().addGroup(layout.createSequentialGroup()
										.addGroup(layout
												.createParallelGroup().addComponent(scroll).addComponent(sqlInput))
										.addGap(130, 130, 130)
										.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(schema)
												.addComponent(scroll2)))
								.addGroup(layout.createSequentialGroup().addComponent(scroll3).addGap(110, 110, 110)
										.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(printTransactions)
												.addComponent(printDetails)
												.addComponent(runButton, 140, 140, 140))
										.addGap(40, 40, 40)))
								.addGap(65, 65, 65)))));

		layout.linkSize(SwingConstants.HORIZONTAL, new Component[] { printDetails, printTransactions });
		

		layout.setVerticalGroup(layout.createParallelGroup().addGroup(layout.createSequentialGroup()
				.addGap(20, 20, 20)
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(schema)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(scroll2, 230, 230, 230))
						.addGroup(layout.createSequentialGroup().addComponent(sqlInput)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(scroll, 230, 230, 230)))
				.addGap(32, 32, 32).addComponent(outputLabel).addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(scroll3)
						.addGroup(layout.createSequentialGroup().addGap(20, 20, 20)
								.addComponent(printTransactions)
								.addGap(60, 60, 60).addComponent(printDetails).addGap(60, 60, 60)
								.addComponent(runButton)))
				.addGap(50, 50, 50)));

		layout.linkSize(SwingConstants.VERTICAL, new Component[] { printDetails, printTransactions, runButton });

		frame.getContentPane().add(panel, BorderLayout.CENTER);

//        frame.pack();

		// =============================
		// To set the window to be visible and in focus.
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long startTime = System.nanoTime();
		String result = "";

		// ================ Getting the entered SQL code & Schemas =================

		String sqlCode = "SELECT * FROM table;\n";
		// To make sure that the first line is a correct SQL code.
		// This will help creating the ParseTree in case the user started the code with
		// something that is not SQL. which in turn, will help reading all SQL codes
		// after this non SQL input.
		sqlCode += sqlText.getText();
		String insertedSchemas = schemas.getText();

		// =========================================================================

		ArrayList<Schema> schemas = getSchemas4gui(insertedSchemas);
		ResultContainer resultContainer = Translator.translate4gui(sqlCode, schemas);
		ArrayList<Transaction> example = resultContainer.getResult();
		boolean hasWarning = resultContainer.getHasWarning();
		String warningInfo = resultContainer.getWarningInfo();

		if (example.isEmpty())
			result += "Empty Or Wrong Input ! \n";
		else {
			if (hasWarning) {
				result += "--------------------- \n";
				result += "|  WARNING !  |\n";
				result += "--------------------- \n";
				result += " \n";
				result += warningInfo;
				result += "\n";
				if (!printTransactions.isSelected())
					result += "======================== \n";
			}
			if (printTransactions.isSelected())
				result += getTransactions2Print(example);
			if (printDetails.isSelected())
				result += getDetails2Print(example, schemas);

			if (hasWarning) {
				result += "\n";
				result += " The Algorithm cannot be run ! \n";
				result += " Please fix the warnings first. \n";
				result += "\n";
				result += "======================== \n";
			} else {
				result += Tools.DecideIsolationLevel2(example);
			}
		}
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		result += "\n";
		result += "Elapsed Time = " + Long.toString(elapsedTime) + " ns";
		output.setText(result);
	}

	public static void main(String[] args) throws FileNotFoundException {

//		runInConsole();

		runInGUI();

//		runInGUIv2();
	}
}
