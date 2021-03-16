package ge18xx.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameSupportHandler {
	private boolean waitingForResponse;
	private String response;
	JGameClient jGameClient;
	public final static int waitTime = 10;
	private final static String GAME_ID = "(\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d\\d\\d)";
	private final static String GS_WITH_GAME_ID = "<GS gameID=\"" + GAME_ID + "\">(.*)</GS>";
	private final static Pattern GS_WITH_GAME_ID_PATTERN = Pattern.compile (GS_WITH_GAME_ID);
	private final static String GSR_WITH_GAME_ID = "<GSResponse gameID=\"" + GAME_ID + "\">";
	private final static Pattern GSR_WITH_GAME_ID_PATTERN = Pattern.compile (GSR_WITH_GAME_ID);
	
	public GameSupportHandler (JGameClient aJGameClient) {
		setWaitingForResponse (false);
		setResponse ("");
		setJGameClient (aJGameClient);
	}
	
	private void setJGameClient (JGameClient aJGameClient) {
		jGameClient = aJGameClient;
	}
	
	public void setWaitingForResponse (boolean aWaiting) {
		waitingForResponse = aWaiting;
	}
	
	public void setResponse (String aResponse) {
		response = aResponse;
	}

	public boolean waiting () {
		return waitingForResponse;
	}
	
	public String getResponse () {
		return response;
	}
	
	public void handleGSResponse (String aGSResponse) {
		setResponse (aGSResponse);
		setWaitingForResponse (false);
	}
	
	public void holdRequestTillReady () {
		while (waitingForResponse) {
			try {
				Thread.sleep (waitTime + 1);
			} catch (InterruptedException e) {
				System.err.println ("Waiting for the Response to Clear - Exception");
				e.printStackTrace();
			}
		}
	}
	
	public String requestGameSupport (String aRequest) {
		ServerHandler tServerHandler;
		
		holdRequestTillReady ();
		tServerHandler = jGameClient.getServerHandler ();
		setWaitingForResponse (true);
		tServerHandler.sendGameSupport (aRequest);
		while (waitingForResponse) {
			try {
				Thread.sleep (waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}
		
		return response;
	}
	
	public String getFromRequestGameID (String aRequest) {
		Matcher tMatcher = GS_WITH_GAME_ID_PATTERN.matcher (aRequest);
		String tFoundGameID = "NOID";
		
		if (tMatcher.find ()) {
			tFoundGameID = tMatcher.group (1);
		}
		
		return tFoundGameID;
	}

	
	public String getFromResponseGameID (String aRequest) {
		Matcher tMatcher = GSR_WITH_GAME_ID_PATTERN.matcher (aRequest);
		String tFoundGameID = "NOID";
		
		if (tMatcher.find ()) {
			tFoundGameID = tMatcher.group (1);
		}
		
		return tFoundGameID;
	}
}
