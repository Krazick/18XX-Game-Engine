package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ChangeBooleanFlagEffect extends Effect {
	public final static String NAME = "Change Boolean Flag";
	boolean booleanFlag;

	public ChangeBooleanFlagEffect () {
		super ();
		setName (NAME);
	}

	public ChangeBooleanFlagEffect (String aName) {
		super (aName);
	}

	public ChangeBooleanFlagEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public ChangeBooleanFlagEffect (String aName, ActorI aActor, boolean aBooleanFlag) {
		super (aName, aActor);
		setBooleanFlag (aBooleanFlag);
	}

	public ChangeBooleanFlagEffect (XMLNode aEffectNode, GameManager aGameManager, AttributeName aFlagName) {
		super (aEffectNode, aGameManager);
		boolean tBooleanFlag;
		
		tBooleanFlag = aEffectNode.getThisBooleanAttribute (aFlagName);
		setBooleanFlag (tBooleanFlag);

		setName (NAME);
	}

	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN, AttributeName aFlagName) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (aFlagName, booleanFlag);

		return tEffectElement;
	}

	public void setBooleanFlag (boolean aBooleanFlag) {
		booleanFlag = aBooleanFlag;
	}

	public boolean getBooleanFlag () {
		return booleanFlag;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
}
