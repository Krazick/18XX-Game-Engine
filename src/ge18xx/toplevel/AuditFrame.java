package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ge18xx.bank.Bank;
import ge18xx.company.Corporation;
import ge18xx.company.CorporationList;
import ge18xx.company.ShareCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ActorI.ActorTypes;

public class AuditFrame extends TableFrame implements ItemListener {
	DefaultTableModel auditModel = new DefaultTableModel (0, 0);
	JTable auditTable;
	int actorBalance;
	String actorName;
	String actorAbbrev;
	ActorI.ActorTypes actorType;
	JComboBox <String> companyCombo;
	JComboBox <String> playerCombo;
	CorporationList companies;
	GameManager gameManager;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuditFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getGameName ());
		
		setGameManager (aGameManager);
		setCompanies (aGameManager.getShareCompanies ());
		String [] tColumnNames = {"#", "Round", "Actor", "Action / Event", "Debit", "Credit", "Balance"};
		int tColWidths [] = {50, 50, 110, 1000, 50, 50, 70};
		int tTotalWidth = 0;
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer ();
		rightRenderer.setHorizontalAlignment (SwingConstants.RIGHT);
		
		auditTable = new JTable ();

		auditModel.setColumnIdentifiers (tColumnNames);
		auditTable.setModel (auditModel);
		auditTable.setGridColor (Color.BLACK);
		auditTable.setShowGrid (true);
		auditTable.setShowVerticalLines (true);
		auditTable.setShowHorizontalLines (true);
		
		TableColumnModel tColumnModel = auditTable.getColumnModel ();
		
		for (int tIndex = 0; tIndex < tColWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (tColWidths [tIndex]);
			
			tTotalWidth += tColWidths [tIndex] + 1;
		}
		auditTable.getColumnModel ().getColumn (0).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (4).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (5).setCellRenderer (rightRenderer);
		auditTable.getColumnModel ().getColumn (6).setCellRenderer (rightRenderer);
		setLocation (100, 100);
		setSize (tTotalWidth, 400);
		setScrollPane (auditTable);
		
		// Test Components
		JPanel tNorthComponents = new JPanel ();
		
		companyCombo = new JComboBox <String> ();
		tNorthComponents.add (companyCombo);
		updateCorpComboBox ();
		
		playerCombo = new JComboBox <String> ();
		tNorthComponents.add (playerCombo);
		updatePlayerComboBox ();
		
		companyCombo.addItemListener (this);
		playerCombo.addItemListener (this);
		add (tNorthComponents, BorderLayout.NORTH);
		setActorType (ActorTypes.ShareCompany);
	}
	
	public void setActorType (ActorI.ActorTypes aActorType) {
		actorType = aActorType;
	}
	
	public ActorI.ActorTypes getActorType () {
		return actorType;
	}
	
	public void setGameManager (GameManager aGameManager) {
		gameManager = aGameManager;
	}
	
    @SuppressWarnings("static-access")
	public void itemStateChanged (ItemEvent aItemEvent) {
    	JComboBox<String> tThisComboBox;
    	
        if (aItemEvent.getStateChange () == aItemEvent.SELECTED) {
        	if (aItemEvent.getSource() instanceof JComboBox) {
           		removeAllRows ();
           		@SuppressWarnings ("unchecked")
				JComboBox<String> source = (JComboBox<String>) aItemEvent.getSource ();
				tThisComboBox = source;
        		if (tThisComboBox.equals (companyCombo)) {
	        		setSelectedShareCompany ();
        		} else if (tThisComboBox.equals (playerCombo)) {
        			setSelectedPlayer ();
        		}
         		gameManager.fillAuditFrame (actorName);
        	}
        }
    }
    
	public void setCompanies (CorporationList aCompanies) {
		companies = aCompanies;
	}
	
	public void setActorBalance (int aActorBalance) {
		actorBalance = aActorBalance;
	}
	
	public void setActorName (String aActorName) {
		actorName = aActorName;
	}
	
	public void setActorAbbrev (String aActorAbbev) {
		actorAbbrev = aActorAbbev;
	}
	
	public String getActorName () {
		return actorName;
	}
	
	public int getActorBalance () {
		return actorBalance;
	}
	
	public void addToActorBalance (int aCredit) {
		actorBalance += aCredit;
	}
	
	public void subtractFromActorBalance (int aDebit) {
		actorBalance -= aDebit;
	}
	
	public void addRow (int aActionNumber, String aRoundID, String aActionEvent, int aDebit, int aCredit) {
		addToActorBalance (aCredit);
		subtractFromActorBalance (aDebit);
		auditModel.addRow (new Object [] {aActionNumber, aRoundID, actorAbbrev, aActionEvent, aDebit, aCredit, actorBalance});
	}
	
	public void removeAllRows () {
		int tRowCount, tRowIndex;
		
		tRowCount = auditModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			auditModel.removeRow( 0);
		}
	}
	
	public void setSelectedPlayer () {
		String tPlayerName;
		int tStartingCash;
		
		tPlayerName = (String) playerCombo.getSelectedItem ();
		setActorName (tPlayerName);
		setActorAbbrev (tPlayerName);
		setActorType (ActorI.ActorTypes.Player);
		tStartingCash = gameManager.getStartingCash ();
		setActorBalance (0);
		addRow (0, "Start", "Initial Capital fron Bank of " + Bank.formatCash (tStartingCash), 0,tStartingCash);
		setActorBalance (tStartingCash);
	}
	
	public void setSelectedShareCompany () {
		ShareCompany tShareCompany;
		String tCompanyAbbrev;
		
		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		tShareCompany = (ShareCompany) companies.getCorporation (tCompanyAbbrev);
		setActorName (tShareCompany.getName ());
		setActorAbbrev (tShareCompany.getAbbrev ());
		setActorType (ActorI.ActorTypes.ShareCompany);
		setActorBalance (0);
	}
	
	public void updatePlayerComboBox () {
		PlayerManager tPlayerManager;
		int tPlayerCount, tPlayerIndex;
		Player tPlayer;
		String tPlayerName;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayerCount = tPlayerManager.getPlayerCount ();
		if (tPlayerCount > 0) {
			for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
				tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
				tPlayerName = tPlayer.getName ();
				if (! isPlayerInComboBox (tPlayerName)) {
					playerCombo.addItem (tPlayerName);
				}
			}
		}
	}
	
	public boolean isPlayerInComboBox (String aPlayerName) {
		boolean tPlayerInComboBox = false;
		int tCount, tIndex;
		String tPlayerFound;
		
		tCount = playerCombo.getItemCount ();
		for (tIndex = 0; tIndex < tCount; tIndex++) {
			tPlayerFound = playerCombo.getItemAt (tIndex);
			if (tPlayerFound.equals (aPlayerName)) {
				tPlayerInComboBox = true;
			}
		
		}
		
		return tPlayerInComboBox;
	}
	
	public void updateCorpComboBox () {
		int tIndex;
		int tCorpCount;
		Corporation tCorporation;
		String tAbbrev;
		
		companyCombo.removeAllItems ();
		if (companies != null) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
					tCorporation = companies.getCorporation (tIndex);
					if (tCorporation != CorporationList.NO_CORPORATION) {
						tAbbrev = tCorporation.getAbbrev ();
						companyCombo.addItem (tAbbrev);
					}
				}
			}
		}
	}

}
