package ge18xx.game.variants;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class AddPhaseVEffect extends PhaseInfoVEffect {
	public static final AttributeName AN_PHASE_NAME = new AttributeName ("phaseName");
	static final String NAME = "Add Phase";
	PhaseInfo phaseInfo;

	public AddPhaseVEffect () {
		super (NAME);
	}

	public AddPhaseVEffect (XMLNode aVariantEffectNode) {
		super (aVariantEffectNode);
		setName (NAME);
		loadPhaseInfo (aVariantEffectNode);
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement by using the super-class and then stores
	 * the CompanyID and the VariantEffect Class
	 *
	 * @param aXMLDocument The XMLDocumdnt to use to create the XMLElement
	 *
	 * @return the filled out XMLElement
	 *
	 */
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tXMLPhaseElement;

		tXMLElement = super.getEffectElement (aXMLDocument);
		tXMLElement.setAttribute (AN_PHASE_NAME, phaseInfo.getFullName ());
		tXMLPhaseElement = phaseInfo.getElement (aXMLDocument);
		tXMLElement.appendChild (tXMLPhaseElement);
		tXMLElement.setAttribute (AN_CLASS, getClass ().getName ());

		return tXMLElement;
	}

	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 *
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 *
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		PhaseManager tPhaseManager;

		System.out.println (NAME + " being Applied" + " Have Phase " + phaseInfo.getName () + "." +
					phaseInfo.getSubName () );
		tPhaseManager = aGameManager.getPhaseManager ();
		tPhaseManager.addPhase (phaseInfo);
	}

	public void setPhaseInfo (PhaseInfo aPhaseInfo) {
		phaseInfo = aPhaseInfo;
	}

	public void loadPhaseInfo (XMLNode aVariantEffectNode) {
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex;
		int tChildrenCount;
		PhaseInfo tPhaseInfo;

		tChildren = aVariantEffectNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (PhaseInfo.EN_PHASE.equals (tChildName)) {
				tPhaseInfo = new PhaseInfo (tChildNode);
				if (tPhaseInfo != PhaseInfo.NO_PHASE_INFO) {
					setPhaseInfo (tPhaseInfo);
				}
			}
		}
	}
}
