package ge18xx.player;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;

//
//  Portfolio.java
//  Game_18XX
//
//  Created by Mark Smith on 11/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.bank.StartPacketFrame;
import ge18xx.bank.StartPacketItem;
import ge18xx.company.Certificate;
import ge18xx.company.CertificateHolderI;
import ge18xx.company.Corporation;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.FrameButton;
import ge18xx.game.GameManager;
import ge18xx.round.action.BuyStockAction;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

/**
 *
 * Will store and Manage the Certificates owned by a PortfolioHolderI
 *
 * @author marksmith
 *
 */
public class Portfolio implements CertificateHolderI {
	public final static ElementName EN_PORTFOLIO = new ElementName ("Portfolio");
	public final static ElementName EN_BIDDERS = new ElementName ("Bidders");
	public final static AttributeName AN_PRIVATE_INDEX = new AttributeName ("privateIndex");
	public final static AttributeName AN_MINOR_INDEX = new AttributeName ("minorIndex");
	public final static AttributeName AN_SHARE_INDEX = new AttributeName ("shareIndex");
	public final static boolean REMOVE_CERTIFICATE = true;
	public final static Portfolio NO_PORTFOLIO = null;
	public final static JPanel NO_PORTFOLIO_JPANEL = null;
	public final static String CERTIFICATE_ADDED = "CERTIFICATE ADDED";
	public final static String CERTIFICATE_REMOVED = "CERTIFICATE REMOVED";
	public final static String NO_PORTFOLIO_LABEL = ">> NO PORTFOLIO <<";
	public final static String NO_COMPANY_YET = ">> NONE YET <<";
	public final static String NO_CERTIFICATES = ">> NO CERTIFICATES <<";
	public final static String NO_NAME_STRING = "<NONE>";
	public final static XMLElement NO_BIDDERS = null;
	public final static int NO_COMPONENT_INDEX = -1;

	/* These items are set once, no need to save/load */
	protected JPanel portfolioInfoJPanel;
	private PortfolioHolderI holder;
	private final Border EMPTY_BORDER = BorderFactory.createEmptyBorder ();

	/* These items change during the Game, must be saved/loaded */
	List<Certificate> certificates;
	int privateIndex;
	int minorIndex;
	int shareIndex;

	public Portfolio () {
		this (PortfolioHolderI.NO_HOLDER);
	}

	public Portfolio (PortfolioHolderI aHolder) {
		certificates = new LinkedList<> ();
		setHolder (aHolder);
		privateIndex = NO_COMPONENT_INDEX;
		minorIndex = NO_COMPONENT_INDEX;
		shareIndex = NO_COMPONENT_INDEX;
	}

	@Override
	public void addCertificate (Certificate aCertificate) {
		aCertificate.setOwner (this);
		holder.updateListeners (CERTIFICATE_ADDED + " to " + holder.getName ());
		certificates.add (aCertificate);
		Collections.sort (certificates);
	}

	public void addJCAndVGlue (JComponent aParentComponent, JComponent aNewChildComponent) {
		if (aParentComponent != null) {
			if (aNewChildComponent != null) {
				aParentComponent.add (aNewChildComponent);
			}
			aParentComponent.add (Box.createVerticalGlue ());
		}
	}

	public void addJCAndHGlue (JComponent aParentComponent, JComponent aNewChildComponent) {
		if (aParentComponent != null) {
			if (aNewChildComponent != null) {
				aParentComponent.add (aNewChildComponent);
			}
			aParentComponent.add (Box.createHorizontalGlue ());
		}
	}

	public JPanel buildCertificateJPanel (String aTitle, String aCorpType, String aSelectedButtonLabel,
			ItemListener aItemListener, GameManager aGameManager) {
		JPanel tCertificateJPanel;
		JPanel tCertificateInfoJPanel;
		int tCount;
		String tCertificateType;
		boolean tIsBankPortfolioHolder;

		tCount = 0;
		tCertificateJPanel = new JPanel ();
		if (aTitle != null) {
			tCertificateJPanel.setBorder (BorderFactory.createTitledBorder (aTitle));
		}
		tCertificateJPanel.setLayout (new BoxLayout (tCertificateJPanel, BoxLayout.X_AXIS));
		tCertificateJPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		addJCAndHGlue (tCertificateJPanel, null);

		tIsBankPortfolioHolder = holder.isABank ();

		for (Certificate tCertificate : certificates) {
			tCertificateType = tCertificate.getCorpType ();
			if (tCertificateType.equals (aCorpType)) {
				tCount++;
				tCertificateInfoJPanel = tCertificate.buildCertificateInfoJPanel (aSelectedButtonLabel, aItemListener,
						tIsBankPortfolioHolder, Player.NO_PLAYER, aGameManager);
				addJCAndHGlue (tCertificateJPanel, tCertificateInfoJPanel);
			}
		}

		buildIfNoCertificates (tCertificateJPanel, tCount);

		return tCertificateJPanel;
	}

	private void buildIfNoCertificates (JPanel aCertificateJPanel, int aCount) {
		JPanel tNoCertificateJPanel;

		if (aCount == 0) {
			tNoCertificateJPanel = buildNoCertificatesPanel ();
			addJCAndHGlue (aCertificateJPanel, tNoCertificateJPanel);
		}
	}

	public JPanel buildCompactCertInfoJPanel (String aCompanyAbbrev, int aCertCount, int aCertTotalPercent) {
		JPanel tCertificateCompactPanel;
		String tCertInfo;
		JLabel tLabel;

		tCertificateCompactPanel = new JPanel ();
		tCertificateCompactPanel.setLayout (new BoxLayout (tCertificateCompactPanel, BoxLayout.Y_AXIS));
		tCertificateCompactPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tCertInfo = compactCertInfo (aCompanyAbbrev, aCertCount, aCertTotalPercent);
		tLabel = new JLabel (tCertInfo);
		tCertificateCompactPanel.add (tLabel);

		return tCertificateCompactPanel;
	}

	private String compactCertInfo (String aCompanyAbbrev, int aCertCount, int aCertTotalPercent) {
		String tCertInfo;

		tCertInfo = aCompanyAbbrev + " (" + aCertCount + "/" + aCertTotalPercent + "%)";

		return tCertInfo;
	}

	public void clearJustBoughtForAllCerts () {
		for (Certificate tCertificate : certificates) {
			tCertificate.setJustBought (false);
		}
	}
	
	public JPanel buildShareCertificateJPanel (String aCorpType, String aSelectedButtonLabel,
			ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tCorporationPanel;
		boolean tIsBankPortfolioHolder;

		tIsBankPortfolioHolder = holder.isABank ();
		if (tIsBankPortfolioHolder) {
			tCorporationPanel = buildCertJPanelForBank (aCorpType, aSelectedButtonLabel, aItemListener, aPlayer,
					aGameManager);
		} else {
			tCorporationPanel = buildCertJPanelForPlayer (aCorpType, aSelectedButtonLabel, aItemListener, aPlayer,
					aGameManager);
		}

		return tCorporationPanel;
	}

	public JPanel buildCertJPanelForPlayer (String aCorpType, String aSelectedButtonLabel, ItemListener aItemListener,
			Player aPlayer, GameManager aGameManager) {
		JPanel tAllCertificatesJPanel;
		JPanel tCertificateInfoJPanel;
		JPanel tCorporationJPanel;
		JPanel tScrollableCorpJPanel;
		JScrollPane tCorporationScrollPane;
		Certificate tCertificateToShow;
		int tCount;
		String tCertificateType;
		String tPrevShareCorpAbbrev;
		String tShareCorpAbbrev;
		boolean tIsBankPortfolioHolder = false;

		tCount = 0;
		tScrollableCorpJPanel = buildEmptyCorpJPanel (aCorpType);
		tCorporationJPanel = buildEmptyCorpJPanel (null);
		tPrevShareCorpAbbrev = NO_COMPANY_YET;
		tAllCertificatesJPanel = null;

		for (Certificate tCertificate : certificates) {
			tCertificateType = tCertificate.getCorpType ();
			if (tCertificateType.equals (aCorpType)) {
				tCount++;
				tShareCorpAbbrev = tCertificate.getCompanyAbbrev ();
				if (tShareCorpAbbrev.equals (tPrevShareCorpAbbrev)) {
					tCertificateInfoJPanel = tCertificate.buildCertificateInfoJPanel (aSelectedButtonLabel,
							aItemListener, tIsBankPortfolioHolder, aPlayer, aGameManager);
					addJCAndHGlue (tAllCertificatesJPanel, tCertificateInfoJPanel);
				} else {
					tCertificateToShow = getCertificateToShow (tCertificate);
					tAllCertificatesJPanel = setupAllCertJPanel ();
					tCertificateInfoJPanel = tCertificateToShow.buildCertificateInfoJPanel (aSelectedButtonLabel,
							aItemListener, tIsBankPortfolioHolder, aPlayer, aGameManager);
					addJCAndHGlue (tAllCertificatesJPanel, tCertificateInfoJPanel);
					addJCAndVGlue (tCorporationJPanel, tAllCertificatesJPanel);
					tPrevShareCorpAbbrev = tShareCorpAbbrev;
				}
			}
		}

		buildIfNoCertificates (tCorporationJPanel, tCount);
		tCorporationScrollPane = new JScrollPane (tCorporationJPanel);
		tCorporationScrollPane.setLayout (new ScrollPaneLayout ());
		tCorporationScrollPane.setBorder (EMPTY_BORDER);
		addJCAndVGlue (tScrollableCorpJPanel, tCorporationScrollPane);

		return tScrollableCorpJPanel;
	}

	public JPanel setupAllCertJPanel () {
		JPanel tAllCertificatesPanel;

		tAllCertificatesPanel = new JPanel ();
		tAllCertificatesPanel.setLayout (new BoxLayout (tAllCertificatesPanel, BoxLayout.X_AXIS));
		tAllCertificatesPanel.setBorder (EMPTY_BORDER);
		tAllCertificatesPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
		addJCAndHGlue (tAllCertificatesPanel, null);

		return tAllCertificatesPanel;
	}

	public void sortByActive () {
		Collections.sort (certificates, Certificate.CertificateActiveOrderComparator);
	}

	public JPanel buildCertJPanelForBank (String aCorpType, String aSelectedButtonLabel, ItemListener aItemListener,
			Player aPlayer, GameManager aGameManager) {
		JPanel tAllCertificatesJPanel;
		JScrollPane tCorporationScrollPane;
		JPanel tCertificateInfoJPanel;
		JPanel tOtherCertificatesInfoJPanel;
		JPanel tCorporationJPanel;
		JPanel tScrollableCorpJPanel;
		Certificate tCertificateToShow;
		Corporation tCorporationForCert;
		int tCount, tCertCount, tCertTotalPercent;
		String tCertificateType;
		String tPrevShareCorpAbbrev;
		String tShareCorpAbbrev;
		boolean tIsBankPortfolioHolder = true;

		tCount = 0;
		tCertCount = 0;
		tCertTotalPercent = 0;
		tScrollableCorpJPanel = buildEmptyCorpJPanel (aCorpType);
		tCorporationJPanel = buildEmptyCorpJPanel (null);
		tPrevShareCorpAbbrev = NO_COMPANY_YET;
		tAllCertificatesJPanel = null;

		sortByActive ();
		for (Certificate tCertificate : certificates) {
			tCertificateType = tCertificate.getCorpType ();
			if (tCertificateType.equals (aCorpType)) {
				tCorporationForCert = tCertificate.getCorporation ();
				if (tCorporationForCert.isFormed ()) {
					tCertCount = getCertificateCountFor (tCorporationForCert);
					tCertTotalPercent = getCertificatePercentageFor (tCorporationForCert);
					tCount++;
					tShareCorpAbbrev = tCertificate.getCompanyAbbrev ();
					if (!tShareCorpAbbrev.equals (tPrevShareCorpAbbrev)) {
						tCertificateToShow = getCertificateToShow (tCertificate);
						tCertificateInfoJPanel = tCertificateToShow.buildCertificateInfoJPanel (aSelectedButtonLabel,
								aItemListener, tIsBankPortfolioHolder, aPlayer, aGameManager);
						tOtherCertificatesInfoJPanel = buildCompactCertInfoJPanel (tShareCorpAbbrev, tCertCount,
								tCertTotalPercent);
						tAllCertificatesJPanel = buildAllCertificatesJPanel (tCertificateInfoJPanel,
								tOtherCertificatesInfoJPanel);

						addJCAndVGlue (tCorporationJPanel, tAllCertificatesJPanel);
						tPrevShareCorpAbbrev = tShareCorpAbbrev;
					}
				}
			}
		}

		buildIfNoCertificates (tCorporationJPanel, tCount);
		tCorporationScrollPane = new JScrollPane (tCorporationJPanel);
		tCorporationScrollPane.setLayout (new ScrollPaneLayout ());
		tCorporationScrollPane.setBorder (EMPTY_BORDER);
		addJCAndVGlue (tScrollableCorpJPanel, tCorporationScrollPane);

		return tScrollableCorpJPanel;
	}

	private Certificate getCertificateToShow (Certificate aCertificate) {
		Certificate tCertificateToShow;
		Corporation tCorporationToShow;

		tCertificateToShow = aCertificate;
		// Want to be sure to show the President's Certificate FIRST to buy, if the Bank
		// has it.
		// The Sort Certificates has trouble placing the President Certificate in Proper
		// order on the Undo.
		tCorporationToShow = tCertificateToShow.getCorporation ();
		if (containsPresidentShareOf (tCorporationToShow)) {
			tCertificateToShow = getPresidentCertificate (tCorporationToShow);
		}

		return tCertificateToShow;
	}

	private JPanel buildAllCertificatesJPanel (JPanel aCertificateInfoJPanel, JPanel aOtherCertificatesInfoJPanel) {
		JPanel tAllCertificatesJPanel;

		tAllCertificatesJPanel = setupAllCertJPanel ();
		tAllCertificatesJPanel.add (aCertificateInfoJPanel);
		tAllCertificatesJPanel.add (Box.createHorizontalStrut (3));

		tAllCertificatesJPanel.add (Box.createHorizontalStrut (3));
		tAllCertificatesJPanel.add (aOtherCertificatesInfoJPanel);
		tAllCertificatesJPanel.add (Box.createHorizontalGlue ());

		return tAllCertificatesJPanel;
	}

	private JPanel buildEmptyCorpJPanel (String aCorpType) {
		JPanel tCorporationPanel;

		tCorporationPanel = new JPanel ();
		tCorporationPanel.setLayout (new BoxLayout (tCorporationPanel, BoxLayout.Y_AXIS));
		if (aCorpType != null) {
			tCorporationPanel.setBorder (BorderFactory.createTitledBorder (aCorpType + " Companies"));
		}
		tCorporationPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		addJCAndHGlue (tCorporationPanel, null);

		return tCorporationPanel;
	}

	private JPanel buildNoCertificatesPanel () {
		JPanel tNoCertificatesPanel;
		JLabel tLabel;

		tLabel = new JLabel (NO_CERTIFICATES);
		tNoCertificatesPanel = setupAllCertJPanel ();
		addJCAndHGlue (tNoCertificatesPanel, tLabel);

		return tNoCertificatesPanel;
	}

	// TODO: Don't have the 'aPrivates, aMinors, aShares' passed in here.
	// Have this routine request this routine cycle through each Company type
	// possible,
	// (Found via GameManager), building a separate CertPanel with the specific
	// type, and add it
	// to the portfolioInfoJPanel.
	public JPanel buildPortfolioJPanel (String tTitle, boolean aPrivates, boolean aMinors,
			boolean aShares, String aSelectedButtonLabel, ItemListener aItemListener, GameManager aGameManager) {
		JPanel tPrivateCertPanel, tMinorCertPanel, tShareCertPanel;

		buildPortfolioJPanel ("Portfolio");

		if (aPrivates) {
			tPrivateCertPanel = buildCertificateJPanel (tTitle, Corporation.PRIVATE_COMPANY, aSelectedButtonLabel,
					aItemListener, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tPrivateCertPanel);
			privateIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		if (aMinors) {
			tMinorCertPanel = buildCertificateJPanel (tTitle, Corporation.MINOR_COMPANY, aSelectedButtonLabel,
					aItemListener, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tMinorCertPanel);
			minorIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}
		if (aShares) {
			tShareCertPanel = buildShareCertificateJPanel (Corporation.SHARE_COMPANY, aSelectedButtonLabel,
					aItemListener, null, aGameManager);
			addJCAndVGlue (portfolioInfoJPanel, tShareCertPanel);
			shareIndex = portfolioInfoJPanel.getComponentCount () - 1;
		}

		return portfolioInfoJPanel;
	}

	protected void buildPortfolioJPanel (String aTitle) {
		BoxLayout tLayout;

		portfolioInfoJPanel = new JPanel ();
		portfolioInfoJPanel.setBorder (BorderFactory.createTitledBorder (aTitle));
		tLayout = new BoxLayout (portfolioInfoJPanel, BoxLayout.Y_AXIS);
		portfolioInfoJPanel.setLayout (tLayout);
		portfolioInfoJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		addJCAndVGlue (portfolioInfoJPanel, null);
	}

	public void clearSelections () {
		for (Certificate tCertificate : certificates) {
			tCertificate.clearSelection ();
		}
	}

	public boolean containsPresidentShareOf (Corporation aCorporation) {
		boolean isPresidentOf;

		isPresidentOf = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isPresidentShare ()) {
					isPresidentOf = true;
				}
			}
		}

		return isPresidentOf;
	}

	public void applyDiscount () {
		boolean tDiscountApplied = false;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.getMustSell () && !tDiscountApplied) {
				tCertificate.applyDiscount ();
				tDiscountApplied = true;
			}
		}
	}

	public boolean hasMustBuyCertificate () {
		boolean tMustBuy = false;
		Certificate tCertificate;

		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			// If the first certificate in the portfolio has no Par Value (Cost == Discount)
			// it is a Must Buy
			if (tCertificate.getParValue () == 0) {
				tMustBuy = true;
			}
		}

		return tMustBuy;
	}

	public boolean noMustSellLeft () {
		boolean tNoMustSellLeft = true;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.getMustSell ()) {
				tNoMustSellLeft = false;
			}
		}

		return tNoMustSellLeft;
	}

	public boolean hasMustSell () {
		boolean tHasMustSell = false;
		Certificate tCertificate;
		Corporation tCorporation;

		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation.getMustSell ()) {
				// If the first certificate in the portfolio has with the MustSell Flag this is
				// what we return
				tHasMustSell = true;
			}
		}

		return tHasMustSell;
	}

	public Certificate getMustSellCertificate () {
		Certificate tCertificate;
		Certificate tThisCertificate = Certificate.NO_CERTIFICATE;
		Corporation tCorporation;

		// If the portfolio has no Certificates, there is no Must Buy
		if (!certificates.isEmpty ()) {
			tCertificate = certificates.get (0);
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation.getMustSell ()) {
				// If the first certificate in the portfolio has with the MustSell Flag this is
				// what we return
				tThisCertificate = tCertificate;
			}
		}

		return tThisCertificate;
	}

	@Override
	public int getCertificateCountAgainstLimit () {
		int tCertificatesThatCount;

		tCertificatesThatCount = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.countsAgainstCertificateLimit ()) {
				tCertificatesThatCount++;
			}
		}

		return tCertificatesThatCount;
	}

	public int getCertificateTotalCount () {
		return certificates.size ();
	}

	public Certificate getCertificateFor (Corporation aCorporation) {
		return getCertificateFor (aCorporation, REMOVE_CERTIFICATE);
	}

	public Certificate getCertificateFor (Corporation aCorporation, boolean aRemove) {
		Certificate tCertificate;
		Certificate tPortfolioCertificate;
		int tIndex;
		int tCertificateCount;

		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		for (tIndex = 0; 
			(tIndex < tCertificateCount) && (tCertificate == Certificate.NO_CERTIFICATE); 
			tIndex++) {
			tPortfolioCertificate = certificates.get (tIndex);
			if (tPortfolioCertificate.isForThis (aCorporation)) {
				tCertificate = tPortfolioCertificate;
				if (aRemove) {
					certificates.remove (tIndex);
					holder.updateListeners (CERTIFICATE_REMOVED + " from " + holder.getName ());
				}
			}
		}

		return tCertificate;
	}

	@Override
	public int getCertificateCountFor (Corporation aCorporation) {
		int tCount;

		tCount = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tCount++;
			}
		}

		return tCount;
	}

	@Override
	public Certificate getCertificate (int aIndex) {
		Certificate tCertificate;
		int tCertificateCount;

		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		if (tCertificateCount > 0) {
			if ((aIndex >= 0) && (aIndex < tCertificateCount)) {
				tCertificate = certificates.get (aIndex);
			}
		}

		return tCertificate;
	}

	public Certificate getCertificate (Corporation aCorporation, int aPercentage) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (aCorporation.equals (tCertificate.getCorporation ())) {
				if (tCertificate.getPercentage () == aPercentage) {
					tThisCertificate = tCertificate;
				}
			}
		}

		return tThisCertificate;
	}

	public Certificate getCertificate (String aAbbrev, int aPercentage, boolean isPresident) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.getCompanyAbbrev ().equals (aAbbrev)) {
				if (tCertificate.getPercentage () == aPercentage) {
					if (tCertificate.isPresidentShare () == isPresident) {
						tThisCertificate = tCertificate;
					}
				}
			}
		}

		return tThisCertificate;
	}

	public Certificate getIPOCertificate (int aPercentage, boolean aPresidentShare) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tThisCertificate == Certificate.NO_CERTIFICATE) {
				if (aPercentage == tCertificate.getPercentage ()) {
					if (aPresidentShare && tCertificate.isPresidentShare ()) {
						tThisCertificate = tCertificate;
					} else if (!aPresidentShare && !tCertificate.isPresidentShare ()) {
						tThisCertificate = tCertificate;
					}
					if (tThisCertificate != Certificate.NO_CERTIFICATE) {
						if (! tThisCertificate.isOwnedByBank ()) {
							tThisCertificate = Certificate.NO_CERTIFICATE;
						}
					}
				}
			}
		}

		return tThisCertificate;
	}

	public Certificate getCertificate (int aPercentage, boolean aPresidentShare) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (aPercentage == tCertificate.getPercentage ()) {
				if (aPresidentShare && tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				} else if (!aPresidentShare && !tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}

		return tThisCertificate;
	}

	public int getCertificatePercentageFor (String aCorpAbbrev) {
		int tPercentage;

		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			/* Only worry if this is a Share Company */
			if (tCertificate.isAShareCompany ()) {
				if (tCertificate.isForThis (aCorpAbbrev)) {
					tPercentage += tCertificate.getPercentage ();
				}
			}
		}

		return tPercentage;
	}

	@Override
	public int getCertificatePercentageFor (Corporation aCorporation) {
		int tPercentage;

		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tPercentage += tCertificate.getPercentage ();
			}
		}

		return tPercentage;
	}

	public int getBankPoolPercentage (Corporation aCorporation) {
		int tPercentage;

		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isOwnedbyBankPool ()) {
					tPercentage += tCertificate.getPercentage ();
				}
			}
		}

		return tPercentage;
	}

	public int getPlayerOrCorpOwnedPercentageFor (Corporation aCorporation) {
		int tPercentage;

		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isOwnedbyPlayerOrCorp ()) {
					tPercentage += tCertificate.getPercentage ();
				}
			}
		}

		return tPercentage;
	}

	public Certificate getCertificateToBidOn () {
		Certificate tCertificateToBidOn;

		tCertificateToBidOn = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBidOn ()) {
				tCertificateToBidOn = tCertificate;
			}
		}

		return tCertificateToBidOn;
	}

	public Certificate getCertificateToBuy () {
		Certificate tCertificateToBuy;

		tCertificateToBuy = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCertificateToBuy = tCertificate;
			}
		}

		return tCertificateToBuy;
	}

	public Certificate getCertificateToExchange () {
		Certificate tCertificateToExchange;

		tCertificateToExchange = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToExchange ()) {
				tCertificateToExchange = tCertificate;
			}
		}

		return tCertificateToExchange;
	}

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;

		tCertificatesToBuy = new LinkedList<> ();
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCertificatesToBuy.add (tCertificate);
			}
		}

		return tCertificatesToBuy;
	}

	public List<Certificate> getCertificatesCanBeSold () {
		List<Certificate> tCertificatesToSell;

		tCertificatesToSell = new LinkedList<> ();
		for (Certificate tCertificate : certificates) {
			if (tCertificate.canBeSold ()) {
				tCertificatesToSell.add (tCertificate);
			}
		}

		return tCertificatesToSell;
	}

	public List<Certificate> getCertificatesToSell () {
		List<Certificate> tCertificatesToSell;

		tCertificatesToSell = new LinkedList<> ();
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCertificatesToSell.add (tCertificate);
			}
		}

		return tCertificatesToSell;
	}

	public int getPercentOfCertificatesForSale () {
		int tPercentOfSelectedCertificates;

		tPercentOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tPercentOfSelectedCertificates += tCertificate.getPercentage ();
			}
		}

		return tPercentOfSelectedCertificates;
	}

	public int getCountOfCertificatesForSale () {
		int tCountOfSelectedCertificates;

		tCountOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCountOfSelectedCertificates++;
			}
		}

		return tCountOfSelectedCertificates;
	}

	public int getCountOfCertificatesForBuy () {
		int tCountOfSelectedCertificates;

		tCountOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCountOfSelectedCertificates++;
			}
		}

		return tCountOfSelectedCertificates;
	}

	public XMLElement setRealAttributes (XMLElement aXMLElement, AttributeName aAttributeName, int aIndex) {
		if (aIndex > 0) {
			aXMLElement.setAttribute (aAttributeName, aIndex);
		}

		return aXMLElement;
	}

	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLCertificateElements;

		tXMLElement = aXMLDocument.createElement (EN_PORTFOLIO);
		setRealAttributes (tXMLElement, AN_PRIVATE_INDEX, privateIndex);
		setRealAttributes (tXMLElement, AN_MINOR_INDEX, minorIndex);
		setRealAttributes (tXMLElement, AN_SHARE_INDEX, shareIndex);
		for (Certificate tCertficate : certificates) {
			tXMLCertificateElements = tCertficate.getElement (aXMLDocument);
			tXMLElement.appendChild (tXMLCertificateElements);
		}

		return tXMLElement;
	}

	public int getBidderCount () {
		int tBidderCount = 0;

		for (Certificate tCertificate : certificates) {
			tBidderCount += tCertificate.getNumberOfBidders ();
		}

		return tBidderCount;
	}

	public String getBidderNames () {
		String tBidderNames = "";

		for (Certificate tCertificate : certificates) {
			if (tBidderNames.length () > 0) {
				tBidderNames += ", ";
			}
			tBidderNames += tCertificate.getBidderNames ();
		}

		return tBidderNames;
	}

	public int getHighestBid () {
		int tHighestBid = 0;

		for (Certificate tCertificate : certificates) {
			tHighestBid = tCertificate.getHighestBid ();
		}

		return tHighestBid;
	}

	public XMLElement getBidders (XMLDocument aXMLDocument) {
		XMLElement tXMLElement = NO_BIDDERS;
		XMLElement tXMLBidders;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.hasBidders ()) {
				if (tXMLElement == NO_BIDDERS) {
					tXMLElement = aXMLDocument.createElement (Certificate.EN_CERTIFICATE);
				}
				tXMLBidders = tCertificate.getElement (aXMLDocument);
				tXMLElement.appendChild (tXMLBidders);
			}
		}

		return tXMLElement;
	}

	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = null;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.sameCertificate (aLoadedCertificate)) {
				tCurrentHolder = (PortfolioHolderLoaderI) holder;
			}
		}

		return tCurrentHolder;
	}

	public String getSelectedCompanyAbbrev () {
		String tSelectedCompanyAbbrev = "";

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tSelectedCompanyAbbrev = tCertificate.getCompanyAbbrev ();
			}
		}

		return tSelectedCompanyAbbrev;
	}

	public int getSelectedPercent () {
		int tSelectedPercent;

		tSelectedPercent = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tSelectedPercent += tCertificate.getPercentage ();
			}
		}

		return tSelectedPercent;
	}

	public int getPresidentPercent (Corporation corporation) {
		String tCertificateOwnerName;
		String tPresidentName;
		PortfolioHolderI tPresident;
		int tPresidentPercent = 0;

		tPresident = getPresident ();
		tPresidentName = tPresident.getName ();

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tCertificateOwnerName = tCertificate.getOwnerName ();
				if (tCertificateOwnerName.equals (tPresidentName)) {
					tPresidentPercent += tCertificate.getPercentage ();
				}
			}
		}

		return tPresidentPercent;
	}

	/**
	 * Determine the player who has the next highest Stock Percentage of the specific Corporation after the current
	 * President, and return that percentage owned. This will ignore shares owned by the Bank or the BankPool.
	 * TODO:  This -SHOULD- also ignore shares that may be owned by a Company.
	 * 
	 * @param aCorporation The corporation to find ownership of
	 * 
	 * @return the Percentage owned by the next highest player.
	 * 
	 */
	public int getNextPresidentPercent (Corporation aCorporation) {
		String tCertificateOwnerName;
		String tNextName;
		String tPresidentName;
		PortfolioHolderI tPresident;
		int tNextPresidentPercent;
		int tPercent;

		tNextName = "";
		tPresident = getPresident ();
		tPresidentName = tPresident.getName ();
		tNextPresidentPercent = 0;
		tPercent = 0;
		sortByOwners ();

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tCertificateOwnerName = tCertificate.getOwnerName ();
				if (! tCertificateOwnerName.equals (Certificate.NO_OWNER_NAME)) {
					if (! tCertificateOwnerName.equals (tPresidentName)) {
						if (tCertificateOwnerName.equals (tNextName)) {
							tPercent += tCertificate.getPercentage ();
						} else {
							if (tPercent > tNextPresidentPercent) {
								tNextPresidentPercent = tPercent;
							}
							tPercent = tCertificate.getPercentage ();
						}
					}
					tNextName = tCertificateOwnerName;
				}
			}
		}
		if (tPercent > tNextPresidentPercent) {
			tNextPresidentPercent = tPercent;
		}

		return tNextPresidentPercent;
	}

	public String getNextPresidentName (Corporation aCorporation) {
		String tCertificateOwnerName;
		String tNextName;
		String tPresidentName;
		PortfolioHolderI tPresident;
		int tNextPresidentPercent;
		int tPercent;
		String tNextPresidentName;

		tNextName = "";
		tPresident = getPresident ();
		tPresidentName = tPresident.getName ();
		tNextPresidentPercent = 0;
		tPercent = 0;
		sortByOwners ();
		tNextPresidentName = "";

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tCertificateOwnerName = tCertificate.getOwnerName ();
				if (!tCertificateOwnerName.equals (tPresidentName)) {
					if (tCertificateOwnerName.equals (tNextName)) {
						tPercent += tCertificate.getPercentage ();
					} else {
						if (tPercent > tNextPresidentPercent) {
							tNextPresidentPercent = tPercent;
						}
						tPercent = tCertificate.getPercentage ();
					}
				}
				tNextName = tCertificateOwnerName;
			}
		}
		if (tPercent > tNextPresidentPercent) {
			tNextPresidentName = tNextName;
		}

		return tNextPresidentName;
	}

	public PortfolioHolderI getPresident () {
		CertificateHolderI tCertificateHolder;
		PortfolioHolderI tPortfolioHolder;

		tPortfolioHolder = PortfolioHolderI.NO_HOLDER;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tCertificateHolder = tCertificate.getOwner ();
				tPortfolioHolder = tCertificateHolder.getPortfolioHolder ();
			}
		}

		return tPortfolioHolder;
	}

	public String getPresidentName () {
		String tName;

		tName = NO_NAME_STRING;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tName = tCertificate.getOwnerName ();
			}
		}

		return tName;
	}

	public Certificate getPresidentCertificate () {
		Certificate tPresidentCertificate;

		tPresidentCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isPresidentShare ()) {
				tPresidentCertificate = tCertificate;
			}
		}

		return tPresidentCertificate;
	}

	public boolean isPresidentAPlayer () {
		boolean tPresidentIsAPlayer;
		PortfolioHolderI tPresident;

		tPresident = getPresident ();
		if (tPresident == PortfolioHolderI.NO_HOLDER) {
			tPresidentIsAPlayer = false;
		} else {
			tPresidentIsAPlayer = tPresident.isAPlayer ();
		}

		return tPresidentIsAPlayer;
	}

	public boolean isSelectedForBuy () {
		boolean tIsSelectedForBuy;

		tIsSelectedForBuy = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelected ()) {
				tIsSelectedForBuy = true;
			}
		}

		return tIsSelectedForBuy;
	}

	public void loadPortfolio (XMLNode aXMLNode) {
		XMLNodeList tXMLNodeList;

		privateIndex = aXMLNode.getThisIntAttribute (AN_PRIVATE_INDEX, NO_COMPONENT_INDEX);
		minorIndex = aXMLNode.getThisIntAttribute (AN_MINOR_INDEX, NO_COMPONENT_INDEX);
		shareIndex = aXMLNode.getThisIntAttribute (AN_SHARE_INDEX, NO_COMPONENT_INDEX);
		tXMLNodeList = new XMLNodeList (certificateParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aXMLNode, Certificate.EN_CERTIFICATE);
	}

	ParsingRoutineI certificateParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			LoadedCertificate tLoadedCertificate;
			Certificate tCertificate;
			PortfolioHolderI tCurrentHolder;
			Portfolio tPortfolio;
			PortfolioHolderLoaderI tHolder;
			Bank tBank;

			tLoadedCertificate = new LoadedCertificate (aChildNode);
			tHolder = (PortfolioHolderLoaderI) holder;
			if (tHolder.getName ().equals (BankPool.NAME)) {
				tCurrentHolder = ((BankPool) tHolder).getCurrentHolderGM (tLoadedCertificate);
			} else {
				tCurrentHolder = tHolder.getCurrentHolder (tLoadedCertificate);
			}
			if (tCurrentHolder == PortfolioHolderI.NO_HOLDER) {
				tBank = holder.getBank ();
				tCurrentHolder = tBank.getStartPacketFrame ();
			}

			if (tCurrentHolder != PortfolioHolderI.NO_HOLDER) {
				tPortfolio = tCurrentHolder.getPortfolio ();
				if (tPortfolio != NO_PORTFOLIO) {
					tCertificate = tPortfolio.getCertificate (tLoadedCertificate.getCompanyAbbrev (),
							tLoadedCertificate.getPercentage (), tLoadedCertificate.getIsPresidentShare ());
					if (tCertificate != Certificate.NO_CERTIFICATE) {
						transferOneCertificateOwnership (tPortfolio, tCertificate);
					}
				}
			}
		}
	};

	public void setHolder (PortfolioHolderI aPortfolioHolder) {
		holder = aPortfolioHolder;
	}

	public PortfolioHolderI getHolder () {
		return holder;
	}

	@Override
	public String getHolderAbbrev () {
		String tHolderAbbrev;

		tHolderAbbrev = ">> NO HOLDER <<";
		if (holder != PortfolioHolderI.NO_HOLDER) {
			tHolderAbbrev = holder.getAbbrev ();
		}

		return tHolderAbbrev;
	}

	@Override
	public String getHolderName () {
		String tHolderName;

		tHolderName = ">> NO HOLDER <<";
		if (holder != PortfolioHolderI.NO_HOLDER) {
			tHolderName = holder.getName ();
		}

		return tHolderName;
	}

	@Override
	public String getName () {
		return "PORTFOLIO";
	}

	public int getPercentageFor (Corporation aCorporation) {
		int tPercentage;

		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tPercentage += tCertificate.getPercentage ();
			}
		}

		return tPercentage;
	}

	/**
	 * Percentage of Shares Sold from Bank
	 *
	 * @return int value of % shares sold (up to 100)
	 */

	public int getPercentOwned () {
		int tPercentOwned;

		tPercentOwned = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isOwned ()) {
				tPercentOwned += tCertificate.getPercentage ();
			}
		}

		return tPercentOwned;
	}

	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return holder;
	}

	public int getPortfolioValue () {
		int iValue;

		iValue = 0;
		for (Certificate tCertificate : certificates) {
			iValue += tCertificate.getValue ();
		}

		return iValue;
	}

	// Find the Corporation, in the Portfolio that matches this CorpID
	// Used to search the Bank Portfolio to load the Start Packet
	public Corporation getCorporationForID (int CorpID) {
		Corporation tCorporation = Corporation.NO_CORPORATION;
		Corporation tTestCorporation;
		int tTestCorporationID;

		for (Certificate tCertificate : certificates) {
			tTestCorporation = tCertificate.getCorporation ();
			tTestCorporationID = tTestCorporation.getID ();
			if (tTestCorporationID == CorpID) {
				tCorporation = tTestCorporation;
			}
		}

		return tCorporation;
	}

	public Certificate getNonPresidentCertificate (Corporation aCorporation) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (!tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}

		return tThisCertificate;
	}

	public Certificate getPresidentCertificate (Corporation aCorporation) {
		Certificate tThisCertificate;

		tThisCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				if (tCertificate.isPresidentShare ()) {
					tThisCertificate = tCertificate;
				}
			}
		}

		return tThisCertificate;
	}

	// Get the Specific Certificate that matches and REMOVE it from the Portfolio
	public Certificate getThisCertificate (Certificate aCertificate) {
		Certificate tCertificate;
		Certificate tPortfolioCertificate;
		int tIndex;
		int tCertificateCount;

		tCertificate = Certificate.NO_CERTIFICATE;
		tCertificateCount = certificates.size ();
		for (tIndex = 0; (tIndex < tCertificateCount) && (tCertificate == Certificate.NO_CERTIFICATE); tIndex++) {
			tPortfolioCertificate = certificates.get (tIndex);
			if (tPortfolioCertificate == aCertificate) {
				tCertificate = tPortfolioCertificate;
				certificates.remove (tIndex);
				holder.updateListeners (CERTIFICATE_REMOVED + " from " + holder.getName ());
			}
		}

		return tCertificate;
	}

	public void removeCertificateFromStartPacketRow (Certificate aCertificate, BuyStockAction aBuyStockAction) {
		StartPacketFrame tStartPacketFrame;
		String tCertificateLocation;
		String [] tCertificateLocations;
		StartPacketItem tStartPacketItem;
		int tItemRow;
		int tItemCol;
		
		tCertificateLocation = GUI.NULL_STRING;
		if (holder instanceof StartPacketFrame) {
			tStartPacketFrame = (StartPacketFrame) holder;
			tCertificateLocation = tStartPacketFrame.getCertificateLocation (aCertificate);
			tStartPacketItem = tStartPacketFrame.removeCertificateFromRow (aCertificate);
			if (tStartPacketItem != StartPacketItem.NO_START_PACKET_ITEM) {
				tCertificateLocations = tCertificateLocation.split (",");
				tItemRow = Integer.parseInt(tCertificateLocations [0]);
				tItemCol = Integer.parseInt (tCertificateLocations [1]);
				aBuyStockAction.addRemoveStartPacketItemEffect (holder, tStartPacketItem, tItemRow, tItemCol);
			}
		}
		System.out.println ("Removed a Certificate " + aCertificate.getCompanyAbbrev () + 
				" from Start Packet Location " + tCertificateLocation);
		
	}

	@Override
	public boolean hasCertificateFor (Corporation aCorporation) {
		boolean tCertificateFound;

		tCertificateFound = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isForThis (aCorporation)) {
				tCertificateFound = true;
			}
		}

		return tCertificateFound;
	}

	public boolean hasSelectedOneToExchange () {
		boolean tHasSelectedOneToExchange;
		int tSelectedCount = 0;

		tHasSelectedOneToExchange = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isAShareCompany ()) {
				if (tCertificate.isSelected ()) {
					tSelectedCount++;
				}
			}
		}
		if (tSelectedCount == 1) {
			tHasSelectedOneToExchange = true;
		}

		return tHasSelectedOneToExchange;
	}

	public boolean hasSelectedPrezToExchange () {
		boolean tHasSelectedPrezToExchange;

		tHasSelectedPrezToExchange = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isAShareCompany ()) {
				if (tCertificate.isPresidentShare ()) {
					if (tCertificate.isSelected ()) {
						tHasSelectedPrezToExchange = true;
					}
				}
			}
		}

		return tHasSelectedPrezToExchange;
	}
	
	public boolean hasSelectedPrivateToBidOn () {
		boolean tHasSelectedPrivateToBidOn;

		tHasSelectedPrivateToBidOn = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isAPrivateCompany ()) {
				if (tCertificate.isSelectedToBidOn ()) {
					tHasSelectedPrivateToBidOn = true;
				}
			}
		}

		return tHasSelectedPrivateToBidOn;
	}

	public boolean hasSelectedStockToBuy () {
		boolean tHasSelectedStockToBuy;

		tHasSelectedStockToBuy = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tHasSelectedStockToBuy = true;
			}
		}

		return tHasSelectedStockToBuy;
	}

	public boolean hasSelectedStockToBid () {
		boolean tHasSelectedStockToBid;

		tHasSelectedStockToBid = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBid ()) {
				tHasSelectedStockToBid = true;
			}
		}

		return tHasSelectedStockToBid;
	}

	public Certificate getSelectedStockToExchange () {
		Certificate tCertificateToExchange = Certificate.NO_CERTIFICATE;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToExchange ()) {
				tCertificateToExchange = tCertificate;
			}
		}

		return tCertificateToExchange;
	}

	public Certificate getSelectedStockToSell () {
		Certificate tCertificateToSell = Certificate.NO_CERTIFICATE;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCertificateToSell = tCertificate;
			}
		}

		return tCertificateToSell;
	}

	public int getCountSelectedCosToBuy () {
		int tCountSelectedCosToBuy = 0;
		String tCoAbbrev, tPrevCoAbbrev;

		tPrevCoAbbrev = "";
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCoAbbrev = tCertificate.getCompanyAbbrev ();
				if (!(tCoAbbrev.equals (tPrevCoAbbrev))) {
					tCountSelectedCosToBuy++;
					tPrevCoAbbrev = tCoAbbrev;
				}
			}
		}

		return tCountSelectedCosToBuy;
	}

	public int getCountSelectedCosToBid () {
		int tCountSelectedCosToBid = 0;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBid ()) {
				tCountSelectedCosToBid++;
			}
		}

		return tCountSelectedCosToBid;
	}

	public Certificate getASelectedCertificatesToBuy () {
		Certificate tSelectedCertificate;

		tSelectedCertificate = Certificate.NO_CERTIFICATE;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tSelectedCertificate = tCertificate;
			}
		}

		return tSelectedCertificate;
	}

	public int getCountSelectedCertificatesToBuy () {
		int tCountOfSelectedCertificates;

		tCountOfSelectedCertificates = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tCountOfSelectedCertificates++;
			}
		}

		return tCountOfSelectedCertificates;
	}

	/**
	 * Review all Selected Shares for Sale to determine if all are the same Size
	 *
	 * @return True if all Selected Shares are the same Size.
	 *
	 */
	public boolean allSelectedSharesSameSize () {
		boolean tAllSelectedSharesSameSize;
		int tPercentage;
		int tFoundPercentage;

		tAllSelectedSharesSameSize = true;
		tPercentage = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tFoundPercentage = tCertificate.getPercentage ();
				if (tPercentage > 0) {
					if (tFoundPercentage != tPercentage) {
						tAllSelectedSharesSameSize = false;
					}
				} else {
					tPercentage = tFoundPercentage;
				}
			}
		}

		return tAllSelectedSharesSameSize;
	}

	/**
	 * Get cost of all Shares selected to be Bought
	 *
	 * @return Total Cost of all Shares selected to be Bought
	 *
	 */
	public int getSelectedStocksCost () {
		int tSelectedStockCost;

		tSelectedStockCost = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToBuy ()) {
				tSelectedStockCost += tCertificate.getCost ();
			}
		}

		return tSelectedStockCost;
	}

	/**
	 * Get cost of all Shares selected to be Sold
	 *
	 * @return Total Cost of all Shares selected to be Sold
	 *
	 */
	public int getSelectedStocksSaleCost () {
		int tSelectedStockSaleCost;

		tSelectedStockSaleCost = 0;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tSelectedStockSaleCost += tCertificate.getCost ();
			}
		}

		return tSelectedStockSaleCost;
	}

	/**
	 * Determine if the player has selected any stocks to Sell
	 *
	 * @return TRUE if at least one stock in the Portfolio has been selected to be
	 *         sold
	 *
	 */
	public boolean hasSelectedStocksToSell () {
		boolean tHasSelectedStocksToSell;

		tHasSelectedStocksToSell = false;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tHasSelectedStocksToSell = true;
			}
		}

		return tHasSelectedStocksToSell;
	}

	public boolean AreAllSelectedStocksSameCorporation () {
		boolean tAreAllSelectedStocksSameCorporation;
		Corporation tCorporation, tPreviousCorporation;

		tAreAllSelectedStocksSameCorporation = true;
		tPreviousCorporation = Corporation.NO_CORPORATION;
		for (Certificate tCertificate : certificates) {
			if (tCertificate.isSelectedToSell ()) {
				tCorporation = tCertificate.getCorporation ();
				if (tPreviousCorporation != Corporation.NO_CORPORATION) {
					if (tCorporation != tPreviousCorporation) {
						tAreAllSelectedStocksSameCorporation = false;
					}
				}
				tPreviousCorporation = tCorporation;
			}
		}

		return tAreAllSelectedStocksSameCorporation;
	}

	public boolean hasShareCompanyStocks () {
		boolean tHasShareCompanyStocks;
		Corporation tCorporation;

		tHasShareCompanyStocks = false;
		for (Certificate tCertificate : certificates) {
			tCorporation = tCertificate.getCorporation ();
			if (tCorporation.isAShareCompany ()) {
				tHasShareCompanyStocks = true;
			}
		}

		return tHasShareCompanyStocks;
	}

	public boolean hasThisCertificate (Certificate aThisCertificate) {
		boolean tHasThisCertificate = false;

		for (Certificate tCertificate : certificates) {
			if (aThisCertificate == tCertificate) {
				tHasThisCertificate = true;
			}
		}

		return tHasThisCertificate;
	}

	@Override
	public boolean isABank () {
		return holder.isABank ();
	}

	@Override
	public boolean isABankPool () {
		return holder.isABankPool ();
	}

	@Override
	public boolean isACorporation () {
		return holder.isACorporation ();
	}

	public boolean isEmpty () {
		return certificates.isEmpty ();
	}

	@Override
	public boolean isAPlayer () {
		return holder.isAPlayer ();
	}

	public void printCompactPortfolioInfo () {
		String tCompanyName, tPreviousName;

		System.out.println ("Portfolio:");		// PRINTLOG
		if (certificates.size () == 0) {
			System.out.print (">> NO CERTIFICATES IN PORTFOLIO <<");
		} else {
			tPreviousName = "";
			for (Certificate tCertificate : certificates) {
				tCompanyName = tCertificate.getCompanyName ();
				if (!(tCompanyName.equals (tPreviousName))) {
					System.out.print ("\nCertificates For " + tCompanyName + " ");
					tPreviousName = tCompanyName;
				}
				System.out.print (tCertificate.getPercentage () + " ");
			}
			System.out.println ("\n");
		}
	}

	public void printPortfolioInfo () {
		System.out.println ("Portfolio for " + getHolderName () + ":");	// PRINTLOG
		if (certificates.size () == 0) {
			System.out.println (">> NO CERTIFICATES IN PORTFOLIO <<");
		} else {
			for (Certificate tCertificate : certificates) {
				tCertificate.printCertificateInfo ();
			}
		}
		System.out.println ("Total Portfolio Value: " + getPortfolioValue ());
	}

	static final Comparator<Certificate> orderByOwner = new Comparator<Certificate> () {
		@Override
		public int compare (Certificate aCertificate1, Certificate aCertificate2) {
			String tOwner1;
			String tOwner2;
			int tSortOrder;
			int tCert1Percentage, tCert2Percentage;

			tOwner1 = aCertificate1.getOwnerName ();
			tOwner2 = aCertificate2.getOwnerName ();

			tSortOrder = tOwner1.compareTo (tOwner2);
			if (tSortOrder == 0) {
				tCert1Percentage = aCertificate1.getPercentage ();
				tCert2Percentage = aCertificate2.getPercentage ();
				if (tCert1Percentage > tCert2Percentage) {
					tSortOrder = -1;
				} else if (tCert1Percentage < tCert2Percentage) {
					tSortOrder = 1;
				}
			}

			return (tSortOrder);
		}
	};

	public void removeAllBids () {
		for (Certificate tCertificate : certificates) {
			tCertificate.removeAllBids ();
		}
	}

	public void sortByOwners () {
		Collections.sort (certificates, orderByOwner);
	}
	
	public boolean transferOneCertificateOwnership (Portfolio aFromPortfolio, Certificate aCertificate) {
		Certificate tThisCertificate;
		Certificate tCertificate;
		boolean tTransferGood;
		boolean tIsPresident;
		String tCompanyAbbrev;
		int tPercentage;

		tTransferGood = false;
		if (aFromPortfolio != NO_PORTFOLIO) {
			if (aCertificate != Certificate.NO_CERTIFICATE) {
				tCompanyAbbrev = aCertificate.getCompanyAbbrev ();
				tPercentage = aCertificate.getPercentage ();
				tIsPresident = aCertificate.isPresidentShare ();
				tThisCertificate = aFromPortfolio.getCertificate (tCompanyAbbrev, tPercentage, tIsPresident);
				tCertificate = aFromPortfolio.getThisCertificate (tThisCertificate);
				if (tCertificate != Certificate.NO_CERTIFICATE) {
					tCertificate.setOwner (this);
					if (isAPlayer ()) {
						tCertificate.setJustBought (true);
					}
					tCertificate.sortCorporationCertificates ();
					addCertificate (tCertificate);
					tCertificate.resetFrameButton ();
					tTransferGood = true;
					if (tIsPresident) {
						if (!tCertificate.isATestGame ()) {
							notifyPresidentChange (aFromPortfolio, tCertificate);
						}
					}
				} else {
					System.err.println ("Transfer Certificate Failed since the Certificate could not be found");
					System.err.println (
							"Looking for " + tCompanyAbbrev + " " + tPercentage + "% as President " + tIsPresident);
					System.err.println ("From the Portfolio of " + aFromPortfolio.getName ());
				}
			} else {
				System.err.println ("Transfer Certificate Failed since Certificate is Null");
			}
		} else {
			System.err.println ("Transfer Certificate Failed since From Portfolio is Null");
		}

		return tTransferGood;
	}

	private void notifyPresidentChange (Portfolio aFromPortfolio, Certificate aCertificate) {
		PlayerFrame tPlayerFrame;
		Player tPlayer;
		String tMessage;
		String tClientName;
		String tPlayerName;
		String tHolderName;

		if (!aCertificate.isLoading ()) {
			if (!aCertificate.isAPrivateCompany ()) {
				if (holder.isAPlayer ()) {
					tPlayer = (Player) holder;
					tClientName = tPlayer.getClientName ();
					tPlayerName = tPlayer.getName ();
					tHolderName = aFromPortfolio.getHolderName ();
					if (!tClientName.equals (tPlayerName)) {
						tPlayerFrame = tPlayer.getPlayerFrame ();
						tMessage = tPlayerName;
						if (Bank.NAME.equals (tHolderName)) {
							tMessage += " received the President Share of " + aCertificate.getCompanyAbbrev ()
									+ " from the Bank";
						} else {
							tMessage += " obtained the Presidency of " + aCertificate.getCompanyAbbrev () + " from "
									+ tHolderName;
						}
						JOptionPane.showMessageDialog (tPlayerFrame, tMessage, "Change of President",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}

	public boolean transferOwnership (Portfolio aFromPortfolio, Corporation aCorporation) {
		boolean tTransferGood;
		Certificate tCertificate;

		tTransferGood = false;
		if (aFromPortfolio != NO_PORTFOLIO) {
			if (aCorporation != Corporation.NO_CORPORATION) {
				tCertificate = aFromPortfolio.getCertificateFor (aCorporation);
				if (tCertificate != Certificate.NO_CERTIFICATE) {
					tCertificate.setOwner (this);
					if (isAPlayer ()) {
						tCertificate.setJustBought (true);
					}
					tCertificate.sortCorporationCertificates ();
					addCertificate (tCertificate);
					tTransferGood = true;
				}
			}
		}

		return tTransferGood;
	}

	public void updateCertificateOwnersInfo () {
		CertificateHolderI tCertificateHolder;
		PortfolioHolderI tPortfolioHolder;
		Player tPlayerOwner;
		Player tPreviousPlayerOwner;

		tPreviousPlayerOwner = Player.NO_PLAYER;
		for (Certificate tCertificate : certificates) {
			tCertificateHolder = tCertificate.getOwner ();
			if (tCertificateHolder != NO_PORTFOLIO) {
				tPortfolioHolder = tCertificateHolder.getPortfolioHolder ();
				if (tPortfolioHolder.isAPlayer ()) {
					tPlayerOwner = (Player) tPortfolioHolder;
					if (tPreviousPlayerOwner != tPlayerOwner) {
						tPlayerOwner.updatePlayerInfo ();
						tPreviousPlayerOwner = tPlayerOwner;
					}
				}
			}
		}
	}

	public boolean itemStateChanged (ItemEvent aItemEvent, PlayerFrame aPlayerFrame) {
		Object tSourceButton;
		JCheckBox tCheckedButton;
		JComboBox<String> tComboBox;
		int tParPrice;
		int tCostToBuy;
		boolean tIsSelected;
		boolean tHandled;

		tHandled = false;
		tSourceButton = aItemEvent.getItemSelectable ();
		for (Certificate tCertificate : certificates) {
			if (! tHandled) {
				tCheckedButton = tCertificate.getCheckedButton ();
				tComboBox = tCertificate.getComboBox ();
				if (tCertificate.isAPrivateCompany ()) {
					if (tSourceButton == tCheckedButton) {
						tHandled = true;
					}
				} else {
					if (tSourceButton == tCheckedButton) {
						tIsSelected = tCheckedButton.isSelected ();
						tCertificate.enableParValuesCombo (tIsSelected);
						tHandled = true;
					} else if (tSourceButton == tComboBox) {
						tParPrice = tCertificate.getComboParValue ();
						tCostToBuy = tCertificate.calcCertificateValue (tParPrice);
						aPlayerFrame.updateBuyButton ((tParPrice > 0), tCostToBuy);
						tHandled = true;
					}
				}
			}
		}

		return tHandled;
	}

	private String buildAbbrevAndType (String aAbbrev, String aType) {
		String tAbbrevAndType;

		tAbbrevAndType = aAbbrev + aType;

		return tAbbrevAndType;
	}

	public JPanel buildOwnershipPanel () {
		JPanel tOwnershipPanel = NO_PORTFOLIO_JPANEL;
		JLabel tCertificateOwnershipLabel;
		List<PortfolioSummary> tPortfolioSummary;
		PortfolioSummary tASummary;
		String tAbbrev;
		String tOwnershipLabel;
		String tNote;
		String tAbbrevAndType1;
		String tAbbrevAndType2;
		String tType;
		int tCount;
		int tPercentage;
		boolean tIsPresident;
		boolean tHandledCertificate;
		Border tCorporateColorBorder;
		Corporation tCorporation;

		if (certificates.size () > 0) {
			tOwnershipPanel = new JPanel ();
			tOwnershipPanel.setLayout (new BoxLayout (tOwnershipPanel, BoxLayout.Y_AXIS));

			tPortfolioSummary = new LinkedList<> ();
			for (Certificate tCertificate : certificates) {
				tType = PortfolioSummary.SHARE_CORP_TYPE;
				tAbbrev = tCertificate.getCompanyAbbrev ();
				if (tAbbrev.length () < 4) {
					tAbbrev += " ";
				}
				if (tCertificate.isAPrivateCompany ()) {
					tType = PortfolioSummary.PRIVATE_CORP_TYPE;
				} else if (tCertificate.isAMinorCompany ()) {
					tType = PortfolioSummary.MINOR_CORP_TYPE;
				}
				tAbbrevAndType1 = buildAbbrevAndType (tAbbrev, tType);
				tCount = 1;
				tPercentage = tCertificate.getPercentage ();
				tIsPresident = tCertificate.isPresidentShare ();
				tHandledCertificate = false;
				for (PortfolioSummary tASingleSummary : tPortfolioSummary) {
					// Test with both Abbrev and Type, to be sure to show B&O Private the B&O Share
					// Company Certs when owned by
					// the same player
					tAbbrevAndType2 = buildAbbrevAndType (tASingleSummary.getAbbrev (), tASingleSummary.getType ());

					if (tAbbrevAndType1.equals (tAbbrevAndType2)) {
						tASingleSummary.addCount (tCount);
						tASingleSummary.addPercentage (tPercentage);
						tASingleSummary.setIsPresident (tIsPresident);
						tHandledCertificate = true;
					}
				}
				if (!tHandledCertificate) {
					tCorporation = tCertificate.getCorporation ();
					tCorporateColorBorder = tCorporation.setupBorder ();
					tNote = tCorporation.getNote ();
					tASummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
							tCorporateColorBorder, tNote);
					tPortfolioSummary.add (tASummary);
				}
			}
			for (PortfolioSummary tASingleSummary : tPortfolioSummary) {
				tOwnershipLabel = tASingleSummary.getSummary ();
				tCertificateOwnershipLabel = new JLabel (tOwnershipLabel);
				tCorporateColorBorder = tASingleSummary.getCorporateColorBorder ();
				if (tCorporateColorBorder != PortfolioSummary.NO_BORDER) {
					tCertificateOwnershipLabel.setBorder (tCorporateColorBorder);
				}
				tCertificateOwnershipLabel.setToolTipText (tASingleSummary.getNote ());
				tOwnershipPanel.add (tCertificateOwnershipLabel);
			}
		}

		return tOwnershipPanel;
	}

	public void configurePrivateCompanyBenefitButtons (JPanel aButtonRow) {
		PrivateCompany tPrivateCompany;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isAPrivateCompany ()) {
				tPrivateCompany = (PrivateCompany) tCertificate.getCorporation ();
				if (tPrivateCompany.hasActiveCompanyBenefits ()) {
					tPrivateCompany.addBenefitButtons (aButtonRow);
				}
			}
		}
	}

	public void configurePrivatePlayerBenefitButtons (JPanel aButtonRow) {
		PrivateCompany tPrivateCompany;

		for (Certificate tCertificate : certificates) {
			if (tCertificate.isAPrivateCompany ()) {
				tPrivateCompany = (PrivateCompany) tCertificate.getCorporation ();
				if (tPrivateCompany.hasActivePlayerBenefits ()) {
					tPrivateCompany.addBenefitButtons (aButtonRow);
				}
			}
		}
	}

	public int getSmallestSharePercentage () {
		int tSmallestSharePercentage = 100;
		int tCertificatePercentage;

		for (Certificate tCertificate : certificates) {
			tCertificatePercentage = tCertificate.getPercentage ();
			if (tCertificatePercentage < tSmallestSharePercentage) {
				tSmallestSharePercentage = tCertificatePercentage;
			}
		}

		return tSmallestSharePercentage;
	}

	public FrameButton getFrameButtonAt (int aIndex) {
		FrameButton tFrameButton;
		Certificate tCertificate;

		tFrameButton = FrameButton.NO_FRAME_BUTTON;
		tCertificate = certificates.get (aIndex);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tFrameButton = tCertificate.getFrameButton ();
		}

		return tFrameButton;
	}

	public Certificate getNextFastBuyCertificate (int aFastBuyIndex, Player aPlayer) {
		Certificate tCertToBuy;
		String tPresidentName;
		Corporation tCorporation;
		ShareCompany tShareCompany;
		String tPlayerName;
		String tAbbrev;
		String tPrevAbbrev;
		int tPlayerCash;
		int tCertParValue;
		int tCorpIndex;
		Bank tBank;

		tCertToBuy = Certificate.NO_CERTIFICATE;
		tPlayerName = aPlayer.getName ();
		tCorpIndex = 0;
		tPrevAbbrev = "";
		for (Certificate tCertificate : certificates) {
			if (tCertToBuy == Certificate.NO_CERTIFICATE) {
				tCorporation = tCertificate.getCorporation ();
				if (tCorporation.isAShareCompany ()) {
					tShareCompany = (ShareCompany) tCorporation;
					tPresidentName = tShareCompany.getPresidentName ();
					tAbbrev = tShareCompany.getAbbrev ();
					if (tPlayerName.equals (tPresidentName)) {
						if (! tShareCompany.hasFloated ()) {
							if (! aPlayer.atCertLimit ()) {
								if (! aPlayer.hasMaxShares (tAbbrev)) {
									if (! tAbbrev.equals (tPrevAbbrev)) {
										tPrevAbbrev = tAbbrev;
										if (tCorpIndex == aFastBuyIndex) {
											tPlayerCash = aPlayer.getCash ();
											tCertParValue = tShareCompany.getParPrice ();
											if (tPlayerCash >= tCertParValue) {
												tBank = tCorporation.getBank ();
												tCertToBuy = tBank.getCertificateFromCorp (tCorporation, ! REMOVE_CERTIFICATE);
											}
										}
										tCorpIndex++;
									}
								}
							}
						}
					}
				}
			}
		}

		return tCertToBuy;
	}
}
