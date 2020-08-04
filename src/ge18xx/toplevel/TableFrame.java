package ge18xx.toplevel;

//
//  TableFrame.java
//  Game_18XX
//
//  Created by Mark Smith on 1/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.utilities.ColorRenderer;

import java.awt.*;

import javax.swing.*;

public class TableFrame extends XMLFrame {
	private static final long serialVersionUID = 1L;

	public TableFrame (String aFrameName, String aGameName) {
		super (aFrameName, aGameName);
	}
	
	public void setScrollPane (JTable aTable, int aWidth, int aHeight, int aBorder) {
		JScrollPane tScrollPane;
		JPanel tPanel;
		
		aTable.setDefaultRenderer (Color.class, new ColorRenderer (true));
		tScrollPane = new JScrollPane ();
		tScrollPane.setViewportView (aTable);
		tScrollPane.setPreferredSize (new Dimension (aWidth, aHeight));
		tPanel = new JPanel ();
		tPanel.setLayout (new BorderLayout ());
		tPanel.add (tScrollPane, BorderLayout.CENTER);
		tPanel.setBorder (BorderFactory.createEmptyBorder (aBorder, aBorder, aBorder, aBorder));
		add (tPanel);
	}
	
	public void setScrollPane (JTable aTable, int aWidth, int aHeight) {
		setScrollPane (aTable, aWidth, aHeight, 20);
	}
	
	public void setScrollPane (JTable aTable) {
		setScrollPane (aTable, 500, 300, 20);
	}
}
