package ge18xx.game.variants;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import ge18xx.company.CorporationList;
import ge18xx.game.GameManager;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.GUI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class VariantEffect {
	static final String NO_NAME = "<NO NAME>";
	static final int NO_QUANTITY = -1;
	static final AttributeName AN_ATTRIBUTE_NAME = new AttributeName ("attributeName");
	static final AttributeName AN_VALUE = new AttributeName ("value");
	static final AttributeName AN_QUANTITY = new AttributeName ("quantity");
	static final AttributeName AN_ACTION = new AttributeName ("action");
	static final AttributeName AN_DEFAULT_EFFECT = new AttributeName ("defaultEffect");
	static final AttributeName AN_NAME = new AttributeName ("name");
	static final AttributeName AN_CELL_NAME = new AttributeName ("cellName");
	static final AttributeName AN_CLASS = new AttributeName ("class");
	static final AttributeName AN_HIDE = new AttributeName ("hide");
	static final AttributeName AN_STATE = new AttributeName ("state");
	static final AttributeName AN_MUST_BUY_TRAIN = new AttributeName ("mustBuyTrain");
	public static final ElementName EN_VARIANT_EFFECT = new ElementName ("VariantEffect");
	public static final ElementName EN_VARIANT_EFFECTS = new ElementName ("VariantEffects");
	public static final VariantEffect NO_VARIANT_EFFECT = null;
	public static final List<VariantEffect> NO_VARIANT_EFFECTS = null;
	public static final JComponent NO_VARIANT_EFFECT_COMPONENT = null;
	public static final XMLNode NO_VARIANT_EFFECTS_NODE = null;
	public static final String SET_TRAIN_QUANTITY = "Set Train Quantity";
	public static final String MUST_BUY_TRAIN = "Must Buy Train";
	public static final String ADD_TO_BANK = "Add To Bank";
	public static final String REMOVE_PHASE = "Remove Phase";
	public static final String ADD_TRAIN = "Add Train";
	public static final String END_GAME_ON_STOCK_CELL = "End Game on Stock Cell";
	public static final Border VE_BORDER = BorderFactory.createLineBorder (Color.black);
	int id;
	String name;
	String action;
	String actorName;
	String cellName;
	boolean defaultEffect;
	boolean state;
	boolean hide;
	JComponent effectComponent;
	public enum ComponentType { JLABEL, CHECKBOX, RADIO_BUTTON }


	public VariantEffect () {
		setID (Variant.NO_ID);
		setName (NO_NAME);
		setAction (NO_NAME);
		setActorName (NO_NAME);
		setState (false);
	}

	public VariantEffect (XMLNode aVariantEffectNode) {
		String tAction;
		String tCellName;
		String tName;
		boolean tDefaultEffect;
		boolean tHide;
		boolean tState;
		int tID;

		tID = aVariantEffectNode.getThisIntAttribute (Variant.AN_ID, Variant.NO_ID);
		tName = aVariantEffectNode.getThisAttribute (AN_NAME);
		tAction = aVariantEffectNode.getThisAttribute (AN_ACTION);
		tDefaultEffect = aVariantEffectNode.getThisBooleanAttribute (AN_DEFAULT_EFFECT);
		tHide = aVariantEffectNode.getThisBooleanAttribute (AN_HIDE);
		tState = aVariantEffectNode.getThisBooleanAttribute (AN_STATE);
		setState (tState);
		setHide (tHide);
		setID (tID);
		setDefaultEffect (tDefaultEffect);

		// TODO: Refactor extracting out End Game on Stock Cell to new VariantEffect child
		// TODO: Refactor extracting out Must Buy Train to new VariantEffect child
		if (tAction.equals (END_GAME_ON_STOCK_CELL)) {
			tCellName = aVariantEffectNode.getThisAttribute (AN_CELL_NAME);
			setValue (tName, tAction, "GAME", tCellName);
		} else if (tAction.equals (MUST_BUY_TRAIN)) {
			setName (tName);
			setAction (tAction);
			setActorName ("GAME");
			setState (true);
		} else {
			setName (tName);
			setAction (tAction);
			setActorName ("GAME");
		}
	}

	public JComponent getEffectComponent () {
		return effectComponent;
	}

	public String getCellName () {
		return cellName;
	}

	public boolean hide () {
		return hide;
	}

	public int getID () {
		return id;
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
		tXMLElement.setAttribute (Variant.AN_ID, id);
		tXMLElement.setAttribute (AN_NAME, name);
		tXMLElement.setAttribute (AN_ACTION, action);
		tXMLElement.setAttribute (AN_DEFAULT_EFFECT, defaultEffect);
		tXMLElement.setAttribute (AN_HIDE, hide);
		tXMLElement.setAttribute (AN_STATE, state);

		return tXMLElement;
	}

	public String getAction () {
		return action;
	}

	public String getActorName () {
		return actorName;
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

	protected void setAction (String aAction) {
		action = aAction;
	}

	protected void setActorName (String aActorName) {
		actorName = aActorName;
	}

	public void setDefaultEffect (boolean aDefaultEffect) {
		defaultEffect = aDefaultEffect;
	}

	public void setEffectComponent (JComponent tEffectComponent) {
		effectComponent = tEffectComponent;
	}

	protected void setHide (boolean aHide) {
		hide = aHide;
	}

	protected void setID (int aID) {
		id = aID;
	}

	protected void setName (String aName) {
		name = aName;
	}

	protected void setState (boolean aState) {
		state = aState;
	}

	private void setValue (String aName, String aAction, String aActorName, String aCellName) {
		setID (Variant.NO_ID);
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
	 * Variant Effect Component Builder -- this should be overridden by the subclasses
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

		if (hide) {
			tComponentLabel = GUI.NO_LABEL;
		} else {
			tComponentLabel = new JLabel (action);
			tComponentLabel.setBorder (BorderFactory.createEmptyBorder (0, 22, 5, 10));
		}

		return tComponentLabel;
	}

	protected JRadioButton buildEffectRadioButton () {
		JRadioButton tRadioButton;

		tRadioButton = new JRadioButton (action);
		tRadioButton.setSelected (defaultEffect);
		tRadioButton.setBorder (BorderFactory.createEmptyBorder (0, 0, 0, 10));

		return tRadioButton;
	}

	protected JCheckBox buildEffectCheckBox () {
		JCheckBox tCheckBox;

		tCheckBox = new JCheckBox (action);
		tCheckBox.setBorder (BorderFactory.createEmptyBorder (0, 0, 0, 10));

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
