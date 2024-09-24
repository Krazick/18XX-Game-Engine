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
	public static final AttributeName AN_INTERRUPTION_ROUND = new AttributeName ("interruptionRound");
	public static final AttributeName AN_NEXT_ROUND = new AttributeName ("nextRound");
	public static final AttributeName AN_OPTIONAL_EXTRA = new AttributeName ("optionalExtra");
	public static final AttributeName AN_MAX_ROUNDS = new AttributeName ("maxRounds");

	String name;
	String nextRound;
	String interruptionRound;
	boolean optionalExtra;
	boolean initialRound;
	int maxRounds;
	
//	<RoundType name="Contract Bid Round" initialRound="true" nextRound="Stock Round"/>
//	<RoundType name="Stock Round" initialRound="true" interruptionRound="Auction Round" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" interruptionRound="Auction Round" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" initialRound="true" nextRound="Operating Round"/>
//	<RoundType name="Stock Round" nextRound="Operating Round"/>
//	<RoundType name="Operating Round" nextRound="Stock Round" maxRounds="3" optionalExtra="true"/>
//	<RoundType name="Operating Round"  interruptionRound="Formation Round" nextRound="Stock Round" maxRounds="3" />
//	<RoundType name="Operating Round" nextRound="Stock Round" maxRounds="3" />
//	<RoundType name="Auction Round" />
//	<RoundType name="Formation Round" />

	public RoundType (XMLNode aXMLRoundTypeNode) {
		String tName;
		String tNextRound;
		String tInterruptionRound;
		boolean tOptionalExtra;
		boolean tInitialRound;
		int tMaxRounds;
		
		tName = aXMLRoundTypeNode.getThisAttribute (AN_NAME);
		tNextRound = aXMLRoundTypeNode.getThisAttribute (AN_NEXT_ROUND);
		tInterruptionRound = aXMLRoundTypeNode.getThisAttribute (AN_INTERRUPTION_ROUND);
		tOptionalExtra = aXMLRoundTypeNode.getThisBooleanAttribute (AN_OPTIONAL_EXTRA);
		tInitialRound = aXMLRoundTypeNode.getThisBooleanAttribute (AN_INITIAL_ROUND);
		tMaxRounds = aXMLRoundTypeNode.getThisIntAttribute (AN_MAX_ROUNDS, 1);
		
		setName (tName);
		setNextRound (tNextRound);
		setInterruptionRound (tInterruptionRound);
		setOptionalExtra (tOptionalExtra);
		setInitialRound (tInitialRound);
		setMaxRounds (tMaxRounds);
	}
	
	public void setName (String aName) {
		name = aName;
	}
	
	public void setNextRound (String aNextRound) {
		nextRound = aNextRound;
	}
	
	public void setInterruptionRound (String aInterruptionRound) {
		interruptionRound = aInterruptionRound;
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
	
	public String getNextRound () {
		return nextRound;
	}
	
	public String getInterruptionRound () {
		return interruptionRound;
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
	
	public String getInfo () {
		String tInfo;
		
		tInfo = "Name: " + name + " NextRound: " + nextRound;
		if (interruptionRound != GUI.NULL_STRING) {
			tInfo += " InterruptionRound: "+ interruptionRound;
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
