package Core;

import java.io.File;
import java.io.PrintStream;

public class ErrorHandler {
	
	public static void error(Exception e, String errSource) { //TODO errors by folder for each module
		MainFrame.processReplies.add("MSG " + MainFrame.ownerHost + " " + MainFrame.ownerNick + " "
									 + MainFrame.ownerNick + ((char)7) + "Writing an error message.");	
		try {
			int code = 0;
			File dir = new File(MainFrame.errorDir);
			dir.mkdirs();
			File out = new File(MainFrame.errorDir + "/" + errSource + "/" + code + ".txt");
			while (out.exists()) {
				out = new File(MainFrame.errorDir + "/" + errSource + "/" + ++code + ".txt");
			}
			System.out.println("Error in " + errSource + ", writing error file.");
			PrintStream ps = new PrintStream(out);
			e.printStackTrace(ps);
			System.out.println("Wrote error file to " + errSource + code + ".txt");
		} catch (Exception e1) {
			System.out.println("Error writing error file.");
			e1.printStackTrace();
		}
	}

}
