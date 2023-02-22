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

public class ParPricesPanel extends ObserverPanel {
	private static final long serialVersionUID = 1L;
	private static final String PAR_PRICES_LABEL = "Par Prices";
	List<JLabel> parPrices = new LinkedList<JLabel> ();
	List<JLabel> companiesAtPar = new LinkedList<JLabel> ();
	List<JPanel> parPriceLineJPanels = new LinkedList<JPanel> ();
	
	public ParPricesPanel (RoundManager aRoundManager) {
		super (aRoundManager);
		buildParPrices ();
	}

	public ParPricesPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout, aRoundManager);
		buildParPrices ();
	}

	public ParPricesPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered, aRoundManager);
		buildParPrices ();
	}

	public ParPricesPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered, aRoundManager);
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
		boolean tObserversAdded;
		
		addMessage (ShareCompany.SET_PAR_PRICE);
		addMessage (TrainCompany.LAST_TRAIN_BOUGHT);
		tShareCompanies = roundManager.getShareCompanies ();
		tObserversAdded = tShareCompanies.addObservers (this);
		if (! tObserversAdded) {
			System.err.println ("Not all Observers added.");
		}
	}

	public void updateParPrices () {
		GameManager tGameManager;
		int tParPriceCount;
		Integer tParPrices [];
		int tMinToFloat;
		int tParPriceIndex;
		int tPrice;
		String [] tPrices;
		JPanel tParPriceLinePanel;
		JLabel tPriceLabel;
		JLabel tCompaniesAtParLabel;

		parPriceLineJPanels.clear ();
		parPrices.clear ();
		removeAll ();
		tGameManager = roundManager.getGameManager ();
		tParPrices = tGameManager.getAllStartCells ();
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
