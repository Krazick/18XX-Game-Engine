package ge18xx.company.formation;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.MapToken;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TokenCompany;
import ge18xx.game.GameManager;
import ge18xx.map.HexMap;
import ge18xx.market.Market;
import ge18xx.market.MarketCell;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.round.Round;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ReplaceTokenAction;
import ge18xx.round.action.StockValueCalculationAction;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.toplevel.MarketFrame;
import geUtilities.GUI;
import swingTweaks.KButton;

public class Nationalization extends PlayerFormationPanel {
	private static final long serialVersionUID = 1L;
	public static final String UPGRADE_TO_PRUSSIAN = "Upgrade to Prussian";

	public Nationalization (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aFormCompany, aPlayer, aActingPresident);
	}
	
	@Override
	public JPanel buildCompanyJPanel (PrivateCompany aPrivateCompany, boolean aActingPlayer) {
		JPanel tPrivateCompanyJPanel;
		
		tPrivateCompanyJPanel = new JPanel ();
		tPrivateCompanyJPanel.setLayout (new BoxLayout (tPrivateCompanyJPanel, BoxLayout.X_AXIS));

		tPrivateCompanyJPanel = super.buildCompanyJPanel (aPrivateCompany, aActingPlayer, tPrivateCompanyJPanel);
		
		buildSpecialButtons (aPrivateCompany, tPrivateCompanyJPanel, aActingPlayer);

		return tPrivateCompanyJPanel;
	}

	@Override
	public JPanel buildCompanyJPanel (MinorCompany aMinorCompany, boolean aActingPlayer) {
		JPanel tMinorCompanyJPanel;
		
		tMinorCompanyJPanel = new JPanel ();
		tMinorCompanyJPanel.setLayout (new BoxLayout (tMinorCompanyJPanel, BoxLayout.X_AXIS));

		tMinorCompanyJPanel = super.buildCompanyJPanel (aMinorCompany, aActingPlayer, tMinorCompanyJPanel);
		
		buildSpecialButtons (aMinorCompany, tMinorCompanyJPanel, aActingPlayer);

		return tMinorCompanyJPanel;
	}

	/* For the 1835 Nationalization, Prussian Formation, the Share Companies never have any impact, 
	 * Don't provide a panel for them.
	 */
	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = null;

		return tShareCompanyJPanel;
	}

	public String canUpgradeToPrussian (Corporation aCorporation) {
		String tToolTip;
		FormPrussian tFormPrussian;
		
		tFormPrussian = (FormPrussian) formCompany;
		
		tToolTip = GUI.EMPTY_STRING;
		
		if (prussianIsForming (tFormPrussian, aCorporation)) {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " is Forming";
		} else {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " has NOT formed yet";
		}
		
		return tToolTip;
	}

	private boolean prussianIsForming (FormPrussian aFormPrussian, Corporation aCorporation) {
		boolean tPrussianIsForming;
		
		tPrussianIsForming = aFormPrussian.formingShareCompany.isFormed ();
		if (aCorporation.canFormUpgrade ()) {
			tPrussianIsForming = true;
		}
		
		return tPrussianIsForming;
	}

	public void buildSpecialButtons (PrivateCompany aPrivateCompany, JPanel aCompanyJPanel, boolean aActingPlayer) {
		KButton tUpgradeToPrussian;
		String tToolTip;
		FormPrussian tFormPrussian;
		Corporation tCorporation;
		
		tFormPrussian = (FormPrussian) formCompany;
		if (aActingPlayer) {
			tToolTip = canUpgradeToPrussian (aPrivateCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}

		tUpgradeToPrussian = tFormPrussian.buildSpecialButton (UPGRADE_TO_PRUSSIAN, UPGRADE_TO_PRUSSIAN, 
				tToolTip, this);
		if (prussianIsForming (tFormPrussian, aPrivateCompany)) {
			if (aActingPlayer) {
				tUpgradeToPrussian.setEnabled (true);
			} else {
				tUpgradeToPrussian.setEnabled (false);
			}
		}
		tCorporation = getExchangeCorporation (aPrivateCompany);
		if (tCorporation != Corporation.NO_CORPORATION) {
			aCompanyJPanel.add (tUpgradeToPrussian);
			aCompanyJPanel.add (Box.createHorizontalStrut (10));
			aPrivateCompany.addSpecialButton (tUpgradeToPrussian);
		}
	}
	
	public void buildSpecialButtons (MinorCompany aMinorCompany, JPanel aCompanyJPanel, boolean aActingPlayer) {
		KButton tUpgradeToPrussian;
		String tToolTip;
		String tCorpAbbrev;
		String tFullCommand;
		FormPrussian tFormPrussian;
		Corporation tCorporation;
		
		tFormPrussian = (FormPrussian) formCompany;
		if (aActingPlayer) {
			tToolTip = canUpgradeToPrussian (aMinorCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}
		tCorporation = getExchangeCorporation (aMinorCompany);
		tCorpAbbrev = tCorporation.getAbbrev ();
		tFullCommand = UPGRADE_TO_PRUSSIAN + " " + tCorpAbbrev;
		tUpgradeToPrussian = tFormPrussian.buildSpecialButton (UPGRADE_TO_PRUSSIAN, tFullCommand, 
				tToolTip, this);
		if (prussianIsForming (tFormPrussian, aMinorCompany)) {
			if (aActingPlayer) {
				tUpgradeToPrussian.setEnabled (true);
			} else {
				tUpgradeToPrussian.setEnabled (false);
			}
		} else {
			tUpgradeToPrussian.setEnabled (false);
		}
		if (tCorporation != Corporation.NO_CORPORATION) {
			aCompanyJPanel.add (tUpgradeToPrussian);
			aCompanyJPanel.add (Box.createHorizontalStrut (10));
			aMinorCompany.addSpecialButton (tUpgradeToPrussian);
		}
	}
	
	@Override
	public void actionPerformed (ActionEvent aEvent) {
		super.actionPerformed (aEvent);
		
		String tActionCommand;
		MinorCompany tMinorCompany;
		PrivateCompany tPrivateCompany;
		KButton tActivatedButton;
		
		tActionCommand = aEvent.getActionCommand ();
		tActivatedButton = getActivatedButton (aEvent);
		if (tActivatedButton != GUI.NO_BUTTON) {
			tMinorCompany = findMinorCompany (tActivatedButton);
			if (tMinorCompany != MinorCompany.NO_MINOR_COMPANY) {
				if (tActionCommand.startsWith (UPGRADE_TO_PRUSSIAN)) {
					handleUpgradeToPrussian (tMinorCompany);
				}
			} else {
				tPrivateCompany = findPrivateCompany (tActivatedButton);
				if (tPrivateCompany != PrivateCompany.NO_PRIVATE_COMPANY) {
					if (tActionCommand.startsWith (UPGRADE_TO_PRUSSIAN)) {
						handleUpgradeToPrussian (tPrivateCompany);
					}
				}
			}
		}
		
		checkAndHandleRoundEnds ();
	}
	
	private void handleUpgradeToPrussian (PrivateCompany aPrivateCompany) {
		TransferOwnershipAction tTransferOwnershipAction;
		RoundManager tRoundManager;
		Round tCurrentRound;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		String tFormingAbbrev;
		Portfolio tBankPortfolio;
		int tPercentage;
		Certificate tFormedCertificate;
		FormPrussian tFormPrussian;
		Bank tBank;
		
		System.out.println ("Ready to convert Private Company " + aPrivateCompany.getName ());
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tFormPrussian = (FormPrussian) formCompany;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();
		tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, player);
		
		tFormedCertificate = Certificate.NO_CERTIFICATE;
		tFormingAbbrev = getFormingAbbrev ();
		tPercentage = aPrivateCompany.getExchangePercentage ();

		tFormedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, false, true);
		if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
			transferShare (tBank, Bank.IPO, player, tFormedCertificate, tTransferOwnershipAction);
		} else {
			System.err.println ("No certificate available with All Players Total Exchange Count 1");
		}
		updateCorporationOwnership (tFormedCertificate);
		confirmFormingPresident (tTransferOwnershipAction);
		aPrivateCompany.close (tTransferOwnershipAction);
		gameManager.addAction (tTransferOwnershipAction);
		tFormPrussian.rebuildFormationPanel (tFormPrussian.getCurrentPlayerIndex ());
	}
	
	private void handleUpgradeToPrussian (MinorCompany aMinorCompany) {
		TransferOwnershipAction tTransferOwnershipAction;
		RoundManager tRoundManager;
		Round tCurrentRound;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		String tFormingAbbrev;
		Portfolio tBankPortfolio;
		int tPercentage;
		boolean tFindPresidentShare;
		Certificate tFormedCertificate;
		ReplaceTokenAction tReplaceTokenAction;
		StockValueCalculationAction tStockValueCalculationAction;
		ActorI.ActionStates tMinorCompanyOldStatus;
		ShareCompany tFormingShareCompany;
		Corporation tTriggeringCompany;
		FormPrussian tFormPrussian;
		Bank tBank;
		
		tBank = gameManager.getBank ();
		tBankPortfolio = tBank.getPortfolio ();
		tFormPrussian = (FormPrussian) formCompany;
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();
		tTransferOwnershipAction = new TransferOwnershipAction (tRoundType, tRoundID, player);
		tFormedCertificate = Certificate.NO_CERTIFICATE;
		tFormingAbbrev = getFormingAbbrev ();
		tPercentage = aMinorCompany.getUpgradePercentage ();
		tFormingShareCompany = tFormPrussian.getFormingCompany ();
		tTriggeringCompany = tFormPrussian.getTriggeringCompany ();
		if (tTriggeringCompany.isSameID (aMinorCompany)) {
			tFindPresidentShare = true;
		} else {
			tFindPresidentShare = false;
		}
		tFormedCertificate = tBankPortfolio.getCertificate (tFormingAbbrev, tPercentage, tFindPresidentShare, true);
		if (tFormedCertificate != Certificate.NO_CERTIFICATE) {
			transferShare (tBank, Bank.IPO, player, tFormedCertificate, tTransferOwnershipAction);
		} else {
			System.err.println ("No certificate available with All Players Total Exchange Count 1");
		}
		updateCorporationOwnership (tFormedCertificate);
		confirmFormingPresident (tTransferOwnershipAction);

		transferAllCash (aMinorCompany, tFormingShareCompany, tTransferOwnershipAction);
		transferAllTrains (aMinorCompany, tFormingShareCompany, tTransferOwnershipAction);
		tMinorCompanyOldStatus = aMinorCompany.getActionStatus ();
		aMinorCompany.close (tTransferOwnershipAction);
		gameManager.addAction (tTransferOwnershipAction);
	
		tReplaceTokenAction = prepareAction (aMinorCompany);
		replaceAToken (aMinorCompany, tFormingShareCompany, tReplaceTokenAction);
		tReplaceTokenAction.setChainToPrevious (true);
		gameManager.addAction (tReplaceTokenAction);
		
		if (tFindPresidentShare) {
			tStockValueCalculationAction = new StockValueCalculationAction (tRoundType, tRoundID, player);
			setMarketCell (aMinorCompany, tFormingShareCompany, tStockValueCalculationAction, tMinorCompanyOldStatus);
			tStockValueCalculationAction.setChainToPrevious (true);
			gameManager.addAction (tStockValueCalculationAction);
		}
		tFormPrussian.rebuildFormationPanel (tFormPrussian.getCurrentPlayerIndex ());
	}
	 
	public void setMarketCell (MinorCompany aMinorCompany, ShareCompany aFormingShareCompany, 
					StockValueCalculationAction aStockValueCalculationAction, 
					ActorI.ActionStates aMinorCompanyOldStatus) {
		MarketCell tClosestMarketCell;
		MarketFrame tMarketFrame;
		Market tMarket;
		ActorI.ActionStates tNewFormingCoStatus;
		String tCoordinates;
		int tNewParPrice;

		tNewParPrice = aFormingShareCompany.getParPrice ();
		tMarketFrame = gameManager.getMarketFrame ();
		if (aFormingShareCompany.hasStartCell ()) {
			tMarket = tMarketFrame.getMarket ();
			tClosestMarketCell = aFormingShareCompany.getMarketStartCellAt (tMarket);
		} else {
			tClosestMarketCell = getClosestMarketCell (tNewParPrice);
		}
		tCoordinates = tClosestMarketCell.getCoordinates ();

		tMarketFrame.setParPriceToMarketCell (aFormingShareCompany, tNewParPrice, tClosestMarketCell);
		aStockValueCalculationAction.addSetParValueEffect (aFormingShareCompany, aFormingShareCompany, 
						tNewParPrice, tCoordinates);

		if (aMinorCompanyOldStatus == ActorI.ActionStates.Owned) {
			tNewFormingCoStatus = aMinorCompany.getActionStatus ();
			aFormingShareCompany.resetStatus (tNewFormingCoStatus);
			aStockValueCalculationAction.addChangeCorporationStatusEffect (aFormingShareCompany, 
					aMinorCompanyOldStatus, tNewFormingCoStatus);
		}
	}
	
	public void replaceAToken (TokenCompany aFoldingCompany, TokenCompany aFormingCompany, 
			ReplaceTokenAction aReplaceTokenAction) {
		MapToken tNewMapToken;
		HexMap tHexMap;
		String tHomeMapCellID;
		String tTokenLocation;
		String tCompanyAbbrev;
		int tCorpID;
		
		tHexMap = gameManager.getGameMap ();
		tNewMapToken = aFormingCompany.getLastMapToken ();
		tHomeMapCellID = aFoldingCompany.getHomeMapCellID (1);
		tCompanyAbbrev = aFoldingCompany.getAbbrev ();
		tCorpID = aFoldingCompany.getID ();
		tTokenLocation = tHexMap.getTokenLocation (tHomeMapCellID, tCompanyAbbrev, tCorpID);
		tHexMap.replaceMapToken (tTokenLocation, tNewMapToken, aFoldingCompany, aReplaceTokenAction);
		tHexMap.redrawMap ();
	}

	public ReplaceTokenAction prepareAction (TokenCompany aTokenCompany) {
		ReplaceTokenAction tReplaceTokenAction;
		RoundManager tRoundManager;
		ActorI.ActionStates tRoundType;
		Round tCurrentRound;
		String tRoundID;
		
		tRoundManager = gameManager.getRoundManager ();
		tRoundType = tRoundManager.getCurrentRoundState ();
		tCurrentRound = tRoundManager.getCurrentRound ();
		tRoundID = tCurrentRound.getID ();
		tReplaceTokenAction = new ReplaceTokenAction (tRoundType, tRoundID, aTokenCompany);
		
		return tReplaceTokenAction;
	}
	
	@Override
	public void updateContinueButton () {
		continueButton.setVisible (false);
	}
}
