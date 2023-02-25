package ge18xx.utilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MessageBean {
	public final static MessageBean NO_BEAN = null;
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	private String message; // the bean  property
	private boolean active;
	
	public MessageBean () {
		setActive (false);	
	}
	
	public void setActive (boolean aActive) {
		active = aActive;
	}
	
	public void addPropertyChangeListener (PropertyChangeListener aListener) {
	    support.addPropertyChangeListener (aListener);
	}
	
	public void removePropertyChangeListener (PropertyChangeListener aListener) {
	    support.removePropertyChangeListener (aListener);
	}
	
	public String getMessage () {
	    return message;
	}
	
	public void setMessage (String aNewValue) {
	    String tOldValue;
	    
	    if (active) {
		    tOldValue = message;
		    message = aNewValue;
		    // The parameter values of firePropertyChange method
		    // constitute the PropertyChangeEvent object
		    support.firePropertyChange ("message", tOldValue, aNewValue);
	    }
	}
}
