package ge18xx.round;

import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.CorporationList;

public class AllCorporationsPanel extends ObserverPanel {

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
