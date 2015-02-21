package Core;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class PongAndRead implements Runnable {
	
	BufferedWriter writer;
	BufferedReader reader;

	public PongAndRead(BufferedWriter writer, BufferedReader reader) {
		this.reader = reader;
		this.writer = writer;
	}
	
	@Override
	public void run() {
		String line = null;
		Parser parser = new Parser();
		Thread parseThread = new Thread(parser);
		parseThread.start();
		try {
		 while ((line = reader.readLine()) != null && MainFrame.run) {
	            if (line.startsWith("PING ")) {
	                // We must respond to PINGs to avoid being disconnected.
	                writer.write("PONG " + line.substring(5) + "\r\n");
	                writer.flush();
	            } else {
	                // Add to the parser's backlog
	                parser.toParse.add(line);
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
			MainFrame.run = false;
			System.exit(0);
		}
		MainFrame.run = false;
	}	
	
}
