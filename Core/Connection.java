package Core;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class Connection {
	
	private Thread keepAlive = new Thread();
	private Thread commands = new Thread();
	BufferedReader reader;
	BufferedWriter writer;

	public Connection(String server, String nick, String login, String pass) throws Exception {
		// Connect directly to the IRC server.
        Socket socket = new Socket(server, 6667);
        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream( )));
        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream( )));
        
        // Log on to the server.
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * :" + nick + "\r\n");
        writer.flush();
        
        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null) {
        	System.out.println(line);
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                break;
            }
            else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }
        }
         
        writer.write("PRIVMSG NickServ :IDENTIFY " + nick + " " + pass + "\r\n");
        writer.flush();
        
        PongAndRead par = new PongAndRead(writer, reader);
        keepAlive = new Thread(par);
        keepAlive.start();
        ExecuteCommands ec = new ExecuteCommands(writer);
        commands = new Thread(ec);
        commands.start();
	}
	
	public void join(String channel) throws Exception {
		writer.write("JOIN " + channel + "\r\n");
		writer.flush();
	}

}
