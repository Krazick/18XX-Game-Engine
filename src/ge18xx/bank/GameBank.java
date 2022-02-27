package ge18xx.bank;

import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.LoadedCertificate;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.player.CashHolderI;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.player.PortfolioHolderI;
import ge18xx.player.PortfolioHolderLoaderI;
import ge18xx.round.action.ActorI;
import ge18xx.train.Train;
import ge18xx.train.TrainHolderI;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class GameBank implements TrainHolderI, PortfolioHolderLoaderI {
	protected GameManager gameManager;
	protected String name;
	protected Portfolio portfolio;
	protected TrainPortfolio trainPortfolio;

	public GameBank (String aName, GameManager aGameManager) {
		name = aName;
		portfolio = new Portfolio (this);
		trainPortfolio = new TrainPortfolio ();
		gameManager = aGameManager;
		Bank tBank = aGameManager.getBank ();
		trainPortfolio.setPortfolioHolder (tBank);
	}

	@Override
	public void addCertificate (Certificate aCertificate) {
		portfolio.addCertificate (aCertificate);
	}

	@Override
	public void addTrain (Train aTrain) {
		trainPortfolio.addTrain (aTrain);
	}
	
	public void clearSelections () {
		portfolio.clearSelections ();
		trainPortfolio.clearSelections ();
	}
	
	public int getCertificatePercentageFor (Corporation aCorporation) {
		int tCertificatePercentage;
		
		tCertificatePercentage = portfolio.getCertificatePercentageFor (aCorporation);
		
		return tCertificatePercentage;
	}
	
	public int getCertificateCountFor (Corporation aCorporation) {
		int tCertificateCount;
		
		tCertificateCount = portfolio.getCertificateCountFor (aCorporation);
		
		return tCertificateCount;
	}

	public Certificate getCertificateFromCorp (Corporation aCorporation) {
		Certificate tCertificate;
		
		tCertificate = portfolio.getCertificateFor (aCorporation);
		
		return tCertificate;
	}

	public Certificate getCertificateToBidOn () {
		Certificate tCertificateToBidOn;
		
		tCertificateToBidOn = portfolio.getCertificateToBidOn ();
		
		return tCertificateToBidOn;
	}
	
	public Certificate getCertificateToBuy () {
		Certificate tCertificateToBuy;
		
		tCertificateToBuy = portfolio.getCertificateToBuy ();
		
		return tCertificateToBuy;
	}

	public Train getCheapestTrain () {
		return trainPortfolio.getCheapestTrain ();
	}
	
	@Override
	public PortfolioHolderLoaderI getCurrentHolder (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = portfolio.getCurrentHolder (aLoadedCertificate);
		
		return tCurrentHolder;
	}
	
	public PortfolioHolderLoaderI getCurrentHolderGM (LoadedCertificate aLoadedCertificate) {
		PortfolioHolderLoaderI tCurrentHolder;
		
		tCurrentHolder = gameManager.getCurrentHolder (aLoadedCertificate);
		
		return tCurrentHolder;
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public Portfolio getPortfolio () {
		return portfolio;
	}
	
	@Override
	public String getStateName () {
		return ActorI.ActionStates.Fixed.toString ();
	}
	
	public JPanel buildTrainPortfolioInfoJPanel (ItemListener aItemListener, Corporation aCorporation, 
			GameManager aGameManager, boolean aCompact, boolean aEnableAction, String aDisableReason) {
		JPanel tTrainJPanel;
		JPanel tTrainPortfolioJPanel;
		BoxLayout tLayout;
		
		tTrainJPanel = new JPanel ();
		tTrainJPanel.setBorder (BorderFactory.createTitledBorder (name));
		tLayout = new BoxLayout (tTrainJPanel, BoxLayout.X_AXIS);
		tTrainJPanel.setLayout (tLayout);
		tTrainPortfolioJPanel = trainPortfolio.buildPortfolioJPanel (aItemListener, aCorporation, 
				aGameManager, TrainCompany.BUY_LABEL, aCompact, aEnableAction, aDisableReason);
		tTrainJPanel.add (Box.createVerticalGlue ());
		tTrainJPanel.add (tTrainPortfolioJPanel);
		tTrainJPanel.add (Box.createVerticalGlue ());

		return tTrainJPanel;
	}
	
	@Override
	public Train getSelectedTrain () {
		return trainPortfolio.getSelectedTrain ();
	}
	
	public int getSelectedTrainCount () {
		return trainPortfolio.getSelectedCount ();
	}
	
	public boolean isSelectedTrainItem (Object aItem) {
		return trainPortfolio.isSelectedItem (aItem);
	}
	
	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, 
			GameManager aGameManager) {
		return buildPortfolioInfoJPanel (aItemListener, aPlayer, aGameManager, 
				Player.BUY_AT_PAR_LABEL);
	}
	
	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, 
			GameManager aGameManager, String aCheckboxLabel) {
		JPanel tPortfolioJPanel;
		JPanel tBankJPanel;
		
		tBankJPanel = new JPanel ();
		tBankJPanel.setLayout (new BoxLayout (tBankJPanel, BoxLayout.X_AXIS));
		tBankJPanel.setBorder (BorderFactory.createTitledBorder (name));
		tPortfolioJPanel = portfolio.buildShareCertificateJPanel (Corporation.SHARE_COMPANY, 
				aCheckboxLabel, aItemListener, aPlayer, aGameManager);
		tBankJPanel.add (Box.createVerticalGlue ());
		tBankJPanel.add (tPortfolioJPanel);
		tBankJPanel.add (Box.createVerticalGlue ());
	
		return tBankJPanel;
	}

	public XMLElement getPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = portfolio.getElements (aXMLDocument);
		
		return tXMLElement;
	}

	public XMLElement getTrainPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		
		tXMLElement = trainPortfolio.getElements (aXMLDocument);
		
		return tXMLElement;
	}
	
	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return this;
	}

	public void loadPortfolio (XMLNode aPortfolioNode) {
		portfolio.loadPortfolio (aPortfolioNode);
	}
	
	public void loadTrainPortfolio (XMLNode aTrainPortfolioNode) {
		Bank tBank;
		
		tBank = gameManager.getBank ();
		trainPortfolio.loadTrainPortfolioFromBank (aTrainPortfolioNode, tBank);
	}
	
	@Override
	public int getLocalSelectedTrainCount () {
		return trainPortfolio.getSelectedCount ();
	}

	@Override
	public Train getTrain (String aName) {
		return trainPortfolio.getTrain (aName);
	}

	@Override
	public int getTrainQuantity (String aName) {
		return trainPortfolio.getTrainQuantity (aName);
	}

	@Override
	public TrainPortfolio getTrainPortfolio () {
		return trainPortfolio;
	}
	
	@Override
	public String getTrainNameAndQty (String aStatus) {
		return trainPortfolio.getTrainNameAndQty (aStatus);
	}

	@Override
	public boolean hasTrainNamed (String aName) {
		return trainPortfolio.hasTrainNamed (aName);
	}
	
	@Override
	public boolean isAPrivateCompany () {
		return false;
	}

	public void printBankInfo () {
		System.out.print (name);
		System.out.println ("Owned Trains [" + getTrainNameAndQty (TrainPortfolio.ALL_TRAINS) + "]");
		System.out.println ("Available Trains [" + getTrainNameAndQty (TrainPortfolio.AVAILABLE_TRAINS) + "]");
		System.out.println ("Future Trains [" + getTrainNameAndQty (TrainPortfolio.FUTURE_TRAINS) + "]");
		System.out.print (name);
		portfolio.printCompactPortfolioInfo ();
	}

	@Override
	public boolean removeSelectedTrain () {
		return trainPortfolio.removeSelectedTrain ();
	}
	
	@Override
	public boolean removeTrain (String aName) {
		return trainPortfolio.removeTrain (aName);
	}

	@Override
	public void replacePortfolioInfo (JPanel aPortfolioInfoJPanel) {
	}

	@Override
	public boolean isBank () {
		return false;
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
	public boolean isPlayer () {
		return false;
	}

	@Override
	public CashHolderI getCashHolder () {
		return gameManager.getBank ();
	}

	@Override
	public String getAbbrev () {
		return "GameBank";
	}

	@Override
	public Bank getBank () {
		return gameManager.getBank ();
	}
	
	public Train [] getAvailableTrains () {
		return trainPortfolio.getAvailableTrains ();
	}

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the GameBank Class
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
	
	public String getTrainSummary () {
		return trainPortfolio.getTrainSummary ();
	}
	
	public boolean hasAnyTrains () {
		boolean tHasAnyTrains = false;
		
		if (trainPortfolio.getTrainCount () > 0) {
			tHasAnyTrains = true;
		}
		
		return tHasAnyTrains;
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

	@Override
	public int getTrainLimit () {
		return 0;
	}
}