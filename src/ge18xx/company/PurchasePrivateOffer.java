package ge18xx.company;

import org.w3c.dom.NodeList;

import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PurchasePrivateOffer extends QueryOffer {
	public static final AttributeName AN_AMOUNT = new AttributeName ("amount");
	public static final AttributeName AN_PRIVATE_ABBREV = new AttributeName ("privateAbbrev");
	public static final String PRIVATE_TYPE = Corporation.PRIVATE_COMPANY;
	
	PrivateCompany privateCompany;
	String privateCompanyAbbrev;
	int amount;

	public PurchasePrivateOffer (String aItemName, String aItemType, PrivateCompany aPrivateCompany,
			int aAmount, String aFromActorName, String aToName, ActorI.ActionStates aOldState) {
		super (aItemName, aItemType, aFromActorName, aToName, aOldState);

		setPrivateCompany (aPrivateCompany);
		setPrivateCompanyAbbrev (aPrivateCompany.getAbbrev ());
		setAmount (aAmount);
	}
	
	public PurchasePrivateOffer (XMLNode aChildNode) {
		super (aChildNode);
		
		XMLNode tPONode;
		NodeList tPurchaseOfferList;
		int tPOCount, tPOIndex;
		String tPrivateAbbrev;
		int tAmount;
		
		tPurchaseOfferList = aChildNode.getChildNodes ();
		tPOCount = tPurchaseOfferList.getLength ();
		for (tPOIndex = 0; tPOIndex < tPOCount; tPOIndex++) {
			tPONode = new XMLNode (tPurchaseOfferList.item (tPOIndex));
			tPrivateAbbrev = tPONode.getThisAttribute (AN_PRIVATE_ABBREV);
			setPrivateCompanyAbbrev (tPrivateAbbrev);
			tAmount = tPONode.getThisIntAttribute (AN_AMOUNT);
			setAmount (tAmount);
		}
	}
	
	@Override
	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;

		tXMLElement = super.getElements (aXMLDocument, aElementName);
		tXMLElement.setAttribute (AN_AMOUNT, amount);
		tXMLElement.setAttribute (AN_PRIVATE_ABBREV, privateCompany.getAbbrev ());

		return tXMLElement;
	}
	
	private void setPrivateCompanyAbbrev (String aPrivateCompayAbbrev) {
		privateCompanyAbbrev = aPrivateCompayAbbrev;
	}
	
	private void setPrivateCompany (PrivateCompany aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}

	private void setAmount (int aAmount) {
		amount = aAmount;
	}
	
	public boolean isPrivateCompany () {
		boolean tIsPrivateCompany = false;

		if (PRIVATE_TYPE.equals (itemType)) {
			tIsPrivateCompany = true;
		} else {
			tIsPrivateCompany = false;
		}

		return tIsPrivateCompany;
	}

	public PrivateCompany getPrivateCompany () {
		return privateCompany;
	}

}