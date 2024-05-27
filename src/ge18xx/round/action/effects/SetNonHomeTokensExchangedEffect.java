package ge18xx.round.action.effects;

import ge18xx.company.formation.FormationPhase;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class SetNonHomeTokensExchangedEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Non Home Tokens Exchanged";
	final static AttributeName AN_NON_HOME_TOKENS_EXCHANGED = new AttributeName ("nonHomeTokensExchanged");

	public SetNonHomeTokensExchangedEffect () {
		super ();
		setName (NAME);
	}

	public SetNonHomeTokensExchangedEffect (String aName) {
		super (aName);
	}

	public SetNonHomeTokensExchangedEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetNonHomeTokensExchangedEffect (ActorI aActor, boolean aHomeTokensExchanged) {
		super (NAME, aActor, aHomeTokensExchanged);
	}

	public SetNonHomeTokensExchangedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_NON_HOME_TOKENS_EXCHANGED);
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_NON_HOME_TOKENS_EXCHANGED);

		return tEffectElement;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;
		FormationPhase tFormationPhase;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tFormationPhase = (FormationPhase) tGameManager.getTriggerClass ();
		tFormationPhase.setNonHomeTokensExchanged (getBooleanFlag ());
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		FormationPhase tFormationPhase;

		tEffectUndone = false;
		tGameManager = aRoundManager.getGameManager ();
		tFormationPhase = (FormationPhase) tGameManager.getTriggerClass ();
		tFormationPhase.setNonHomeTokensExchanged (! getBooleanFlag ());
		tEffectUndone = true;

		return tEffectUndone;
	}
}
