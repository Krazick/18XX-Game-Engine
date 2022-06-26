package ge18xx.game.variants;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Variant {
	static final String NO_TITLE = "<NO TITLE>";
	public static final Variant NO_VARIANT = null;
	public static final Variant [] NO_VARIANTS = null;
	public static final ElementName EN_VARIANT = new ElementName ("Variant");
	public static final ElementName EN_VARIANTS = new ElementName ("Variants");
	static final AttributeName AN_TITLE = new AttributeName ("title");
	static final AttributeName AN_TYPE = new AttributeName ("type");
	static final String TYPE_ALL = "ALL";
	static final String TYPE_CHOOSE_ANY = "Choose Any";
	static final String TYPE_CHOOSE_1 = "Choose 1";
	String title;
	String type;
	VariantEffect variantEffects [];
	boolean enabled;

	public Variant () {
		setValues (NO_TITLE, NO_TITLE);
	}

	public Variant (XMLNode aCellNode) {
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
		variantEffects = new VariantEffect [tChildrenCount];
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECT.equals (tChildName)) {
				variantEffects [tEffectIndex++] = new VariantEffect (tChildNode);
			}
		}
	}

	public int getEffectCount () {
		return variantEffects.length;
	}

	public VariantEffect getEffectIndex (int aIndex) {
		VariantEffect tEffect;

		if ((aIndex >= 0) && (aIndex < variantEffects.length)) {
			tEffect = variantEffects [aIndex];
		} else {
			tEffect = VariantEffect.NO_VARIANT_EFFECT;
		}

		return tEffect;
	}

	public XMLElement getVariantElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;
		XMLElement tVariantEffectElements;
		XMLElement tVariantEffectElement;

		tXMLElement = aXMLDocument.createElement (EN_VARIANT);
		tXMLElement.setAttribute (AN_TITLE, title);
		tVariantEffectElements = aXMLDocument.createElement (VariantEffect.EN_VARIANT_EFFECTS);
		for (VariantEffect tVariantEffect : variantEffects) {
			if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tVariantEffectElement = tVariantEffect.getEffectElement (aXMLDocument);
				tVariantEffectElements.appendChild (tVariantEffectElement);
			}
		}
		tXMLElement.appendChild (tVariantEffectElements);

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
	
	// TODO: Build out a set of OptionEffect sub-classes for each different Variant
	// Each Variant
	public void applyVariantEffects (GameManager aGameManager) {
		int tEffectCount, tEffectIndex;
		VariantEffect tEffect;
		
		tEffectCount = getEffectCount ();
		for (tEffectIndex = 0; tEffectIndex < tEffectCount; tEffectIndex++) {
			tEffect = getEffectIndex (tEffectIndex);
			if (tEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tEffect.applyVariantEffect (aGameManager);
			}
		}
	}
}
