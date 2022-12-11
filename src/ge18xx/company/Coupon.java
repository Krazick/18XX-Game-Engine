package ge18xx.company;

import javax.swing.JPanel;

public class Coupon implements Comparable<Object> {
	String name;
	int price;
	
	public Coupon (String aName, int aPrice) {
		setName (aName);
		setPrice (aPrice);
	}

	@Override
	public int compareTo (Object aObject) {
		// TODO Auto-generated method stub
		return 0;
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

	public JPanel buildCertificateInfoPanel () {
		// TODO Auto-generated method stub
		return null;
	}
	public void setSelection () {
	}

	public void clearSelection () {
	}

}
