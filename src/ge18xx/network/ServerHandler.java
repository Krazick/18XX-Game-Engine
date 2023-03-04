package ge18xx.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerHandler implements Runnable {
	public final static ServerHandler NO_SERVER_HANDLER = null;
	private final static int DefaultTimeout = 12000;
	private final static int DefaultSleep = 60000;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean continueRunning;
	protected String name;
	private String host;
	private int port;
	protected Logger logger;
	private NetworkGameSupport gameManager;

	public ServerHandler (Socket aServerSocket, NetworkGameSupport aGameManager) {
		setValues (aServerSocket);
		gameManager = aGameManager;
		setupLogger ();
	}

	public ServerHandler (Socket aServerSocket, BufferedReader aBufferedReader, PrintWriter aPrintWriter,
			NetworkGameSupport aGameManager) {
		setSocket (aServerSocket);
		setInBufferedReader (aBufferedReader);
		setOutPrintWriter (aPrintWriter);
		gameManager = aGameManager;
		setupLogger ();
	}

	private void setupLogger () {
		String tXMLBaseDir;

		tXMLBaseDir = gameManager.getXMLBaseDirectory ();
		System.setProperty ("log4j.configurationFile", tXMLBaseDir + "log4j2.xml");
		logger = LogManager.getLogger (ServerHandler.class);
		logger.info ("Logger setup in Server Handler");
	}

	public ServerHandler (String aHost, int aPort, NetworkGameSupport aGameManager)
			throws ConnectException, SocketTimeoutException {
		boolean tContinueRunning = false;

		gameManager = aGameManager;
		setupLogger ();
		try {
			// Connect to socket by host, port, and with specified timeout.
			setHost (aHost);
			setPort (aPort);
			tContinueRunning = establishSocketConnection ();
		} catch (UnknownHostException tException) {
			log ("Unkown Host Exception thrown when creating Socket to Server", tException);
		} catch (ConnectException tException) {
			log ("Connection Exception thrown when creating Socket to Server", tException);
			throw tException;
		} catch (SocketTimeoutException tException) {
			log ("Socket Timeout Exception when establing Connection to Server", tException);
			throw tException;
		} catch (IOException tException) {
			log ("IOException thrown when creating Socket to Server", tException);
		}

		setContinueRunning (tContinueRunning);
	}

	boolean establishSocketConnection () throws UnknownHostException, IOException, SocketException, ConnectException {
		InetAddress tIPAddress;
		boolean tGoodConnection;

		Socket tSocket = new Socket ();
		tIPAddress = InetAddress.getByName (host);
		logger.info ("Attempting Socket Connection to Host " + host + " using IP " + tIPAddress + " Port " + port);
		tSocket.connect (new InetSocketAddress (tIPAddress, port), DefaultTimeout);
		logger.info ("Socket Connection Established");
		tSocket.setKeepAlive (true);
		setValues (tSocket);
		tGoodConnection = true;

		return tGoodConnection;
	}

	protected void setContinueRunning (boolean aContinueRunning) {
		continueRunning = aContinueRunning;
	}

	private void setHost (String aHost) {
		host = aHost;
	}

	private void setPort (int aPort) {
		port = aPort;
	}

	public void setValues (Socket aServerSocket) {
		setSocket (aServerSocket);
		setupBufferedReader ();
		setupPrintWriter ();
	}

	public void setSocket (Socket aServerSocket) {
		socket = aServerSocket;
	}

	public void setName (String aName) {
		name = aName;
	}

	private void setupBufferedReader () {
		BufferedReader tBufferedReader;

		try {
			logger.info ("Attempting to setup Buffered Reader");
			tBufferedReader = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
			setInBufferedReader (tBufferedReader);
			logger.info ("Successful Buffered Reader");
		} catch (IOException tException) {
			log ("IOException thrown when seting up Buffered Reader", tException);
		}
	}

	public void setInBufferedReader (BufferedReader aBufferedReader) {
		in = aBufferedReader;
	}

	private void setupPrintWriter () {
		PrintWriter tPrintWriter;

		try {
			logger.info ("Attempting to setup Print Writer");
			tPrintWriter = new PrintWriter (socket.getOutputStream (), true);
			setOutPrintWriter (tPrintWriter);
			logger.info ("Successful Print Writer");
		} catch (IOException tException) {
			log ("IOException thrown when seting up PrintWriter", tException);
		}
	}

	public void setOutPrintWriter (PrintWriter aPrintWriter) {
		out = aPrintWriter;
	}

	@Override
	public void run () {
		String tString;
		int tRetryCount;

		tRetryCount = 3;
		while (tRetryCount > 0) {
			if (in != null) {
				try {
					while (continueRunning) {
						tString = in.readLine ();
						if (tString == null) {
							setContinueRunning (false);
						} else if (tString.startsWith ("[") && tString.endsWith ("]")) {
							handleServerCommands (tString);
						} else {
							handleServerMessage (tString);
						}
					}
				} catch (SocketTimeoutException tException1) {
					log ("Socket Timeout Exception when reading from Server. Retry Count " + tRetryCount, tException1);
				} catch (IOException tException2) {
					log ("IOException thrown when Reading from Server. Retry Count " + tRetryCount, tException2);
				}
			}
			try {
				Thread.sleep (DefaultSleep);
			} catch (InterruptedException tException3) {
				log ("Exception thrown when Sleeping after SocketTimeout/IO Exception) ", tException3);
			}
			tRetryCount--;
			if (tRetryCount == 2) {
				if (tryReConnect ()) {
					tRetryCount = 3;
					setContinueRunning (true);
//					startHeartbeat ();
				}
			}
		}
		closeAll ();
	}

	protected abstract void startHeartbeat ();

	private boolean tryReConnect () {
		boolean tContinue;

		try {
			Thread.sleep (10000);
			logger.warn ("Attempting once to Reconnect to the Server");
			socket = null;
			System.gc ();
			establishSocketConnection ();
			logger.warn ("Success on creating a new Socket to the Server");
			handleChatReconnect ();
			tContinue = true;
		} catch (Exception tException) {
			String message = "ReConnect not successful " + tException.getMessage ();
			log (message, tException);
			tContinue = false;
		}

		return tContinue;
	}

	public boolean isConnected () {
		return continueRunning;
	}

	// Abstract Classes to handle Content from the Server --

	protected abstract void handleServerMessage (String tString);

	protected abstract void handleServerCommands (String aString);

	// Generate Client Requests to the Server ---

	public void println (String tString) {
		if (out != null) {
			tString = tString.replaceAll ("\r", "").replaceAll ("\n", "");
			out.println (tString);
		}
	}

	// --- End of Client Requests to the Server

	private void log (String aMessage, Exception aException) {
		System.err.println (aMessage);
		aException.printStackTrace ();
		logger.error (aMessage, aException);
	}

	public void closeAll () {
		continueRunning = false;
		if (in != null) {
			try {
				in.close ();
			} catch (IOException tException) {
				log ("IOException thrown when Closing the Server InputStreamReader", tException);
			}
		}
		if (out != null) {
			out.close ();
			try {
				socket.close ();
			} catch (IOException tException) {
				log ("IOException thrown when Closing Socket", tException);
			}
		}
	}

	public void shutdown () {
		// Report to Server this Client is stopping
		println ("stop");
		closeAll ();
	}

	protected abstract void handleChatReconnect ();

	protected abstract boolean sendGameSupport (String aRequest);

	protected abstract String buildGameSupportXML (String tGameID, String string);
}
