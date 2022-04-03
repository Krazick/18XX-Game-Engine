package ge18xx.network;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.mockito.Mockito;

import ge18xx.game.GameManager;
import ge18xx.game.GameTestFactory;

public class NetworkTestFactory {
	GameTestFactory gameTestFactory;

	public NetworkTestFactory () {
		gameTestFactory = new GameTestFactory ();
	}
	
	public NetworkTestFactory (GameTestFactory aGameTestFactory) {
		gameTestFactory = aGameTestFactory;
	}

	public ChatServerHandler buildChatServerHandler () throws ConnectException, SocketTimeoutException {
		ChatServerHandler tChatServerHandler;
		GameManager mGameManager;
		Socket mServerSocket;
		BufferedReader mBufferedReader;
		PrintWriter mPrintWriter;		
		
		mGameManager = gameTestFactory.buildGameManagerMock ();
		mServerSocket = buildServerSocketMock ();
		mBufferedReader = buildBufferedReaderMock ();
		mPrintWriter = buildPrintWriterMock ();
		tChatServerHandler = new ChatServerHandler (mServerSocket, mBufferedReader, mPrintWriter, mGameManager);
		
		return tChatServerHandler;
	}

	public Socket buildServerSocketMock () {
		Socket mServerSocket;
		
		mServerSocket = Mockito.mock (Socket.class);

		return mServerSocket;
	}

	public BufferedReader buildBufferedReaderMock () {
		BufferedReader mBufferedReader;
		
		mBufferedReader = Mockito.mock (BufferedReader.class);

		return mBufferedReader;
	}

	public PrintWriter buildPrintWriterMock () {
		PrintWriter mPrintWriter;
		
		mPrintWriter = Mockito.mock (PrintWriter.class);

		return mPrintWriter;
	}
}
