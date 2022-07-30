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

		tBank = getBank ();
		trainPortfolio.loadTrainPortfolioFromBank (aTrainPortfolioNode, tBank);
	}

	public String getTrainSummary () {
		return trainPortfolio.getTrainSummary ();
	}

	public boolean hasAnyTrains () {
		boolean tHasAnyTrains;

		if (trainPortfolio.getTrainCount () > 0) {
			tHasAnyTrains = true;
		} else {
			tHasAnyTrains = false;
		}

		return tHasAnyTrains;
	}

	public XMLElement getTrainPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = trainPortfolio.getElements (aXMLDocument);

		return tXMLElement;
	}

	public JPanel buildTrainPortfolioInfoJPanel (ItemListener aItemListener, Corporation aCorporation,
			boolean aCompact, boolean aEnableAction, String aDisableReason) {
		JPanel tTrainJPanel;
		JPanel tTrainPortfolioJPanel;

		tTrainPortfolioJPanel = trainPortfolio.buildPortfolioJPanel (aItemListener, aCorporation, gameManager,
				TrainCompany.BUY_LABEL, aCompact, aEnableAction, aDisableReason);
		tTrainJPanel = buildPortfolioPanel (tTrainPortfolioJPanel);

		return tTrainJPanel;
	}

	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer) {
		return buildPortfolioInfoJPanel (aItemListener, aPlayer, Player.BUY_AT_PAR_LABEL);
	}

	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, String aCheckboxLabel) {
		JPanel tPortfolioJPanel;
		JPanel tBankJPanel;

		tPortfolioJPanel = portfolio.buildShareCertificateJPanel (Corporation.SHARE_COMPANY, aCheckboxLabel,
				aItemListener, aPlayer, gameManager);
		tBankJPanel = buildPortfolioPanel (tPortfolioJPanel);

		return tBankJPanel;
	}

	private JPanel buildPortfolioPanel (JPanel aPortfolioJPanel) {
		JPanel tPortfolioPanel;
		
		tPortfolioPanel = new JPanel ();
		tPortfolioPanel.setLayout (new BoxLayout (tPortfolioPanel, BoxLayout.X_AXIS));
		tPortfolioPanel.setBorder (BorderFactory.createTitledBorder (name));
		tPortfolioPanel.add (Box.createVerticalGlue ());
		tPortfolioPanel.add (aPortfolioJPanel);
		tPortfolioPanel.add (Box.createVerticalGlue ());
		
		return tPortfolioPanel;
	}

	@Override
	public PortfolioHolderI getPortfolioHolder () {
		return this;
	}

	public XMLElement getPortfolioElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = portfolio.getElements (aXMLDocument);

		return tXMLElement;
	}

	public void loadPortfolio (XMLNode aPortfolioNode) {
		portfolio.loadPortfolio (aPortfolioNode);
	}

	public void printBankInfo () {
		trainPortfolio.printNameAndQty (name);
		portfolio.printCompactPortfolioInfo ();
	}

	@Override
	public String getAbbrev () {
		return "GameBank";
	}

	@Override
	public CashHolderI getCashHolder () {
		return getBank ();
	}

	@Override
	public Bank getBank () {
		return gameManager.getBank ();
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