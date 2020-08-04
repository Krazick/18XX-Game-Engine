package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class RemoveEscrowEffect extends Effect {
	public final static String NAME = "Remove Escrow";
	final static AttributeName AN_ESCROW = new AttributeName ("escrow");
	final static AttributeName AN_ESCROW_TO_PLAYER_COMPANY = new AttributeName ("escrowToPlayerCompany");
	final static AttributeName AN_ESCROW_TO_PLAYER_CASH = new AttributeName ("escrowToPlayerCash");
	Escrow escrow;

	public RemoveEscrowEffect () {
		this (NAME);
	}

	public RemoveEscrowEffect (String aName) {
		super (aName);
	}

	public RemoveEscrowEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public RemoveEscrowEffect (ActorI aActor, Escrow aEscrow) {
		this (NAME, aActor);
		setEscrow (aEscrow);
	}
	
	public RemoveEscrowEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		String tCertificateName;
		Certificate tCertificate;
		int tCash;
//		Player tPlayer;

		tCertificateName = aEffectNode.getThisAttribute (AN_ESCROW_TO_PLAYER_COMPANY);
		tCertificate = aGameManager.getCertificate (tCertificateName, 100, true);
		tCash = aEffectNode.getThisIntAttribute (AN_ESCROW_TO_PLAYER_CASH);
		escrow = new Escrow (tCertificate, tCash);
//		tPlayer = (Player) actor;
//		tPlayer.addEscrowInfo (tCertificate, tCash);
	}
	
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tCompanyAbbrev;
		int tCash;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tCompanyAbbrev = escrow.getCompanyAbbrev ();
		tCash = escrow.getCash ();
		tEffectElement.setAttribute (AN_ESCROW_TO_PLAYER_COMPANY, tCompanyAbbrev);
		tEffectElement.setAttribute (AN_ESCROW_TO_PLAYER_CASH, tCash);
	
		return tEffectElement;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + escrow.getCompanyAbbrev() + 
				" made by " +  actor.getName () + 
				" Amount of " + Bank.formatCash (escrow.getCash ()) + ".");
	}

	public void setEscrow (Escrow aEscrow) {
		escrow = aEscrow;
	}
	
	public Escrow getEscrow () {
		return escrow;
	}
	
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;
		Player tPlayer = (Player) actor;
		
		tPlayer.removeEscrow (escrow, tPlayer.ESCROW_CLOSE_MATCH);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
}
