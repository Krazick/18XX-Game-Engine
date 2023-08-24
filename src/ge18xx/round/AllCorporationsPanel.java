package ge18xx.round;

import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.TrainCompany;
import ge18xx.player.Portfolio;
import ge18xx.train.TrainPortfolio;

public class AllCorporationsPanel extends ListenerPanel {
	private static final String NAME = "All Corporations";
	private static final long serialVersionUID = 1L;

	public AllCorporationsPanel (RoundManager aRoundManager) {
		super (aRoundManager, NAME);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout, aRoundManager, NAME);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered, aRoundManager, NAME);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered, aRoundManager, NAME);
		buildAllCorporationsJPanel ();
	}

	private void buildAllCorporationsJPanel () {
		setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
		updateAllCorporationsJPanel ();
		observeCorporations ();
	}
	
	private void observeCorporations () {
		CorporationList tCorporationList;
		boolean tListenersAdded;
		int tCorporationCount;
		
		addMessage (Corporation.CORPORATION_STATUS_CHANGE);
		addMessage (TrainCompany.CORPORATION_CASH_CHANGED);
		addMessage (Portfolio.CERTIFICATE_ADDED);
		addMessage (Portfolio.CERTIFICATE_REMOVED);
		addMessage (TrainPortfolio.ADDED_TRAIN);
		addMessage (TrainPortfolio.REMOVED_TRAIN);
		tListenersAdded = true;
		
		tCorporationList = roundManager.getShareCompanies ();
		tCorporationCount = tCorporationList.getCorporationCount ();
		if (tCorporationCount > 0) {
			tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);
		}
		tCorporationList = roundManager.getMinors ();
		tCorporationCount = tCorporationList.getCorporationCount ();
		if (tCorporationCount > 0) {
			tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);
		}
		tCorporationList = roundManager.getPrivates ();
		tCorporationCount = tCorporationList.getCorporationCount ();
		if (tCorporationCount > 0) {
			tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);
		}
		
		if (! tListenersAdded) {
			System.err.println ("Not all Listeners added.");
		}
	}
	
	public void updateAllCorporationsJPanel () {
		OperatingRound tOperatingRound;
		CorporationList tCorporationList;

		removeAll ();
		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationList = tOperatingRound.getPrivateCompanies ();
		buildCorpListPanel (tCorporationList);

		tCorporationList = tOperatingRound.getMinorCompanies ();
		buildCorpListPanel (tCorporationList);

		tCorporationList = tOperatingRound.getShareCompanies ();
		buildCorpListPanel (tCorporationList);
		
		revalidate ();
	}

	public void buildCorpListPanel (CorporationList aCorporationList) {
		JPanel tCompanyJPanel;
		int tCorporationCount;
		
		tCorporationCount = aCorporationList.getCorporationCount ();
		if (tCorporationCount > 0) {
			tCompanyJPanel = aCorporationList.buildCompanyJPanel (true);
			add (tCompanyJPanel);
			add (Box.createVerticalStrut (10));
		}
	}

	@Override
	protected void updatePanel () {
		updateAllCorporationsJPanel ();
	}
}
