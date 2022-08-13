package ge18xx.company;

import ge18xx.company.benefit.QueryExchangeBenefit;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ExchangePrivateQuery extends QueryOffer {
	public static final AttributeName AN_PRIVATE_ABBREV = new AttributeName ("privateAbbrev");
	public static final AttributeName AN_BENEFIT_NAME = new AttributeName ("benefitName");
	public static final String BENEFIT_TYPE = QueryExchangeBenefit.NAME;
	PrivateCompany privateCompany;
	String privateAbbrev;
	String benefitName;

	public ExchangePrivateQuery (String aItemName, String aFromActorName, String aToActorName, ActionStates aOldState, 
			PrivateCompany aPrivateCompany, String aBenefitName) {
		super (aItemName, aFromActorName, aToActorName, aOldState);
		setPrivateCompany (aPrivateCompany);
		setPrivateAbbrev (aPrivateCompany.getAbbrev ());
		setBenefitName (aBenefitName);
	}

	public ExchangePrivateQuery (XMLNode aChildNode, GameManager aGameManager) {
		super (aChildNode, aGameManager);
		String tPrivateAbbrev;
		String tBenefitName;
		PrivateCompany tPrivateCompany;
		CorporationList tPrivates;
		
		tPrivateAbbrev = aChildNode.getThisAttribute (AN_PRIVATE_ABBREV);
		tPrivates = aGameManager.getPrivates ();
		tPrivateCompany = (PrivateCompany) tPrivates.getCorporation (tPrivateAbbrev);
		setPrivateCompany (tPrivateCompany);
		tBenefitName = aChildNode.getThisAttribute (AN_BENEFIT_NAME);
		setBenefitName (tBenefitName);
	}
	
	@Override
	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;

		tXMLElement = super.getElements (aXMLDocument, aElementName);
		tXMLElement.setAttribute (AN_BENEFIT_NAME, benefitName);
		tXMLElement.setAttribute (AN_PRIVATE_ABBREV, privateCompany.getAbbrev ());
		tXMLElement.setAttribute (AN_CLASS_NAME, this.getClass ().getName ());

		return tXMLElement;
	}

	private void setBenefitName (String aBenefitName) {
		benefitName = aBenefitName;
	}
	
	private void setPrivateAbbrev (String aPrivateAbbrev) {
		privateAbbrev = aPrivateAbbrev;
	}
	
	private void setPrivateCompany (PrivateCompany aPrivateCompany) {
		privateCompany = aPrivateCompany;
	}

	@Override
	public String getItemType () {
		return BENEFIT_TYPE;
	}
}
