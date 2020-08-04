package ge18xx.round.action.effects;

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

public class EscrowToPlayerEffect extends Effect {
	public final static String NAME = "Add Escrow to Player";
	final static AttributeName AN_ESCROW_TO_PLAYER_COMPANY = new AttributeName ("escrowToPlayerCompany");
	final static AttributeName AN_ESCROW_TO_PLAYER_CASH = new AttributeName ("escrowToPlayerCash");
	Escrow escrow;

	public EscrowToPlayerEffect () {
		super (NAME, ActorI.NO_ACTOR);
		setActor (ActorI.NO_ACTOR);
		setEscrow (Escrow.NO_ESCROW);
	}

	public EscrowToPlayerEffect (ActorI aActor, Escrow aEscrow) {
		super (NAME, aActor);
		setEscrow (aEscrow);
	}

	public EscrowToPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		
		String tCertificateName;
		Certificate tCertificate;
		int tCash;
		Player tPlayer;
		
		tCertificateName = aEffectNode.getThisAttribute (AN_ESCROW_TO_PLAYER_COMPANY);
		tCertificate = aGameManager.getCertificate (tCertificateName, 100, true);
		tCash = aEffectNode.getThisIntAttribute (AN_ESCROW_TO_PLAYER_CASH);
		escrow = new Escrow (tCertificate, tCash);
		tPlayer = (Player) actor;
		tPlayer.addEscrowInfo (tCertificate, tCash);
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

	public Escrow getEscrow () {
		return escrow;
	}
	
	public void setEscrow (Escrow aEscrow) {
		escrow = aEscrow;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + escrow.getCompanyAbbrev() + " made by "+  actor.getName () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApply;
		
		// The creation of the Escrow when the Action is Parsed 
		// means we don't have to apply the effect of the Action.
		tEffectApply = true;
		
		return tEffectApply;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;
		
		tEffectUndone = false;
		tPlayer = (Player) actor;
		tPlayer.removeEscrow (escrow);
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
