package ge18xx.company;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ge18xx.bank.Bank;
import geUtilities.XMLNode;

public class Coupon {
	String name;
	int price;
	JLabel costLabel;
	
	public Coupon (String aName, int aPrice) {
		setName (aName);
		setPrice (aPrice);
	}
	
	public Coupon (XMLNode aXMLNode) {
	
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

	public JPanel buildCouponInfoPanel () {
		JPanel tCouponInfoPanel;
		Border tCouponInfoBorder;

		tCouponInfoPanel = new JPanel ();
		tCouponInfoPanel.setLayout (new BoxLayout (tCouponInfoPanel, BoxLayout.Y_AXIS));
		tCouponInfoBorder = setupCouponInfoBorder ();
		tCouponInfoPanel.setBorder (tCouponInfoBorder);
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
