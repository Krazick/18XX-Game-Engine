package ge18xx.round.action;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ge18xx.toplevel.XMLFrame;

public class ActionReportFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;
	JTextPane actionReport;
	JScrollPane actionScrollPane;
	StyledDocument actionReportDoc;
	SimpleAttributeSet normalKeyWord;
	SimpleAttributeSet errorKeyWord;

	public ActionReportFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
		actionReport = new JTextPane ();
		actionReport.setText ( "Action Report:\n" );
		actionReportDoc = actionReport.getStyledDocument ();

	//  Define a keyword attributes
		normalKeyWord = new SimpleAttributeSet ();
		StyleConstants.setForeground (normalKeyWord, Color.BLACK);
		StyleConstants.setBackground (normalKeyWord, Color.WHITE);
		StyleConstants.setFontSize (normalKeyWord, 14);

		errorKeyWord = new SimpleAttributeSet ();
		StyleConstants.setForeground (errorKeyWord, Color.RED);
		StyleConstants.setBackground (errorKeyWord, Color.YELLOW);
		StyleConstants.setFontSize (errorKeyWord, 16);
		StyleConstants.setItalic (errorKeyWord, true);

		JScrollPane actionScrollPane = new JScrollPane (actionReport);
		add (actionScrollPane);
		setSize (800, 500);
		actionReport.setEditable (false);
	}

	/**
	 * Append the provided standard Report with Normal Styling to the end of the Action Report Frame, and
	 * Scroll to the end of the Frame
	 *
	 * @param aReport The Standard Report that requires no special formatting.
	 *
	 */
	public void append (String aReport) {
		append (aReport, normalKeyWord);
	}

	/**
	 * Append the provided Error Report with Error Styling to the end of the Action Report Frame, and
	 * Scroll to the end of the Frame
	 *
	 * @param aErrorReport The Error Report that requires Error formatting. Includes blank line before and after
	 *
	 */
	public void appendErrorReport (String aErrorReport) {
		append ("\nERROR: " + aErrorReport, errorKeyWord);
	}

	private void scrollToBottom () {
		actionReport.setCaretPosition (actionReport.getDocument ().getLength ());
	}

	private void append (String aReport, SimpleAttributeSet aKeyWord) {
		try
		{
			actionReportDoc.insertString (actionReportDoc.getLength (), aReport, aKeyWord);
			scrollToBottom ();
		}
		catch (Exception eException) {
			System.out.println (eException);
		}
	}

	/**
	 * Retrieve the complete Action Report Frame Text Content
	 *
	 * @return The Full Text in the Report Frame
	 *
	 */
	public String getText () {
		String tFullReport;

		tFullReport = actionReport.getText ();

		return tFullReport;
	}
}
