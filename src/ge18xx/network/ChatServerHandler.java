package ge18xx.network;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import ge18xx.game.NetworkGameSupport;

public class ChatServerHandler extends ServerHandler {
	protected JGameClient jClient;

	public ChatServerHandler (String aHost, int aPort, NetworkGameSupport aGameManager) throws ConnectException, SocketTimeoutException {
		super (aHost, aPort, aGameManager);
	}

	public void initializeChat (JGameClient aJClient) {
		jClient = aJClient;
		
		// Save my name on this local client
		setName (jClient.getName ());
		
		// Tell the Server who I am, and allow it to send to other players I am here
		sendNewUser ();
	}
	
	public void handleServerMessage (String tMessage) {
		jClient.handleServerMessage (tMessage);
//		System.out.println ("Received Message [" + tMessage + "]");
	}
	
	public void handleServerCommands (String aCommand) {
		String tMessage = aCommand.substring (1, aCommand.length () - 1);
		String tName, tShortened;
		boolean tAddToChat;
		
		if (tMessage.startsWith ("Server: ")) {
			tShortened = tMessage.substring (8);
			tAddToChat = true;
			if (tShortened.startsWith("<GSResponse")) {
				jClient.handleGSResponse (tShortened);
				tAddToChat = false;
			} else if (tShortened.startsWith ("Sorry we are full")) {

			} else if (tShortened.startsWith ("rejected")) {
				closeAll ();
				setContinueRunning (false);
				jClient.rejectedConnect ();
			} else if (tShortened.endsWith (" is AFK")) {
				tName = extractName (tShortened);
				jClient.setPlayerAsAFK (tName);
			} else if (tShortened.endsWith (" is Not AFK")) {
				tName = extractName (tShortened);
				jClient.resetPlayerFromAFK (tName);				
			} else if (tShortened.endsWith (" has joined")) {
				tShortened = handleJoined (tShortened);
			} else if (tShortened.endsWith (" has reconnected")) {
				tShortened = handleJoined (tShortened);
			} else if (tShortened.endsWith (" has left") ||
						tShortened.endsWith (" has aborted")) {
				tName = extractName (tShortened);
				jClient.removePlayer (tName);
			} else if (tShortened.endsWith ("is Ready to play the Game")) {
				handlePlayerReady (tShortened);
			} else if (tShortened.endsWith (" Starts the Game")) {
				jClient.startsGame ();
			} else {
				System.err.println ("Received Command that wasn't Matched [" + aCommand + "]");
			}
			if (tAddToChat) {
				appendToChat (tShortened);
			}
		}
	}

	private void handlePlayerReady (String aShortened) {
		String tName;
		tName = extractName (aShortened);
		
		jClient.playerReady (tName, aShortened);
	}
	
	private String handleJoined (String aShortened) {
		String tName;
		String tMessage;
		
		tName = extractName (aShortened);
		jClient.addPlayer (tName);
		if (! tName.equals (name)) {
			tMessage = tName + " is present";
		} else {
			tMessage = tName + ", you have joined";
		}
		if (aShortened.contains ("[AFK]")) {
			jClient.setPlayerAsAFK (tName);
		}
		if (aShortened.contains ("[READY]")) {
			jClient.playerReady (tName, "");
		}
		
		return tMessage;
	}

	private String extractName (String aShortened) {
		String tName;
		
		tName = aShortened.substring (0, aShortened.indexOf (" "));
		
		return tName;
	}
	
	public void appendToChat (String aMessage) {
		jClient.appendToChat (aMessage);
	}
	
	// Send Commands to the Server ---
	
	public void sendGameActivity (String aGameActivity) {
		println (aGameActivity);
	}
	
	@Override
	public void sendGameSupport (String aGameSupport) {
		println (aGameSupport);
	}

	public void sendNewUser () {
		println ("name " + name);
	}

	public void requestUserNameList () {
		println ("who");
	}

	public void sendMessage (String aMessage) {
		println ("say " + aMessage);
	}
	
	public void sendUserIsNotAFK () {
		println ("Not AFK");
	}
	
	public void sendUserIsAFK () {
		println ("AFK");
	}

	public String buildGameSupportXML (String aGameID, String tXMLChild) {
		String tGameSupportXML;
		String tGameIDChunk = "";
		
		if (! aGameID.equals ("")) {
			tGameIDChunk =  " gameID=\"" + aGameID + "\"";
		}
		tGameSupportXML = "Game Support <GS" + tGameIDChunk + ">" + tXMLChild + "</GS>";
		
		return tGameSupportXML;
	}
	
	public void sendUserReady (String aGameID) {
		String tGameSupportXML;
		
		tGameSupportXML = buildGameSupportXML (aGameID, "<Ready>");
		println (tGameSupportXML);
		jClient.appendToChat ("I am ready to play the Game", true);
	}

	public void sendUserStart (String aGameID) {
		String tGameSupportXML;

		tGameSupportXML = buildGameSupportXML (aGameID, "<Start>");
		println (tGameSupportXML);
//		println ("Game Support <GS gameID=\"" + aGameID + "\"><Start></GS>");
		jClient.appendToChat ("I started the Game", true);
	}

	@Override
	protected void handleChatReconnect() {
		String tGameSupportXML;
		String tGameID;
		String tUserName;
		
		tGameID = jClient.getGameID ();
		tUserName = jClient.getPlayerName ();
		System.out.println ("Ready to attempt Server Connect");
		tGameSupportXML = buildGameSupportXML (tGameID, "<Reconnect name=\"" + tUserName + "\">");
		println (tGameSupportXML);
		startHeartbeat ();
	}
	
	@Override
	protected void startHeartbeat () {
		jClient.startHeartbeat ();
	}
	// -- End of Sending Commands to the Server
}
