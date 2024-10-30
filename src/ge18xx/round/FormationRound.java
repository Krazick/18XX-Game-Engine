package ge18xx.round;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class FormationRound extends InterruptionRound {
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";
	public static final AttributeName AN_PHASE = new AttributeName ("phase");
	String phase;

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
	}
	
	@Override
	public void loadRound (XMLNode aRoundNode) {
		String tPhase;
		
		super.loadRound (aRoundNode);
		tPhase = aRoundNode.getThisAttribute (AN_PHASE);
		setPhase (tPhase);
	}
	
	public void setPhase (String aPhase) {
		phase = aPhase;
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
		boolean tIsInterrupting;
		GameManager tGameManager;
		String tCurrentPhaseName;
		PhaseInfo tCurrentPhaseInfo;
		
		tIsInterrupting = false;
		// TODO: Need to add test if Interruption is required:
		// for 1856 Purchase of 
		//		We have Entered Phase 5 (in Phase Info)
		//		AND at least one Share Company has at least one Loan Outstanding
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.gameHasRoundType (NAME)) {
			tCurrentPhaseInfo = roundManager.getCurrentPhaseInfo ();
			tCurrentPhaseName = tCurrentPhaseInfo.getFullName ();
			if (phase.equals (tCurrentPhaseName)) {
				tIsInterrupting = hasOutstandingLoans ();
			}
		}
		
		// for 1835 Purchase of 
		//		a X Train and Formation is Optional
		//		OR Start of OR if PR formation started
		//		OR Purchase of Z Train and Formation REQUIRED
		
		return tIsInterrupting;
	}
	
	public boolean hasOutstandingLoans () {
		CorporationList tShareCompanies;
		boolean tCanStart;
		
		tCanStart = false;
		if (roundManager.gameHasLoans ()) {
			tShareCompanies = roundManager.getShareCompanies ();
			tCanStart = tShareCompanies.anyHaveLoans ();
		}
		
		return tCanStart;
	}

	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_FORMATION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
