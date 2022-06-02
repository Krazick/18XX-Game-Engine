package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;

import javax.swing.JPanel;

public interface PortfolioHolderI extends ActorI {

	Portfolio portfolio = new Portfolio ();

	public void addCertificate (Certificate aCertificate);

	public Bank getBank ();

	public PortfolioHolderI getPortfolioHolder ();

	public Portfolio getPortfolio ();

	public void replacePortfolioInfo (JPanel aPortfolioJPanel);

	@Override
	public boolean isABank ();

	@Override
	public boolean isABankPool ();

	@Override
	public boolean isAPlayer ();

	public boolean isCompany ();

	@Override
	public String getName ();

	@Override
	public String getAbbrev ();

	public final static PortfolioHolderI NO_HOLDER = null;
}
