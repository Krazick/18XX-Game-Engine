package ge18xx.train;

import ge18xx.bank.Bank;
import ge18xx.company.TrainCompany;
import ge18xx.phase.PhaseInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
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

public class TrainRevenueFrame extends JFrame implements ActionListener, PropertyChangeListener,
		ItemListener {

	/**
	 * 
	 */
	private String NOT_YOUR_COMPANY = "This is not your company operating";
	private static final long serialVersionUID = 1L;
	private static final String CONFIRM_ROUTE_ACTION = "DoConfirmRouteAction";
	private static final String RESET_ROUTES_ACTION = "DoResetAction";
	private static final String CONFIRM_ACTION = "DoConfirmAction";
	private static final String CANCEL_ACTION = "DoCancelAction";
	private static final String ROUTE_ACTION = "DoRouteAction";
	public static final TrainRevenueFrame NO_TRAIN_REVENUE_FRAME = null;
	String LAST_REVENUE = "Last Round Revenue ";
	String THIS_REVENUE = "This Round Revenue ";
	String SELECT_ROUTE = "Select Route";
	String RUNNING_ROUTE = "Running";
	String CONFIRM_REVENUE = "Confirm All Revenues";
	String CANCEL = "Cancel";
	String RESET_ROUTES = "Reset Routes";
	int maxTrainCount = 5;
	int maxStops = 15;
	TrainCompany trainCompany;
	JLabel title;
	JLabel presidentLabel;
	JLabel lastRevenue;
	JLabel thisRevenue;
	JButton confirm;
	JButton cancel;
	JButton reset;
	JButton [] selectRoutes;
	JPanel allFramePanel;
	Box allRevenuesBox;
	JPanel buttonsPanel;
	JFormattedTextField [] [] revenuesByTrain;
	JLabel [] totalRevenueByEachTrain;
	boolean yourCompany;
	
	public TrainRevenueFrame (TrainCompany aTrainCompany, String aTitle) throws HeadlessException {
		super (aTitle);
		FlowLayout tFlowLayout = new FlowLayout();
		BoxLayout tLayoutY;
		
		allFramePanel = new JPanel ();
		tLayoutY = new BoxLayout (allFramePanel, BoxLayout.Y_AXIS);
		allFramePanel.setLayout (tLayoutY);
		allFramePanel.setAlignmentY (CENTER_ALIGNMENT);
		allFramePanel.setAlignmentX (CENTER_ALIGNMENT);
		trainCompany = aTrainCompany;
		title = new JLabel ();
		title.setAlignmentX (CENTER_ALIGNMENT);
		updatePresidentLabel ();
		presidentLabel.setAlignmentX (CENTER_ALIGNMENT);
		lastRevenue = new JLabel (LAST_REVENUE + trainCompany.getFormattedLastRevenue ());
		lastRevenue.setAlignmentX (CENTER_ALIGNMENT);
		thisRevenue = new JLabel (THIS_REVENUE + "NONE");
		thisRevenue.setAlignmentX (CENTER_ALIGNMENT);
		allRevenuesBox = null;
		fillRevenuesBox ();
		
		allFramePanel.add (Box.createVerticalStrut (10));
		allFramePanel.add (title);
		allFramePanel.add (Box.createVerticalStrut (10));
		allFramePanel.add (presidentLabel);
		allFramePanel.add (Box.createVerticalStrut (10));
		allFramePanel.add (lastRevenue);
		allFramePanel.add (Box.createVerticalStrut (10));
		allFramePanel.add (allRevenuesBox);
		allFramePanel.add (Box.createVerticalStrut (10));		
		allFramePanel.add (thisRevenue);
		allFramePanel.add (Box.createVerticalStrut (10));
		
		buttonsPanel = new JPanel (tFlowLayout);
		confirm = setupButton (CONFIRM_REVENUE, CONFIRM_ACTION);
		buttonsPanel.add (confirm);
		
		cancel = setupButton (CANCEL, CANCEL_ACTION);
		buttonsPanel.add (cancel);
		
		reset = setupButton (RESET_ROUTES, RESET_ROUTES_ACTION);
		buttonsPanel.add (reset);
		
		allFramePanel.add (buttonsPanel);
		add (allFramePanel);
		revenuesByTrain = new JFormattedTextField [maxTrainCount] [maxStops];
		totalRevenueByEachTrain = new JLabel [maxTrainCount];
		selectRoutes = new JButton [maxTrainCount];
		pack ();
		updateFrameSize ();
		setYourCompany (true);
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
		int tAllTrainRevenue, tTrainCount;
		
		if (CONFIRM_ACTION.equals (aEvent.getActionCommand ())) {
			tAllTrainRevenue = addAllTrainRevenues ();
			tTrainCount = trainCompany.getTrainCount ();
			trainCompany.setThisRevenue (tAllTrainRevenue);
			trainCompany.trainOperated (tAllTrainRevenue, tTrainCount);
			this.setVisible (false);
		}
		if (CANCEL_ACTION.equals (aEvent.getActionCommand ())) {
			this.setVisible (false);
		}
		if (ROUTE_ACTION.equals (aEvent.getActionCommand ())) {
			handleSelectRoute (aEvent);
		}
		if (CONFIRM_ROUTE_ACTION.equals (aEvent.getActionCommand ())) {
			handleConfirmRoute (aEvent);
		}
		if (RESET_ROUTES_ACTION.equals (aEvent.getActionCommand ())) {
			handleResetAllRoutes ();
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
					trainCompany.exitSelectRouteMode (tRouteInformation);
				}
			}
		}
	}
	
	public void handleResetAllRoutes () {
		System.out.println ("Ready to Reset All Routes");
		int tTrainIndex, tTrainCount;
		RouteInformation tRouteInformation;
		Train tTrain;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tRouteInformation = tTrain.getCurrentRouteInformation ();
			if (tRouteInformation != RouteInformation.NO_ROUTE_INFORMATION) {
				tRouteInformation.clearTrainOn ();
			}
		}
		trainCompany.exitSelectRouteMode (RouteInformation.NO_ROUTE_INFORMATION);
	}

	public void handleConfirmRoute (ActionEvent aSelectRouteEvent) {
		JButton tRouteButton = (JButton) aSelectRouteEvent.getSource ();
		int tTrainIndex, tTrainCount;
		Train tTrain;
		RouteInformation tRouteInformation;
		
		tTrainCount = trainCompany.getTrainCount ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tRouteButton.equals (selectRoutes [tTrainIndex])) {
				tTrain = trainCompany.getTrain (tTrainIndex);
				tRouteInformation = tTrain.getCurrentRouteInformation ();
				fillRevenueForTrain (tRouteInformation, tTrain, tTrainIndex);
				trainCompany.exitSelectRouteMode (tRouteInformation);
			}
		}
	}

	private void fillRevenueForTrain (RouteInformation aRouteInformation, Train aTrain, int aTrainIndex) {
		int tCityCount;
		int tSelectedTrainIndex;
		int tCityIndex;
		int tRevenue;
		
		tSelectedTrainIndex = aTrainIndex + 1;
		tCityCount = aTrain.getCityCount ();
		System.out.println ("Train " + tSelectedTrainIndex + " with size " + tCityCount + " has Route with " + 
					aRouteInformation.getCenterCount () + " Centers -- Phase " + aRouteInformation.getPhase ());
		
		for (tCityIndex = 0; tCityIndex < aTrain.getCityCount (); tCityIndex++) {
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (0);
		}
		for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
			tRevenue = aRouteInformation.getRevenueAt (tCityIndex);
			revenuesByTrain [aTrainIndex] [tCityIndex].setValue (tRevenue);
		}
	}
	
	public void handleSelectRoute (ActionEvent aSelectRouteEvent) {
		JButton tRouteButton = (JButton) aSelectRouteEvent.getSource ();
		int tTrainIndex, tTrainCount, tSelectedTrainIndex;
		Train tTrain;
		Color tColor = Color.BLUE;
		String tRoundID;
		int tRegionBonus = 0, tSpecialBonus = 0;
		RouteInformation tRouteInformation;
		int tPhase;
		PhaseInfo tPhaseInfo;
		String tToolTipText;
		
		tTrainCount = trainCompany.getTrainCount ();
		tPhaseInfo = trainCompany.getCurrentPhaseInfo ();
		tRoundID = trainCompany.getOperatingRoundID ();
		tPhase = tPhaseInfo.getName ();
		tSelectedTrainIndex = 0;
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tRouteButton.equals (selectRoutes [tTrainIndex])) {
				tSelectedTrainIndex = tTrainIndex + 1;
				tTrain = trainCompany.getTrain (tTrainIndex);
				tTrain.clearRouteInformation ();
				tRouteInformation = new RouteInformation (tTrain, tTrainIndex, tColor, tRoundID, tRegionBonus, tSpecialBonus, 
						tPhase, trainCompany, this);
				trainCompany.enterSelectRouteMode (tRouteInformation);
			}
		}
		tToolTipText = "Complete Route Selection for Train " + tSelectedTrainIndex;
		disableAllSelectRoutes (tToolTipText);
		tToolTipText = "Select Valid Route for Train " + tSelectedTrainIndex + " before confirming";
		enableConfirmRoute (tRouteButton, tToolTipText);
	}
	
	public void enableConfirmRoute (JButton aRouteButton, String aToolTipText) {
		aRouteButton.setText ("Confirm Route");
		aRouteButton.setActionCommand (CONFIRM_ROUTE_ACTION);
		aRouteButton.setToolTipText (aToolTipText);
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
			tTrainRevenueBox.add (Box.createHorizontalStrut (40));
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
			
			selectRoutes [tTrainIndex] = setupButton (SELECT_ROUTE, ROUTE_ACTION);
			tTrainRevenueBox.add (selectRoutes [tTrainIndex]);
			tTrainRevenueBox.add (Box.createHorizontalStrut (40));
			allRevenuesBox.add (tTrainRevenueBox);
		}
	}
	
	public void disableAllSelectRoutes (String aToolTipText) {
		int tTrainIndex, tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();

		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			selectRoutes [tTrainIndex].setEnabled (false);
			selectRoutes [tTrainIndex].setToolTipText (aToolTipText);
		}
	}
	
	public void updateRevenues (RouteInformation aRouteInformation) {
		int tTrainIndex;
		Train tTrain;
		
		tTrainIndex = aRouteInformation.getTrainIndex ();
		tTrain = aRouteInformation.getTrain ();
		fillRevenueForTrain (aRouteInformation, tTrain, tTrainIndex);
	}
	
	public void enableAllSelectRoutes () {
		int tTrainIndex, tTrainCount;
		
		if (trainCompany != null) {
			tTrainCount = trainCompany.getTrainCount ();

			for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
				if (selectRoutes [tTrainIndex] != null) {
					selectRoutes [tTrainIndex].setEnabled (true);
					selectRoutes [tTrainIndex].setToolTipText ("");
					selectRoutes [tTrainIndex].setText (SELECT_ROUTE);
					selectRoutes [tTrainIndex].setActionCommand (ROUTE_ACTION);
				}
			}
		}
	}
	
	@Override
	public void itemStateChanged (ItemEvent e) {

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
					System.err.println ("Train # " + tTrainIndex + " Route is Invalid");
				}
			}
			
		}

		// TODO: Must Fix testing 'isCurrentRouteValid' Method, not working properly for PRR
		tAllRoutesValid = true;
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
	
	@Override
	public void propertyChange (PropertyChangeEvent aEvent) {
		Object tSource = aEvent.getSource ();
		int tTrainCount, tTrainIndex, tCityCount, tCityIndex;
		int tTotalRevenue, tTrainRevenue;
		Train tTrain;
		boolean tValidRevenues = true;
		boolean tAllRoutesValid;
		
		tTrainCount = trainCompany.getTrainCount ();
		tTotalRevenue = 0;
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			tTrainRevenue = addTrainRevenues (tTrainIndex, tCityCount);
			
			if (! allRevenuesValid (tTrainIndex, tCityCount)) {
				tValidRevenues = false;
			}
			
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				if (revenuesByTrain [tTrainIndex] [tCityIndex] == tSource) {
					totalRevenueByEachTrain [tTrainIndex].setText (Bank.formatCash (tTrainRevenue));
				}
			}
			tTotalRevenue += tTrainRevenue;
		}
		thisRevenue.setText (THIS_REVENUE + Bank.formatCash (tTotalRevenue));
		if (isYourCompany ()) {
			tAllRoutesValid = allRoutesValid ();
			
			confirm.setEnabled (tValidRevenues && tAllRoutesValid);
			if (! tValidRevenues) {
				confirm.setToolTipText ("One or more Revenues is not valid");
			} else if (! tAllRoutesValid) {
				confirm.setToolTipText ("One or more Routes is not valid");
			} else {
				confirm.setToolTipText ("");
			}
		} else {
			confirm.setEnabled (false);
			confirm.setToolTipText (NOT_YOUR_COMPANY);
		}
	}
	
	public void updateFrameSize () {
		int tTrainCount;
		int tMaxTrainSize;
		int tWidth, tHeight;
		
		tTrainCount = trainCompany.getTrainCount ();
		tMaxTrainSize = trainCompany.getMaxTrainSize ();
		tWidth = 350 + tMaxTrainSize * 50;
		tHeight = 205 + (tTrainCount * 30);
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
	}

	public void enableConfirmRouteButton (int aTrainIndex) {
		if (isYourCompany ()) {
			if ((aTrainIndex >= 0) && (aTrainIndex < trainCompany.getTrainCount ())) {
				selectRoutes [aTrainIndex].setEnabled (true);
				selectRoutes [aTrainIndex].setToolTipText ("Valid Route Found");
			} else {
				System.err.println ("TrainIndex of " + aTrainIndex + " is out of range");
			}
		} else {
			disableConfirmRouteButton (aTrainIndex, NOT_YOUR_COMPANY);
		}
	}

	public void disableConfirmRouteButton (int aTrainIndex, String aToolTipText) {
		if ((aTrainIndex >= 0) && (aTrainIndex < trainCompany.getTrainCount ())) {
			selectRoutes [aTrainIndex].setEnabled (false);
			selectRoutes [aTrainIndex].setToolTipText (aToolTipText);
			confirm.setEnabled (false);
			confirm.setToolTipText ("One or more Routes is not valid");
		} else {
			System.err.println ("TrainIndex of " + aTrainIndex + " is out of range");
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
		reset.setEnabled (tEnabled);
		reset.setToolTipText (NOT_YOUR_COMPANY);
		for (JButton tSelectedRoute : selectRoutes) {
			if (tSelectedRoute != null) {
				tSelectedRoute.setEnabled (tEnabled);
				tSelectedRoute.setToolTipText (NOT_YOUR_COMPANY);
			}
		}
		for (tTrainIndex = 0; (tTrainIndex < trainCompany.getTrainCount ()); tTrainIndex++) {
			if (tTrainIndex == aTrainIndex) {
				selectRoutes [tTrainIndex].setText (RUNNING_ROUTE);
			} else {
				selectRoutes [tTrainIndex].setText (SELECT_ROUTE);
			}
			selectRoutes [tTrainIndex].setEnabled (tEnabled);
			selectRoutes [tTrainIndex].setToolTipText (NOT_YOUR_COMPANY);
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				revenuesByTrain [tTrainIndex] [tCityIndex].setEditable (tEnabled);
			}
		}
	}
}
