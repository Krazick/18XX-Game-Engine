package ge18xx.company.benefit;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NodeList;

import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class Benefits {
	public final static ElementName EN_BENEFITS = new ElementName ("Benefits");
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
			System.out.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}
	}

	private void addBenefit (Benefit aBenefit) {
		benefits.add (aBenefit);
	}
}
