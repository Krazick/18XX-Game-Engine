package ge18xx.game.variants;

import ge18xx.game.GameManager;
import geUtilities.XMLNode;

public class RemovePhaseVEffect extends PhaseInfoVEffect {
	static final String NAME = "Remove Phase";

	public RemovePhaseVEffect () {
		super (NAME);
	}

	public RemovePhaseVEffect (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}

	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 *
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 *
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		System.out.println (NAME + " being Applied");
	}

}
