package ge18xx.network;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeartbeatThread implements Runnable {
	private Logger logger;
	final Level HEARTBEAT = Level.forName ("HEARTBEAT", 550);

	Thread thread;
	boolean continueRunning = false;
	JGameClient jGameClient;
	LocalTime startTime;
	LocalTime responseTime;
	DateTimeFormatter hmssFormat = DateTimeFormatter.ofPattern ("HH:mm:ss.SSS");
	long totalResponseTime;
	String heartbeatRequest = "<Heartbeat>";
	boolean sendHeartbeat = true;
	String baseDirectory;

	public HeartbeatThread (JGameClient aJGameClient) {
		setContinueRunning (false);
		jGameClient = aJGameClient;
		baseDirectory = jGameClient.getXMLBaseDirectory ();
		setupLogger ();
		totalResponseTime = 0;
	}

	private void setupLogger () {
		System.setProperty ("log4j.configurationFile", baseDirectory + "log4j2.xml");
		logger = LogManager.getLogger ("com.ge18xx.heartbeat");
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

	@Override
	public void run () {
		int tCounter = 0;

		logger.log (HEARTBEAT, "Thread Starting to run");
		while (continueRunning) {
			tCounter++;
			try {
				// thread to sleep for 10000 milliseconds (10 Seconds)
				sendHeartbeat ();
				displayTiming (tCounter);
				Thread.sleep (10000);
			} catch (Exception tException) {
				logger.error ("Heartbeat Thread throwing Exception");
				logger.error (tException);
			}
		}
		logger.log (HEARTBEAT, "Thread Stopping");
	}

	private void displayTiming (int aCounter) {
		long tDifference;
		long tAverage;
		String tMessage;

		tDifference = startTime.until (responseTime, ChronoUnit.MILLIS) - NetworkGameSupport.WAIT_TIME;
		totalResponseTime += tDifference;
		tAverage = totalResponseTime / aCounter;
		tMessage = "Thread cycle " + aCounter + " Start Time " + hmssFormat.format (startTime) + " Response Time "
				+ hmssFormat.format (responseTime) + " Duration in MilliSeconds (less Wait Time) " + tDifference
				+ " Average Duration " + tAverage;
		logger.log (HEARTBEAT, tMessage);
	}

	public void sendHeartbeat () {
		String tGameID;

		if (sendHeartbeat) {
			tGameID = jGameClient.getGameID ();
			captureStartTime ();
			jGameClient.requestGameSupport (tGameID, heartbeatRequest);
			captureResponseTime ();
		}
	}

	public void stopHeartbeatDelivery () {
		sendHeartbeat = false;
		continueRunning = false;
	}

	public void startHeartbeatDelivery () {
		sendHeartbeat = true;
		continueRunning = true;
	}
}
