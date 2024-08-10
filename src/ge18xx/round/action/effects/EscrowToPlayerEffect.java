package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.player.EscrowHolderI;
import ge18xx.player.Escrows;
import ge18xx.player.Player;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class EscrowToPlayerEffect extends Effect {
	public final static String NAME = "Add Escrow to Player";
	final static AttributeName AN_ESCROW_TO_PLAYER_COMPANY = new AttributeName ("escrowToPlayerCompany");
	final static AttributeName AN_ESCROW_TO_PLAYER_CASH = new AttributeName ("escrowToPlayerCash");
	final static AttributeName AN_ESCROW_HOLDER_NAME = new AttributeName ("escrowHolderName");
	Escrow escrow;
	int cash;
	String holderName;
	Certificate certificate;

	public EscrowToPlayerEffect (ActorI aActor, Escrow aEscrow) {
		super (NAME, aActor);
		setEscrow (aEscrow);
	}

	public EscrowToPlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		String tCertificateName;
		String tHolderName;
		Certificate tCertificate;
		int tCash;

		tCertificateName = aEffectNode.getThisAttribute (AN_ESCROW_TO_PLAYER_COMPANY);
		tCertificate = aGameManager.getCertificate (tCertificateName, 100, true);
		tCash = aEffectNode.getThisIntAttribute (AN_ESCROW_TO_PLAYER_CASH);
		tHolderName = aEffectNode.getThisAttribute (AN_ESCROW_HOLDER_NAME);
		setCash (tCash);
		escrow = new Escrow (tCertificate, tCash);
		escrow.setName (tHolderName);
		setHolderName (tHolderName);
		setCertificate (tCertificate);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tCompanyAbbrev;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tCompanyAbbrev = certificate.getCompanyAbbrev ();
		tEffectElement.setAttribute (AN_ESCROW_TO_PLAYER_COMPANY, tCompanyAbbrev);
		tEffectElement.setAttribute (AN_ESCROW_TO_PLAYER_CASH, cash);
		tEffectElement.setAttribute (AN_ESCROW_HOLDER_NAME, holderName);

		return tEffectElement;
	}

	public Escrow getEscrow () {
		return escrow;
	}

	public void setEscrow (Escrow aEscrow) {
		escrow = aEscrow;
		saveEscrowValues ();
	}

	public void saveEscrowValues () {
		setCash (escrow.getCash ());
		setHolderName (escrow.getName ());
		setCertificate (escrow.getCertificate ());
	}
	
	public int getCash () {
		return cash;
	}
	
	public String getHolderName () {
		return holderName;
	}
	
	public Certificate getCertificate () {
		return certificate;
	}
	
	public void setCash (int aCash) {
		cash = aCash;
	}
	
	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	public void setHolderName (String aHolderName) {
		holderName = aHolderName;
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + escrow.getCompanyAbbrev () + " made by " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApply;
		EscrowHolderI tEscrowHolder;
		Certificate tCertificate;
		int tCash;

		tCertificate = escrow.getCertificate ();
		tCash = escrow.getCash ();
		tEscrowHolder = (EscrowHolderI) actor;
		tEscrowHolder.addEscrowInfo (tCertificate, tCash);
		tEffectApply = true;

		return tEffectApply;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Player tPlayer;

		tEffectUndone = false;
		tPlayer = (Player) actor;
		tEffectUndone = tPlayer.removeEscrow (escrow, Escrows.ESCROW_CLOSE_MATCH);
		if (! tEffectUndone) {
			setUndoFailureReason ("Escrow was not removed");
		}

		return tEffectUndone;
	}
}
