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
		mServerSocket = buildMockServerSocket ();
		mBufferedReader = buildMockBufferedReader ();
		mPrintWriter = buildMockPrintWriter ();
		tChatServerHandler = new ChatServerHandler (mServerSocket, mBufferedReader, mPrintWriter, mGameManager);
		
		return tChatServerHandler;
	}

	public ServerHandler buildMockServerHandler () throws ConnectException, SocketTimeoutException {
		ServerHandler mServerHandler;
		
		mServerHandler = Mockito.mock(ServerHandler.class);
		
		return mServerHandler;
	}

	public ChatServerHandler buildMockChatServerHandler () throws ConnectException, SocketTimeoutException {
		ChatServerHandler mChatServerHandler;
		
		mChatServerHandler = Mockito.mock(ChatServerHandler.class);
		
		return mChatServerHandler;
	}
	
	public Socket buildMockServerSocket () {
		Socket mServerSocket;
		
		mServerSocket = Mockito.mock (Socket.class);

		return mServerSocket;
	}

	public BufferedReader buildMockBufferedReader () {
		BufferedReader mBufferedReader;
		
		mBufferedReader = Mockito.mock (BufferedReader.class);

		return mBufferedReader;
	}

	public PrintWriter buildMockPrintWriter () {
		PrintWriter mPrintWriter;
		
		mPrintWriter = Mockito.mock (PrintWriter.class);

		return mPrintWriter;
	}
}
