package ge18xx.round;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.MinorCompany;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ChangeRoundAction;
import geUtilities.GUI;
//import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class OperatingRound extends Round {
//	public final static ElementName EN_OPERATING_ROUND = new ElementName ("OperatingRound");
	public final static OperatingRound NO_OPERATING_ROUND = null;
	public final static String NAME = "Operating Round";
	CorporationList privateCompanies;
	CorporationList minorCompanies;
	CorporationList shareCompanies;
	String operatingType;

	public OperatingRound (RoundManager aRoundManager, CorporationList aPrivates,
			CorporationList aMinors, CorporationList aShares) {
		super (aRoundManager);
		privateCompanies = aPrivates;
		minorCompanies = aMinors;
		shareCompanies = aShares;
		setOperatingType (GUI.NULL_STRING);
		setName (NAME);
		setRoundType ();
	}

	@Override
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
		setName (NAME);
		setRoundType ();
	}

	public void setOperatingType (String aOperatingType) {
		operatingType = aOperatingType;
	}

	@Override
	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_OPERATING_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}

	// Methods to ask this (Operating Round) to handle

	@Override
	public String getStateName () {
		return getRoundState ().toString ();
	}

	public String currentOperatingType () {
		return operatingType;
	}
	
	public CorporationList getPrivateCompanies () {
		return privateCompanies;
	}
	
	public CorporationList getMinorCompanies () {
		return minorCompanies;
	}

	public CorporationList getShareCompanies () {
		return shareCompanies;
	}

	@Override
	public ActorI.ActionStates getRoundState () {
		return ActorI.ActionStates.OperatingRound;
	}

	@Override
	public boolean isAOperatingRound () {
		return true;
	}

	// Methods to ask Private Companies to handle
	
	public int getPrivateCompanyCount () {
		return privateCompanies.getRowCount ();
	}

	public void payRevenues () {
		privateCompanies.payPrivateRevenues (getBank (), this);
	}

	public void handleQueryBenefits () {
		privateCompanies.handleQueryBenefits (roundManager.getRoundFrame ());
	}

	// Methods to ask Minor Companies to handle
	
	public int getMinorCompanyCount () {
		return minorCompanies.getRowCount ();
	}

	// Methods to ask Share Companies to handle

	public int getShareCompanyCount () {
		return shareCompanies.getRowCount ();
	}
	
	public String getOperatingOwnerName () {
		return shareCompanies.getOperatingOwnerName ();
	}

	public String getOwnerWhoWillOperate () {
		return shareCompanies.getOwnerWhoWillOperate ();
	}

	public ShareCompany getShareCompanyIndex (int aIndex) {
		return (ShareCompany) shareCompanies.getCorporation (aIndex);
	}

	public void sortByOperatingOrder () {
		shareCompanies.sortByOperatingOrder ();
	}

//	@Override
	public boolean startOperatingRound () {
		boolean tStartedOperatingRound;
		Round tCurrentRound;
		PlayerManager tPlayerManager;
		int tIDPart1;
		int tIDPart2;

		tPlayerManager = roundManager.getPlayerManager ();
		tCurrentRound = roundManager.getCurrentRound ();
		tIDPart1 = incrementRoundIDPart1 ();
		tIDPart2 = Round.START_ID2;
		roundManager.setRoundToOperatingRound (tCurrentRound, tIDPart1, tIDPart2);
		tPlayerManager.clearAllPlayerDividends ();
		tPlayerManager.clearAllPercentBought ();

		tStartedOperatingRound = true;
		if (! roundManager.applyingAction ()) {
			if (getPrivateCompanyCount () > 0) {
				payRevenues ();
				handleQueryBenefits ();
			}
		}
		if (anyFloatedCompanies ()) {
			minorCompanies.clearOperatedStatus ();
			shareCompanies.clearOperatedStatus ();
			updateActionLabel ();
		} else {
			tStartedOperatingRound = false;
		}
		roundManager.updateRoundFrame ();

		return tStartedOperatingRound;
	}

	public boolean anyFloatedCompanies () {
		boolean tAnyFloatedCompanies;

		tAnyFloatedCompanies = false;
		tAnyFloatedCompanies = tAnyFloatedCompanies || minorCompanies.anyCanOperate ();
		tAnyFloatedCompanies = tAnyFloatedCompanies || shareCompanies.anyCanOperate ();

		return tAnyFloatedCompanies;
	}

	public void updateActionLabel () {
		boolean tFoundCurrentlyOperating;
		boolean tFoundNextToOperate;
		boolean tHaveFoundOperating;

		setOperatingType (GUI.NULL_STRING);
		tHaveFoundOperating = false;
		// Find the Currently Operating Minor Company
		tFoundCurrentlyOperating = updateForCurrentlyOperating (minorCompanies);
		if (!tFoundCurrentlyOperating) {
			tFoundNextToOperate = updateForNextToOperate (minorCompanies);
			if (tFoundNextToOperate) {
				setOperatingType (Corporation.MINOR_COMPANY);
				tHaveFoundOperating = true;
			}
		} else {
			tHaveFoundOperating = true;
		}
		
		// If no Minor is Operating, find Currently Operating Share Company
		
		if (! tHaveFoundOperating) {
			tFoundCurrentlyOperating = updateForCurrentlyOperating (shareCompanies);
			if (tFoundCurrentlyOperating) {
				setOperatingType (Corporation.SHARE_COMPANY);
			} else {
				tFoundNextToOperate = updateForNextToOperate (shareCompanies);
				if (tFoundNextToOperate) {
					setOperatingType (Corporation.SHARE_COMPANY);
				}
			}
		}
	}

	public boolean updateForNextToOperate (CorporationList aCompanies) {
		boolean tFoundNextToOperate;
		Corporation tTrainCompany;
		int tNextShareToOperate;

		tFoundNextToOperate = false;
		tNextShareToOperate = aCompanies.getNextToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (Corporation) aCompanies.getCorporation (tNextShareToOperate);
			roundManager.updateActionLabel (tTrainCompany);
			tFoundNextToOperate = true;
		}

		return tFoundNextToOperate;
	}
	
	public boolean updateForCurrentlyOperating (CorporationList aCompanies) {
		boolean tFoundCurrentOperating;
		int tCurrentlyOperating;
		Corporation tTrainCompany;
		
		tFoundCurrentOperating = false;
		tCurrentlyOperating = aCompanies.getCurrentlyOperating ();
		if (tCurrentlyOperating != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (Corporation) aCompanies.getCorporation (tCurrentlyOperating);
			roundManager.updateActionLabel (tTrainCompany);
			tFoundCurrentOperating = true;
		}
		
		return tFoundCurrentOperating;
	}

	public void printRoundInfo () {
		System.out.println (" Operating Round " + idPart1 + "." + idPart2);	// PRINTLOG
	}

	@Override
	public boolean ends () {
		boolean tMinorsAreDone;
		boolean tSharesAreDone;
		boolean tEnds;

		if (minorCompanies != CorporationList.NO_CORPORATION_LIST) {
			tMinorsAreDone = minorCompanies.haveAllCompaniesOperated ();
		} else {
			tMinorsAreDone = true;
		}

		if (shareCompanies != CorporationList.NO_CORPORATION_LIST) {
			tSharesAreDone = shareCompanies.haveAllCompaniesOperated ();
		} else {
			tSharesAreDone = true;
		}
		
		tEnds = tMinorsAreDone && tSharesAreDone;

		return tEnds;
	}

	public boolean companyStartedOperating () {
		int tNextShareToOperate;
		boolean tCompanyStartedOperating;

		tNextShareToOperate = getNextCompanyToOperate ();

		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			if (operatingType.equals (Corporation.SHARE_COMPANY)) {
				tCompanyStartedOperating = shareCompanies.companyStartedOperating (tNextShareToOperate);
			} else if (operatingType.equals (Corporation.MINOR_COMPANY)) {
				tCompanyStartedOperating = minorCompanies.companyStartedOperating (tNextShareToOperate);
			} else {
				tCompanyStartedOperating = false;				
			}
		} else {
			tCompanyStartedOperating = false;
		}

		return tCompanyStartedOperating;
	}

	public void prepareCorporation () {
		int tNextShareToOperate;

		tNextShareToOperate = getNextCompanyToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			if (operatingType.equals (Corporation.SHARE_COMPANY)) {
				shareCompanies.prepareCorporation (tNextShareToOperate);
			} else if (operatingType.equals (Corporation.MINOR_COMPANY)) {
				minorCompanies.prepareCorporation (tNextShareToOperate);
			}
		}
	}

	public void showCurrentCompanyFrame () {
		int tNextShareToOperate;

		tNextShareToOperate = getNextCompanyToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			if (operatingType.equals (Corporation.SHARE_COMPANY)) {
				shareCompanies.showCompanyFrame (tNextShareToOperate);
			} else if (operatingType.equals (Corporation.MINOR_COMPANY)) {
				minorCompanies.showCompanyFrame (tNextShareToOperate);
			}
		}
	}

	public int getNextCompanyToOperate (CorporationList aTrainCompanies) {
		TrainCompany tTrainCompany;
		ShareCompany tShareCompany;
		MinorCompany tMinorCompany;
		int tNextCompanyToOperate;
		
		tNextCompanyToOperate = aTrainCompanies.getNextToOperate ();
		if (tNextCompanyToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (TrainCompany) aTrainCompanies.getCorporation (tNextCompanyToOperate);
			if (! tTrainCompany.hasFloated () ) {
				if (tTrainCompany.shouldFloat ()) {
					if (tTrainCompany.isAShareCompany ()) {
						tShareCompany = (ShareCompany) tTrainCompany;
						tShareCompany.floatCompany ();
					} else if (tTrainCompany.isAMinorCompany ()) {
						tMinorCompany = (MinorCompany) tTrainCompany;
						tMinorCompany.floatCompany ();
					} else {
						System.err.println ("The Train Company trying to float is not a Share Company");
					}
				} else {
					tNextCompanyToOperate =  CorporationList.NO_CORPORATION_INDEX;
				}
			}
		}
		
		return tNextCompanyToOperate;
	}
	
	public int getNextCompanyToOperate () {
		int tNextCompanyToOperate;
		
		tNextCompanyToOperate = getNextCompanyToOperate (minorCompanies);
		if (tNextCompanyToOperate == CorporationList.NO_CORPORATION_INDEX) {
			tNextCompanyToOperate = getNextCompanyToOperate (shareCompanies);
			if (tNextCompanyToOperate != CorporationList.NO_CORPORATION_INDEX) {
				setOperatingType (Corporation.SHARE_COMPANY);
			}
		} else {
			setOperatingType (Corporation.MINOR_COMPANY);
		}
		
		return tNextCompanyToOperate;
	}
	
	public Corporation getOperatingCompany () {
		Corporation tCorporation;

		tCorporation = shareCompanies.getOperatingTrainCompany ();
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = minorCompanies.getOperatingTrainCompany ();
			if (tCorporation != Corporation.NO_CORPORATION) {
				setOperatingType (Corporation.MINOR_COMPANY);
			}
		} else {
			setOperatingType (Corporation.SHARE_COMPANY);
		}

		return tCorporation;
	}

	@Override
	public boolean canStart () {
		Bank tBank;
		boolean tCanStart;
		
		tBank = getBank ();
		tCanStart = tBank.canStartOperatingRound ();
		
		return tCanStart;
	}
	
	@Override
	public void finish () {
	}

	@Override
	public void finish (XMLFrame aXMLFrame) {
	}

	@Override
	public void resume () {		
	}
	
	public void setRoundToOperatingRound (Round aCurrentRound, String aOldID, int aRoundIDPart1, int aRoundIDPart2) {
		ChangeRoundAction tChangeRoundAction;
//		ActorI.ActionStates tCurrentRoundType;
		RoundFrame tRoundFrame;
		String tNewID;
//		String tCurrentRoundID;
		String tGameName;
		int tOperatingRoundCount;

		if (aRoundIDPart2 == Round.START_ID2) {
			roundManager.setOperatingRoundCount ();
		}
		roundManager.setCurrentOR (aRoundIDPart2);
		setID (aRoundIDPart1, aRoundIDPart2);
		tNewID = getID ();
		tOperatingRoundCount = roundManager.getOperatingRoundCount ();
		
//		tCurrentRoundID = aCurrentRound.getID ();
//		tCurrentRoundType = roundManager.getCurrentRoundState ();
//		tChangeRoundAction = new ChangeRoundAction (tCurrentRoundType, tCurrentRoundID, aCurrentRound);
		
		tChangeRoundAction = buildChangeRoundAction ();
		roundManager.changeRound (aCurrentRound, ActorI.ActionStates.OperatingRound, this, aOldID,
				tNewID, tChangeRoundAction);
		
		tRoundFrame = roundManager.getRoundFrame ();
		tGameName = roundManager.getGameName ();

		tRoundFrame.setOperatingRound (tGameName, aRoundIDPart1, aRoundIDPart2, tOperatingRoundCount);
		tRoundFrame.revalidate ();
		if (!roundManager.applyingAction ()) {
			addAction (tChangeRoundAction);
		}
	}

	@Override
	public void start () {
		PlayerManager tPlayerManager;
		Round tCurrentRound;
		String tOldID;
		int tIDPart1;
		int tIDPart2;

		tPlayerManager = roundManager.getPlayerManager ();
		tCurrentRound = roundManager.getCurrentRound ();

		tOldID = getID ();
		if (repeatRound) {
			tIDPart1 = getIDPart1 ();
			tIDPart2 = getIDPart2 () + 1;
		} else {
			tIDPart1 = incrementRoundIDPart1 ();
			tIDPart2 = Round.START_ID2;
			tPlayerManager.clearAllPlayerDividends ();
			tPlayerManager.clearAllPercentBought ();
		}
		super.start ();

		setRoundToOperatingRound (tCurrentRound, tOldID, tIDPart1, tIDPart2);

		if (! roundManager.applyingAction ()) {
			if (getPrivateCompanyCount () > 0) {
				payRevenues ();
				handleQueryBenefits ();
			}
		}
		if (anyFloatedCompanies ()) {
			minorCompanies.clearOperatedStatus ();
			shareCompanies.clearOperatedStatus ();
			updateActionLabel ();
		}
		roundManager.updateRoundFrame ();
	}
}
