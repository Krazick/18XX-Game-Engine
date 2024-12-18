package ge18xx.company;

import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import swingTweaks.KButton;

//
//  MinorCompany.java
//  Game_18XX
//
//  Created by Mark Smith on 12/31/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class MinorCompany extends TokenCompany {
	public static final MinorCompany NO_MINOR_COMPANY = null;
	public static final ElementName EN_MINOR_COMPANY = new ElementName ("Minor");
	public static final AttributeName AN_FORM_UPGRADE = new AttributeName ("formUpgrade");
	public static final AttributeName AN_FORM_PHASE = new AttributeName ("formPhase");
	public static final AttributeName AN_UPGRADE_ID = new AttributeName ("upgradeID");
	public static final AttributeName AN_UPGRADE_PERCENTAGE = new AttributeName ("upgradePercentage");
	int upgradeToID;
	int upgradePercentage;
	boolean formUpgrade;
	String formPhase;

	public MinorCompany (XMLNode aChildNode, CorporationList aCorporationList) {
		super (aChildNode, aCorporationList);
		upgradeToID = aChildNode.getThisIntAttribute (AN_UPGRADE_ID);
		upgradePercentage = aChildNode.getThisIntAttribute (AN_UPGRADE_PERCENTAGE);
		formUpgrade = aChildNode.getThisBooleanAttribute (AN_FORM_UPGRADE);
		formPhase = aChildNode.getThisAttribute (AN_FORM_PHASE);
	}
	
	@Override
	public int addAllDataElements (CorporationList aCorporationList, int aRowIndex, int aStartColumn) {
		int tCurrentColumn;

		tCurrentColumn = aStartColumn;
		tCurrentColumn = super.addAllDataElements (aCorporationList, aRowIndex, tCurrentColumn);
		aCorporationList.addDataElement (getOwner (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getUpgradeTo (), aRowIndex, tCurrentColumn++);
		aCorporationList.addDataElement (getupgradePercentage (), aRowIndex, tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public int addAllHeaders (CorporationList aCorporationList, int aStartColumn) {
		int tCurrentColumn;

		tCurrentColumn = aStartColumn;
		tCurrentColumn = super.addAllHeaders (aCorporationList, tCurrentColumn);
		aCorporationList.addHeader ("Owner", tCurrentColumn++);
		aCorporationList.addHeader ("Upgrade To", tCurrentColumn++);
		aCorporationList.addHeader ("Upgrade %", tCurrentColumn++);

		return tCurrentColumn;
	}

	@Override
	public int fieldCount () {
		return super.fieldCount () + 3;
	}

	@Override
	public XMLElement getCorporationStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLCorporationState;

		tXMLCorporationState = aXMLDocument.createElement (EN_MINOR_COMPANY);
		getCorporationStateElement (tXMLCorporationState, aXMLDocument);
		super.appendOtherElements (tXMLCorporationState, aXMLDocument);

		return tXMLCorporationState;
	}

	@Override
	public ElementName getElementName () {
		return EN_MINOR_COMPANY;
	}
	
	@Override
	public JPanel buildCorpInfoJPanel () {
		JPanel tCorpInfoJPanel;
		JLabel tPrice;
		KButton tInfoButton;
		Certificate tCertificate;
		
		tCorpInfoJPanel = super.buildCorpInfoJPanel ();
		tPrice = new JLabel ("Price: " + Bank.formatCash (getValue ()));
		tCorpInfoJPanel.add (tPrice);
		
		tCertificate = corporationCertificates.getCertificate (0);
		tCertificate.setupInfoBuffon ();
		tInfoButton = tCertificate.getInfoButton ();
		tCorpInfoJPanel.add (tInfoButton);
		
		return tCorpInfoJPanel;
	}

	@Override
	public void floatCompany () {
		Token tMarketToken;
		
		super.floatCompany ();
		
		tMarketToken = tokens.getMarketToken ();
		tokens.setTokenUsed (tMarketToken, true);
	}
	
	@Override
	public int getAllowedTileLays () {
		int tAllowedTileLays;
		
		tAllowedTileLays = corporationList.getMinorTileLays ();
		
		return tAllowedTileLays;
	}

	public boolean canFormUpgrade () {
		return formUpgrade;
	}
	
	// Minor Company will override
	@Override
	public boolean canHoldDividend () {
		return false;
	}

	public String getFormPhase () {
		return formPhase;
	}
	
	public String getOwner () {
		String tOwnerName;
		
		if (isOwned ()) {
			tOwnerName = getPresidentName ();
		} else {
			tOwnerName = NO_PRESIDENT;
		}
		
		return tOwnerName;
	}

	@Override
	public String getStatusName () {
		return super.getStatusName ();
	}

	@Override
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
	public boolean isAMinorCompany () {
		return true;
	}
	
	@Override
	public boolean mustBuyTrain () {
		return false;
	}

	public boolean isOwned () {
		boolean isOwned;
		Certificate tCertificate;
		int tCertificateCount;
		int tCertificateIndex;

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

	@Override
	public void setTreasury (int aInitialTreasury) {
		addCash (aInitialTreasury);
	}

	@Override
	public JPanel buildPrivateCertJPanel (ItemListener aItemListener, int aAvailableCash) {
		return GUI.NO_PANEL;
	}

	@Override
	public int getCurrentValue () {
		return getValue ();
	}

	@Override
	public int calculateStartingTreasury () {
		return value;
	}
	
	@Override
	public boolean hasFloated () {
		boolean tHasFloated;
		
		if (status == ActorI.ActionStates.Owned) {
			tHasFloated = false;
		} else {
			tHasFloated = true;
		}
		
		return tHasFloated;
	}

	@Override
	public boolean shouldFloat () {
		boolean tShouldFloat;
		
		if ((status == ActorI.ActionStates.Closed) ||
			(status == ActorI.ActionStates.Unowned)) {
			tShouldFloat = false;
		} else {
			tShouldFloat = true;
		}
		
		return tShouldFloat;
	}
	
	protected void setBorder (Corporation aCorporation, JLabel tBenefitLabel) {
		Border tBorder;
		Border tBorder1;
		Border tBorder2;

		tBorder1 = BorderFactory.createLineBorder (aCorporation.getBgColor (), 2);
		tBorder2 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		tBorder = BorderFactory.createCompoundBorder (tBorder1, tBorder2);
		tBenefitLabel.setBorder(tBorder);
	}

	public JLabel getBenefitLabel () {
		JLabel tBenefitLabel;
		String tBenefitText;
		Corporation tUpgradeCorporation;
		GameManager tGameManager;
		 
		tGameManager = getGameManager ();
		tUpgradeCorporation = tGameManager.getCorporationByID (upgradeToID);
		if (tUpgradeCorporation == Corporation.NO_CORPORATION) {
			tBenefitLabel = GUI.NO_LABEL;
		} else {
			tBenefitText = "Upgrade to " + upgradePercentage + "% of " + tUpgradeCorporation.getAbbrev ();		
			tBenefitLabel = new JLabel (tBenefitText);
			setBorder (tUpgradeCorporation, tBenefitLabel);
		}
		
		return tBenefitLabel;
	}
}
