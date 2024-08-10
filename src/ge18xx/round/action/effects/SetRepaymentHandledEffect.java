package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class SetRepaymentHandledEffect extends FormationPanelEffect {
	public final static String NAME = "Set Repayment Handled";
	final static AttributeName AN_REPAYMENT_HANDLED = new AttributeName ("repaymentHandled");
	boolean replaymentHandled;

	public SetRepaymentHandledEffect () {
		super ();
		setName (NAME);
	}

	public SetRepaymentHandledEffect (String aName) {
		super (aName);
	}

	public SetRepaymentHandledEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetRepaymentHandledEffect (ActorI aActor, boolean aRepaymentHandled) {
		super (NAME, aActor);
		setRepaymentHandled (aRepaymentHandled);
	}

	public SetRepaymentHandledEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		boolean tRepaymentHandled;
		
		tRepaymentHandled = aEffectNode.getThisBooleanAttribute (AN_REPAYMENT_HANDLED);
		setRepaymentHandled (tRepaymentHandled);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_REPAYMENT_HANDLED, replaymentHandled);

		return tEffectElement;
	}

	public void setRepaymentHandled (boolean aRepaymentFinished) {
		replaymentHandled = aRepaymentFinished;
	}
	
	public boolean getRepaymentHandled () {
		return replaymentHandled;
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
		ShareCompany tShareCompany;
		Player tPresident;
		int tPlayerIndex;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tPresident = (Player) tShareCompany.getPresident ();
			tShareCompany.setRepaymentHandled (replaymentHandled);
			tPlayerIndex = getPlayerIndex (aRoundManager, tPresident);
			rebuildFormationPanel (aRoundManager, tPlayerIndex);
			tEffectApplied = true;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		ShareCompany tShareCompany;
		Player tPresident;
		int tPlayerIndex;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tShareCompany = (ShareCompany) actor;
			tShareCompany.setRepaymentHandled (false);
			tPresident = (Player) tShareCompany.getPresident ();
			tPlayerIndex = getPlayerIndex (aRoundManager, tPresident);
			rebuildFormationPanel (aRoundManager, tPlayerIndex);
			tEffectUndone = true;
		}

		return tEffectUndone;
	}
}
