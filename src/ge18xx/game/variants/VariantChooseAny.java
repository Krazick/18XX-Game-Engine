package ge18xx.game.variants;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import geUtilities.xml.XMLNode;

public class VariantChooseAny extends Variant {
	public static final String TITLE = "Variant Any";

	public VariantChooseAny () {
		setTitle (TITLE);
	}

	public VariantChooseAny (XMLNode aXMLNode) {
		super (aXMLNode);
	}

	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescScrollPane;

		tDescScrollPane = buildVariantDescription (VariantEffect.ComponentType.CHECKBOX);

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
		JCheckBox tCheckBox;

		tSelected = false;
		if (aVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
			for (VariantEffect tVariantEffect : variantEffects) {
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					if (tVariantEffect.getID () == aVariantEffect.getID ()) {
						tCheckBox = (JCheckBox) tVariantEffect.getEffectComponent ();
						tCheckBox.setSelected (true);
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
