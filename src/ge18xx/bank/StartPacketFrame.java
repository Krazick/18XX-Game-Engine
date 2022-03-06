package ge18xx.bank;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.LoadedCertificate;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.player.StartPacketPortfolio;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.LoadableXMLI;
import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;
import ge18xx.utilities.XMLDocument;

public class StartPacketFrame extends XMLFrame implements LoadableXMLI, PortfolioHolderLoaderI {
	private static final ElementName EN_START_PACKET = new ElementName ("StartPacket");
	private static final ElementName EN_ITEM = new ElementName ("Item");
	public static final String SP_NAME = "Start Packet";
	public static final String SPFRAME_SUFFIX = " " + SP_NAME + " Frame";
	private static final long serialVersionUID = 1L;
	private static final int NO_ACTIVE_ROW = -1;
	public static final StartPacketFrame NO_START_PACKET = null;
	List<StartPacketRow> startPacketRows;
	GameManager gameManager;
	JPanel portfolioInfoJPanel;
	StartPacketPortfolio portfolio;
	
	public StartPacketFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getActiveGameName ());
		setGameManager (aGameManager);
		startPacketRows = new LinkedList<StartPacketRow> ();
		portfolio = new StartPacketPortfolio (this);
	}
	
	@Override	
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
	}
	
	public JPanel buildStartPacketInfoJPanel (ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tSPPortfolioJPanel;
		JPanel tRowJPanel;
		BoxLayout tSPLayout;
		boolean tPreviousRowSoldOut;
		String tSelectedButtonLabel;
		
		tSPPortfolioJPanel = new JPanel ();
		tSPLayout = new BoxLayout (tSPPortfolioJPanel, BoxLayout.Y_AXIS);
		tSPPortfolioJPanel.setLayout (tSPLayout);
		tSPPortfolioJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tPreviousRowSoldOut = true;
		
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tPreviousRowSoldOut) {
				tSelectedButtonLabel = Player.BUY_LABEL;
			} else {
				tSelectedButtonLabel = Player.BID_LABEL;
			}
			if (tStartPacketRow.isRowNotSoldOut (portfolio)) {
				tRowJPanel = tStartPacketRow.buildRowJPanel (tSelectedButtonLabel, 
						aItemListener, aPlayer, aGameManager);
				tSPPortfolioJPanel.add (Box.createVerticalGlue ());
				tSPPortfolioJPanel.add (tRowJPanel);
				tSPPortfolioJPanel.add (Box.createVerticalGlue ());
				tPreviousRowSoldOut = false;
			}
		}

		return tSPPortfolioJPanel;
	}
	
	public int getStartPacketRowCount () {
		return startPacketRows.size ();
	}
	
	public StartPacketRow getStartPacketRowAt (int aIndex) {
		return startPacketRows.get (aIndex);
	}
	
	public boolean noMustSellLeft () {
		return portfolio.noMustSellLeft ();
	}
	
	public void applyDiscount () {
		portfolio.applyDiscount ();
	}
	
	public boolean hasMustBuyCertificate () {
		return portfolio.hasMustBuyCertificate ();
	}
	
	public boolean hasMustSell () {
		return portfolio.hasMustSell ();
	}
	
	public Certificate getMustSellCertificate () {
		return portfolio.getMustSellCertificate ();
	}
	
	public void clearSelections () {
		portfolio.clearSelections ();
	}
	
	public int getCertificateCount () {
		return portfolio.getCertificateCountAgainstLimit ();
	}

	public Certificate getCertificateToBidOn () {
		return portfolio.getCertificateToBidOn ();
	}

	public Certificate getCertificateToBuy () {
		return portfolio.getCertificateToBuy ();
	}
	
	public Certificate getPrivateForAuction () {
		Certificate tCertificateToAuction;
		
		tCertificateToAuction = Certificate.NO_CERTIFICATE;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tCertificateToAuction == Certificate.NO_CERTIFICATE) {
				tCertificateToAuction = tStartPacketRow.getCertificateToAuction ();				
			}
		}
	
		return tCertificateToAuction;
	}
	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = portfolio.getCurrentHolder (aLoadedCertificate);

		return tCurrentHolder;
	}
	
	public Certificate getFreeCertificateWithThisCertificate (Certificate aThisCertificate) {
		Certificate tFreeCertificate;
		
		tFreeCertificate = Certificate.NO_CERTIFICATE;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tFreeCertificate == Certificate.NO_CERTIFICATE) {
				tFreeCertificate = tStartPacketRow.getFreeCertificateWithThisCertificate (aThisCertificate);				
			}
		}
	
		return tFreeCertificate;
	}
	
	@Override
	public String getName () {
		return getTypeName ();
	}
	
	public StartPacketPortfolio getStartPacketPortfolio () {
		return portfolio;
	}
	
	@Override
	public String getTypeName () {
		return SP_NAME;
	}

	public boolean hasThisCertificate (Certificate aThisCertificate) {
		return portfolio.hasThisCertificate (aThisCertificate);
	}
	
	public boolean isStartPacketPortfolioEmpty () {
		return portfolio.isEmpty ();
	}
	
	public boolean loadStartPacketWithCertificates (Portfolio aBankPortfolio) {
		boolean tAllCertsLoaded = true;
		
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tAllCertsLoaded &= tStartPacketRow.loadWithCertificates (aBankPortfolio, portfolio);
		}
		return tAllCertsLoaded;
	}
	
	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		XMLNodeList tXMLNodeList;
		XMLNode XMLStartPacketRoot;
		
		startPacketRows = new LinkedList<StartPacketRow> ();
		XMLStartPacketRoot = aXMLDocument.getDocumentElement ();
		tXMLNodeList = new XMLNodeList (startPacketParsingRoutine);
		tXMLNodeList.parseXMLNodeList (XMLStartPacketRoot, EN_START_PACKET);
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.setStartPacket (this);
		}
	}
	
	private int getActiveRowCount () {
		int tActiveRowCount = 0;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tStartPacketRow.isRowNotSoldOut (portfolio)) {
				tActiveRowCount++;
			}
		}
		
		return tActiveRowCount;
	}
	
	private int getFirstActiveRow () {
		int tFirstActiveRow = NO_ACTIVE_ROW;
		
		for (int tIndex = 0; tIndex < startPacketRows.size (); tIndex++) {
			
			StartPacketRow tStartPacketRow = startPacketRows.get(tIndex);
			
			if (tStartPacketRow.isRowNotSoldOut (portfolio)) {
				if (tFirstActiveRow == NO_ACTIVE_ROW) {
					tFirstActiveRow = tIndex;
				}
			}
		}
	
		return tFirstActiveRow;
	}
	
	public boolean availableShareHasBids () {
		return nextShareHasBids (0);
	}
	
	public boolean nextShareHasBids () {
		return nextShareHasBids (1);
	}
	
	public boolean nextShareHasBids (int aRowAdjustment) {
		int tCurrentRowsCount;
		int tFirstActiveRow;
		boolean tNextShareHasBids = false;
		Certificate tCertificate = Certificate.NO_CERTIFICATE;
		StartPacketRow tCurrentPacketRow;
		StartPacketRow tNextPacketRow;
		
		tCurrentRowsCount = getActiveRowCount ();
		tFirstActiveRow = getFirstActiveRow ();

		if (tFirstActiveRow != NO_ACTIVE_ROW) {
			tCurrentPacketRow = startPacketRows.get (tFirstActiveRow);
			if (tCurrentPacketRow.isOneLeftInRow ()) {
				// At least one additional row, and therefore item -- may have bids
				if (tCurrentRowsCount > 1) {
					tNextPacketRow = startPacketRows.get (tFirstActiveRow + aRowAdjustment);
					tCertificate = tNextPacketRow.getCertificateInRow (0);
					tNextShareHasBids = tCertificate.hasBidders ();
				} else {
					if (aRowAdjustment == 1) {
						tNextShareHasBids = false;
					} else {
						tCertificate = tCurrentPacketRow.getCertificateInRow (0);
						tNextShareHasBids = tCertificate.hasBidders ();
					}
				}	
			} else {
				tCertificate = tCurrentPacketRow.getCertificateInRow (1);
				if (tCertificate == Certificate.NO_CERTIFICATE) {
					System.err.println ("No Certificate Returned");
				} else {
					tNextShareHasBids = tCertificate.hasBidders ();
				}
			}
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				System.err.println ("Next Share to in Start Packet Row " + tFirstActiveRow + " with count " + tCurrentRowsCount + " NOT FOUND");
			}
		}
		
		return tNextShareHasBids;
	}
	
	public void printStartPacket () {
		System.out.println (SP_NAME);
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.printStartPacketRow ();
		}
	}
	
	private void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}

	ParsingRoutineI startPacketParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			XMLNodeList tXMLNodeList;
		
			tXMLNodeList = new XMLNodeList (itemParsingRoutine);
			tXMLNodeList.parseXMLNodeList (aChildNode, EN_ITEM);
		}
	};
	
	ParsingRoutineI itemParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			StartPacketRow tStartPacketRow;
			
			tStartPacketRow = new StartPacketRow (aChildNode);
			startPacketRows.add (tStartPacketRow);
		}
	};
	
	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
	}

	@Override
	public Portfolio getPortfolio () {
		return portfolio;
	}

	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return this;
	}
	
	@Override
	public String getStateName () {
		return ActorI.ActionStates.Fixed.toString ();
	}

	@Override
	public boolean isBank () {
		return true;
	}

	@Override
	public boolean isBankPool () {
		return false;
	}

	@Override
	public boolean isCompany () {
		return false;
	}
	
	@Override
	public boolean isAPrivateCompany () {
		return false;
	}

	@Override
	public boolean isPlayer () {
		return false;
	}

	@Override
	public void replacePortfolioInfo (JPanel aPortfolioJPanel) {
		
	}
	
	public void enableMustBuyPrivateButton () {
		boolean enableMustBuyPrivateButton = false;
		
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (! enableMustBuyPrivateButton) {
				enableMustBuyPrivateButton = tStartPacketRow.enableMustBuyPrivateButton ();
			}
		}
	}
	
	public void disbleAllCheckedButtons (String aToolTip) {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.disableAllCheckedButtons (aToolTip);
		}
	}
	
	public void enableAllCheckedButtons (String aToolTip, Player aPlayer) {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.enableAllCheckedButtons (aToolTip, aPlayer);			
		}
	}
	
	public void enableSelectedButton (String aToolTip) {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.enableSelectedButton (aToolTip);			
		}
	}

	@Override
	public String getAbbrev () {
		return "SPF";
	}

	@Override
	public Bank getBank () {
		return gameManager.getBank ();
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for Start Packet Frame Class
	}
	
	@Override
	public boolean isAPlayer () {
		return false;
	}
	
	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public boolean isABank () {
		return true;
	}

	@Override
	public boolean isACorporation () {
		return false;
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;
		
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				tCertificate = tStartPacketRow.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
			}
		}
		
		return tCertificate;
	}

	@Override
	public boolean isABankPool () {
		return false;
	}

	@Override
	public boolean isATrainCompany () {
		return false;
	}
	
	@Override
	public void completeBenefitInUse () {
	}
}
