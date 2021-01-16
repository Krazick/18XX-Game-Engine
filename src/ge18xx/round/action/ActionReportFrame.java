package ge18xx.round.action;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ge18xx.toplevel.XMLFrame;

public class ActionReportFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	JTextArea actionReport;
	JScrollPane actionScrollPane;
	
	public ActionReportFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
		actionReport = new JTextArea (5, 20);
		JScrollPane actionScrollPane = new JScrollPane (actionReport);
		add (actionScrollPane);
		setSize (800, 500);
		actionReport.setEditable (false);
	}
	
	public void append (String aReport) {
		actionReport.append ("\n" + aReport);
		actionReport.setCaretPosition (actionReport.getDocument ().getLength ());
	}
}
