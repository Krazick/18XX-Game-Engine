package ge18xx.toplevel;

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

import java.awt.Point;
import java.io.IOException;

import javax.swing.JFrame;

public class XMLFrame extends JFrame {
	public static enum Visibility { ON, OFF, CONFIG_VALUE };
	
	private static final long serialVersionUID = 1L;
	public static final XMLFrame NO_XML_FRAME = null;
	int defaultWidth;
	int defaultHeight;
	int defaultXLocation;
	int defaultYLocation;
	int defaultHexSize;
	boolean defaultVisible;
	String gameName;
	
	public XMLFrame (String aFrameName) {
		this (aFrameName, GameManager.NO_GAME_NAME);
	}
	
	public XMLFrame (String aFrameName, String aGameName) {
		super (aFrameName);
		gameName = aGameName;
	}
	
	public String getGameName () {
		return gameName;
	}
	
	public String extractFrameName () {
		String tFrameName;
		String tPattern = "^(.*) \\([A-Za-z][A-Za-z0-9_]*\\)$";
		
		tFrameName = getTitle ();
		if (tFrameName.matches (tPattern)) {
			int tIndexLastSpace = tFrameName.lastIndexOf (" ");
			tFrameName = tFrameName.substring (0, tIndexLastSpace);
		}
		
		return tFrameName;
	}

	public boolean loadXML (String aXMLFileName, LoadableXMLI aLoadableObject) throws IOException {
		boolean tXMLFileWasLoaded;
		
		if (! ("".equals (aXMLFileName))) {
			try {
				XMLDocument tXMLDocument = new XMLDocument (aXMLFileName);
				aLoadableObject.loadXML (tXMLDocument);
				tXMLFileWasLoaded = true;
			} catch (Exception tException) {
				System.err.println ("Oops, mucked up the XML " + aLoadableObject.getTypeName () + " File Access [" + aXMLFileName + "].");
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
	
	public void setDefaults (int aWidth, int aHeight, int aXLocation, int aYLocation, boolean aVisible, int aHexSize) {
		defaultWidth = aWidth;
		defaultHeight = aHeight;
		defaultXLocation = aXLocation;
		defaultYLocation = aYLocation;
		defaultVisible = aVisible;
		defaultHexSize = aHexSize;
	}
	
	public void setDefaults (FrameInfo aFrameInfo) {
		setDefaults (aFrameInfo.getWidth (), aFrameInfo.getHeight (),
				aFrameInfo.getXLocation (), aFrameInfo.getYLocation (), 
				aFrameInfo.getVisible (), aFrameInfo.getHexSize ());
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
	
	public void setDefaultFrameInfo () {
		setLocation (defaultXLocation, defaultYLocation);
		setSize (defaultWidth, defaultHeight);
		setVisible (defaultVisible);
		if (defaultHexSize > 0) {
			setHexScale (defaultHexSize);
		}
	}
	
	public XMLElement getXMLFrameElement (XMLDocument aXMLDocument) {
		XMLElement tXMLFrameElement = XMLElement.NO_XML_ELEMENT;
		FrameInfo tFrameInfo;
		GameFrameConfig tGameFrameConfig = new GameFrameConfig ("template");
		
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
		//		ON- always set it Visible, 
		//		OFF - always set it to Invisible
		//		Otherwise, the Default Frame setting will be used from the Game, or the User's Config File
		if (aVisibility.equals (Visibility.ON.toString ())) {
			setVisible (true);
		} else if (aVisibility.equals (Visibility.OFF.toString ())) {
			setVisible (false);				
		}
	}
	
	public int getHexScale () {
		return 0;
	}
	
	public void setHexScale (int aScale) {
		// DO NOTHING by default - If a Specific Frame Type needs to set the Scale, it will have it's Overriding Function.
		// Primarily for the MapFrame
	}
	
	public void toTheFront () {
	    int tState = super.getExtendedState () & ~JFrame.ICONIFIED & JFrame.NORMAL;

	    super.setExtendedState (tState);
	    super.setEnabled (true);
	    super.setAlwaysOnTop (true);
	    super.setVisible (true);
	    super.toFront ();
	    super.requestFocus ();
	    super.setAlwaysOnTop (false);
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

}