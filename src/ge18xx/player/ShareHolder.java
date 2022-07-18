package ge18xx.player;

public class ShareHolder {
	PortfolioHolderI shareHolder;
	int percentageHeld;

	public ShareHolder (PortfolioHolderI aPortfolioHolder, int aShareCount) {
		setShareHolder (aPortfolioHolder);
		setPercentageHeld (aShareCount);
	}

	public void addPercentangeHeld (int aPercentage) {
		setPercentageHeld (getPercentageHeld () + aPercentage);
	}

	public String getName () {
		return shareHolder.getName ();
	}

	public PortfolioHolderI getShareHolder () {
		return shareHolder;
	}

	public int getPercentageHeld () {
		return percentageHeld;
	}

	public boolean isShareHolder (PortfolioHolderI aPortfolioHolder) {
		return (aPortfolioHolder == shareHolder);
	}

	public void setShareHolder (PortfolioHolderI aPortfolioHolder) {
		shareHolder = aPortfolioHolder;
	}

	public void setPercentageHeld (int aPercentage) {
		percentageHeld = aPercentage;
	}
	
	public String getShareHolderInfo (ShareHolder aShareHolder) {
		String tShareHolderInfo;
		
		tShareHolderInfo = "Holder " + getName () + " Percentage Shares held: " + percentageHeld;
		
		return tShareHolderInfo;
	}

}
