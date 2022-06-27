package ge18xx.game.variants;

import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.NodeList;

import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Variant {
	static final String NO_TITLE = "<NO TITLE>";
	public static final Variant NO_VARIANT = null;
	public static final Variant [] NO_VARIANTS = null;
	public static final ElementName EN_VARIANT = new ElementName ("Variant");
	public static final ElementName EN_VARIANTS = new ElementName ("Variants");
	public static final AttributeName AN_TITLE = new AttributeName ("title");
	public static final AttributeName AN_VARIANT_CLASS = new AttributeName ("class");
	static final String TYPE_ALL = "ALL";
	static final String TYPE_CHOOSE_ANY = "Choose Any";
	static final String TYPE_CHOOSE_1 = "Choose 1";
	String title;
	String type;
	VariantEffect variantEffects [];
	boolean enabled;
	JComponent titleComponent;

	public Variant () {
		setTitle (NO_TITLE);
	}

	public Variant (XMLNode aCellNode) {
		String tTitle;

		tTitle = aCellNode.getThisAttribute (AN_TITLE);
		setTitle (tTitle);
		loadVariantEffects (aCellNode);
	}

	public void loadVariantEffects (XMLNode aCellNode) {
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex;
		int tChildrenCount;
		int tEffectIndex;
		VariantEffect tVariantEffect;
		
		tChildren = aCellNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tEffectIndex = 0;
		variantEffects = new VariantEffect [tChildrenCount];
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECT.equals (tChildName)) {
				tVariantEffect = loadVariantEffect (tChildNode);
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					variantEffects [tEffectIndex++] = tVariantEffect;
				}
			}
		}
	}

	public VariantEffect loadVariantEffect (XMLNode aVariantEffectNode) {
		String tClassName;
		VariantEffect tVariantEffect;
		Class<?> tVariantEffectToLoad;
		Constructor<?> tVariantEffectConstructor;
		
		tVariantEffect = VariantEffect.NO_VARIANT_EFFECT;
		try {
			tClassName = aVariantEffectNode.getThisAttribute (Variant.AN_VARIANT_CLASS);
			tVariantEffectToLoad = Class.forName (tClassName);
			tVariantEffectConstructor = tVariantEffectToLoad.getConstructor (aVariantEffectNode.getClass ());
			tVariantEffect = (VariantEffect) tVariantEffectConstructor.newInstance (aVariantEffectNode);
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			tException.printStackTrace ();
		}

		return tVariantEffect;
	}
	
	public JPanel buildVariantDescription (ItemListener aItemListener) {
		return GUI.NO_PANEL;
	}
	
	public JComponent buildTitleComponent (VariantEffect.ComponentType aEffectComponentType) {
		JComponent tTitleComponent;
		
		if (aEffectComponentType == VariantEffect.ComponentType.JLABEL) {
			tTitleComponent = new JCheckBox (getTitle ());	
		} else {
			tTitleComponent = new JLabel (getTitle ());
		}
		tTitleComponent.setBorder (BorderFactory.createEmptyBorder (5, 0, 0, 0));
		
		return tTitleComponent;
	}
	
	public JPanel buildVariantDescription (VariantEffect.ComponentType aEffectComponentType, ItemListener aItemListener) {
		JPanel tDescPanel;
		JComponent tTitleComponent;
		JComponent tEffectComponent;
		ButtonGroup tEffectButtonGroup;
		boolean tRadioButtonGroup;
		
		tDescPanel = new JPanel ();
		tDescPanel.setLayout (new BoxLayout (tDescPanel, BoxLayout.PAGE_AXIS));
		
		tTitleComponent = buildTitleComponent (aEffectComponentType);
		tDescPanel.add (tTitleComponent);
		
		tRadioButtonGroup = (aEffectComponentType == VariantEffect.ComponentType.RADIO_BUTTON);
		if (tRadioButtonGroup) {
			tEffectButtonGroup = new ButtonGroup ();
		} else {
			tEffectButtonGroup = GUI.NO_BUTTON_GROUP;
		}
		for (VariantEffect tVariantEffect : variantEffects) {
			if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tEffectComponent = tVariantEffect.buildEffectComponent (aEffectComponentType, aItemListener);
				if (tEffectComponent != VariantEffect.NO_VARIANT_COMPONENT) {
					tDescPanel.add (tEffectComponent);
					if (tRadioButtonGroup) {
						tEffectButtonGroup.add ((AbstractButton) tEffectComponent);
					}
				}
			}
		}
		
		return tDescPanel;
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
	
	public boolean isEnabled () {
		return enabled;
	}

	public void setEnabled (boolean aEnabled) {
		enabled = aEnabled;
	}

	protected void setTitle (String aTitle) {
		title = aTitle;
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
