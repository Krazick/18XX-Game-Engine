package ge18xx.toplevel;

//
//	File:	PrefPane.java
//

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swingDelays.KButton;

public class PrefPane extends JFrame {
	private static final long serialVersionUID = 1L;
	protected KButton okButton;
	protected JLabel prefsText;

	public PrefPane () {
		super ();
		
		JPanel textPanel;
		JPanel buttonPanel;
		Container tContentPane;
		
		tContentPane = getContentPane ();
		tContentPane.setLayout (new BorderLayout (10, 10));
		prefsText = new JLabel ("Game_18XX Preferences...");
		textPanel = new JPanel (new FlowLayout (FlowLayout.LEFT, 10, 10));
		textPanel.add (prefsText);
		tContentPane.add (textPanel, BorderLayout.NORTH);

		okButton = new KButton ("OK");
		buttonPanel = new JPanel (new FlowLayout (FlowLayout.RIGHT, 10, 10));
		buttonPanel.add (okButton);
		okButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent newEvent) {
				setVisible (false);
			}
		});
		tContentPane.add (buttonPanel, BorderLayout.SOUTH);
		setSize (390, 129);
		setLocation (20, 40);
	}
}