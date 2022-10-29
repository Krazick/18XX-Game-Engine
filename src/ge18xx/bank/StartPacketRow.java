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
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

public class StartPacketRow implements ParsingRoutineI {
	public static final StartPacketRow NO_START_PACKET_ROW = null;
	private static final ElementName EN_CERTIFICATE = new ElementName ("Certificate");
	private static final AttributeName AN_ROW = new AttributeName ("row");
	StartPacketFrame startPacketFrame;
	int rowNumber;
	List<StartPacketItem> startPacketItems;

	StartPacketRow (XMLNode aNode) {
		XMLNodeList tXMLNodeList;

		rowNumber = aNode.getThisIntAttribute (AN_ROW);
		startPacketItems = new LinkedList<> ();
		tXMLNodeList = new XMLNodeList (this);
		tXMLNodeList.parseXMLNodeList (aNode, EN_CERTIFICATE);
		setStartPacket (StartPacketFrame.NO_START_PACKET);
	}

	public JPanel buildRowJPanel (String aSelectedButtonLabel, ItemListener aItemListener, Player aPlayer,
			GameManager aGameManager) {
		JPanel tRowJPanel;
		JPanel tRowItemJPanel;
		BoxLayout tLayout;

		tRowJPanel = new JPanel ();
		tRowJPanel.setBorder (BorderFactory.createTitledBorder ("Row " + rowNumber));
		tLayout = new BoxLayout (tRowJPanel, BoxLayout.X_AXIS);
		tRowJPanel.setLayout (tLayout);
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tRowItemJPanel = tStartPacketItem.buildStartPacketItemJPanel (aSelectedButtonLabel, aItemListener, aPlayer,
					aGameManager);
			tRowJPanel.add (Box.createHorizontalGlue ());
			tRowJPanel.add (tRowItemJPanel);
			tRowJPanel.add (Box.createHorizontalGlue ());
		}

		return tRowJPanel;
	}

	// Return true if only one is left. Needed to verify if next Certificate is in
	// current row, or next row.

	boolean isOneLeftInRow () {
		return (startPacketItems.size () == 1);
	}

	boolean isRowEmpty () {
		return startPacketItems.isEmpty ();
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

	public int getItemCount () {
		return startPacketItems.size ();
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

	public Certificate getCertificateInRow (int aIndex) {
		Certificate tItemCertificate;
		StartPacketItem tStartPacketItem;

		tItemCertificate = Certificate.NO_CERTIFICATE;
		if (validIndex (aIndex)) {
			tStartPacketItem = startPacketItems.get (aIndex);
			tItemCertificate = tStartPacketItem.getCertificate ();
		} else {
			System.err.println ("Only " + getItemCount () + " Items in Row, asked for " + aIndex);
		}

		return tItemCertificate;
	}

	private boolean validIndex (int aIndex) {
		boolean tValidIndex;

		tValidIndex = false;
		if ((aIndex >= 0) && (aIndex < getItemCount ())) {
			tValidIndex = true;
		}

		return tValidIndex;
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

	public boolean loadWithCertificates (Portfolio aBankPortfolio, Portfolio aStartPacketPortfolio) {
		boolean tAllCertsLoaded = true;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tAllCertsLoaded &= tStartPacketItem.loadWithCertificates (aBankPortfolio, aStartPacketPortfolio);
		}

		return tAllCertsLoaded;
	}

	void printStartPacketRow () {
		System.out.println ("Start Packet Row # " + rowNumber);
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tStartPacketItem.printStartPacketItemInfo ();
		}
	}

	void setStartPacket (StartPacketFrame aStartPacket) {
		startPacketFrame = aStartPacket;
	}

	@Override
	public void foundItemMatchKey1 (XMLNode aChildNode) {
		StartPacketItem tStartPacketItem;

		tStartPacketItem = new StartPacketItem (aChildNode);
		startPacketItems.add (tStartPacketItem);
		tStartPacketItem.setStartPacketRow (this);
	}

	public boolean enableMustBuyPrivateButton () {
		boolean tPrivateEnabled = false;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tPrivateEnabled = tStartPacketItem.enableMustBuyPrivateButton ();
		}

		return tPrivateEnabled;
	}

	public void disableAllCheckedButtons (String aToolTip) {
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tStartPacketItem.disableCheckedButton (aToolTip);
		}
	}

	public void enableAllCheckedButtons (String aToolTip, Player aPlayer) {
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (!tStartPacketItem.hasBidOnThisCert (aPlayer)) {
				tStartPacketItem.enableCheckedButton (aToolTip);
			}
		}
	}

	public void enableSelectedButton (String aToolTip) {
		for (StartPacketItem tStartPacketItem : startPacketItems) {
			if (tStartPacketItem.isSelected ()) {
				tStartPacketItem.enableCheckedButton (aToolTip);
			}
		}
	}

	public Certificate getMatchingCertificate (String aAbbrev, int aPercentage, boolean aIsPresident) {
		Certificate tCertificate = Certificate.NO_CERTIFICATE;

		for (StartPacketItem tStartPacketItem : startPacketItems) {
			tCertificate = tStartPacketItem.getMatchingCertificate (aAbbrev, aPercentage, aIsPresident);
		}

		return tCertificate;
	}
}
