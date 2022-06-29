package ge18xx.game.variants;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ge18xx.utilities.XMLNode;

public class VariantAll extends Variant {
	static final String TITLE = "Variant All";

	public VariantAll () {
		setTitle (TITLE);
	}

	public VariantAll (XMLNode aXMLNode) {
		super (aXMLNode);
	}
	
	@Override
	public JPanel buildVariantDescription () {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.JLABEL);
		
		return tDescPanel;
	}
	
	@Override
	public void addActiveVariantEffects (List<VariantEffect> aActiveVariantEffects) {
		if (isActive ()) {
			for (VariantEffect tVariantEffect : variantEffects) {
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					aActiveVariantEffects.add (tVariantEffect);
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
		
		return tSelected;
	}

}
