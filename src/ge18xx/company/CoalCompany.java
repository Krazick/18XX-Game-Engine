package ge18xx.company;

import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class CoalCompany extends TrainCompany {

	//
	//  CoalCompany.java
	//  Game_18XX
	//
	//  Created by Mark Smith on 12/31/07.
	//  Copyright 2007 __MyCompanyName__. All rights reserved.
	//

	public static final ElementName EN_COAL_COMPANY = new ElementName ("CoalCompany");
		
	public CoalCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
	}
		
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
			
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
			
		return tCurrentColumn;
	}
		
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn = aStartColumn;
		
		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
			
		return tCurrentColumn;
	}
		
	public ElementName getElementName () {
		return EN_COAL_COMPANY;
	}

	public String getOwner () {
		if (isOwned ()) {
			return getPresidentName ();
		} else {
			return NO_PRESIDENT;
		}
	}
	
	@Override
	public String getStatusName () {
		return super.getStatusName ();
	}
		
	@Override
	public String getType () {
		return COAL_COMPANY;
	}
	
	@Override
	public boolean isACoalCompany () {
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
}
