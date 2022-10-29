package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

public class ShareHolders {
	List<ShareHolder> shareHolders;

	public ShareHolders () {
		shareHolders = new LinkedList<> ();
	}

	public void addShareHolder (PortfolioHolderI aPortfolioHolder, int aPercentage) {
		boolean tShareHolderInList;

		tShareHolderInList = false;
		for (ShareHolder tShareHolder : shareHolders) {
			if (tShareHolder.isShareHolder (aPortfolioHolder)) {
				tShareHolderInList = true;
				tShareHolder.addPercentangeHeld (aPercentage);
			}
		}
		if (!tShareHolderInList) {
			ShareHolder tNewShareHolder;
			tNewShareHolder = new ShareHolder (aPortfolioHolder, aPercentage);
			shareHolders.add (tNewShareHolder);
		}
	}

	public PortfolioHolderI getPortfolioHolder (int tIndex) {
		ShareHolder tShareHolder;
		PortfolioHolderI tPortfolioHolder;

		tPortfolioHolder = null;
		if ((tIndex < 0) || (tIndex > shareHolders.size ())) {
			tShareHolder = null;
		} else {
			tShareHolder = shareHolders.get (tIndex);
			tPortfolioHolder = tShareHolder.getShareHolder ();
		}

		return tPortfolioHolder;
	}

	public int getPercentage (int tIndex) {
		ShareHolder tShareHolder;
		int tShareCount;

		if ((tIndex < 0) || (tIndex > shareHolders.size ())) {
			tShareCount = 0;
		} else {
			tShareHolder = shareHolders.get (tIndex);
			tShareCount = tShareHolder.getPercentageHeld ();
		}

		return tShareCount;
	}

	public int getShareHolderCount () {
		return shareHolders.size ();
	}

	public void printShareHolderInfo () {
		String tShareHolderInfo;

		for (ShareHolder tShareHolder : shareHolders) {
			tShareHolderInfo = tShareHolder.getShareHolderInfo (tShareHolder);
			System.out.println (tShareHolderInfo);
		}
	}
}
