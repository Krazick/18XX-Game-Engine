package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.train.Train;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuyPrivateFrame extends BuyItemFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	Certificate certificate;

	public BuyPrivateFrame (TrainCompany aTrainCompany, PortfolioHolderI aOwner, 
			Certificate aCertificate) {
		super (CorporationFrame.BUY_PRIVATE, aTrainCompany);
		
		Player tPlayer;
		
		setAllButtonListeners (this);
		certificate = aCertificate;
		if (aOwner.isAPlayer ()) {
			tPlayer = (Player) aOwner;
			setCurrentOwner (tPlayer);
		} else {
			setCurrentOwner (Player.NO_PLAYER);
		}
		updateInfo ();
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		String tActionCommand;
		
		tActionCommand = e.getActionCommand ();
		if (tActionCommand == SET_BUY_PRICE_ACTION) {
			updateButtons ();
			updateBuyerInfo ();
			updateSellerInfo ();
		}
		if (tActionCommand == BUY_ACTION) {
			buyPrivateCertificate ();
			setVisible (false);
		}
	}

	@Override
	protected void setDefaultPrice () {
		int tDefaultValue;
		int tShareTreasury;
		
		tShareTreasury = trainCompany.getTreasury ();

		tDefaultValue = certificate.getValue ();
		if (tDefaultValue > tShareTreasury) {
			tDefaultValue = tShareTreasury;
		}
		setPrice (tDefaultValue);
	}

	private void updateInfo () {
		int tLowPrice, tHighPrice, tCertPrice;
		String tDescription;
		int tShareTreasury;
		
		tShareTreasury = trainCompany.getTreasury ();
		tCertPrice = certificate.getValue ();
		setDefaultPrice ();
		tLowPrice = tCertPrice / 2;
		tHighPrice = tCertPrice * 2;
		if (tHighPrice > tShareTreasury) {
			tHighPrice = tShareTreasury;
		}
		
		tDescription = trainCompany.getPresidentName () + ", Choose Buy Price for " + 
				certificate.getCompanyAbbrev () + " " + PurchaseOffer.PRIVATE_TYPE + " from " + 
				currentOwner.getName ();
		updateSellerInfo ();
		updateInfo (PurchaseOffer.PRIVATE_TYPE, tLowPrice, tHighPrice, tDescription);
	}

	private void updateSellerInfo () {
		String tOwnerName = "NO OWNER";
		int tTreasury = 0;
		String tSellerInfo;
		
		if (currentOwner != Player.NO_PLAYER) {
			tOwnerName = currentOwner.getName ();
			tTreasury = getCurrentOwnerCash () + getPrice ();
		}
		tSellerInfo =  tOwnerName + " will have " + Bank.formatCash (tTreasury) + 
						" cash after purchase.";
		updateSellerInfo (tSellerInfo);
	}

	private void buyPrivateCertificate () {
		CertificateHolderI tCertificateHolder;
		Player tOwningPlayer;
		CorporationFrame tCorporationFrame;
		PurchaseOffer tPurchaseOffer;
		
		if (certificate != Certificate.NO_CERTIFICATE) {
			tCertificateHolder = certificate.getOwner ();
			if (tCertificateHolder.isAPlayer ()) {
				tOwningPlayer = (Player) (tCertificateHolder.getPortfolioHolder ());
				if (needToMakeOffer (tOwningPlayer, trainCompany)) {
					tPurchaseOffer = makePurchaseOffer (tOwningPlayer, certificate, Train.NO_TRAIN);
					setVisible (false);

					tCorporationFrame = trainCompany.getCorporationFrame ();
					tCorporationFrame.waitForResponse ();
					
					// Once a Response is received, examine for Accept or Reject of the Purchase Offer
					// If Accept, perform the Buy Private
					if (tPurchaseOffer.wasAccepted ()) {
						buyPrivateCompany (tOwningPlayer);	
					} else {
						// TODO: Notify with Dialog the Offer was Rejected
						System.out.println ("Purchase Offer for Private was Rejected");
					}
				} else {
					buyPrivateCompany (tOwningPlayer);
				}
			}
		}
	}

	private void buyPrivateCompany (Player aOwningPlayer) {
		int tCashValue;
		Portfolio tCompanyPortfolio;
		Portfolio tPlayerPortfolio;
		BuyStockAction tBuyStockAction;
		CorporationFrame tCorporationFrame;
		String tOperatingRoundID;
		
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tBuyStockAction = new BuyStockAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, trainCompany);
		tCashValue = getPrice ();
		trainCompany.transferCashTo (aOwningPlayer, tCashValue);
		tBuyStockAction.addCashTransferEffect (trainCompany, aOwningPlayer, tCashValue);
		tCompanyPortfolio = trainCompany.getPortfolio ();
		tPlayerPortfolio = aOwningPlayer.getPortfolio ();
		doFinalShareBuySteps (tCompanyPortfolio, tPlayerPortfolio, certificate, tBuyStockAction);
		trainCompany.addAction (tBuyStockAction);
		tCorporationFrame = trainCompany.getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}

	private void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, Certificate aCertificate,
										BuyStockAction aBuyStockAction) {
		PrivateCompany tPrivateCompany;
		ShareCompany tShareCompany;
		
		tPrivateCompany = (PrivateCompany) aCertificate.getCorporation ();
		tPrivateCompany.removeBenefitButtons ();
		if (trainCompany.isAShareCompany ()) {
			tShareCompany = (ShareCompany) trainCompany;
			tShareCompany.doFinalShareBuySteps (aToPortfolio, aFromPortfolio, certificate, aBuyStockAction);
		}
	}
}
