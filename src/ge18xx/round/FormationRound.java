package ge18xx.round;

import ge18xx.round.action.ActorI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class FormationRound extends InterruptionRound {
	public final static ElementName EN_FORMATION_ROUND = new ElementName ("FormationRound");
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
	}
	
	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.FormationRound;
	}
	
	@Override
	public boolean isAFormationRound () {
		return true;
	}
	
	@Override
	public boolean isInterrupting () {
		// TODO: Need to add test if Interruption is required:
		// for 1856 Purchase of a 6 Train
		// for 1835 Purchase of 
		//		a X Train and Formation is Optional
		//		or Start of OR if PR formation started
		//		Purchase of Z Train and Formation REQUIRED
		
		return false;
	}
	
	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_FORMATION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
