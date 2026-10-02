package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class HideTileTrayScaleSliderPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "TileTrayScaleSlider";
	public static final ElementName EN_TILE_TRAY_SCALE_SLIDER = new ElementName ("TileTrayScaleSlider");
	public static final AttributeName AN_HIDDEN = new AttributeName ("hidden");
	public static final String buttonText = "Tile Tray Scale Slider is hidden";

	public HideTileTrayScaleSliderPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tHideMapScaleSlider;

		setDecisionType (decisionType);
		tHideMapScaleSlider = new JCheckBox ();
		setupCheckbox (this, tHideMapScaleSlider, buttonText);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tTileTrayScaleSliderElement;
		boolean tHideTileTrayScaleSlider;
		
		tHideTileTrayScaleSlider = hideTileTrayScaleSlider ();
		tTileTrayScaleSliderElement = aXMLDocument.createElement (EN_TILE_TRAY_SCALE_SLIDER);
		tTileTrayScaleSliderElement.setAttribute (AN_HIDDEN, tHideTileTrayScaleSlider);
		
		return tTileTrayScaleSliderElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tChoice;

		tChoice = parseBooleanPreference (aChildNode, AN_HIDDEN, checkBox);
		setDecisionChoice (tChoice);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aEvent) {
		gameManager.updateAllFrames ();
	}
	
	public boolean hideTileTrayScaleSlider () {
		return checkBox.isSelected ();
	}
}
