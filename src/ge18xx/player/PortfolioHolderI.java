package ge18xx.player;

import javax.swing.JPanel;

import ge18xx.bank.Bank;
import ge18xx.company.Certificate;
import ge18xx.round.action.ActorI;

public interface PortfolioHolderI extends ActorI {

	public final static PortfolioHolderI NO_HOLDER = null;
	public static final String PLAYER_PORTFOLIO_CHANGED = "Player Portfolio Changed ";
	
	Portfolio portfolio = new Portfolio (NO_HOLDER);

	public void addCertificate (Certificate aCertificate);

	public Bank getBank ();

	public PortfolioHolderI getPortfolioHolder ();

	public Portfolio getPortfolio ();

	public default void replacePortfolioInfo (JPanel aPortfolioJPanel) {
		// DO NOTHING by default
	}

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

	public void updateListeners (String aMessage);
}
