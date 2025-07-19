package ge18xx.company.formation;

import java.awt.Point;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.StartFormationAction;
import geUtilities.xml.XMLFrame;

public class FormPrussian extends FormCompany {
	XMLFrame formationFrame;
	int currentPlayerIndex;

	public FormPrussian (GameManager aGameManager) {
		super (aGameManager);
		
		String tFullFrameTitle;
		
		tFullFrameTitle = setFormationState (ActorI.ActionStates.Nationalization);
		System.out.println ("Initiate Form Prussian, Frame Title: " + tFullFrameTitle +
				" Game: " + gameManager.getActiveGameName ());
		buildAllPlayers (tFullFrameTitle);
		
		gameManager.setTriggerClass (this);
		gameManager.setTriggerFormation (this);
	}
	
	@Override
	public void prepareFormation (StartFormationAction aStartFormationAction) {
		showFormationFrame ();

	}
	
	@Override
	public void showFormationFrame () {
		formationFrame.showFrame ();
	}
	
	@Override
	public void rebuildFormationPanel () {
		int tCurrentPlayerIndex;
		
		tCurrentPlayerIndex = currentPlayerIndex;
		if (tCurrentPlayerIndex >= 0) {
			rebuildFormationPanel (tCurrentPlayerIndex);
		}

	}
	public void buildAllPlayers (String aFrameName) {
		Border tMargin;
		Point tRoundFrameOffset;
		int tHeight;
		int tWidth;
		List<Player> tPlayers;
		PlayerManager tPlayerManager;

		tPlayerManager = gameManager.getPlayerManager ();
		tPlayers = tPlayerManager.getPlayers ();
		formationFrame = new XMLFrame (aFrameName, gameManager);
		formationFrame.setSize (800, 600);
		
		formationJPanel = new JPanel ();
		tMargin = new EmptyBorder (10,10,10,10);
		formationJPanel.setBorder (tMargin);
		
		formationJPanel.setLayout (new BoxLayout (formationJPanel, BoxLayout.Y_AXIS));

		setupPlayers (tPlayerManager, tPlayers);
		formationFrame.buildScrollPane (formationJPanel);

		tRoundFrameOffset = gameManager.getOffsetRoundFrame ();
		formationFrame.setLocation (tRoundFrameOffset);
		gameManager.addNewFrame (formationFrame);
		
		tWidth = 1140;
		tHeight = panelHeight ();
		formationFrame.setSize (tWidth,  tHeight);
	}
	
	@Override
	public void setupPlayers (PlayerManager aPlayerManager, List<Player> aPlayers) {
		int tCurrentPlayerIndex;
		
		findActingPresident ();
		tCurrentPlayerIndex = aPlayerManager.getPlayerIndex (actingPresident);
		setCurrentPlayerIndex (tCurrentPlayerIndex);
		updatePlayers (aPlayers, actingPresident);
	}

	@Override
	public void updatePlayers (List<Player> aPlayers, Player aActingPresident) {
		super.updatePlayers (aPlayers, aActingPresident);

	}
	
	@Override
	public boolean isInterrupting () {
		return true;
	}
}
