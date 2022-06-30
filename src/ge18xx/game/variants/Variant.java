package ge18xx.game.variants;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
//import javax.swing.Box;
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
	public static final int NO_ID = 0;
	public static final Variant NO_VARIANT = null;
	public static final JComponent NO_VARIANT_COMPONENT = null;
	public static final Variant [] NO_VARIANTS = null;
	public static final ElementName EN_VARIANT = new ElementName ("Variant");
	public static final ElementName EN_VARIANTS = new ElementName ("Variants");
	public static final AttributeName AN_TITLE = new AttributeName ("title");
	public static final AttributeName AN_VARIANT_CLASS = new AttributeName ("class");
	public static final AttributeName AN_ID = new AttributeName ("id");
	static final String TYPE_ALL = "ALL";
	static final String TYPE_CHOOSE_ANY = "Choose Any";
	static final String TYPE_CHOOSE_1 = "Choose 1";
	int id;
	String title;
	String type;
	List<VariantEffect> variantEffects;
	boolean enabled;
	JComponent titleComponent;

	public Variant () {
		setID (NO_ID);
		setTitle (NO_TITLE);
	}

	public Variant (XMLNode aXMLNode) {
		String tTitle;
		int tID;
		
		tID = aXMLNode.getThisIntAttribute (AN_ID, NO_ID);
		tTitle = aXMLNode.getThisAttribute (AN_TITLE);
		setID (tID);
		setTitle (tTitle);
		loadVariantEffects (aXMLNode);
	}

	public void loadVariantEffects (XMLNode aXMLNode) {
		String tChildName;
		XMLNode tChildNode;
		NodeList tChildren;
		int tIndex;
		int tChildrenCount;
		VariantEffect tVariantEffect;
		
		tChildren = aXMLNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		variantEffects = new LinkedList<VariantEffect> ();
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (VariantEffect.EN_VARIANT_EFFECT.equals (tChildName)) {
				tVariantEffect = loadVariantEffect (tChildNode);
				if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
					variantEffects.add (tVariantEffect);
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
	
	public JPanel buildVariantDescription () {
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
	
	public JPanel buildVariantDescription (VariantEffect.ComponentType aEffectComponentType) {
		JPanel tDescPanel;
		JComponent tEffectComponent;
		ButtonGroup tEffectButtonGroup;
		boolean tRadioButtonGroup;
		
		tDescPanel = new JPanel ();
		tDescPanel.setBorder (VariantEffect.VE_BORDER);
		tDescPanel.setLayout (new BoxLayout (tDescPanel, BoxLayout.PAGE_AXIS));
		titleComponent = buildTitleComponent (aEffectComponentType);
		titleComponent.setBorder (BorderFactory.createEmptyBorder (0, 10, 0, 10));
//		tDescPanel.add (Box.createHorizontalStrut (10));
		tDescPanel.add (titleComponent);
		
		tRadioButtonGroup = (aEffectComponentType == VariantEffect.ComponentType.RADIO_BUTTON);
		if (tRadioButtonGroup) {
			tEffectButtonGroup = new ButtonGroup ();
		} else {
			tEffectButtonGroup = GUI.NO_BUTTON_GROUP;
		}
		for (VariantEffect tVariantEffect : variantEffects) {
			if (tVariantEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tEffectComponent = tVariantEffect.buildEffectComponent (aEffectComponentType);
				if (tEffectComponent != VariantEffect.NO_VARIANT_EFFECT_COMPONENT) {
					tVariantEffect.setEffectComponent (tEffectComponent);
					tDescPanel.add (tEffectComponent);
					if (tRadioButtonGroup) {
						tEffectButtonGroup.add ((AbstractButton) tEffectComponent);
					}
				}
			}
		}
		
		return tDescPanel;
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

	public int getID () {
		return id;
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

	public void setID (int aID) {
		id = aID;
	}
	
	protected void setTitle (String aTitle) {
		title = aTitle;
	}
	
	public void applyVariantEffects (GameManager aGameManager) {
		for (VariantEffect tEffect: variantEffects) {
			if (tEffect != VariantEffect.NO_VARIANT_EFFECT) {
				tEffect.applyVariantEffect (aGameManager);
			}
		}
	}

	public void addActiveVariantEffects (List<VariantEffect> aActiveVariantEffects) {
		System.err.println ("Base Class should not be called, sub-classes should override this method");
	}
	
	public boolean isActive () {
		boolean tIsActive;
		JCheckBox tCheckBox;
		
		tIsActive = false;
		if (titleComponent != GUI.NO_JCOMPONENT) {
			if (titleComponent instanceof JCheckBox) {
				tCheckBox = (JCheckBox) titleComponent;
				if (tCheckBox.isSelected ()) {
					tIsActive = true;
				}
			}
		}
		
		return tIsActive;
	}

	public boolean hasVariantEffect (int aVariantID) {
		boolean tHasVariantEffect;
		VariantEffect tEffect;
		
		tEffect = getVariantEffect (aVariantID);
		if (tEffect == VariantEffect.NO_VARIANT_EFFECT) {
			tHasVariantEffect = false;
		} else {
			tHasVariantEffect = true;
		}
		
		return tHasVariantEffect;
	}
	
	public VariantEffect getVariantEffect (int aVariantID) {
		VariantEffect tFoundEffect;
		
		tFoundEffect = VariantEffect.NO_VARIANT_EFFECT;
		for (VariantEffect tEffect: variantEffects) {
			if (tEffect != VariantEffect.NO_VARIANT_EFFECT) {
				if (tEffect.getID () == aVariantID) {
					tFoundEffect = tEffect;
				}
			}
		}

		return tFoundEffect;
	}
	
	public boolean selectActiveVariantEffects (VariantEffect aVariantEffect) {
		boolean tSelected;
		
		tSelected = false;
		
		return tSelected;
	}
}
