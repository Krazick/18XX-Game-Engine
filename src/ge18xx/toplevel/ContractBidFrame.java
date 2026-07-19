package ge18xx.toplevel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.player.ContractBid;
import ge18xx.player.Player;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLSaveGameI;
import swingTweaks.KButton;

public class ContractBidFrame extends XMLFrame implements ActionListener, XMLSaveGameI {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Contract Bid Frame";
	public static final ContractBidFrame NO_CONTRACT_BID_FRAME = null;
	private final String SIGN = "Sign Contract Bid";
	private final String DONE = "Done Contract Bid";
	private final String UNDO = "Undo";
	JPanel contractBidJPanel;
	JPanel buttonJPanel;
	JPanel fullPanel;
	KButton doneButton;
	KButton undoButton;
	boolean isNetworkGame;
	ContractBid contractBid;

	public ContractBidFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		boolean tIsNetworkGame;
		
		tIsNetworkGame = aGameManager.isNetworkGame ();
		isNetworkGame = tIsNetworkGame;
		fullPanel = new JPanel ();
		fullPanel.setLayout (new BoxLayout (fullPanel, BoxLayout.Y_AXIS));
				
		add (fullPanel);

		System.out.println (NAME + " Constructed");
	}
	
	public void setContractBidJPanel (JPanel aContractBidJPanel) {
		contractBidJPanel = aContractBidJPanel;
	}
	
	public JPanel getContractBidJPanel () {
		return contractBidJPanel;
	}

	public void fillContractBidJPanel (Player aPlayer) {
		JPanel tContractBidJPanel;
		
		if (contractBidJPanel == ContractBid.NO_CONTRACT_BID_PANEL) {
			System.out.println ("Filling requires Building ContractBidJPanel for " + aPlayer.getName ());

			contractBid.buildJPanel ();
			tContractBidJPanel = contractBid.getContractBidJPanel ();
			setContractBidJPanel (tContractBidJPanel);
		}
		buildButtonJPanel ();
		contractBidJPanel.add (buttonJPanel);
		fullPanel.add (contractBidJPanel, 0);
	}
	
	public void buildButtonJPanel () {
		buttonJPanel = new JPanel ();
		buttonJPanel.setLayout (new BoxLayout (buttonJPanel, BoxLayout.X_AXIS));
		buttonJPanel.add (Box.createVerticalStrut (5));
		doneButton = setupButton (SIGN, SIGN);
		buttonJPanel.add (Box.createVerticalStrut (5));
		doneButton = setupButton (DONE, DONE);
		buttonJPanel.add (Box.createVerticalStrut (5));
		undoButton = setupButton (UNDO, UNDO);
		buttonJPanel.add (Box.createVerticalStrut (5));
	}
	
	public KButton setupButton (String aButtonText, String aButtonCommand) {
		KButton tKButton;

		tKButton = new KButton (aButtonText);
		tKButton.setActionCommand (aButtonCommand);
		tKButton.addActionListener (this);
		tKButton.setAlignmentX (Component.CENTER_ALIGNMENT);
		buttonJPanel.add (tKButton);
		if (isNetworkGame) {
			if (aButtonText.equals (UNDO)) {
				tKButton.setEnabled (false);
			}
		}

		return tKButton;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tTheAction;

		tTheAction = aEvent.getActionCommand ();
		if (SIGN.equals (tTheAction)) {
			signContract ();
		}
		if (DONE.equals (tTheAction)) {
			completeContract ();
		}
		if (UNDO.equals (tTheAction)) {
			undoLastAction ();
		}
	}
	
	private void undoLastAction () {
		String tPlayerName;
		Player tPlayer;
		
		tPlayer = contractBid.getPlayer ();
		tPlayerName = tPlayer.getName ();
		System.out.println (NAME + " Action is 'UNDO' for " + tPlayerName);
		
	}

	private void completeContract () {
		String tPlayerName;
		Player tPlayer;
		
		tPlayer = contractBid.getPlayer ();
		tPlayerName = tPlayer.getName ();
		System.out.println (NAME + " Action is 'DONE' for " + tPlayerName);
	}

	private void signContract () {
		String tPlayerName;
		Player tPlayer;
		
		tPlayer = contractBid.getPlayer ();
		tPlayerName = tPlayer.getName ();
		System.out.println (NAME + " Action is 'SIGN' for " + tPlayerName);
	}

	@Override
	public void showFrame () {
		super.showFrame ();
		System.out.println ("Show " + NAME + " for " + contractBid.getPlayer ().getName ());
		contractBid.showContractBidJPanel ();
	}

	public void setContractBid (ContractBid tContractBid) {
		contractBid = tContractBid;
	}
}
