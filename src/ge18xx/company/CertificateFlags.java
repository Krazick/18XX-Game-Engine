package ge18xx.company;

import ge18xx.player.Player;

public class CertificateFlags {

	boolean enabled;
	boolean playerHasEnoughCash;
	boolean playerHasBidOnThisCert;
	boolean playerHasEnoughCashToBid;
	boolean playerHasSoldThisCompany;
	boolean playerHasMaxShares;
	boolean playerHasBoughtShare;
	boolean hasMustBuyCertificate;
	boolean playerAtCertLimit;
	boolean boughtShare;
	String companyAbbrev;
	int playerCash;

	public CertificateFlags (Certificate aCertificate, Player aPlayer) {
		if (aPlayer != Player.NO_PLAYER) {
			setDefaultFlags (aCertificate, aPlayer);
		} else {
			companyAbbrev = "NONE";
			playerHasSoldThisCompany = false;
			playerHasMaxShares = false;
			playerAtCertLimit = false;
			playerHasBoughtShare = false;
			playerHasBidOnThisCert = false;
			hasMustBuyCertificate = false;
			setPlayerCash (0);
		}
		
		setPlayerHasEnoughCash (true);
		playerHasEnoughCash = true;
		playerHasEnoughCashToBid = true;
		setEnabled (false);
	}

	private void setDefaultFlags (Certificate aCertificate, Player aPlayer) {
		companyAbbrev = aCertificate.getCompanyAbbrev ();
		playerHasSoldThisCompany = aPlayer.hasSoldCompany (companyAbbrev);
		playerHasMaxShares = aPlayer.hasMaxShares (companyAbbrev);
		playerAtCertLimit = aPlayer.atCertLimit ();
		playerHasBoughtShare = aPlayer.hasBoughtShare ();
		playerHasBidOnThisCert = aCertificate.hasBidOnThisCert (aPlayer);
		hasMustBuyCertificate = aPlayer.hasMustBuyCertificate ();
		setPlayerCash (aPlayer.getCash ());
	}
	
	public void setEnabled (boolean aEnabled) {
		enabled = aEnabled;
	}
	
	public boolean enabled () {
		return enabled;
	}
	
	public void setPlayerCash (int aPlayerCash) {
		playerCash = aPlayerCash;
	}
	
	public int getPlayerCash () {
		return playerCash;
	}
	
	public boolean enoughPlayerCash (int aPrice) {
		boolean tPlayerHasEnoughCash;
		
		tPlayerHasEnoughCash = (playerCash >= aPrice);
		setPlayerHasEnoughCash (tPlayerHasEnoughCash);
		
		return tPlayerHasEnoughCash;
	}
	
	public void setPlayerHasEnoughCash (boolean aPlayerHasEnoughCash) {
		playerHasEnoughCash = aPlayerHasEnoughCash;
	}
	
	public boolean playerHasEnoughCash () {
		return playerHasEnoughCash;
	}
	
	public void setPlayerHasEnoughToCashToBid (boolean aPlayerHasEoughCashToBid) {
		playerHasEnoughCashToBid = aPlayerHasEoughCashToBid;
	}
	
	public boolean playerHasEnoughCashToBid () {
		return playerHasEnoughCashToBid;
	}
}
