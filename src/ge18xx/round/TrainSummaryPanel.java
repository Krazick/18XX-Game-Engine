package ge18xx.round;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.GameBank;
import ge18xx.train.TrainPortfolio;

import geUtilities.GUI;
import geUtilities.MessageBean;

public class TrainSummaryPanel extends ListenerPanel {
	private static final long serialVersionUID = 1L;
	private static final String NAME = "Train Summary";
	private static final String TRAIN_SUMMARY_LABEL = NAME;
	private JTextArea trainSummary;
	
	public TrainSummaryPanel (RoundManager aRoundManager) {
		super (aRoundManager, NAME);
		buildTrainSummary ();
	}

	private void setNewTrainSummary () {
		JTextArea tTrainSummary;
		
		tTrainSummary = new JTextArea (GUI.EMPTY_STRING);
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
		add (trainSummary);
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
		if (tBankPoolTrainSummary.equals (GUI.EMPTY_STRING)) {
			tFullTrainSummary = tBankTrainSummary;
		} else {
			tFullTrainSummary = tBankPoolTrainSummary + GUI.NEWLINE + tBankTrainSummary;
		}

		trainSummary.setText (tFullTrainSummary);
		trainSummary.setBackground (GUI.defaultColor);
	}

	public String getTrainSummary (GameBank aBankWithTrains) {
		String tBankTrainSummary;
		String tBankName;
		
		tBankTrainSummary = GUI.EMPTY_STRING;
		tBankName = aBankWithTrains.getName ();
		if (aBankWithTrains.hasAnyTrains ()) {
			tBankTrainSummary = tBankName + GUI.NEWLINE + aBankWithTrains.getTrainSummary ();
		} else if (tBankName == GUI.NULL_STRING) {
			tBankTrainSummary = TrainPortfolio.NO_TRAINS_TEXT;
		} else if (tBankName.equals (Bank.NAME)){
			tBankTrainSummary = TrainPortfolio.NO_TRAINS_TEXT;
		}

		return tBankTrainSummary;
	}
}
