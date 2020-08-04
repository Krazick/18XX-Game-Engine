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
	public final static String NAME = "Operating Round";
	CorporationList privateCompanies;
	CorporationList coalCompanies;
	CorporationList minorCompanies;
	CorporationList shareCompanies;
	
	public OperatingRound (RoundManager aRoundManager, CorporationList aPrivates, CorporationList aCoals, 
					CorporationList aMinors, CorporationList aShares) {
		super (aRoundManager);
		setID (0, 0);
		privateCompanies = aPrivates;
		coalCompanies = aCoals;
		minorCompanies = aMinors;
		shareCompanies = aShares;
	}
	
	public boolean anyFloatedCompanies () {
		boolean tAnyFloatedCompanies = false;
		
		tAnyFloatedCompanies = tAnyFloatedCompanies || coalCompanies.anyCanOperate ();
		tAnyFloatedCompanies = tAnyFloatedCompanies || minorCompanies.anyCanOperate ();
		tAnyFloatedCompanies = tAnyFloatedCompanies || shareCompanies.anyCanOperate ();
		
		return tAnyFloatedCompanies;
	}
	
	public boolean startOperatingRound () {
		boolean tStartedOperatingRound = true;
		
		payRevenues ();
		if (anyFloatedCompanies ()) {
			coalCompanies.clearOperatedStatus ();
			minorCompanies.clearOperatedStatus ();
			shareCompanies.clearOperatedStatus ();
			// Note -- Need to check for Coal Companies and Minor Companies BEFORE Share Companies
			roundManager.revalidateRoundFrame ();
			updateActionLabel ();
		} else {
			tStartedOperatingRound = false;
		}
		
		return tStartedOperatingRound;
	}
	
	public void updateActionLabel () {
		ShareCompany tShareCompany;
		int tNextShareToOperate;
		
		tNextShareToOperate = shareCompanies.getNextToOperate ();
		if (tNextShareToOperate >= 0) {
			tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareToOperate); 
			roundManager.updateActionLabel (tShareCompany);
		}
	}
	
	public CorporationList getCoalCompanies () {
		return coalCompanies;
	}

	public int getCoalCompanyCount () {
		return coalCompanies.getRowCount ();
	}
	
	public CorporationList getMinorCompanies () {
		return minorCompanies;
	}

	public int getMinorCompanyCount () {
		return minorCompanies.getRowCount ();
	}
	
	public String getName () {
		return NAME;
	}

	public String getOperatingOwnerName () {
		return shareCompanies.getOperatingOwnerName ();
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
	
	public void loadRound (XMLNode aRoundNode) {
		super.loadRound (aRoundNode);
	}

	public CorporationList getShareCompanies () {
		return shareCompanies;
	}
	
	public ShareCompany getShareCompanyIndex (int aIndex) {
		return (ShareCompany) shareCompanies.getCorporation(aIndex);
	}
	
	@Override
	public String getType () {
		return NAME;
	}
	
	public void payRevenues () {
		if (getPrivateCompanyCount () > 0) {
			privateCompanies.payPrivateRevenues (getBank (), this);
		}
	}
	
	public void printRoundInfo () {
		System.out.println (" Operating Round " + idPart1 + "." + idPart2);
	}
	
	@Override
	public boolean roundIsDone () {
		boolean tRoundDone;
		
		tRoundDone = false;
		if (shareCompanies != null) {
			tRoundDone = shareCompanies.haveAllCompaniesOperated ();
		}
		
		return tRoundDone;
	}
	
	public void showCurrentCompanyFrame () {
		int tNextShareToOperate;
		
		tNextShareToOperate = getNextToOperate ();
		shareCompanies.showCompanyFrame (tNextShareToOperate);
	}
	
	public int getNextToOperate () {
		int tNextShareToOperate;
		ShareCompany tShareCompany;
		int tTreasury, tCapitalizationAmount;
	
		// TODO: 1837 - (Austria) Need to check for Coal Companies and Minor Companies BEFORE Share Companies
		
		tNextShareToOperate = shareCompanies.getNextToOperate ();
		tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareToOperate); 
		// TODO: 1856 - test against "May Float" State, not just "Will Float" State
		if (tShareCompany.shouldFloat ()) {
			tCapitalizationAmount = tShareCompany.getCapitalizationAmount ();
			tTreasury = tCapitalizationAmount * tShareCompany.getParPrice ();
			tShareCompany.floatCompany (tTreasury);
		}
		
		// TODO: non-1830 For "Partial Capitalization" need to see if more Shares have been sold and add that capitalization  -- Maybe as part of Buy Share during Stock Round?
		return tNextShareToOperate;
	}
	
	public void updateCurrentCompanyFrame () {
		int tNextShareToOperate;
		ShareCompany tShareCompany;
		
		// Need for every time a Company Operates, to be sure to provide capitalization
		tNextShareToOperate = getNextToOperate ();
		tShareCompany = (ShareCompany) shareCompanies.getCorporation (tNextShareToOperate); 
		tShareCompany.updateFrameInfo ();
	}

	public Corporation getOperatingCompany () {
		Corporation tCorporation;
		
		tCorporation = shareCompanies.getOperatingCompany ();
		if (tCorporation == CorporationList.NO_CORPORATION) {
			tCorporation = minorCompanies.getOperatingCompany ();
		}
		if (tCorporation == CorporationList.NO_CORPORATION) {
			tCorporation = coalCompanies.getOperatingCompany ();
		}

		return tCorporation;
	}
	
	@Override
	public boolean isAOperatingRound () {
		return true;
	}
}
