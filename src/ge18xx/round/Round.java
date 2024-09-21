package ge18xx.round;

import java.util.List;

import ge18xx.bank.Bank;
import ge18xx.bank.BankPool;
import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseManager;
import ge18xx.player.Portfolio;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActionManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public abstract class Round implements ActorI {
	public final static String NAME = "Round";
	public final static ElementName EN_ROUND = new ElementName ("Round");
	public final static AttributeName AN_ROUND_PART1 = new AttributeName ("idPart1");
	public final static AttributeName AN_ROUND_PART2 = new AttributeName ("idPart2");
	public final static Round NO_ROUND = null;
	public final static String NO_ID_STRING = "0.1";
	public static final int START_ID1 = 0;
	public static final int START_ID2 = 1;
	public static final int NO_ID = 0;
	int idPart1;
	int idPart2;
	RoundManager roundManager;
	String name;

	public Round (RoundManager aRoundManager) {
		setRoundManager (aRoundManager);
		setID (START_ID1, START_ID2);
	}

	public void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}

	public void setID (int aIDPart1, int aIDPart2) {
		setIDPart1 (aIDPart1);
		setIDPart2 (aIDPart2);
	}

	public void setIDPart1 (int aIDPart1) {
		idPart1 = aIDPart1;		
	}
	
	public void setIDPart2 (int aIDPart2) {
		idPart2 = aIDPart2;
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public void loadRound (XMLNode aRoundNode) {
		int tIDPart1;
		int tIDPart2;
		
		tIDPart1 = aRoundNode.getThisIntAttribute (AN_ROUND_PART1);
		tIDPart2 = aRoundNode.getThisIntAttribute (AN_ROUND_PART2);
		
		setID (tIDPart1, tIDPart2);
	}

	public void setID (String aID) {
		String tIDs[];
		int tID1;
		int tID2;

		tIDs = aID.split ("\\.");
		tID1 = Integer.parseInt (tIDs [0]);
		if (tIDs.length == 2) {
			tID2 = Integer.parseInt (tIDs [1]);
		} else {
			tID2 = START_ID2;
		}
		setID (tID1, tID2);
	}

	public void setRoundAttributes (XMLElement aXMLElement) {
		aXMLElement.setAttribute (AN_ROUND_PART1, idPart1);
		aXMLElement.setAttribute (AN_ROUND_PART2, idPart2);
	}

	// Methods to ask this (Round) Class to handle

	@Override
	public String getName () {
		return name;
	}

	@Override
	public String getAbbrev () {
		return name;
	}

	public boolean isActor (String aActorName) {
		return name.equals (aActorName);
	}

	// Methods that ask RoundManager to handle
	
	public void addAction (Action aAction) {
		roundManager.addAction (aAction);
	}

	public void clearAllAuctionStates () {
		roundManager.clearAllAuctionStates ();
	}

	public void printBriefActionReport () {
		roundManager.printBriefActionReport ();
	}

	public void updateRoundFrame () {
		roundManager.updateRoundFrame ();
	}

	public void updateAllFrames () {
		roundManager.updateAllFrames ();
	}

	public void resetOperatingRound () {
		roundManager.resetOperatingRound (idPart1, idPart2);
	}

	public void resumeStockRound () {
		roundManager.resumeStockRound (idPart1);
	}
	
	public void startStockRound () {
		roundManager.startStockRound ();
	}

	public void startAuctionRound (boolean aCreateNewAuctionAction) {
		roundManager.startAuctionRound (aCreateNewAuctionAction);
	}

	// Methods that ask RoundManager to handle and RETURN something

	public ActionManager getActionManager () {
		return roundManager.getActionManager ();
	}

	public Bank getBank () {
		return roundManager.getBank ();
	}

	public BankPool getBankPool () {
		return roundManager.getBankPool ();
	}

	public Portfolio getBankPoolPortfolio () {
		return roundManager.getBankPoolPortfolio ();
	}

	public Certificate getCertificateToBuy () {
		return roundManager.getCertificateToBuy ();
	}

	public List<Certificate> getCertificatesToBuy () {
		List<Certificate> tCertificatesToBuy;

		tCertificatesToBuy = roundManager.getCertificatesToBuy ();

		return tCertificatesToBuy;
	}

	public Certificate getCertificateToBidOn () {
		return roundManager.getCertificateToBidOn ();
	}

	public GameManager getGameManager () {
		return roundManager.getGameManager ();
	}

	public Action getLastAction () {
		return roundManager.getLastAction ();
	}

	public PhaseManager getPhaseManager () {
		return roundManager.getPhaseManager ();
	}

	public boolean hasActionsToUndo () {
		return roundManager.hasActionsToUndo ();
	}

	public boolean startOperatingRound () {
		Round tCurrentRound;
		
		tCurrentRound = roundManager.getCurrentRound ();
		roundManager.startOperatingRound (tCurrentRound);

		return true;
	}

	public String getClientUserName () {
		return roundManager.getClientUserName ();
	}

	public boolean isNetworkGame () {
		return roundManager.isNetworkGame ();
	}

	public boolean undoLastAction () {
		return roundManager.undoLastAction ();
	}

	public boolean wasLastActionStartAuction () {
		return roundManager.wasLastActionStartAuction ();
	}

	// Methods to ask this (Round) Class to handle

	public RoundManager getRoundManager () {
		return roundManager;
	}

	public int getIDPart1 () {
		return idPart1;
	}

	public int getIDPart2 () {
		return idPart2;
	}

	public String getID () {
		String tID;

		tID = idPart1 + "." + idPart2;

		return tID;
	}

	public boolean isFirstRound () {
		return (idPart1 == 1);
	}

	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.NoRound;
	}

	@Override
	public String getStateName () {
		return getRoundType ().toString ();
	}

	public boolean roundIsDone () {
		return false;
	}

	@Override
	public void resetPrimaryActionState (ActorI.ActionStates aPreviousState) {
		if (aPreviousState == ActorI.ActionStates.StockRound) {
			resumeStockRound ();
		}
		if (aPreviousState == ActorI.ActionStates.OperatingRound) {
			resetOperatingRound ();
		}
	}
}
