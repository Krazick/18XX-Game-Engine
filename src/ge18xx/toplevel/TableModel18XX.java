package ge18xx.toplevel;

//
//  TableModel18XX.java
//  Game_18XX
//
//  Created by Mark Smith on 12/28/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//
import javax.swing.table.AbstractTableModel;

public class TableModel18XX extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] headers;
    private Object[][] data;
	int rowCount, colCount;
	
	public TableModel18XX (int aRowCount, int aColCount) {
		data = new Object [aRowCount] [aColCount];
		headers = new String [aColCount];
		rowCount = aRowCount;
		colCount = aColCount;
	}

	public boolean addDataElement (Object aData, int aRowIndex, int aColIndex) {
		boolean tGoodAdd = false;
		
		if ((aRowIndex >= 0) && (aRowIndex < rowCount)) {
			if ((aColIndex >= 0) && (aColIndex < colCount)) {
				setValueAt (aData, aRowIndex, aColIndex);
				tGoodAdd = true;
			}
		}
		
		return tGoodAdd;
	}
	
	public boolean addHeader (String aHeader, int aColIndex) {
		boolean tGoodAdd;
		
		if ((aColIndex >= 0) && (aColIndex <= colCount)) {
			headers [aColIndex] = aHeader;
			tGoodAdd = true;
		} else {
			tGoodAdd = false;
		}
		
		return tGoodAdd;
	}
	
    public Class<?> getColumnClass (int col) {
		Object tValueAt;
		
		if ((col >= 0) && (col < colCount)) {
			tValueAt = getValueAt (0, col);
			if (tValueAt != null) {
				return tValueAt.getClass ();
			} else {
				return "DUMMY".getClass ();
			}
		} else {
			return "DUMMY".getClass ();
		}
    }
	
    public int getColumnCount () {
        return headers.length;
    }
	
    public String getColumnName (int col) {
		if ((col >= 0) && (col < colCount)) {
			return headers [col];
		} else {
			return "";
		}
    }
	
    public int getRowCount () {
        return data.length;
    }
	
    public Object getValueAt (int row, int col) {
		if ((row >= 0) && (row < rowCount)) {
			if ((col >= 0) && (col < colCount)) {
				if (data [row] [col] != null) {
					return data [row] [col];
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else {
			return "";
		}
    }
	
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable (int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }
	
    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt (Object value, int row, int col) {
        data [row] [col] = value;
        fireTableCellUpdated (row, col);
    }
}
