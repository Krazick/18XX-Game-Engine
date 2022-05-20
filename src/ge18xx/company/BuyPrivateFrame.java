package ge18xx.company;

import ge18xx.bank.Bank;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.BuyStockAction;
import ge18xx.round.action.PurchaseOfferAction;
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
		if (aOwner.isPlayer ()) {
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

	protected void updateSellerInfo () {
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

	public void updateInfo () {
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
		updateBuyItemPanel (PurchaseOffer.PRIVATE_TYPE, tDescription, tLowPrice, tHighPrice);
		updateBuyerInfo ();
		updateSellerInfo ();
		setBuyButtonText (currentOwner);
		
		setFrameLocation ();
	}


	private void buyPrivateCertificate () {
		CertificateHolderI tCertificateHolder;
		Player tOwningPlayer;
		CorporationFrame tCorporationFrame;

		if (certificate != Certificate.NO_CERTIFICATE) {
			tCertificateHolder = certificate.getOwner ();
			if (tCertificateHolder.isPlayer ()) {
				tOwningPlayer = (Player) (tCertificateHolder.getPortfolioHolder ());
				if (needToMakeOffer (tOwningPlayer, trainCompany)) {
					if (makePurchaseOffer (tOwningPlayer)) {
						tCorporationFrame = trainCompany.getCorporationFrame ();
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
		String tOperatingRoundID;
		
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tBuyStockAction = new BuyStockAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, trainCompany);
		tCashValue = getPrice ();
		trainCompany.transferCashTo (aOwningPlayer, tCashValue);
		tBuyStockAction.addCashTransferEffect (trainCompany, aOwningPlayer, tCashValue);
		tCompanyPortfolio = trainCompany.getPortfolio ();
		tPlayerPortfolio = aOwningPlayer.getPortfolio ();
		doFinalShareBuySteps (tCompanyPortfolio, tPlayerPortfolio, certificate, tBuyStockAction);
		tBuyStockAction.addBoughtShareEffect (trainCompany);
		trainCompany.addAction (tBuyStockAction);
		tCorporationFrame = trainCompany.getCorporationFrame ();
		tCorporationFrame.updateInfo ();
	}

	private boolean makePurchaseOffer (Player aOwningPlayer) {
		PurchaseOfferAction tPurchaseOfferAction;
		PurchaseOffer tPurchaseOffer;
		ActorI.ActionStates tOldState, tNewState;
		boolean tOfferMade = true;
		PrivateCompany tPrivateCompany;
		String tOperatingRoundID;
		
		tPrivateCompany = (PrivateCompany) certificate.getCorporation ();
		tPurchaseOffer = new PurchaseOffer (certificate.getCompanyName (), certificate.getCorpType (), Train.NO_TRAIN,
				tPrivateCompany, tPrivateCompany.getAbbrev (), aOwningPlayer.getName (), getPrice (), trainCompany.getStatus ());

		tOldState = trainCompany.getStatus ();
		trainCompany.setPurchaseOffer (tPurchaseOffer);
		
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tPurchaseOfferAction = new PurchaseOfferAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID,
				trainCompany);
		tPurchaseOfferAction.addPurchaseOfferEffect (trainCompany, aOwningPlayer, getPrice (),
				PurchaseOffer.PRIVATE_TYPE, certificate.getCompanyAbbrev ());

		trainCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		tNewState = trainCompany.getStatus ();
		tPurchaseOfferAction.addChangeCorporationStatusEffect (trainCompany, tOldState, tNewState);
		trainCompany.addAction (tPurchaseOfferAction);

		return tOfferMade;
	}

	public void doFinalShareBuySteps (Portfolio aToPortfolio, Portfolio aFromPortfolio, Certificate aCertificate,
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
