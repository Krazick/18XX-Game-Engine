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
	boolean playerHasMustBuyCertificate;
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
			setPlayerHasBoughtShare (false);
			setPlayerHasBidOnThisCertificate (false);
			setPlayerHasMustBuyCertificate (false);
			setPlayerCash (0);
		}
		
		setPlayerHasEnoughCash (true);
		setPlayerHasEnoughToCashToBid (true);
		setEnabled (false);
	}

	private void setDefaultFlags (Certificate aCertificate, Player aPlayer) {
		companyAbbrev = aCertificate.getCompanyAbbrev ();
		playerHasSoldThisCompany = aPlayer.hasSoldCompany (companyAbbrev);
		playerHasMaxShares = aPlayer.hasMaxShares (companyAbbrev);
		playerAtCertLimit = aPlayer.atCertLimit ();
		setPlayerHasBoughtShare (aPlayer.hasBoughtShare ());
		setPlayerHasBidOnThisCertificate (aCertificate.hasBidOnThisCert (aPlayer));
		setPlayerHasMustBuyCertificate (aPlayer.hasMustBuyCertificate ());
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
	
	public void setPlayerHasBidOnThisCertificate (boolean aPlayerHasBidOnThisCert) {
		playerHasBidOnThisCert = aPlayerHasBidOnThisCert;
	}
	
	public boolean playerHasBidOnThisCert () {
		return playerHasBidOnThisCert;
	}
	
	public void setPlayerHasMustBuyCertificate (boolean aPlayerHasMustBuyCertificate) {
		playerHasMustBuyCertificate = aPlayerHasMustBuyCertificate;
	}
	
	public boolean playerHasMustBuyCertificate () {
		return playerHasMustBuyCertificate;
	}
	
	public void setPlayerHasBoughtShare (boolean aPlayerHasBoughtShare) {
		playerHasBoughtShare = aPlayerHasBoughtShare;
	}
	
	public boolean playerHasBoughtShare () {
		return playerHasBoughtShare;
	}
}
