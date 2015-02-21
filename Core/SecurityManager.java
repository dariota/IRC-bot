package Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SecurityManager {
	
	public static HashMap<String, Integer> commandLevels = new HashMap<>();

	public static void refreshCommands() {
		try {
			Scanner f = new Scanner(new File("commandLevels.txt"));
			String line;
			while ((line = f.nextLine()) != null) {
				commandLevels.put(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]));
			}
			f.close();
		} catch (FileNotFoundException e) {
			ErrorHandler.error(e, "SecMan");
		}
	}

}
