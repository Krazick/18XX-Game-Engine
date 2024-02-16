package ge18xx.company.benefit;

public final class FakeBenefit extends Benefit {
	public static final String NAME = "FAKE";

	public FakeBenefit () {
	}

	@Override
	public int getCost () {
		return 0;
	}

	@Override
	public boolean isRealBenefit () {
		return false;
	}

	@Override
	public String getNewButtonLabel () {
		return null;
	}
}
