package ge18xx.bank;

import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.game.FrameButton;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class StartPacketRow implements ParsingRoutineI {
	private static final ElementName EN_CERTIFICATE = new ElementName ("Certificate");
	private static final AttributeName AN_ROW = new AttributeName ("row");
	private static final AttributeName AN_BUY_N_ITEMS = new AttributeName ("buyNItems");
	public static final StartPacketRow NO_START_PACKET_ROW = null;
	public static final int NO_ROW_LOCATION = -1;
	int rowNumber;
	int buyNItems;
	StartPacketFrame startPacketFrame;
	List<StartPacketItem> startPacketItems;

	StartPacketRow (XMLNode aNode) {
		XMLNodeList tXMLNodeList;
		int tBuyNItems;
		int tRowNumber;
		
		tRowNumber = aNode.getThisIntAttribute (AN_ROW);
		tBuyNItems = aNode.getThisIntAttribute (AN_BUY_N_ITEMS, 0);
		startPacketItems = new LinkedList<> ();
		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aNode, EN_CERTIFICATE);
		setStartPacket (StartPacketFrame.NO_START_PACKET);
		setRowNumber (tRowNumber);
		setBuyNItems (tBuyNItems);
	}

	public void setRowNumber (int aRowNumber) {
		rowNumber = aRowNumber;
	}
	
	public void setBuyNItems (int aBuyNItems) {
		buyNItems = aBuyNItems;
	}
	
	public int getBuyNItems () {
		return buyNItems;
	}
	
	public int getRowNumber () {
		return rowNumber;
	}
	
	public JPanel buildRowJPanel (String aSelectedButtonLabel, boolean aPreviousRowSoldOut, int aBuyNItems,
			ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		JPanel tRowJPanel;
		BoxLayout tLayout;
		int tItemsShownAsBuy;

		tRowJPanel = new JPanel ();
		tRowJPanel.setBorder (BorderFactory.createTitledBorder ("Row " + rowNumber));
		tLayout = new BoxLayout (tRowJPanel, BoxLayout.X_AXIS);
		tRowJPanel.setLayout (tLayout);
		tItemsShownAsBuy = 0;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tStartPacketItem.available ()) {
				if (! aPreviousRowSoldOut) {
					if (tItemsShownAsBuy < aBuyNItems) {
						buildAndAddRowItem (aSelectedButtonLabel, aItemListener, aPlayer, aGameManager, tRowJPanel,
								tStartPacketItem);
						tItemsShownAsBuy++;
					} else {
						if (aSelectedButtonLabel.equals (Player.BID_LABEL)) {
							if (! tStartPacketItem.getCanBeBidOn ()) {
								aSelectedButtonLabel = GUI.EMPTY_STRING;
							}
						} else {
							aSelectedButtonLabel = GUI.EMPTY_STRING;
						}
						buildAndAddRowItem (aSelectedButtonLabel, aItemListener, aPlayer, aGameManager, tRowJPanel,
								tStartPacketItem);
					}
				} else {
					if (aSelectedButtonLabel.equals (Player.BID_LABEL)) {
						if (! tStartPacketItem.getCanBeBidOn ()) {
							aSelectedButtonLabel = GUI.EMPTY_STRING;
						}
					}
					buildAndAddRowItem (aSelectedButtonLabel, aItemListener, aPlayer, aGameManager, tRowJPanel,
							tStartPacketItem);
					
				}
		}
		}

		return tRowJPanel;
	}

	public void buildAndAddRowItem (String aSelectedButtonLabel, ItemListener aItemListener, Player aPlayer,
			GameManager aGameManager, JPanel tRowJPanel, StartPacketItem aStartPacketItem) {
		JPanel tRowItemJPanel;
		
		tRowItemJPanel = aStartPacketItem.buildStartPacketItemJPanel (aSelectedButtonLabel, aItemListener, aPlayer,
				aGameManager);
		if (tRowItemJPanel != GUI.NO_PANEL) {
			tRowJPanel.add (Box.createHorizontalGlue ());
			tRowJPanel.add (tRowItemJPanel);
			tRowJPanel.add (Box.createHorizontalGlue ());
		}
	}

	public int getAvailableItemCount () {
		int tAvailableItemCount;
		
		tAvailableItemCount = 0;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tStartPacketItem.available ()) {
				tAvailableItemCount++;
			}
		}

		return tAvailableItemCount;
	}
	
	public void disableAllCheckedButtons (String aToolTip) {
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tStartPacketItem.disableCheckedButton (aToolTip);
		}
	}

	public Certificate getMustBuyCertificate () {
		Certificate tMustBuyCertificate = Certificate.NO_CERTIFICATE;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tMustBuyCertificate == Certificate.NO_CERTIFICATE) {
				tMustBuyCertificate = tStartPacketItem.getMustBuyCertificate ();
			}
		}
		
		return tMustBuyCertificate;
	}

	public boolean enableMustBuyPrivateButton () {
		boolean tPrivateEnabled = false;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tPrivateEnabled = tStartPacketItem.enableMustBuyPrivateButton ();
		}

		return tPrivateEnabled;
	}

	public void enableSelectedButton (String aToolTip) {
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tStartPacketItem.isSelected ()) {
				tStartPacketItem.enableCheckedButton (aToolTip);
			}
		}
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		StartPacketItem tStartPacketItem;

		tStartPacketItem = new StartPacketItem (aChildNode);
		startPacketItems.add (tStartPacketItem);
		tStartPacketItem.setStartPacketRow (this);
	}

	public void addStartPacketItem (StartPacketItem tStartPacketItem, int tCol) {
		startPacketItems.add (tCol, tStartPacketItem);
	}
	
	public Certificate getCertificateInRow (int aIndex) {
		Certificate tItemCertificate;
		StartPacketItem tStartPacketItem;

		tItemCertificate = Certificate.NO_CERTIFICATE;
		if (validIndex (aIndex)) {
			tStartPacketItem = startPacketItems.get (aIndex);
			tItemCertificate = tStartPacketItem.getCertificate ();
		}

		return tItemCertificate;
	}
	
	public Certificate getCertificateToAuction () {
		Certificate tCertificateToAuction;
		Certificate tCertificate;

		tCertificateToAuction = Certificate.NO_CERTIFICATE;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tCertificateToAuction == Certificate.NO_CERTIFICATE) {
				tCertificate = tStartPacketItem.getCertificate ();
				if (tCertificate.hasBidders ()) {
					tCertificateToAuction = tCertificate;
				}
			}
		}

		return tCertificateToAuction;
	}

	public FrameButton getFrameButtonInRow (int aIndex) {
		Certificate tCertificate;
		FrameButton tFrameButton = FrameButton.NO_FRAME_BUTTON;

		tCertificate = getCertificateInRow (aIndex);
		if (tCertificate != Certificate.NO_CERTIFICATE) {
			tFrameButton = tCertificate.getFrameButton ();
		}

		return tFrameButton;
	}

	public Certificate getFreeCertificateWithThisCertificate (Certificate aThisCertificate) {
		Certificate tFreeCertificate;
		Certificate tItemCertificate;

		tFreeCertificate = Certificate.NO_CERTIFICATE;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tItemCertificate = tStartPacketItem.getCertificate ();
			if (tItemCertificate == aThisCertificate) {
				tFreeCertificate = tStartPacketItem.getFreeCertificate ();
			}
		}

		return tFreeCertificate;
	}

	public int getItemCount () {
		return startPacketItems.size ();
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tCertificate = tStartPacketItem.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
		}

		return tCertificate;
	}

	boolean isOneLeftInRow () {
		return (startPacketItems.size () == 1);
	}

	boolean isRowEmpty () {
		return startPacketItems.isEmpty ();
	}

	boolean isRowSoldOut (Portfolio aStartPacketPortfolio) {
		boolean tIsRowSoldOut;
		Certificate tThisCertificate;

		tIsRowSoldOut = true;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tThisCertificate = tStartPacketItem.getCertificate ();
			if (startPacketFrame.hasThisCertificate (tThisCertificate)) {
				tIsRowSoldOut = false;
			}
		}

		return tIsRowSoldOut;
	}
	
	boolean isRowNotSoldOut (Portfolio aStartPacketPortfolio) {
		boolean tRowNotSoldOut = true;
		Certificate tThisCertificate;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tThisCertificate = tStartPacketItem.getCertificate ();
			if (!startPacketFrame.hasThisCertificate (tThisCertificate)) {
				tRowNotSoldOut = false;
			}
		}

		return tRowNotSoldOut;
	}

	public boolean loadWithCertificates (Portfolio aBankPortfolio, Portfolio aStartPacketPortfolio) {
		boolean tAllCertsLoaded = true;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tAllCertsLoaded &= tStartPacketItem.loadWithCertificates (aBankPortfolio, aStartPacketPortfolio);
		}

		return tAllCertsLoaded;
	}

	void printStartPacketRow () {
		System.out.println ("Start Packet Row # " + rowNumber);		// PRINTLOG method
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tStartPacketItem.printStartPacketItemInfo ();
		}
	}

	void setStartPacket (StartPacketFrame aStartPacket) {
		startPacketFrame = aStartPacket;
	}

	private boolean validIndex (int aIndex) {
		boolean tValidIndex;

		tValidIndex = false;
		if ((aIndex >= 0) && (aIndex < getItemCount ())) {
			tValidIndex = true;
		}

		return tValidIndex;
	}
	
	public StartPacketItem getStartPacketItem (int aCorporationID) {
		StartPacketItem tFoundStartPacketItem;
		
		tFoundStartPacketItem = StartPacketItem.NO_START_PACKET_ITEM;
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tFoundStartPacketItem == StartPacketItem.NO_START_PACKET_ITEM) {
				if (tStartPacketItem.getCorporationId () == aCorporationID) {
					tFoundStartPacketItem = tStartPacketItem;
				}
			}	
		}
		
		return tFoundStartPacketItem;
	}

	public void updateStartPacketRow () {
		Certificate tCertificate;
		
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tCertificate = tStartPacketItem.getCertificate ();
			if (! startPacketFrame.hasThisCertificate (tCertificate)) {
				tStartPacketItem.setAvailable (false);
			}
		}
	}
	
	public StartPacketItem removeCertificate (Certificate aCertificate) {
		StartPacketItem tStartPacketItem;
		int tItemCount;
		int tItemFound;
		int tItemIndex;
		
		tItemCount = getItemCount ();
		tItemFound = NO_ROW_LOCATION;
		for (tItemIndex = 0; tItemIndex < tItemCount; tItemIndex++) {
			tStartPacketItem = startPacketItems.get (tItemIndex);
			if (tStartPacketItem.containsCertificate (aCertificate)) {
				tItemFound = tItemIndex;
			}
		}
		if (tItemFound >= 0) {
			tStartPacketItem = startPacketItems.get (tItemFound);
			tStartPacketItem.setAvailable (false);
		} else {
			tStartPacketItem = StartPacketItem.NO_START_PACKET_ITEM;
		}
		
		return tStartPacketItem;
	}

	public int getCerticateLocation (Certificate aCertificate) {
		StartPacketItem tStartPacketItem;
		int tItemCount;
		int tItemFound;
		int tItemIndex;

		tItemCount = getItemCount ();
		tItemFound = NO_ROW_LOCATION;
		for (tItemIndex = 0; tItemIndex < tItemCount; tItemIndex++) {
			tStartPacketItem = startPacketItems.get (tItemIndex);
			if (tStartPacketItem.containsCertificate (aCertificate)) {
				tItemFound = tItemIndex;
			}
		}

		return tItemFound;
	}
}
