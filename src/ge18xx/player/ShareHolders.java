package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

public class ShareHolders {
	List<ShareHolder> shareHolders;

	public ShareHolders() {
		shareHolders = new LinkedList<ShareHolder> ();
	}

	public void addShareHolder (PortfolioHolderI aPortfolioHolder, int aShareCount) {
		boolean tShareHolderInList;
		
		tShareHolderInList = false;
		for (ShareHolder tShareHolder : shareHolders) {
			if (tShareHolder.isShareHolder (aPortfolioHolder)) {
				tShareHolderInList = true;
				tShareHolder.addSharesHeld (aShareCount);
			}
		}
		if (! tShareHolderInList) {
			ShareHolder tNewShareHolder;
			tNewShareHolder = new ShareHolder (aPortfolioHolder, aShareCount);
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
	
	public int getShareCount (int tIndex) {
		ShareHolder tShareHolder;
		int tShareCount;
		
		tShareCount = 0;
		if ((tIndex < 0) || (tIndex > shareHolders.size ())) {
			
		} else {
			tShareHolder = shareHolders.get (tIndex);
			tShareCount = tShareHolder.getSharesHeld ();
		}
		
		return tShareCount;
	}
	
	public int getShareHolderCount () {
		return shareHolders.size ();
	}
	
	public void printShareHolderInfo () {
		for (ShareHolder tShareHolder : shareHolders) {
			System.out.println ("Holder " + tShareHolder.getName () + " percentage " + tShareHolder.getSharesHeld ());
		}
	}
}
