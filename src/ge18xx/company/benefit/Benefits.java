package ge18xx.company.benefit;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.NodeList;

import ge18xx.company.PrivateCompany;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Benefits {
	public final static ElementName EN_BENEFITS = new ElementName ("Benefits");
	public final static Benefits NO_BENEFITS = null;
	List<Benefit> benefits;
	
	public Benefits (XMLNode aBenefitsNode) {
		benefits = new LinkedList<Benefit> ();	
		parseBenefits (aBenefitsNode);
	}
	
	private void parseBenefits (XMLNode aBenefitsNode) {
		XMLNode tBenefitNode;
		NodeList tBenefitChildren;
		Benefit tBenefit;
		Class<?> tBenefitToLoad;
		Constructor<?> tBenefitConstructor;
		int tBenefitNodeCount, tBenefitIndex;
		String tBenefitNodeName;
		String tClassName;

		tBenefitChildren = aBenefitsNode.getChildNodes ();
		tBenefitNodeCount = tBenefitChildren.getLength ();
		try {
			for (tBenefitIndex = 0; tBenefitIndex < tBenefitNodeCount; tBenefitIndex++) {
				tBenefitNode = new XMLNode (tBenefitChildren.item (tBenefitIndex));
				tBenefitNodeName = tBenefitNode.getNodeName ();
				if (Benefit.EN_BENEFIT.equals (tBenefitNodeName)) {
					// Use Reflections to identify the OptionEffect to create, and call the constructor with the XMLNode and Game Manager
					tClassName = tBenefitNode.getThisAttribute (Benefit.AN_CLASS);
					tBenefitToLoad = Class.forName (tClassName);
					tBenefitConstructor = tBenefitToLoad.getConstructor (tBenefitNode.getClass ());
					tBenefit = (Benefit) tBenefitConstructor.newInstance (tBenefitNode);
					addBenefit (tBenefit);
				}
			}			
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}

	private void addBenefit (Benefit aBenefit) {
		benefits.add (aBenefit);
	}

	public void parseBenefitsStates (XMLNode aBenefitsNode) {
		int tBenefitNodeCount, tBenefitIndex;
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
		Benefit tMatchedBenefit = Benefit.NO_BENEFIT;
		
		for (Benefit tBenefit : benefits) {
			if (tMatchedBenefit == Benefit.NO_BENEFIT) {
				tMatchedBenefit = tBenefit.findMatchedBenefit (aBenefitNode);
			}
		}
		
		return tMatchedBenefit;
	}
	
	public JButton findButtonFor (JPanel aButtonRow, String aButtonLabel) {
		JButton tThisButton;
		JButton tFoundButton = Benefit.NO_BUTTON;
		Component tComponent;
		String tButtonText;
		int tComponentCount, tComponentIndex;
		
		tComponentCount = aButtonRow.getComponentCount ();
		if (tComponentCount > 0) {
			for (tComponentIndex = 0; tComponentIndex < tComponentCount; tComponentIndex++) {
				tComponent = aButtonRow.getComponent (tComponentIndex);
				if (tComponent instanceof JButton) {
					tThisButton = (JButton) tComponent;
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
		boolean tHasButtonFor = false;
		JButton tThisButton;
		
		tThisButton = findButtonFor (aButtonRow, aButtonLabel);
		if (tThisButton != Benefit.NO_BUTTON) {
			tHasButtonFor = true;
		}
				
		return tHasButtonFor;
	}
	
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		for (Benefit tBenefit : benefits) {
			tBenefit.configure (aPrivateCompany, aButtonRow);
		}
	}
	
	public boolean hasActiveCompanyBenefits () {
		boolean tHasActiveCompanyBenefits = false;
		
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActiveCompanyBenefit ()) {
				tHasActiveCompanyBenefits = true;
			}
		}
		
		return tHasActiveCompanyBenefits;
	}
	
	public boolean hasActivePlayerBenefits () {
		boolean tHasActivePlayerBenefits = false;
		
		for (Benefit tBenefit : benefits) {
			if (tBenefit.isActivePlayerBenefit ()) {
				tHasActivePlayerBenefits = true;
			}
		}
		
		return tHasActivePlayerBenefits;
	}
	
	public int getCount () {
		return benefits.size ();
	}
	
	public XMLElement getBenefitsStateElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tXMLBenefitElement;
		
		tXMLElement = aXMLDocument.createElement (EN_BENEFITS);
		for (Benefit tBenefit : benefits) {
			tXMLBenefitElement = tBenefit.getCorporationStateElement (aXMLDocument);
			tXMLElement.appendChild (tXMLBenefitElement);
		}
		
		return tXMLElement;
	}
}
