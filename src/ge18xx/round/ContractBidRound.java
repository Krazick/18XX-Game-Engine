package ge18xx.round;

import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.ContractBidFrame;
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
		
 		ContractBidFrame tContractBidFrame;
 		
		setName (NAME);
		setRoundType ();
		gameManager = aRoundManager.getGameManager ();
		tContractBidFrame = new ContractBidFrame (NAME, gameManager);
		setContractBidFrame (tContractBidFrame);
	}

	public void setContractBidFrame (ContractBidFrame aContractBidFrame) {
		contractBidFrame = aContractBidFrame;
	}
	
	public ContractBidFrame getContractBidFrame () {
		return contractBidFrame;
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
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_CONTRACT_BID_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
