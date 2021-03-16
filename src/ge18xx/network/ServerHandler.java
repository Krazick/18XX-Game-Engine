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

public abstract class ServerHandler implements Runnable {
    private final static int DefaultTimeout = 2000;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean continueRunning;
	protected String name;
	private String host;
	private int port;
	
	public ServerHandler (Socket aServerSocket) {
		setValues (aServerSocket);
	}
	
	public ServerHandler (String aHost, int aPort) throws ConnectException, SocketTimeoutException {
		boolean tContinueRunning = false;
		
		try {
            // Connect to socket by host, port, and with specified timeout.
			setHost (aHost);
			setPort (aPort);
            establishSocketConnection ();
			tContinueRunning = true;
		} catch (UnknownHostException tException) {
			log ("Unkown Host Exception thrown when creating Socket to Server", tException);
		} catch (ConnectException tException) {
			throw tException;
		} catch (SocketTimeoutException tException) {
			log ("Socket Timeout Exception when establing Connection to Server", tException);
			throw tException;
		} catch (IOException tException) {
			log ("IOException thrown when creating Socket to Server", tException);
		}
		
		setContinueRunning (tContinueRunning);
	}

	private void establishSocketConnection ()
			throws UnknownHostException, IOException, SocketException {
		InetAddress tIPAddress;
		
        Socket tSocket = new Socket ();
        tIPAddress = InetAddress.getByName (host);
		tSocket.connect (new InetSocketAddress (tIPAddress, port), DefaultTimeout);
		tSocket.setKeepAlive (true);
		setValues (tSocket);
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
	
	private void setValues (Socket aServerSocket) {
		socket = aServerSocket;
		setupBufferedReader ();
		setupPrintWriter ();
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	private void setupBufferedReader () {
		try {
			in = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
		} catch (IOException tException) {
			log ("IOException thrown when seting up Buffered Reader", tException);
		}		
	}
	
	private void setupPrintWriter () {
		try {
			out = new PrintWriter (socket.getOutputStream (), true);
		} catch (IOException tException) {
			log ("IOException thrown when seting up PrintWriter", tException);
		}		
	}
	
	@Override
	public void run () {
		String tString;

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
			} catch (SocketTimeoutException tException) {
				log ("Socket Timeout Exception when reading from Server", tException);
			} catch (IOException tException) {
				log ("Exception thrown when Reading from Server", tException);
			} finally {
				closeAll ();
			}		
		}
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
	
//	private boolean tryReConnect () {
//		boolean tContinue;
//   	
//		try {
//			serverFrame.closeServerSocket ("Trying to ReConnect");
//			//empty my old lost connection and let it get by garbage collect immediately 
//			socket = null;
//			System.gc ();
// 			//Wait a new client Socket connection and address this to my local variable
//			socket = serverFrame.acceptServerSocket ("Trying to ReConnect"); // Waiting for another Connection
//			System.out.println ("YEAH!!!!   Connection established...");
//			tContinue = true;
//		} catch (Exception eException) {
//			String message = "ReConnect not successful " + eException.getMessage ();
//			log (message, eException); //etc...
//			tContinue = false;
//		}
//    
//		return tContinue;
//	}

	protected abstract void sendGameSupport (String aRequest);

	protected abstract String buildGameSupportXML (String tGameID, String string);
}
