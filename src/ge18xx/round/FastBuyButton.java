package ge18xx.round;

import javax.swing.JButton;

import ge18xx.company.Certificate;

public class FastBuyButton extends JButton {

	private static final long serialVersionUID = 1L;
	private Certificate certificate;
	public FastBuyButton (String aLabel, Certificate aCertificate) {
		super (aLabel);
		setCertificate (aCertificate);
	}

	public Certificate getCertificate () {
		return certificate;
	}
	
	public void setCertificate (Certificate aCertificate) {
		certificate = aCertificate;
	}
	
}
