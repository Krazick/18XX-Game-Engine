package ge18xx.company.benefit;

import javax.swing.JLabel;

import ge18xx.company.Corporation;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.map.MapCell;
import ge18xx.round.action.Action;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

public class PassiveTokenBenefit extends FreeLicenseBenefit {
	public static final String NAME = "Passive Token Placement";
	public static final AttributeName AN_TOKEN_TYPE = new AttributeName ("tokenType");
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
		Corporation tOwningCompany;

		placeAllBenefitTokens (aAction);
		tOwningCompany = getOwningCompany ();
		completeBenefitInUse (tOwningCompany);
		addAdditionalEffects (aAction);

	}
	
	protected MapFrame getMapFrame () {
		GameManager tGameManager;
		MapFrame tMapFrame;

		tGameManager = privateCompany.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();

		return tMapFrame;
	}
	
	protected void placeBenefitToken (MapCell aSelectedMapCell, String aTokenType, int aBenefitValue) {
		MapFrame tMapFrame;
		
		tMapFrame = getMapFrame ();
		tMapFrame.placeBenefitToken (aSelectedMapCell, aTokenType, this, aBenefitValue);
		tMapFrame.revalidate ();
		tMapFrame.repaint ();
	}

	protected void placeAllBenefitTokens (Action aAction) {
		String tMapCellID;
		MapCell tMapCell;
		int tMapCellCount;
		int tMapCellIndex;
		MapFrame tMapFrame;
		HexMap tHexMap;
		
		tMapFrame = getMapFrame ();	
		tHexMap = tMapFrame.getMap ();
		tMapCellCount = getMapCellIDCount ();
		for (tMapCellIndex = 0; tMapCellIndex < tMapCellCount; tMapCellIndex++) {
			tMapCellID = getMapCellID (tMapCellIndex);
			tMapCell = tHexMap.getMapCellForID (tMapCellID);
			placeBenefitToken (tMapCell, tokenType, value);
		}
	}
	
	@Override
	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		
		tBenefitLabel = GUI.NO_LABEL;
		
		return tBenefitLabel;
	}
}
