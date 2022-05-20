package ge18xx.game;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import ge18xx.utilities.GUI;

public class FrameButton {
	public final static FrameButton NO_FRAME_BUTTON = null;
	public final static String NO_GROUP_NAME = GUI.NULL_STRING;
	JButton jButton;
	JCheckBox jCheckBox;
	String groupName;

	public FrameButton (JButton aJButton) {
		setJButton (aJButton);
		setJCheckBox (GUI.NO_CHECK_BOX);
		setGroupName (NO_GROUP_NAME);
	}

	public FrameButton (JCheckBox aJCheckBox, String aGroupName) {
		setJCheckBox (aJCheckBox);
		setGroupName (aGroupName);
		setJButton (GUI.NO_BUTTON);
	}

	public void setCheckBox (JCheckBox aJCheckBox, String aGroupName) {
		setJCheckBox (aJCheckBox);
		setGroupName (aGroupName);
	}

	private void setJButton (JButton aJButton) {
		jButton = aJButton;
	}

	private void setJCheckBox (JCheckBox aJCheckBox) {
		jCheckBox = aJCheckBox;
	}

	public void setGroupName (String aGroupName) {
		groupName = aGroupName;
	}

	public boolean isJButton () {
		return jButton != GUI.NO_BUTTON;
	}

	public void setVisible (boolean aVisibleFlag) {
		if (isJCheckBox ()) {
			jCheckBox.setVisible (aVisibleFlag);
		} else if (isJButton ()) {
			jButton.setVisible (aVisibleFlag);
		}
	}

	public boolean isVisible () {
		boolean tIsVisible = false;

		if (isJCheckBox ()) {
			tIsVisible = jCheckBox.isVisible ();
		} else if (isJButton ()) {
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

		if (isJButton ()) {
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

		if (isJButton ()) {
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

		if (isJButton ()) {
			tIsEnabled = jButton.isEnabled ();
		} else if (isJCheckBox ()) {
			tIsEnabled = jCheckBox.isEnabled ();
		}

		return tIsEnabled;
	}

	public JButton getJButton () {
		JButton tJButton = GUI.NO_BUTTON;

		if (isJButton ()) {
			tJButton = jButton;
		}

		return tJButton;
	}

	public JCheckBox getJCheckBox () {
		JCheckBox tJCheckBox = GUI.NO_CHECK_BOX;

		if (isJCheckBox ()) {
			tJCheckBox = jCheckBox;
		}

		return tJCheckBox;
	}
}
