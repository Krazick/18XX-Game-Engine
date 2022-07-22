package ge18xx.player;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;

import javax.swing.JPanel;

public interface PortfolioHolderI extends ActorI {

	public final static PortfolioHolderI NO_HOLDER = null;

	Portfolio portfolio = new Portfolio ();

	public void addCertificate (Certificate aCertificate);

	public Bank getBank ();

	public PortfolioHolderI getPortfolioHolder ();

	public Portfolio getPortfolio ();

	public void replacePortfolioInfo (JPanel aPortfolioJPanel);

	@Override
	public default boolean isABank () {
		return false;
	}

	@Override
	public default boolean isABankPool () {
		return false;
	}

	@Override
	public default boolean isAPlayer () {
		return false;
	}

	@Override
	public default boolean isACorporation () {
		return false;
	}

	@Override
	public String getName ();

	@Override
	public String getAbbrev ();
}
