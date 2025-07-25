package ge18xx.round.action.effects;

import ge18xx.company.formation.FormCompany;
import ge18xx.game.GameManager;
import ge18xx.round.FormationRound;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetAllPlayerSharesHandledEffect extends FormationPanelEffect {
	public final static String NAME = "Set All PlayerShares Handled";
	final static AttributeName AN_ALL_PLAYER_SHARES_HANDLED = new AttributeName ("allPlayerSharesHandled");
	boolean allPlayerSharesHandled;

	public SetAllPlayerSharesHandledEffect (ActorI aActor, boolean aAllPlayerSharesHandledEffect) {
		super (NAME, aActor);
		setAllPlayerSharesHandledEffect (aAllPlayerSharesHandledEffect);
	}

	public SetAllPlayerSharesHandledEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		boolean tSetAllPlayerSharesHandledEffect;
		
		tSetAllPlayerSharesHandledEffect = aEffectNode.getThisBooleanAttribute (AN_ALL_PLAYER_SHARES_HANDLED);
		setAllPlayerSharesHandledEffect (tSetAllPlayerSharesHandledEffect);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_ALL_PLAYER_SHARES_HANDLED, allPlayerSharesHandled);

		return tEffectElement;
	}

	public void setAllPlayerSharesHandledEffect (boolean aAllPlayerSharesHandledEffect) {
		allPlayerSharesHandled = aAllPlayerSharesHandledEffect;
	}
	
	public boolean getAllPlayerSharesHandledEffect () {
		return allPlayerSharesHandled;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " to TRUE.");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		FormationRound tFormationRound;
		FormCompany tFormCGR;
		
		tEffectApplied = false;
		if (actor.isAPlayer ()) {
			tFormationRound = aRoundManager.getFormationRound ();
			tFormCGR = (FormCompany) tFormationRound.getTriggerFormationClass ();
			tFormCGR.setAllPlayerSharesHandled (allPlayerSharesHandled);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		FormationRound tFormationRound;
		FormCompany tFormCGR;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tFormationRound = aRoundManager.getFormationRound ();
			tFormCGR = (FormCompany) tFormationRound.getTriggerFormationClass ();
			tFormCGR.setAllPlayerSharesHandled (allPlayerSharesHandled);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
