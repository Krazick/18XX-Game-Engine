package ge18xx.toplevel;

import java.awt.BorderLayout;

//
//	File:	AboutBox.java
//

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AboutBox extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	protected JLabel titleLabel, aboutLabel[];
	protected static int labelCount = 8;
	protected static int aboutWidth = 280;
	protected static int aboutHeight = 230;
	protected static int aboutTop = 200;
	protected static int aboutLeft = 350;
	protected Font titleFont, bodyFont;
	protected ResourceBundle resbundle;

	public AboutBox () {
		super ("");
		this.setResizable (false);
		resbundle = ResourceBundle.getBundle ("Game_18XXstrings", Locale.getDefault ());
		SymWindow aSymWindow = new SymWindow ();
		this.addWindowListener (aSymWindow);

		// Initialize useful fonts
		titleFont = new Font ("Lucida Grande", Font.BOLD, 14);
		if (titleFont == null) {
			titleFont = new Font ("SansSerif", Font.BOLD, 14);
		}
		bodyFont = new Font ("Lucida Grande", Font.PLAIN, 10);
		if (bodyFont == null) {
			bodyFont = new Font ("SansSerif", Font.PLAIN, 10);
		}

		this.getContentPane ().setLayout (new BorderLayout (15, 15));

		aboutLabel = new JLabel [labelCount];
		aboutLabel [0] = new JLabel ("");
		aboutLabel [1] = new JLabel (resbundle.getString ("frameConstructor"));
		aboutLabel [1].setFont (titleFont);
		aboutLabel [2] = new JLabel (resbundle.getString ("appVersion"));
		aboutLabel [2].setFont (bodyFont);
		aboutLabel [3] = new JLabel ("");
		aboutLabel [4] = new JLabel ("");
		aboutLabel [5] = new JLabel ("JDK " + System.getProperty ("java.version"));
		aboutLabel [5].setFont (bodyFont);
		aboutLabel [6] = new JLabel (resbundle.getString ("copyright"));
		aboutLabel [6].setFont (bodyFont);
		aboutLabel [7] = new JLabel ("");

		Panel textPanel2 = new Panel (new GridLayout (labelCount, 1));
		for (int i = 0; i < labelCount; i++) {
			aboutLabel [i].setHorizontalAlignment (SwingConstants.CENTER);
			textPanel2.add (aboutLabel [i]);
		}
		this.getContentPane ().add (textPanel2, BorderLayout.CENTER);
		this.pack ();
		this.setLocation (aboutLeft, aboutTop);
		this.setSize (aboutWidth, aboutHeight);
	}

	class SymWindow extends java.awt.event.WindowAdapter {
		@Override
		public void windowClosing (java.awt.event.WindowEvent event) {
			setVisible (false);
		}
	}

	@Override
	public void actionPerformed (ActionEvent newEvent) {
		setVisible (false);
	}
}