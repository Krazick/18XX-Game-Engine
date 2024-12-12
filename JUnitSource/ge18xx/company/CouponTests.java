package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JPanel;
import javax.swing.border.Border;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

class CouponTests {
	public static final ElementName EN_COUPON = new ElementName ("Coupon");
	CouponTestFactory couponTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	BankTestFactory bankTestFactory;
	Coupon couponAlpha;
	Coupon couponBeta;
	Bank bank;
	
	@BeforeEach
	void setUp () throws Exception {
		utilitiesTestFactory = new UtilitiesTestFactory ();
		couponTestFactory = new CouponTestFactory (utilitiesTestFactory);
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();
	}

	@Test
	@DisplayName ("Basic Coupon Tests")
	void basicCouponTests () {
		Border tCouponBorder;
		
		couponAlpha = couponTestFactory.buildCoupon ("Alpha", 20);
		couponBeta = couponTestFactory.buildCoupon ();
		
		assertEquals ("Alpha", couponAlpha.getName ());
		assertEquals ("Alpha", couponAlpha.getFullName ());
		assertEquals (20, couponAlpha.getPrice ());
		
		assertEquals ("TEST-Coupon", couponBeta.getName ());
		assertEquals ("TEST-Coupon", couponBeta.getFullName ());
		assertEquals (40, couponBeta.getPrice ());
		
		tCouponBorder = null;
		assertNull (tCouponBorder);
		tCouponBorder = couponAlpha.setupCouponInfoBorder ();
		assertNotNull (tCouponBorder);
	}
	
	@Test
	@DisplayName ("Coupon Cost Label Tests")
	void couponCostLabelTests () {
		couponAlpha = couponTestFactory.buildCoupon ("Alpha", 20);
		assertFalse (couponAlpha.validCostLabel ());
		
		couponAlpha.setCostLabel ("Test Label");
		assertTrue (couponAlpha.validCostLabel ());
		
		couponAlpha.setCostLabel ("Another TestLabel");
		assertTrue (couponAlpha.validCostLabel ());
	}
	
	@Test
	@DisplayName ("Coupon with JPanel Cost Label Tests")
	void couponJPanelCostLabelTests () {
		JPanel tTestJPanel;
		JPanel tAlphaNotBorrowedPanel;
		JPanel tAlphaBorrowedPanel;
		
		tTestJPanel = new JPanel ();
		couponAlpha = couponTestFactory.buildCoupon ("Alpha", 20);
		
		couponAlpha.setCostLabel (tTestJPanel, 50);
		assertEquals (20, couponAlpha.getPrice ());

		tAlphaNotBorrowedPanel = couponAlpha.buildCouponInfoPanel (false);
		assertNotNull (tAlphaNotBorrowedPanel);

		tAlphaBorrowedPanel = couponAlpha.buildCouponInfoPanel (true);
		assertNotNull (tAlphaBorrowedPanel);
	}
	
	@Test
	@DisplayName ("Coupon add XMLElement Tests")
	void couponAddXMLElementTests () {
		XMLElement tXMLElement;
		XMLDocument tXMLDocument;
		
		tXMLDocument = new XMLDocument ();
		tXMLElement = tXMLDocument.createElement (EN_COUPON);

		couponAlpha = couponTestFactory.buildCoupon ("Alpha", 50);
		couponAlpha.addAttributes (tXMLElement);
		assertEquals ("<Coupon name=\"Alpha\" price=\"50\"/>\n", tXMLElement.toXMLString ());
	}
	
	@Test
	@DisplayName ("Loan Redemption Coupon Tests")
	void loanRedemptionCouponTests () {
		LoanRedemptionCoupon tLoanRedemptionCoupon;
		
		tLoanRedemptionCoupon = couponTestFactory.buildLoanRedemptionCoupon (100);
		assertEquals (100, tLoanRedemptionCoupon.getPrice ());
		assertEquals ("Loan Redemption Coupon", tLoanRedemptionCoupon.getName ());
	}
	
	
	@Test
	@DisplayName ("Loan Interest Coupon Tests")
	void loanInterestCouponTests () {
		LoanInterestCoupon tLoanInterestCoupon;
		
		tLoanInterestCoupon = couponTestFactory.buildLoanInterestCoupon (50, 35);
		assertEquals (50, tLoanInterestCoupon.getPrice ());
		assertEquals (35, tLoanInterestCoupon.getRevenueContribution ());
		assertEquals ("Loan Interest Coupon", tLoanInterestCoupon.getName ());
		
		tLoanInterestCoupon.setRevenueContribution (23);
		assertEquals (23, tLoanInterestCoupon.getRevenueContribution ());
		
	}

}
