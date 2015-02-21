package Features;

import Core.MainFrame;

public class PrivMsg {

	public static void sendMessage(String nick, String message) {
		MainFrame.toRun.add("[RUN]:PRIVMSG " + nick + " :" + message + "\r\n");
	}

}
