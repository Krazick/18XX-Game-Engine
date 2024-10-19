package ge18xx.round;

import ge18xx.round.action.ActorI;
//import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;

public class ContractBidRound extends Round {
//	public final static ElementName EN_CONTRACT_BID_ROUND = new ElementName ("ContractBidRound");
	public static final ContractBidRound NO_CONTRACT_BID_ROUND = null;
	public static final String NAME = "Contract Bid Round";

	public ContractBidRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
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
