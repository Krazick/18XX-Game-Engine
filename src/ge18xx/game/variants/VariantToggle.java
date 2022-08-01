package ge18xx.game.variants;

import ge18xx.game.GameManager;
import ge18xx.utilities.XMLNode;

public class VariantToggle extends VariantAll {
	public VariantToggle (XMLNode aXMLNode) {
		super (aXMLNode);
	}

	@Override
	public boolean isActive () {
		return true;
	}
	
	@Override
	public void applyVariantEffects (GameManager aGameManager) {
		boolean tIsSelected;
		
		tIsSelected = isSelected ();
		for (VariantEffect tEffect: variantEffects) {
			if (tEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tEffect.setState (tIsSelected);
				tEffect.applyVariantEffect (aGameManager);
			}
		}
	}
}
