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

import ge18xx.company.CorporationList;
import ge18xx.round.RoundManager;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class PrivatesFrame extends CorporationTableFrame {
	public static final ElementName EN_PRIVATES = new ElementName (CorporationList.TYPE_NAMES [0] + "s");
	private static final long serialVersionUID = 1L;
	
	public PrivatesFrame (String aFrameName, RoundManager aRoundManager) {
		super (aFrameName, CorporationList.TYPE_NAMES [0], aRoundManager);
	}
	
	public boolean anyPrivatesUnowned () {
		return companies.anyPrivatesUnowned ();
	}
	
	public void applyCloseToPrivates () {
		companies.applyClose ();
	}
	
	public void removeAllBids () {
		companies.removeAllBids ();
	}

	public JPanel buildPrivatesForPurchaseJPanel (ItemListener aItemListener, int aAvailableCash) {
		JPanel tPrivatesJPanel;
		
		tPrivatesJPanel = companies.buildCompaniesForPurchaseJPanel (aItemListener, 
				CorporationList.TYPE_NAMES [0].toString (), aAvailableCash);
		
		return tPrivatesJPanel;
	}

	public XMLElement createPrivatesListDefinitions (XMLDocument aXMLDocument) {
		return (super.createCompaniesListDefinitions (aXMLDocument));
	}

	public XMLElement getCorporationStateElements (XMLDocument aXMLDocument) {
		return (super.getCorporationStateElements (aXMLDocument, EN_PRIVATES));
	}
	
	public int getCountOfSelectedCertificates () {
		return super.getCountOfSelectedCertificates ();
	}
	
	public boolean gameHasPrivates () {
		if (getCountOfPrivates () > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getCountOfOpenPrivates () {
		return (super.getCountOfOpenCompanies ());
	}
	
	public int getCountOfPlayerOwnedPrivates () {
		return (super.getCountOfPlayerOwnedCompanies ());
	}

	public int getCountOfPrivates () {
		return (super.getCountOfCompanies ());
	}

	public CorporationList getPrivates () {
		return (super.getCompanies ());
	}
	
	public void loadPrivatesStates (XMLNode aXMLNode) {
		super.loadStates (aXMLNode);
	}
}
