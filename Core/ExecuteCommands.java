package Core;
import java.io.BufferedWriter;
import java.io.IOException;


public class ExecuteCommands implements Runnable {

	BufferedWriter writer;
	private static final String NOT_AUTHORISED = " :You are not authorised to use that command.";
	private static final String MESSAGE_SIGNAL = new String(new char[] {7});
	
	public ExecuteCommands(BufferedWriter writer) {
		this.writer = writer;
	}
	
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		try {
			while (MainFrame.run) {
				if ((MainFrame.processReplies.size() > 0 || MainFrame.toRun.size() > 0) && (System.currentTimeMillis() - time) > 100) {
					time = System.currentTimeMillis();
					for (int i = 0; i < MainFrame.toRun.size(); i++) {
						if (MainFrame.toRun.get(i).startsWith("[RUN]:")) {
							writer.write(MainFrame.toRun.get(i).replace("[RUN]:", "") + "\r\n");
						}
					}
					for (int i = 0; i < MainFrame.processReplies.size(); i++) {
						String command = MainFrame.processReplies.get(i);
						String execute = "";
						String message = "";
						try {
							message = "" + command.split(MESSAGE_SIGNAL)[1];
						} catch (ArrayIndexOutOfBoundsException e) {}
						String[] commandParts = command.split(" ");
						StringBuilder sb = new StringBuilder();
						boolean banOnly = false;
						boolean part = false;
						
						try {
							if (isAuthorised(commandParts[0], commandParts[1])) {
								switch (commandParts[0]) {
								case "OUT": //OUT <senderHost> <senderNick> <button>
									System.out.println("MOVE:" + commandParts[3]);
									break;
								case "RAW": //RAW <senderHost> <senderNick> ¬<IRC COMMAND>
									execute += message;
									break;
								case "MSG": //MSG <senderHost> <senderNick> <target> ¬<message>
									execute += "PRIVMSG " + commandParts[3] + " :" + message;
									break;
								case "PARTL": //PARTL <senderHost> <senderNick> <channel> <channel> ... <channel> ¬[<message>] 
									sb.append("PART ");
									part = true;
								case "JOIN": //JOIN <senderHost> <senderNick> <channel> <channel> ... <channel>
									if (!part) sb.append("JOIN ");
									for (int j = 3; j < commandParts.length; j++) {
										sb.append(commandParts[j] + ", ");
									}
									execute += sb.substring(0, sb.length() - 2);
									if (part) execute += " " + message;
									break;
								case "JOINK": //JOINK <senderHost> <senderNick> <channel> <key>
									execute += "JOIN " + commandParts[3] + " " + commandParts[4];
									break;
								case "INVITE": //INVITE <senderHost> <senderNick> <nickname> <channel>
									execute += "INVITE " + commandParts[3] + " " + commandParts[4];
									break;
								case "BAN":
									banOnly = true;
								case "KBAN": //KBAN <senderHost> <senderNick> <channel> <nick> ¬[<message>]
									if (MainFrame.nickToHost.containsKey(commandParts[2])) {
										execute += "MODE " + commandParts[1] + " b " + MainFrame.nickToHost.get(commandParts[2]) + "\r\n";
									} else {
										MainFrame.putOffReplies.add(command);
										execute += "WHOIS " + commandParts[4];
									}
									if (banOnly) break;
								case "KICK": //KICK <senderHost> <senderNick> <channel> <nick> ¬[<message>]
									if (message.equals("")) message = "u heff bin turminated";
									execute += "KICK " + commandParts[3] + " " + commandParts[4] + " :" + message;
									break;
								case "MUTE": //MUTE <senderHost> <senderNick> <channel>
									execute += "MODE " + commandParts[3] + " m";
									break;
								case "MODE": //MODE <senderHost> <senderNick> <channel> <mode> ¬[<parameters>]
									execute += "MODE " + commandParts[3] + " " + commandParts[4] + " " + message;
									break;
								case "NICK": //NICK <senderHost> <senderNick> <nick>
									execute += "NICK " + commandParts[3];
									MainFrame.nick = commandParts[3];
									break;
								case "NOTICE": //NOTICE <senderHost> <senderNick> <nick> ¬[<parameters>]
									execute += "NOTICE " + commandParts[3] + " " + message;
									break;
								case "PART": //PART <senderHost> <senderNick> <channel> ¬[<message>]
									execute += "PART " + commandParts[3] + " :" + message;
									break;
								case "QUIT": //QUIT <senderHost> <senderNick> <channel> ¬[<message>]
									execute += "QUIT " + message;
									break;
								case "TOPIC": //TOPIC <senderHost> <senderNick> <channel> ¬[<topic>]
									execute += "TOPIC " + commandParts[3] + " " + message;
									break;
								}
							} else {
								execute += "PRIVMSG " + commandParts[2] + NOT_AUTHORISED;
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							execute += "PRIVMSG " + MainFrame.ownerNick + " :Error in execution, see output.\r\n";
							try {
								execute += "PRIVMSG " + commandParts[2] + " :There was an error processing your command.";
							} catch (ArrayIndexOutOfBoundsException e1) {}
							e.printStackTrace();
						}
						execute += "\r\n";
						writer.write(execute);
						writer.flush();
					}
					
					writer.flush();
				}
				while (MainFrame.toRun.size() > 0) {
					MainFrame.toRun.remove(0);
				}
				while (MainFrame.processReplies.size() > 0) {
					MainFrame.processReplies.remove(0);
				}
				try {
					Thread.sleep(MainFrame.SLEEP_TIME);
				} catch (InterruptedException e) {}
			} 
		} catch (IOException e) {
			e.printStackTrace();
			MainFrame.run = false;
			System.exit(0);
		}
	}

	private boolean isAuthorised(String command, String senderHost) {
		int level = MainFrame.peopleHost.containsKey(senderHost) ? MainFrame.peopleHost.get(senderHost) : 0;
		int commandLevel = SecurityManager.commandLevels.containsKey(command) ? 
							SecurityManager.commandLevels.get(command) : 11;
		if (level >= commandLevel) return true;
		return false;
	}

}
