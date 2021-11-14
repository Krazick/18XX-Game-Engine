package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
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

public class AuditFrame extends TableFrame implements ItemListener, ActionListener {
	DefaultTableModel auditModel = new DefaultTableModel (0, 0);
	private String REFRESH_LIST = "REFRESH LIST";
	private String PLAYER_PREFIX = "Player: ";
	private String SHARE_CORP_PREFIX = "Share Company: ";
	public static int NO_CREDIT = 0;
	public static int NO_DEBIT = 0;
	JTable auditTable;
	int actorBalance;
	String actorName;
	String actorAbbrev;
	ActorI.ActorTypes actorType;
	JComboBox <String> companyCombo;
	JComboBox <String> playerCombo;
	JComboBox <String> actorsCombo;
	JButton refreshList;
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
		
		playerCombo = new JComboBox <String> ();
		
		actorsCombo = new JComboBox <String> ();
		tNorthComponents.add (actorsCombo);
		updateActorsComboBox ();
		
		refreshList = new JButton ("Refresh List");
		refreshList.setActionCommand (REFRESH_LIST);
		refreshList.addActionListener (this);
		tNorthComponents.add (refreshList);
		
		add (tNorthComponents, BorderLayout.NORTH);
		setActorType (ActorTypes.Player);
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
	
    @Override
	public void itemStateChanged (ItemEvent aItemEvent) {
    	JComboBox<String> tThisComboBox;
    	
        if (aItemEvent.getStateChange () == ItemEvent.SELECTED) {
        	if (aItemEvent.getSource() instanceof JComboBox) {
           		removeAllRows ();
           		@SuppressWarnings ("unchecked")
				JComboBox<String> source = (JComboBox<String>) aItemEvent.getSource ();
				tThisComboBox = source;
				if (tThisComboBox.equals (actorsCombo)) {
					setSelectedActor ();
				} else if (tThisComboBox.equals (companyCombo)) {
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
	
    public void updateActorsCombo () {
    	String tActorName;
    	
    	tActorName = getSelectedActorName ();
    	if (! actorName.endsWith (tActorName)) {
    		System.out.println ("Need to update Pulldown Menu to match Actor");
    	}
    }

    private String getSelectedActorName () {
    	return (String) actorsCombo.getSelectedItem ();
    }
    
	private void setSelectedActor () {
		String tActorName;
		
		tActorName = getSelectedActorName ();
		System.out.println ("Selected Actor is " + tActorName);
		if (tActorName.startsWith (PLAYER_PREFIX)) {
			tActorName = tActorName.substring (PLAYER_PREFIX.length ());
			setSelectedPlayer (tActorName);
		} else if (tActorName.startsWith (SHARE_CORP_PREFIX)) {
			tActorName = tActorName.substring (SHARE_CORP_PREFIX.length ());
			setSelectedShareCompany (tActorName);
		}
	}
	
	public void setSelectedPlayer () {
		String tPlayerName;
		
		tPlayerName = (String) playerCombo.getSelectedItem ();
		setSelectedPlayer (tPlayerName);
	}

	private void setSelectedPlayer (String aPlayerName) {
		int tStartingCash;
		
		System.err.println ("Selected Player is: [" + aPlayerName + "]");
		setActorName (aPlayerName);
		setActorAbbrev (aPlayerName);
		setActorType (ActorI.ActorTypes.Player);
		tStartingCash = gameManager.getStartingCash ();
		setActorBalance (0);
		addRow (0, "Start", "Initial Capital fron Bank of " + Bank.formatCash (tStartingCash), NO_DEBIT, tStartingCash);
		setActorBalance (tStartingCash);
	}
	
	public void setSelectedShareCompany () {
		String tCompanyAbbrev;
		
		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		setSelectedShareCompany(tCompanyAbbrev);
	}

	private void setSelectedShareCompany (String aCompanyAbbrev) {
		ShareCompany tShareCompany;
		
		System.err.println ("Selected Company is: [" + aCompanyAbbrev + "]");

		tShareCompany = (ShareCompany) companies.getCorporation (aCompanyAbbrev);
		setActorName (tShareCompany.getName ());
		setActorAbbrev (tShareCompany.getAbbrev ());
		setActorType (ActorI.ActorTypes.ShareCompany);
		setActorBalance (0);
	}
	
//	public void updatePlayerComboBox () {
//		PlayerManager tPlayerManager;
//		int tPlayerCount, tPlayerIndex;
//		Player tPlayer;
//		String tPlayerName;
//		
//		tPlayerManager = gameManager.getPlayerManager ();
//		tPlayerCount = tPlayerManager.getPlayerCount ();
//		if (tPlayerCount > 0) {
//			for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
//				tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
//				tPlayerName = tPlayer.getName ();
//				if (! isPlayerInComboBox (tPlayerName)) {
//					playerCombo.addItem (tPlayerName);
//				}
//			}
//		}
//	}
	
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
	
//	public void updateCorpComboBox () {
//		int tIndex;
//		int tCorpCount;
//		Corporation tCorporation;
//		String tAbbrev;
//		
//		if (companies != null) {
//			tCorpCount = companies.getRowCount ();
//			if (tCorpCount > 0) {
//				for (tIndex = 0; tIndex < tCorpCount; tIndex++) {
//					tCorporation = companies.getCorporation (tIndex);
//					if (tCorporation != CorporationList.NO_CORPORATION) {
//						tAbbrev = tCorporation.getAbbrev ();
//						companyCombo.addItem (tAbbrev);
//					}
//				}
//			}
//		}
//	}
	
	public void updateActorsComboBox () {
		PlayerManager tPlayerManager;
		int tPlayerCount, tPlayerIndex;
		int tCorpCount, tCorpIndex;
		Player tPlayer;
		Corporation tCorporation;
		String tActorName;
		
		tPlayerManager = gameManager.getPlayerManager ();
		tPlayerCount = tPlayerManager.getPlayerCount ();
		
		// As we add Actors we don't want to recursively call itemListener
		actorsCombo.removeAllItems ();
		if (tPlayerCount > 0) {
			for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
				tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
				tActorName = PLAYER_PREFIX + tPlayer.getName ();
				if (! isActorsInComboBox (tActorName)) {
					actorsCombo.addItem (tActorName);
				}
			}
		}
		
		if (companies != null) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tCorpIndex = 0; tCorpIndex < tCorpCount; tCorpIndex++) {
					tCorporation = companies.getCorporation (tCorpIndex);
					if (tCorporation != Corporation.NO_CORPORATION) {
						tActorName = SHARE_CORP_PREFIX + tCorporation.getAbbrev ();
						if (! isActorsInComboBox (tActorName)) {
							actorsCombo.addItem (tActorName);
						}
					}
				}
			}
		}
		actorsCombo.addItemListener (this);
	}
	
	public boolean isActorsInComboBox (String aActorsName) {
		boolean tActorInComboBox = false;
		int tCount, tIndex;
		String tActorFound;
		
		tCount = actorsCombo.getItemCount ();
		for (tIndex = 0; tIndex < tCount; tIndex++) {
			tActorFound = actorsCombo.getItemAt (tIndex);
			if (tActorFound.equals (aActorsName)) {
				tActorInComboBox = true;
			}
		}
		
		return tActorInComboBox;
	}

	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction = aActionEvent.getActionCommand ();
		if (REFRESH_LIST.equals (tTheAction)) {
			removeAllRows ();
			if (actorType.equals (ActorI.ActorTypes.Player)) {
				setSelectedPlayer (actorName);
			} else if (actorType.equals (ActorI.ActorTypes.ShareCompany)) {
				setSelectedShareCompany (actorAbbrev);
			}
			gameManager.fillAuditFrame (actorName);
		}
	}
}
