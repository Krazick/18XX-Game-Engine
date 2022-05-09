package ge18xx.game;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class CashFlowGraph {
	Stage stage;
	JTable auditTable;
	StackedAreaChart<String, Number> areaChart;
	CategoryAxis xAxis;
	NumberAxis yAxis;
	XYChart.Series<String, Number> series;

	public CashFlowGraph (JTable aAuditTable) {
		auditTable = aAuditTable;
	}

	public void showGraph () {
	}

	public void launch () {
		JFrame frame = new JFrame ("Cash Flow Graph");
		final JFXPanel fxPanel = new JFXPanel ();
		frame.add (fxPanel);
		frame.setSize (1200, 800);
		frame.setVisible (true);

		Platform.runLater (new Runnable () {
			@Override
			public void run () {
				initFX (fxPanel);
			}
		});
	}

	private void initFX (JFXPanel fxPanel) {
		Scene scene = createScene ();
		fxPanel.setScene (scene);
	}

	private Scene createScene () {
//        Group  root;
		Scene scene;

		createAreaChart ();
		fillData ();

		// Creating a Group object
//		root = new Group (areaChart); 

		// Creating a scene object
		scene = new Scene (areaChart, 1200, 800);

		return (scene);
	}

	private void createAreaChart () {
		xAxis = new CategoryAxis ();
		yAxis = new NumberAxis (0, 12000, 200);
		yAxis.setLabel ("Cash Amounts");
		areaChart = new StackedAreaChart<String, Number> (xAxis, yAxis);
		areaChart.setMaxSize (1000, 600);
	}

	private void fillData () {
		List<Integer> tBalances = new LinkedList<Integer> ();
		List<String> tActionNumbers = new LinkedList<String> ();
		int tRowCount;
		String tActorName;
		String tActionNumber;
		Integer tBalance;

		tRowCount = auditTable.getModel ().getRowCount ();
		for (int tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			tActionNumber = getActionNumber (auditTable, tRowIndex);
			tActionNumbers.add (tActionNumber);
			tBalance = getBalance (auditTable, tRowIndex);
			tBalances.add (tBalance);
		}
		xAxis.setCategories (FXCollections.<String>observableArrayList (tActionNumbers));
		yAxis.setLabel ("Cash Amounts");
		series = new XYChart.Series<String, Number> ();
		tActorName = getActorName (auditTable, 0);
		series.setName (tActorName);
		for (int tRowIndex = 0; tRowIndex < tRowCount; tRowIndex++) {
			series.getData ()
					.add (new XYChart.Data<String, Number> (tActionNumbers.get (tRowIndex), tBalances.get (tRowIndex)));
		}

		areaChart.getData ().add (series);
	}

	public String getActionNumber (JTable aTable, int aRowIndex) {
		String tActionNumber;

		tActionNumber = "" + getData (aTable, aRowIndex, 0);

		return tActionNumber;
	}

	public String getActorName (JTable aTable, int aRowIndex) {
		String tActorName;

		tActorName = (String) getData (aTable, aRowIndex, 2);

		return tActorName;
	}

	public Integer getBalance (JTable aTable, int aRowIndex) {
		Integer tBalance;

		tBalance = (Integer) getData (aTable, aRowIndex, aTable.getColumnCount () - 1);

		return tBalance;
	}

	public Object getData (JTable aTable, int aRowIndex, int aColIndex) {
		return aTable.getModel ().getValueAt (aRowIndex, aColIndex);
	}

}
