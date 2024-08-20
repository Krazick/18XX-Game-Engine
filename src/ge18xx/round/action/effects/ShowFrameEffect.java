package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.GUI;

public class ShowFrameEffect extends Effect {
	public static final AttributeName AN_XMLFRAME_TITLE = new AttributeName ("xmlFrameTitle");
	public static final String NAME = "Show Frame";
	XMLFrame xmlFrame;
	String xmlFrameTitle;

	public ShowFrameEffect () {
		this (NAME, ActorI.NO_ACTOR);
	}

	public ShowFrameEffect (String aName, ActorI aActor) {
		this (aName, aActor, XMLFrame.NO_XML_FRAME);
	}

	public ShowFrameEffect (ActorI aActor, XMLFrame aXMLFrame) {
		this (NAME, aActor, aXMLFrame);
	}

	public ShowFrameEffect (String aName, ActorI aActor, XMLFrame aXMLFrame) {
		super (aName, aActor);
		GameManager tGameManager;
		
		setXMLFrame (aXMLFrame);
		tGameManager = (GameManager) aXMLFrame.getGameManager ();
		setXMLFrameTitle (tGameManager, aXMLFrame.getTitle ());
	}

	public ShowFrameEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
		
		String tXMLFrameTitle;
		XMLFrame tXMLFrame;

		tXMLFrameTitle = aEffectNode.getThisAttribute (AN_XMLFRAME_TITLE);
		setXMLFrameTitle (aGameManager, tXMLFrameTitle);	// Always set the Title of the Frame
		tXMLFrame = aGameManager.getXMLFrameNamed (tXMLFrameTitle);
		setXMLFrame (tXMLFrame);				// Set the XML Frame, and if not found it will be NO_FRAME anyway
	}

	private void setXMLFrame (XMLFrame aXMLFrame) {
		xmlFrame = aXMLFrame;
	}

	public void setXMLFrameTitle (GameManager aGameManager, String aXMLFrameTitle) {
		String tClientUserName;
		String tBriefName;
		
		if (aXMLFrameTitle != GUI.EMPTY_STRING) {
			tClientUserName = aGameManager.getClientUserName ();
			tBriefName = aXMLFrameTitle.replaceAll (" \\(" + tClientUserName + "\\)", "");
			xmlFrameTitle = tBriefName;
		}
	}
	
	public XMLFrame getXMLFrame () {
		return xmlFrame;
	}
	
	public String getXMLFrameTitle () {
		return xmlFrameTitle;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;

		if (xmlFrame != XMLFrame.NO_XML_FRAME) {
			xmlFrame.setVisible (true);
		}
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_XMLFRAME_TITLE, xmlFrameTitle);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " Show " + xmlFrameTitle + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = false;
		if (xmlFrame != XMLFrame.NO_XML_FRAME) {
			xmlFrame.setVisible (false);
		}
		tEffectApplied = true;

		return tEffectApplied;
	}
}
