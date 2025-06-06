package ge18xx.toplevel;

import javax.swing.JTable;

import ge18xx.company.Certificate;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.TransferOwnershipAction;
import ge18xx.train.TrainHolderI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLSaveGameI;

public class CorporationTableFrame extends XMLFrame implements XMLSaveGameI {
	private static final long serialVersionUID = 1L;
	public static final ElementName EN_COMPANIES = new ElementName ("Companies");
	public static final CorporationTableFrame NO_CORP_TABLE_FRAME = null;
	CorporationList companies;

	public CorporationTableFrame (String aFrameName, ElementName aTypeName, RoundManager aRoundManager) {
		super (aFrameName, aRoundManager.getGameManager ());

		JTable tTable;

		companies = new CorporationList (aTypeName, aRoundManager);
		tTable = companies.getJTable ();
		buildScrollPane (tTable);
	}

	public void addMessageBeans () {
		companies.addMessageBeans ();
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

	public XMLElement addElements (XMLDocument aXMLDocument) {
		XMLElement tXMLCompaniesStates;

		tXMLCompaniesStates = addElements (aXMLDocument, EN_COMPANIES);

		return tXMLCompaniesStates;
	}

	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_TYPE) {
		XMLElement tXMLCompaniesStates;

		tXMLCompaniesStates = XMLElement.NO_XML_ELEMENT;
		if (companies != CorporationList.NO_CORPORATION_LIST) {
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

	public Corporation getCorporationByAbbrev (String aCorporationAbbrev) {
		return companies.getCorporation (aCorporationAbbrev);
	}
	
	public Corporation getCorporationByName (String aCorporationName) {
		return companies.getCorporationByName (aCorporationName);
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

	public int getCountOfOpen () {
		return companies.getCountOfOpen ();
	}

	public int getCountOfOperatingCompanies () {
		return companies.getCountOfOperatingCompanies ();
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
		int tTotalCorpCash;
		int tCompanyCount;

		tTotalCorpCash = 0;
		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCompanyCount = companies.getRowCount ();

			if (tCompanyCount > 0) {
				tTotalCorpCash = companies.getTotalCorpCash ();
			}
		}

		return tTotalCorpCash;
	}
	
	public int getSelectedTrainCount () {
		int tSelectedTrainCount;
		String tActiveCompanyAbbrev;
		
		tSelectedTrainCount = 0;
		
		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tActiveCompanyAbbrev = companies.getCurrentlyOperatingAbbrev ();
			tSelectedTrainCount = companies.getSelectedTrainCount (tActiveCompanyAbbrev);
		}
		
		return tSelectedTrainCount;
	}
	
	public TrainHolderI getSelectedTrainHolder () {
		TrainHolderI tSelectedTrainHolder;
		String tActiveCompanyAbbrev;
		
		tSelectedTrainHolder = TrainHolderI.NO_TRAIN_HOLDER;
		
		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tActiveCompanyAbbrev = companies.getCurrentlyOperatingAbbrev ();
			tSelectedTrainHolder = companies.getSelectedTrainHolder (tActiveCompanyAbbrev);
		}
		
		return tSelectedTrainHolder;
	}
}
