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
	private static final long serialVersionUID = 1L;
	private static final String CONFIRM_ACTION = "DoConfirmAction";
	private static final String CANCEL_ACTION = "DoCancelAction";
	private static final String ROUTE_ACTION = "DoRouteAction";
	String LAST_REVENUE = "Last Round Revenue ";
	String THIS_REVENUE = "This Round Revenue ";
	String SELECT_ROUTE = "Select Route";
	String CONFIRM_REVENUE = "Confirm Revenue";
	String CANCEL = "Cancel";
	int maxTrainCount = 5;
	int maxStops = 15;
	TrainCompany trainCompany;
	JLabel title;
	JLabel lastRevenue;
	JLabel thisRevenue;
	JButton confirm;
	JButton cancel;
	JButton [] selectRoutes;
	JPanel allFramePanel;
	Box allRevenuesBox;
	JPanel buttonsPanel;
	JFormattedTextField [] [] revenuesByTrain;
	JLabel [] totalRevenueByEachTrain;
	
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
		lastRevenue = new JLabel (LAST_REVENUE + trainCompany.getFormattedLastRevenue ());
		lastRevenue.setAlignmentX (CENTER_ALIGNMENT);
		thisRevenue = new JLabel (THIS_REVENUE + "NONE");
		thisRevenue.setAlignmentX (CENTER_ALIGNMENT);
		allRevenuesBox = null;
		fillRevenuesBox ();
		
		allFramePanel.add (Box.createVerticalStrut (10));
		allFramePanel.add (title);
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
		
//		confirm = new JButton ("Confirm Revenue");
//		confirm.setActionCommand (CONFIRM_ACTION);
//		confirm.addActionListener (this);
//		cancel = new JButton ("Cancel");
//		cancel.setActionCommand (CANCEL_ACTION);
//		cancel.addActionListener (this);
		allFramePanel.add (buttonsPanel);
		add (allFramePanel);
		revenuesByTrain = new JFormattedTextField [maxTrainCount] [maxStops];
		totalRevenueByEachTrain = new JLabel [maxTrainCount];
		selectRoutes = new JButton [maxTrainCount];
		pack ();
		updateFrameSize ();
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

	}
	
	public void handleSelectRoute (ActionEvent aSelectRouteEvent) {
		JButton tRouteButton = (JButton) aSelectRouteEvent.getSource ();
		int tTrainIndex, tTrainCount, tCityCount;
		Train tTrain;
		Color tColor = Color.BLUE;
		String tRoundID = "1.1";
		int tRegionBonus = 0, tSpecialBonus = 0;
		RouteInformation tRouteInformation;
		int tPhase;
		PhaseInfo tPhaseInfo;
		
		tTrainCount = trainCompany.getTrainCount ();
		tPhaseInfo = trainCompany.getCurrentPhaseInfo();
		tPhase = tPhaseInfo.getName ();
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			if (tRouteButton.equals (selectRoutes [tTrainIndex])) {
				tTrain = trainCompany.getTrain (tTrainIndex);
				tCityCount = tTrain.getCityCount ();
				tTrain.clearRouteInformation ();
				tRouteInformation = new RouteInformation (tTrain, tTrainIndex, tColor, tRoundID, tRegionBonus, tSpecialBonus, tPhase, trainCompany);
				trainCompany.enterSelectRouteMode (tRouteInformation);
				System.out.println ("Selecting Route for Train Index " + tTrainIndex + " City Count " + tCityCount);
				
			}
		}

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
	
	@Override
	public void itemStateChanged (ItemEvent e) {

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
		boolean validRevenue = true;
		
		tTrainCount = trainCompany.getTrainCount ();
		tTotalRevenue = 0;
		for (tTrainIndex = 0; tTrainIndex < tTrainCount; tTrainIndex++) {
			tTrain = trainCompany.getTrain (tTrainIndex);
			tCityCount = tTrain.getCityCount ();
			tTrainRevenue = addTrainRevenues (tTrainIndex, tCityCount);
			
			if (! allRevenuesValid (tTrainIndex, tCityCount)) {
				validRevenue = false;
			}
			for (tCityIndex = 0; tCityIndex < tCityCount; tCityIndex++) {
				if (revenuesByTrain [tTrainIndex] [tCityIndex] == tSource) {
					totalRevenueByEachTrain [tTrainIndex].setText (Bank.formatCash (tTrainRevenue));
				}
			}
			tTotalRevenue += tTrainRevenue;
		}
		thisRevenue.setText (THIS_REVENUE + tTotalRevenue);
		confirm.setEnabled (validRevenue);
		if (validRevenue) {
			confirm.setToolTipText ("");
		} else {
			confirm.setToolTipText ("One or more Revenues is not valid");
		}
	}
	
	public void updateFrameSize () {
		int tTrainCount;
		
		tTrainCount = trainCompany.getTrainCount ();
		setSize (450, 165 + (tTrainCount * 30));
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
		lastRevenue.setText (LAST_REVENUE + trainCompany.getFormattedLastRevenue ());
		thisRevenue.setText (THIS_REVENUE + "NONE");
		fillRevenuesBox ();
		updateFrameSize ();
	}
}
