package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineIO;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class Escrow implements CashHolderI {
	public static final ElementName EN_ESCROW = new ElementName ("Escrow");
	public static final AttributeName AN_CASH = new AttributeName ("cash");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_ACTION_STATE = new AttributeName ("actionState");
	public static final AttributeName AN_PRIVATE_NAME = new AttributeName ("privateName");
	private final String NO_NAME = null;
	public final static Escrow NO_ESCROW = null;
	int cash;
	String name;
	ActionStates actionState;
	Certificate certificate;
	
	Escrow () {
		setCash (0);
		setName (NO_NAME);
		actionState = ActionStates.NoAction;
		setCertificate (Certificate.NO_CERTIFICATE);
	}

	Escrow (Certificate aCertificate) {
		setCash (0);
		setName (NO_NAME);
		actionState = ActionStates.NoAction;
		setCertificate (aCertificate);
	}
	
	public Escrow (Certificate aCertificate, int aCash) {
		setCash (aCash);
		setName (NO_NAME);
		actionState = ActionStates.NoAction;
		setCertificate (aCertificate);
	}
//	<Escrow actionState="No Action" cash="120" name="0) Escrow for Mark2">
//	<Certificate abbrev="M&amp;H" isPresident="true" percentage="100">
//	<Bidders>
//	<Bidder cash="115" name="Jim"/>
//	<Bidder cash="120" name="Mark2"/>
//	</Bidders>
//	</Certificate>
//	</Escrow>
	
	public Escrow (XMLNode aEscrowXMLNode, Bank aBank) {
		int tCash;
		String tName;
		XMLNodeList tXMLCertificateNodeList;
		
		tName = aEscrowXMLNode.getThisAttribute (AN_NAME);
		tCash = aEscrowXMLNode.getThisIntAttribute (AN_CASH);
		
		System.out.println ("Escrow Node Formatted " + aEscrowXMLNode.toString ());
		tXMLCertificateNodeList = new XMLNodeList (certificateParsingRoutine, aBank);
		tXMLCertificateNodeList.parseXMLNodeList (aEscrowXMLNode, Certificate.EN_CERTIFICATE);

		System.out.println ("Parsing an Escrow XML Node with Name " + tName + " Cash " + tCash);
		actionState = ActionStates.NoAction;
		setCash (tCash);
		setName (tName);
		
	}
	
	ParsingRoutineIO certificateParsingRoutine  = new ParsingRoutineIO () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
		}

		@Override
		public void foundItemMatchKey1 (XMLNode aCertificateNode, Object aBankObject) {
			Certificate tCertificate;
			String tAbbrev;
			int tPercentage;
			boolean tIsPresident;
			Bank tBank;
			
			tBank = (Bank) aBankObject;
			tAbbrev = aCertificateNode.getThisAttribute (Corporation.AN_ABBREV);
			tIsPresident = aCertificateNode.getThisBooleanAttribute (Certificate.AN_IS_PRESIDENT);
			tPercentage = aCertificateNode.getThisIntAttribute (Certificate.AN_PERCENTAGE);
			tCertificate = tBank.getMatchingCertificate (tAbbrev, tPercentage, tIsPresident);
			setCertificate (tCertificate);
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				System.err.println ("--- Did not find Certificate for " + tAbbrev + " from Bank");
			}
			tCertificate.addBiddersInfo (aCertificateNode);
		}
	};

	@Override
	public String getStateName () {
		return actionState.toString ();
	}

	@Override
	public void addCash (int aAmount) {
		cash += aAmount;
	}

	@Override
	public int getCash () {
		return cash;
	}

	@Override
	public String getName () {
		return name;
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public void setName (String aName, int aIndex) {
		setName (aIndex + getUnindexedName (aName));
	}
	
	public static String getUnindexedName (String aName) {
		return ") Escrow for " + aName;
	}
	
	public String getCompanyAbbrev () {
		return certificate.getCompanyAbbrev ();
	}
	
	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
	public void setCash (int aCash) {
		cash = aCash;
	}
	
	public void setActionState (ActionStates aActionState) {
		actionState = aActionState;
	}
	
	public void setCertificate (Certificate aCertificate, int aIndex) {
		certificate = aCertificate;
		setName (aCertificate.getCompanyAbbrev (), aIndex);
	}
	
	public Certificate getCertificate () {
		return certificate;
	}
	
	public void showInfo () {
		System.out.println (name + " for " + certificate.getCompanyAbbrev () + " Amount " + Bank.formatCash (cash));
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLCertificateElement;
		
		tXMLElement = aXMLDocument.createElement (EN_ESCROW);
		tXMLElement.setAttribute (AN_CASH, cash);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_ACTION_STATE, actionState.toString ());
		tXMLCertificateElement = certificate.getElement (aXMLDocument);
		tXMLElement.appendChild (tXMLCertificateElement);

		return tXMLElement;
	}
	
	public boolean isAPrivateCompany () {
		return false;
	}

	@Override
	public void transferCashTo (CashHolderI aToCashHolder, int aAmount) {
		// Transfer the specified Cash Amount to the Cash Holder
		// If the aAmount is Negative, it is going from the "To Cash Holder" to the Escrow
		aToCashHolder.addCash (aAmount);
		addCash (-aAmount);
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the Escrow State
	}
	
	@Override
	public boolean isAPlayer () {
		return false;
	}
	
	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public boolean isABank () {
		return false;
	}

	@Override
	public boolean isACorporation () {
		return false;
	}
	
	@Override
	public String getAbbrev () {
		return getName ();
	}

	public void printInfo (String aHolderName) {
		System.out.println ("+++Escrow Holder: " + aHolderName + " Escrow Name " + name + 
				" Cert for " + certificate.getCompanyAbbrev () + " Amount " + Bank.formatCash (cash));
		
	}
}
