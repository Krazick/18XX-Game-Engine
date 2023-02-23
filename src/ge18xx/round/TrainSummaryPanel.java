package ge18xx.round;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.GUI;
import ge18xx.utilities.MessageBean;

public class TrainSummaryPanel extends ListenerPanel {

	private static final long serialVersionUID = 1L;
	private static final String TRAIN_SUMMARY_LABEL = "Train Summary";
	private JTextArea trainSummary;
	
	public TrainSummaryPanel (RoundManager aRoundManager) {
		super (aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (LayoutManager aLayout, RoundManager aRoundManager) {
		super (aLayout, aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aIsDoubleBuffered, aRoundManager);
		buildTrainSummary ();
	}

	public TrainSummaryPanel (LayoutManager aLayout, boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aLayout, aIsDoubleBuffered, aRoundManager);
		buildTrainSummary ();
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
	protected void updatePanel () {
		updateTrainSummary ();
	}

	private void buildTrainSummary () {	
		buildBorder (TRAIN_SUMMARY_LABEL, TitledBorder.CENTER, Color.BLACK);
		setNewTrainSummary ();
		updateTrainSummary ();
		add (Box.createHorizontalStrut (10));
		add (trainSummary);
		add (Box.createHorizontalStrut (10));
		observeBank ();
	}
	
	private void observeBank () {
		Bank tBank;
		BankPool tBankPool;
		MessageBean tBankBean;
		MessageBean tBankPoolBean;
		
		addMessage (TrainPortfolio.ADDED_TRAIN);
		addMessage (TrainPortfolio.REMOVED_TRAIN);
		tBank = roundManager.getBank ();
		tBankPool = roundManager.getBankPool ();
		
		tBankBean = tBank.getMessageBean ();
		tBankBean.addPropertyChangeListener (this);
		tBankPoolBean = tBankPool.getMessageBean ();
		tBankPoolBean.addPropertyChangeListener (this);
	}
	
	public void updateTrainSummary () {
		String tFullTrainSummary;
		String tBankPoolTrainSummary;
		String tBankTrainSummary;
		Bank tBank;
		BankPool tBankPool;

		tBankPool = roundManager.getBankPool ();
		tBankPoolTrainSummary = getTrainSummary (tBankPool);

		tBank = roundManager.getBank ();
		tBankTrainSummary = getTrainSummary (tBank);
		tFullTrainSummary = tBankPoolTrainSummary + GUI.NEWLINE + tBankTrainSummary;

		trainSummary.setText (tFullTrainSummary);
		trainSummary.setBackground (GUI.defaultColor);
	}

	public String getTrainSummary (GameBank aBankWithTrains) {
		String tBankTrainSummary;
		String tBankName;
		
		tBankTrainSummary = "";
		tBankName = aBankWithTrains.getName ();
		if (aBankWithTrains.hasAnyTrains ()) {
			tBankTrainSummary = tBankName + GUI.NEWLINE + GUI.NEWLINE + aBankWithTrains.getTrainSummary ();
		} else if (tBankName.equals (Bank.NAME)){
			tBankTrainSummary = TrainPortfolio.NO_TRAINS_TEXT;
		}

		return tBankTrainSummary;
	}
}
