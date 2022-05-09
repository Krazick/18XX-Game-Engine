package ge18xx.player;

public class ShareHolder {
	PortfolioHolderI shareHolder;
	int sharesHeld;

	public ShareHolder () {
		setSharesHeld (0);
	}

	public ShareHolder (PortfolioHolderI aPortfolioHolder, int aShareCount) {
		setShareHolder (aPortfolioHolder);
		setSharesHeld (aShareCount);
	}

	public void addSharesHeld (int aShareCount) {
		setSharesHeld (getSharesHeld () + aShareCount);
	}

	public String getName () {
		return shareHolder.getName ();
	}

	public PortfolioHolderI getShareHolder () {
		return shareHolder;
	}

	public int getSharesHeld () {
		return sharesHeld;
	}

	public boolean isShareHolder (PortfolioHolderI aPortfolioHolder) {
		return (aPortfolioHolder == shareHolder);
	}

	public void setShareHolder (PortfolioHolderI aPortfolioHolder) {
		shareHolder = aPortfolioHolder;
	}

	public void setSharesHeld (int aShareCount) {
		sharesHeld = aShareCount;
	}
}
