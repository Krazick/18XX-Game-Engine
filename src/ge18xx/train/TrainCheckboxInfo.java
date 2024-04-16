package ge18xx.train;

import java.awt.event.ItemListener;

import geUtilities.GUI;

public class TrainCheckboxInfo {
	ItemListener itemListener;
	String label;
	String toolTip;
	boolean enabled;

	public TrainCheckboxInfo () {
		setItemListener (GUI.NO_ITEM_LISTENER);
		setLabel (GUI.EMPTY_STRING);
		setToolTip (GUI.EMPTY_STRING);
		setEnabled (false);
	}

	public void setItemListener (ItemListener aItemListener) {
		itemListener = aItemListener;	
	}

	public void setLabel (String aLabel) {
		label = aLabel;
	}

	public void setToolTip (String aToolTip) {
		toolTip = aToolTip;
	}

	public void setEnabled (boolean aEnabled) {
		enabled = aEnabled;
	}
	
	public ItemListener getItemListener () {
		return itemListener;
	}
	
	public String getLabel () {
		return label;
	}
	
	public String getToolTip () {
		return toolTip;
	}
	
	public boolean getEnabled () {
		return enabled;
	}
}
