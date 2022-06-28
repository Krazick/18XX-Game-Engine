package ge18xx.game.variants;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class VariantEffect {
	static final String NO_NAME = "<NO NAME>";
	static final int NO_QUANTITY = -1;
	static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	static final AttributeName AN_ACTION = new AttributeName ("action");
	static final AttributeName AN_DEFAULT_EFFECT = new AttributeName ("defaultEffect");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final AttributeName AN_CELL_NAME = new AttributeName ("cellName");
	static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final ElementName EN_VARIANT_EFFECT = new ElementName ("VariantEffect");
	public static final ElementName EN_VARIANT_EFFECTS = new ElementName ("VariantEffects");
	public static final VariantEffect NO_VARIANT_EFFECT = null;
	public static final List<VariantEffect> NO_VARIANT_EFFECTS = null;
	public static final JComponent NO_VARIANT_EFFECT_COMPONENT = null;
	public static final String SET_TRAIN_QUANTITY = "Set Train Quantity";
	public static final String MUST_BUY_TRAIN = "Must Buy Train";
	public static final String ADD_TO_BANK = "Add To Bank";
	public static final String REMOVE_PHASE = "Remove Phase";
	public static final String ADD_TRAIN = "Add Train";
	public static final String END_GAME_ON_STOCK_CELL = "End Game on Stock Cell";
	String name;
	String action;
	String actorName;
	String cellName;
	boolean defaultEffect;
	boolean state;
	JComponent effectComponent;
	public enum ComponentType { JLABEL, CHECKBOX, RADIO_BUTTON }
	

	public VariantEffect () {
		setValue (NO_NAME, NO_NAME, NO_NAME, NO_NAME);
	}

	public VariantEffect (XMLNode aVariantEffectNode) {
		String tAction;
		String tCellName;
		String tName;
		boolean tDefaultEffect;
		
		tName = aVariantEffectNode.getThisAttribute (AN_NAME);
		tAction = aVariantEffectNode.getThisAttribute (AN_ACTION);
		tDefaultEffect = aVariantEffectNode.getThisBooleanAttribute (AN_DEFAULT_EFFECT);
		setDefaultEffect (tDefaultEffect);
		if (tAction.equals (END_GAME_ON_STOCK_CELL)) {
			tCellName = aVariantEffectNode.getThisAttribute (AN_CELL_NAME);
			setValue (tName, tAction, "GAME", tCellName);
		} else if (tAction.equals (MUST_BUY_TRAIN)) {
			setValue (tName, tAction, "GAME", true);
		} else {
			setValue (tName, tAction, NO_NAME, NO_NAME);
		}
	}

	public JComponent getEffectComponent () {
		return effectComponent;
	}
	
	public String getCellName () {
		return cellName;
	}

	public boolean getState () {
		return state;
	}

	/**
	 * Given an XMLDocument, this will create the XMLElement that stores the Effect Name 
	 * and whether this is a Default Effect
	 * 
	 * @param aXMLDocument The XMLDocumdnt to use to create the XMLElement
	 * 
	 * @return the filled out XMLElement
	 * 
	 */
	public XMLElement getEffectElement (XMLDocument aXMLDocument) {
		XMLElement tXMLElement;

		tXMLElement = aXMLDocument.createElement (EN_VARIANT_EFFECT);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_DEFAULT_EFFECT, defaultEffect);
		if (cellName != NO_NAME) {
			tXMLElement.setAttribute (AN_CELL_NAME, cellName);
		}
		
		if (name.equals (MUST_BUY_TRAIN)) {
			tXMLElement.setAttribute (AN_MUST_BUY_TRAIN, state);
		}

		
		return tXMLElement;
	}

	public String getAction () {
		return action;
	}
	
	public String getName () {
		return name;
	}

	public boolean isEnabled () {
		boolean tEnabled;
		
		if (effectComponent == NO_VARIANT_EFFECT_COMPONENT) {
			tEnabled = false;
		} else if (effectComponent instanceof JLabel)  {
			tEnabled = true;
		} else {
			tEnabled = effectComponent.isEnabled ();
		}
		
		return tEnabled;
	}
	
	private void setValue (String aName, String aAction, String aActorName, boolean aState) {
		name = aName;
		action = aAction;
		actorName = aActorName;
		state = aState;
	}

	public void setEffectComponent (JComponent tEffectComponent) {
		effectComponent = tEffectComponent;
	}
	
	protected void setName (String aName) {
		name = aName;
	}
	
	public void setDefaultEffect (boolean aDefaultEffect) {
		defaultEffect = aDefaultEffect;
	}
	
	private void setValue (String aName, String aAction, String aActorName, String aCellName) {
		setName (aName);
		action = aAction;
		actorName = aActorName;
		cellName = aCellName;
	}
	
	/**
	 * Apply the Variant Effect using the Game Manager as needed.
	 * 
	 * @param aGameManager The current GameManager to have the Variant Effect applied to.
	 * 
	 */
	public void applyVariantEffect (GameManager aGameManager) {
		String tEffectAction;
		CorporationList tCorporationList;

		tEffectAction = getAction ();
		if (VariantEffect.MUST_BUY_TRAIN.equals (tEffectAction)) {
			tCorporationList = aGameManager.getShareCompanies ();
			tCorporationList.setAllMustBuyTrain ();
		}
	}
	
	/**
	 * Variant Effect Component Builder -- this should be overriden by the subclasses
	 * 
	 * @param aItemListener Placeholder for the Item Listener class that will handle the request
	 * @return from this case NO_VARIANT_COMPONENT
	 * 
	 */
	public JComponent buildEffectComponent (VariantEffect.ComponentType aComponentType) {
		return NO_VARIANT_EFFECT_COMPONENT;
	}
	
	public String buildComponentText () {
		String tRadioButtonText;
		
		tRadioButtonText = action + " " + name;
		
		return tRadioButtonText;
	}
	
	protected JLabel buildEffectJLabel () {
		JLabel tComponentLabel;

		tComponentLabel = new JLabel (action);
		tComponentLabel.setBorder (BorderFactory.createEmptyBorder (0, 22, 5, 0));
		
		return tComponentLabel;
	}
	
	protected JRadioButton buildEffectRadioButton () {
		JRadioButton tRadioButton;
		
		tRadioButton = new JRadioButton (action);
		tRadioButton.setSelected (defaultEffect);
		
		return tRadioButton;	
	}

	protected JCheckBox buildEffectCheckBox () {
		JCheckBox tCheckBox;

		tCheckBox = new JCheckBox (action);
		
		return tCheckBox;	
	}

	protected boolean isSelected () {
		boolean tIsSelected;
		JCheckBox tCheckBox;
		JRadioButton tRadioButton;
		
		tIsSelected = false;
		
		if (effectComponent instanceof  JCheckBox) {
			tCheckBox = (JCheckBox) effectComponent;
			tIsSelected = tCheckBox.isSelected ();
		}
		if (effectComponent instanceof  JRadioButton) {
			tRadioButton = (JRadioButton) effectComponent;
			tIsSelected = tRadioButton.isSelected ();
		}
		
		return tIsSelected;
	}
}
