package ge18xx.company.benefit;

public final class FakeBenefit extends Benefit {
	public final static String NAME = "FAKE";

	public FakeBenefit () {
	}

	@Override
	public int getCost () {
		return 0;
	}

	@Override
	public boolean realBenefit () {
		return false;
	}
}
