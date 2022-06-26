package ge18xx.game.variants;

import ge18xx.utilities.XMLNode;

public class VariantChooseAny extends Variant {
	static final String TITLE = "Variant Any";

	public VariantChooseAny () {
		setTitle (TITLE);
	}

	public VariantChooseAny (XMLNode aCellNode) {
		super (aCellNode);
		// TODO Auto-generated constructor stub
	}

}
