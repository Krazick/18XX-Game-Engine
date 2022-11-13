package ge18xx.round;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class OperatingRound extends Round {
	public final static ElementName EN_OPERATING_ROUND = new ElementName ("OperatingRound");
	public final static OperatingRound NO_OPERATING_ROUND = null;
	public final static String NAME = "Operating Round";
	CorporationList privateCompanies;

	CorporationList minorCompanies;
	CorporationList shareCompanies;

	public OperatingRound (RoundManager aRoundManager, CorporationList aPrivates,
			CorporationList aMinors, CorporationList aShares) {
		super (aRoundManager);
		setID (0, 0);
		privateCompanies = aPrivates;

		minorCompanies = aMinors;
		shareCompanies = aShares;
	}

	public boolean anyFloatedCompanies () {
		boolean tAnyFloatedCompanies = false;

		tAnyFloatedCompanies = tAnyFloatedCompanies || minorCompanies.anyCanOperate ();
		tAnyFloatedCompanies = tAnyFloatedCompanies || shareCompanies.anyCanOperate ();

		return tAnyFloatedCompanies;
	}

	@Override
	public boolean startOperatingRound () {
		boolean tStartedOperatingRound = true;

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
		ShareCompany tShareCompany;
		int tNextShareToOperate;
		int tCurrentlyOperating;

		// TODO: Test if Minor Companies need to operate Before Share Companies
		tCurrentlyOperating = shareCompanies.getCurrentlyOperating ();
		if (tCurrentlyOperating != CorporationList.NO_CORPORATION_INDEX) {
			tShareCompany = (ShareCompany) shareCompanies.getCorporation (tCurrentlyOperating);
			roundManager.updateActionLabel (tShareCompany);
		} else {
			tNextShareToOperate = shareCompanies.getNextToOperate ();
			if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
				tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareToOperate);
				roundManager.updateActionLabel (tShareCompany);
			}
		}
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
		System.out.println (" Operating Round " + idPart1 + "." + idPart2);
	}

	@Override
	public boolean roundIsDone () {
		boolean tRoundDone;

		tRoundDone = false;

		// TODO: Test if all Train Companies (if any) have Operated, not just Share Companies
		// (test Minors - 1835)

		if (shareCompanies != CorporationList.NO_CORPORATION_LIST) {
			tRoundDone = shareCompanies.haveAllCompaniesOperated ();
		}

		return tRoundDone;
	}

	public boolean companyStartedOperating () {
		int tNextShareToOperate;
		boolean tCompanyStartedOperating;

		tNextShareToOperate = getNextShareToOperate ();

		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tCompanyStartedOperating = shareCompanies.companyStartedOperating (tNextShareToOperate);
		} else {
			tCompanyStartedOperating = false;
		}

		return tCompanyStartedOperating;
	}

	public void prepareCorporation () {
		int tNextShareToOperate;

		tNextShareToOperate = getNextShareToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			shareCompanies.prepareCorporation (tNextShareToOperate);
		}
	}

	public void showCurrentCompanyFrame () {
		int tNextShareToOperate;

		tNextShareToOperate = getNextShareToOperate ();
		if (tNextShareToOperate != CorporationList.NO_CORPORATION_INDEX) {
			shareCompanies.showCompanyFrame (tNextShareToOperate);
		}
	}

	public int getNextShareToOperate () {
		int tNextShareIndexToOperate;
		ShareCompany tShareCompany;
		int tStartingTreasury;

		// TODO: 1835 - Need to check for Minor Companies BEFORE Share Companies

		tNextShareIndexToOperate = shareCompanies.getNextToOperate ();
		if (tNextShareIndexToOperate != CorporationList.NO_CORPORATION_INDEX) {
			tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareIndexToOperate);
			if (!tShareCompany.hasFloated ()) {
				if (tShareCompany.shouldFloat ()) {
					tStartingTreasury = tShareCompany.calculateStartingTreasury ();
					tShareCompany.floatCompany (tStartingTreasury);
				} else {
					tNextShareIndexToOperate = CorporationList.NO_CORPORATION_INDEX;
				}
			}
		}

		return tNextShareIndexToOperate;
	}

	public void updateCurrentCompanyFrame () {
		int tNextShareToOperate;
		ShareCompany tShareCompany;

		// Need for every time a Company Operates, to be sure to provide capitalization
		tNextShareToOperate = getNextShareToOperate ();
		tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareToOperate);
		tShareCompany.updateFrameInfo ();
	}

	public Corporation getOperatingCompany () {
		Corporation tCorporation;

		tCorporation = shareCompanies.getOperatingCompany ();
		if (tCorporation == Corporation.NO_CORPORATION) {
			tCorporation = minorCompanies.getOperatingCompany ();
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
