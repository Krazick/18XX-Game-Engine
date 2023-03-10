package ge18xx.company.benefit;

import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.effects.LayBenefitTokenEffect;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class PassiveTokenBenefit extends FreeLicenseBenefit {
	public final static String NAME = "Passive Token Placement";
	final static AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
	String tokenType;
	
	public PassiveTokenBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		String tTokenType;
		
		setName (NAME);
		tTokenType = aXMLNode.getThisAttribute (AN_TOKEN_TYPE);
		setTokenType (tTokenType);
	}

	public void setTokenType (String aTokenType) {
		tokenType = aTokenType;
	}

	@Override
	public void handlePassive (ShareCompany aShareCompany, Action aAction) {
		placeAllBenefitTokens (aAction);
		setUsed (true);
	}
	
	protected MapFrame getMapFrame () {
		GameManager tGameManager;
		MapFrame tMapFrame;

		tGameManager = privateCompany.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();

		return tMapFrame;
	}
	
	protected void placeBenefitToken (MapCell aSelectedMapCell, int aLocation, String aTokenType,  
					int aBenefitValue) {
		MapFrame tMapFrame;
		
		tMapFrame = getMapFrame ();
		tMapFrame.placeBenefitToken (aSelectedMapCell, aLocation, aTokenType, aBenefitValue);
		tMapFrame.revalidate ();
		tMapFrame.repaint ();
	}

	protected void placeAllBenefitTokens (Action aAction) {
		String tMapCellID;
		MapCell tMapCell;
		int tMapCellCount;
		int tMapCellIndex;
		int tLocation;
		MapFrame tMapFrame;
		HexMap tHexMap;
		LayBenefitTokenEffect tLayBenefitTokenEffect;
		ActorI tActor;
		
		tMapFrame = getMapFrame ();	
		tHexMap = tMapFrame.getMap ();
		tMapCellCount = getMapCellIDCount ();
		for (tMapCellIndex = 0; tMapCellIndex < tMapCellCount; tMapCellIndex++) {
			tMapCellID = getMapCellID (tMapCellIndex);
			tLocation = getLocationInt (tMapCellIndex);
			tMapCell = tHexMap.getMapCellForID (tMapCellID);
			tActor = aAction.getActor ();
			tLayBenefitTokenEffect = new LayBenefitTokenEffect (tActor, tMapCell, tokenType, value);
			System.out.println ("Ready to Place " + tokenType + " Token at " + tMapCellID + " Location " + tLocation);
			placeBenefitToken (tMapCell, tLocation, tokenType, value);
			aAction.addEffect (tLayBenefitTokenEffect);
		}
	}
}
