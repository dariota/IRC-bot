package Core;
import java.util.ArrayList;
import java.util.Random;

public class Parser implements Runnable {
	
	ArrayList<String> toParse = new ArrayList<>();
	Random rand = new Random();
	private static final String SEPARATOR = new String(new char[] {7});

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		while (MainFrame.run) {
			if ((System.currentTimeMillis() - time) > 100) {
				time = System.currentTimeMillis();
				if (toParse.size() > 0) {
					for (int i = toParse.size()-1; i != -1; i--) {
						String s = toParse.get(i);
						System.out.println("PARS:" + s);
						String type = s.split(" ")[1];
						String replyTo = "";
						String nick = s.split("!")[0].replaceFirst(":", "");
						String senderHost = "";
						try {
							senderHost = s.split("!")[1].split(" ")[0];
						} catch (ArrayIndexOutOfBoundsException e) {}
						String content = "";
						if (type.equals("PRIVMSG") || type.equals("JOIN") || type.equals("NOTICE")) {
							replyTo = s.split(" ")[2];
							replyTo = replyTo.equals(MainFrame.nick) ? nick : replyTo;
							try {
								if (s.replaceFirst(":", "").indexOf(":") != -1) {
									content = s.substring(s.replaceFirst(":", "").indexOf(":") + 2);
									try {
										System.out.println(MainFrame.ownerHost + " " + s.split("!")[1].split(" ")[0]);
										if ((MainFrame.ownerHost).equals(s.split("!")[1].split(" ")[0])) {
											if (content.startsWith("@load ")) {
												String label = "";
												label = content.split(" ")[1];
												ProcessManager.initialiseNewModule(label, content.split("@load " + label)[1]);
											} else if (content.startsWith("@unload ")) {
												String label = "";
												label = content.split(" ")[1];
												ProcessManager.endModule(label);
											} else if (content.startsWith("@refresh ")) {
												SecurityManager.refreshCommands();
											}
										}
									} catch (ArrayIndexOutOfBoundsException e) {}
								}
							} catch (StringIndexOutOfBoundsException e) {
								System.out.println("error on:\n" + s);
							}
						} else if (type.equals("311")) {
							MainFrame.nickToHost.put(s.split(MainFrame.nick + " ")[0], s.split(MainFrame.nick + " ")[1].split(" ")[1] + "@*");
						}
						String out = "";
						try {
							Integer.parseInt(type);
							content = nick;
							nick = "";
							out += "!";
						} catch (NumberFormatException e) {}
						out += type + SEPARATOR + replyTo.replaceFirst(":", "") + SEPARATOR + nick + SEPARATOR + content + SEPARATOR + senderHost + "\r\n";
						ProcessManager.sendLine(out);
						MainFrame.toRun.add(toParse.get(i));
						toParse.remove(i);
					}
				}
			}
			
			try {
				Thread.sleep(MainFrame.SLEEP_TIME);
			} catch (InterruptedException e) {
			}
		}
	}


}
