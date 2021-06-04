package ge18xx.company.benefit;

import javax.swing.JButton;
import javax.swing.JPanel;

import ge18xx.company.CorporationFrame;
import ge18xx.company.PrivateCompany;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class TilePlacementBenefit extends MapBenefit {
	boolean extraTilePlacement;
	final static AttributeName AN_EXTRA = new AttributeName ("extra");

	public TilePlacementBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		
		boolean tExtraTilePlacement;
		
		tExtraTilePlacement = aXMLNode.getThisBooleanAttribute (AN_EXTRA);
		setExtraTilePlacement (tExtraTilePlacement);
	}
	
	private void setExtraTilePlacement (boolean aExtraTilePlacement) {
		extraTilePlacement = aExtraTilePlacement;
	}
	
	@Override
	public String getNewButtonLabel (PrivateCompany aPrivateCompany) {
		String tNewButtonText;
		
		tNewButtonText = "Place Tile on " + aPrivateCompany.getAbbrev () + " Home";
		
		return tNewButtonText;
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		String tOwningCorpAbbrev;
		JButton tPlaceTileButton;
		
		if (shouldConfigure (aPrivateCompany)) {
			tOwningCorpAbbrev = aPrivateCompany.getOwnerName ();
			System.out.println ("Should Configure Buttons for " + tOwningCorpAbbrev + 
						" for Tile Placement with Private " + aPrivateCompany.getAbbrev ());
			tPlaceTileButton = new JButton (getNewButtonLabel (aPrivateCompany));
			tPlaceTileButton.setActionCommand (CorporationFrame.PLACE_TILE_PRIVATE);
			aButtonRow.add (tPlaceTileButton);
		}
	}
}
