package ge18xx.round;

import ge18xx.company.CorporationList;
import ge18xx.company.formation.FormCGR;
import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.round.action.ActorI;
import geUtilities.GUI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class FormationRound extends InterruptionRound {
	public static final FormationRound NO_FORMATION_ROUND = null;
	public static final String NAME = "Formation Round";

	public FormationRound (RoundManager aRoundManager) {
		super (aRoundManager);
		setName (NAME);
		setRoundType ();
	}
	
	@Override
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
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
		String tRoundTypePhase;
		
		tIsInterrupting = false;
		// TODO: Need to add test if Interruption is required:
		// for 1856 Purchase of 
		//		We have Entered Phase 5 (in Phase Info)
		//		AND at least one Share Company has at least one Loan Outstanding
		tGameManager = roundManager.getGameManager ();
		if (tGameManager.gameHasRoundType (NAME)) {
			tRoundTypePhase = getPhase ();
			if (tRoundTypePhase != GUI.EMPTY_STRING) {
				tCurrentPhaseInfo = roundManager.getCurrentPhaseInfo ();
				tCurrentPhaseName = tCurrentPhaseInfo.getFullName ();
				if (tRoundTypePhase.equals (tCurrentPhaseName)) {
					tIsInterrupting = hasOutstandingLoans ();
				}
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
	public void start () {
		FormCGR tFormCGR;
		GameManager tGameManager;
//		Corporation tCorporation;

		System.out.println ("Ready to START Formation Round");
		tGameManager = roundManager.getGameManager ();
		tFormCGR = tGameManager.getFormCGR ();
		if (tFormCGR == FormCGR.NO_FORM_CGR) {
			tFormCGR = new FormCGR (tGameManager);
		}
//		tCorporation = roundManager.getOperatingCompany ();
//		tFormCGR.setTriggeringShareCompany ((ShareCompany) tCorporation);
		if (! tFormCGR.isFormationFrameVisible ()) {
			tFormCGR.showFormationFrame ();
		}
	}
	
	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_FORMATION_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}
}
