package ge18xx.company;

import org.mockito.Mockito;

import geUtilities.utilites.xml.UtilitiesTestFactory;
import geUtilities.xml.XMLNode;

public class CouponTestFactory {
	UtilitiesTestFactory utilitiesTestFactory;
	
	public CouponTestFactory (UtilitiesTestFactory aUtilitiesTestFactory) {
		utilitiesTestFactory = aUtilitiesTestFactory;
	}

	public Coupon buildCoupon (String aName, int aPrice) {
		Coupon tCoupon;
		
		tCoupon = new Coupon (aName, aPrice);
		
		return tCoupon;
	}

	
	public Coupon buildCouponMock () {
		Coupon mCoupon;

		mCoupon = Mockito.mock (Coupon.class);

		return mCoupon;
	}

	public Coupon buildCoupon () {
		String tCouponTestXML = "<Coupon name=\"TEST-Coupon\" price=\"40\" />";
		XMLNode tXMLCouponNode;
		Coupon tParsedCoupon;
		
		tXMLCouponNode = utilitiesTestFactory.buildXMLNode (tCouponTestXML);
		tParsedCoupon = new Coupon (tXMLCouponNode);
		
		return tParsedCoupon;
	}
	
	public LoanRedemptionCoupon buildLoanRedemptionCoupon (int aPrice) {
		LoanRedemptionCoupon tLoanRedemptionCoupon;
		
		tLoanRedemptionCoupon = new LoanRedemptionCoupon (aPrice);
		
		return tLoanRedemptionCoupon;
	}
	
	public LoanInterestCoupon buildLoanInterestCoupon (int aPrice, int aRevenueContribution) {
		LoanInterestCoupon tLoanInterestCoupon;
		
		tLoanInterestCoupon = new LoanInterestCoupon (aPrice, aRevenueContribution);
		
		return tLoanInterestCoupon;
	}
}
