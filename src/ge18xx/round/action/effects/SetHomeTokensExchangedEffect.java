package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCGR;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetHomeTokensExchangedEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Home Tokens Exchanged";
	final static AttributeName AN_HOME_TOKENS_EXCHANGED = new AttributeName ("homeTokensExchanged");

	public SetHomeTokensExchangedEffect (ActorI aActor, boolean aHomeTokensExchanged) {
		super (NAME, aActor, aHomeTokensExchanged);
	}

	public SetHomeTokensExchangedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager, AN_HOME_TOKENS_EXCHANGED);
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
		FormCGR tFormCGR;
		
		tEffectApplied = false;
		tGameManager = aRoundManager.getGameManager ();
		tFormCGR = (FormCGR) tGameManager.getTriggerClass ();
		tFormCGR.setHomeTokensExchanged (getBooleanFlag ());
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;
		FormCGR tFormCGR;

		tEffectUndone = false;
		tGameManager = aRoundManager.getGameManager ();
		tFormCGR = (FormCGR) tGameManager.getTriggerClass ();
		tFormCGR.setHomeTokensExchanged (! getBooleanFlag ());
		tEffectUndone = true;

		return tEffectUndone;
	}
}
