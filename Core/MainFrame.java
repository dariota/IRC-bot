package Core;
import java.util.ArrayList;
import java.util.HashMap;

import Features.PrivMsg;

public class MainFrame {
	
	public static ArrayList<String> toRun = new ArrayList<>();
	public static HashMap<String, Integer> peopleHost = new HashMap<>();
	public static ArrayList<String> processReplies = new ArrayList<>();
	public static ArrayList<String> putOffReplies = new ArrayList<>();
	public static HashMap<String, String> nickToHost = new HashMap<>(); 
	public static boolean run = true;
	public static final long SLEEP_TIME = 100;
	public static String errorDir;
	public static String nick;
	public static String ownerNick;
	public static String ownerHost;

    public static void main(String[] args) throws Exception {//TODO security level persistence, reddit load
    														//label and unload modules, document ExecuteCommands, 
    													   //wrapper for writer with throttling, error handler
    													  //handle nick changes
    	// The server to connect to and our details.
        String server = args[0];
        nick = args[1];
        String login = args[2];
        String pass = args[3];
        ownerNick = args[4];
        ownerHost = args[5];
        try {
        	errorDir = args[6];
        } catch (ArrayIndexOutOfBoundsException e) {
        	errorDir = "errors/";
        }
        
        peopleHost.put(ownerHost, 11);
        
        getConnected(server, nick, login, pass);
        PrivMsg.sendMessage(ownerNick, "I'm online.");
        Thread t = new Thread(new CommandLineInput());
        t.start();
        Thread t2 = new Thread(new ProcessManager());
        t2.start();
    }
    
    public static void getConnected(String server, String nick, String login, String pass) {
    	try {
    		new Connection(server, nick, login, pass);
    	} catch (Exception e) {
    		e.printStackTrace();
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}
    		getConnected(server, nick, login, pass);
    	}
    }

}