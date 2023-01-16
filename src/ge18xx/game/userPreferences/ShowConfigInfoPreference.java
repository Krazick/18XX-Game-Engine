package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ShowConfigInfoPreference extends UserPreference implements ItemListener {
	public static final ElementName EN_CONFIG_INFO = new ElementName ("ConfigInfo");
	public static final AttributeName AN_SHOW = new AttributeName ("show");
	JCheckBox showConfigInfoFrame;
	
	public ShowConfigInfoPreference (GameManager aGameManager) {
		super (aGameManager);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		showConfigInfoFrame = new JCheckBox ("Show Config Info File (full Path) when saving");
		showConfigInfoFrame.setSelected (false);
		showConfigInfoFrame.addItemListener (this);
		showConfigInfoFrame.addActionListener (this);
		aUserPreferencesPanel.add (showConfigInfoFrame);
		aUserPreferencesPanel.add (Box.createVerticalStrut (5));
	}
	
	public boolean showConfigInfoFileInfo () {
		return showConfigInfoFrame.isSelected ();
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showConfigInfoFileInfo ();
		tClientNameElement = aXMLDocument.createElement (EN_CONFIG_INFO);
		tClientNameElement.setAttribute (AN_SHOW, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tShow;
		
		tShow = aChildNode.getThisBooleanAttribute (AN_SHOW);
		showConfigInfoFrame.setSelected (tShow);
	}
	
	@Override
	public void itemStateChanged (ItemEvent aItemEvent) {
		gameManager.updateAllFrames ();
	}
}
