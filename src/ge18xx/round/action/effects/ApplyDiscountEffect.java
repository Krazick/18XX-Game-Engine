package ge18xx.round.action.effects;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ApplyDiscountEffect extends Effect {
	public final static String NAME = "Apply Discount";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_OLD_DISCOUNT = new AttributeName ("oldDiscount");
	final static AttributeName AN_NEW_DISCOUNT = new AttributeName ("newDiscount");
	String companyAbbrev;
	int oldDiscount;
	int newDiscount;

	public ApplyDiscountEffect () {
		super ();
		setCompanyAbbrev (Corporation.NO_NAME_STRING);
		setOldDiscount (0);
		setNewDiscount (0);
	}

	public ApplyDiscountEffect (ActorI aActor, String aCompanyName, int aOldDiscount, int aNewDiscount) {
		super (NAME, aActor);
		setCompanyAbbrev (aCompanyName);
		setOldDiscount (aOldDiscount);
		setNewDiscount (aNewDiscount);
	}

	public ApplyDiscountEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tCompanyAbbrev;
		Certificate tCertificate;
		int tOldDiscount;
		int tNewDiscount;

		tCompanyAbbrev = aEffectNode.getThisAttribute (AN_COMPANY_ABBREV);
		tCertificate = aGameManager.getCertificate (tCompanyAbbrev, 100, true);
		tOldDiscount = aEffectNode.getThisIntAttribute (AN_OLD_DISCOUNT);
		tNewDiscount = aEffectNode.getThisIntAttribute (AN_NEW_DISCOUNT);
		setCompanyAbbrev (tCompanyAbbrev);
		setOldDiscount (tOldDiscount);
		setNewDiscount (tNewDiscount);

		tCertificate.setDiscount (tNewDiscount);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_COMPANY_ABBREV, companyAbbrev);
		tEffectElement.setAttribute (AN_OLD_DISCOUNT, oldDiscount);
		tEffectElement.setAttribute (AN_NEW_DISCOUNT, newDiscount);

		return tEffectElement;
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	public int getOldDiscount () {
		return oldDiscount;
	}

	public int getNewDiscount () {
		return newDiscount;
	}

	private void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}

	private void setOldDiscount (int aOldDiscount) {
		oldDiscount = aOldDiscount;
	}

	private void setNewDiscount (int aNewDiscount) {
		newDiscount = aNewDiscount;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + getEffectReport ());
	}

	public String getEffectReport () {
		return (name + " for " + companyAbbrev + " change Discount from "
				+ Bank.formatCash (oldDiscount) + " to " + Bank.formatCash (newDiscount) + ".");		
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		Certificate tCertificate;
		int tPercentage;
		boolean tPresidentShare;

		tPercentage = 100;
		tPresidentShare = true;
		tCertificate = aRoundManager.getCertificate (companyAbbrev, tPercentage, tPresidentShare);
		tEffectApplied = false;
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.setDiscount (newDiscount);
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The President (" + tPercentage + "%) Certificate for " +
					companyAbbrev + " was not Found.");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		Certificate tCertificate;
		int tPercentage;
		boolean tPresidentShare;

		tPercentage = 100;
		tPresidentShare = true;
		tCertificate = aRoundManager.getCertificate (companyAbbrev, tPercentage, tPresidentShare);
		tEffectUndone = false;
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tCertificate.setDiscount (oldDiscount);
			tEffectUndone = true;
		} else {
			setUndoFailureReason ("The President (" + tPercentage + "%) Certificate for " +
					companyAbbrev + " was not Found.");
		}

		return tEffectUndone;
	}
}