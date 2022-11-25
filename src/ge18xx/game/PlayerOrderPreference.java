package ge18xx.game;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ge18xx.player.Player;
import ge18xx.player.PlayerManager;

public class PlayerOrderPreference {

	ButtonGroup buttonGroup;
	JRadioButton playerOrderButtons [];

	public PlayerOrderPreference () {
		buttonGroup = new ButtonGroup ();
	}

	public void buildUserPreferences (JPanel aUserPreferencesPanel) {
		ButtonGroup buttonGroup;
		JLabel tPlayerOrderLabel;
		int tPlayerOrderIndex;
		
		tPlayerOrderLabel = new JLabel ("Player Order in Round Frame");
		aUserPreferencesPanel.add (tPlayerOrderLabel);
		playerOrderButtons = new JRadioButton [4];
		playerOrderButtons [0] = new JRadioButton ("Client Player first");
		playerOrderButtons [0].setSelected (true);
		playerOrderButtons [1]  = new JRadioButton ("Priority Player at Start of Stock Round First"); 
		playerOrderButtons [2]  = new JRadioButton ("Priority Player always First"); 
		playerOrderButtons [3]  = new JRadioButton ("Current Player First"); 
		buttonGroup = new ButtonGroup ();
		for (tPlayerOrderIndex = 0; tPlayerOrderIndex < 4; tPlayerOrderIndex++) {
			buttonGroup.add (playerOrderButtons [tPlayerOrderIndex]);
			aUserPreferencesPanel.add (playerOrderButtons [tPlayerOrderIndex]);
		}
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
	
	public String getFirstPlayerName (GameManager aGameManager) {
		String tFirstPlayerName;
		int tPlayerOrderPreference;
		int tPriorityPlayerIndex;
		PlayerManager tPlayerManager;
		Player tPlayer;
		
		tPlayerOrderPreference = getPlayerOrderPreference ();
		switch (tPlayerOrderPreference) {
			case 1:
				tFirstPlayerName = aGameManager.getClientUserName ();
				break;
				
			case 2:
				tPlayerManager = aGameManager.getPlayerManager ();
				tPriorityPlayerIndex = tPlayerManager.getPriorityPlayerIndex ();
				tPlayer = tPlayerManager.getPlayer (tPriorityPlayerIndex);
				tFirstPlayerName = tPlayer.getName ();
				break;
				
			case 3:
				tPlayer = aGameManager.getCurrentPlayer ();
				tFirstPlayerName = tPlayer.getName ();
				break;
				
			default:
				tFirstPlayerName = aGameManager.getClientUserName ();	
		}
		
		return tFirstPlayerName;
	}

}
