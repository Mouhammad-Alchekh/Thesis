import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

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

	public static void printDetails(ArrayList<Transaction> t, ArrayList<Schema> schemas) {
		System.out.println("The entered database schemas are: ");
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

	public static void main(String[] args) throws FileNotFoundException {

		ArrayList<Schema> schemas = getSchemas();
		
//		ArrayList<Transaction> example = Translator.translate("./input.txt", schemas);

//		printTransactions(example);

//		printDetails(example, schemas);

//		Tools.DecideIsolationLevel(example);

//		System.out.println("Done");

	}
}
