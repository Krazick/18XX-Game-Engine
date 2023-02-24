package ge18xx.round;

import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.TrainCompany;
import ge18xx.player.Portfolio;

public class AllCorporationsPanel extends ListenerPanel {

	private static final long serialVersionUID = 1L;
//	private static final String ALL_CORPORATIONS_JPANEL_LABEL = "All Corporations Information";

	public AllCorporationsPanel (RoundManager aRoundManager) {
		super (aRoundManager);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout, aRoundManager);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered, aRoundManager);
		buildAllCorporationsJPanel ();
	}

	public AllCorporationsPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered, aRoundManager);
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
		
		addMessage (Corporation.CORPORATION_STATUS_CHANGE);
		addMessage (TrainCompany.CASH_TRANSFER);
		addMessage (Portfolio.CERTIFICATE_ADDED);
		addMessage (Portfolio.CERTIFICATE_REMOVED);
		tListenersAdded = true;
		
		tCorporationList = roundManager.getShareCompanies ();
		tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);;
		tCorporationList = roundManager.getMinors ();
		tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);
		tCorporationList = roundManager.getPrivates ();
		tListenersAdded = tListenersAdded && tCorporationList.addListeners (this);
		
		if (! tListenersAdded) {
			System.err.println ("Not all Listeners added.");
		}

	}
	
	public void updateAllCorporationsJPanel () {
		JPanel tCompanyJPanel;
		OperatingRound tOperatingRound;
		int tCorporationCount;
		CorporationList tCorporationList;

		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationCount = tOperatingRound.getPrivateCompanyCount ();
		removeAll ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getPrivateCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (true);
			add (tCompanyJPanel);
			add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getMinorCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getMinorCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (true);
			add (tCompanyJPanel);
			add (Box.createVerticalStrut (10));
		}

		tCorporationCount = tOperatingRound.getShareCompanyCount ();
		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tCompanyJPanel = tCorporationList.buildCompanyJPanel (false);
			add (tCompanyJPanel);
			add (Box.createVerticalStrut (10));
		}
		revalidate ();
	}

	@Override
	protected void updatePanel () {
		updateAllCorporationsJPanel ();
	}
}