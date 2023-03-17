package ge18xx.player;

import java.util.LinkedList;

import javax.swing.JLabel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.WinAuctionAction;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class Escrows {
	public final static boolean ESCROW_EXACT_MATCH = true;
	public final static boolean ESCROW_CLOSE_MATCH = false;
	public final static ElementName EN_ESCROWS = new ElementName ("Escrows");
	public final static Escrows NO_ESCROWS = null;
	LinkedList<Escrow> escrows;
	String escrowHolderName;
	EscrowHolderI escrowHolder;

	public Escrows (EscrowHolderI aEscrowHolder) {
		escrows = new LinkedList<> ();
		escrowHolder = aEscrowHolder;
		escrowHolderName = escrowHolder.getName ();
	}

	public XMLElement getEscrowXML (XMLDocument aXMLDocument) {
		XMLElement tXMLEscrows, tXMLEscrowElement;

		tXMLEscrows = aXMLDocument.createElement (EN_ESCROWS);
		for (Escrow tEscrow : escrows) {
			tXMLEscrowElement = tEscrow.getElements (aXMLDocument);
			tXMLEscrows.appendChild (tXMLEscrowElement);
		}

		return tXMLEscrows;
	}

	public void loadEscrowState (XMLNode aPlayerNode) {
		XMLNodeList tXMLEscrowsNodeList;

		tXMLEscrowsNodeList = new XMLNodeList (escrowsParsingRoutine);
		tXMLEscrowsNodeList.parseXMLNodeList (aPlayerNode, EN_ESCROWS);
	}

	ParsingRoutineI escrowsParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadEscrows (aChildNode);
		}
	};

	ParsingRoutineI singleEscrowParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadSingleEscrow (aChildNode);
		}
	};

	private void loadEscrows (XMLNode aEscrowsNode) {
		XMLNodeList tXMLEscrowNodeList;

		tXMLEscrowNodeList = new XMLNodeList (singleEscrowParsingRoutine);
		tXMLEscrowNodeList.parseXMLNodeList (aEscrowsNode, Escrow.EN_ESCROW);
	}

	private void loadSingleEscrow (XMLNode aEscrowsNode) {
		Escrow tEscrow;
		Bank tBank;

		tBank = escrowHolder.getBank ();
		tEscrow = new Escrow (aEscrowsNode, tBank);
		escrows.add (tEscrow);
	}

	public void printAllEscrows () {
		System.out.println ("Printing all Escrows for " + escrowHolder.getName ());	// PRINTLOG
		for (Escrow tFoundEscrow : escrows) {
			System.out.println (tFoundEscrow.getInfo (escrowHolderName));
		}
	}

	public Escrow getEscrowMatching (String aEscrowName) {
		Escrow tFoundEscrow = Escrow.NO_ESCROW;

		for (Escrow tEscrow : escrows) {
			if (tEscrow.getName ().equals (aEscrowName)) {
				tFoundEscrow = tEscrow;
			}
		}

		return tFoundEscrow;
	}

	public Escrow getEscrowFor (Certificate aCertificate) {
		Escrow tEscrow = Escrow.NO_ESCROW;
		Escrow tThisEscrow;
		int tEscrowCount = escrows.size ();

		for (int tEscrowIndex = 0; tEscrowIndex < tEscrowCount; tEscrowIndex++) {
			tThisEscrow = escrows.get (tEscrowIndex);
			if (aCertificate.equals (tThisEscrow.getCertificate ())) {
				tEscrow = tThisEscrow;
			}
		}

		return tEscrow;
	}

	public JLabel getEscrowLabel () {
		String tEscrowText;
		JLabel tEscrowLabel;
		int tEscrowCount;
		int tTotalEscrow;

		tTotalEscrow = getTotalEscrow ();
		tEscrowCount = getEscrowCount ();
		tEscrowText = tEscrowCount + " Bid";
		if (tEscrowCount > 1) {
			tEscrowText += "s";
		}
		tEscrowText += " totaling " + Bank.formatCash (tTotalEscrow);
		tEscrowLabel = new JLabel (tEscrowText);

		return tEscrowLabel;
	}

	public int getTotalEscrow () {
		int tEscrowCount = escrows.size ();
		Escrow tThisEscrow;
		int tTotalEscrow = 0;

		for (int tEscrowIndex = 0; tEscrowIndex < tEscrowCount; tEscrowIndex++) {
			tThisEscrow = escrows.get (tEscrowIndex);
			tTotalEscrow += tThisEscrow.getCash ();
		}

		return tTotalEscrow;
	}

	public Escrow getCheapestEscrow () {
		Escrow tEscrow = Escrow.NO_ESCROW;
		Escrow tThisEscrow;
		int tEscrowCount = escrows.size ();
		int tEscrowAmount = 0;

		for (int tEscrowIndex = 0; tEscrowIndex < tEscrowCount; tEscrowIndex++) {
			tThisEscrow = escrows.get (tEscrowIndex);
			if (tEscrowAmount == 0) {
				tEscrowAmount = tThisEscrow.getCash ();
				tEscrow = tThisEscrow;
			} else {
				if (tThisEscrow.getCash () < tEscrowAmount) {
					tEscrowAmount = tThisEscrow.getCash ();
					tEscrow = tThisEscrow;
				}
			}
		}

		return tEscrow;
	}

	public Escrow getEscrowAt (int aEscrowIndex) {
		Escrow tEscrow;

		tEscrow = escrows.get (aEscrowIndex);

		return tEscrow;
	}

	public int getEscrowCount () {
		return escrows.size ();
	}

	public void refundEscrow (Certificate aCertificate, int aBidAmount, WinAuctionAction aWinAuctionAction) {
		Escrow tEscrow = getMatchingEscrow (aCertificate);

		tEscrow.transferCashTo (escrowHolder, aBidAmount);
		aWinAuctionAction.addRefundEscrowEffect (tEscrow, escrowHolder, aBidAmount);
		removeEscrow (tEscrow);
		aWinAuctionAction.addRemoveEscrowEffect (escrowHolder, tEscrow);
	}

	public void removeAllEscrows () {
		escrows.clear ();
	}

	public Escrow getMatchingEscrow (String aActorName) {
		Escrow tFoundEscrow = (Escrow) ActorI.NO_ACTOR;
		String tEscrowName;
		int tEscrowCount = escrows.size ();
		boolean tEscrowMatched;
		boolean tEscrowWasFound = false;

		if (tEscrowCount > 0) {
			for (Escrow tEscrow : escrows) {
				// Find an Escrow Name that matches for this Player, and return the first. Don't
				// change once found
				if (!tEscrowWasFound) {
					tEscrowName = tEscrow.getName ();
					tEscrowMatched = tEscrowName.equals (aActorName);
					if (tEscrowMatched) {
						tFoundEscrow = tEscrow;
						tEscrowWasFound = true;
					}
				}
			}
		}

		return tFoundEscrow;
	}

	public Escrow getMatchingEscrow (Certificate aCertificate) {
		Escrow tFoundEscrow = (Escrow) ActorI.NO_ACTOR;
		int tEscrowCount = escrows.size ();
		Certificate tFoundCertficate;

		if (tEscrowCount > 0) {
			for (Escrow tEscrow : escrows) {
				tFoundCertficate = tEscrow.getCertificate ();
				// Find an Escrow that matches the Certificate
				if (tFoundCertficate.equals (aCertificate) && (tFoundEscrow == ((Escrow) ActorI.NO_ACTOR))) {
					tFoundEscrow = tEscrow;
				}
			}
		}

		return tFoundEscrow;
	}

	public Escrow addEmptyEscrow (String aName) {
		Escrow tEscrow;

		tEscrow = new Escrow ();
		tEscrow.setName (aName);

		return tEscrow;
	}

	public Escrow addEscrowInfo (Certificate aCertificate, int aAmount) {
		Escrow tEscrow;

		tEscrow = new Escrow (aCertificate);
		tEscrow.setName (escrowHolderName, escrows.size ());
		tEscrow.transferCashTo (escrowHolder, -aAmount);
		escrows.add (tEscrow);
		aCertificate.addBidderInfo (escrowHolder, aAmount);

		return tEscrow;
	}

	public void raiseBid (Certificate aCertificate, int aRaise) {
		int tEscrowCount = escrows.size ();
		Escrow tFoundEscrow;

		if (tEscrowCount > 0) {
			tFoundEscrow = getEscrowFor (aCertificate);
			escrowHolder.transferCashTo (tFoundEscrow, aRaise);
		}
	}

	public boolean removeEscrow (Escrow aEscrow) {
		return removeEscrow (aEscrow, ESCROW_EXACT_MATCH);
	}

	public boolean removeEscrow (Escrow aEscrow, boolean aMatchCriteria) {
		int tEscrowCount = escrows.size ();
		Escrow tEscrow;
		String tPassedEscrowCompany, tFoundEscrowCompany;
		boolean tEscrowRemoved = false;

		if (tEscrowCount > 0) {
			tPassedEscrowCompany = aEscrow.getCompanyAbbrev ();
			for (int tEscrowIndex = 0; tEscrowIndex < tEscrowCount; tEscrowIndex++) {
				if (!tEscrowRemoved) {
					tEscrow = escrows.get (tEscrowIndex);
					if (aMatchCriteria == ESCROW_EXACT_MATCH) {
						if (tEscrow.equals (aEscrow)) {
							escrows.remove (tEscrowIndex);
							tEscrowRemoved = true;
						}
					} else {
						tFoundEscrowCompany = tEscrow.getCompanyAbbrev ();
						if (tPassedEscrowCompany.equals (tFoundEscrowCompany)) {
							escrows.remove (tEscrowIndex);
							tEscrowRemoved = true;
						}
					}
				}
			}
		}

		return tEscrowRemoved;
	}
}
