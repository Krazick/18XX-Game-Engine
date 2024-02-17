package ge18xx.player;

import ge18xx.company.LoadedCertificate;

public interface PortfolioHolderLoaderI extends PortfolioHolderI {
	public PortfolioHolderI getCurrentHolder (LoadedCertificate aCertificate);
}
