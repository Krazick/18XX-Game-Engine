package ge18xx.train;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.map.HexMap;
import ge18xx.phase.PhaseInfo;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.ClearAllRoutesAction;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.Logger;

/**
 * This class manages the Train Revenue Frame that will show all of the trains the company owns, and allows you to
 * operate the train. The Revenues the train produces will be shown in the Frame. It allows the Route to be reset,
 * and reuse the previous route run by the train.
 * 
 * @author Mark J Smith
 * @version 1.0
 *
 */
public class TrainRevenueFrame extends JFrame implements ActionListener, PropertyChangeListener,
		ItemListener {

	/**
	 * 
	 */
	private String NOT_YOUR_COMPANY = "This is not your company operating";
	private static final long serialVersionUID = 1L;
	private static final String CONFIRM_ROUTE_ACTION = "DoConfirmRouteAction";
	private static final String RESET_ROUTE_ACTION = "DoResetRouteAction";
	private static final String CONFIRM_ALL_REVENUES_ACTION = "DoConfirmAllRevenuesAction";
	private static final String CANCEL_ACTION = "DoCancelAction";
	private static final String SELECT_ROUTE_ACTION = "DoSelectRouteAction";
	private static final String REUSE_ROUTE_ACTION = "doReuseRouteAction";
	private static final String NO_NOTIFICATION = "";
	public static final TrainRevenueFrame NO_TRAIN_REVENUE_FRAME = null;

	String LAST_REVENUE = "Last Round Revenue ";
	String THIS_REVENUE = "This Round Revenue ";
	String SELECT_ROUTE = "Select Route";
	String CONFIRM_ROUTE = "Confirm Route";
	String RUNNING_ROUTE = "Running";
	String CONFIRM_ALL_REVENUES = "Confirm All Revenues";
	String CANCEL = "Cancel";
	String RESET_ROUTE = "Reset Route";
	String REUSE_ROUTE = "Reuse Route";
	int maxTrainCount = 5;
	int maxStops = 15;
	TrainCompany trainCompany;
	JLabel title;
	JLabel presidentLabel;
	JLabel lastRevenueLabel;
	JLabel thisRevenueLabel;
	JLabel notificationLabel;
	JButton confirm;
	JButton cancel;
	JButton [] selectRoutes;
	JButton [] resetRoutes;
	JButton [] reuseRoutes;
	JPanel allFramesJPanel;
	JPanel allRevenuesJPanel;
	JPanel buttonsJPanel;
	JFormattedTextField [] [] revenuesByTrain;
	JLabel [] totalRevenueByEachTrain;
	private int lastRevenue;
	private int thisRevenue;
	boolean yourCompany;
	boolean frameSetup;
	Logger logger;
	
	/**
	 * This will construct the basic Train Revenue Frame
	 * 
	 * @param aTrainCompany
	 * @param aTitle
	 * @throws HeadlessException
	 */
	
	public TrainRevenueFrame (TrainCompany aTrainCompany, String aTitle) throws HeadlessException {
		super (aTitle);
		
		trainCompany = aTrainCompany;
		title = new JLabel ();
		title.setAlignmentX (CENTER_ALIGNMENT);
		updatePresidentLabel ();
		allRevenuesJPanel = null;
		setRevenueValues (aTrainCompany);
		buildRevenuesJPanel ();
		buildsButtonsJPanel ();
		presidentLabel.setAlignmentX (CENTER_ALIGNMENT);
		buildAllFramesJPanel (aTrainCompany);
		
		revenuesByTrain = new JFormattedTextField [maxTrainCount] [maxStops];
		totalRevenueByEachTrain = new JLabel [maxTrainCount];
		selectRoutes = new JButton [maxTrainCount];
		resetRoutes = new JButton [maxTrainCount];
		reuseRoutes = new JButton [maxTrainCount];
		pack ();
		updateFrameSize ();
		setYourCompany (true);
		setFrameSetup (false);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		logger = Game_18XX.getLoggerX ();
	}

	public void setRevenueValues (TrainCompany aTrainCompany) {
		int tThisRevenue, tLastRevenue;
		
		tThisRevenue = aTrainCompany.getThisRevenue ();
		tLastRevenue = aTrainCompany.getLastRevenue ();
		setThisRevenue (tThisRevenue);
		setLastRevenue (tLastRevenue);
	}
	
	private void setThisRevenue (int aThisRevenue) {
		thisRevenue = aThisRevenue;
		updateThisRevenueLabel ();
	}
	
	private void setLastRevenue (int aLastRevenue) {
		lastRevenue = aLastRevenue;
		updateLastRevenueLabel ();
	}

	private void updateLastRevenueLabel () {
		String tFormattedRevenue;
		
		tFormattedRevenue = formatRevenue (lastRevenue);
		if (lastRevenueLabel != null) {
			lastRevenueLabel.setText (LAST_REVENUE + tFormattedRevenue);
		}
	}
	
	private void updateThisRevenueLabel () {
		String tFormattedRevenue;
		
		tFormattedRevenue = formatRevenue (thisRevenue);
		if (thisRevenueLabel != null) {
			thisRevenueLabel.setText (THIS_REVENUE + tFormattedRevenue);
		}
	}

	public String formatRevenue (int aRevenueValue) {
		String tFormattedRevenue;
		
		if (aRevenueValue == TrainCompany.NO_REVENUE_GENERATED) {
			tFormattedRevenue = TrainCompany.NO_REVENUE;
		} else {
			tFormattedRevenue = Bank.formatCash (aRevenueValue);
		}

		return tFormattedRevenue;
	}
	
	private void buildAllFramesJPanel (TrainCompany aTrainCompany) {
		BoxLayout tLayoutY;
		
		allFramesJPanel = new JPanel ();
		tLayoutY = new BoxLayout (allFramesJPanel, BoxLayout.Y_AXIS);
		allFramesJPanel.setLayout (tLayoutY);
		allFramesJPanel.setAlignmentY (CENTER_ALIGNMENT);
		allFramesJPanel.setAlignmentX (CENTER_ALIGNMENT);
		
		lastRevenueLabel = new JLabel (LAST_REVENUE + "NOT SET YET");
		lastRevenueLabel.setAlignmentX (CENTER_ALIGNMENT);

		thisRevenueLabel = new JLabel (THIS_REVENUE + "NOT SET YET");
		thisRevenueLabel.setAlignmentX (CENTER_ALIGNMENT);

		notificationLabel = new JLabel (NO_NOTIFICATION);
		notificationLabel.setAlignmentX (CENTER_ALIGNMENT);
		
		setRevenueValues (aTrainCompany);

		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (title);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (presidentLabel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (lastRevenueLabel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (allRevenuesJPanel);
		allFramesJPanel.add (Box.createVerticalStrut (10));		
		allFramesJPanel.add (thisRevenueLabel);
		allFramesJPanel.add (Box.createVerticalStrut (10));		
		allFramesJPanel.add (notificationLabel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (buttonsJPanel);
		add (allFramesJPanel);
	}

	private void buildsButtonsJPanel () {
		FlowLayout tFlowLayout = new FlowLayout ();
		
		buttonsJPanel = new JPanel (tFlowLayout);
		confirm = setupButton (CONFIRM_ALL_REVENUES, CONFIRM_ALL_REVENUES_ACTION);
		buttonsJPanel.add (confirm);
		
		cancel = setupButton (CANCEL, CANCEL_ACTION);
		buttonsJPanel.add (cancel);
	}

	public void updatePresidentLabel () {
		String tTextLabel;
		
		tTextLabel = "President: " + trainCompany.getPresidentName ();
		if (presidentLabel == null) {
			presidentLabel = new JLabel (tTextLabel);
		} else {
			presidentLabel.setText (tTextLabel);
		}
	}
	
	public void updateNotificationLabel (String aNotification) {
		if (aNotification != GUI.NULL_STRING) {
			if (aNotification.length () > 0) {
				aNotification = "WARNING: " + aNotification;
			}
			notificationLabel.setText (aNotification);
		}
	}
	
	public void setYourCompany (boolean aYourCompany) {
		yourCompany = aYourCompany;
	}
	
	private boolean isYourCompany () {
		return yourCompany;
	}
	
	private JButton setupButton (String aTitle, String aAction) {
		JButton tButton;
		
		tButton = new JButton (aTitle);
		tButton.setActionCommand (aAction);
		tButton.addActionListener (this);

		return tButton;
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		
		tActionCommand = aEvent.getActionCommand ();
		if (CONFIRM_ALL_REVENUES_ACTION.equals (tActionCommand)) {
			handleCommitAllRevenues ();
		}
		if (CANCEL_ACTION.equals (tActionCommand)) {
			handleCancelAction ();
		}
		if (SELECT_ROUTE_ACTION.equals (tActionCommand)) {
			handleSelectRoute (aEvent);
		}
		if (CONFIRM_ROUTE_ACTION.equals (tActionCommand)) {
			handleConfirmRoute (aEvent);
		}
		if (RESET_ROUTE_ACTION.equals (tActionCommand)) {
			handleResetRoute (aEvent);
		}
		if (REUSE_ROUTE_ACTION.equals (tActionCommand)) {
			handleReuseRoute (aEvent);
		}
	}

	private void handleCommitAllRevenues () {
		int tAllTrainRevenue;
		
		tAllTrainRevenue = addAllTrainRevenues ();
		trainCompany.setThisRevenue (tAllTrainRevenue);
		trainCompany.trainsOperated (tAllTrainRevenue);
		copyAllRoutesToPrevious ();
		setVisible (false);
	}

	private void handleCancelAction () {
		clearAllTrainRoutes ();
		trainCompany.exitSelectRouteMode ();
		setVisible (false);
	}
	
	public void clearAllTrainRoutes () {
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;
		ClearAllRoutesAction tClearRouteAction;
		String tOperatingRoundID;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			clearRouteFromTrain (tTrain);
		}
		tOperatingRoundID = trainCompany.getOperatingRoundID ();
		tClearRouteAction = new ClearAllRoutesAction (ActorI.ActionStates.OperatingRound, tOperatingRoundID, trainCompany);
		tClearRouteAction.addClearAllTrainsFromMapEffect (trainCompany);
		trainCompany.addAction (tClearRouteAction);
		updateAllFrameButtons ();
		trainCompany.repaintMapFrame ();
	}

	private void clearRouteFromTrain (Train aTrain) {
		RouteInformation tRouteInformation;
		
		tRouteInformation = aTrain.getCurrentRouteInformation ();
		if (tRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			int tTrainIndex, tTrainCount;
			
			tTrainCount = trainCompany.getTrainCount ();
			for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
				if (trainCompany.getTrain (tTrainIndex) == aTrain) {
					trainCompany.clearTrainFromMap (tTrainIndex);
				}
			}
//			tRouteInformation.clearTrainFromMap ();
			aTrain.setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
		}
		aTrain.setOperating (false);
	}

	private void copyAllRoutesToPrevious () {
		int tTrainIndex;
		int tTrainCount;
		RouteInformation tRouteInformation;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tRouteInformation = tTrain.getCurrentRouteInformation ();
			tTrain.setPreviousRouteInformation (tRouteInformation);
		}
	}

	public void restoreAllRoutesFromPrevious () {
		int tTrainIndex;
		int tTrainCount;
		RouteInformation tRouteInformation;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tRouteInformation = tTrain.getPreviousRouteInformation ();
			tTrain.setCurrentRouteInformation (tRouteInformation);
		}
	}

	public void clearAllRevenueValues () {
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			clearRevenuesFromTrain (tTrainIndex, tTrain);
		}
	}
	
	private void clearRevenuesFromTrain (int aTrainIndex, Train aTrain) {
		int tCityCount, tCityIndex;
		
		tCityCount = aTrain.getCityCount ();
		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			if (revenuesByTrain [aTrainIndex] [tCityIndex] != null) {
				revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
			}
		}	
	}
	
	private void handleConfirmRoute (ActionEvent aConfirmRouteEvent) {
		JButton tConfirmRouteButton = (JButton) aConfirmRouteEvent.getSource ();
		int tTrainIndex, tTrainCount;
		Train tTrain;
		RouteInformation tRouteInformation;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tConfirmRouteButton.equals (selectRoutes [tTrainIndex])) {
				tTrain = trainCompany.getTrain (tTrainIndex);
				tRouteInformation = tTrain.getCurrentRouteInformation ();
				fillRevenueForTrain (tRouteInformation, tTrain, tTrainIndex);
				tTrain.setOperating (false);
				trainCompany.exitSelectRouteMode ();
			}
		}
		updateAllFrameButtons ();
	}

	private void handleResetRoute (ActionEvent aResetRouteEvent) {
		JButton tResetRouteButton = (JButton) aResetRouteEvent.getSource ();
		int tTrainIndex, tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tResetRouteButton.equals (resetRoutes [tTrainIndex])) {
				resetTrainRoute (tTrainIndex);
//				trainCompany.exitSelectRouteMode ();
				trainCompany.clearTrainFromMap (tTrainIndex);
			}
		}
		updateAllFrameButtons ();
	}

	private void handleReuseRoute (ActionEvent aReuseRouteEvent) {
		JButton tReuseRouteButton;
		int tTrainIndex, tTrainCount;
		
		tReuseRouteButton = (JButton) aReuseRouteEvent.getSource ();
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tReuseRouteButton.equals (reuseRoutes [tTrainIndex])) {
				reuseTrainRoute (tTrainIndex);
				trainCompany.exitSelectRouteMode ();
			}
		}
		updateAllFrameButtons ();
	}
	
	// Steps needed:
	// ** 1. Clone the Previous Route into a new Current Route Object
	// ** 2. Highlight the Current Route on the Map Frame, Tile by Tile, Track by Track
	// ** 3. Fill the Train Revenue Frame with the Revenues for the Route
	//    4. Create new RouteAction, with all of the Effects to allow all tiles, tracks, revenue centers to be filled
	//       just as if they were selected tile by tile, track by track, center by center with the mouse
	//    5. The RouteAction should not need "Cycle Route Effects", but every extendRouteEffect should be included 
	//       in the single Route Action
	//    6. Add the RouteAction
	//		 a. If this is a Network Game, this action should be sent to other Clients
	//    7. Optional: Allow for route Extension, by selecting a cell, and code should find which end of the route
	//       to Extend
	//    8. Verify that none of the Track Segments on the Route has been marked as in-use (ie the Operator runs a
	//       different train first manually, and then tried to Reuse a Route flagged for the train).
	
	// If a Tile on the Route being re-used has been upgraded, then:
	//    1. If this upgrade has a Revenue Center where the Revenue Value has increased, be sure use higher value
	//    2. If this upgrade has a Revenue Center where the Revenue Value is based on the Phase, be sure to apply
	//       The correct value for the current phase
	//    3. If the Tile had a "down-graded" Revenue Center, to no Revenue Center, clear the Revenue Value
	// The Route must be validated to confirm it is open for the company to operate (not blocked by Station Markers)
	
	private void reuseTrainRoute (int aTrainIndex) {
		Train tTrain;
		RouteInformation tCurrentRouteInformation;
		
		tTrain = trainCompany.getTrain (aTrainIndex);
		trainCompany.clearTrainFromMap (aTrainIndex);
		tTrain.setOperating (true);
		setupNewRouteInformation (tTrain);
		highlightRouteSegments (tTrain);
		tCurrentRouteInformation = tTrain.getCurrentRouteInformation ();
		tCurrentRouteInformation.addReuseRouteAction (tTrain);
	}
	
	private void highlightRouteSegments (Train aTrain) {
		RouteInformation tRouteInformation;
		MapFrame tMapFrame;
		GameManager tGameManager;
		HexMap tMap;
		
		tGameManager = trainCompany.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();
		tMap = tMapFrame.getMap ();
		tRouteInformation = aTrain.getCurrentRouteInformation ();
		tRouteInformation.highlightRouteSegments (tMap);
	}
	
	private void setupNewRouteInformation (Train aTrain) {
		RouteInformation tRouteInformation;
		RouteInformation tRouteToReuse;
		String tRoundID;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		tRouteToReuse = aTrain.getPreviousRouteInformation ();
		tRoundID = trainCompany.getOperatingRoundID ();
		tPhaseInfo = trainCompany.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		
		tRouteInformation = new RouteInformation (tRouteToReuse, tRoundID, tPhase);
		
		tRouteInformation.copyRouteSegments (tRouteToReuse);
		tRouteInformation.updateReusedRoute (tPhase);
		aTrain.setCurrentRouteInformation (tRouteInformation);
	}

	private void resetTrainRoute (int aTrainIndex) {
		Train tTrain;
		
		tTrain = trainCompany.getTrain (aTrainIndex);
		clearRevenuesFromTrain (aTrainIndex, tTrain);
		clearRouteFromTrain (tTrain);
		tTrain.setOperating (false);
	}
			
	private void fillRevenueForTrain (RouteInformation aRouteInformation, Train aTrain, int aTrainIndex) {
		int tCityCount;
		int tCityIndex;
		int tRevenue;
		
		tCityCount = aTrain.getCityCount ();
		
		for (tCityIndex = 0; tCityIndex < aTrain.getCityCount (); tCityIndex++) {
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
		}
		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			tRevenue = aRouteInformation.getRevenueAt (tCityIndex);
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (tRevenue);
		}
	}
	
	public void handleSelectRoute (ActionEvent aSelectRouteEvent) {
		JButton tSelectRouteButton = (JButton) aSelectRouteEvent.getSource ();
		int tTrainIndex, tTrainCount;
		Train tTrain;
		Color tColor = Color.BLUE;
		String tRoundID;
		int tRegionBonus = 0, tSpecialBonus = 0;
		RouteInformation tRouteInformation;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		tTrainCount = trainCompany.getTrainCount ();
		tPhaseInfo = trainCompany.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		tRoundID = trainCompany.getOperatingRoundID ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tSelectRouteButton.equals (selectRoutes [tTrainIndex])) {
				trainCompany.clearTrainFromMap (tTrainIndex);
				tTrain = trainCompany.getTrain (tTrainIndex);
				tTrain.clearRouteInformation ();
				tRouteInformation = new RouteInformation (tTrain, tTrainIndex, tColor, tRoundID, tRegionBonus, tSpecialBonus, 
						tPhase, trainCompany, this);
				tTrain.setOperating (true);
				trainCompany.enterSelectRouteMode (tRouteInformation);
			}
		}
		updateNotificationLabel (NO_NOTIFICATION);
		updateAllFrameButtons ();
	}
	
	public void enableResetRoute (JButton aResetRouteButton, String aToolTipText) {
		aResetRouteButton.setText (RESET_ROUTE);
		aResetRouteButton.setActionCommand (RESET_ROUTE_ACTION);
		aResetRouteButton.setToolTipText (aToolTipText);
	}
	
	/**
	 * This returns the sum of all of the trains the company owns that have been collected by the trains
	 * 
	 * @return the Total of all Trains revenues
	 */
	
	public int addAllTrainRevenues () {
		int tAllTrainRevenues, tTrainCount, tTrainIndex, tCityCount, tTrainRevenue;
		Train tTrain;
		
		tAllTrainRevenues = 0;
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			tTrainRevenue = addTrainRevenues (tTrainIndex, tCityCount);
			tAllTrainRevenues += tTrainRevenue;
		}
		
		return tAllTrainRevenues;
	}
	
	/**
	 * This returns the sum of a single Train Revenues, provided the Train Index and the count of the cities
	 * The actual Revenue values summed are from the Train Revenue Frame Labels filled with the Revenues
	 * 
	 * @param aTrainIndex The index of which train to sum up
	 * @param aCityCount The total number of Revenue Fields to sum up
	 * @return Total Revenue value of the RevenueLabels for the specific train
	 */
	
	public int addTrainRevenues (int aTrainIndex, int aCityCount) {
		int tTotalRevenue, tCityIndex;
		JFormattedTextField tRevenueField;
		Object tRevenueValue;
		
		tTotalRevenue = 0;
		for (tCityIndex = 0; tCityIndex < aCityCount; tCityIndex++) {
			tRevenueField = revenuesByTrain [aTrainIndex] [tCityIndex];
			if (tRevenueField != null) {
				tRevenueValue = tRevenueField.getValue ();
				if (tRevenueValue != null) {
					tTotalRevenue += ((Number) tRevenueValue).intValue ();
				}
			}
		}
		
		return tTotalRevenue;
	}
	
	private void buildRevenuesJPanel () {
		int tTrainCount, tTrainIndex;
		JPanel tTrainRevenueJPanel;
		
		tTrainCount = trainCompany.getTrainCount ();
		
		if (allRevenuesJPanel == GUI.NO_PANEL) {
			allRevenuesJPanel = new JPanel ();
		} else {
			allRevenuesJPanel.removeAll ();
		}
		allRevenuesJPanel.setAlignmentX (CENTER_ALIGNMENT);
		
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tTrainIndex > 0) {
				allRevenuesJPanel.add (Box.createVerticalStrut (10));
			}
			
			tTrainRevenueJPanel = buildTrainRevenueJPanel (tTrainIndex);
			allRevenuesJPanel.add (tTrainRevenueJPanel);
		}
	}

	private JPanel buildTrainRevenueJPanel (int aTrainIndex) {
		JLabel tTrainLabel;
		int tCityCount;
		JPanel tTrainRevenueJPanel;
		BoxLayout tLayoutX;
		Train tTrain;
		
		// TODO: for a N+N Train, need to get maximum City Count
		// TODO: QUICK FIX, the "getCityCount" will return a Maximum of maxStops (15) to allow Diesels to Operate
		// MUST Figure out way to give Diesels an infinite length
		
		tTrain = trainCompany.getTrain (aTrainIndex);
		tCityCount = tTrain.getCityCount ();
		tTrainRevenueJPanel = new JPanel ();
		tLayoutX = new BoxLayout (tTrainRevenueJPanel, BoxLayout.X_AXIS);
		tTrainRevenueJPanel.setLayout (tLayoutX);
		tTrainLabel = new JLabel (tTrain.getName () + " Train #" + (aTrainIndex + 1));
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (10));
		tTrainRevenueJPanel.add (tTrainLabel);
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (10));
		buildRevenuesByTrain (aTrainIndex, tCityCount, tTrainRevenueJPanel);
		totalRevenueByEachTrain [aTrainIndex] = new JLabel ("0");
		tTrainRevenueJPanel.add (totalRevenueByEachTrain [aTrainIndex]);
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (5));
		
		selectRoutes [aTrainIndex] = setupButton (SELECT_ROUTE, SELECT_ROUTE_ACTION);
		updateSelectRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (selectRoutes [aTrainIndex]);
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (5));
		resetRoutes [aTrainIndex] = setupButton (RESET_ROUTE, RESET_ROUTE_ACTION);
		updateResetRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (resetRoutes [aTrainIndex]);
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (5));
		reuseRoutes [aTrainIndex] = setupButton (REUSE_ROUTE, REUSE_ROUTE_ACTION);
		updateReuseRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (reuseRoutes [aTrainIndex]);
		tTrainRevenueJPanel.add (Box.createHorizontalStrut (10));
		
		return tTrainRevenueJPanel;
	}

	private void buildRevenuesByTrain (int aTrainIndex, int aCityCount, JPanel aTrainRevenueJPanel) {
		int tCityIndex;
		Dimension tTextFieldSize = new Dimension (30, 20);
		
		for (tCityIndex = 0; tCityIndex < aCityCount; tCityIndex++) {
			revenuesByTrain [aTrainIndex] [tCityIndex] = new JFormattedTextField ();
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
			revenuesByTrain [aTrainIndex] [tCityIndex].setColumns (3);
			revenuesByTrain [aTrainIndex] [tCityIndex].setHorizontalAlignment (JTextField.RIGHT);
			revenuesByTrain [aTrainIndex] [tCityIndex].setMaximumSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCityIndex].setPreferredSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCityIndex].setMinimumSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCityIndex].addPropertyChangeListener ("value", this);
			aTrainRevenueJPanel.add (revenuesByTrain [aTrainIndex] [tCityIndex]);
			aTrainRevenueJPanel.add (Box.createHorizontalStrut (3));
			if ((tCityIndex + 1) < aCityCount) {
				aTrainRevenueJPanel.add (new JLabel ("+"));
			} else {
				aTrainRevenueJPanel.add (new JLabel ("="));
			}
			aTrainRevenueJPanel.add (Box.createHorizontalStrut (3));
		}
	}
	
	public void updateRevenues (RouteInformation aRouteInformation) {
		int tTrainIndex;
		Train tTrain;
		
		tTrainIndex = aRouteInformation.getTrainIndex ();
		tTrain = aRouteInformation.getTrain ();
		fillRevenueForTrain (aRouteInformation, tTrain, tTrainIndex);
	}
	
	@Override
	public void itemStateChanged (ItemEvent e) {

	}

	public void operateTrains (Point aFrameOffset) {
		setLastRevenue (trainCompany.getLastRevenue ());
		setThisRevenue (trainCompany.getThisRevenue ());
		updateInfo ();
		if (! trainCompany.isOperatingTrains ()) {
			clearAllRevenueValues ();
		}
		setLocation (aFrameOffset);
		setYourCompany (true);
		setVisible (true);
	}
	
	public boolean allRoutesValid () {
		boolean tAllRoutesValid = true;
		int tTrainIndex, tTrainCount;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();

		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex); 
			if (tTrain != Train.NO_TRAIN) {
				if (! tTrain.isCurrentRouteValid ())  {
					tAllRoutesValid = false;
				}
			}
		}
		
		return tAllRoutesValid;
	}
	
	public boolean allRevenuesValid (int aTrainIndex, int aCityCount) {
		int tCityIndex;
		boolean tAllRevenuesValid = true;
		
		for (tCityIndex = 0; (tCityIndex < aCityCount) && tAllRevenuesValid; tCityIndex++) {
			tAllRevenuesValid = tAllRevenuesValid && isValidRevenue (aTrainIndex, tCityIndex);
		}		
		
		return tAllRevenuesValid;
	}

	public boolean isValidRevenue (int aTrainIndex, int aCityIndex) {
		boolean tIsValidRevenue = true;
		JFormattedTextField tRevenueField;
		Object tRevenueValue;
		int tRevenue;

		tRevenueField = revenuesByTrain [aTrainIndex] [aCityIndex];
		if (tRevenueField != null) {
			tRevenueValue = tRevenueField.getValue ();
			if (tRevenueValue != null) {
				tRevenue = ((Number) tRevenueValue).intValue ();
				if (! multipleOfTen (tRevenue)) {
					tIsValidRevenue = false;
				}
				if (tRevenue < 0) {
					tIsValidRevenue = false;
				}
			}
		}

		return tIsValidRevenue;
	}
	
	public boolean multipleOfTen (int aValue) {
		boolean tMultipleOfTen = false;
		int tOneTenth = aValue/10;
		
		if ((tOneTenth * 10) == aValue) {
			tMultipleOfTen = true;
		}
		
		return tMultipleOfTen;
	}
	
	private boolean allTrainRevenuesAreValid () {
		boolean tAllTrainRevenuesAreValid = true;
		Train tTrain;
		int tCityCount, tTrainIndex, tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			
			if (! allRevenuesValid (tTrainIndex, tCityCount)) {
				tAllTrainRevenuesAreValid = false;
			}
		}
		
		return tAllTrainRevenuesAreValid;
	}
	
	@Override
	public void propertyChange (PropertyChangeEvent aEvent) {
		Object tSource = aEvent.getSource ();
		int tTotalRevenue;
		
		tTotalRevenue = getTotalRevenue (tSource);
		setThisRevenue (tTotalRevenue);
		updateAllFrameButtons ();
	}

	private int getTotalRevenue (Object aSource) {
		int tTrainCount;
		int tTrainIndex;
		int tCityCount;
		int tCityIndex;
		int tTotalRevenue;
		int tTrainRevenue;
		
		Train tTrain;
		tTrainCount = trainCompany.getTrainCount ();
		tTotalRevenue = 0;
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			tTrainRevenue = addTrainRevenues (tTrainIndex, tCityCount);
			
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				if (revenuesByTrain [tTrainIndex] [tCityIndex] == aSource) {
					totalRevenueByEachTrain [tTrainIndex].setText (Bank.formatCash (tTrainRevenue));
				}
			}
			tTotalRevenue += tTrainRevenue;
		}
		
		return tTotalRevenue;
	}
	
	public void updateFrameSize () {
		int tTrainCount;
		int tMaxTrainSize;
		int tWidth, tHeight;
		
		tTrainCount = trainCompany.getTrainCount ();
		tMaxTrainSize = trainCompany.getMaxTrainSize ();
		tWidth = 530 + tMaxTrainSize * 50;
		tHeight = 200 + (tTrainCount * 57);
		setSize (tWidth, tHeight);
	}
	
	public void updateInfo () {
		String tTitleText;
		String tTrainInfo;
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		if (tTrainCount == 0) {
			tTrainInfo = "No Trains";
		} else if (tTrainCount == 1) {
			tTrainInfo = "1 Train";
		} else {
			tTrainInfo = tTrainCount + " Trains";
		}
		tTitleText = "Train Revenue for " + trainCompany.getName () + " with " + tTrainInfo;
		title.setText (tTitleText);
		updatePresidentLabel ();
		updateLastRevenueLabel ();
		if (! isVisible ()) {
			buildRevenuesJPanel ();
		}
		updateThisRevenueLabel ();
		updateFrameSize ();
		setFrameSetup (true);
	}
	
	public void setFrameSetup (boolean aFrameSetup) {
		frameSetup = aFrameSetup;
	}
	
	public void udpateConfirmAllRoutesButton () {
		if (confirm != null) {
			if (isYourCompany ()) {
				if (anyTrainIsOperating ()) {
					confirm.setEnabled (false);
					confirm.setToolTipText ("A Train is Operating");
				} else if (! allRoutesValid ()) {
					confirm.setEnabled (false);
					confirm.setToolTipText ("One or more Routes are Invalid");
				} else if (! allTrainRevenuesAreValid ()) {
					confirm.setEnabled (false);
					confirm.setToolTipText ("One or more Revenues are Invalid");
				} else {
					confirm.setEnabled (true);
					confirm.setToolTipText ("Confirm that all Routes and Revenues are Accepted");		
				}
			} else {
				confirm.setEnabled (false);
				confirm.setToolTipText (NOT_YOUR_COMPANY);
			}
		}
	}
	
	public void updateSelectRouteButton (int aTrainIndex) {
		Train tTrain;
		RouteInformation tRouteInformation;
		String tToolTip;
		String tWarningMessage;
		int tRouteCode;
		
		if (isValidIndex (aTrainIndex)) {
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				if (tTrain.isOperating ()) {
					tRouteInformation = tTrain.getCurrentRouteInformation ();
					if (tRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
						enableSelectRouteButton (aTrainIndex, CONFIRM_ROUTE, CONFIRM_ROUTE_ACTION, "Confirm Route");
						disableSelectRouteButton (aTrainIndex, "Route is not yet valid");
					} else if (tRouteInformation.isRouteTooLong ()) {
						tToolTip = "Route is Too Long. Max length is " + tTrain.getName ();
						disableSelectRouteButton (aTrainIndex, tToolTip);
						updateNotificationLabel (tToolTip);
					} else {
						tRouteCode = tRouteInformation.isValidRoute ();
						tWarningMessage = tRouteInformation.getWarningMessage ();
						updateNotificationLabel (tWarningMessage);
						if (tRouteCode == 1) {
							enableSelectRouteButton (aTrainIndex, CONFIRM_ROUTE, CONFIRM_ROUTE_ACTION, "Confirm Route");
						} else {
							tToolTip = "No Train for company, or other unspecified error";
							switch (tRouteCode) {
								case -1:
								case -2:
									tToolTip = "Route must have at least two Revenue Centers";
									break;
								case -3:
									tToolTip = "Route has no Corporate Station";
									break;
								case -4:
									tToolTip = "Route has a Loop that reuses the same Revenue Center";
									updateNotificationLabel (tToolTip);
									break;
								case -5:
									tToolTip = "Route is Blocked by other Corporation Stations";
									updateNotificationLabel (tToolTip);
									break;
							}
							disableSelectRouteButton (aTrainIndex, tToolTip);
						}
					}
				} else {
					if (anyTrainIsOperating ()) {
						disableSelectRouteButton (aTrainIndex, "Another Train is Operating");			
					} else {
						enableSelectRouteButton (aTrainIndex, SELECT_ROUTE, SELECT_ROUTE_ACTION, "Select Route");					
					}
				}
			} else {
				disableSelectRouteButton (aTrainIndex, NOT_YOUR_COMPANY);			
			}
		} else {
			logger.error ("TrainIndex of " + aTrainIndex + " is out of range");
		}
	}
	
	private boolean anyTrainIsOperating () {
		return trainCompany.anyTrainIsOperating ();
	}
	
	private void disableSelectRouteButton (int aTrainIndex, String aToolTipText) {
		selectRoutes [aTrainIndex].setEnabled (false);
		selectRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}
	
	private void enableSelectRouteButton (int aTrainIndex, String aButtonLabel, String aAction, String aToolTipText) {
		selectRoutes [aTrainIndex].setText (aButtonLabel);
		selectRoutes [aTrainIndex].setActionCommand (aAction);
		selectRoutes [aTrainIndex].setEnabled (true);
		selectRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}

	private void disableResetRouteButton (int aTrainIndex, String aToolTipText) {
		resetRoutes [aTrainIndex].setEnabled (false);
		resetRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}
	
	private void enableResetRouteButton (int aTrainIndex, String aToolTipText) {
		resetRoutes [aTrainIndex].setEnabled (true);
		resetRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}
	
	private void disableReuseRouteButton (int aTrainIndex, String aToolTipText) {
		reuseRoutes [aTrainIndex].setEnabled (false);
		reuseRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}
	
	private void enableReuseRouteButton (int aTrainIndex, String aToolTipText) {
		reuseRoutes [aTrainIndex].setEnabled (true);
		reuseRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}
	
	private boolean isValidIndex (int aTrainIndex) {
		return (aTrainIndex >= 0) && (aTrainIndex < trainCompany.getTrainCount ());
	}
	
	public void updateAllFrameButtons () {
		updateNotificationLabel (NO_NOTIFICATION);
		updateSelectRouteButtons ();
		updateResetRouteButtons ();
		updateReuseRouteButtons ();
		udpateConfirmAllRoutesButton ();
		updateCancelButton ();
	}
	
	private void updateSelectRouteButtons () {
		int tTrainIndex;
	
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (selectRoutes [tTrainIndex] != null) {
				updateSelectRouteButton (tTrainIndex);
			}
		}
	}
	
	private void updateResetRouteButtons () {
		int tTrainIndex;
	
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (resetRoutes [tTrainIndex] != null) {
				updateResetRouteButton (tTrainIndex);
			}
		}
	}
	
	private void updateReuseRouteButtons () {
		int tTrainIndex;
	
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (reuseRoutes [tTrainIndex] != null) {
				updateReuseRouteButton (tTrainIndex);
			}
		}
	}
	
	private boolean totalRevenueIsZero (int aTrainIndex) {
		boolean tTotalRevenueIsZero = false;
		String tTotalRevenueForTrain;
		
		tTotalRevenueForTrain = totalRevenueByEachTrain [aTrainIndex].getText ();
		if (tTotalRevenueForTrain.equals ("0")) {
			tTotalRevenueIsZero = true;
		}
		
		return tTotalRevenueIsZero;
	}
	
	private void updateReuseRouteButton (int aTrainIndex) {
		Train tTrain;
		RouteInformation tPreviousRouteInformation;
		
		if (isValidIndex (aTrainIndex)) {
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				tPreviousRouteInformation = tTrain.getPreviousRouteInformation ();
				if (tPreviousRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
					disableReuseRouteButton (aTrainIndex, "No Previous Route Found to use");
				} else if (tPreviousRouteInformation.canBeReused ()) {
					if (anyTrainIsOperating ()) {
						disableReuseRouteButton (aTrainIndex, "Another Train is Operating");
					} else {
						enableReuseRouteButton (aTrainIndex, "Ready to use");
					}
				} else if (tTrain.isOperating ()) {
					disableReuseRouteButton (aTrainIndex, "This Train is already Operating");
				} else {
					disableReuseRouteButton (aTrainIndex, "At least one Route Segment Track is in Use");
				}
			} else {
				disableReuseRouteButton (aTrainIndex, NOT_YOUR_COMPANY);
			}
		}
	}
	
	/**
	 *  This method will update the status for the ResetRoute Button for the Nth train owned by this Company
	 *  
	 * @param aTrainIndex To identify which button to update.
	 */
	
	private void updateResetRouteButton (int aTrainIndex) {
		Train tTrain;
		RouteInformation tRouteInformation;
		
		if (isValidIndex (aTrainIndex)) {
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				if (tTrain.isOperating ()) {
					tRouteInformation = tTrain.getCurrentRouteInformation ();
					if (tRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
						if (totalRevenueIsZero (aTrainIndex)) {
							disableResetRouteButton (aTrainIndex, "No Route yet to reset");
						} else {
							enableResetRouteButton (aTrainIndex, "Reset Train's Route and Revenues");
						}
					} else {
						enableResetRouteButton (aTrainIndex, "Reset Train's Route and Revenues");
					}
				} else {
					if (anyTrainIsOperating ()) {
						disableResetRouteButton (aTrainIndex, "Another Train is Operating");
					} else {
						tRouteInformation = tTrain.getCurrentRouteInformation ();
						if (tRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
							disableResetRouteButton (aTrainIndex, "No Route yet to reset");
						} else {
							enableResetRouteButton (aTrainIndex, "Reset Train's Route and Revenues");
						}
					}
				}
			} else {
				disableResetRouteButton (aTrainIndex, NOT_YOUR_COMPANY);
			}
		} else {
			logger.error ("TrainIndex of " + aTrainIndex + " is out of range");
		}
	}

	private void updateCancelButton () {
		if (isYourCompany ()) {
			cancel.setEnabled (true);
			cancel.setToolTipText ("");
		} else {
			cancel.setEnabled (false);
			cancel.setToolTipText (NOT_YOUR_COMPANY);
		}
	}
	
	/**
	 * This method will disable All of the Buttons on the Frame
	 * 
	 */
	
	public void disableAll () {
		int tTrainIndex, tCityIndex, tCityCount;
		Train tTrain;
		boolean tEnabled;
		
		tEnabled = false;
		setYourCompany (false);
		udpateConfirmAllRoutesButton ();
		updateCancelButton ();
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			updateSelectRouteButton (tTrainIndex);
			updateResetRouteButton (tTrainIndex);
			updateReuseRouteButton (tTrainIndex);
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				revenuesByTrain [tTrainIndex] [tCityIndex].setEditable (tEnabled);
			}
		}
	}
}
