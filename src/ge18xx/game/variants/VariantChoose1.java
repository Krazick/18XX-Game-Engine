package ge18xx.game.variants;

import java.awt.event.ItemListener;

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
	public JPanel buildVariantDescription (ItemListener aItemListener) {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.RADIO_BUTTON, aItemListener);
		
		return tDescPanel;
	}
}
