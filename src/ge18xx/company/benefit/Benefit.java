package ge18xx.company.benefit;

import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLNode;

public class Benefit {
	public final static ElementName EN_BENEFITS = new ElementName ("Benefits");
	public final static ElementName EN_BENEFIT = new ElementName ("Benefit");
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_CLOSE_ON_USE = new AttributeName ("closeOnUse");
	public final static AttributeName AN_PASSIVE = new AttributeName ("passive");
	public final static AttributeName AN_ACTOR_TYPE = new AttributeName ("actorType");
	
	ActorI.ActorTypes actorType;
	boolean closeOnUse;
	boolean used;
	boolean passive;
	
//	String mapCellID;
//	boolean extraTilePlacement;
//	boolean tilePlacement;
//	boolean tokenPlacement;
//	int cost;
//	boolean sameTurn;
//	String exchangeID;
//	int exchangePercentage;
//	String freeShareID;
//	int freeSharePercentage;
	
	public Benefit () {
//		setValues (ActorI.ActorTypes.NO_TYPE.toString (), false, "", 0, false, false, false, "", 0, false, "", 0);
	}
	
	public Benefit (XMLNode aXMLNode) {
		boolean tClose;
		boolean tPassive;
		String tActorType;
		
		tActorType = aXMLNode.getThisAttribute (AN_ACTOR_TYPE);
		tClose = aXMLNode.getThisBooleanAttribute (AN_CLOSE_ON_USE);
		tPassive = aXMLNode.getThisBooleanAttribute (AN_PASSIVE);
		setCloseOnUse (tClose);
		setPassive (tPassive);
		setActorType (tActorType);
		setUsed (false);
	}
	
	private void setActorType (String aActorType) {
		actorType = ActorI.ActorTypes.fromString (aActorType);
	}
	
	private void setPassive (boolean aPassive) {
		passive = aPassive;
	}
	
	private void setUsed (boolean aUsed) {
		used = aUsed;
	}
	
	private void setCloseOnUse (boolean aCloseOnUse) {
		closeOnUse = aCloseOnUse;
	}
	
	public boolean used () {
		return used;
	}
	
	public boolean closeOnUse () {
		return closeOnUse;
	}
	
	public boolean passive () {
		return passive;
	}
	
//	private void setValues (String aActorType, boolean aExtraTilePlacement, String aMapCellID, int aCost,
//			boolean aTilePlacement, boolean aTokenPlacement, boolean aSameTurn, String aExchangeID, int aExchangePercentage,
//			boolean aCloseOnUse, String aFreeShareID, int aFreeSharePercentage) {
//		setActorType (aActorType);
////		setExtraTilePlacement  (aExtraTilePlacement);
//		setMapCellID  (aMapCellID);
//		setCost  (aCost);
////		setExtraTilePlacement (aTilePlacement);
////		setExtraTokenPlacement (aTokenPlacement);
//		setSameTurn (aSameTurn);
////		setExchangeID (aExchangeID);
////		setExchangePercentage (aExchangePercentage);
//		setCloseOnUse (aCloseOnUse);
//		setFreeShareID (aFreeShareID);
//		setFreeSharePercentage (aFreeSharePercentage);
//	}
//	
//	private void setFreeSharePercentage (int aFreeSharePercentage) {
//		freeSharePercentage = aFreeSharePercentage;
//	}
//
//	private void setFreeShareID (String aFreeShareID) {
//		freeShareID = aFreeShareID;
//	}
//
//	private void setCloseOnUse (boolean aCloseOnUse) {
//		closeOnUse = aCloseOnUse;
//	}
////
////	private void setExchangePercentage (int aExchangePercentage) {
////		exchangePercentage = aExchangePercentage;
////	}
////
////	private void setExchangeID (String aExchangeID) {
////		exchangeID = aExchangeID;
////	}
//
//	private void setSameTurn (boolean aSameTurn) {
//		sameTurn = aSameTurn;
//	}
//
//	private void setCost (int aCost) {
//		cost = aCost;
//	}
//
//	private void setMapCellID (String aMapCellID) {
//		mapCellID = aMapCellID;
//	}
//
////	private void setExtraTilePlacement (boolean aExtraTilePlacement) {
////		extraTilePlacement = aExtraTilePlacement;
////	}
//
//	private void setActorType (String aActorType) {
//		actorType = ActorI.ActorTypes.fromString(aActorType);
//	}
//	
//	public ActorI.ActorTypes getActorType () {
//		return actorType;
//	}
//	
//	public boolean givesExtraTilePlacment () {
//		return extraTilePlacement;
//	}
//	
//	public String getMapCellID () {
//		return mapCellID;
//	}
//	
//	public int getCost () {
//		return cost;
//	}
//	
//	public boolean mustBeSameTurn () {
//		return sameTurn;
//	}
//	
//	public boolean closesPrivate () {
//		return closeOnUse;
//	}
//	
//	public String getFreeShareID () {
//		return freeShareID;
//	}
//	
//	public int getFreeSharePercentage () {
//		return freeSharePercentage;
//	}
//	
////	public String getExchangeID () {
////		return exchangeID;
////	}
//	
//	public int getExchangePercentage () {
//		return exchangePercentage;
//	}
}
