package ge18xx.company;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;


//
//  MinorCompany.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class MinorCompany extends TokenCompany {
	public static final ElementName EN_MINOR_COMPANY = new ElementName ("Minor");
	static final AttributeName AN_UPGRADE_ID = new AttributeName ("upgradeID");
	static final AttributeName AN_UPGRADE_PERCENTAGE = new AttributeName ("upgradePercentage");
	int upgradeToID;
	int upgradePercentage;
	
	public MinorCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		upgradeToID = aChildNode.getThisIntAttribute (AN_UPGRADE_ID);
		upgradePercentage = aChildNode.getThisIntAttribute (AN_UPGRADE_PERCENTAGE);
	}
	
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getOwner (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getUpgradeTo (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getupgradePercentage (), aRowIndex, tCurrentColumn++);
		
		return tCurrentColumn;
	}
	
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Owner", tCurrentColumn++);
		aCorporationList.addHeader ("Upgrade To", tCurrentColumn++);
		aCorporationList.addHeader ("Upgrade %", tCurrentColumn++);
		
		return tCurrentColumn;
	}

	public int fieldCount () {
		return super.fieldCount () + 3;
	}

	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;
		
		tXMLCorporationState = aXMLDocument.createElement (EN_MINOR_COMPANY);
		getCorporationStateElement (tXMLCorporationState);

		return tXMLCorporationState;
	}
	
	public ElementName getElementName () {
		return EN_MINOR_COMPANY;
	}

	public String getOwner () {
		if (isOwned ()) {
			return getPresidentName ();
		} else {
			return NO_PRESIDENT;
		}
	}
	
	public String getStatusName () {
		return super.getStatusName ();
	}
	
	public String getType () {
		return MINOR_COMPANY;
	}
	
	public int getUpgradeTo () {
		return upgradeToID;
	}
	
	public int getupgradePercentage () {
		return upgradePercentage;
	}
	
	@Override
	public boolean isMinorCompany () {
		return true;
	}

	public boolean isOwned () {
		boolean isOwned;
		Certificate tCertificate;
		int tCertificateCount, tCertificateIndex;
		
		isOwned = false;
		tCertificateCount = corporationCertificates.getCertificateCountAgainstLimit ();
		for (tCertificateIndex = 0; tCertificateIndex < tCertificateCount; tCertificateIndex++) {
			tCertificate = corporationCertificates.getCertificate (tCertificateIndex);
			if (tCertificate.isOwned ()) {
				isOwned = true;
			}
		}
		
		return (isOwned);
	}
	
	public void setTreasury (int aInitialTreasury) {
		addCash (aInitialTreasury);
	}

	@Override
	protected boolean isPlaceTileMode () {
		return false;
	}

	@Override
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		// TODO Auto-generated method stub
		return null;
	}
}
