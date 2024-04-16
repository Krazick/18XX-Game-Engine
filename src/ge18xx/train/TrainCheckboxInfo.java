package ge18xx.train;

import java.awt.event.ItemListener;

import geUtilities.GUI;

public class TrainActionCheckboxInfo {
	ItemListener itemListener;
	String actionLabel;
	String actionToolTip;
	boolean actionEnabled;

	public TrainActionCheckboxInfo () {
		setItemListener (GUI.NO_ITEM_LISTENER);
		setActionLabel (GUI.EMPTY_STRING);
		setActionToolTip (GUI.EMPTY_STRING);
		setActionEnabled (false);
	}

	public void setItemListener (ItemListener aItemListener) {
		itemListener = aItemListener;	
	}

	public void setActionLabel (String aActionLabel) {
		actionLabel = aActionLabel;
	}

	public void setActionToolTip (String aActionToolTip) {
		actionToolTip = aActionToolTip;
	}

	public void setActionEnabled (boolean aActionEnabled) {
		actionEnabled = aActionEnabled;
	}
	
	public ItemListener getItemListener () {
		return itemListener;
	}
	
	public String getActionLabel () {
		return actionLabel;
	}
	
	public String getActionToolTip () {
		return actionToolTip;
	}
	
	public boolean getActionEnabled () {
		return actionEnabled;
	}
}
