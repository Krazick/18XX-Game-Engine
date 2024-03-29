package ge18xx.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameSupportHandler {
	private boolean waitingForResponse;
	private String response;
	JGameClient jGameClient;
	public static final String GS_RESPONSE_TAG = "GSResponse";
	private static final String GAME_ID = "(\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d\\d\\d)";
	private static final String GS_WITH_GAME_ID = "<GS gameID=\"" + GAME_ID + "\">(.*)</GS>";
	private static final Pattern GS_WITH_GAME_ID_PATTERN = Pattern.compile (GS_WITH_GAME_ID);
	private static final String GSR_WITH_GAME_ID = "<" + GS_RESPONSE_TAG + " gameID=\"" + GAME_ID + "\">";
	private static final Pattern GSR_WITH_GAME_ID_PATTERN = Pattern.compile (GSR_WITH_GAME_ID);

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
				Thread.sleep (NetworkGameSupport.WAIT_TIME + 1);
			} catch (InterruptedException e) {
				System.err.println ("Waiting for the Response to Clear - Exception");
				e.printStackTrace ();
			}
		}
	}

	public String requestGameSupport (String aRequest) {
		ServerHandler tServerHandler;
		boolean tWaitForResponse = true;

		holdRequestTillReady ();
		setWaitingForResponse (tWaitForResponse);
		tServerHandler = jGameClient.getServerHandler ();
		tWaitForResponse = tServerHandler.sendGameSupport (aRequest);
		setWaitingForResponse (tWaitForResponse);
		while (waitingForResponse) {
			try {
				Thread.sleep (NetworkGameSupport.WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}

		return response;
	}

	public String getGameIDFromRequest (String aRequest) {
		Matcher tMatcher = GS_WITH_GAME_ID_PATTERN.matcher (aRequest);
		String tFoundGameID = JGameClient.NO_GAME_ID;

		if (tMatcher.find ()) {
			tFoundGameID = tMatcher.group (1);
		}

		return tFoundGameID;
	}

	public String getGameIDFromNetworkResponse (String aResponse) {
		Matcher tMatcher = GSR_WITH_GAME_ID_PATTERN.matcher (aResponse);
		String tFoundGameID = JGameClient.NO_GAME_ID;

		if (tMatcher.find ()) {
			tFoundGameID = tMatcher.group (1);
		}

		return tFoundGameID;
	}

	public String retrieveGameID () {
		String tGameIDRequest;
		String tGameID;
		String tResponse;

		tGameIDRequest = JGameClient.GAME_SUPPORT_PREFIX + " <GS><GameIDRequest></GS>";
		tResponse = requestGameSupport (tGameIDRequest);
		tGameID = getGameIDFromNetworkResponse (tResponse);

		return tGameID;
	}
}
