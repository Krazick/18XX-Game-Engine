package ge18xx.company;

public class LoanInterestCoupon extends Coupon {
	public static final String NAME = "Loan Interest Coupon";
	int revenueContribution;
	
	public LoanInterestCoupon (int aPrice, int aRevenueContribution) {
		super (NAME, aPrice);
		
		setRevenueContribution (aRevenueContribution);
	}

	public void setRevenueContribution (int aRevenueContribution) {
		revenueContribution = aRevenueContribution;
	}
	
	public int getRevenueContribution () {
		return revenueContribution;
	}
}
