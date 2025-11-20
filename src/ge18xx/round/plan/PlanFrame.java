package ge18xx.round.plan;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.toplevel.MapFrame;
import geUtilities.GUI;
import geUtilities.xml.XMLFrame;

public class PlanFrame extends XMLFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Plan";
	GameManager gameManager;
	HexMap hexMap;
	JLabel mapPlanLabel;
	JPanel allButtonsJPanel;
	public JScrollPane mapPlanScrollPane;

	public PlanFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		HexMap tHexMap;
		MapFrame tMapFrame;
		
		gameManager = (GameManager) gameEngineManager;
		tMapFrame = gameManager.getMapFrame ();
		tHexMap = tMapFrame.getMap ();
		buildMapScrollPanel (tHexMap);
		buildNorthPanel ();
		setSize (500, 500);
	}
	
	private void buildNorthPanel () {
		JLabel tMapPlanLabel;
		
		tMapPlanLabel = new JLabel ("Plan for Placing Tile on a MapCell");
		add (tMapPlanLabel);
	}

	private void buildMapScrollPanel (HexMap aHexMap) {
		setHexMap (aHexMap);
		buildScrollPane (hexMap, BorderLayout.CENTER);
	}
	
	public void buildScrollPane (JLabel aMapJLabel, String aBorderLayout) {
		mapPlanScrollPane = new JScrollPane ();
		mapPlanScrollPane.add (aMapJLabel);
		mapPlanScrollPane.setViewportView (mapPlanLabel);
		if (aBorderLayout != GUI.NULL_STRING) {
			add (mapPlanScrollPane, aBorderLayout);
		} else {
			add (mapPlanScrollPane);
		}
	}

	@Override
	public void showFrame () {
		refreshMap ();
		super.showFrame ();
	}
	
	public void refreshMap () {
		Icon tIconFromMap;
		ImageIcon tImageIcon;
		
		tIconFromMap = hexMap.getIcon ();
		if (tIconFromMap instanceof ImageIcon) {
			tImageIcon = (ImageIcon) tIconFromMap;
			mapPlanLabel.setIcon (tImageIcon);
		} else {
			System.err.println ("Could not find an ImageIcon on the Label");
		}
	}
	
	public void setHexMap (HexMap aHexMap) {
		hexMap = aHexMap;
	}
	
	@Override
	public void actionPerformed (ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
