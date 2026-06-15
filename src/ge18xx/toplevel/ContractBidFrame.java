package ge18xx.toplevel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ge18xx.game.GameManager;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLSaveGameI;

public class ContractBidFrame extends XMLFrame implements ActionListener, XMLSaveGameI {
	private static final long serialVersionUID = 1L;
	public static final ContractBidFrame NO_CONTRACT_BID_FRAME = null;
	
	public ContractBidFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		System.out.println ("Contract Bid Frame Constructed");
	}
	
	@Override
	public void actionPerformed (ActionEvent e) {
		System.out.println ("Contract Bid Frame Action to be done");
	}
}
