package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.xml.XMLFrame;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class ShowFrameEffect extends Effect {
	public final static String NAME = "Show Frame";
	final static AttributeName AN_XMLFRAME_TITLE = new AttributeName ("xmlFrameTitle");
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
		setXMLFrame (aXMLFrame);
		setXMLFrameTitle (aXMLFrame.getTitle ());
	}

	public ShowFrameEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		String tXMLFrameTitle;
		XMLFrame tXMLFrame;

		tXMLFrameTitle = aEffectNode.getThisAttribute (AN_XMLFRAME_TITLE);
		tXMLFrame = aGameManager.getXMLFrameName (tXMLFrameTitle);
		setXMLFrame (tXMLFrame);
	}

	private void setXMLFrame (XMLFrame aXMLFrame) {
		xmlFrame = aXMLFrame;
	}

	public void setXMLFrameTitle (String aXMLFrameTitle) {
		xmlFrameTitle = aXMLFrameTitle;
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
		boolean tEffectApplied = false;

		if (xmlFrame != XMLFrame.NO_XML_FRAME) {
			xmlFrame.setVisible (false);
		}
		tEffectApplied = true;

		return tEffectApplied;
	}
}
