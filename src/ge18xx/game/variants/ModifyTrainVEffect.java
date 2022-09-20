package ge18xx.game.variants;

import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.train.TrainInfo;
import ge18xx.utilities.XMLNode;

public class ModifyTrainVEffect extends VariantEffect {
	static final String NAME = "Modify Train";
	String attributeName;
	String value;
	String trainName;
	
	public ModifyTrainVEffect () {
		super ();
		setName (NAME);
	}

	public ModifyTrainVEffect (XMLNode aVariantEffectNode) {
		super (aVariantEffectNode);
		System.out.println ("Ready to read Attributes for Modify Train");
		String tAttributeName;
		String tValue;
		String tTrainName;
		
		tTrainName = aVariantEffectNode.getThisAttribute (TrainInfo.AN_NAME);
		tAttributeName = aVariantEffectNode.getThisAttribute (AN_ATTRIBUTE_NAME);
		tValue = aVariantEffectNode.getThisAttribute (AN_VALUE);
		setAttributeName (tAttributeName);
		setValue (tValue);
		setTrainName (tTrainName);
		switch (attributeName) {
			case "onLast" :
				System.out.println ("Set " + trainName + " Train's " + attributeName + " Attribute to " + value);
				break;
			default:
				System.out.println ("Don't know what to do with " + attributeName);
		}
	}

	public void setAttributeName (String aAttributeName) {
		attributeName = aAttributeName;
	}
	
	public void setTrainName (String aTrainName) {
		trainName = aTrainName;
	}
	
	public void setValue (String aValue) {
		value = aValue;
	}
	
	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 * 
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 * 
	 */
	@Override
	public void applyVariantEffect (GameManager aGameManager) {
		GameInfo tGameInfo;
		TrainInfo tTrainInfo;
		int tTrainInfoCount;
		int tTrainInfoIndex;
		String tTrainName;
		int tValue;
		
		tGameInfo = aGameManager.getActiveGame ();
		tTrainInfoCount = tGameInfo.getTrainCount ();
		for (tTrainInfoIndex = 0; tTrainInfoIndex < tTrainInfoCount; tTrainInfoIndex++) {
			tTrainInfo = tGameInfo.getTrainInfo (tTrainInfoIndex);
			tTrainName = tTrainInfo.getName ();
			if (tTrainName.equals (trainName)) {
				System.out.println ("Found a " + tTrainName + " resetting Attribute");
				switch (attributeName) {
					case "onLast":
						tValue = Integer.valueOf (value);
						tTrainInfo.setOnLastOrderAvailable (tValue);
						break;
					default:
						System.out.println ("Don't know what to do with " + attributeName);
				}
			}
		}	
	}
}
