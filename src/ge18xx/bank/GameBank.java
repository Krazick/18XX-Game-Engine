package ge18xx.bank;

import java.awt.event.ItemListener;
import java.util.List;

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
		Portfolio tPortfolio;
		TrainPortfolio tTrainPortfolio;
		
		gameManager = aGameManager;
		name = aName;
		tPortfolio = new Portfolio (this);
		tTrainPortfolio = new TrainPortfolio ();
		setPortfolio (tPortfolio);
		setTrainPortfolio (tTrainPortfolio);
	}

	public void setPortfolio (Portfolio aPortfolio) {
		portfolio = aPortfolio;
	}
	
	public void setTrainPortfolio (TrainPortfolio aTrainPortfolio) {
		trainPortfolio = aTrainPortfolio;
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

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;

		tCertificatesToBuy = portfolio.getCertificatesToBuy ();

		return tCertificatesToBuy;
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

	public Train getCheapestTrain () {
		return trainPortfolio.getCheapestTrain ();
	}

	@Override
	public Train getSelectedTrain () {
		return trainPortfolio.getSelectedTrain ();
	}

	public int getSelectedTrainCount () {
		return trainPortfolio.getSelectedCount ();
	}

	@Override
	public int getLocalSelectedTrainCount () {
		return getSelectedTrainCount ();
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
	public boolean removeSelectedTrain () {
		return trainPortfolio.removeSelectedTrain ();
	}

	@Override
	public boolean removeTrain (String aName) {
		return trainPortfolio.removeTrain (aName);
	}

	public Train [] getAvailableTrains () {
		return trainPortfolio.getAvailableTrains ();
	}

	public void loadTrainPortfolio (XMLNode aTrainPortfolioNode) {
		Bank tBank;

		tBank = gameManager.getBank ();
		trainPortfolio.loadTrainPortfolioFromBank (aTrainPortfolioNode, tBank);
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

	public XMLElement getTrainPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = trainPortfolio.getElements (aXMLDocument);

		return tXMLElement;
	}

	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, GameManager aGameManager) {
		return buildPortfolioInfoJPanel (aItemListener, aPlayer, aGameManager, Player.BUY_AT_PAR_LABEL);
	}

	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, GameManager aGameManager,
			String aCheckboxLabel) {
		JPanel tPortfolioJPanel;
		JPanel tBankJPanel;

		tBankJPanel = new JPanel ();
		tBankJPanel.setLayout (new BoxLayout (tBankJPanel, BoxLayout.X_AXIS));
		tBankJPanel.setBorder (BorderFactory.createTitledBorder (name));
		tPortfolioJPanel = portfolio.buildShareCertificateJPanel (Corporation.SHARE_COMPANY, aCheckboxLabel,
				aItemListener, aPlayer, aGameManager);
		tBankJPanel.add (Box.createVerticalGlue ());
		tBankJPanel.add (tPortfolioJPanel);
		tBankJPanel.add (Box.createVerticalGlue ());

		return tBankJPanel;
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
		tTrainPortfolioJPanel = trainPortfolio.buildPortfolioJPanel (aItemListener, aCorporation, aGameManager,
				TrainCompany.BUY_LABEL, aCompact, aEnableAction, aDisableReason);
		tTrainJPanel.add (Box.createVerticalGlue ());
		tTrainJPanel.add (tTrainPortfolioJPanel);
		tTrainJPanel.add (Box.createVerticalGlue ());

		return tTrainJPanel;
	}

	public XMLElement getPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = portfolio.getElements (aXMLDocument);

		return tXMLElement;
	}

	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return this;
	}

	public void loadPortfolio (XMLNode aPortfolioNode) {
		portfolio.loadPortfolio (aPortfolioNode);
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
	public void replacePortfolioInfo (JPanel aPortfolioInfoJPanel) {
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

	@Override
	public void resetPrimaryActionState (ActionStates aPrimaryActionState) {
		// Nothing to do for the GameBank Class
	}

	@Override
	public boolean isABank () {
		return true;
	}

	@Override
	public int getTrainLimit () {
		return 0;
	}
}