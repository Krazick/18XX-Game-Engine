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

	public ListenerPanel (RoundManager aRoundManager) {
		super ();
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ListenerPanel (LayoutManager aLayout, RoundManager aRoundManager) {
		super (aLayout);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ListenerPanel (boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ListenerPanel (LayoutManager aLayout, boolean aIsDoubleBuffered, RoundManager aRoundManager) {
		super (aLayout, aIsDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
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
		System.out.println ("Property Change Method Called with Event. Listening " + listen);
		String tMessage;
		Object tSource;
		MessageBean tMessageBean;
		
		if (listen) {
			tSource = aEvent.getSource ();
			if (tSource instanceof MessageBean) {
				tMessageBean = (MessageBean) tSource;
				tMessage = tMessageBean.getMessage ();
				for (String tValidMessage : messages) {
					if (tMessage.equals (tValidMessage)) {
						updatePanel ();
					} else if (tMessage.startsWith (tValidMessage)) {
						updatePanel ();
					}
				}
			}
		}

		updatePanel ();
	}

	protected abstract void updatePanel ();
}
