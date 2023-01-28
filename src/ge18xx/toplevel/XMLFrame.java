package ge18xx.toplevel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import ge18xx.game.Config;
import ge18xx.game.FrameInfo;
import ge18xx.game.GameFrameConfig;
import ge18xx.game.GameManager;

//
//  XMLFrames.java
//  Game_18XX
//
//  Created by Mark Smith on 11/28/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class XMLFrame extends JFrame {
	public GameManager gameManager;
	
	public static enum Visibility {
		ON, OFF, CONFIG_VALUE
	}

	private static final long serialVersionUID = 1L;
	public static final XMLFrame NO_XML_FRAME = null;
	boolean defaultVisible;
	int defaultWidth;
	int defaultHeight;
	int defaultXLocation;
	int defaultYLocation;
	int defaultHexSize;
	int defaultState;
	String gameName;
	JScrollPane scrollPane;

	public XMLFrame (String aFrameName, GameManager aGameManager) {
		super ();
		setGameManager (aGameManager);
		if (gameManager == GameManager.NO_GAME_MANAGER) {
			gameName = GameManager.NO_GAME_NAME;
		} else {
			gameName = gameManager.getActiveGameName ();
		}
		setTitle (aFrameName);
	}
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
	public void updateFrameTitle (String aBaseTitle) {
		String tFrameTitle;

		if (gameManager != GameManager.NO_GAME_MANAGER) {
			tFrameTitle = gameManager.createFrameTitle (aBaseTitle);
			setTitle (tFrameTitle);
		}
	}

	protected void setIconImage (GameManager aGameManager) {
		Image tImage;

		tImage = aGameManager.getIconImage ();
		setIconImage (tImage);
	}

	public String getGameName () {
		return gameName;
	}

	public void buildScrollPane (JComponent aImage) {
		buildScrollPane (aImage, null);
	}

	public void buildScrollPane (JComponent aImage, String aBorderLayout) {
		scrollPane = new JScrollPane ();
		scrollPane.setViewportView (aImage);
		if (aBorderLayout != null) {
			add (scrollPane, aBorderLayout);
		} else {
			add (scrollPane);
		}
	}

	public String extractFrameName () {
		int tIndexLastSpace;
		String tFrameName;
		String tPattern = "^(.*) \\([A-Za-z][A-Za-z0-9_]*\\)$";

		tFrameName = getTitle ();
		if (tFrameName.matches (tPattern)) {
			tIndexLastSpace = tFrameName.lastIndexOf (" ");
			tFrameName = tFrameName.substring (0, tIndexLastSpace);
		}

		return tFrameName;
	}

	public boolean loadXML (XMLDocument aXMLDocument, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;

		try {
			aLoadableObject.loadXML (aXMLDocument);
			tXMLFileWasLoaded = true;
		} catch (Exception tException) {
			System.err.println ("Exception Message [" + tException.getMessage () + "].");
			tException.printStackTrace (System.err);
			tXMLFileWasLoaded = false;
		}

		return tXMLFileWasLoaded;

	}
	
	public boolean loadXML (String aXMLFileName, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;
		XMLDocument tXMLDocument;

		if (!("".equals (aXMLFileName))) {
			try {
				tXMLDocument = new XMLDocument (aXMLFileName);
				aLoadableObject.loadXML (tXMLDocument);
				tXMLFileWasLoaded = true;
			} catch (Exception tException) {
				System.err.println ("Oops, mucked up the XML " + aLoadableObject.getTypeName () + " File Access ["
						+ aXMLFileName + "].");
				System.err.println ("Exception Message [" + tException.getMessage () + "].");
				tException.printStackTrace (System.err);
				tXMLFileWasLoaded = false;
			}
		} else {
			System.err.println ("No File Name for " + aLoadableObject.getTypeName ());
			tXMLFileWasLoaded = false;
		}

		return tXMLFileWasLoaded;
	}

	public void setDefaults (int aWidth, int aHeight, int aXLocation, int aYLocation, boolean aVisible, int aHexSize, int aState ) {
		defaultWidth = aWidth;
		defaultHeight = aHeight;
		defaultXLocation = aXLocation;
		defaultYLocation = aYLocation;
		defaultVisible = aVisible;
		defaultHexSize = aHexSize;
		defaultState = aState;
	}

	public void setDefaults (FrameInfo aFrameInfo) {
		setDefaults (aFrameInfo.getWidth (), aFrameInfo.getHeight (), aFrameInfo.getXLocation (),
				aFrameInfo.getYLocation (), aFrameInfo.getVisible (), aFrameInfo.getHexSize (), aFrameInfo.getState ());
	}

	public void setDefaults (XMLNode aXMLMapRoot) {
		FrameInfo tFrameInfo = new FrameInfo (aXMLMapRoot);
		setDefaults (tFrameInfo);
	}

	public int getDefaultWidth () {
		return defaultWidth;
	}

	public int getDefaultHeight () {
		return defaultHeight;
	}

	public int getDefaultXLocation () {
		return defaultXLocation;
	}

	public int getDefaultYLocation () {
		return defaultYLocation;
	}

	public boolean getDefaultVisible () {
		return defaultVisible;
	}

	public int getDefaultHexScale () {
		return defaultHexSize;
	}

	public int getDefaultState () {
		return defaultState;
	}
	
	public void setDefaultFrameInfo () {
		setLocation (defaultXLocation, defaultYLocation);
		setSize (defaultWidth, defaultHeight);
		setVisible (defaultVisible);
		setState (defaultState);
		if (defaultHexSize > 0) {
			setHexScale (defaultHexSize);
		}
	}

	public XMLElement getXMLFrameElement (XMLDocument aXMLDocument) {
		FrameInfo tFrameInfo;
		GameFrameConfig tGameFrameConfig;
		XMLElement tXMLFrameElement;

		tGameFrameConfig = new GameFrameConfig ("template");
		tXMLFrameElement = tGameFrameConfig.createXMLFrameElement (aXMLDocument);
		tFrameInfo = new FrameInfo (this);
		tFrameInfo.appendXMLFrameAttributes (tXMLFrameElement);

		return tXMLFrameElement;
	}

	public static String getVisibileConfig () {
		return Visibility.CONFIG_VALUE.toString ();
	}

	public static String getVisibileOFF () {
		return Visibility.OFF.toString ();
	}

	public static String getVisibileON () {
		return Visibility.ON.toString ();
	}

	public void setFrameToConfigDetails (GameManager aGameManager) {
		setFrameToConfigDetails (aGameManager, getVisibileOFF ());
	}

	public void setFrameToConfigDetails (GameManager aGameManager, String aVisibility) {
		GameFrameConfig tGameFrameConfig;

		tGameFrameConfig = aGameManager.getGameFrameConfig ();
		if (tGameFrameConfig != Config.NO_GAME_FRAME) {
			setFrameToConfigDetails (tGameFrameConfig, aVisibility);
		}
	}

	public void setFrameToConfigDetails (GameFrameConfig aGameFrameConfig) {
		setFrameToConfigDetails (aGameFrameConfig, getVisibileConfig ());
	}

	public void setFrameToConfigDetails (GameFrameConfig aGameFrameConfig, String aVisibility) {
		String tGameName = getGameName ();

		if (aGameFrameConfig == Config.NO_GAME_FRAME) {
			System.out.println ("No Configuration Data for " + tGameName);
		} else {
			setFrameToConfigDefaults (aGameFrameConfig, aVisibility);
		}
	}

	public void setFrameToConfigDefaults (GameFrameConfig aGameFrameConfig, String aVisibility) {
		String tFrameName;
		FrameInfo tFrameInfo;

		tFrameName = getTitle ();
		tFrameInfo = aGameFrameConfig.getFrameInfoFor (tFrameName);
		if (tFrameInfo != FrameInfo.NO_FRAME_INFO) {
			setDefaults (tFrameInfo);
			setDefaultFrameInfo ();
		}
		// If the Visibility Flag passed in is
		// ON- always set it Visible,
		// OFF - always set it to Invisible
		// Otherwise, the Default Frame setting will be used from the Game, or the
		// User's Config File
		if (aVisibility.equals (Visibility.ON.toString ())) {
			setVisible (true);
		} else if (aVisibility.equals (Visibility.OFF.toString ())) {
			setVisible (false);
		}
		setFrameNormal ();
	}

	public int getHexScale () {
		return 0;
	}

	public void setHexScale (int aScale) {
		// DO NOTHING by default - If a Specific Frame Type needs to set the Scale, it
		// will have it's Overriding Function.
		// Primarily for the MapFrame
	}

	public void toTheFront () {
		int tState = super.getExtendedState () & ~Frame.ICONIFIED & Frame.NORMAL;

		super.setExtendedState (tState);
		super.setEnabled (true);
		super.setAlwaysOnTop (true);
		super.setVisible (true);
		super.toFront ();
		super.requestFocus ();
		super.setAlwaysOnTop (false);
	}

	public boolean isMinimized () {
		boolean tIsMinimized;
		
		 if (getExtendedState () == JFrame.ICONIFIED) {
			tIsMinimized = true;
		} else {
			tIsMinimized = false;
		}
		
		return tIsMinimized;
	}
	
	public void setFrameNormal () {
		setState (JFrame.NORMAL);
	}
	
	public Point getOffsetFrame () {
		Point tFramePoint, tNewPoint;
		double tX, tY;
		int tNewX, tNewY;

		tFramePoint = getLocation ();
		tX = tFramePoint.getX ();
		tY = tFramePoint.getY ();
		tNewX = (int) tX + 100;
		tNewY = (int) tY + 100;
		tNewPoint = new Point (tNewX, tNewY);

		return tNewPoint;
	}

	public void showFrame () {
		toTheFront ();
		pack ();
		revalidate ();
	}

	public void setScrollPanePSize (Dimension aNewDimension) {
		if (scrollPane != null) {
			scrollPane.setPreferredSize (aNewDimension);
			scrollPane.revalidate ();
		}
	}

	/**
	 * Create a new JButton with the specified parameters.
	 * 
	 * @param aLabel Label for the Button
	 * @param aAction Action Command for the Button
	 * @param aListener The Action Listener Class
	 * @param aAlignment The Alignment
	 * @return the newly create JButton
	 */
	public JButton setupButton (String aLabel, String aAction, ActionListener aListener, float aAlignment) {
		JButton tButton;
	
		tButton = new JButton (aLabel);
		setupButton (aAction, aListener, aAlignment, tButton);
	
		return tButton;
	}

	/**
	 * Modify the provided JButton with the specified parameters
	 * 
	 * @param aAction Action Command for the Button
	 * @param aListener The Action Listener Class
	 * @param aAlignment The Alignment
	 * @param aButton The Button to be modified
	 */
	public void setupButton (String aAction, ActionListener aListener, float aAlignment, JButton aButton) {
		aButton.setActionCommand (aAction);
		aButton.addActionListener (aListener);
		aButton.setAlignmentX (aAlignment);
	}
}