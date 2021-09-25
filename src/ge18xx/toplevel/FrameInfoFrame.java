package ge18xx.toplevel;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import ge18xx.game.FrameInfo;
import ge18xx.game.GameManager;

public class FrameInfoFrame extends XMLFrame implements ActionListener {
	DefaultTableModel frameModel = new DefaultTableModel (0, 0);
	ArrayList<XMLFrame> configFrames;
	ArrayList<Container> infoContainers;
	Container allContainerPanels;
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
		
		allContainerPanels = Box.createVerticalBox ();
		infoContainers = new ArrayList<Container> ();
		setLocation (100, 100);
		setSize (500, 400);

		fillFrames ();
		add (allContainerPanels);
	}
	
	public void fillFrames () {
		boolean tAddVerticalGlue = false;
		Container tOneFrameBox;
		
		allContainerPanels.add (Box.createVerticalStrut (10));
		for (XMLFrame tXMLFrame : configFrames) {
			if (tAddVerticalGlue) {
				allContainerPanels.add (Box.createVerticalGlue ());
			}
			tOneFrameBox = buildOneFrameBox (tXMLFrame);
			if (tOneFrameBox != null) {
				infoContainers.add (tOneFrameBox);
				allContainerPanels.add (tOneFrameBox);
				tAddVerticalGlue = true;
			} else {
				tAddVerticalGlue = false;
			}
		}
		allContainerPanels.add (Box.createVerticalStrut (10));
	}
	
	public Container buildOneFrameBox (XMLFrame aXMLFrame) {
		FrameInfo tFrameInfo;
		JButton tResetButton;
		JLabel tLabel;
		Container tOneFrameBox = null;
		String tFrameName;
		
		tFrameInfo = new FrameInfo (aXMLFrame);
		
		if (tFrameInfo.getHeight () > 0) {
			tResetButton = new JButton ("Reset");
			tOneFrameBox = Box.createHorizontalBox ();
			tOneFrameBox.add (Box.createHorizontalStrut (10));
	
			tFrameName = tFrameInfo.getName ();
			tLabel = new JLabel (tFrameName);
			tOneFrameBox.add (tLabel);
			tOneFrameBox.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getX ());
			tOneFrameBox.add (tLabel);
			tOneFrameBox.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getY ());
			tOneFrameBox.add (tLabel);
			tOneFrameBox.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getHeightStr ());
			tOneFrameBox.add (tLabel);
			tOneFrameBox.add (Box.createHorizontalGlue ());
			tLabel = new JLabel (tFrameInfo.getWidthStr ());
			tOneFrameBox.add (tLabel);
			tOneFrameBox.add (Box.createHorizontalGlue ());
			
			tResetButton.setActionCommand (RESET_START + tFrameName);
			tResetButton.addActionListener (this);
			
			tOneFrameBox.add (tResetButton);
			tOneFrameBox.add (Box.createHorizontalStrut (10));
		}
		
		return tOneFrameBox;

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
			System.out.println ("Found Frame with X = " + 
						tFoundXMLFrame.getLocation ().x + " and Y = " +
						tFoundXMLFrame.getLocation ().y);
			System.out.println ("Default X " + tFoundXMLFrame.getDefaultXLocation ());
			System.out.println ("Default Y " + tFoundXMLFrame.getDefaultYLocation ());
			tFoundXMLFrame.setLocation (100, 100);
			gameManager.showFrame (tFoundXMLFrame);
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
