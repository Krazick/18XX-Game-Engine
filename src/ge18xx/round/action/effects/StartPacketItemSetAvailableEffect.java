package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.bank.StartPacketFrame;
import ge18xx.bank.StartPacketItem;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class StartPacketItemSetAvailableEffect extends Effect {
	public final static String NAME = "Start Packet Item Set Available";
	final static AttributeName AN_AVAILABLE = new AttributeName ("available");
	StartPacketItem startPacketItem;
	boolean available;
	
	public StartPacketItemSetAvailableEffect () {
	}

	public StartPacketItemSetAvailableEffect (String aName) {
		super (aName);
	}

	public StartPacketItemSetAvailableEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public StartPacketItemSetAvailableEffect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		super (aName, aActor, aBenefitInUse);
	}

	public StartPacketItemSetAvailableEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		boolean tAvailable;
		int tCorporationID;
		StartPacketItem tStartPacketItem;
		StartPacketFrame tStartPacketFrame;
		Bank tBank;
		
		tAvailable = aEffectNode.getThisBooleanAttribute (AN_AVAILABLE);
		tCorporationID = aEffectNode.getThisIntAttribute (StartPacketItem.AN_CORPORATION_ID);
		tBank = aGameManager.getBank ();
		tStartPacketFrame = tBank.getStartPacketFrame ();
		tStartPacketItem = tStartPacketFrame.getStartPacketItem (tCorporationID);
		setStartPacketItem (tStartPacketItem);
		setAvailable (tAvailable);
	}
	
	public StartPacketItemSetAvailableEffect (ActorI aActor, StartPacketItem aStartPacketItem, 
			boolean aAvailable) {
		super (NAME, aActor);
		setStartPacketItem (aStartPacketItem);
		setAvailable (aAvailable);
	}
	
	public void setAvailable (boolean aAvailable) {
		available = aAvailable;
	}
	
	public void setStartPacketItem (StartPacketItem aStartPacketItem) {
		startPacketItem = aStartPacketItem;
	}
	
	public StartPacketItem getStartPacketItem () {
		return startPacketItem;
	}
		
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;
		
		tEffectReport = REPORT_PREFIX + name + " flag as " + available +  " for " 
					+ startPacketItem.getCorporationAbbrev () + ".";
					
		return tEffectReport;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		startPacketItem.getEffectElements (aXMLDocument, tEffectElement);
		
		tEffectElement.setAttribute (AN_AVAILABLE, available);

		return tEffectElement;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		startPacketItem.setAvailable (available);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;
		startPacketItem.setAvailable (true);
		
		tEffectUndone = true;

		return tEffectUndone;
	}

}
