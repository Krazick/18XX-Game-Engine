package ge18xx.round;

import java.awt.Color;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ge18xx.utilities.MessageBean;

public abstract class ListenerPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	RoundManager roundManager;
	List<String> messages;
	boolean listen;
	String panelName;

	public ListenerPanel (RoundManager aRoundManager, String aPanelName) {
		super ();
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
		setPanelName (aPanelName);
	}
	
	public ListenerPanel (LayoutManager aLayout, RoundManager aRoundManager, String aPanelName) {
		super (aLayout);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
		setPanelName (aPanelName);
	}

	public ListenerPanel (boolean aIsDoubleBuffered, RoundManager aRoundManager, 
						String aPanelName) {
		super (aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
		setPanelName (aPanelName);
	}

	public ListenerPanel (LayoutManager aLayout, boolean aIsDoubleBuffered, 
						RoundManager aRoundManager, String aPanelName) {
		super (aLayout, aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
		setPanelName (aPanelName);
	}

	private void setPanelName (String aName) {
		panelName = aName;
	}

	private void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
	}

	public void setListen (boolean aListen) {
		listen = aListen;
	}
	
	protected void addMessage (String aMessage) {
		messages.add (aMessage);
	}
	
	public void buildBorder (String aTitle, int aJustification, Color aColor) {
		Border tBorder1;
		Border tBorder2;

		tBorder1 = BorderFactory.createLineBorder (aColor);
		tBorder2 = BorderFactory.createTitledBorder (tBorder1, aTitle, aJustification, TitledBorder.TOP);
		setBorder (tBorder2);
	}

	@Override
	public void propertyChange (PropertyChangeEvent aEvent) {
		String tMessage;
		Object tSource;
		MessageBean tMessageBean;
		boolean tUpdatePanel;
		int tMessageCount;
		int tMessageIndex;
		
		// Update routine to look at all messages, and any that should trigger an update 
		// will then set the (new) 'tUpdatePanel' flag as true.
		// After the loop through all messages, if the flag is true, trigger the updatePanel call.
		
		tUpdatePanel = false;
		tSource = aEvent.getSource ();
		if (tSource instanceof MessageBean) {
			tMessageBean = (MessageBean) tSource;
			tMessageCount = tMessageBean.getMessageCount ();
		} else {
			tMessageBean = MessageBean.NO_BEAN;
			tMessageCount = 0;
		}

		if (listen) {
			if (tMessageBean != MessageBean.NO_BEAN) {
				for (tMessageIndex = 0; tMessageIndex < tMessageCount; tMessageIndex++) {
					tMessage = tMessageBean.getMessageAt (tMessageIndex);
					for (String tValidMessage : messages) {
						if (tMessage.equals (tValidMessage)) {
							tUpdatePanel = true;
						} else if (tMessage.startsWith (tValidMessage)) {
							tUpdatePanel = true;
						}
					}
					
				}
			}
		}

		if (tUpdatePanel) {
			updatePanel ();
		}
	}

	protected abstract void updatePanel ();
}
