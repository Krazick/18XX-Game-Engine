package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.PurchaseOfferAction;
import ge18xx.train.Train;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BuyPrivateFrame extends JFrame implements ActionListener, ChangeListener, 
		PropertyChangeListener {
	private static final String SET_BUY_PRICE_ACTION = "SetBuyPrice";
	private static final String BUY_ACTION = "BuyPrivate";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton doSetPriceButton;
	JButton doBuyButton;
	Certificate certificate;
	JPanel privatePanel;
	ShareCompany shareCompany;
	String operatingRoundID;
	GameManager gameManager;
	JTextField priceField;
	JLabel corporationTreasuryLabel;
	JLabel ownerTreasuryLabel;
	JLabel frameLabel;
	int shareTreasury, remainingTreasury;
	
	public BuyPrivateFrame (ShareCompany aShareCompany) {
		super ("Buy Private Company");
		
		shareCompany = aShareCompany;
		gameManager = shareCompany.getGameManager ();
		privatePanel = new JPanel ();
		privatePanel.add (Box.createVerticalStrut (10));
		frameLabel = new JLabel ("Choose Buy Price");
		frameLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
		privatePanel.add (frameLabel);
		privatePanel.add (Box.createVerticalStrut (10));

		priceField = new JTextField ();
		priceField.setText ("0");
		priceField.setColumns (3);
		privatePanel.add (Box.createVerticalStrut (10));
		privatePanel.add (priceField);
		privatePanel.add (Box.createVerticalStrut (10));
		
		shareTreasury = shareCompany.getTreasury ();
		corporationTreasuryLabel = new JLabel ("Corporation");
		privatePanel.add (corporationTreasuryLabel);
		
		ownerTreasuryLabel = new JLabel ("Owner");
		setOwnerTreasuryLabel ();
		privatePanel.add (ownerTreasuryLabel);
		privatePanel.add (Box.createVerticalStrut (10));
		
		doSetPriceButton = buildButton ("Set Buy Price", SET_BUY_PRICE_ACTION);
		privatePanel.add (doSetPriceButton);
		doBuyButton = buildButton (CorporationFrame.BUY_PRIVATE, BUY_ACTION);
		
		privatePanel.add (doBuyButton);
		setCorporationTreasuryLabel ();
		
		add (privatePanel);

		pack ();
		setSize (500, 150);
		setVisible (false);
	}
	
	private int getPrice () {
		String tPrice;
		int tGetPrice;
		
		tPrice = priceField.getText ();
		if (tPrice.startsWith ("$")) {
			tPrice = tPrice.substring (1);
		}
		tPrice = tPrice.trim ();
		if (tPrice.equals ("")) {
			tGetPrice = 0;
		} else {
			try {
				tGetPrice = Integer.parseInt (tPrice);
			} catch (NumberFormatException eNFE) {
				tGetPrice = 0;
				priceField.setText ("0");
			}
		}
		
		return tGetPrice;
	}
	
	@Override
	public void requestFocus () {
		priceField.requestFocus ();
	}
	
	private void setBuyButtonText () {
		doBuyButton.setText ("Buy Private for " + Bank.formatCash (getPrice ()));
	}
	
	private void setCorporationTreasuryLabel () {
		remainingTreasury = shareTreasury - getPrice ();
		corporationTreasuryLabel.setText (shareCompany.getName () + " will have " + 
				Bank.formatCash (remainingTreasury) + " after purchase.\n");		
	}
	
	public void setDefaultPrice () {
		int tDefaultValue;
		
		tDefaultValue = certificate.getValue ();
		if (tDefaultValue > shareTreasury) {
			tDefaultValue = shareTreasury;
		}
		setPrice (tDefaultValue);
	}
	
	public void setOwnerTreasuryLabel () {
		Player tPlayer;
		CertificateHolderI tCertificateHolder;
		String tTreasury = "No Owner";
		int tPlayerCashAfter;
		
		if (certificate != Certificate.NO_CERTIFICATE) {
			tCertificateHolder = certificate.getOwner ();
			if (tCertificateHolder.isPlayer ()) {
				tPlayer = (Player) tCertificateHolder.getPortfolioHolder ();
				tPlayerCashAfter = tPlayer.getCash () + getPrice ();
				tTreasury = tPlayer.getName () + " will have " + 
						Bank.formatCash (tPlayerCashAfter) + " after purchase.\n";
				
			}
		}
		ownerTreasuryLabel.setText (tTreasury);
	}
	
	public void setPrice (int aPrice) {
		priceField.setText (aPrice + "");
		setBuyButtonText ();
	}
	
	public JButton buildButton (String aButtonLabel, String aActionCommand) {
		JButton tButton;
		
		tButton = new JButton (aButtonLabel);
		tButton.setAlignmentX (CENTER_ALIGNMENT);
		tButton.setActionCommand (aActionCommand);
		tButton.addActionListener (this);
		
		return tButton;
	}

	public boolean isGoodPrice () {
		boolean tGoodPrice;
		int tPrice, tValue, tLowPrice, tHighPrice;

		tPrice = getPrice ();
		if (certificate == Certificate.NO_CERTIFICATE) {
			tValue = 0;
		} else {
			tValue = certificate.getValue ();			
		}
		tLowPrice = tValue/2;
		tHighPrice = tValue * 2;

		tGoodPrice = true;
		if (tPrice < tLowPrice) {
			tGoodPrice = false;
		} else if (tPrice > tHighPrice) {
			tGoodPrice = false;
		} else if (tPrice > shareTreasury) {
			tGoodPrice = false;
		}

		return tGoodPrice;
	}
	
	public String getReasonForBad () {
		String tReasonForBad;
		int tPrice, tValue, tLowPrice, tHighPrice;
		
		tPrice = getPrice ();
		tValue = certificate.getValue ();
		tLowPrice = tValue/2;
		tHighPrice = tValue * 2;

		tReasonForBad = "Ready for Purchase";
		if (tPrice < tLowPrice) {
			tReasonForBad = "Must choose price > " + (tLowPrice - 1);
		} else if (tPrice > tHighPrice) {
			tReasonForBad = "Must choose price < " + (tHighPrice + 1);
		} else if (tPrice > shareTreasury) {
			tReasonForBad = shareCompany.getName () + " only has " + Bank.formatCash (shareTreasury) + " to spend.";
		}

		return tReasonForBad;
	}
	
	@Override
	public void actionPerformed (ActionEvent e) {
		boolean tGoodPrice;
		String tReasonForBad;
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand == SET_BUY_PRICE_ACTION) {
			tGoodPrice = isGoodPrice ();
			tReasonForBad = getReasonForBad ();
			setBuyButtonText ();
			setCorporationTreasuryLabel ();
			setOwnerTreasuryLabel ();
			doBuyButton.setEnabled (tGoodPrice);
			doBuyButton.setToolTipText (tReasonForBad);
		}
		if (tActionCommand == BUY_ACTION) {
			buyPrivateCertificate ();
			setVisible (false);
		}
	}
	
	private boolean samePresident (Player aOwningPlayer) {
		boolean tSamePresident = false;
		String tPresidentName, tOwningPresidentName;
		
		tOwningPresidentName = aOwningPlayer.getName ();
		tPresidentName = shareCompany.getPresidentName ();
		if (tOwningPresidentName.equals(tPresidentName)) {
			tSamePresident = true;
		}
		
		return tSamePresident;
	}
	
	private boolean needToMakeOffer (Player aOwningPlayer) {
		boolean tNeedToMakeOffer = true;
		
		if (gameManager.isNetworkGame ()) {
			if (samePresident (aOwningPlayer)) {
				tNeedToMakeOffer = false;
			}
		} else {
			tNeedToMakeOffer = false;
		}
		
		return tNeedToMakeOffer;
	}

	private void buyPrivateCertificate () {
		CertificateHolderI tCertificateHolder;
		Player tOwningPlayer;
		CorporationFrame tCorporationFrame;
		
		if (certificate != Certificate.NO_CERTIFICATE) {
			tCertificateHolder = certificate.getOwner ();
			if (tCertificateHolder.isPlayer ()) {
				tOwningPlayer = (Player) (tCertificateHolder.getPortfolioHolder ());
				if (needToMakeOffer (tOwningPlayer)) {
					if (makePurchaseOffer (tOwningPlayer)) {
						tCorporationFrame = shareCompany.getCorporationFrame ();
						tCorporationFrame.waitForResponse ();
					}
				} else {
					buyPrivateCompany (tOwningPlayer);
				}
			}
		}
	}

	public void buyPrivateCompany (Player aOwningPlayer) {
		int tCashValue;
		Portfolio tCompanyPortfolio;
		Portfolio tPlayerPortfolio;
		BuyStockAction tBuyStockAction;
		CorporationFrame tCorporationFrame;
		
		tBuyStockAction = new BuyStockAction (ActorI.ActionStates.OperatingRound, 
				operatingRoundID, shareCompany);
		tCashValue = getPrice ();
		shareCompany.transferCashTo (aOwningPlayer, tCashValue);
		tBuyStockAction.addCashTransferEffect (shareCompany, aOwningPlayer, tCashValue);
		tCompanyPortfolio = shareCompany.getPortfolio ();
		tPlayerPortfolio = aOwningPlayer.getPortfolio ();
		doFinalShareBuySteps (tCompanyPortfolio, tPlayerPortfolio, certificate, tBuyStockAction);
		tBuyStockAction.addBoughtShareEffect (shareCompany);
		shareCompany.addAction (tBuyStockAction);
		tCorporationFrame = shareCompany.getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}
	
	private boolean makePurchaseOffer (Player aOwningPlayer) {
		PurchaseOfferAction tPurchaseOfferAction;
		PurchaseOffer tPurchaseOffer;
		ActorI.ActionStates tOldState, tNewState;
		boolean tOfferMade = true;
		PrivateCompany tPrivateCompany;
		
		tOldState = shareCompany.getStatus ();
		tPrivateCompany = (PrivateCompany) certificate.getCorporation ();
		tPurchaseOffer = new PurchaseOffer (certificate.getCompanyName (), certificate.getCorpType (),
				Train.NO_TRAIN, tPrivateCompany,
				tPrivateCompany.getAbbrev (), aOwningPlayer.getName (), 
				getPrice (), tOldState);
		shareCompany.setPurchaseOffer (tPurchaseOffer);
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound,
				operatingRoundID, shareCompany);
		tPurchaseOfferAction.addPurchaseOfferEffect (shareCompany, aOwningPlayer, 
				getPrice (), certificate.getCorpType (), certificate.getCompanyAbbrev ());
		
		shareCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = shareCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (shareCompany, tOldState, tNewState);
		shareCompany.addAction (tPurchaseOfferAction);
		
		return tOfferMade;
	}
	
	public void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, 
			Certificate aCertificate, BuyStockAction aBuyStockAction) {
		ActorI.ActionStates tCurrentCorporationStatus, tNewCorporationStatus;
		PortfolioHolderI tFromHolder, tToHolder;
		PrivateCompany tPrivateCompany;
		
		tFromHolder = aFromPortfolio.getHolder ();
		tToHolder = aToPortfolio.getHolder ();
		tPrivateCompany = (PrivateCompany) aCertificate.getCorporation ();
		tPrivateCompany.removeBenefitButtons ();
		aToPortfolio.transferOneCertificateOwnership (aFromPortfolio, aCertificate);
		aBuyStockAction.addTransferOwnershipEffect (tFromHolder, aCertificate,  tToHolder);
		tCurrentCorporationStatus = aCertificate.getCorporationStatus ();
		aCertificate.updateCorporationOwnership ();
		tNewCorporationStatus = aCertificate.getCorporationStatus ();
		if (tCurrentCorporationStatus != tNewCorporationStatus) {
			aBuyStockAction.addStateChangeEffect (aCertificate.getCorporation (), 
					tCurrentCorporationStatus, tNewCorporationStatus);
		}
	}

	@Override
	public void stateChanged (ChangeEvent e) {
	    setCorporationTreasuryLabel ();
	}
	
	@Override
	public void propertyChange (PropertyChangeEvent e) {
		setCorporationTreasuryLabel ();
	}
	
	public void updateInfo (Certificate aCertificate) {
		int tLowPrice, tHighPrice, tCertPrice;
		
		shareTreasury = shareCompany.getTreasury ();
		certificate = aCertificate;
		tCertPrice = certificate.getValue ();
		setDefaultPrice ();
		tLowPrice = tCertPrice/2;
		tHighPrice = tCertPrice * 2;
		if (tHighPrice > shareTreasury) {
			tHighPrice = shareTreasury;
		}
		operatingRoundID = shareCompany.getOperatingRoundID ();
		setCorporationTreasuryLabel ();
		setOwnerTreasuryLabel ();
		
		frameLabel.setText (shareCompany.getPresidentName () + ", choose Buy Price for " + 
				certificate.getCompanyAbbrev () + " from " + certificate.getOwnerName () + 
				" Range [" + Bank.formatCash (tLowPrice) + " to " + Bank.formatCash (tHighPrice) + "]");
	}
}
