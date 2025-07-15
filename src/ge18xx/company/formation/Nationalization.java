package ge18xx.company.formation;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.MinorCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;

public class Nationalization extends PlayerFormationPanel {

	private static final long serialVersionUID = 1L;

	public Nationalization (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aFormCompany, aPlayer, aActingPresident);
	}
	
	@Override
	public JPanel buildCompanyJPanel (MinorCompany aMinorCompany, boolean aActingPlayer) {
		JPanel tMinorCompanyJPanel;
		
		tMinorCompanyJPanel = new JPanel ();
		tMinorCompanyJPanel.setLayout (new BoxLayout (tMinorCompanyJPanel, BoxLayout.X_AXIS));

		tMinorCompanyJPanel = super.buildCompanyJPanel (aMinorCompany, aActingPlayer, tMinorCompanyJPanel);
		
//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tMinorCompanyJPanel;
	}

	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = new JPanel ();
		tShareCompanyJPanel.setLayout (new BoxLayout (tShareCompanyJPanel, BoxLayout.X_AXIS));

		tShareCompanyJPanel = super.buildCompanyJPanel (aShareCompany, aActingPlayer, tShareCompanyJPanel);
		
//		buildSpecialButtons (aShareCompany, tShareCompanyJPanel, aActingPlayer);

		return tShareCompanyJPanel;
	}

}
