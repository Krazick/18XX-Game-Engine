package ge18xx.game.userPreferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ClientNameInFramePreference extends UserPreference implements ItemListener {
	public static final ElementName EN_CLIENT_NAME = new ElementName ("ClientName");
	public static final AttributeName AN_IN_FRAME = new AttributeName ("inFrame");
	JCheckBox clientNameInFrame;
	
	public ClientNameInFramePreference (GameManager aGameManager) {
		super (aGameManager);
	}

	@Override
	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		clientNameInFrame = new JCheckBox ("Show Client Name in Frame Titles (Network Games Only)");
		clientNameInFrame.setSelected (false);
		clientNameInFrame.addItemListener (this);
		clientNameInFrame.addActionListener (this);
		aUserPreferencesPanel.add (clientNameInFrame);
	}
	
	public boolean showClientNameInFrameTitle () {
		return clientNameInFrame.isSelected ();
	}

	@Override
	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showClientNameInFrameTitle ();
		tClientNameElement = aXMLDocument.createElement (EN_CLIENT_NAME);
		tClientNameElement.setAttribute (AN_IN_FRAME, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	@Override
	public void parsePreference (XMLNode aChildNode) {
		boolean tInFrame;
		
		tInFrame = aChildNode.getThisBooleanAttribute (AN_IN_FRAME);
		clientNameInFrame.setSelected (tInFrame);
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		gameManager.updateAllFrames ();
	}
}
