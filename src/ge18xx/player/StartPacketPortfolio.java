package ge18xx.player;

import ge18xx.game.GameManager;

import java.awt.Component;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class StartPacketPortfolio extends Portfolio {
	public StartPacketPortfolio () {
		super ();
	}
	
	public StartPacketPortfolio (PortfolioHolderI aHolder) {
		super (aHolder);
	}
	
	public JPanel buildPortfolioJPanel (boolean aPrivates, boolean aCoals, boolean aMinors, 
			boolean aShares, String aSelectedButtonLabel, ItemListener aItemListener, 
			GameManager aGameManager) {
		BoxLayout tLayout;
		
		portfolioInfoJPanel = new JPanel ();
		portfolioInfoJPanel.setBorder (BorderFactory.createTitledBorder ("Start Packet Portfolio Information"));
		tLayout = new BoxLayout (portfolioInfoJPanel, BoxLayout.Y_AXIS);
		portfolioInfoJPanel.setLayout (tLayout);
		portfolioInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		addJCAndVGlue (portfolioInfoJPanel, null);
		
		return portfolioInfoJPanel;
	}
}
