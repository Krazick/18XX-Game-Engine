package ge18xx.game.variants;

import ge18xx.utilities.XMLNode;

public class AddTrainVEffect extends VariantEffect {
	static final String NAME = "Add Train";
	
	
	public AddTrainVEffect () {
		setName (NAME);
	}
	
	public AddTrainVEffect (XMLNode aCellNode) {
		super (aCellNode);
		
		String tTrainName;
		int tQuantity;
	}

}
