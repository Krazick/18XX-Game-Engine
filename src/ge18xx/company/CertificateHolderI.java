package ge18xx.company;

import ge18xx.player.PortfolioHolderI;

//
//  CertificateHolderI.java
//  Game_18XX
//
//  Created by Mark Smith on 11/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public interface CertificateHolderI {
	public static final CertificateHolderI NO_OWNER = null;

	public abstract void addCertificate (Certificate aCertificate);

	public abstract int getCertificateCountAgainstLimit ();

	public abstract Certificate getCertificate (int aIndex);

	public abstract int getCertificateCountFor (Corporation aCorporation);
	
	public abstract int getShareCountFor (Corporation aCorporation);
	
	public abstract int getCertificatePercentageFor (Corporation aCorporation);

	public abstract int getCertificatePercentsFor (Corporation aCorporation);

	public abstract String getCertificatePercentList (Corporation aCorporation);

	public abstract boolean hasCertificateFor (Corporation aCorporation);

	public abstract String getHolderName ();

	public abstract String getHolderAbbrev ();

	public abstract String getName ();

	public abstract PortfolioHolderI getPortfolioHolder ();

	public boolean isABank ();

	public boolean isABankPool ();

	public boolean isAPlayer ();

	public boolean isACorporation ();
}
