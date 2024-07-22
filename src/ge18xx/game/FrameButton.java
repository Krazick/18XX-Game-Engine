package ge18xx.game;

import javax.swing.JCheckBox;

import geUtilities.GUI;
import swingTweaks.KButton;

public class FrameButton {
	public final static FrameButton NO_FRAME_BUTTON = null;
	public final static String NO_GROUP_NAME = GUI.NULL_STRING;
	KButton jButton;
	JCheckBox jCheckBox;
	String groupName;

	public FrameButton (KButton aKButton) {
		setKButton (aKButton);
		setJCheckBox (GUI.NO_CHECK_BOX);
		setGroupName (NO_GROUP_NAME);
	}

	public FrameButton (JCheckBox aJCheckBox, String aGroupName) {
		setJCheckBox (aJCheckBox);
		setGroupName (aGroupName);
		setKButton (GUI.NO_BUTTON);
	}

	public void setCheckBox (JCheckBox aJCheckBox, String aGroupName) {
		setJCheckBox (aJCheckBox);
		setGroupName (aGroupName);
	}

	private void setKButton (KButton aKButton) {
		jButton = aKButton;
	}

	private void setJCheckBox (JCheckBox aJCheckBox) {
		jCheckBox = aJCheckBox;
	}

	public void setGroupName (String aGroupName) {
		groupName = aGroupName;
	}

	public boolean isKButton () {
		return jButton != GUI.NO_BUTTON;
	}

	public void setVisible (boolean aVisibleFlag) {
		if (isJCheckBox ()) {
			jCheckBox.setVisible (aVisibleFlag);
		} else if (isKButton ()) {
			jButton.setVisible (aVisibleFlag);
		}
	}

	public boolean isVisible () {
		boolean tIsVisible = false;

		if (isJCheckBox ()) {
			tIsVisible = jCheckBox.isVisible ();
		} else if (isKButton ()) {
			tIsVisible = jButton.isVisible ();
		}

		return tIsVisible;
	}

	public boolean isJCheckBox () {
		return jCheckBox != GUI.NO_CHECK_BOX;
	}

	public String getGroupName () {
		return groupName;
	}

	public String getTitle () {
		String tTitle = GUI.NULL_STRING;

		if (isKButton ()) {
			tTitle = jButton.getText ();
		} else if (isJCheckBox ()) {
			tTitle = jCheckBox.getText ();
		}

		return tTitle;
	}

	public String getDescription () {
		String tButtonDescription = "";

		if (groupName != FrameButton.NO_GROUP_NAME) {
			tButtonDescription = groupName + " - ";
		}
		tButtonDescription += getTitle ();

		return tButtonDescription;
	}

	public String getToolTipText () {
		String tToolTipText = GUI.NO_TOOL_TIP;

		if (isKButton ()) {
			tToolTipText = jButton.getToolTipText ();
		} else if (isJCheckBox ()) {
			tToolTipText = jCheckBox.getToolTipText ();
		}

		return tToolTipText;
	}

	public boolean getDisabled () {
		boolean tIsDisabled = !getEnabled ();

		return tIsDisabled;
	}

	public boolean getEnabled () {
		boolean tIsEnabled = false;

		if (isKButton ()) {
			tIsEnabled = jButton.isEnabled ();
		} else if (isJCheckBox ()) {
			tIsEnabled = jCheckBox.isEnabled ();
		}

		return tIsEnabled;
	}

	public KButton getKButton () {
		KButton tKButton = GUI.NO_BUTTON;

		if (isKButton ()) {
			tKButton = jButton;
		}

		return tKButton;
	}

	public JCheckBox getJCheckBox () {
		JCheckBox tJCheckBox = GUI.NO_CHECK_BOX;

		if (isJCheckBox ()) {
			tJCheckBox = jCheckBox;
		}

		return tJCheckBox;
	}
}
