package ge18xx.company.benefit;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.w3c.dom.NodeList;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import geUtilities.GUI;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;
import swingTweaks.KButton;

public class Benefits {
	public static final ElementName EN_BENEFITS = new ElementName ("Benefits");
	public static final Benefits NO_BENEFITS = null;
	List<Benefit> benefits;

	public Benefits (XMLNode aBenefitsNode, Corporation aCorporation) {
		benefits = new LinkedList<> ();
		parseBenefits (aBenefitsNode, aCorporation);
	}

	public List<Benefit> getBenefits () {
		return benefits;
	}
	
	private void parseBenefits (XMLNode aBenefitsNode, Corporation aCorporation) {
		XMLNode tBenefitNode;
		NodeList tBenefitChildren;
		Benefit tBenefit;
		Class<?> tBenefitToLoad;
		Constructor<?> tBenefitConstructor;
		int tBenefitNodeCount;
		int tBenefitIndex;
		String tBenefitNodeName;
		String tClassName;

		tBenefitChildren = aBenefitsNode.getChildNodes ();
		tBenefitNodeCount = tBenefitChildren.getLength ();
		tBenefitNodeName = "NO BENEFIT YET";
		tClassName = "No Class Yet";
		try {
			for (tBenefitIndex = 0; tBenefitIndex < tBenefitNodeCount; tBenefitIndex++) {
				tBenefitNode = new XMLNode (tBenefitChildren.item (tBenefitIndex));
				tBenefitNodeName = tBenefitNode.getNodeName ();
				if (Benefit.EN_BENEFIT.equals (tBenefitNodeName)) {
					// Use Reflections to identify the Benefit to create, and call the
					// constructor with the XMLNode and Game Manager
					tClassName = tBenefitNode.getThisAttribute (Benefit.AN_CLASS);
					tBenefitToLoad = Class.forName (tClassName);
					tBenefitConstructor = tBenefitToLoad.getConstructor (tBenefitNode.getClass ());
					tBenefit = (Benefit) tBenefitConstructor.newInstance (tBenefitNode);
					tBenefit.setCorporation (aCorporation);
					addBenefit (tBenefit);
				}
			}
		} catch (Exception tException) {
			System.err.println ("Caught Exception for Benefit Class " + tClassName
						+ " with Stack Trace");
			tException.printStackTrace ();
		}
	}

	private void addBenefit (Benefit aBenefit) {
		benefits.add (aBenefit);
	}

	public void parseBenefitsStates (XMLNode aBenefitsNode) {
		int tBenefitNodeCount;
		int tBenefitIndex;
		String tBenefitNodeName;
		XMLNode tBenefitNode;
		NodeList tBenefitChildren;
		Benefit tMatchedBenefit;

		tBenefitChildren = aBenefitsNode.getChildNodes ();
		tBenefitNodeCount = tBenefitChildren.getLength ();
		try {
			for (tBenefitIndex = 0; tBenefitIndex < tBenefitNodeCount; tBenefitIndex++) {
				tBenefitNode = new XMLNode (tBenefitChildren.item (tBenefitIndex));
				tBenefitNodeName = tBenefitNode.getNodeName ();
				if (Benefit.EN_BENEFIT.equals (tBenefitNodeName)) {
					tMatchedBenefit = findMatchedBenefit (tBenefitNode);
					if (tMatchedBenefit != Benefit.NO_BENEFIT) {
						tMatchedBenefit.updateState (tBenefitNode);
					}
				}
			}
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}

	protected Benefit findMatchedBenefit (XMLNode aBenefitNode) {
		Benefit tMatchedBenefit;

		tMatchedBenefit = Benefit.NO_BENEFIT;
		for (Benefit tBenefit : benefits) {
			if (tMatchedBenefit == Benefit.NO_BENEFIT) {
				tMatchedBenefit = tBenefit.findMatchedBenefit (aBenefitNode);
			}
		}

		return tMatchedBenefit;
	}

	public void getOwnerTypeBenefits (List<Benefit> aOwnerTypeBenefits) {
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isOwnerTypeBenefit ()) {
				aOwnerTypeBenefits.add (tBenefit);
			}
		}
	}
	
	public KButton findButtonFor (JPanel aButtonRow, String aButtonLabel) {
		KButton tThisButton;
		KButton tFoundButton;
		Component tComponent;
		String tButtonText;
		int tComponentCount;
		int tComponentIndex;

		tFoundButton = GUI.NO_BUTTON;
		tComponentCount = aButtonRow.getComponentCount ();
		if (tComponentCount > 0) {
			for (tComponentIndex = 0; tComponentIndex < tComponentCount; tComponentIndex++) {
				tComponent = aButtonRow.getComponent (tComponentIndex);
				if (tComponent instanceof KButton) {
					tThisButton = (KButton) tComponent;
					tButtonText = tThisButton.getText ();
					if (aButtonLabel.equals (tButtonText)) {
						tFoundButton = tThisButton;
					}
				}
			}
		}

		return tFoundButton;
	}

	public boolean hasButtonFor (JPanel aButtonRow, String aButtonLabel) {
		boolean tHasButtonFor;
		KButton tThisButton;

		tThisButton = findButtonFor (aButtonRow, aButtonLabel);
		if (tThisButton != GUI.NO_BUTTON) {
			tHasButtonFor = true;
		} else {
			tHasButtonFor = false;
		}

		return tHasButtonFor;
	}

	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		for (Benefit tBenefit : benefits) {
			tBenefit.configure (aPrivateCompany, aButtonRow);
		}
	}

	public void removeBenefitButtons () {
		JPanel tButtonRow;

		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActivePlayerBenefit ()) {
				tBenefit.removeButton ();
			}
			if (tBenefit.isActiveCompanyBenefit ()) {
				tButtonRow = tBenefit.getButtonPanel ();
				tBenefit.removeButton (tButtonRow);
			}
		}
	}

	public void removeBenefitButtons (JPanel aButtonRow) {
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActiveCompanyBenefit ()) {
				tBenefit.removeButton (aButtonRow);
			}
		}
	}

	public void addAllActorsBenefitButtons (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isAllActorsCompanyBenefit ()) {
				tBenefit.configure (aPrivateCompany, aButtonRow);
			}
		}
	}
	
	public boolean hasActiveCompanyBenefits () {
		boolean tHasActiveCompanyBenefits;

		tHasActiveCompanyBenefits = false;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActiveCompanyBenefit ()) {
				tHasActiveCompanyBenefits = true;
			}
		}

		return tHasActiveCompanyBenefits;
	}

	public boolean hasActivePlayerBenefits () {
		boolean tHasActivePlayerBenefits;

		tHasActivePlayerBenefits = false;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActivePlayerBenefit ()) {
				tHasActivePlayerBenefits = true;
			}
		}

		return tHasActivePlayerBenefits;
	}

	public void enableBenefit (String aBenefitName) {
		QueryExchangeBenefit tQueryExchangeBenefit;
		String tFoundBenefitName;

		for (Benefit tBenefit : benefits) {
			tFoundBenefitName = tBenefit.getName ();
			if (tFoundBenefitName.equals (aBenefitName)) {
				tQueryExchangeBenefit = (QueryExchangeBenefit) tBenefit;
				tQueryExchangeBenefit.setUsed (false);
			}
		}
	}

	public void disableBenefit (String aBenefitName) {
		QueryExchangeBenefit tQueryExchangeBenefit;
		String tFoundBenefitName;

		for (Benefit tBenefit : benefits) {
			tFoundBenefitName = tBenefit.getName ();
			if (tFoundBenefitName.equals (aBenefitName)) {
				tQueryExchangeBenefit = (QueryExchangeBenefit) tBenefit;
				tQueryExchangeBenefit.setUsed (true);
			}
		}
	}

	public void handleQueryBenefits (JFrame aRoundFrame) {
		QueryExchangeBenefit tQueryExchangeBenefit;

		for (Benefit tBenefit : benefits) {
			if (tBenefit instanceof QueryExchangeBenefit) {
				tQueryExchangeBenefit = (QueryExchangeBenefit) tBenefit;
				tQueryExchangeBenefit.handleBenefit (aRoundFrame);
			}
		}
	}

	public Benefit getBenefitNamed (String aBenefitName) {
		Benefit tFoundBenefit;
		String tThisBenefitName;

		tFoundBenefit = Benefit.NO_BENEFIT;
		for (Benefit tBenefit : benefits) {
			tThisBenefitName = tBenefit.getName ();
			if (tThisBenefitName.equals (aBenefitName)) {
				tFoundBenefit = tBenefit;
			}
		}

		return tFoundBenefit;
	}

	public int getCount () {
		return benefits.size ();
	}

	public XMLElement getBenefitsStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLBenefitElement;

		tXMLElement = aXMLDocument.createElement (EN_BENEFITS);
		for (Benefit tBenefit : benefits) {
			tXMLBenefitElement = tBenefit.getCorporationStateElement (aXMLDocument);
			tXMLElement.appendChild (tXMLBenefitElement);
		}

		return tXMLElement;
	}

	public Benefit findBenefit (String aBenefitName) {
		Benefit tFoundBenefit;

		tFoundBenefit = Benefit.NO_BENEFIT;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.getName ().equals (aBenefitName)) {
				tFoundBenefit = tBenefit;
			}
		}

		return tFoundBenefit;
	}
	
	public boolean hasAnyPassiveCompanyBenefits () {
		boolean tHasAnyPassiveCompanyBenefits;
		
		tHasAnyPassiveCompanyBenefits = false;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isPassiveCompanyBenefit ()) {
				tHasAnyPassiveCompanyBenefits = true;
			}
		}
		
		return tHasAnyPassiveCompanyBenefits;
	}
	
	public boolean hasAnyPassivePlayerBenefits () {
		boolean tHasAnyPassivePlayerBenefits;
		
		tHasAnyPassivePlayerBenefits = false;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isPassivePlayerBenefit ()) {
				tHasAnyPassivePlayerBenefits = true;
			}
		}
		
		return tHasAnyPassivePlayerBenefits;
	}
	
	public PassiveEffectBenefit getUnusedPassiveCompanyBenefit () {
		PassiveEffectBenefit tPassiveBenefit;
		
		tPassiveBenefit = (PassiveEffectBenefit) Benefit.NO_BENEFIT;
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isPassiveCompanyBenefit ()) {
				if (! tBenefit.used ()) {
					tPassiveBenefit = (PassiveEffectBenefit) tBenefit;
				}
			}
		}
		
		return tPassiveBenefit;
	}
}
