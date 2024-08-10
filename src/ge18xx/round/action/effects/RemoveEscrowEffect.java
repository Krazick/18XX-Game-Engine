package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.player.Escrow;
import ge18xx.player.EscrowHolderI;
import ge18xx.player.Escrows;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class RemoveEscrowEffect extends Effect {
	public final static String NAME = "Remove Escrow Record";
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

		tCertificateName = aEffectNode.getThisAttribute (AN_ESCROW_TO_PLAYER_COMPANY);
		tCertificate = aGameManager.getCertificate (tCertificateName, 100, true);
		tCash = aEffectNode.getThisIntAttribute (AN_ESCROW_TO_PLAYER_CASH);
		escrow = new Escrow (tCertificate, tCash);
	}

	@Override
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
		return (REPORT_PREFIX + name + " for " + escrow.getCompanyAbbrev () + " made by " + actor.getName ()
				 + ".");
	}

	public void setEscrow (Escrow aEscrow) {
		escrow = aEscrow;
	}

	public Escrow getEscrow () {
		return escrow;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;
		EscrowHolderI tHolder = (EscrowHolderI) actor;

		tHolder.removeEscrow (escrow, Escrows.ESCROW_CLOSE_MATCH);
		tEffectApplied = true;

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
