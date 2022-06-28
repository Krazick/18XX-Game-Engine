package ge18xx.game.variants;

import java.util.List;

import javax.swing.JPanel;

import ge18xx.utilities.XMLNode;

public class VariantChooseAny extends Variant {
	static final String TITLE = "Variant Any";

	public VariantChooseAny () {
		setTitle (TITLE);
	}

	public VariantChooseAny (XMLNode aXMLNode) {
		super (aXMLNode);
	}
	
	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.CHECKBOX);
		
		return tDescPanel;
	}
	
	@Override
	public void addActiveVariantEffects (List<VariantEffect> aActiveVariantEffects) {
		for (VariantEffect tVariantEffect : variantEffects) {
			if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
				if (tVariantEffect.isSelected ()) {
					aActiveVariantEffects.add (tVariantEffect);
				}
			}
		}		
	}
}
