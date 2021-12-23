package ge18xx.game;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class FrameButton {
	public final static FrameButton NO_FRAME_BUTTON = null;
	private final JButton NO_JBUTTON = null;
	private final JCheckBox NO_JCHECKBOX = null;
	private final String NO_GROUP_NAME = null;
	public final String NO_TITLE = null;
	JButton jButton;
	JCheckBox jCheckBox;
	String groupName;
	
	public FrameButton (JButton aJButton) {
		setJButton (aJButton);
		setJCheckBox (NO_JCHECKBOX);
		setGroupName (NO_GROUP_NAME);
	}

	public FrameButton (JCheckBox aJCheckBox, String aGroupName) {
		setJCheckBox (aJCheckBox);
		setGroupName (aGroupName);
		setJButton (NO_JBUTTON);
	}
	
	private void setJButton (JButton aJButton) {
		jButton = aJButton;
	}
	
	private void setJCheckBox (JCheckBox aJCheckBox) {
		jCheckBox = aJCheckBox;
	}
	
	private void setGroupName (String aGroupName) {
		groupName = aGroupName;
	}
	
	public boolean isJButton () {
		return jButton != NO_JBUTTON;
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
		return jCheckBox != NO_JCHECKBOX;
	}
	
	public String getGroupName () {
		return groupName;
	}
	
	public String getTitle () {
		String tTitle = NO_TITLE;
		
		if (isJButton ()) {
			tTitle = jButton.getText ();
		} else if (isJCheckBox ()) {
			tTitle = jCheckBox.getText ();
		}
		
		return tTitle;
	}
	
	public String getToolTipText () {
		String tToolTipText = NO_TITLE;
		
		if (isJButton ()) {
			tToolTipText = jButton.getToolTipText ();
		} else if (isJCheckBox ()) {
			tToolTipText = jCheckBox.getToolTipText ();
		}
		
		return tToolTipText;
	}
	
	public boolean getDisabled () {
		boolean tIsDisabled = ! getEnabled ();
		
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
		JButton tJButton = null;
		
		if (isJButton ()) {
			tJButton = jButton;
		}
		
		return tJButton;
	}

	public JCheckBox getJCheckBox () {
		JCheckBox tJCheckBox = null;
		
		if (isJCheckBox ()) {
			tJCheckBox = jCheckBox;
		}
		
		return tJCheckBox;
	}
}
