package ge18xx.round;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;

import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.round.action.ActorI;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class OperatingRound extends Round {
	public final static ElementName EN_OPERATING_ROUND = new ElementName ("OperatingRound");
	public final static OperatingRound NO_OPERATING_ROUND = null;
	public final static String NAME = "Operating Round";
	CorporationList privateCompanies;
	CorporationList minorCompanies;
	CorporationList shareCompanies;
	String operatingType;

	public OperatingRound (RoundManager aRoundManager, CorporationList aPrivates,
			CorporationList aMinors, CorporationList aShares) {
		super (aRoundManager);
		setID (0, 0);
		privateCompanies = aPrivates;
		minorCompanies = aMinors;
		shareCompanies = aShares;
		setOperatingType (GUI.NULL_STRING);
	}

	public void setOperatingType (String aOperatingType) {
		operatingType = aOperatingType;
	}
	
	public String currentOperatingType () {
		return operatingType;
	}
	
	public boolean anyFloatedCompanies () {
		boolean tAnyFloatedCompanies;

		tAnyFloatedCompanies = false;
		tAnyFloatedCompanies = tAnyFloatedCompanies || minorCompanies.anyCanOperate ();
		tAnyFloatedCompanies = tAnyFloatedCompanies || shareCompanies.anyCanOperate ();

		return tAnyFloatedCompanies;
	}

	@Override
	public boolean startOperatingRound () {
		boolean tStartedOperatingRound;

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
		TrainCompany tTrainCompany;
		int tNextShareToOperate;

		tFoundNextToOperate = false;
		tNextShareToOperate = aCompanies.getNextToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (TrainCompany) aCompanies.getCorporation (tNextShareToOperate);
			roundManager.updateActionLabel (tTrainCompany);
			tFoundNextToOperate = true;
		}

		return tFoundNextToOperate;
	}
	
	public boolean updateForCurrentlyOperating (CorporationList aCompanies) {
		boolean tFoundCurrentOperating;
		int tCurrentlyOperating;
		TrainCompany tTrainCompany;
		
		tFoundCurrentOperating = false;
		tCurrentlyOperating = aCompanies.getCurrentlyOperating ();
		if (tCurrentlyOperating != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (TrainCompany) aCompanies.getCorporation (tCurrentlyOperating);
			roundManager.updateActionLabel (tTrainCompany);
			tFoundCurrentOperating = true;
		}
		
		return tFoundCurrentOperating;
	}
	
	public CorporationList getMinorCompanies () {
		return minorCompanies;
	}

	public int getMinorCompanyCount () {
		return minorCompanies.getRowCount ();
	}

	@Override
	public String getName () {
		return NAME;
	}

	public String getOperatingOwnerName () {
		return shareCompanies.getOperatingOwnerName ();
	}

	public String getOwnerWhoWillOperate () {
		return shareCompanies.getOwnerWhoWillOperate ();
	}

	public CorporationList getPrivateCompanies () {
		return privateCompanies;
	}

	public int getPrivateCompanyCount () {
		return privateCompanies.getRowCount ();
	}

	@Override
	public ActorI.ActionStates getRoundType () {
		return ActorI.ActionStates.OperatingRound;
	}

	@Override
	public String getStateName () {
		return getRoundType ().toString ();
	}

	public int getShareCompanyCount () {
		return shareCompanies.getRowCount ();
	}

	public XMLElement getRoundState (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_OPERATING_ROUND);
		setRoundAttributes (tXMLElement);

		return tXMLElement;
	}

	@Override
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
	}

	public CorporationList getShareCompanies () {
		return shareCompanies;
	}

	public ShareCompany getShareCompanyIndex (int aIndex) {
		return (ShareCompany) shareCompanies.getCorporation (aIndex);
	}

	@Override
	public String getType () {
		return NAME;
	}

	public void payRevenues () {
		privateCompanies.payPrivateRevenues (getBank (), this);
	}

	public void handleQueryBenefits () {
		privateCompanies.handleQueryBenefits (roundManager.getRoundFrame ());
	}

	public void printRoundInfo () {
		System.out.println (" Operating Round " + idPart1 + "." + idPart2);	// PRINTLOG
	}

	@Override
	public boolean roundIsDone () {
		boolean tMinorsAreDone;
		boolean tSharesAreDone;
		boolean tRoundIsDone;

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
		
		tRoundIsDone = tMinorsAreDone && tSharesAreDone;

		return tRoundIsDone;
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
		int tNextCompanyToOperate;
//		int tStartingTreasury;
		
		tNextCompanyToOperate = aTrainCompanies.getNextToOperate ();
		if (tNextCompanyToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tTrainCompany = (TrainCompany) aTrainCompanies.getCorporation (tNextCompanyToOperate);
			if (! tTrainCompany.hasFloated () ) {
				if (tTrainCompany.shouldFloat ()) {
					if (tTrainCompany.isAShareCompany ()) {
						tShareCompany = (ShareCompany) tTrainCompany;
//						tShareCompany.setDestinationCapitalizationLevel ();
//						tStartingTreasury = tShareCompany.calculateStartingTreasury ();
						tShareCompany.floatCompany ();
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

	public void sortByOperatingOrder () {
		shareCompanies.sortByOperatingOrder ();
	}

	@Override
	public boolean isAOperatingRound () {
		return true;
	}
}
