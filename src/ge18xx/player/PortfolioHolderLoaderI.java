package ge18xx.player;

import ge18xx.company.LoadedCertificate;

public interface PortfolioHolderLoaderI extends PortfolioHolderI {
	public static final PortfolioHolderLoaderI NO_PORTFOLIO_HOLDER_LOADER = null;

	public PortfolioHolderI getCurrentHolder (LoadedCertificate aCertificate);
}
