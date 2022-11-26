package ge18xx.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.StockRound;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PlayerOrderPreference implements ActionListener {
	public static final ElementName EN_PLAYER_ORDER = new ElementName ("PlayerOrder");
	public static final AttributeName AN_ORDER_TYPE = new AttributeName ("orderType");

	ButtonGroup buttonGroup;
	JRadioButton playerOrderButtons [];
	GameManager gameManager;

	public PlayerOrderPreference (GameManager aGameManager) {
		buttonGroup = new ButtonGroup ();
		gameManager = aGameManager;
	}

	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		ButtonGroup buttonGroup;
		JLabel tPlayerOrderLabel;
		int tPlayerOrderIndex;
		
		tPlayerOrderLabel = new JLabel ("Player Order in Round Frame");
		aUserPreferencesPanel.add (tPlayerOrderLabel);
		playerOrderButtons = new JRadioButton [4];
		playerOrderButtons [0] = setupRadioButton ("Client Player first", "ClientFirst");
		playerOrderButtons [0].setSelected (true);
		playerOrderButtons [1] = setupRadioButton ("Priority Player at Start of Stock Round First", "StockPriorityFirst"); 
		playerOrderButtons [2] = setupRadioButton ("Priority Player always First", "PriorityFirst"); 
		playerOrderButtons [3] = setupRadioButton ("Current Player First", "CurrentFirst"); 
		buttonGroup = new ButtonGroup ();
		for (tPlayerOrderIndex = 0; tPlayerOrderIndex < 4; tPlayerOrderIndex++) {
			buttonGroup.add (playerOrderButtons [tPlayerOrderIndex]);
			aUserPreferencesPanel.add (playerOrderButtons [tPlayerOrderIndex]);
		}
	}
	
	public JRadioButton setupRadioButton (String aLabel, String aActionCommand) {
		JRadioButton tRadioButton;
		
		tRadioButton = new JRadioButton (aLabel);
		tRadioButton.setActionCommand (aActionCommand);
		tRadioButton.addActionListener (this);
		
		return tRadioButton;
	}
	
	public int getPlayerOrderPreference () {
		int tPlayerOrderIndex;
		int tSelectedPlayerOrder;
		
		tSelectedPlayerOrder = 0;
		for (tPlayerOrderIndex = 0; tPlayerOrderIndex < 4; tPlayerOrderIndex++) {
			if (playerOrderButtons [tPlayerOrderIndex].isSelected ()) {
				tSelectedPlayerOrder = tPlayerOrderIndex;
			}
		}

		return tSelectedPlayerOrder;
	}
	
	public String getFirstPlayerName () {
		String tFirstPlayerName;
		int tPlayerOrderPreference;
		int tPriorityPlayerIndex;
		PlayerManager tPlayerManager;
		StockRound tStockRound;
		Player tPlayer;
		
		tPlayerOrderPreference = getPlayerOrderPreference ();
		switch (tPlayerOrderPreference) {
			case 1:
				tStockRound = gameManager.getStockRound ();
				tPlayerManager = gameManager.getPlayerManager ();
				tPriorityPlayerIndex = tStockRound.getStartRoundPriorityIndex ();
				tPlayer = tPlayerManager.getPlayer (tPriorityPlayerIndex);
				tFirstPlayerName = tPlayer.getName ();
				break;
				
			case 2:
				tPlayerManager = gameManager.getPlayerManager ();
				tPriorityPlayerIndex = tPlayerManager.getPriorityPlayerIndex ();
				tPlayer = tPlayerManager.getPlayer (tPriorityPlayerIndex);
				tFirstPlayerName = tPlayer.getName ();
				break;
				
			case 3:
				tPlayer = gameManager.getCurrentPlayer ();
				tFirstPlayerName = tPlayer.getName ();
				break;
				
			default:
				tFirstPlayerName = gameManager.getClientUserName ();	
		}
		
		return tFirstPlayerName;
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tPlayerOrderElement;
		int tPlayerOrder;
		
		tPlayerOrder = getPlayerOrderPreference ();
		tPlayerOrderElement = aXMLDocument.createElement (EN_PLAYER_ORDER);
		tPlayerOrderElement.setAttribute (AN_ORDER_TYPE, tPlayerOrder);
		
		return tPlayerOrderElement;
	}

	public void parsePlayerOrder (XMLNode aChildNode) {
		int tPlayerOrderType;
		
		playerOrderButtons [0].setSelected (false);
		tPlayerOrderType = aChildNode.getThisIntAttribute (AN_ORDER_TYPE);
		playerOrderButtons [tPlayerOrderType].setSelected (true);
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		gameManager.updateRoundFrame ();
	}
}
