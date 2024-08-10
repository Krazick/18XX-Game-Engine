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
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;

public class StartPacketFrame extends XMLFrame implements LoadableXMLI, PortfolioHolderLoaderI {
	private static final long serialVersionUID = 1L;
	public static final ElementName EN_ITEM = new ElementName ("Item");
	public static final ElementName EN_START_PACKET = new ElementName ("StartPacket");
	public static final StartPacketFrame NO_START_PACKET = null;
	public static final String SP_NAME = "Start Packet";
	public static final String SPFRAME_SUFFIX = " " + SP_NAME + " Frame";
	public static final int NO_ACTIVE_ROW = -1;
	public enum StartPacketStates { NO_ROWS, FIRST_ROW, SECOND_ROW, OTHER_ROWS, ALL_ROWS };

	List<StartPacketRow> startPacketRows;
	StartPacketPortfolio portfolio;
	GameManager gameManager;
	JPanel portfolioInfoJPanel;

	ParsingRoutineI itemParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			StartPacketRow tStartPacketRow;

			tStartPacketRow = new StartPacketRow (aChildNode);
			startPacketRows.add (tStartPacketRow);
		}
	};

	ParsingRoutineI startPacketParsingRoutine = new ParsingRoutineI () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			XMLNodeList tXMLNodeList;

			tXMLNodeList = new XMLNodeList (itemParsingRoutine);
			tXMLNodeList.parseXMLNodeList (aChildNode, EN_ITEM);
		}
	};

	public StartPacketFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		gameManager = aGameManager;
		startPacketRows = new LinkedList<> ();
		portfolio = new StartPacketPortfolio (this);
	}

	@Override
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
	}

	public void applyDiscount () {
		portfolio.applyDiscount ();
	}

	public boolean availableShareHasBids () {
		return nextShareHasBids (0);
	}

	public JPanel buildStartPacketInfoJPanel (ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tSPPortfolioJPanel;
		JPanel tRowJPanel;
		BoxLayout tSPLayout;
		boolean tPreviousRowSoldOut;
		String tSelectedButtonLabel;
		StartPacketStates tSPState;
		int tRowCount;
		int tBuyNItems;

		tSPPortfolioJPanel = new JPanel ();
		tSPLayout = new BoxLayout (tSPPortfolioJPanel, BoxLayout.Y_AXIS);
		tSPPortfolioJPanel.setLayout (tSPLayout);
		tSPPortfolioJPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
		tPreviousRowSoldOut = true;
		tSPState = getInitialSPState ();
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tBuyNItems = tStartPacketRow.getBuyNItems ();
			tSelectedButtonLabel = getButtonLabel (tPreviousRowSoldOut, tBuyNItems);
			tRowCount = tStartPacketRow.getAvailableItemCount ();
			if (tRowCount > 0) {
				if (tSPState != StartPacketStates.ALL_ROWS)  {
					if (tSPState == StartPacketStates.NO_ROWS) {
						tSPState = StartPacketStates.FIRST_ROW;
						tBuyNItems = tRowCount;
					} else if (tSPState == StartPacketStates.FIRST_ROW) {
						tSPState = StartPacketStates.SECOND_ROW;
					} else if (tSPState == StartPacketStates.SECOND_ROW) {
						tSPState = StartPacketStates.OTHER_ROWS;
						tSelectedButtonLabel = GUI.EMPTY_STRING;
					} else if (tSPState == StartPacketStates.OTHER_ROWS) {
						tSelectedButtonLabel = GUI.EMPTY_STRING;
					}
				}
				tRowJPanel = tStartPacketRow.buildRowJPanel (tSelectedButtonLabel, tPreviousRowSoldOut, tBuyNItems,
						aItemListener, aPlayer, aGameManager);
				tSPPortfolioJPanel.add (Box.createVerticalGlue ());
				tSPPortfolioJPanel.add (tRowJPanel);
				tSPPortfolioJPanel.add (Box.createVerticalGlue ());
				tPreviousRowSoldOut = false;
			} else {
				if (tSPState == StartPacketStates.FIRST_ROW) {
					tSPState = StartPacketStates.OTHER_ROWS;
				}
			}
		}

		return tSPPortfolioJPanel;
	}

	public StartPacketStates getInitialSPState () {
		StartPacketStates tStartPacketState;
		int tBuyNItems;
		
		tStartPacketState = StartPacketStates.ALL_ROWS;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tBuyNItems = tStartPacketRow.getBuyNItems ();
			if (tBuyNItems > 0) {
				tStartPacketState = StartPacketStates.NO_ROWS;
			}
		}
		
		return tStartPacketState;
	}
	
	public String getButtonLabel (boolean aPreviousRowSoldOut, int aBuyNItems) {
		String tButtonLabel;
		
		if (aPreviousRowSoldOut) {
			tButtonLabel = Player.BUY_LABEL;
		} else if (aBuyNItems == 0) {
			tButtonLabel = Player.BID_LABEL;
		} else {
			tButtonLabel = Player.BUY_LABEL;
		}
		
		return tButtonLabel;
	}
	
	public void clearSelections () {
		portfolio.clearSelections ();
	}

	@Override
	public void completeBenefitInUse () {
	}

	public void disbleAllCheckedButtons (String aToolTip) {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.disableAllCheckedButtons (aToolTip);
		}
	}

	public Certificate getMustBuyCertificate () {
		Certificate tMustBuyCertificate = Certificate.NO_CERTIFICATE;

		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tMustBuyCertificate == Certificate.NO_CERTIFICATE) {
				tMustBuyCertificate = tStartPacketRow.getMustBuyCertificate ();
			}
		}
		
		return tMustBuyCertificate;
	}
		
	public void enableMustBuyPrivateButton () {
		boolean enableMustBuyPrivateButton = false;

		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (!enableMustBuyPrivateButton) {
				enableMustBuyPrivateButton = tStartPacketRow.enableMustBuyPrivateButton ();
			}
		}
	}

	public void enableSelectedButton (String aToolTip) {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.enableSelectedButton (aToolTip);
		}
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
	}

	@Override
	public String getAbbrev () {
		return "SPF";
	}

	private int getActiveRowCount () {
		int tActiveRowCount = 0;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (! tStartPacketRow.isRowSoldOut (portfolio)) {
				tActiveRowCount++;
			}
		}

		return tActiveRowCount;
	}

	@Override
	public Bank getBank () {
		return gameManager.getBank ();
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

	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;

		tCurrentHolder = portfolio.getCurrentHolder (aLoadedCertificate);

		return tCurrentHolder;
	}

	private int getFirstActiveRow () {
		int tFirstActiveRow = NO_ACTIVE_ROW;

		for (int tIndex = 0; tIndex < startPacketRows.size (); tIndex++) {

			StartPacketRow tStartPacketRow = startPacketRows.get (tIndex);

			if (! tStartPacketRow.isRowSoldOut (portfolio)) {
				if (tFirstActiveRow == NO_ACTIVE_ROW) {
					tFirstActiveRow = tIndex;
				}
			}
		}

		return tFirstActiveRow;
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

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tCertificate == Certificate.NO_CERTIFICATE) {
				tCertificate = tStartPacketRow.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
			}
		}

		return tCertificate;
	}

	public Certificate getMustSellCertificate () {
		return portfolio.getMustSellCertificate ();
	}

	@Override
	public String getName () {
		return getTypeName ();
	}

	@Override
	public Portfolio getPortfolio () {
		return portfolio;
	}

	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return this;
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

	public StartPacketPortfolio getStartPacketPortfolio () {
		return portfolio;
	}

	public StartPacketRow getStartPacketRowAt (int aIndex) {
		return startPacketRows.get (aIndex);
	}

	public int getStartPacketRowCount () {
		return startPacketRows.size ();
	}

	@Override
	public String getStateName () {
		return ActorI.ActionStates.Fixed.toString ();
	}

	@Override
	public String getTypeName () {
		return SP_NAME;
	}

	public boolean hasMustBuyCertificate () {
		return portfolio.hasMustBuyCertificate ();
	}

	public boolean hasMustSell () {
		return portfolio.hasMustSell ();
	}

	public boolean hasThisCertificate (Certificate aThisCertificate) {
		return portfolio.hasThisCertificate (aThisCertificate);
	}

	@Override
	public boolean isABank () {
		return true;
	}

	@Override
	public boolean isAOperatingRound () {
		return false;
	}

	@Override
	public boolean isAPrivateCompany () {
		return false;
	}

	@Override
	public boolean isAShareCompany () {
		return false;
	}

	@Override
	public boolean isAStockRound () {
		return false;
	}

	@Override
	public boolean isATrainCompany () {
		return false;
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

		startPacketRows = new LinkedList<> ();
		XMLStartPacketRoot = aXMLDocument.getDocumentNode ();
		tXMLNodeList = new XMLNodeList (startPacketParsingRoutine);
		tXMLNodeList.parseXMLNodeList (XMLStartPacketRoot, EN_START_PACKET);
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.setStartPacket (this);
		}
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
		}

		return tNextShareHasBids;
	}

	public boolean noMustSellLeft () {
		return portfolio.noMustSellLeft ();
	}

	public void printStartPacket () {
		System.out.println (SP_NAME);			// PRINTLOG method
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.printStartPacketRow ();
		}
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for Start Packet Frame Class
	}

	@Override
	public void updateListeners (String aMessage) {
		
	}
	
	public StartPacketItem getStartPacketItem (int aCorporationID) {
		StartPacketItem tStartPacketItem;
		
		tStartPacketItem = StartPacketItem.NO_START_PACKET_ITEM;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			if (tStartPacketItem == StartPacketItem.NO_START_PACKET_ITEM) {
				tStartPacketItem = tStartPacketRow.getStartPacketItem (aCorporationID);
			}	
		}
		
		return tStartPacketItem;
	}
	
	public String getCertificateLocation (Certificate aCertificate) {
		StartPacketRow tStartPacketRow;
		String tCertificateLocation;
		int tRowCount;
		int tRowIndex;
		int tRowLocation;
		boolean tFoundCertificate;
		
		tCertificateLocation = "";
		tRowCount = startPacketRows.size ();
		tFoundCertificate = false;
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			if (! tFoundCertificate) {
				tStartPacketRow = startPacketRows.get (tRowIndex);
				tRowLocation = tStartPacketRow.getCerticateLocation (aCertificate);
				if (tRowLocation != StartPacketRow.NO_ROW_LOCATION) {
					tFoundCertificate = true;
					tCertificateLocation = tRowIndex + "," + tRowLocation;
				}
			}
		}
		
		return tCertificateLocation;
	}
	
	public void updateStartPacket () {
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketRow.updateStartPacketRow ();
		}
	}
	
	public StartPacketItem removeCertificateFromRow (Certificate aCertificate) {
		StartPacketItem tStartPacketItem;
		StartPacketItem tRemovedStartPacketItem;
		
		tRemovedStartPacketItem = StartPacketItem.NO_START_PACKET_ITEM;
		for (StartPacketRow tStartPacketRow : startPacketRows) {
			tStartPacketItem = tStartPacketRow.removeCertificate (aCertificate);
			if (tStartPacketItem != StartPacketItem.NO_START_PACKET_ITEM) {
				tRemovedStartPacketItem = tStartPacketItem;
			}
		}

		return tRemovedStartPacketItem;
	}
	
	@Override
	public GameManager getGameManager () {
		return gameManager;
	}
}
