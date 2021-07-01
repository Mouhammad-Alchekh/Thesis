import java.util.ArrayList;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public abstract class Translator {
	public static ArrayList<Transaction> convert(String fileName) {
		ArrayList<Transaction> result = new ArrayList<Transaction>();

		
		try {
			// get the input from the file name as a stream of characters. 
			CharStream inputStream = CharStreams.fromFileName(fileName);
			
			// tokenize the input stream using a lexer
			SQLiteLexer sqlLexer = new SQLiteLexer(inputStream);
			
			// create a token stream from a token source whiche is sql lexer
			CommonTokenStream tokenStream = new CommonTokenStream(sqlLexer);
			
			// create a parser from a token stream
			SQLiteParser sqlParser = new SQLiteParser(tokenStream);
			
			// Specify an entry point
//			SQLiteParser.ParseContext context = sqlParser.parse();
			SQLiteParser.Transaction_nameContext context = sqlParser.transaction_name();
			
			// Create our custom Listener
			Listener listener = new Listener();
			// Create the ParseTree walker
			
			ParseTreeWalker walker = new ParseTreeWalker();
			// Walk over the tree
			walker.walk(listener, context);
			
			result = listener.getResult();
						
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
