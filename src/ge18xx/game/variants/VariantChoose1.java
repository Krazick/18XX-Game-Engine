package ge18xx.game.variants;

import java.util.List;

import javax.swing.JPanel;

import ge18xx.utilities.XMLNode;

public class VariantChoose1 extends Variant {
	static final String TITLE = "Variant Choose 1";

	public VariantChoose1 () {
		setTitle (TITLE);
	}

	public VariantChoose1 (XMLNode aXMLNode) {
		super (aXMLNode);
	}
	
	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.RADIO_BUTTON);
		
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
