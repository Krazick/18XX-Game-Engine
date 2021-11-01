package ge18xx.utilities;

//
//  ColorRenderer.java
//  Game_18XX
//
//  Created by Mark Smith on 1/6/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

/* 
* ColorRenderer.java Class from Java Tutorials.
 */

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class ColorRenderer extends JLabel implements TableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;
	
    public ColorRenderer (boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque (true); //MUST do this for background to show up.
    }
	
    @Override
	public Component getTableCellRendererComponent (JTable table, Object color, boolean isSelected, 
						boolean hasFocus, int row, int column) {
        Color newColor = (Color) color;
        setBackground (newColor);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder (2, 5, 2, 5, table.getSelectionBackground ());
                }
                setBorder (selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground ());
                }
                setBorder (unselectedBorder);
            }
        }
        
        setToolTipText ("RGB value: " + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue());
		
        return this;
    }
}