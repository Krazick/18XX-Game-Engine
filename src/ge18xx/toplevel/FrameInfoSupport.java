package ge18xx.toplevel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ge18xx.game.FrameInfo;
import ge18xx.game.GameManager;

import geUtilities.GUI;

public class FrameInfoSupport implements ActionListener {
	ArrayList<XMLFrame> configFrames;
	ArrayList<JPanel> infoJPanels;
	JPanel frameInfoPanel;
	GameManager gameManager;
	private String RESET = "Reset";
	/**
	 *
	 */

	public FrameInfoSupport (String aFrameName, GameManager aGameManager) {
		configFrames = aGameManager.getConfigFrames ();
		gameManager = aGameManager;

		frameInfoPanel = new JPanel ();
		frameInfoPanel.setLayout (new BoxLayout (frameInfoPanel, BoxLayout.Y_AXIS));
		infoJPanels = new ArrayList<> ();

		fillFrames ();
	}

	public JPanel getFrameInfoPanel () {
		return frameInfoPanel;
	}
	
	public void fillFrames () {
		boolean tAddVerticalGlue = false;
		JPanel tOneFrameJPanel;

		frameInfoPanel.add (Box.createVerticalStrut (10));
		tOneFrameJPanel = buildHeaderPanel ();
		frameInfoPanel.add (tOneFrameJPanel);
		for (XMLFrame tXMLFrame : configFrames) {
			if (tAddVerticalGlue) {
				frameInfoPanel.add (Box.createVerticalGlue ());
			}
			tOneFrameJPanel = buildOneFrameJPanel (tXMLFrame);
			if (tOneFrameJPanel != GUI.NO_PANEL) {
				infoJPanels.add (tOneFrameJPanel);
				frameInfoPanel.add (tOneFrameJPanel);
				tAddVerticalGlue = true;
			} else {
				tAddVerticalGlue = false;
			}
		}
		frameInfoPanel.add (Box.createVerticalStrut (10));
	}

	private JPanel buildHeaderPanel () {
		JPanel tOneFrameJPanel = null;
		JLabel tLabel;
		
		tOneFrameJPanel = new JPanel ();
		tOneFrameJPanel.setLayout (new BoxLayout (tOneFrameJPanel, BoxLayout.X_AXIS));
		tOneFrameJPanel.add (Box.createHorizontalStrut (10));

		tLabel = new JLabel ("Frame Title");
		tOneFrameJPanel.add (tLabel);
		tOneFrameJPanel.add (Box.createHorizontalGlue ());
		addOneLabel ("X", tOneFrameJPanel);
		addOneLabel ("Y",  tOneFrameJPanel);
		addOneLabel ("Height", tOneFrameJPanel);
		addOneLabel ("Width", tOneFrameJPanel);
		tOneFrameJPanel.add (Box.createHorizontalStrut (75));

		return tOneFrameJPanel;
	}
	
	public JPanel buildOneFrameJPanel (XMLFrame aXMLFrame) {
		FrameInfo tFrameInfo;
		JButton tResetButton;
		JLabel tLabel;
		JPanel tOneFrameJPanel = null;
		String tFrameName;

		tFrameInfo = new FrameInfo (aXMLFrame);

		if (tFrameInfo.getHeight () > 0) {
			tOneFrameJPanel = new JPanel ();
			tOneFrameJPanel.setLayout (new BoxLayout (tOneFrameJPanel, BoxLayout.X_AXIS));
			tOneFrameJPanel.add (Box.createHorizontalStrut (10));

			tFrameName = tFrameInfo.getName ();
			tLabel = new JLabel (tFrameName);
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());
			addOneLabel (tFrameInfo.getX (), tOneFrameJPanel);
			addOneLabel (tFrameInfo.getY (), tOneFrameJPanel);
			addOneLabel (tFrameInfo.getHeightString (), tOneFrameJPanel);
			addOneLabel (tFrameInfo.getWidthString (), tOneFrameJPanel);

			tResetButton = new JButton (RESET);
			tResetButton.setActionCommand (RESET + " " + tFrameName);
			tResetButton.addActionListener (this);

			tOneFrameJPanel.add (tResetButton);
			tOneFrameJPanel.add (Box.createHorizontalStrut (10));
		}

		return tOneFrameJPanel;

	}

	public void addOneLabel (String aValue, JPanel aOneFrameJPanel) {
		JLabel tLabel;
		
		tLabel = new JLabel (aValue);
		aOneFrameJPanel.add (tLabel);
		aOneFrameJPanel.add (Box.createHorizontalStrut (10));
	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		String tFrameName;

		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.startsWith (RESET)) {
			tFrameName = tActionCommand.substring (RESET.length () + 1);
			handleFrameReset (tFrameName);
		}
	}

	public void handleFrameReset (String aFrameName) {
		XMLFrame tFoundXMLFrame;

		tFoundXMLFrame = getFrameNamed (aFrameName);
		if (tFoundXMLFrame != XMLFrame.NO_XML_FRAME) {
			tFoundXMLFrame.setLocation (100, 100);
			tFoundXMLFrame.showFrame ();
		}
	}

	public XMLFrame getFrameNamed (String aFrameName) {
		XMLFrame tFoundXMLFrame = XMLFrame.NO_XML_FRAME;

		for (XMLFrame tXMLFrame : configFrames) {
			if (tXMLFrame.extractFrameName ().equals (aFrameName)) {
				tFoundXMLFrame = tXMLFrame;
			}
		}

		return tFoundXMLFrame;
	}
}
