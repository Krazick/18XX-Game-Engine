package ge18xx.company;

import org.mockito.Mockito;

import ge18xx.company.License.LicenseTypes;
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
	
	public License buildLicense () {
		License tLicense;
		
		tLicense = new License ();
		
		return tLicense;
	}

	public License buildLicense (String aName, int aPrice, int aBenefitValue) {
		License tLicense;
		
		tLicense = new License (aName, aPrice, aBenefitValue);
		
		return tLicense;
	}

	public License buildLicense (LicenseTypes aType, int aPrice, int aBenefitValue) {
		License tLicense;
		
		tLicense = new License (aType, aPrice, aBenefitValue);
		
		return tLicense;
	}

	public License buildLicenseMock () {
		License mLicense;

		mLicense = Mockito.mock (License.class);

		return mLicense;
	}
}
