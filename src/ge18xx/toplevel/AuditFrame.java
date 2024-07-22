package ge18xx.toplevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;

public class AuditFrame extends XMLFrame implements ItemListener, ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String BASE_TITLE = "Audit";
	public static final int NO_CREDIT = 0;
	public static final int NO_DEBIT = 0;
	private String REFRESH_LIST = "REFRESH LIST";
	private String DRAW_LINE_GRAPH = "DRAW_LINE_GRAPH";
	private String BANK_PREFIX = "Bank";
	private String PLAYER_PREFIX = "Player: ";
	private String SHARE_CORP_PREFIX = "Share Company: ";
	DefaultTableModel auditModel = new DefaultTableModel (0, 7);
	JTable auditTable;
	JComboBox<String> companyCombo;
	JComboBox<String> playerCombo;
	JComboBox<String> actorsCombo;
	KButton refreshList;
	KButton lineGraph;
	ActorI.ActorTypes actorType;
	CorporationList companies;
	int actorBalance;
	String actorName;
	String actorAbbrev;

	public AuditFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager);
		
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		setCompanies (tGameManager.getShareCompanies ());
		String [] tColumnNames = { "#", "Round", "Actor", "Action / Event", "Debit", "Credit", "Balance" };
		int tColWidths[] = { 50, 50, 110, 1000, 50, 50, 70 };
		int tTotalWidth = 0;

		buildAuditTable (tColumnNames, tColWidths, tTotalWidth);

		// Test Components
		JPanel tNorthComponents = buildNorthComponents ();

		add (tNorthComponents, BorderLayout.NORTH);
		setActorType (ActorTypes.Player);
	}

	/**
	 * Update the Frame, and specifically updateFrameTitle (from super class XMLFrame) with the static BASE_TITLE provided
	 */
	public void updateFrame () {
		updateFrameTitle (BASE_TITLE);
	}

	private JPanel buildNorthComponents () {
		JPanel tNorthComponents = new JPanel ();

		companyCombo = new JComboBox<> ();
		playerCombo = new JComboBox<> ();
		actorsCombo = new JComboBox<> ();
		tNorthComponents.add (actorsCombo);
		updateActorsComboBox ();

		refreshList = new KButton ("Refresh List");
		refreshList.setActionCommand (REFRESH_LIST);
		refreshList.addActionListener (this);
		tNorthComponents.add (refreshList);

		lineGraph = new KButton ("Draw Line Graph");
		lineGraph.setActionCommand (DRAW_LINE_GRAPH);
		lineGraph.addActionListener (this);
		tNorthComponents.add (lineGraph);
		lineGraph.setVisible (false);

		return tNorthComponents;
	}

	private void buildAuditTable (String [] aColumnNames, int [] aColWidths, int aTotalWidth) {
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer ();

		rightRenderer.setHorizontalAlignment (SwingConstants.RIGHT);
		auditTable = new JTable ();

		auditModel.setColumnIdentifiers (aColumnNames);
		auditTable.setModel (auditModel);
		auditTable.setGridColor (Color.BLACK);
		auditTable.setShowGrid (true);
		auditTable.setShowVerticalLines (true);
		auditTable.setShowHorizontalLines (true);

		TableColumnModel tColumnModel = auditTable.getColumnModel ();

		for (int tIndex = 0; tIndex < aColWidths.length; tIndex++) {
			tColumnModel.getColumn (tIndex).setMaxWidth (aColWidths [tIndex]);

			aTotalWidth += aColWidths [tIndex] + 1;
		}
		setColumnAlign (0, SwingConstants.RIGHT);
		setColumnAlign (4, SwingConstants.RIGHT);
		setColumnAlign (5, SwingConstants.RIGHT);
		setColumnAlign (6, SwingConstants.RIGHT);
		setLocation (100, 100);
		setSize (aTotalWidth, 400);
		buildScrollPane (auditTable);
	}

	private void setColumnAlign (int aColumnIndex, int tAlignment) {
		DefaultTableCellRenderer tCellRenderer = new DefaultTableCellRenderer ();

		tCellRenderer.setHorizontalAlignment (tAlignment);
		auditTable.getColumnModel ().getColumn (aColumnIndex).setHeaderRenderer (tCellRenderer);
		auditTable.getColumnModel ().getColumn (aColumnIndex).setCellRenderer (tCellRenderer);
	}

	public void setActorType (ActorI.ActorTypes aActorType) {
		actorType = aActorType;
	}

	public ActorI.ActorTypes getActorType () {
		return actorType;
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
		String tActorShown;

		tActorShown = getActorToShow ();
		addToActorBalance (aCredit);
		subtractFromActorBalance (aDebit);
		auditModel.addRow (
				new Object [] { aActionNumber, aRoundID, tActorShown, aActionEvent, aDebit, aCredit, actorBalance });
	}

	private String getActorToShow () {
		String tActorShown;

		if (actorAbbrev != null) {
			tActorShown = actorAbbrev;
		} else {
			tActorShown = actorName;
		}

		return tActorShown;
	}

	public void removeAllRows () {
		int tRowCount, tRowIndex;

		tRowCount = auditModel.getRowCount ();
		for (tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			auditModel.removeRow (0);
		}
	}

	public void updateActorsCombo () {
		String tActorName;

		tActorName = getSelectedActorName ();
		if (!actorName.endsWith (tActorName)) {
			System.err.println ("Need to update Pulldown Menu to match Actor");
		}
	}

	private String getSelectedActorName () {
		return (String) actorsCombo.getSelectedItem ();
	}

	private boolean setSelectedActor () {
		String tActorName;
		boolean tIsPlayer = false;

		tActorName = getSelectedActorName ();
		if (tActorName.startsWith (PLAYER_PREFIX)) {
			tActorName = tActorName.substring (PLAYER_PREFIX.length ());
			tIsPlayer = setSelectedPlayer (tActorName);
		} else if (tActorName.startsWith (SHARE_CORP_PREFIX)) {
			tActorName = tActorName.substring (SHARE_CORP_PREFIX.length ());
			setSelectedShareCompany (tActorName);
		} else if (tActorName.startsWith (BANK_PREFIX)) {
			setSelectedBank ();
		}

		return tIsPlayer;
	}

	public boolean setSelectedPlayer () {
		String tPlayerName;
		boolean tPlayer;

		tPlayerName = (String) playerCombo.getSelectedItem ();
		tPlayer = setSelectedPlayer (tPlayerName);

		return tPlayer;
	}

	private boolean setSelectedPlayer (String aPlayerName) {
		boolean tPlayer = true;

		setActorName (aPlayerName);
		setActorAbbrev (aPlayerName);
		setActorType (ActorI.ActorTypes.Player);

		return tPlayer;
	}

	private void setSelectedBank () {
		Bank tBank;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tBank = tGameManager.getBank ();
		setActorName (tBank.getName ());
		setActorAbbrev (tBank.getName ());
		setActorType (ActorI.ActorTypes.Bank);
	}

	private void setPlayerStartingCash () {
		int tStartingCash;
		String tActionDescription;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tActionDescription = "Initial Capital fron Bank of ";

		tStartingCash = tGameManager.getStartingCash ();
		setStartingCash (tStartingCash, tActionDescription);
	}

	private void setBankStartingCash () {
		int tStartingCash;
		String tActionDescription;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tActionDescription = "Initial Capital of Bank ";

		tStartingCash = tGameManager.getBankStartingCash ();
		setStartingCash (tStartingCash, tActionDescription);
	}

	private void setStartingCash (int aStartingCash, String aActionDescription) {
		int tFirstActionNumber;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tFirstActionNumber = 0;
		if (tGameManager.isNetworkGame ()) {
			tFirstActionNumber += 100;
		}
		setActorBalance (0);
		addRow (tFirstActionNumber, "Start", aActionDescription + Bank.formatCash (aStartingCash), NO_DEBIT,
				aStartingCash);
		setActorBalance (aStartingCash);
	}

	public void setSelectedShareCompany () {
		String tCompanyAbbrev;

		tCompanyAbbrev = (String) companyCombo.getSelectedItem ();
		setSelectedShareCompany (tCompanyAbbrev);
	}

	private void setSelectedShareCompany (String aCompanyAbbrev) {
		ShareCompany tShareCompany;

		tShareCompany = (ShareCompany) companies.getCorporation (aCompanyAbbrev);
		setActorName (tShareCompany.getName ());
		setActorAbbrev (tShareCompany.getAbbrev ());
		setActorType (ActorI.ActorTypes.ShareCompany);
		setActorBalance (0);
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

	public void updateActorsComboBox () {
		PlayerManager tPlayerManager;
		int tPlayerCount, tPlayerIndex;
		int tCorpCount, tCorpIndex;
		Player tPlayer;
		Corporation tCorporation;
		String tActorName;
		Bank tBank;
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		tPlayerManager = tGameManager.getPlayerManager ();
		tPlayerCount = tPlayerManager.getPlayerCount ();

		// As we add Actors we don't want to recursively call itemListener
		actorsCombo.removeAllItems ();
		if (tPlayerCount > 0) {
			for (tPlayerIndex = 0; tPlayerIndex < tPlayerCount; tPlayerIndex++) {
				tPlayer = tPlayerManager.getPlayer (tPlayerIndex);
				tActorName = PLAYER_PREFIX + tPlayer.getName ();
				if (!isActorsInComboBox (tActorName)) {
					actorsCombo.addItem (tActorName);
				}
			}
		}

		if (companies != CorporationList.NO_CORPORATION_LIST) {
			tCorpCount = companies.getRowCount ();
			if (tCorpCount > 0) {
				for (tCorpIndex = 0; tCorpIndex < tCorpCount; tCorpIndex++) {
					tCorporation = companies.getCorporation (tCorpIndex);
					if (tCorporation != Corporation.NO_CORPORATION) {
						tActorName = SHARE_CORP_PREFIX + tCorporation.getAbbrev ();
						if (!isActorsInComboBox (tActorName)) {
							actorsCombo.addItem (tActorName);
						}
					}
				}
			}
		}
		tBank = tGameManager.getBank ();
		actorsCombo.addItem (tBank.getName ());
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
	public void itemStateChanged (ItemEvent aItemEvent) {
		if (aItemEvent.getStateChange () == ItemEvent.SELECTED) {
			if (aItemEvent.getSource () instanceof JComboBox) {
				refreshAuditTable (aItemEvent);
			}
		}
	}

	@Override
	public void actionPerformed (ActionEvent aActionEvent) {
		String tTheAction;

		tTheAction = aActionEvent.getActionCommand ();
		if (REFRESH_LIST.equals (tTheAction)) {
			updateAuditTable ();
		}
	}

	private void updateAuditTable () {
		boolean tPlayer = false;

		if (actorType.equals (ActorI.ActorTypes.Player)) {
			tPlayer = setSelectedPlayer (actorName);
		} else if (actorType.equals (ActorI.ActorTypes.ShareCompany)) {
			setSelectedShareCompany (actorAbbrev);
		} else if (actorType.equals (ActorI.ActorTypes.Bank)) {
			setSelectedBank ();
		}
		refreshAuditTable (tPlayer);
	}

	private void refreshAuditTable (ItemEvent aItemEvent) {
		JComboBox<String> tThisComboBox;
		boolean tPlayer = false;

		@SuppressWarnings ("unchecked")
		JComboBox<String> source = (JComboBox<String>) aItemEvent.getSource ();
		tThisComboBox = source;
		if (tThisComboBox.equals (actorsCombo)) {
			tPlayer = setSelectedActor ();
		} else if (tThisComboBox.equals (companyCombo)) {
			setSelectedShareCompany ();
		} else if (tThisComboBox.equals (playerCombo)) {
			tPlayer = setSelectedPlayer ();
		}

		refreshAuditTable (tPlayer);
	}

	public void refreshAuditTable (boolean aPlayer) {
		GameManager tGameManager;
		
		tGameManager = (GameManager) gameEngineManager;
		removeAllRows ();
		if (aPlayer) {
			setPlayerStartingCash ();
		}
		if (actorType == ActorI.ActorTypes.Bank) {
			setBankStartingCash ();
		}
		tGameManager.fillAuditFrame (actorName);
	}
}
