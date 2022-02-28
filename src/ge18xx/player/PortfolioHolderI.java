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
	
	public boolean isBank ();
	
	public boolean isBankPool ();
	
	public boolean isPlayer ();
	
	public boolean isCompany ();
	
	@Override
	public String getName ();
	
	@Override
	public String getAbbrev ();

	@Override
	public boolean isABankPool ();
}
