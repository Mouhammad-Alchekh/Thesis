import java.util.ArrayList;

public class Schema {
	private String name;
	private String pKey;
	private ArrayList<String> attributes;

	public Schema(String name, String pKey, ArrayList<String> attributes) {
		this.name = name;
		this.pKey = pKey;
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getpKey() {
		return pKey;
	}

	public void setpKey(String pKey) {
		this.pKey = pKey;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}
	
	public void Print() {
		String result = " "; // to combine all operations into this string
		int size = this.attributes.size();
		for (int i = 0; i < size; i++) {
			if (result != " ") // to add a comma between 2 operations
				result += ", ";
			String att = attributes.get(i);
			result += att;
		}
		System.out.println(String.format("%s%s%s%s%s%s", this.name, "(PK=", this.pKey, ",", result, ")"));
	}
}
