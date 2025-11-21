package ge18xx.round.plan;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import geUtilities.xml.GameEngineManager;
import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;


public class PlanFrame extends XMLFrame {

	private static final long serialVersionUID = 1L;
	JPanel mapPanel;
	JPanel tilePanel;
	JPanel buttonPanel;
	KButton discardPlan;
	KButton applyPlan;
	KButton savePlan;
	MapPlan mapPlan;
	
	public PlanFrame (String aFrameName, GameEngineManager aGameManager) {
		super (aFrameName, aGameManager);
		buildMapPanel ();
		buildTilePanel ();
		buildButtonPanel ();
		
		add (mapPanel, BorderLayout.WEST);
		add (tilePanel, BorderLayout.CENTER);
		add (buttonPanel, BorderLayout.EAST);
		setSize (500, 500);
		showFrame ();
	}

	private void buildButtonPanel () {
		JLabel tButtonLabel;
		
		tButtonLabel = new JLabel ("This is a ButtonPanel");
		buttonPanel = new JPanel ();
		buttonPanel.add (tButtonLabel);
	}

	private void buildTilePanel () {
		JLabel tTilePanelLabel;
		
		tTilePanelLabel = new JLabel ("This is a TilePanel");
		tilePanel = new JPanel ();
		tilePanel.add (tTilePanelLabel);
	}

	private void buildMapPanel () {
		JLabel tMapPanelLabel;
		
		tMapPanelLabel = new JLabel ("This is a MapPanel");
		mapPanel = new JPanel ();
		mapPanel.add (tMapPanelLabel);
	}

	public void setPlaceTileMapPlan (PlaceTileMapPlan tPlaceTileMapPlan) {
		// TODO Auto-generated method stub
		mapPlan = tPlaceTileMapPlan;
	}

}
