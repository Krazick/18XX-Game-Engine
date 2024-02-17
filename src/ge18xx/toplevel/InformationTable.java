package ge18xx.toplevel;

//
//  InfoTable.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTable;

public abstract class InformationTable extends JPanel {
	private static final long serialVersionUID = 1L;
	JTable table;
	TableModel18XX tm18xx;

	public InformationTable () {
		super ();
	}

	public boolean addDataElement (Object aData, int aRowIndex, int aColIndex) {
		return tm18xx.addDataElement (aData, aRowIndex, aColIndex);
	}

	public boolean addHeader (String aHeader, int aColIndex) {
		return tm18xx.addHeader (aHeader, aColIndex);
	}

	public int getColCount () {
		return tm18xx.getColumnCount ();
	}

	public JTable getJTable () {
		table = new JTable ();
		table.setPreferredScrollableViewportSize (new Dimension (500, 70));

		return table;
	}

	public int getRowCount () {
		return tm18xx.getRowCount ();
	}

	public String getTypeName () {
		return "Information List";
	}

	public boolean initiateArrays (int aRowCount, int aColCount) {
		if ((aRowCount > 0) && (aColCount > 0)) {
			tm18xx = new TableModel18XX (aRowCount, aColCount);
			return true;
		} else {
			return false;
		}
	}

	public int maxColCount () {
		return 0;
	}

	public int maxRowCount () {
		return 0;
	}

	public void setModel () {
		table.setModel (tm18xx);
	}
}
