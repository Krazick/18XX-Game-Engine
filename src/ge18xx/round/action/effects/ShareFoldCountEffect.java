package ge18xx.round.action.effects;

import ge18xx.company.ShareCompany;
import ge18xx.company.formation.FormationPhase;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ShareFoldCountEffect extends Effect {
	public final static String NAME = "Share Fold Count";
	final static AttributeName AN_OLD_SHARE_FOLD_COUNT = new AttributeName ("oldShareFoldCount");
	final static AttributeName AN_NEW_SHARE_FOLD_COUNT = new AttributeName ("newShareFoldCount");
	int oldShareFoldCount;
	int newShareFoldCount;

	public ShareFoldCountEffect () {
		this (NAME);
	}

	public ShareFoldCountEffect (String aName) {
		super (aName);
	}

	public ShareFoldCountEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public ShareFoldCountEffect (ActorI aActor, int aOldShareFoldCount, int aNewShareFoldCount) {
		super (NAME, aActor);
		setOldShareFoldCount (aOldShareFoldCount);
		setNewShareFoldCount (aNewShareFoldCount);
	}

	public ShareFoldCountEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		int tOldShareFoldCount;
		int tNewShareFoldCount;
		
		tOldShareFoldCount = aEffectNode.getThisIntAttribute (AN_OLD_SHARE_FOLD_COUNT);
		setOldShareFoldCount (tOldShareFoldCount);
		tNewShareFoldCount = aEffectNode.getThisIntAttribute (AN_NEW_SHARE_FOLD_COUNT);
		setNewShareFoldCount (tNewShareFoldCount);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_OLD_SHARE_FOLD_COUNT, oldShareFoldCount);
		tEffectElement.setAttribute (AN_NEW_SHARE_FOLD_COUNT, newShareFoldCount);

		return tEffectElement;
	}

	public void setNewShareFoldCount (int aNewShareFoldCount) {
		newShareFoldCount = aNewShareFoldCount;
	}
	
	public int getNewShareFoldCount () {
		return newShareFoldCount;
	}

	public void setOldShareFoldCount (int aOldShareFoldCount) {
		oldShareFoldCount = aOldShareFoldCount;
	}
	
	public int getOldShareFoldCount () {
		return oldShareFoldCount;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " from " + getOldShareFoldCount () + 
					" to " + getNewShareFoldCount ());
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TriggerClass tTriggerClass;
		FormationPhase tLoanRepayment;
		GameManager tGameManager;
		String tNotification;
		String tFoldingCompanyAbbrev;
		ShareCompany tShareCompany;
		int tShareFoldCount;
		
		tEffectApplied = false;
		if (actor.isAShareCompany ()) {
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			if (tTriggerClass instanceof FormationPhase) {
				tLoanRepayment = (FormationPhase) tTriggerClass;
				tShareCompany = (ShareCompany) actor;
				tFoldingCompanyAbbrev = tShareCompany.getAbbrev ();
				tShareFoldCount = tShareCompany.getShareFoldCount ();
				tLoanRepayment.setShareFoldCount (newShareFoldCount);
				
				tNotification = tLoanRepayment.buildFoldNotification (tFoldingCompanyAbbrev, tShareFoldCount);
				tLoanRepayment.setNotificationText (tNotification);
				
				tEffectApplied = true;
			}
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TriggerClass tTriggerClass;
		FormationPhase tLoanRepayment;
		GameManager tGameManager;
		
		tEffectUndone = false;
		if (actor.isAShareCompany ()) {
			tGameManager = aRoundManager.getGameManager ();
			tTriggerClass = tGameManager.getTriggerClass ();
			if (tTriggerClass instanceof FormationPhase) {
				tLoanRepayment = (FormationPhase) tTriggerClass;
				tLoanRepayment.setShareFoldCount (oldShareFoldCount);
				tEffectUndone = true;
			}
		}

		return tEffectUndone;
	}
}
