package ge18xx.player;

import ge18xx.game.GameManager;

import java.awt.Component;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class StartPacketPortfolio extends Portfolio {
	public final static StartPacketPortfolio NO_START_PACKET = null;
	
	public StartPacketPortfolio () {
		super ();
	}
	
	public StartPacketPortfolio (PortfolioHolderI aHolder) {
		super (aHolder);
	}
	
	@Override
	public JPanel buildPortfolioJPanel (String aTitle, boolean aPrivates, boolean aCoals, boolean aMinors, 
			boolean aShares, String aSelectedButtonLabel, ItemListener aItemListener, 
			GameManager aGameManager) {
		BoxLayout tLayout;
		
		portfolioInfoJPanel = new JPanel ();
		portfolioInfoJPanel.setBorder (BorderFactory.createTitledBorder (aTitle));
		tLayout = new BoxLayout (portfolioInfoJPanel, BoxLayout.Y_AXIS);
		portfolioInfoJPanel.setLayout (tLayout);
		portfolioInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		addJCAndVGlue (portfolioInfoJPanel, null);
		
		return portfolioInfoJPanel;
	}
}
