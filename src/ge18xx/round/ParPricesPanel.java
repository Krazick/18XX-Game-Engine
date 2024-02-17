package ge18xx.round;

import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ge18xx.bank.Bank;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;

public class ParPricesPanel extends ListenerPanel {
	private static final long serialVersionUID = 1L;
	private static final String NAME = "Par Prices";
	private static final String PAR_PRICES_LABEL = "Par Prices";
	List<JLabel> parPrices = new LinkedList<JLabel> ();
	List<JLabel> companiesAtPar = new LinkedList<JLabel> ();
	List<JPanel> parPriceLineJPanels = new LinkedList<JPanel> ();
	
	public ParPricesPanel (RoundManager aRoundManager) {
		super (aRoundManager, NAME);
		buildParPrices ();
	}

	public ParPricesPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout, aRoundManager, NAME);
		buildParPrices ();
	}

	public ParPricesPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered, aRoundManager, NAME);
		buildParPrices ();
	}

	public ParPricesPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered, aRoundManager, NAME);
		buildParPrices ();
	}

	@Override
	protected void updatePanel () {
		updateParPrices ();
	}
	
	private void buildParPrices () {
		setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
		buildBorder (PAR_PRICES_LABEL, TitledBorder.CENTER, Color.BLACK);
		observeShareCompanies ();
		updateParPrices ();
	}
	
	private void observeShareCompanies () {
		CorporationList tShareCompanies;
		boolean tListenersAdded;
		
		addMessage (ShareCompany.SET_PAR_PRICE);
		addMessage (TrainCompany.LAST_TRAIN_BOUGHT);
		tShareCompanies = roundManager.getShareCompanies ();
		tListenersAdded = tShareCompanies.addListeners (this);
		if (! tListenersAdded) {
			System.err.println ("Not all Observers added.");
		}
	}

	public void updateParPrices () {
		GameManager tGameManager;
		int tParPriceCount;
		int tMinToFloat;
		int tParPriceIndex;
		int tPrice;
		int tGovtRailwayUniqueParPrice;
		Integer tParPrices [];
		String tPrices [];
		JPanel tParPriceLinePanel;
		JLabel tPriceLabel;
		JLabel tCompaniesAtParLabel;

		parPriceLineJPanels.clear ();
		parPrices.clear ();
		removeAll ();
		tGameManager = roundManager.getGameManager ();
		tParPrices = tGameManager.getAllStartCells ();
		tGovtRailwayUniqueParPrice = getGovtRailwayUniqueParPrice (tGameManager, tParPrices);
		tParPrices = addGovtRailwayParPrice (tGovtRailwayUniqueParPrice, tParPrices);
		tParPriceCount = tParPrices.length;
		tMinToFloat = tGameManager.getMinSharesToFloat ();

		tPrices = new String [tParPriceCount];
		
		for (tParPriceIndex = 0; tParPriceIndex < tParPriceCount; tParPriceIndex++) {
			tPrice = tParPrices [tParPriceIndex].intValue ();
			tPrices [tParPriceIndex] = Bank.formatCash (tPrice);
			tPriceLabel = new JLabel (tPrices [tParPriceIndex]);
			parPrices.add (tPriceLabel);
			tCompaniesAtParLabel = new JLabel ("");
			companiesAtPar.add (tCompaniesAtParLabel);

			tParPriceLinePanel = buildParPriceLinePanel (tParPriceIndex, tMinToFloat, tPrice);
			parPriceLineJPanels.add (tParPriceLinePanel);
			add (parPriceLineJPanels.get (tParPriceIndex));
		}
		updateJustParPrices (tParPriceCount);
	}

	private Integer [] addGovtRailwayParPrice (int aGovtRailwayUniqueParPrice, Integer [] aParPrices) {
		Integer [] tNewParPrices;
		int tParPriceCount;
		int tParPriceIndex;
		
		if (aGovtRailwayUniqueParPrice == 0) {
			tNewParPrices = aParPrices;
		} else {
			tParPriceCount = aParPrices.length + 1;
			tNewParPrices = new Integer [tParPriceCount];
			tNewParPrices [0] = aGovtRailwayUniqueParPrice;
			for (tParPriceIndex = 0; tParPriceIndex < aParPrices.length; tParPriceIndex++) {
				tNewParPrices [tParPriceIndex + 1] = aParPrices [tParPriceIndex];
			}
		}
		
		return tNewParPrices;
	}
	
	private int getGovtRailwayUniqueParPrice (GameManager aGameManager, Integer aParPrices []) {
		int tShareIndex;
		int tShareCount;
		int tParPriceCount;
		int tParPriceIndex;
		boolean tHasUniqueParPrice;
		int tParPrice;
		ShareCompany tShareCompany;
		CorporationList tShareCompanies;	
		
		tShareCompanies = aGameManager.getShareCompanies ();
		tShareCount = tShareCompanies.getCorporationCount ();
		tParPrice = 0;
		tParPriceCount = aParPrices.length;
		tHasUniqueParPrice = true;
		for (tShareIndex = 0; tShareIndex < tShareCount; tShareIndex++) {
			tShareCompany = (ShareCompany) tShareCompanies.getCorporation (tShareIndex);
			if (tShareCompany.isGovtRailway ()) {
				if (!tShareCompany.isUnowned ()) {
					tParPrice = tShareCompany.getParPrice ();
					for (tParPriceIndex = 0; tParPriceIndex < tParPriceCount; tParPriceIndex++) {
						if (tParPrice == aParPrices [tParPriceIndex]) {
							tHasUniqueParPrice = false;
						}
					}
				}
			}
		}
		if (! tHasUniqueParPrice) {
			tParPrice = 0;
		}
		
		return tParPrice;
	}
	
	private JPanel buildParPriceLinePanel (int aParPriceIndex, int aMinToFloat, int aPrice) {
		JPanel tParPriceLinePanel;
		JLabel tMinStartupLabel;
		int tMinStartupCash;
		String tMinStartup;

		tMinStartupCash = aMinToFloat * aPrice;
		tMinStartup  = "[" + aMinToFloat + " / " + Bank.formatCash (tMinStartupCash) + "]";
		tMinStartupLabel = new JLabel (tMinStartup);
		tParPriceLinePanel = new JPanel ();
		tParPriceLinePanel.setLayout (new BoxLayout (tParPriceLinePanel, BoxLayout.X_AXIS));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (tMinStartupLabel);
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (parPrices.get (aParPriceIndex));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.add (companiesAtPar.get (aParPriceIndex));
		tParPriceLinePanel.add (Box.createHorizontalStrut (10));
		tParPriceLinePanel.setAlignmentX (Component.LEFT_ALIGNMENT);

		return tParPriceLinePanel;
	}

	public void updateJustParPrices (int aParPriceCount) {
		OperatingRound tOperatingRound;
		int tCorporationCount;
		int tCorporationIndex;
		int tPriceCount;
		int tPriceIndex;
		int tParPriceIndex;
		CorporationList tCorporationList;
		String tPriceLabel;
		String tParPrice;
		ShareCompany tShareCompany;
		String tCompaniesAtPrice [];

		tOperatingRound = roundManager.getOperatingRound ();
		tCorporationCount = tOperatingRound.getShareCompanyCount ();

		if (tCorporationCount > 0) {
			tCorporationList = tOperatingRound.getShareCompanies ();
			tPriceCount = companiesAtPar.size ();
			tCompaniesAtPrice = new String [tPriceCount];
			for (tCorporationIndex = 0; tCorporationIndex < tCorporationCount; tCorporationIndex++) {
				tShareCompany = (ShareCompany) tCorporationList.getCorporation (tCorporationIndex);
				if (tShareCompany.hasParPrice ()) {
					tParPrice = Bank.formatCash (tShareCompany.getParPrice ());
					for (tParPriceIndex = 0; tParPriceIndex < aParPriceCount; tParPriceIndex++) {
						tPriceLabel = parPrices.get (tParPriceIndex).getText ();
						if (tPriceLabel.equals (tParPrice)) {
							if (tCompaniesAtPrice [tParPriceIndex] == null) {
								tCompaniesAtPrice [tParPriceIndex] = tShareCompany.getAbbrev ();
							} else {
								tCompaniesAtPrice [tParPriceIndex] += ", " + tShareCompany.getAbbrev ();
							}
						}
					}
				}
			}

			for (tPriceIndex = 0; tPriceIndex < tPriceCount; tPriceIndex++) {
				companiesAtPar.get (tPriceIndex).setText (tCompaniesAtPrice [tPriceIndex]);
			}
		}
		revalidate ();
	}
}
