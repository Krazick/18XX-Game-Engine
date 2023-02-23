package ge18xx.utilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MessageBean {	
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String message; // the bean  property
        
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
        
        tOldValue = message;
        message = aNewValue;
        // The parameter values of firePropertyChange method
        // constitute the PropertyChangeEvent object
        support.firePropertyChange ("message", tOldValue, aNewValue);
    }
}
