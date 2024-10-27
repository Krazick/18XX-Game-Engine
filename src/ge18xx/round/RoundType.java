package ge18xx.round;

import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.ElementName;
import geUtilities.xml.XMLNode;

public class RoundType {
	public static final ElementName EN_ROUND_TYPES = new ElementName ("RoundTypes");
	public static final ElementName EN_ROUND_TYPE = new ElementName ("RoundType");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_INITIAL_ROUND = new AttributeName ("initialRound");
	public static final AttributeName AN_INTERRUPTION_ROUND_NAME = new AttributeName ("interruptionRound");
	public static final AttributeName AN_NEXT_ROUND_NAME = new AttributeName ("nextRound");
	public static final AttributeName AN_OPTIONAL_EXTRA = new AttributeName ("optionalExtra");
	public static final AttributeName AN_INTERRUPTS_AFTER_ACTIONS = new AttributeName ("interruptsAfterActions");
	public static final AttributeName AN_ENDS_AFTER_ACTIONS = new AttributeName ("endsAfterActions");
	public static final AttributeName AN_MAX_ROUNDS = new AttributeName ("maxRounds");
	public static final RoundType NO_ROUND_TYPE = null;
	
	String name;
	String nextRoundName;
	String interruptionRoundName;
	String interruptsAfterActions;
	String endsAfterActions;
	boolean optionalExtra;
	boolean initialRound;
	int maxRounds;
	
//	<RoundType name="Contract Bid Round" initialRound="true" nextRound="Stock Round"/>
//	<RoundType name="Stock Round" initialRound="true" interruptionRound="Auction Round" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" interruptionRound="Auction Round" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" initialRound="true" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" nextRound="Operating Round"/>
//	<RoundType name="Operating Round" nextRound="Stock Round" maxRounds="3" optionalExtra="true"/>
//	<RoundType name="Operating Round" interruptionRound="Formation Round" nextRound="Stock Round" maxRounds="3" />
//	<RoundType name="Operating Round" nextRound="Stock Round" maxRounds="3" />
//	<RoundType name="Auction Round" afterActions="Done Player, Win Auction" />
//	<RoundType name="Formation Round" />

	public RoundType (XMLNode aXMLRoundTypeNode) {
		String tName;
		String tNextRoundName;
		String tInterruptionRoundName;
		String tInterruptsAfterActions;
		String tEndsAfterActions;
		boolean tOptionalExtra;
		boolean tInitialRound;
		int tMaxRounds;
		
		tName = aXMLRoundTypeNode.getThisAttribute (AN_NAME);
		tNextRoundName = aXMLRoundTypeNode.getThisAttribute (AN_NEXT_ROUND_NAME);
		tInterruptionRoundName = aXMLRoundTypeNode.getThisAttribute (AN_INTERRUPTION_ROUND_NAME);
		tInterruptsAfterActions = aXMLRoundTypeNode.getThisAttribute (AN_INTERRUPTS_AFTER_ACTIONS);
		tEndsAfterActions = aXMLRoundTypeNode.getThisAttribute (AN_ENDS_AFTER_ACTIONS);
		tOptionalExtra = aXMLRoundTypeNode.getThisBooleanAttribute (AN_OPTIONAL_EXTRA);
		tInitialRound = aXMLRoundTypeNode.getThisBooleanAttribute (AN_INITIAL_ROUND);
		tMaxRounds = aXMLRoundTypeNode.getThisIntAttribute (AN_MAX_ROUNDS, 1);
		
		setName (tName);
		setNextRoundName (tNextRoundName);
		setInterruptionRoundName (tInterruptionRoundName);
		setInterruptsAfterActions (tInterruptsAfterActions);
		setEndsAfterActions (tEndsAfterActions);
		setOptionalExtra (tOptionalExtra);
		setInitialRound (tInitialRound);
		setMaxRounds (tMaxRounds);
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	public void setNextRoundName (String aNextRound) {
		nextRoundName = aNextRound;
	}
	
	public void setInterruptionRoundName (String aInterruptionRoundName) {
		interruptionRoundName = aInterruptionRoundName;
	}
	
	public void setInterruptsAfterActions (String aInterruptsAfterActions) {
		interruptsAfterActions = aInterruptsAfterActions;
	}
	
	public void setEndsAfterActions (String aEndsAfterActions) {
		endsAfterActions = aEndsAfterActions;
	}
	
	public void setOptionalExtra (boolean aOptionalExtra) {
		optionalExtra = aOptionalExtra;
	}
	
	public void setInitialRound (boolean aInitialRound) {
		initialRound = aInitialRound;
	}
	
	public void setMaxRounds (int aMaxRounds) {
		maxRounds = aMaxRounds;
	}
	
	public String getName () {
		return name;
	}
	
	public String getNextRoundName () {
		return nextRoundName;
	}
	
	public String getInterruptionRound () {
		return interruptionRoundName;
	}
	
	public String getInterruptsAfterActions () {
		return interruptsAfterActions;
	}
	
	public String getEndsAfterActions () {
		return endsAfterActions;
	}
	
	public boolean getOptionalExtra () {
		return optionalExtra;
	}
	
	public boolean getInitialRound () {
		return initialRound;
	}
	
	public int getMaxRounds () {
		return maxRounds;
	}
	
	public boolean isAInterruptsAfterAction (String aAction) {
		boolean tIsAInterruptsAfterAction;
		
		tIsAInterruptsAfterAction = interruptsAfterActions.contains (aAction);
		
		return tIsAInterruptsAfterAction;
	}
	
	public boolean isAEndsAfterAction (String aAction) {
		boolean tIsAEndsAfterAction;
		
		tIsAEndsAfterAction = endsAfterActions.contains (aAction);
		
		return tIsAEndsAfterAction;
	}
	
	public boolean matches (String aName) {
		boolean tMatches;
		
		tMatches = name.equals (aName);
		
		return tMatches;
	}
	
	public String getInfo () {
		String tInfo;
		
		tInfo = "Name: " + name + " NextRound: " + nextRoundName;
		if (interruptionRoundName != GUI.NULL_STRING) {
			tInfo += " InterruptionRound: "+ interruptionRoundName;
		}
		if (optionalExtra) {
			tInfo += " Optional Extra Round";
		}
		if (initialRound) {
			tInfo += " This is Initial Round";
		}
		tInfo += " Max Rounds: " + maxRounds;
		
		return tInfo;
	}
}
