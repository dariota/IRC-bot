package Core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class ProcessManager implements Runnable {
	
	private static Runtime runtime = Runtime.getRuntime();
	private static HashMap<String, ProcessWrapper> processes = new HashMap<>();
	
	public static void initialiseNewModule(String label, String execution) {
		try {
			Process p = runtime.exec(execution);
			processes.put(label, new ProcessWrapper(p));
		} catch (IOException e) {
			char messageSeparator = 7;
			MainFrame.processReplies.add("MSG " + MainFrame.ownerHost + " " + MainFrame.ownerNick + " " 
					+ MainFrame.ownerNick + " " + messageSeparator +
					"Failed to load process " + label + ".");
		}
	}
	
	public static void endModule(String label) {
		char messageSeparator = 7;
		if (processes.containsKey(label)) {
			processes.get(label).p.destroy();
			processes.remove(label);
			MainFrame.processReplies.add("MSG " + MainFrame.ownerHost + " " + MainFrame.ownerNick + " " 
					+ MainFrame.ownerNick + " " + messageSeparator +
					"Unloaded " + label + ".");
		} else {
			MainFrame.processReplies.add("MSG " + MainFrame.ownerHost + " " + MainFrame.ownerNick + " " 
					+ MainFrame.ownerNick + " " + messageSeparator +
					"No such process " + label + " to unload.");
		}
	}
	
	public static void sendLine(String s) {
		s += "\r\n";
		if (processes.size() == 0) return; 
		for (Entry<String, ProcessWrapper> espw : processes.entrySet()) {
			ProcessWrapper pw = espw.getValue();
			try {
				pw.osw.write(s);
				pw.osw.flush();
			} catch (IOException e) {
				for (int j = 0; j < 5; j++) {
					try {
						pw.osw.write(s);
						pw.osw.flush();
						break;
					} catch (IOException e1) {}
				}
				pw.p.destroy();
				processes.remove(espw.getKey());
			}
		}
	}
	
	public static void receiveLines() {
		for (Entry<String, ProcessWrapper> espw : processes.entrySet()) {
			ProcessWrapper pw = espw.getValue();
			try {
				String result = "";
				while ((result = pw.br.readLine()).equals(""));
				MainFrame.processReplies.add(result);
			} catch (IOException e) {
				pw.p.destroy();
				processes.remove(espw.getKey());
				continue;
			}
		}
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		while (MainFrame.run) {
			if ((System.currentTimeMillis() - time) > MainFrame.SLEEP_TIME) {
				receiveLines();
				time = System.currentTimeMillis();
			} else {
				try {
					Thread.sleep(MainFrame.SLEEP_TIME);
				} catch (InterruptedException e) {}
			}
		}
	}

}