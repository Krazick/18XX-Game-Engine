package ge18xx.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ClientNameInFramePreference implements ActionListener, ItemListener {
	public static final ElementName EN_CLIENT_NAME = new ElementName ("ClientName");
	public static final AttributeName AN_IN_FRAME = new AttributeName ("inFrame");
	JCheckBox clientNameInFrame;
	GameManager gameManager;
	
	public ClientNameInFramePreference (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		clientNameInFrame = new JCheckBox ("Show Client Name in Frame Titles");
		clientNameInFrame.setSelected (false);
		clientNameInFrame.addItemListener (this);
		clientNameInFrame.addActionListener (this);
		aUserPreferencesPanel.add (clientNameInFrame);
	}
	
	public boolean showClientNameInFrameTitle () {
		return clientNameInFrame.isSelected ();
	}
	

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tClientNameElement;
		boolean tShowClientNameInFrame;
		
		tShowClientNameInFrame = showClientNameInFrameTitle ();
		tClientNameElement = aXMLDocument.createElement (EN_CLIENT_NAME);
		tClientNameElement.setAttribute (AN_IN_FRAME, tShowClientNameInFrame);
		
		return tClientNameElement;
	}

	public void parseClientName (XMLNode aChildNode) {
		boolean tInFrame;
		
		tInFrame = aChildNode.getThisBooleanAttribute (AN_IN_FRAME);
		clientNameInFrame.setSelected (tInFrame);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		gameManager.updateAllFrames ();
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		gameManager.updateAllFrames ();
	}
}
