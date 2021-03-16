package ge18xx.network;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import ge18xx.game.NetworkGameSupport;

public class HeartbeatThread implements Runnable {

	Thread thread;
	boolean continueRunning = false;
	JGameClient jGameClient;
	NetworkGameSupport gameManager;
   	LocalTime startTime;
   	LocalTime responseTime;
   	DateTimeFormatter hmssFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   	long totalResponseTime;
   	String heartbeatRequest = "<Heartbeat>";
	
	public HeartbeatThread (JGameClient aJGameClient) {
		setContinueRunning (false);
		jGameClient = aJGameClient;
		gameManager = jGameClient.getGameManager ();
		totalResponseTime = 0;
	}
	
	public void setContinueRunning (boolean aContinueRunning) {
		continueRunning = aContinueRunning;
	}
	
	public void captureStartTime () {
		startTime = LocalTime.now ();
	}
	
	public void setStartTime (LocalTime aStartTime) {
		startTime = aStartTime;
	}
	
	public void captureResponseTime () {
		responseTime = LocalTime.now ();
	}
	
	public void setResponseTime (LocalTime aResponseTime) {
		responseTime = aResponseTime;
	}
	
	public LocalTime getStartTime () {
		return startTime;
	}
	
	public LocalTime getResponseTime () {
		return responseTime;
	}
	
   public void run () {
    	int tCounter = 0;
     	
    	System.out.println ("Heartbeat Thread running");
        while (continueRunning) {
        	tCounter++;
        	try {
	            // thread to sleep for 10000 milliseconds (10 Seconds)
	            sendHeartbeat ();
	            displayTiming (tCounter);
	            Thread.sleep (10000);
	         } catch (Exception e) {
	            System.out.println (e);
	         }
        }
    }

	private void displayTiming (int aCounter) {
		long tDifference;
		long tAverage;
		
		tDifference = startTime.until (responseTime, ChronoUnit.MILLIS) - GameSupportHandler.waitTime; 
		totalResponseTime += tDifference;
		tAverage = totalResponseTime/aCounter;
		System.out.println ("Heartbeat Thread cycle " + aCounter + 
				" Start Time " + hmssFormat.format (startTime) +
				" Response Time " + hmssFormat.format (responseTime) +
				" Duration in MilliSeconds (less Wait Time) " + tDifference + 
				" Average Duration " + tAverage);
	}
	
   
	public void sendHeartbeat () {
		String tGameID;
		
		tGameID = gameManager.getGameID ();
        captureStartTime ();
		jGameClient.requestGameSupport (tGameID, heartbeatRequest);
        captureResponseTime ();
	}

}
