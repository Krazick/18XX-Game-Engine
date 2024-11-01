package ge18xx.train;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.Logger;

import ge18xx.bank.Bank;
import ge18xx.center.RevenueCenter;
import ge18xx.company.ShareCompany;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.game.Game_18XX;
import ge18xx.map.HexMap;
import ge18xx.phase.PhaseInfo;
import ge18xx.tiles.Track;
import ge18xx.toplevel.MapFrame;

import geUtilities.xml.XMLFrame;
import swingTweaks.KButton;
import geUtilities.GUI;

/**
 * This class manages the Train Revenue Frame that will show all of the trains
 * the company owns, and allows you to operate the train. The Revenues the train
 * produces will be shown in the Frame. It allows the Route to be reset, and
 * reuse the previous route run by the train.
 *
 * @author Mark J Smith
 * @version 1.0
 *
 */
public class TrainRevenueFrame extends XMLFrame implements ActionListener, PropertyChangeListener, ItemListener {
	private static final long serialVersionUID = 1L;
	private static final String CONFIRM_ROUTE_ACTION = "DoConfirmRouteAction";
	private static final String RESET_ROUTE_ACTION = "DoResetRouteAction";
	private static final String CONFIRM_ALL_REVENUES_ACTION = "DoConfirmAllRevenuesAction";
	private static final String CANCEL_ACTION = "DoCancelAction";
	private static final String SELECT_ROUTE_ACTION = "DoSelectRouteAction";
	private static final String REUSE_ROUTE_ACTION = "doReuseRouteAction";
	private static final String NO_NOTIFICATION = "";
	private static final String NOT_YOUR_COMPANY = "This is not your company operating";
	private static final int MAX_TRAIN_COUNT = 5;
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
	TrainCompany trainCompany;
	JLabel title;
	JLabel presidentLabel;
	JLabel lastRevenueLabel;
	JLabel thisRevenueLabel;
	JLabel notificationLabel;
	KButton confimAllRoutes;
	KButton cancel;
	KButton [] confirmRoutes;
	KButton [] selectRoutes;
	KButton [] resetRoutes;
	KButton [] reuseRoutes;
	JPanel allFramesJPanel;
	JPanel allRevenuesJPanel;
	JPanel loanInfoPanel;
	JPanel buttonsJPanel;
	JFormattedTextField [] [] revenuesByTrain;
	JFormattedTextField [] revenuesByPlusTrain;
	int [] plusUsedCount;
	JLabel [] totalRevenueByEachTrain;
	private int lastRevenue;
	private int thisRevenue;
	private int revenueContribution;
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
		super (aTitle, aTrainCompany.getGameManager ());

		trainCompany = aTrainCompany;
		title = new JLabel ();
		title.setAlignmentX (CENTER_ALIGNMENT);
		updatePresidentLabel ();
		allRevenuesJPanel = GUI.NO_PANEL;
		setRevenueValues (aTrainCompany);
		loanInfoPanel = new JPanel ();
		buildRevenuesJPanel ();
		buildButtonsJPanel ();
		presidentLabel.setAlignmentX (CENTER_ALIGNMENT);
		buildAllFramesJPanel (aTrainCompany);

		revenuesByTrain = new JFormattedTextField [MAX_TRAIN_COUNT] [Train.MAX_STOPS];
		revenuesByPlusTrain = new JFormattedTextField [MAX_TRAIN_COUNT];
		plusUsedCount = new int [MAX_TRAIN_COUNT];
		totalRevenueByEachTrain = new JLabel [MAX_TRAIN_COUNT];
		confirmRoutes = new KButton [MAX_TRAIN_COUNT];
		selectRoutes = new KButton [MAX_TRAIN_COUNT];
		resetRoutes = new KButton [MAX_TRAIN_COUNT];
		reuseRoutes = new KButton [MAX_TRAIN_COUNT];
		pack ();
		updateFrameSize ();
		setYourCompany (true);
		setFrameSetup (false);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		logger = Game_18XX.getLoggerX ();
	}

	public void setRevenueValues (TrainCompany aTrainCompany) {
		int tThisRevenue;
		int tPreviousRevenue;

		tThisRevenue = aTrainCompany.getThisRevenue ();
		tPreviousRevenue = aTrainCompany.getPreviousRevenue ();
		setThisRevenue (tThisRevenue);
		setPreviousRevenue (tPreviousRevenue);
	}

	public void setThisRevenue (int aThisRevenue) {
		thisRevenue = aThisRevenue;
		updateThisRevenueLabel ();
	}

	private void setPreviousRevenue (int aPreviousRevenue) {
		lastRevenue = aPreviousRevenue;
		updateLastRevenueLabel ();
	}

	private void setRevenueContribution (int aRevenueContribution) {
		revenueContribution = aRevenueContribution;
	}
	
	public int getRevenueContribution () {
		return revenueContribution;
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
		allFramesJPanel.add (loanInfoPanel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (buttonsJPanel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		add (allFramesJPanel);
	}

	private void buildButtonsJPanel () {
		FlowLayout tFlowLayout;

		tFlowLayout = new FlowLayout ();
		confimAllRoutes = setupButton (CONFIRM_ALL_REVENUES, CONFIRM_ALL_REVENUES_ACTION, this, Component.CENTER_ALIGNMENT);
		cancel = setupButton (CANCEL, CANCEL_ACTION, this, Component.CENTER_ALIGNMENT);

		buttonsJPanel = new JPanel (tFlowLayout);
		buttonsJPanel.add (confimAllRoutes);
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
		int tOldThisRevenue;
		
		tAllTrainRevenue = addAllTrainRevenues ();
		tOldThisRevenue = trainCompany.getThisRevenue ();
		trainCompany.setThisRevenue (tAllTrainRevenue);
		trainCompany.trainsOperated (tAllTrainRevenue, tOldThisRevenue);
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

		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tTrain.setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
			tTrain.setOperating (false);
		}
		trainCompany.clearAllTrainsFromMap (true);
		updateAllFrameButtons ();
		trainCompany.repaintMapFrame ();
	}

	private void clearRouteFromTrain (Train aTrain) {
		RouteInformation tRouteInformation;

		tRouteInformation = aTrain.getCurrentRouteInformation ();
		if (tRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			trainCompany.clearATrainFromMap (aTrain);
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

	public void clearRevenuesFromTrain (int aTrainIndex, Train aTrain) {
		int tCityCount;
		int tCityIndex;

		tCityCount = aTrain.getCityCount ();
		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			if (revenuesByTrain [aTrainIndex] [tCityIndex] != null) {
				revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
			}
		}
		if (aTrain.isPlusTrain ()) {
			if (revenuesByPlusTrain [aTrainIndex] != null) {
				revenuesByPlusTrain [aTrainIndex].setValue (0);
				plusUsedCount [aTrainIndex] = 0;
			}
		}
	}

	private void handleConfirmRoute (ActionEvent aConfirmRouteEvent) {
		KButton tConfirmRouteButton;
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;
		RouteInformation tRouteInformation;

		tTrainCount = trainCompany.getTrainCount ();
		tConfirmRouteButton = (KButton) aConfirmRouteEvent.getSource ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tConfirmRouteButton.equals (confirmRoutes [tTrainIndex])) {
				tTrain = trainCompany.getTrain (tTrainIndex);
				tRouteInformation = tTrain.getCurrentRouteInformation ();
				fillRevenueForTrain (tRouteInformation, tTrain, tTrainIndex);
				tTrain.setOperating (false);
				showSelectRouteButton (tTrainIndex);
				trainCompany.exitSelectRouteMode ();
			}
		}
		updateAllFrameButtons ();
	}

	private void handleResetRoute (ActionEvent aResetRouteEvent) {
		KButton tResetRouteButton;
		int tTrainIndex;
		int tTrainCount;

		tResetRouteButton = (KButton) aResetRouteEvent.getSource ();
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tResetRouteButton.equals (resetRoutes [tTrainIndex])) {
				resetTrainRoute (tTrainIndex);
			}
		}
		trainCompany.exitSelectRouteMode ();
		updateAllFrameButtons ();
	}

	private void handleReuseRoute (ActionEvent aReuseRouteEvent) {
		KButton tReuseRouteButton;
		int tTrainIndex;
		int tTrainCount;

		tReuseRouteButton = (KButton) aReuseRouteEvent.getSource ();
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tReuseRouteButton.equals (reuseRoutes [tTrainIndex])) {
				reuseTrainRoute (tTrainIndex);
				trainCompany.exitSelectRouteMode ();
			}
		}
		updateAllFrameButtons ();
	}

	private void reuseTrainRoute (int aTrainIndex) {
		Train tTrain;
		RouteInformation tCurrentRouteInformation;

		tTrain = trainCompany.getTrain (aTrainIndex);
		trainCompany.clearATrainFromMap (aTrainIndex, true);
		tTrain.setOperating (true);
		setupNewRouteInformation (tTrain, aTrainIndex);
		highlightRouteSegments (tTrain);
		tCurrentRouteInformation = tTrain.getCurrentRouteInformation ();
		tCurrentRouteInformation.addReuseRouteAction (tTrain);
	}

	private void highlightRouteSegments (Train aTrain) {
		RouteInformation tRouteInformation;
		GameManager tGameManager;
		MapFrame tMapFrame;
		HexMap tMap;

		tGameManager = trainCompany.getGameManager ();
		tMapFrame = tGameManager.getMapFrame ();
		tMap = tMapFrame.getMap ();
		tRouteInformation = aTrain.getCurrentRouteInformation ();
		tRouteInformation.highlightRouteSegments (tMap);
	}

	private void setupNewRouteInformation (Train aTrain, int aTrainIndex) {
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
		tRouteInformation.updateReusedRoute (tPhase, trainCompany.getID ());
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
		int tPlusCurrentRevenue;
		int tPlusCount;
		int tRevenueCenterCount;
		int tRevenueCenterIndex;
		boolean tIsAPlusTrain;
		boolean tRevenueHandled;
		RevenueCenter tRevenueCenter;

		tCityCount = aTrain.getCityCount ();

		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
		}
		tIsAPlusTrain = aTrain.isPlusTrain ();
		if (tIsAPlusTrain) {
			revenuesByPlusTrain [aTrainIndex].setValue (0);
			plusUsedCount [aTrainIndex] = 0;
			tPlusCount = aTrain.getTownCount ();
		} else {
			tPlusCount = 0;
		}
		tCityIndex = 0;
		tRevenueCenterCount = aRouteInformation.getCenterCount ();
		for (tRevenueCenterIndex = 0; tRevenueCenterIndex < tRevenueCenterCount; tRevenueCenterIndex++) {
			
			tRevenueCenter = aRouteInformation.getRevenueCenterAt (tRevenueCenterIndex);
			tRevenue = aRouteInformation.getRevenueAt (tRevenueCenterIndex);
			tRevenueHandled = false;
			if (tRevenueCenter != RevenueCenter.NO_CENTER) {
				if (tIsAPlusTrain) {
					if (tRevenueCenter.isTown ())  {
						if (tPlusCount > plusUsedCount [aTrainIndex]) {
							plusUsedCount [aTrainIndex] += 1;
							tPlusCurrentRevenue = Integer.parseInt (revenuesByPlusTrain [aTrainIndex].getText ());
							revenuesByPlusTrain [aTrainIndex].setValue (tPlusCurrentRevenue + tRevenue);
							tRevenueHandled = true;
						}
					}
				}
			}
			
			if (!tRevenueHandled) {
				if (tCityIndex < tCityCount) {
					revenuesByTrain [aTrainIndex] [tCityIndex].setValue (tRevenue);
					tCityIndex++;
				}
			}

		}

	}

	public void handleSelectRoute (ActionEvent aSelectRouteEvent) {
		KButton tSelectRouteButton;
		int tTrainIndex;
		int tTrainCount;
		int tRegionBonus;
		int tSpecialBonus;
		int tPhase;
		String tRoundID;
		RouteInformation tRouteInformation;
		PhaseInfo tPhaseInfo;
		Train tTrain;
		Color tColor;

		tSelectRouteButton = (KButton) aSelectRouteEvent.getSource ();
		tRegionBonus = 0;
		tSpecialBonus = 0;
		tColor = Color.BLUE;
		tTrainCount = trainCompany.getTrainCount ();
		tPhaseInfo = trainCompany.getCurrentPhaseInfo ();
		tPhase = tPhaseInfo.getName ();
		tRoundID = trainCompany.getOperatingRoundID ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tSelectRouteButton.equals (selectRoutes [tTrainIndex])) {
				trainCompany.clearATrainFromMap (tTrainIndex);
				tTrain = trainCompany.getTrain (tTrainIndex);
				tTrain.clearRouteInformation ();
				tRouteInformation = new RouteInformation (tTrain, tTrainIndex, tColor, tRoundID, tRegionBonus,
						tSpecialBonus, tPhase, trainCompany, this);
				tTrain.setOperating (true);
				trainCompany.enterSelectRouteMode (tRouteInformation);
			}
		}
		updateNotificationLabel (NO_NOTIFICATION);
		updateAllFrameButtons ();
	}

	public void enableResetRoute (KButton aResetRouteButton, String aToolTipText) {
		aResetRouteButton.setText (RESET_ROUTE);
		aResetRouteButton.setActionCommand (RESET_ROUTE_ACTION);
		aResetRouteButton.setToolTipText (aToolTipText);
	}

	/**
	 * This returns the sum of the revenues from all of the trains the company owns
	 *
	 * @return the Total of all Trains revenues less the RevenueContribution due to Loan Interest Payments (if any)
	 */

	public int addAllTrainRevenues () {
		int tAllTrainRevenues;
		int tTrainCount;
		int tTrainIndex;
		int tTrainRevenue;
		Train tTrain;

		tAllTrainRevenues = 0;
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tTrainRevenue = addTrainRevenues (tTrainIndex, tTrain);
			tAllTrainRevenues += tTrainRevenue;
		}
		tAllTrainRevenues -= getRevenueContribution ();
		
		return tAllTrainRevenues;
	}

	/**
	 * This returns the sum of a single Train Revenues, provided the Train Index and
	 * the count of the cities The actual Revenue values summed are from the Train
	 * Revenue Frame Labels filled with the Revenues
	 *
	 * @param aTrainIndex The index of which train to sum up
	 * @param aTrain The Train to collect revenues for
	 * @return Total Revenue value of the RevenueLabels for the specific train
	 */

	public int addTrainRevenues (int aTrainIndex, Train aTrain) {
		int tTotalRevenue;
		int tCityIndex;
		int tCityCount;
		JFormattedTextField tRevenueField;
		Object tRevenueValue;

		tTotalRevenue = 0;
		tCityCount = aTrain.getCityCount ();
		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			tRevenueField = revenuesByTrain [aTrainIndex] [tCityIndex];
			if (tRevenueField != null) {
				tRevenueValue = tRevenueField.getValue ();
				if (tRevenueValue != null) {
					tTotalRevenue += ((Number) tRevenueValue).intValue ();
				}
			}
		}
		if (aTrain.isPlusTrain ()) {
			tRevenueField = revenuesByPlusTrain [aTrainIndex];
			if (tRevenueField != null) {
				tRevenueValue = tRevenueField.getValue ();
				if (tRevenueValue != null) {
					tTotalRevenue += ((Number) tRevenueValue).intValue ();
				}
			}
		}

		return tTotalRevenue;
	}

	private void updateLoanInfoPanel () {
		ShareCompany tShareCompany;
		BoxLayout tLayoutX;
		int tLoanCount;
		int tLoanInterest;
		int tTreasury;
		int tRevenueContribution;
		JLabel tLoanCountLabel;
		JLabel tLoanInterestLabel;
		JLabel tCorpTreasury;
		JLabel tRevenueContributionLabel;
	
		if (trainCompany.isAShareCompany ()) {
			tShareCompany = (ShareCompany) trainCompany;
			loanInfoPanel.removeAll ();
			tLoanCount = tShareCompany.getLoanCount ();
			if (tLoanCount > 0) {
				loanInfoPanel.setAlignmentX (CENTER_ALIGNMENT);
				tLayoutX = new BoxLayout (loanInfoPanel, BoxLayout.X_AXIS);
				loanInfoPanel.setLayout (tLayoutX);
				tLoanCountLabel = new JLabel ("Loan Count: " + tLoanCount);
				loanInfoPanel.add (tLoanCountLabel);
				loanInfoPanel.add (Box.createHorizontalStrut (10));
				
				tLoanInterest = tLoanCount * 10;
				tLoanInterestLabel = new JLabel ("Interest Owed: " + Bank.formatCash (tLoanInterest));
				loanInfoPanel.add (tLoanInterestLabel);
				loanInfoPanel.add (Box.createHorizontalStrut (10));
				
				tTreasury = tShareCompany.getCash ();
				tCorpTreasury = new JLabel ("Treasury: " + Bank.formatCash (tTreasury));
				loanInfoPanel.add (tCorpTreasury);
				loanInfoPanel.add (Box.createHorizontalStrut (10));
				
				tRevenueContribution = calculateRevenueContribution (tLoanInterest, tTreasury);
				setRevenueContribution (tRevenueContribution);
				tRevenueContributionLabel = new JLabel ("Revenue Contribution: " + 
											Bank.formatCash (tRevenueContribution));
				loanInfoPanel.add (tRevenueContributionLabel);
				loanInfoPanel.add (Box.createHorizontalStrut (10));
				
				loanInfoPanel.setVisible (true);
			} else {
				loanInfoPanel.setVisible (false);
			}
		}
	}
	
	public int calculateRevenueContribution (int tLoanInterest, int tTreasury) {
		int tRevenueContribution;
		int tRC1;
		
		tRevenueContribution = 0;
		if (tTreasury < tLoanInterest) {
			tRC1 = tLoanInterest - tTreasury;
			tRevenueContribution = ((int) (tRC1/10)) * 10;
			if (tRC1 != tRevenueContribution) {
				tRevenueContribution = ((int) ((tRC1 + 10)/10)) * 10;
			}
 		}
		tRevenueContribution = Math.min (tRevenueContribution, thisRevenue);
		tRevenueContribution = Math.max (tRevenueContribution, 0);
		
		return tRevenueContribution;
	}
	
	private void buildRevenuesJPanel () {
		int tTrainCount;
		int tTrainIndex;
		int tHeight;
		int tWidth;
		JPanel tTrainRevenueJPanel;
		BoxLayout tLayoutY;

		tTrainCount = trainCompany.getTrainCount ();

		if (allRevenuesJPanel == GUI.NO_PANEL) {
			allRevenuesJPanel = new JPanel ();
		} else {
			allRevenuesJPanel.removeAll ();
		}
		allRevenuesJPanel.setAlignmentX (CENTER_ALIGNMENT);
		tLayoutY = new BoxLayout (allRevenuesJPanel, BoxLayout.Y_AXIS);
		allRevenuesJPanel.setLayout (tLayoutY);

		allRevenuesJPanel.add (Box.createVerticalStrut (5));
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tTrainIndex > 0) {
				allRevenuesJPanel.add (Box.createVerticalStrut (10));
			}

			tTrainRevenueJPanel = buildTrainRevenueJPanel (tTrainIndex);
			allRevenuesJPanel.add (tTrainRevenueJPanel);
		}
		allRevenuesJPanel.add (Box.createVerticalStrut (5));
		tHeight = calculateTrainHeight ();
		tWidth = calculateTrainWidth ();
		allRevenuesJPanel.setSize (tHeight, tWidth);
	}

	private JPanel buildTrainRevenueJPanel (int aTrainIndex) {
		JLabel tTrainLabel;
		JLabel tBorrowedLabel;
		int tCityCount;
		JPanel tTrainRevenueJPanel;
		JPanel tTrainInfoJPanel;
		BoxLayout tLayoutX;
		BoxLayout tLayoutY;
		Train tTrain;
		Color tPaintColor;

		// TODO -- MUST Figure out way to give Diesels an infinite length

		tTrain = trainCompany.getTrain (aTrainIndex);
		tCityCount = tTrain.getCityCount ();
		tTrainRevenueJPanel = new JPanel ();
		tLayoutX = new BoxLayout (tTrainRevenueJPanel, BoxLayout.X_AXIS);
		tTrainRevenueJPanel.setLayout (tLayoutX);

		tTrainInfoJPanel = new JPanel ();
		tLayoutY = new BoxLayout (tTrainInfoJPanel, BoxLayout.Y_AXIS);
		tTrainInfoJPanel.setLayout (tLayoutY);
		
		if (tTrain.isBorrowed ()) {
			tBorrowedLabel = new JLabel ("<BORROWED>");
			tTrainInfoJPanel.add (tBorrowedLabel);
		}
		tPaintColor = (Color) Track.getPaintColor (aTrainIndex + 1);
		tTrainLabel = new JLabel ("  " + tTrain.getName () + " Train #" + (aTrainIndex + 1) + "  ");
		tTrainLabel.setOpaque (true);
		tTrainLabel.setBackground (tPaintColor);
		
		tTrainInfoJPanel.add (tTrainLabel);
		tTrainRevenueJPanel.add (tTrainInfoJPanel);
		
		buildRevenuesByTrain (aTrainIndex, tCityCount, tTrain, tTrainRevenueJPanel);
		totalRevenueByEachTrain [aTrainIndex] = new JLabel ("0");
		tTrainRevenueJPanel.add (totalRevenueByEachTrain [aTrainIndex]);

		confirmRoutes [aTrainIndex] = setupButton (CONFIRM_ROUTE, CONFIRM_ROUTE_ACTION, this, Component.CENTER_ALIGNMENT);
		selectRoutes [aTrainIndex] = setupButton (SELECT_ROUTE, SELECT_ROUTE_ACTION, this, Component.CENTER_ALIGNMENT);
		updateConfirmRouteButton (aTrainIndex);
		disableConfirmRouteButton (aTrainIndex, "Train Not Operating");
		updateSelectRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (confirmRoutes [aTrainIndex]);
		tTrainRevenueJPanel.add (selectRoutes [aTrainIndex]);
		resetRoutes [aTrainIndex] = setupButton (RESET_ROUTE, RESET_ROUTE_ACTION, this, Component.CENTER_ALIGNMENT);
		updateResetRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (resetRoutes [aTrainIndex]);
		reuseRoutes [aTrainIndex] = setupButton (REUSE_ROUTE, REUSE_ROUTE_ACTION, this, Component.CENTER_ALIGNMENT);
		updateReuseRouteButton (aTrainIndex);
		tTrainRevenueJPanel.add (reuseRoutes [aTrainIndex]);

		return tTrainRevenueJPanel;
	}

	private void buildRevenuesByTrain (int aTrainIndex, int aCenterCount, Train aTrain, 
										JPanel aTrainRevenueJPanel) {
		int tCenterIndex;
		int tRowIndex;
		int tRowCount;
		Dimension tTextFieldSize;
		JPanel tBothRevenuePanels;
		JPanel tRevenuesByTrainPanels [];
		BoxLayout tLayoutY;
		BoxLayout tLayoutX1;
		BoxLayout tLayoutX2;

		tTextFieldSize = new Dimension (35, 20);
		if (aCenterCount > Train.HALF_MAX_STOPS) {
			tRowCount = 2;
		} else {
			tRowCount = 1;
		}
		tRevenuesByTrainPanels = new JPanel [tRowCount];
		tRowIndex = 0;
		tRevenuesByTrainPanels [tRowIndex] = new JPanel ();
		tLayoutX1 = new BoxLayout (tRevenuesByTrainPanels [tRowIndex], BoxLayout.X_AXIS);
		tRevenuesByTrainPanels [tRowIndex].setLayout (tLayoutX1);
		if (tRowCount == 2) {
			tRevenuesByTrainPanels [tRowIndex + 1] = new JPanel ();
			tLayoutX2 = new BoxLayout (tRevenuesByTrainPanels [tRowIndex + 1], BoxLayout.X_AXIS);
			tRevenuesByTrainPanels [tRowIndex + 1].setLayout (tLayoutX2);
		}
		for (tCenterIndex = 0; tCenterIndex < aCenterCount; tCenterIndex++) {
			revenuesByTrain [aTrainIndex] [tCenterIndex] = new JFormattedTextField ();
			revenuesByTrain [aTrainIndex] [tCenterIndex].setValue (0);
			revenuesByTrain [aTrainIndex] [tCenterIndex].setColumns (3);
			revenuesByTrain [aTrainIndex] [tCenterIndex].setHorizontalAlignment (SwingConstants.RIGHT);
			revenuesByTrain [aTrainIndex] [tCenterIndex].setMaximumSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCenterIndex].setPreferredSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCenterIndex].setMinimumSize (tTextFieldSize);
			revenuesByTrain [aTrainIndex] [tCenterIndex].addPropertyChangeListener ("value", this);
			tRevenuesByTrainPanels [tRowIndex].add (revenuesByTrain [aTrainIndex] [tCenterIndex]);
			if ((tCenterIndex + 1) < aCenterCount) {
				tRevenuesByTrainPanels [tRowIndex] .add (new JLabel ("+"));
			} else {
				if (! aTrain.isPlusTrain ()) {
					tRevenuesByTrainPanels [tRowIndex].add (new JLabel ("="));
				}
			}
			if ((tCenterIndex + 1) == Train.HALF_MAX_STOPS) {
				tRowIndex++;
			}
		}
		if (aTrain.isPlusTrain ()) {
			tRevenuesByTrainPanels [tRowIndex].add (new JLabel ("+"));
			revenuesByPlusTrain [aTrainIndex] = new JFormattedTextField ();
			revenuesByPlusTrain [aTrainIndex].setValue (0);
			revenuesByPlusTrain [aTrainIndex].setColumns (2);
			revenuesByPlusTrain [aTrainIndex].setHorizontalAlignment (SwingConstants.RIGHT);
			revenuesByPlusTrain [aTrainIndex].setMaximumSize (tTextFieldSize);
			revenuesByPlusTrain [aTrainIndex].setPreferredSize (tTextFieldSize);
			revenuesByPlusTrain [aTrainIndex].setMinimumSize (tTextFieldSize);
			revenuesByPlusTrain [aTrainIndex].addPropertyChangeListener ("value", this);
			tRevenuesByTrainPanels [tRowIndex].add (revenuesByPlusTrain [aTrainIndex]);
			tRevenuesByTrainPanels [tRowIndex].add (new JLabel ("="));
		}

		if (tRowCount == 1) {
			aTrainRevenueJPanel.add (tRevenuesByTrainPanels [0]);
		} else {
			tBothRevenuePanels = new JPanel ();
			tLayoutY = new BoxLayout (tBothRevenuePanels, BoxLayout.Y_AXIS);
			tBothRevenuePanels.setLayout (tLayoutY);
			tBothRevenuePanels.add (tRevenuesByTrainPanels [0]);
			tBothRevenuePanels.add (tRevenuesByTrainPanels [1]);
			aTrainRevenueJPanel.add (tBothRevenuePanels);
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
		setPreviousRevenue (trainCompany.getPreviousRevenue ());
		setThisRevenue (trainCompany.getThisRevenue ());
		setYourCompany (true);
		updateInfo ();
		if (! trainCompany.isOperatingTrains ()) {
			clearAllRevenueValues ();
		}
		setLocation (aFrameOffset);
		setVisible (true);
	}

	private boolean allRoutesValid () {
		boolean tAllRoutesValid;
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;

		tAllRoutesValid = true;
		tTrainCount = trainCompany.getTrainCount ();

		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			if (tTrain != Train.NO_TRAIN) {
				if (!tTrain.isCurrentRouteValid ()) {
					tAllRoutesValid = false;
				}
			}
		}

		return tAllRoutesValid;
	}

	public boolean allRevenuesValid (int aTrainIndex, int aCityCount) {
		int tCityIndex;
		boolean tAllRevenuesValid;

		tAllRevenuesValid = true;
		for (tCityIndex = 0; (tCityIndex < aCityCount) && tAllRevenuesValid; tCityIndex++) {
			tAllRevenuesValid = tAllRevenuesValid && isValidRevenue (aTrainIndex, tCityIndex);
		}

		return tAllRevenuesValid;
	}

	private boolean isValidRevenue (int aTrainIndex, int aCityIndex) {
		boolean tIsValidRevenue = true;
		JFormattedTextField tRevenueField;
		Object tRevenueValue;
		int tRevenue;

		tRevenueField = revenuesByTrain [aTrainIndex] [aCityIndex];
		if (tRevenueField != null) {
			tRevenueValue = tRevenueField.getValue ();
			if (tRevenueValue != null) {
				tRevenue = ((Number) tRevenueValue).intValue ();
				if (!multipleOfTen (tRevenue)) {
					tIsValidRevenue = false;
				}
				if (tRevenue < 0) {
					tIsValidRevenue = false;
				}
			}
		}

		return tIsValidRevenue;
	}

	private boolean multipleOfTen (int aValue) {
		boolean tMultipleOfTen = false;
		int tOneTenth = aValue / 10;

		if ((tOneTenth * 10) == aValue) {
			tMultipleOfTen = true;
		}

		return tMultipleOfTen;
	}

	private boolean allTrainRevenuesAreValid () {
		boolean tAllTrainRevenuesAreValid;
		Train tTrain;
		int tCityCount;
		int tTrainIndex;
		int tTrainCount;

		tAllTrainRevenuesAreValid = true;
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();

			if (!allRevenuesValid (tTrainIndex, tCityCount)) {
				tAllTrainRevenuesAreValid = false;
			}
		}

		return tAllTrainRevenuesAreValid;
	}

	@Override
	public void propertyChange (PropertyChangeEvent aEvent) {
		Object tSource;
		int tTotalRevenue;

		tSource = aEvent.getSource ();
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
			tTrainRevenue = addTrainRevenues (tTrainIndex, tTrain);

			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				if (revenuesByTrain [tTrainIndex] [tCityIndex] == aSource) {
					totalRevenueByEachTrain [tTrainIndex].setText (Bank.formatCash (tTrainRevenue));
				}
			}
			if (tTrain.isPlusTrain ()) {
				if (revenuesByPlusTrain [tTrainIndex] == aSource) {
					totalRevenueByEachTrain [tTrainIndex].setText (Bank.formatCash (tTrainRevenue));					
				}
			}
			tTotalRevenue += tTrainRevenue;
		}

		return tTotalRevenue;
	}

	private int calculateTrainWidth () {
		int tWidth;
		int tMaxTrainSize;

		tMaxTrainSize = trainCompany.getMaxTrainSize ();
		if (tMaxTrainSize > Train.HALF_MAX_STOPS) {
			tMaxTrainSize = Train.HALF_MAX_STOPS;
		}
		tWidth = 485 + (tMaxTrainSize * 45);

		return tWidth;
	}

	private int calculateTrainHeight () {
		int tHeight;
		int tTrainCount;

		tTrainCount = trainCompany.getTrainCount ();
		tHeight = tTrainCount * 35;

		return tHeight;
	}

	private void updateFrameSize () {
		int tWidth;
		int tHeight;

		tWidth = calculateTrainWidth ();
		tHeight = 240 + calculateTrainHeight ();
		setSize (tWidth, tHeight);
	}

	public void updateInfo () {
		String tTitleText;
		String tTrainInfo;

		tTrainInfo = updateTrainInfo ();
		tTitleText = "Train Revenue for " + trainCompany.getName () + " with " + tTrainInfo;
		title.setText (tTitleText);
		updatePresidentLabel ();
		updateLastRevenueLabel ();
		if (! isVisible ()) {
			buildRevenuesJPanel ();
		}
		updateThisRevenueLabel ();
		updateLoanInfoPanel ();
		updateFrameSize ();
		setFrameSetup (true);
	}

	private String updateTrainInfo () {
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
		if (tTrainCount > 0) {
			trainCompany.updateTrainIndexes ();
		}

		return tTrainInfo;
	}

	public void setFrameSetup (boolean aFrameSetup) {
		frameSetup = aFrameSetup;
	}

	public void udpateConfirmAllRoutesButton () {
		if (confimAllRoutes != null) {
			if (isYourCompany ()) {
				if (anyTrainIsOperating ()) {
					confimAllRoutes.setEnabled (false);
					confimAllRoutes.setToolTipText ("A Train is Operating");
				} else if (!allRoutesValid ()) {
					confimAllRoutes.setEnabled (false);
					confimAllRoutes.setToolTipText ("One or more Routes are Invalid");
				} else if (!allTrainRevenuesAreValid ()) {
					confimAllRoutes.setEnabled (false);
					confimAllRoutes.setToolTipText ("One or more Revenues are Invalid");
				} else {
					confimAllRoutes.setEnabled (true);
					confimAllRoutes.setToolTipText ("Confirm that all Routes and Revenues are Accepted");
				}
			} else {
				confimAllRoutes.setEnabled (false);
				confimAllRoutes.setToolTipText (NOT_YOUR_COMPANY);
			}
		}
	}

	public void updateSelectRouteButton (int aTrainIndex) {
		Train tTrain;

		if (isValidIndex (aTrainIndex)) {
			showSelectRouteButton (aTrainIndex);
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				if (tTrain.isOperating ()) {
					hideSelectRouteButton (aTrainIndex);
				} else if (tTrain.hasRoute ()) {
					disableSelectRouteButton (aTrainIndex, "Train has Operated, Reset Route to change");
				} else if (anyTrainIsOperating ()) {
					disableSelectRouteButton (aTrainIndex, "Another Train is Operating");
				} else {
					enableSelectRouteButton (aTrainIndex, "Select Route");
				}
			} else {
				disableSelectRouteButton (aTrainIndex, NOT_YOUR_COMPANY);
			}
		} else {
			logger.error ("TrainIndex of " + aTrainIndex + " is out of range");
		}
	}

	public void updateConfirmRouteButton (int aTrainIndex) {
		Train tTrain;
		RouteInformation tRouteInformation;
		String tToolTip;
		String tWarningMessage;
		int tRouteCode;

		if (isValidIndex (aTrainIndex)) {
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				if (tTrain.isOperating ()) {
					hideSelectRouteButton (aTrainIndex);
					tRouteInformation = tTrain.getCurrentRouteInformation ();
					if (tRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
						disableConfirmRouteButton (aTrainIndex, "No Route selected to be confirmed");
					} else if (tRouteInformation.isRouteTooLong ()) {
						tToolTip = "Route is Too Long. Max length is " + tTrain.getName ();
						disableConfirmRouteButton (aTrainIndex, tToolTip);
						updateNotificationLabel (tToolTip);
					} else {
						tRouteCode = tRouteInformation.isValidRoute ();
						tWarningMessage = tRouteInformation.getWarningMessage ();
						updateNotificationLabel (tWarningMessage);
						if (tRouteCode == 1) {
							enableConfirmRouteButton (aTrainIndex, "Confirm Route");
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
								break;
							}
							updateNotificationLabel (tToolTip);
							disableConfirmRouteButton (aTrainIndex, tToolTip);
						}
					}
				} else {
					if (anyTrainIsOperating ()) {
						disableSelectRouteButton (aTrainIndex, "Another Train is Operating");
					} else {
						enableSelectRouteButton (aTrainIndex, "Select Route");
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

	private void showSelectRouteButton (int aTrainIndex) {
		selectRoutes [aTrainIndex].setVisible (true);
		confirmRoutes [aTrainIndex].setVisible (false);
	}

	private void hideSelectRouteButton (int aTrainIndex) {
		selectRoutes [aTrainIndex].setVisible (false);
		confirmRoutes [aTrainIndex].setVisible (true);
	}

	private void disableConfirmRouteButton (int aTrainIndex, String aToolTipText) {
		confirmRoutes [aTrainIndex].setEnabled (false);
		confirmRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}

	private void enableConfirmRouteButton (int aTrainIndex, String aToolTipText) {
		confirmRoutes [aTrainIndex].setEnabled (true);
		confirmRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}

	private void disableSelectRouteButton (int aTrainIndex, String aToolTipText) {
		selectRoutes [aTrainIndex].setEnabled (false);
		selectRoutes [aTrainIndex].setToolTipText (aToolTipText);
	}

	private void enableSelectRouteButton (int aTrainIndex, String aToolTipText) {
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
		updateConfirmRouteButtons ();
		updateSelectRouteButtons ();
		updateResetRouteButtons ();
		updateReuseRouteButtons ();
		udpateConfirmAllRoutesButton ();
		updateCancelButton ();
		updateLoanInfoPanel ();
	}

	private void updateConfirmRouteButtons () {
		int tTrainIndex;
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (confirmRoutes [tTrainIndex] != GUI.NO_BUTTON) {
				updateConfirmRouteButton (tTrainIndex);
			}
		}
	}

	private void updateSelectRouteButtons () {
		int tTrainIndex;
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (selectRoutes [tTrainIndex] != GUI.NO_BUTTON) {
				updateSelectRouteButton (tTrainIndex);
			}
		}
	}

	private void updateResetRouteButtons () {
		int tTrainIndex;
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (resetRoutes [tTrainIndex] != GUI.NO_BUTTON) {
				updateResetRouteButton (tTrainIndex);
			}
		}
	}

	private void updateReuseRouteButtons () {
		int tTrainIndex;
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (reuseRoutes [tTrainIndex] != GUI.NO_BUTTON) {
				updateReuseRouteButton (tTrainIndex);
			}
		}
	}

	private boolean totalRevenueIsZero (int aTrainIndex) {
		boolean tTotalRevenueIsZero;
		String tTotalRevenueForTrain;

		tTotalRevenueIsZero = false;
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
	 * This method will update the status for the ResetRoute Button for the Nth
	 * train owned by this Company
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
	 * This method will disable All of the Buttons on the Frame, and set flag as NOT YOUR COMPANY
	 *
	 */

	public void disableAll () {
		int tTrainIndex;
		int tCityIndex;
		int tCityCount;
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
