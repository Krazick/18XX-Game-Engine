package ge18xx.game.variants;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.phase.PhaseInfo;
import ge18xx.phase.PhaseManager;
import ge18xx.utilities.XMLNode;

public class AddPhaseVEffect extends PhaseInfoVEffect {
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
