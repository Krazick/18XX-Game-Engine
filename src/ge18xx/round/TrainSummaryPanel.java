package ge18xx.round;

import java.awt.Color;
import java.awt.LayoutManager;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.GUI;

public class TrainSummaryPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private JTextArea trainSummary;
	RoundManager roundManager;
	
	public TrainSummaryPanel (RoundManager aRoundManager) {
		super ();
		setRoundManager (aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (LayoutManager aLayout, RoundManager aRoundManager) {
		super (aLayout);
		setRoundManager (aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (LayoutManager aLayout, boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aLayout, aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		buildTrainSummary ();
	}

	private void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}

	private void setNewTrainSummary () {
		JTextArea tTrainSummary;
		
		tTrainSummary = new JTextArea ("");
		tTrainSummary.setEditable (false);
		setTrainSummary (tTrainSummary);
	}
	
	private void setTrainSummary (JTextArea aTrainSummary) {
		trainSummary = aTrainSummary;
	}
	
	@Override
	public void update (Observable aObservable, Object aArg) {
		updateTrainSummary ();
	}

	private void buildTrainSummary () {
		Border tBorder1;
		Border tBorder2;
		
		setNewTrainSummary ();
		updateTrainSummary ();
		tBorder1 = BorderFactory.createLineBorder (Color.BLACK);
		tBorder2 = BorderFactory.createTitledBorder (tBorder1, "Train Summary", TitledBorder.CENTER, TitledBorder.TOP);
		setBorder (tBorder2);
		add (Box.createHorizontalStrut (10));
		add (trainSummary);
		add (Box.createHorizontalStrut (10));
		observeBank ();
	}
	
	private void observeBank () {
		Bank tBank;
		BankPool tBankPool;
		
		tBank = roundManager.getBank ();
		tBankPool = roundManager.getBankPool ();
		tBank.addObserver (this);
		tBankPool.addObserver (this);
	}
	
	public void updateTrainSummary () {
		String tFullTrainSummary;
		String tBankPoolTrainSummary;
		String tBankTrainSummary;
		Bank tBank;
		BankPool tBankPool;

		System.out.println ("Updating Train Summary inside TrainSummaryPanel");
		tBankPool = roundManager.getBankPool ();
		tBankPoolTrainSummary = getTrainSummary (tBankPool);

		tBank = roundManager.getBank ();
		tBankTrainSummary = getTrainSummary (tBank);
		tFullTrainSummary = tBankPoolTrainSummary + GUI.NEWLINE + tBankTrainSummary;

		trainSummary.setText (tFullTrainSummary);
		trainSummary.setBackground (GUI.defaultColor);
	}

	public String getTrainSummary (GameBank aBankWithTrains) {
		String tBankTrainSummary = "";
		String tBankName;
		
		tBankName = aBankWithTrains.getName ();
		if (aBankWithTrains.hasAnyTrains ()) {
			tBankTrainSummary = tBankName + GUI.NEWLINE + GUI.NEWLINE + aBankWithTrains.getTrainSummary ();
		} else if (tBankName.equals (Bank.NAME)){
			tBankTrainSummary = TrainPortfolio.NO_TRAINS_TEXT;
		}

		return tBankTrainSummary;
	}

}
