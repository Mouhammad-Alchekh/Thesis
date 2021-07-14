import java.util.ArrayList;
import java.util.Scanner;

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

	public static ArrayList<Schema> getSchemas() {
		ArrayList<Schema> result = new ArrayList<Schema>();
		boolean finish = false;
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

	public static void main(String[] args) {
		ArrayList<Schema> schemas = new ArrayList<Schema>();

		ArrayList<String> a1 = new ArrayList<String>();
		a1.add("name");
		a1.add("age");
		a1.add("gender");
		a1.add("salary");
		Schema s1 = new Schema("Employee", "id", a1);

		ArrayList<String> a2 = new ArrayList<String>();
		a2.add("name");
		a2.add("phone");
		a2.add("address");
		Schema s2 = new Schema("Client", "id", a2);

		ArrayList<String> a3 = new ArrayList<String>();
		a3.add("country");
		a3.add("city");
		Schema s3 = new Schema("Branch", "name", a3);

		ArrayList<String> a4 = new ArrayList<String>();
		a4.add("count");
		a4.add("price");
		a4.add("product");
		a4.add("type");
		a4.add("color");
		Schema s4 = new Schema("Inventory", "id", a4);

		schemas.add(s1);
		schemas.add(s2);
		schemas.add(s3);
		schemas.add(s4);

//		ArrayList<Schema> schemas = getSchemas();

		ArrayList<Transaction> example = Translator.translate("./input.txt", schemas);

		printTransactions(example);

		printDetails(example, schemas);

//		Tools.DecideIsolationLevel(example);

//		System.out.println("Done");

	}
}
