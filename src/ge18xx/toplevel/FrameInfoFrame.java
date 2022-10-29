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
import ge18xx.utilities.GUI;

public class FrameInfoFrame extends XMLFrame implements ActionListener {
	ArrayList<XMLFrame> configFrames;
	ArrayList<JPanel> infoJPanels;
	JPanel allJFramesJPanel;
	GameManager gameManager;
	private String RESET_START = "Reset ";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FrameInfoFrame (String aFrameName, GameManager aGameManager) {
		super (aFrameName, aGameManager.getGameName ());
		configFrames = aGameManager.getConfigFrames ();
		gameManager = aGameManager;

		allJFramesJPanel = new JPanel ();
		allJFramesJPanel.setLayout (new BoxLayout (allJFramesJPanel, BoxLayout.Y_AXIS));
		infoJPanels = new ArrayList<> ();
		setLocation (100, 100);
		setSize (500, 400);

		fillFrames ();
		add (allJFramesJPanel);
	}

	public void fillFrames () {
		boolean tAddVerticalGlue = false;
		JPanel tOneFrameJPanel;

		allJFramesJPanel.add (Box.createVerticalStrut (10));
		for (XMLFrame tXMLFrame : configFrames) {
			if (tAddVerticalGlue) {
				allJFramesJPanel.add (Box.createVerticalGlue ());
			}
			tOneFrameJPanel = buildOneFrameJPanel (tXMLFrame);
			if (tOneFrameJPanel != GUI.NO_PANEL) {
				infoJPanels.add (tOneFrameJPanel);
				allJFramesJPanel.add (tOneFrameJPanel);
				tAddVerticalGlue = true;
			} else {
				tAddVerticalGlue = false;
			}
		}
		allJFramesJPanel.add (Box.createVerticalStrut (10));
	}

	public JPanel buildOneFrameJPanel (XMLFrame aXMLFrame) {
		FrameInfo tFrameInfo;
		JButton tResetButton;
		JLabel tLabel;
		JPanel tOneFrameJPanel = null;
		String tFrameName;

		tFrameInfo = new FrameInfo (aXMLFrame);

		if (tFrameInfo.getHeight () > 0) {
			tResetButton = new JButton ("Reset");
			tOneFrameJPanel = new JPanel ();
			tOneFrameJPanel.setLayout (new BoxLayout (tOneFrameJPanel, BoxLayout.X_AXIS));
			tOneFrameJPanel.add (Box.createHorizontalStrut (10));

			tFrameName = tFrameInfo.getName ();
			tLabel = new JLabel (tFrameName);
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getX ());
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getY ());
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getHeightStr ());
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getWidthStr ());
			tOneFrameJPanel.add (tLabel);
			tOneFrameJPanel.add (Box.createHorizontalGlue ());

			tResetButton.setActionCommand (RESET_START + tFrameName);
			tResetButton.addActionListener (this);

			tOneFrameJPanel.add (tResetButton);
			tOneFrameJPanel.add (Box.createHorizontalStrut (10));
		}

		return tOneFrameJPanel;

	}

	@Override
	public void actionPerformed (ActionEvent aEvent) {
		String tActionCommand;
		String tFrameName;

		tActionCommand = aEvent.getActionCommand ();
		if (tActionCommand.startsWith (RESET_START)) {
			tFrameName = tActionCommand.substring (RESET_START.length ());
			handleFrameReset (tFrameName);
		}
	}

	public void handleFrameReset (String aFrameName) {
		XMLFrame tFoundXMLFrame;

		System.out.println ("Reset Button selected for [" + aFrameName + "]");
		tFoundXMLFrame = getFrameNamed (aFrameName);
		if (tFoundXMLFrame != XMLFrame.NO_XML_FRAME) {
			System.out.println ("Found Frame with X = " + tFoundXMLFrame.getLocation ().x + " and Y = "
					+ tFoundXMLFrame.getLocation ().y);
			System.out.println ("Default X " + tFoundXMLFrame.getDefaultXLocation ());
			System.out.println ("Default Y " + tFoundXMLFrame.getDefaultYLocation ());
			tFoundXMLFrame.setLocation (100, 100);
			tFoundXMLFrame.showFrame ();
//			gameManager.showFrame (tFoundXMLFrame);
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
