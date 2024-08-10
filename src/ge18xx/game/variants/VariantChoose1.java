package ge18xx.game.variants;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import geUtilities.xml.XMLNode;

public class VariantChoose1 extends Variant {
	public static final String TITLE = "Variant Choose 1";

	public VariantChoose1 () {
		setTitle (TITLE);
	}

	public VariantChoose1 (XMLNode aXMLNode) {
		super (aXMLNode);
	}

	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescScrollPane;

		tDescScrollPane = buildVariantDescription (VariantEffect.ComponentType.RADIO_BUTTON);

		return tDescScrollPane;
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

	@Override
	public boolean selectActiveVariantEffects (VariantEffect aVariantEffect) {
		boolean tSelected;
		JRadioButton tRadioButton;

		tSelected = false;
		if (aVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
			for (VariantEffect tVariantEffect : variantEffects) {
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					if (tVariantEffect.getID () == aVariantEffect.getID ()) {
						tRadioButton = (JRadioButton) tVariantEffect.getEffectComponent ();
						tRadioButton.setSelected (true);
						tSelected = true;
					}
				}
			}
		} else {
			System.err.println ("Passed in VariantEffect is NULL");
		}

		return tSelected;
	}

}
