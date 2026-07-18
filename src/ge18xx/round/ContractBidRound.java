package ge18xx.round;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
//import ge18xx.player.ContractBid;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import ge18xx.toplevel.ContractBidFrame;
import geUtilities.GUI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;

public class ContractBidRound extends Round {
	public static final ContractBidRound NO_CONTRACT_BID_ROUND = null;
	public static final String NAME = "Contract Bid Round";
	GameManager gameManager;
	ContractBidFrame contractBidFrame;

	public ContractBidRound (RoundManager aRoundManager) {
 		super (aRoundManager);
		
		setName (NAME);
		setRoundType ();
// 		setupFrame (aRoundManager);
	}

	public void setupFrame (RoundManager aRoundManager) {
		ContractBidFrame tContractBidFrame;
		String tFrameName;
 		GameInfo tActiveGame;
 		
		gameManager = aRoundManager.getGameManager ();
		tActiveGame = gameManager.getActiveGame ();
		
		tFrameName = tActiveGame.getName () + " " + ContractBidFrame.NAME;
		tContractBidFrame = new ContractBidFrame (tFrameName, gameManager);
		setContractBidFrame (tContractBidFrame);
		gameManager.addNewFrame (contractBidFrame);
		gameManager.setContractBidFrame (contractBidFrame);
	}

	public void setContractBidFrame (ContractBidFrame aContractBidFrame) {
		contractBidFrame = aContractBidFrame;
	}
	
	public ContractBidFrame getContractBidFrame () {
		return contractBidFrame;
	}
	
	public void showContractBidFrame () {
		contractBidFrame.showFrame ();
	}
	
	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.ContractBidRound;
	}
	
	@Override
	public boolean isAContractBidRound () {
		return true;
	}

	@Override
	public void finish () {
		// TODO Auto-generated method stub
	}

	@Override
	public void finish (XMLFrame aXMLFrame) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start () {
		String tGameName;
		String tRoundID;
		GameManager tGameManager;
		RoundFrame tRoundFrame;
		ChangeRoundAction tChangeRoundAction;
		Round tCurrentRound;
		String tOldRoundID;
		String tNewRoundID;
		int tIDPart1;
		
		tGameManager = roundManager.getGameManager ();
		tChangeRoundAction = buildChangeRoundAction ();
		if (! tGameManager.applyingAction ()) {
			tOldRoundID = getID ();
			tIDPart1 = getIDPart1 () + 1;
			setIDPart1 (tIDPart1);
			tNewRoundID = getID ();
		} else {
			tOldRoundID = getID ();
			tNewRoundID = getID ();
		}
		tCurrentRound = roundManager.getCurrentRound ();
		roundManager.changeRound (tCurrentRound, ActorI.ActionStates.ContractBidRound, this, tOldRoundID,
				tNewRoundID, tChangeRoundAction);

		super.start ();
		
		tRoundFrame = roundManager.getRoundFrame ();
		tRoundID = getID ();
		tGameName = tGameManager.getActiveGameName ();
		tRoundFrame.setContractBidRoundInfo (tGameName, tRoundID);
		tRoundFrame.updateAll ();
	}
	
	@Override
	public String getID () {
		return getIDPart1 () + GUI.EMPTY_STRING;
	}

	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_CONTRACT_BID_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
