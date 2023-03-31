package ge18xx.toplevel;

//
//  PrivatesFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 1/1/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.PrivateCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.round.RoundManager;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

public class PrivatesFrame extends CorporationTableFrame {
	public static final String BASE_TYPE = CorporationList.TYPE_NAMES [0].toString ();
	public static final String BASE_TITLE = BASE_TYPE + " " + Corporation.COMPANIES;
	public static final PrivatesFrame NO_PRIVATES_FRAME = null;
	public static final ElementName EN_PRIVATES = new ElementName (BASE_TYPE + "s");
	private static final long serialVersionUID = 1L;

	public PrivatesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [0], aRoundManager);
	}

	@Override
	public boolean anyPrivatesUnowned () {
		return companies.anyPrivatesUnowned ();
	}

	@Override
	public void applyCloseToPrivates () {
		companies.applyClose ();
	}

	@Override
	public void removeAllBids () {
		companies.removeAllBids ();
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivatesJPanel;

		tPrivatesJPanel = companies.buildCompaniesForPurchaseJPanel (aItemListener, BASE_TYPE, aAvailableCash);

		return tPrivatesJPanel;
	}

	@Override
	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_PRIVATES));
	}

	public boolean gameHasPrivates () {
		if (getCountOfCompanies () > 0) {
			return true;
		} else {
			return false;
		}
	}

	public PrivateCompany getPrivateCompany (String aCompanyAbbrev) {
		PrivateCompany tPrivateCompany;

		tPrivateCompany = (PrivateCompany) companies.getCorporation (aCompanyAbbrev);

		return tPrivateCompany;
	}

	public int getTotalEscrow () {
		int tTotalEscrow = 0;

		tTotalEscrow = companies.getTotalEscrow ();

		return tTotalEscrow;
	}

	public Benefit findBenefit (String aBenefitName) {
		return companies.findBenefit (aBenefitName);
	}
	
	public void printCompanies () {
		companies.printReport ();
	}
}
