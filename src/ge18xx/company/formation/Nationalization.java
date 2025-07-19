package ge18xx.company.formation;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ge18xx.company.Corporation;
import ge18xx.company.MinorCompany;
import ge18xx.company.PrivateCompany;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import geUtilities.GUI;
import swingTweaks.KButton;

public class Nationalization extends PlayerFormationPanel {
	private static final long serialVersionUID = 1L;
	public static final String UPGRADE_TO_PRUSSIAN = "Upgrade to Prussian";

	public Nationalization (GameManager aGameManager, FormCompany aFormCompany, Player aPlayer,
			Player aActingPresident) {
		super (aGameManager, aFormCompany, aPlayer, aActingPresident);
	}
	
	@Override
	public JPanel buildCompanyJPanel (PrivateCompany aPrivateCompany, boolean aActingPlayer) {
		JPanel tPrivateCompanyJPanel;
		
		tPrivateCompanyJPanel = new JPanel ();
		tPrivateCompanyJPanel.setLayout (new BoxLayout (tPrivateCompanyJPanel, BoxLayout.X_AXIS));

		tPrivateCompanyJPanel = super.buildCompanyJPanel (aPrivateCompany, aActingPlayer, tPrivateCompanyJPanel);
		
		buildSpecialButtons (aPrivateCompany, tPrivateCompanyJPanel, aActingPlayer);

		return tPrivateCompanyJPanel;
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

	/* For the 1835 Nationalization, Prussian Formation, the Share Companies never have any impact, 
	 * Don't provide a panel for them.
	 */
	@Override
	public JPanel buildCompanyJPanel (ShareCompany aShareCompany, boolean aActingPlayer) {
		JPanel tShareCompanyJPanel;
		
		tShareCompanyJPanel = null;

		return tShareCompanyJPanel;
	}
	
	public String canUpgradeToPrussian (PrivateCompany aPrivateCompany) {
		String tToolTip;
		FormPrussian tFormPrussian;
		
		tFormPrussian = (FormPrussian) formCompany;
		
		tToolTip = GUI.EMPTY_STRING;
		
		if (prussianIsForming (tFormPrussian)) {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " is Forming";
		} else {
			tToolTip = tFormPrussian.formingShareCompany.getName () + " has NOT formed yet";
		}
		
		return tToolTip;
	}

	private boolean prussianIsForming (FormPrussian aFormPrussian) {
		return aFormPrussian.formingShareCompany.isFormed ();
	}

	public void buildSpecialButtons (PrivateCompany aPrivateCompany, JPanel aCompanyJPanel, boolean aActingPlayer) {
		KButton tUpgradeToPrussian;
		String tToolTip;
		FormPrussian tFormPrussian;
		Corporation tCorporation;
		
		tFormPrussian = (FormPrussian) formCompany;
		if (aActingPlayer) {
			tToolTip = canUpgradeToPrussian (aPrivateCompany);
		} else {
			tToolTip = NOT_ACTING_PRESIDENT;
		}

		tUpgradeToPrussian = tFormPrussian.buildSpecialButton (UPGRADE_TO_PRUSSIAN, UPGRADE_TO_PRUSSIAN, 
				tToolTip, this);
		if (prussianIsForming (tFormPrussian)) {
			tUpgradeToPrussian.setEnabled (false);
		}
		tCorporation = getExchangeCorporation (aPrivateCompany);
		if (tCorporation != Corporation.NO_CORPORATION) {
			aCompanyJPanel.add (tUpgradeToPrussian);
			aCompanyJPanel.add (Box.createHorizontalStrut (10));
			aPrivateCompany.addSpecialButton (tUpgradeToPrussian);
		}
	}
}
