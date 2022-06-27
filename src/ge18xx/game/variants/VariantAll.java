package ge18xx.game.variants;

import java.awt.event.ItemListener;

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
	public JPanel buildVariantDescription (ItemListener aItemListener) {
		JPanel tDescPanel;
		
		tDescPanel = buildVariantDescription (VariantEffect.ComponentType.JLABEL, aItemListener);
		
		return tDescPanel;
	}

}
