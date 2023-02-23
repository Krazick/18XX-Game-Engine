package ge18xx.utilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MessageBean {	
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String message; // the bean  property
    private int index;
    
    public MessageBean () {
    		setIndex (0);
    }
    
    public void setIndex (int aIndex) {
    		index = aIndex;
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
        
        setIndex (index + 1);
        tOldValue = message;
        message =  "(" + index + ") " + aNewValue;
        // The parameter values of firePropertyChange method
        // constitute the PropertyChangeEvent object
        support.firePropertyChange ("message", tOldValue, aNewValue);
    }
}
