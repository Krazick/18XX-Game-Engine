package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ChangeRoundIDEffect extends Effect {
	public final static String NAME = "Change Round ID";
	final static AttributeName AN_OLD_ROUND_ID = new AttributeName ("oldRoundID");
	final static AttributeName AN_NEW_ROUND_ID = new AttributeName ("newRoundID");
	public final static String NO_ID = null;
	String oldRoundID;
	String newRoundID;
	
	public ChangeRoundIDEffect () {
		super ();
		setName (NAME);
		setOldRoundID (NO_ID);
		setNewRoundID (NO_ID);
	}

	public ChangeRoundIDEffect (ActorI aActor, String aOldRoundID, String aNewRoundID) {
		super (NAME, aActor);
		setOldRoundID (aOldRoundID);
		setNewRoundID (aNewRoundID);
	}

	public ChangeRoundIDEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tOldRoundID;
		String tNewRoundID;
		
		tOldRoundID = aEffectNode.getThisAttribute (AN_OLD_ROUND_ID);
		setOldRoundID (tOldRoundID);
		tNewRoundID = aEffectNode.getThisAttribute (AN_NEW_ROUND_ID);
		setNewRoundID (tNewRoundID);
	}

	public String getOldRoundID () {
		return oldRoundID;
	}

	public String getNewRoundID () {
		return newRoundID;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
		tEffectElement.setAttribute (AN_OLD_ROUND_ID, getOldRoundID ());
		tEffectElement.setAttribute (AN_NEW_ROUND_ID, getNewRoundID ());
	
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " of " +  actor.getName () + " from " + oldRoundID + " to " + newRoundID + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setOldRoundID (String aRoundID) {
		oldRoundID = aRoundID;
	}
	
	public void setNewRoundID (String aNewRoundID) {
		newRoundID = aNewRoundID;
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Round tRound;
		
		tEffectApplied = false;
		System.out.println ("Apply " + name + " of " + newRoundID + " to " + actor.getName ());
		tRound = aRoundManager.getRoundByTypeName (actor.getName ());
		if (tRound == Round.NO_ROUND) {
			System.err.println ("Actor " + actor.getName () + " is not a recognized Round Instance");
		} else {
			tRound.setID (newRoundID);
			aRoundManager.setCurrentOR (tRound.getIDPart2 ());
			tEffectApplied = true;
		}
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Round tRound;
		
		tEffectUndone = false;
		System.out.println ("Undo " + name + " of " + oldRoundID + " from " + actor.getName ());
		tRound = aRoundManager.getRoundByTypeName (actor.getName ());
		if (tRound == Round.NO_ROUND) {
			System.out.println ("Actor " + actor.getName () + " is not a recognized Round Instance");
		} else {
			tRound.setID (oldRoundID);
			tEffectUndone = true;
		}
		
		return tEffectUndone;
	}
}
