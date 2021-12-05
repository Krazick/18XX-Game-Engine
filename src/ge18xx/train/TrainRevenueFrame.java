package ge18xx.train;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.game.Game_18XX;
import ge18xx.phase.PhaseInfo;

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
	public static final TrainRevenueFrame NO_TRAIN_REVENUE_FRAME = null;
	String LAST_REVENUE = "Last Round Revenue ";
	String THIS_REVENUE = "This Round Revenue ";
	String SELECT_ROUTE = "Select Route";
	String CONFIRM_ROUTE = "Confirm Route";
	String RUNNING_ROUTE = "Running";
	String CONFIRM_ALL_REVENUES = "Confirm All Revenues";
	String CANCEL = "Cancel";
	String RESET_ROUTE = "Reset Route";
	int maxTrainCount = 5;
	int maxStops = 15;
	TrainCompany trainCompany;
	JLabel title;
	JLabel presidentLabel;
	JLabel lastRevenue;
	JLabel thisRevenue;
	JButton confirm;
	JButton cancel;
	JButton [] selectRoutes;
	JButton [] resetRoutes;
	JPanel allFramesJPanel;
	Box allRevenuesBox;
	JPanel buttonsJPanel;
	JFormattedTextField [] [] revenuesByTrain;
	JLabel [] totalRevenueByEachTrain;
	boolean yourCompany;
	boolean frameSetup;
	Logger logger;
	
	public TrainRevenueFrame (TrainCompany aTrainCompany, String aTitle) throws HeadlessException {
		super (aTitle);
		
		trainCompany = aTrainCompany;
		title = new JLabel ();
		title.setAlignmentX (CENTER_ALIGNMENT);
		updatePresidentLabel ();
		allRevenuesBox = null;
		fillRevenuesBox ();
		buildsButtonsJPanel ();
		presidentLabel.setAlignmentX (CENTER_ALIGNMENT);
		lastRevenue = new JLabel (LAST_REVENUE + trainCompany.getFormattedLastRevenue ());
		lastRevenue.setAlignmentX (CENTER_ALIGNMENT);
		thisRevenue = new JLabel (THIS_REVENUE + "NONE");
		thisRevenue.setAlignmentX (CENTER_ALIGNMENT);
		
		buildAllFramesJPanel ();
		
		revenuesByTrain = new JFormattedTextField [maxTrainCount] [maxStops];
		totalRevenueByEachTrain = new JLabel [maxTrainCount];
		selectRoutes = new JButton [maxTrainCount];
		resetRoutes = new JButton [maxTrainCount];
		pack ();
		updateFrameSize ();
		setYourCompany (true);
		setFrameSetup (false);
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
		logger = Game_18XX.getLogger ();
	}

	private void buildAllFramesJPanel () {
		BoxLayout tLayoutY;
		
		allFramesJPanel = new JPanel ();
		tLayoutY = new BoxLayout (allFramesJPanel, BoxLayout.Y_AXIS);
		allFramesJPanel.setLayout (tLayoutY);
		allFramesJPanel.setAlignmentY (CENTER_ALIGNMENT);
		allFramesJPanel.setAlignmentX (CENTER_ALIGNMENT);
		
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (title);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (presidentLabel);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (lastRevenue);
		allFramesJPanel.add (Box.createVerticalStrut (10));
		allFramesJPanel.add (allRevenuesBox);
		allFramesJPanel.add (Box.createVerticalStrut (10));		
		allFramesJPanel.add (thisRevenue);
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
		int tAllTrainRevenue;
		
		if (CONFIRM_ALL_REVENUES_ACTION.equals (aEvent.getActionCommand ())) {
			tAllTrainRevenue = addAllTrainRevenues ();
			trainCompany.setThisRevenue (tAllTrainRevenue);
			trainCompany.trainsOperated (tAllTrainRevenue);
			setVisible (false);
		}
		if (CANCEL_ACTION.equals (aEvent.getActionCommand ())) {
			clearAllTrainsRoutes ();
			trainCompany.exitSelectRouteMode ();
			setVisible (false);
		}
		if (SELECT_ROUTE_ACTION.equals (aEvent.getActionCommand ())) {
			handleSelectRoute (aEvent);
		}
		if (CONFIRM_ROUTE_ACTION.equals (aEvent.getActionCommand ())) {
			handleConfirmRoute (aEvent);
		}
		if (RESET_ROUTE_ACTION.equals (aEvent.getActionCommand ())) {
			handleResetRoute (aEvent);
		}
	}
	
	public void clearTrainsFromMap () {
		int tTrainIndex, tTrainCount;
		RouteInformation tRouteInformation;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tRouteInformation = tTrain.getCurrentRouteInformation ();
			if (tRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
				tRouteInformation.clearTrainFromMap ();
				if ((tTrainIndex + 1) == tTrainCount) {
					trainCompany.exitSelectRouteMode ();
				}
			}
		}
		updateAllFrameButtons ();
		trainCompany.repaintMapFrame ();
	}

	public void copyAllRoutesToPrevious () {
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

	public void clearAllTrainsRoutes () {
		int tTrainIndex;
		int tTrainCount;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			clearRouteFromTrain (tTrain);
		}
		updateAllFrameButtons ();
		trainCompany.repaintMapFrame ();
	}

	private void clearRouteFromTrain (Train aTrain) {
		RouteInformation tRouteInformation;
		
		tRouteInformation = aTrain.getCurrentRouteInformation ();
		if (tRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
			tRouteInformation.clearTrainOn ();
			aTrain.setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
		}
		aTrain.setOperating (false);
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
	
	public void handleConfirmRoute (ActionEvent aConfirmRouteEvent) {
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

	public void handleResetRoute (ActionEvent aResetRouteEvent) {
		JButton tResetRouteButton = (JButton) aResetRouteEvent.getSource ();
		int tTrainIndex, tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tResetRouteButton.equals (resetRoutes [tTrainIndex])) {
				resetTrainRoute (tTrainIndex);
				trainCompany.exitSelectRouteMode ();
			}
		}
		updateAllFrameButtons ();
	}

	private void resetTrainRoute (int aTrainIndex) {
		Train tTrain;
		
		tTrain = trainCompany.getTrain (aTrainIndex);
		clearRevenuesFromTrain (aTrainIndex, tTrain);
		clearRouteFromTrain (tTrain);
		tTrain.setOperating (false);
		tTrain.setCurrentRouteInformation (RouteInformation.NO_ROUTE_INFORMATION);
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
		tRoundID = trainCompany.getOperatingRoundID ();
		tPhase = tPhaseInfo.getName ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tSelectRouteButton.equals (selectRoutes [tTrainIndex])) {
				tTrain = trainCompany.getTrain (tTrainIndex);
				tTrain.clearRouteInformation ();
				tRouteInformation = new RouteInformation (tTrain, tTrainIndex, tColor, tRoundID, tRegionBonus, tSpecialBonus, 
						tPhase, trainCompany, this);
				tTrain.setOperating (true);
				trainCompany.enterSelectRouteMode (tRouteInformation);
			}
		}
		updateAllFrameButtons ();
	}
	
	public void enableResetRoute (JButton aResetRouteButton, String aToolTipText) {
		aResetRouteButton.setText (RESET_ROUTE);
		aResetRouteButton.setActionCommand (RESET_ROUTE_ACTION);
		aResetRouteButton.setToolTipText (aToolTipText);
	}
	
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
	
	public void fillRevenuesBox () {
		JLabel tTrainLabel;
		int tTrainCount, tTrainIndex, tCityCount, tCityIndex;
		Box tTrainRevenueBox;
		Train tTrain;
		Dimension textFieldSize = new Dimension (40, 20);
		
		tTrainCount = trainCompany.getTrainCount ();
		
		if (allRevenuesBox == null) {
			allRevenuesBox = Box.createVerticalBox ();
		} else {
			allRevenuesBox.removeAll ();
		}
		allRevenuesBox.setAlignmentX (CENTER_ALIGNMENT);
		
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tTrainIndex > 0) {
				allRevenuesBox.add (Box.createVerticalStrut (10));
			}
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			// TODO: QUICK FIX, the "getCityCount" will return a Maximum of maxStops (15) to allow Diesels to Operate
			// MUST Figure out way to give Diesels an infinite length
			
			tTrainRevenueBox = Box.createHorizontalBox ();
			tTrainLabel = new JLabel (tTrain.getName () + " Train #" + (tTrainIndex + 1));
			tTrainRevenueBox.add (Box.createHorizontalStrut (30));
			tTrainRevenueBox.add (tTrainLabel);
			tTrainRevenueBox.add (Box.createHorizontalStrut (10));
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				revenuesByTrain [tTrainIndex] [tCityIndex] = new JFormattedTextField ();
				revenuesByTrain [tTrainIndex] [tCityIndex].setValue (0);
				revenuesByTrain [tTrainIndex] [tCityIndex].setColumns (3);
				revenuesByTrain [tTrainIndex] [tCityIndex].setHorizontalAlignment (JTextField.RIGHT);
				revenuesByTrain [tTrainIndex] [tCityIndex].setMaximumSize (textFieldSize);
				revenuesByTrain [tTrainIndex] [tCityIndex].setPreferredSize (textFieldSize);
				revenuesByTrain [tTrainIndex] [tCityIndex].setMinimumSize (textFieldSize);
				revenuesByTrain [tTrainIndex] [tCityIndex].addPropertyChangeListener ("value", this);
				tTrainRevenueBox.add (revenuesByTrain [tTrainIndex] [tCityIndex]);
				tTrainRevenueBox.add (Box.createHorizontalStrut (5));
				if ((tCityIndex + 1) < tCityCount) {
					tTrainRevenueBox.add (new JLabel ("+"));
				} else {
					tTrainRevenueBox.add (new JLabel ("="));
				}
				tTrainRevenueBox.add (Box.createHorizontalStrut (5));
				
			}
			totalRevenueByEachTrain [tTrainIndex] = new JLabel ("0");
			tTrainRevenueBox.add (totalRevenueByEachTrain [tTrainIndex]);
			tTrainRevenueBox.add (Box.createHorizontalStrut (10));
			
			selectRoutes [tTrainIndex] = setupButton (SELECT_ROUTE, SELECT_ROUTE_ACTION);
			updateSelectRouteButton (tTrainIndex);
			tTrainRevenueBox.add (selectRoutes [tTrainIndex]);
			tTrainRevenueBox.add (Box.createHorizontalStrut (5));
			resetRoutes [tTrainIndex] = setupButton (RESET_ROUTE, RESET_ROUTE_ACTION);
			updateResetRouteButton (tTrainIndex);
			tTrainRevenueBox.add (resetRoutes [tTrainIndex]);
			tTrainRevenueBox.add (Box.createHorizontalStrut (30));
			
			allRevenuesBox.add (tTrainRevenueBox);
		}
	}
	
	public void disableAllResetRoutes (String aToolTipText) {
		int tTrainIndex, tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();

		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			// TODO: Test for NULL Train
			resetRoutes [tTrainIndex].setEnabled (false);
			resetRoutes [tTrainIndex].setToolTipText (aToolTipText);
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
		updateInfo ();
		copyAllRoutesToPrevious ();
		clearAllTrainsRoutes ();
		clearAllRevenueValues ();
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
		thisRevenue.setText (THIS_REVENUE + Bank.formatCash (tTotalRevenue));
		this.updateAllFrameButtons ();
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
		tWidth = 450 + tMaxTrainSize * 50;
		tHeight = 180 + (tTrainCount * 40);
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
		lastRevenue.setText (LAST_REVENUE + trainCompany.getFormattedLastRevenue ());
		thisRevenue.setText (THIS_REVENUE + "NONE");
		fillRevenuesBox ();
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
		
		if (isValidIndex (aTrainIndex)) {
			if (isYourCompany ()) {
				tTrain = trainCompany.getTrain (aTrainIndex);
				if (tTrain.isOperating ()) {
					tRouteInformation = tTrain.getCurrentRouteInformation ();
					if (tRouteInformation == RouteInformation.NO_ROUTE_INFORMATION) {
						enableSelectRouteButton (aTrainIndex, CONFIRM_ROUTE, CONFIRM_ROUTE_ACTION, "Confirm Route");
						disableSelectRouteButton (aTrainIndex, "Route is not yet valid");
					} else if (tRouteInformation.isRouteTooLong ()) {
						disableSelectRouteButton (aTrainIndex, "Route is Too Long. Max length is " + tTrain.getName ());
					} else if (! tRouteInformation.isValidRoute ()) {
						if (tRouteInformation.getCenterCount () < 2) {
							tToolTip = "Route must have at least two Revenue Centers";
						} else {
							tToolTip = "Route is Blocked by Station, or Have no Corp Station";
						}
						disableSelectRouteButton (aTrainIndex, tToolTip);
					} else {
						enableSelectRouteButton (aTrainIndex, CONFIRM_ROUTE, CONFIRM_ROUTE_ACTION, "Confirm Route");
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
		selectRoutes [aTrainIndex].setActionCommand(aAction);
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
	
	private boolean isValidIndex (int aTrainIndex) {
		return (aTrainIndex >= 0) && (aTrainIndex < trainCompany.getTrainCount ());
	}
	
	public void updateAllFrameButtons () {
		updateSelectRouteButtons ();
		updateResetRouteButtons ();
		udpateConfirmAllRoutesButton ();
	}
	
	public void updateSelectRouteButtons () {
		int tTrainIndex;
	
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (selectRoutes [tTrainIndex] != null) {
				updateSelectRouteButton (tTrainIndex);
			}
		}
	}
	
	public void updateResetRouteButtons () {
		int tTrainIndex;
	
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (resetRoutes [tTrainIndex] != null) {
				updateResetRouteButton (tTrainIndex);
			}
		}
	}
	
	public boolean totalRevenueIsZero (int aTrainIndex) {
		boolean tTotalRevenueIsZero = false;
		String tTotalRevenueForTrain;
		
		tTotalRevenueForTrain = totalRevenueByEachTrain [aTrainIndex].getText ();
		if (tTotalRevenueForTrain.equals ("0")) {
			tTotalRevenueIsZero = true;
		}
		
		return tTotalRevenueIsZero;
	}
	
	public void updateResetRouteButton (int aTrainIndex) {
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

	public void disableAll (int aTrainIndex) {
		int tTrainIndex, tCityIndex, tCityCount;
		Train tTrain;
		boolean tEnabled;
		
		tEnabled = false;
		setYourCompany (false);
		confirm.setEnabled (tEnabled);
		confirm.setToolTipText (NOT_YOUR_COMPANY);
		cancel.setEnabled (tEnabled);
		cancel.setToolTipText (NOT_YOUR_COMPANY);
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			updateSelectRouteButton (tTrainIndex);
			updateResetRouteButton (tTrainIndex);
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				revenuesByTrain [tTrainIndex] [tCityIndex].setEditable (tEnabled);
			}
		}
	}
}
