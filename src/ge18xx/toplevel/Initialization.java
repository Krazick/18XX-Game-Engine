package ge18xx.toplevel;

//
//  Initialization.java
//  Game_18XX
//
//  Created by Mark Smith on 9/1/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Initialization extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton okButton;
    protected JLabel InitializationText;
	protected String selectedGame;
	protected String selectedVariation;
	
    public Initialization () {
		super();
		
        this.getContentPane().setLayout(new BorderLayout(10, 10));
        InitializationText = new JLabel ("18XX Game Selection ...");
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        textPanel.add(InitializationText);
		addGameChoices (textPanel);
        this.getContentPane().add (textPanel, BorderLayout.NORTH);
		
        okButton = new JButton("OK");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add (okButton);
        okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent newEvent) {
				setVisible(false);
			}	
		});
        this.getContentPane ().add (buttonPanel, BorderLayout.SOUTH);
		setSize (590, 129);
		setLocation (20, 140);
    }
	
	public void addGameChoices (JPanel textPanel) {
		JRadioButton tGameChoice;
		ButtonGroup group = new ButtonGroup();
		String tGameChoices [] = {"1856", "1835", "1853", "1830", "1870", "18EU"};
		int tIndex;
		int tGameCount = tGameChoices.length;
		
		for (tIndex = 0; tIndex < tGameCount; tIndex++) {
			tGameChoice = new JRadioButton (tGameChoices [tIndex]);
			tGameChoice.setActionCommand(tGameChoices[tIndex]);
			if (tIndex == 0) {
				tGameChoice.setSelected(true);
				selectedGame = tGameChoices [tIndex];
			}
			group.add (tGameChoice);
			tGameChoice.addActionListener (new ActionListener() {
				public void actionPerformed(ActionEvent newEvent) {
					selectedGame = newEvent.getActionCommand();
				}	
			});
			
			textPanel.add (tGameChoice);
		}
	}
	
	public String getSelectedGame () {
		return selectedGame;
	}
	
}