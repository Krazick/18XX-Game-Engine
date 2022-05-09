package ge18xx.toplevel;

import javax.swing.JTable;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class CorporationTableFrame extends TableFrame {
	public static final ElementName EN_COMPANIES = new ElementName ("Companies");
	public static final CorporationTableFrame NO_CORP_TABLE_FRAME = null;
	private static final long serialVersionUID = 1L;
	CorporationList companies;

	public CorporationTableFrame (String aFrameName, ElementName aTypeName, RoundManager aRoundManager) {
		super (aFrameName, aRoundManager.getGameName ());

		JTable tTable;

		companies = new CorporationList (aTypeName, aRoundManager);
		tTable = companies.getJTable ();
		setScrollPane (tTable);
	}

	public boolean anyPrivatesUnowned () {
		return false;
	}

	public void applyCloseToPrivates () {
		// DO nothing here, let Higher Frame handle it
	}

	public void removeAllBids () {
		// DO nothing here, let Higher Frame handle it
	}

	public void clearSelections () {
		companies.clearSelections ();
	}

	public void closeCompany (int aCompanyID, TransferOwnershipAction aTransferOwnershipAction) {
		companies.closeCompany (aCompanyID, aTransferOwnershipAction);
	}

	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLCompaniesStates;

		tXMLCompaniesStates = getCorporationStateElements (aXMLDocument, EN_COMPANIES);

		return tXMLCompaniesStates;
	}

	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument, ElementName aEN_TYPE) {
		XMLElement tXMLCompaniesStates;

		tXMLCompaniesStates = null;
		if (companies != null) {
			if (companies.getCorporationCount () > 0) {
				tXMLCompaniesStates = aXMLDocument.createElement (aEN_TYPE);
				companies.getCorporationStateElements (aXMLDocument, tXMLCompaniesStates);
			}
		}

		return tXMLCompaniesStates;
	}

	public XMLElement createCompaniesListDefinitions (XMLDocument aXMLDocument) {
		return (companies.createElement (aXMLDocument));
	}

	public ActorI getActor (String aActorName) {
		return (companies.getActor (aActorName));
	}

	public Certificate getCertificate (String aCompanyAbbrev, int aPercentage, boolean aPresidentShare) {
		return (companies.getCertificate (aCompanyAbbrev, aPercentage, aPresidentShare));
	}

	public Corporation getCorporationByID (int aCorporationID) {
		return companies.getCorporationByID (aCorporationID);
	}

	public int getCountOfCompanies () {
		return companies.getRowCount ();
	}

	public CorporationList getCompanies () {
		return companies;
	}

	public int getCountOfOpenCompanies () {
		return companies.getCountOfOpen ();
	}

	public int getCountOfPlayerOwnedCompanies () {
		return companies.getCountOfPlayerOwned ();
	}

	public int getCountOfSelectedCertificates () {
		return companies.getCountOfSelectedCertificates ();
	}

	public Corporation getSelectedCorporation () {
		return companies.getSelectedCorporation ();
	}

	public void loadStates (XMLNode aXMLNode) {
		companies.loadStates (aXMLNode);
	}

	public void fixLoadedRoutes (MapFrame aMapFrame, String aCompanyType) {
		int tCompanyCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCompanyCount = companies.getRowCount ();

			if (tCompanyCount > 0) {
				companies.fixLoadedRoutes (aMapFrame);
			}
		}
	}

	public int getTotalCorpCash () {
		int tTotalCorpCash = 0;
		int tCompanyCount;

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCompanyCount = companies.getRowCount ();

			if (tCompanyCount > 0) {
				tTotalCorpCash = companies.getTotalCorpCash ();
			}
		}

		return tTotalCorpCash;
	}
}
