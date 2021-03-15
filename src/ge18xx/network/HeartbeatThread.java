package ge18xx.network;

import ge18xx.game.NetworkGameSupport;

public class HeartbeatThread implements Runnable {

	Thread thread;
	boolean continueRunning = false;
	JGameClient jGameClient;
	NetworkGameSupport gameManager;
	
	public HeartbeatThread (JGameClient aJGameClient) {
		setContinueRunning (false);
		jGameClient = aJGameClient;
		gameManager = jGameClient.getGameManager ();
	}
	
	public void setContinueRunning (boolean aContinueRunning) {
		continueRunning = aContinueRunning;
	}
	
    public void run () {
    	int tCounter = 0;
    	System.out.println ("Heartbeat Thread running");
        while (continueRunning) {
        	tCounter++;
        	try {
	            // thread to sleep for 60000 milliseconds
	            Thread.sleep (30000);
	            System.out.println ("Heartbeat Thread cycle " + tCounter);
	            sendHeartbeat ();
	         } catch (Exception e) {
	            System.out.println (e);
	         }
        }
     }
	
	public void sendHeartbeat () {
		String tGameID;
		String tRequest;
		
		tGameID = gameManager.getGameID ();
		tRequest = "<Heartbeat>";
		jGameClient.requestGameSupport (tGameID, tRequest);
	}

}
