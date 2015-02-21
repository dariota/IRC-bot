package Core;

import java.util.Scanner;

public class CommandLineInput implements Runnable {

	@Override
	public void run() {
		Scanner s = new Scanner(System.in);
		while(true) {
			String input = s.nextLine();
			if (input.equalsIgnoreCase("!!!")) break;
			if (input.equalsIgnoreCase("garbage")) {
				System.gc();
				continue;
			}
			MainFrame.toRun.add("[RUN]:" + input);
		}
		s.close();
	}

}
