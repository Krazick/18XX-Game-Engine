package ge18xx.round;

import java.awt.Color;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ge18xx.utilities.GUI;

public abstract class ObserverPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	RoundManager roundManager;
	List<String> messages;

	public ObserverPanel (RoundManager aRoundManager) {
		super ();
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ObserverPanel (LayoutManager layout, RoundManager aRoundManager) {
		super (layout);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ObserverPanel (boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (isDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	public ObserverPanel (LayoutManager layout, boolean isDoubleBuffered, RoundManager aRoundManager) {
		super (layout, isDoubleBuffered);
		setRoundManager (aRoundManager);
		messages = new LinkedList<String> ();
	}

	private void setRoundManager (RoundManager aRoundManager) {
		roundManager = aRoundManager;
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
	public void update (Observable aObservable, Object aMessage) {
		String tMessage;
		
		if (aMessage instanceof String) {
			tMessage = (String) aMessage;
		} else {
			tMessage = GUI.EMPTY_STRING;
		}
		System.out.println ("Observed Message " + tMessage);
		for (String tValidMessage : messages) {
			if (tMessage.equals (tValidMessage)) {
				updatePanel ();
			} else if (tMessage.startsWith (tValidMessage)) {
				updatePanel ();
			}
		}
	}

	protected abstract void updatePanel ();
}
