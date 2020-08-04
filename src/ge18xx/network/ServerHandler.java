package ge18xx.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public abstract class ServerHandler implements Runnable {
    private final static int DefaultTimeout = 2000;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean continueRunning;
	protected String name;
	
	public ServerHandler (Socket aServerSocket) {
		setValues (aServerSocket);
	}
	
	public ServerHandler (String aHost, int aPort) throws ConnectException {
		boolean tContinueRunning = false;
		
		try {
            Socket tSocket = new Socket ();
            // Connect to socket by host, port, and with specified timeout.
            tSocket.connect (new InetSocketAddress (InetAddress.getByName (aHost), aPort), DefaultTimeout);
            tSocket.setKeepAlive (true);
			setValues (tSocket);
			tContinueRunning = true;
		} catch (UnknownHostException tException) {
			log ("Unkown Host Exception thrown when creating Socket to Server", tException);
		} catch (ConnectException tException) {
			throw tException;
		} catch (SocketTimeoutException tException) {
			log ("Connect SocketTimeoutException thrown when trying to Connect to Server", tException);
		} catch (IOException tException) {
			log ("IOException thrown when creating Socket to Server", tException);
		}
		
		setContinueRunning (tContinueRunning);
	}

	protected void setContinueRunning (boolean aContinueRunning) {
		continueRunning = aContinueRunning;
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
}
