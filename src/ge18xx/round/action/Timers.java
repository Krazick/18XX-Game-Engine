package ge18xx.round.action;

import java.time.Duration;
import java.time.LocalDateTime;

import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class Timers {
	public static final AttributeName AN_ADD_TIME_BUDGET = new AttributeName ("addTimeBudget");
	public static final AttributeName AN_START_TIMED_EVENTS = new AttributeName ("startTimedEvents");
	public static final AttributeName AN_TIME_BUDGET = new AttributeName ("timeBudget");
	public static final AttributeName AN_TOTAL_TIME_USED = new AttributeName ("totalTimeUsed");
	public static final AttributeName AD_ACTION_START_TIME = new AttributeName ("actionStartTime");
	public static final AttributeName AN_ACTION_END_TIME = new AttributeName ("actionEndTime");
	private static final LocalDateTime CLEAR_ACTION_TIME = LocalDateTime.now ();
	
	// Timers Fields
	boolean addTimeBudget;
	boolean startTimedEvents;
	Duration timeBudget;
	Duration totalTimeUsed;
	LocalDateTime actionStartTime;
	LocalDateTime actionEndTime;

	public Timers () {
		// Set up Time Budget, and initial state for the Time Variables.
		setAddTimeBudget (true);
		clearActionTimes ();
		setTimeBudget (Duration.ZERO);
		setTotalTimeUsed (Duration.ZERO);
	}

	public void addTimeFields (XMLElement aXMLElement) {
		// TODO Auto-generated method stub
		aXMLElement.setAttribute (AN_ADD_TIME_BUDGET, addTimeBudget);
		aXMLElement.setAttribute (AN_START_TIMED_EVENTS, startTimedEvents);
		aXMLElement.setAttribute (AN_TIME_BUDGET, timeBudget.toString ());
		aXMLElement.setAttribute (AN_TOTAL_TIME_USED, totalTimeUsed.toString ());
		aXMLElement.setAttribute (AD_ACTION_START_TIME, actionStartTime.toString ());
		aXMLElement.setAttribute (AN_ACTION_END_TIME, actionEndTime.toString ());
	}

	public void loadTimeFields (XMLNode aPlayerNode) {
		boolean tAddTimeBudget;
		boolean tStartTimedEvents;
		Duration tTimeBudget;
		Duration tTotalTimeUsed;
		LocalDateTime tActionStartTime;
		LocalDateTime tActionEndTime;

		tAddTimeBudget = aPlayerNode.getThisBooleanAttribute (AN_ADD_TIME_BUDGET);
		tStartTimedEvents = aPlayerNode.getThisBooleanAttribute (AN_START_TIMED_EVENTS);
		tTimeBudget = aPlayerNode.getThisDurationAttribute (AN_TIME_BUDGET);
		tTotalTimeUsed = aPlayerNode.getThisDurationAttribute (AN_TOTAL_TIME_USED);
		tActionStartTime = aPlayerNode.getThisLocalDateTimeAttribute (AD_ACTION_START_TIME);
		tActionEndTime = aPlayerNode.getThisLocalDateTimeAttribute (AN_ACTION_END_TIME);

		setAddTimeBudget (tAddTimeBudget);
		setStartTimedEvents (tStartTimedEvents);
		setTimeBudget (tTimeBudget);
		setTotalTimeUsed (tTotalTimeUsed);
		setActionStartTime (tActionStartTime);
		setActionEndTime (tActionEndTime);
	}
	public void clearActionTimes () {
		setActionStartTime (CLEAR_ACTION_TIME);
		setActionEndTime (CLEAR_ACTION_TIME);
	}
	
	public void setAddTimeBudget (boolean aAddTimeBudget) {
		addTimeBudget = aAddTimeBudget;
	}

	public void setStartTimedEvents (boolean aStartTimedEvents) {
		startTimedEvents = aStartTimedEvents;
	}
	
	public void setActionStartTime (LocalDateTime aActionStartTime) {
		actionStartTime = aActionStartTime;
	}
	
	public void setActionEndTime (LocalDateTime aActionEndTime) {
		actionEndTime = aActionEndTime;
	}
	
	public LocalDateTime getActonStartTime () {
		return actionStartTime;
	}
	
	public LocalDateTime getActonEndTime () {
		return actionEndTime;
	}
	
//	public void setTimeUsed (Duration aTotalTimeUsed) {
//		totalTimeUsed = aTotalTimeUsed;
//	}

	public void setTimeBudget (Duration aTimeBudget) {
		timeBudget = aTimeBudget;
	}

	public boolean hasTimedEventsStarted () {
		return startTimedEvents;
	}
	
	public void setTotalTimeUsed (Duration aTotalTimeUsed) {
		totalTimeUsed = aTotalTimeUsed;
	}
	
	public Duration getTotalTimeUsed () {
		return totalTimeUsed;
	}
	
	public Duration addNewDuration () {
		Duration tDuration;
		Duration tTotalDuration;
		
		tDuration = Duration.between (actionStartTime, actionEndTime);
		tTotalDuration = totalTimeUsed.plus (tDuration);
		setTotalTimeUsed (tTotalDuration);
		
		return tDuration;
	}
	
    public String formatDuration (Duration aDuration) {
    	String tFormatted;
    	
    	long tSeconds = aDuration.getSeconds ();
        long tHours = (tSeconds % 86400) / 3600;
        long tMinutes = (tSeconds % 3600) / 60;
        long tSecs = tSeconds % 60;

        tFormatted = String.format ("%2d:%02d:%02d", tHours, tMinutes, tSecs);
        
        return tFormatted;
    }

    public boolean getAddTimeBudget () {
    	return addTimeBudget;
    }
}
