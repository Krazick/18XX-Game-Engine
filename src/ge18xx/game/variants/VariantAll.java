package ge18xx.game.variants;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import geUtilities.XMLNode;

public class VariantAll extends Variant {
	static final String TITLE = "Variant All";

	public VariantAll () {
		setTitle (TITLE);
	}

	public VariantAll (XMLNode aXMLNode) {
		super (aXMLNode);
		applyEnabledToEffects ();
	}

	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescPanel;

		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.JLABEL);

		return tDescPanel;
	}

	private void applyEnabledToEffects () {
		for (VariantEffect tVariantEffect : variantEffects) {
			if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tVariantEffect.setState (enabled);
			}
		}
	}

	@Override
	public void addActiveVariantEffects (List<VariantEffect> aActiveVariantEffects) {
		boolean tIsSelected;

		if (isActive ()) {
			tIsSelected = isSelected ();
			for (VariantEffect tVariantEffect : variantEffects) {
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					tVariantEffect.setState (tIsSelected);
					aActiveVariantEffects.add (tVariantEffect);
				}
			}
		} else {
			for (VariantEffect tVariantEffect : variantEffects) {
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					tVariantEffect.setState (false);
				}
			}
		}
	}

	@Override
	public boolean selectActiveVariantEffects (VariantEffect aVariantEffect) {
		boolean tSelected;
		JCheckBox tTitle;

		tSelected = true;
		tTitle = (JCheckBox) titleComponent;
		tTitle.setSelected (true);
		aVariantEffect.setState (tSelected);

		return tSelected;
	}
}
