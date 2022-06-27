package ge18xx.game.variants;

import java.awt.event.ItemListener;

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
	public JPanel buildVariantDescription (ItemListener aItemListener) {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.CHECKBOX, aItemListener);
		
		return tDescPanel;
	}
}
