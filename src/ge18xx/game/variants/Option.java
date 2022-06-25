package ge18xx.game.variants;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Option {
	static final String NO_TITLE = "<NO TITLE>";
	public static final OptionEffect NO_OPTION_EFFECT = null;
	public static final Option NO_OPTION = null;
	public static final Option [] NO_OPTIONS = null;
	public static final ElementName EN_OPTION = new ElementName ("Option");
	public static final ElementName EN_OPTIONS = new ElementName ("Options");
	static final AttributeName AN_TITLE = new AttributeName ("title");
	static final AttributeName AN_TYPE = new AttributeName ("type");
	static final String TYPE_ALL = "ALL";
	static final String TYPE_CHOOSE_ANY = "Choose Any";
	static final String TYPE_CHOOSE_1 = "Choose 1";
	String title;
	String type;
	OptionEffect optionEffects [];
	boolean enabled;

	public Option () {
		setValues (NO_TITLE, NO_TITLE);
	}

	public Option (XMLNode aCellNode) {
		String tTitle;
		String tType;
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex, tChildrenCount, tEffectIndex;

		tTitle = aCellNode.getThisAttribute (AN_TITLE);
		tType = aCellNode.getThisAttribute (AN_TITLE, TYPE_ALL);
		setValues (tTitle, tType);
		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tEffectIndex = 0;
		optionEffects = new OptionEffect [tChildrenCount];
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (OptionEffect.EN_OPTION_EFFECT.equals (tChildName)) {
				optionEffects [tEffectIndex++] = new OptionEffect (tChildNode);
			}
		}
	}

	public int getEffectCount () {
		return optionEffects.length;
	}

	public OptionEffect getEffectIndex (int aIndex) {
		OptionEffect tEffect;

		if ((aIndex >= 0) && (aIndex < optionEffects.length)) {
			tEffect = optionEffects [aIndex];
		} else {
			tEffect = NO_OPTION_EFFECT;
		}

		return tEffect;
	}

	public XMLElement getOptionElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tOptionEffectElements, tOptionEffectElement;

		tXMLElement = aXMLDocument.createElement (EN_OPTION);
		tXMLElement.setAttribute (AN_TITLE, title);
		tOptionEffectElements = aXMLDocument.createElement (OptionEffect.EN_OPTION_EFFECTS);
		for (OptionEffect tEffect : optionEffects) {
			if (tEffect != OptionEffect.NO_OPTION_EFFECT) {
				tOptionEffectElement = tEffect.getEffectElement (aXMLDocument);
				tOptionEffectElements.appendChild (tOptionEffectElement);
			}
		}
		tXMLElement.appendChild (tOptionEffectElements);

		return tXMLElement;
	}

	public String getTitle () {
		return title;
	}

	public String getType () {
		return type;
	}
	
	public boolean isTypeAll () {
		return (type.equals (TYPE_ALL));
	}
	
	public boolean isTypeChoose1 () {
		return (type.equals (TYPE_CHOOSE_1));
	}
	
	public boolean isTypeChooseAny () {
		return (type.equals (TYPE_CHOOSE_ANY));
	}
	
	public boolean isEnabled () {
		return enabled;
	}

	public void setEnabled (boolean aEnabled) {
		enabled = aEnabled;
	}

	private void setValues (String aTitle, String aType) {
		title = aTitle;
		type = aType;
		setEnabled (false);
	}
	
	// TODO: Build out a set of OptionEffect sub-classes for each different Option
	// Each Option
	public void applyOptionEffects (GameManager aGameManager) {
		int tEffectCount, tEffectIndex;
		OptionEffect tEffect;
		
		tEffectCount = getEffectCount ();
		for (tEffectIndex = 0; tEffectIndex < tEffectCount; tEffectIndex++) {
			tEffect = getEffectIndex (tEffectIndex);
			if (tEffect != OptionEffect.NO_OPTION_EFFECT) {
				tEffect.applyOptionEffect (aGameManager);
			}
		}
	}
}
