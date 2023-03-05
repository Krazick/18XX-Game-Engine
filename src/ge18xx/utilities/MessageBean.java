package ge18xx.utilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

public class MessageBean {
	public final static MessageBean NO_BEAN = null;
	private PropertyChangeSupport support;
	private String oldMessage;
	private String propertyName;
	private List<String> messages;
	private boolean active;
	int index;
	
	// Add a list of Messages, and an Index.
	// Add a method 'flushMessages' to clear out the list of Messages
	// Add a method 'sendMessages' to fire the Property Changed to the 'support' object
	// Add a method 'processMessage' to indicate the message has been handled.  
	// 		This would include removing the message
	// Rename 'setMessage' to addMessage
	//		Allow 'addMessage' to send messages, if Active
	
	// When the Action Manager is ready to apply a Action:
	//  1. Deactivate all the Message Beans
	//  2. Allow the Message Bean to add new Messages
	//  3. When all of the Effects have been applied, Reactive the Message Bean
	//  4. Call the 'sendMessages' method to send all backlogged messages 
	
	public MessageBean (String aPropertyName) {
		support = new PropertyChangeSupport (this);
		setActive (false);
		messages = new LinkedList<String> ();
		index = 0;
		setOldMessage (GUI.EMPTY_STRING);
		setPropertyName (aPropertyName);
	}
	
	public void setActive (boolean aActive) {
		active = aActive;
	}
	
	public void setPropertyName (String aPropertyName) {
		propertyName = aPropertyName;
	}
	
	public String getPropertyName () {
		return propertyName;
	}
	
	public void flushMessages () {
		messages.clear ();
	}
	
	public void addPropertyChangeListener (PropertyChangeListener aListener) {
	    support.addPropertyChangeListener (aListener);
	}
	
	public void removePropertyChangeListener (PropertyChangeListener aListener) {
	    support.removePropertyChangeListener (aListener);
	}
	
	public void setOldMessage (String aOldMessage) {
		oldMessage = aOldMessage;
	}
	
	public int getMessageCount () {
		return messages.size ();
	}
	
	public String getMessageAt (int aIndex) {
		return messages.get (aIndex);
	}
	
	public void addMessage (String aNewValue) {
	    messages.add (aNewValue);
	    index++;
	    if (active) {
	    		sendMessages ();
	    }
	}
	
	public void sendMessages  () {
		String tOldMessage;
		
//		System.out.println ("Send Bean [" + propertyName + "] Messages Count " + 
//							messages.size () +  " " + messages.toString ());
		if (active) {
			tOldMessage = oldMessage;
			for (String tNewMessage : messages) {
			    // The parameter values of firePropertyChange method
			    // constitute the PropertyChangeEvent object
			    support.firePropertyChange (propertyName, tOldMessage, tNewMessage);
			}
			flushMessages ();
		}
	}
}
