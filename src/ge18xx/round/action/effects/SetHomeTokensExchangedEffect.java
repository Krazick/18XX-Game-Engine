package ge18xx.round.action.effects;

import ge18xx.company.formation.FormationPhase;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class SetHomeTokensExchangedEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Home Tokens Exchanged";
	final static AttributeName AN_HOME_TOKENS_EXCHANGED = new AttributeName ("homeTokensExchanged");

	public SetHomeTokensExchangedEffect () {
		super ();
		setName (NAME);
	}

	public SetHomeTokensExchangedEffect (String aName) {
		super (aName);
	}

	public SetHomeTokensExchangedEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetHomeTokensExchangedEffect (ActorI aActor, boolean aHomeTokensExchanged) {
		super (NAME, aActor, aHomeTokensExchanged);
	}

	public SetHomeTokensExchangedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_HOME_TOKENS_EXCHANGED);

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
		tFormationPhase.setHomeTokensExchanged (getBooleanFlag ());
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
		tFormationPhase.setHomeTokensExchanged (! getBooleanFlag ());
		tEffectUndone = true;

		return tEffectUndone;
	}
}
