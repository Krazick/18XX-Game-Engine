package ge18xx.company;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import geUtilities.AttributeName;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class Coupon {
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_PRICE = new AttributeName ("price");
	public static final int NO_VALUE = 0;
	String name;
	int price;
	JLabel costLabel;
	
	public Coupon (String aName, int aPrice) {
		setName (aName);
		setPrice (aPrice);
	}
	
	public Coupon (XMLNode aXMLNode) {
		String tName;
		int tPrice;
		
		tName = aXMLNode.getThisAttribute (AN_NAME);
		setName (tName);
		tPrice = aXMLNode.getThisIntAttribute (AN_PRICE, NO_VALUE);
		setPrice (tPrice);
	}
	
	public int getPrice () {
		return price;
	}

	public String getName () {
		return name;
	}
	
	public void setPrice (int aPrice) {
		price = aPrice;
	}

	public void setName (String aName) {
		name = aName;
	}
	
	public void addAttributes (XMLElement aXMLElement) {
		aXMLElement.setAttribute (AN_NAME, name);
		if (price != NO_VALUE) {
			aXMLElement.setAttribute (AN_PRICE, price);
		}
	}
	
	public JPanel buildCouponInfoPanel (boolean aBorrowed) {
		JPanel tCouponInfoPanel;
		Border tCouponInfoBorder;

		tCouponInfoPanel = new JPanel ();
		tCouponInfoPanel.setLayout (new BoxLayout (tCouponInfoPanel, BoxLayout.Y_AXIS));
		tCouponInfoBorder = setupCouponInfoBorder ();
		tCouponInfoPanel.setBorder (tCouponInfoBorder);
		if (aBorrowed) {
			tCouponInfoPanel.add (new JLabel ("<BORROWED>"));
		}
		tCouponInfoPanel.add (new JLabel (getFullName ()));
		setCostLabel (tCouponInfoPanel, getPrice ());

		return tCouponInfoPanel;
	}
	
	public void setSelection () {
	}

	public void clearSelection () {
	}

	protected String getFullName () {
		return getName ();
	}

	protected void setCostLabel (JPanel aCouponInfoPanel, int aPrice) {
		String tCostLabel;

		tCostLabel = "Cost " + Bank.formatCash (aPrice);
		costLabel = new JLabel (tCostLabel);
		aCouponInfoPanel.add (costLabel);
	}
	
	protected void setCostLabel (String aLabel) {
		if (validCostLabel ()) {
			costLabel.setText (aLabel);
		} else {
			costLabel = new JLabel (aLabel);
		}
	}
	
	protected boolean validCostLabel () {
		boolean tValidCostLabel;
		
		if (costLabel != null) {
			tValidCostLabel = true;
		} else {
			tValidCostLabel = false;
		}
		
		return tValidCostLabel;
	}
	protected Border setupCouponInfoBorder () {
		Border tCouponInfoBorder;
		Border tInnerBorder;
		Border tOuterBorder;
		Color tInnerColor;
	
		tInnerColor = new Color (237, 237, 237);
		tInnerBorder = BorderFactory.createLineBorder (tInnerColor, 5);
		tOuterBorder = BorderFactory.createLineBorder (Color.black, 1);
		tCouponInfoBorder = BorderFactory.createCompoundBorder (tOuterBorder, tInnerBorder);
	
		return tCouponInfoBorder;
	}

}
