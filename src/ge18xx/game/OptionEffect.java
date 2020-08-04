package ge18xx.game;

import org.w3c.dom.NodeList;

import ge18xx.train.TrainInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class OptionEffect {
	static final String NO_NAME = "<NO NAME>";
	static final int NO_QUANTITY = -1;
	static final AttributeName AN_EFFECT_NAME = new AttributeName ("trainName");
	static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final AttributeName AN_PHASE_NAME = new AttributeName ("phaseName");
	static final AttributeName AN_CELL_NAME = new AttributeName ("cellName");
	static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final ElementName EN_OPTION_EFFECT = new ElementName ("OptionEffect");
	public static final ElementName EN_OPTION_EFFECTS = new ElementName ("OptionEffects");
	public static final String SET_TRAIN_QUANTITY = "Set Train Quantity";
	public static final String MUST_BUY_TRAIN = "Must Buy Train";
	public static final String ADD_TO_BANK = "Add To Bank";
	public static final String REMOVE_PHASE = "Remove Phase";
	public static final String ADD_TRAIN = "Add Train";
	public static final String END_GAME_ON_STOCK_CELL = "End Game on Stock Cell";
	String name;
	String actorName;
	String phaseName;
	String cellName;
	TrainInfo train;
	int quantity;
	boolean state;
	
	public OptionEffect () {
		setValue (NO_NAME, NO_NAME, NO_QUANTITY, NO_NAME, NO_NAME);
	}
	
	public OptionEffect (XMLNode aCellNode) {
		String tName, tTrainName, tPhaseName, tCellName;
		int tQuantity, tTrainCount, tIndex;
		NodeList tTrainChildren;
		XMLNode tTrainNode;
		String tTrainChildName;
		
		tName = aCellNode.getThisAttribute (AN_NAME);
		if (tName.equals (SET_TRAIN_QUANTITY)) {
			tTrainName = aCellNode.getThisAttribute (AN_EFFECT_NAME);
			tQuantity = aCellNode.getThisIntAttribute (AN_QUANTITY);
			setValue (tName, tTrainName, tQuantity, NO_NAME, NO_NAME);
		} else if (tName.equals (ADD_TO_BANK)) {
			tQuantity = aCellNode.getThisIntAttribute (AN_QUANTITY);
			setValue (tName, "BANK", tQuantity, NO_NAME, NO_NAME);
		} else if (tName.equals (REMOVE_PHASE)) {
			tPhaseName = aCellNode.getThisAttribute (AN_PHASE_NAME);
			setValue (tName, "GAME", NO_QUANTITY, tPhaseName, NO_NAME);
		} else if (tName.equals (END_GAME_ON_STOCK_CELL)) {
			tCellName = aCellNode.getThisAttribute (AN_CELL_NAME);
			setValue (tName, "GAME", NO_QUANTITY, NO_NAME, tCellName);
		} else if (tName.equals (ADD_TRAIN)) {
			setValue (tName, "GAME", NO_QUANTITY, NO_NAME, NO_NAME);
			tTrainChildren = aCellNode.getChildNodes ();
			tTrainCount = tTrainChildren.getLength ();
			for (tIndex = 0; tIndex < tTrainCount; tIndex++) {
				tTrainNode = new XMLNode (tTrainChildren.item (tIndex));
				tTrainChildName = tTrainNode.getNodeName ();
				if ("Train".equals (tTrainChildName)) {
					train = new TrainInfo (tTrainNode);						
				}
			}
		} else if (tName.equals (MUST_BUY_TRAIN)) {
			setValue (tName, "GAME", true);
		} else {
			setValue (tName, NO_NAME, NO_QUANTITY, NO_NAME, NO_NAME);
		}
	}
	
	public String getCellName () {
		return cellName;
	}
	
	public boolean getState () {
		return state;
	}
	
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tTrainElement;

		tXMLElement = aXMLDocument.createElement (EN_OPTION_EFFECT);
		tXMLElement.setAttribute (AN_NAME, name);
		if (actorName != null) {
			if (!(actorName.equals (NO_NAME))) {
				tXMLElement.setAttribute (AN_EFFECT_NAME, actorName);
			}
		}
		if (quantity != NO_QUANTITY) {
			tXMLElement.setAttribute (AN_QUANTITY, quantity);
		}
		if (phaseName != NO_NAME) {
			tXMLElement.setAttribute (AN_PHASE_NAME, phaseName);
		}
		if (train != null) {
			tTrainElement = train.getTrainInfoElement (aXMLDocument);
			tXMLElement.appendChild (tTrainElement);
		}
		if (cellName != NO_NAME) {
			tXMLElement.setAttribute (AN_CELL_NAME, cellName);
		}
		if (name.equals (MUST_BUY_TRAIN)) {
			tXMLElement.setAttribute (AN_MUST_BUY_TRAIN, state);
		}
		
		return tXMLElement;
	}
	
	public String getName () {
		return name;
	}
	
	public String getPhaseName () {
		return phaseName;
	}
	
	public int getQuantity () {
		return quantity;
	}
	
	public TrainInfo getTrainInfo () {
		return train;
	}
	
	public String getTrainName () {
		return actorName;
	}
	
	private void setValue (String aEffectName, String aActorName, boolean aState) {
		name = aEffectName;
		actorName = aActorName;
		state = aState;
	}
	
	private void setValue (String aEffectName, String aActorName, int aQuantity, String aPhaseName, String aCellName) {
		name = aEffectName;
		actorName = aActorName;
		quantity = aQuantity;
		phaseName = aPhaseName;
		cellName = aCellName;
	}
}
