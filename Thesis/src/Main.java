import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void printTransactions(ArrayList<Transaction> t) {
		for (int i = 0; i < t.size(); i++) {
			t.get(i).Print();
		}
		System.out.println();
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
		return result;
	}

	public static void main(String[] args) {

//		ArrayList<Schema> schemas = getSchemas();
//		for (int i = 0; i < schemas.size(); i++) {
//			schemas.get(i).Print();
//		}

		ArrayList<Transaction> example = Translator.convert("./input.txt");
//		printTransactions(example);
//		Tools.DecideIsolationLevel(example);
		System.out.println("Done");

	}
}
