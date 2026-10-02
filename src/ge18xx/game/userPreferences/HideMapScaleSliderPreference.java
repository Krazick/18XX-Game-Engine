package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class HideMapScaleSliderPreference extends TrueFalseDecisionPreference implements ItemListener {
	public static final String decisionType = "MapScaleSlider";
	public static final ElementName EN_MAP_SCALE_SLIDER = new ElementName ("MapScaleSlider");
	public static final AttributeName AN_HIDDEN = new AttributeName ("hidden");
	public static final String buttonText = "Map Scale Slider is hidden";

	public HideMapScaleSliderPreference (GameManager aGameManager) {
		super (aGameManager);
		
		JCheckBox tHideMapScaleSlider;

		setDecisionType (decisionType);
		tHideMapScaleSlider = new JCheckBox ();
		setupCheckbox (this, tHideMapScaleSlider, buttonText);
	}
	
	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		super.buildUserPreferences (aUserPreferencesPanel);
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tMapScaleSliderElement;
		boolean tHideMapScaleSlider;
		
		tHideMapScaleSlider = hideMapScaleSlider ();
		tMapScaleSliderElement = aXMLDocument.createElement (EN_MAP_SCALE_SLIDER);
		tMapScaleSliderElement.setAttribute (AN_HIDDEN, tHideMapScaleSlider);
		
		return tMapScaleSliderElement;
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
	
	public boolean hideMapScaleSlider () {
		return checkBox.isSelected ();
	}
}
