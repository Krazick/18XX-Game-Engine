package ge18xx.company.special;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.utilities.GUI;

public class TriggerClass {
	public static final TriggerClass NO_TRIGGER_CLASS = null;
	
	public TriggerClass () {
		
	}
	
	public TriggerClass (GameManager aGameManager) {
		
	}

	public void rebuildSpecialPanel (int aCurrentPlayerIndex) {
	
	}
	
	public void hideSpecialPanel () {
		
	}
	
	public void showSpecialPanel () {
		
	}
	
	public int updateToNextPlayer (List<Player> aPlayers) {
		return 0;
	}
	
	public void setCurrentPlayerIndex (int aCurrentPlayerIndex) {
		
	}
	
	public JButton buildSpecialButton (String aTitle, String aActionCommand, String aToolTip, ActionListener aActionListener) {
		JButton tSpecialButton;
		boolean tEnabled;
		
		tEnabled = getEnabled (aToolTip);
		tSpecialButton = new JButton (aTitle);
		tSpecialButton.setActionCommand (aActionCommand);
		tSpecialButton.setEnabled (tEnabled);
		tSpecialButton.setToolTipText (aToolTip);
		tSpecialButton.addActionListener (aActionListener);
		
		return tSpecialButton;
	}

	public boolean getEnabled (String aToolTip) {
		boolean tEnabled;
		
		if (GUI.EMPTY_STRING.equals (aToolTip)) {
			tEnabled = true;
		} else {
			tEnabled = false;
		}
		
		return tEnabled;
	}

}
