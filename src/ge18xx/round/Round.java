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
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public abstract class Round implements ActorI {
	public final static String NAME = "Round";
	public final static ElementName EN_ROUND = new ElementName ("Round");
	public final static AttributeName AN_ROUND_PART1 = new AttributeName ("idPart1");
	public final static AttributeName AN_ROUND_PART2 = new AttributeName ("idPart2");
	public final static Round NO_ROUND = null;
	static final int NO_ID = 0;
	int idPart1;
	int idPart2;
	RoundManager roundManager;

	public Round (RoundManager aRoundManager) {
		setID (NO_ID, NO_ID);
		setRoundManager (aRoundManager);
	}

	public void addAction (Action aAction) {
		roundManager.addAction (aAction);
	}

	public void clearAllAuctionStates () {
		roundManager.clearAllAuctionStates ();
	}

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

	public Action getLastAction () {
		return roundManager.getLastAction ();
	}

	public PhaseManager getPhaseManager () {
		return roundManager.getPhaseManager ();
	}

	public RoundManager getRoundManager () {
		return roundManager;
	}

	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.NoRound;
	}

	@Override
	public String getStateName () {
		return getRoundType ().toString ();
	}

	public String getType () {
		return "Round";
	}

	public boolean hasActionsToUndo () {
		return roundManager.hasActionsToUndo ();
	}

	public void loadRound (XMLNode aRoundNode) {
		idPart1 = aRoundNode.getThisIntAttribute (AN_ROUND_PART1);
		idPart2 = aRoundNode.getThisIntAttribute (AN_ROUND_PART2);
	}

	public boolean roundIsDone () {
		return false;
	}

	public void printBriefActionReport () {
		roundManager.printBriefActionReport ();
	}

	public void setID (String aID) {
		String tIDs[];
		int tID1, tID2;

		tIDs = aID.split ("\\.");
		tID1 = Integer.parseInt (tIDs [0]);
		if (tIDs.length == 2) {
			tID2 = Integer.parseInt (tIDs [1]);
		} else {
			tID2 = 1;
		}
		setID (tID1, tID2);
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

	public void setPrimaryActionState (ActorI.ActionStates aPreviousState) {
		if (aPreviousState == ActorI.ActionStates.StockRound) {
			startStockRound ();
		}
		if (aPreviousState == ActorI.ActionStates.OperatingRound) {
			startOperatingRound ();
		}
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

	public void setRoundAttributes (XMLElement aXMLElement) {
		aXMLElement.setAttribute (AN_ROUND_PART1, idPart1);
		aXMLElement.setAttribute (AN_ROUND_PART2, idPart2);
	}

	public void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}

	public void resetOperatingRound () {
		roundManager.resetOperatingRound (idPart1, idPart2);
	}

	public void resumeStockRound () {
		roundManager.resumeStockRound (idPart1);
	}

	public void startAuctionRound (boolean aCreateNewAuctionAction) {
		roundManager.startAuctionRound (aCreateNewAuctionAction);
	}

	public boolean startOperatingRound () {
		roundManager.startOperatingRound ();

		return true;
	}

	public String getClientUserName () {
		return roundManager.getClientUserName ();
	}

	public boolean isNetworkGame () {
		return roundManager.isNetworkGame ();
	}

	public void startStockRound () {
		roundManager.startStockRound ();
	}

	public boolean undoLastAction () {
		return roundManager.undoLastAction ();
	}

	public void updateRoundFrame () {
		roundManager.updateRoundFrame ();
	}

	public void updateAllFrames () {
		roundManager.updateAllFrames ();
	}

	@Override
	public String getName () {
		return NAME;
	}

	public boolean wasLastActionStartAuction () {
		return roundManager.wasLastActionStartAuction ();
	}

	@Override
	public String getAbbrev () {
		return getName ();
	}

	public boolean isActor (String aActorName) {
		return getName ().equals (aActorName);
	}
}
