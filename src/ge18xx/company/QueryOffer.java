package ge18xx.company;

import org.w3c.dom.NodeList;

import ge18xx.round.action.ActorI;
import ge18xx.round.action.GenericActor;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public abstract class QueryOffer {
	public static final QueryOffer NO_QUERY_OFFER = null;
	public static final ElementName EN_QUERY_OFFER = new ElementName ("QueryOffer");
	public static final AttributeName AN_CLASS_NAME = new AttributeName ("className");
	public static final AttributeName AN_ITEM_NAME = new AttributeName ("itemName");
	public static final AttributeName AN_FROM_ACTOR_NAME = new AttributeName ("fromActorName");
	public static final AttributeName AN_TO_ACTOR_NAME = new AttributeName ("toActorName");
	public static final AttributeName AN_OLD_STATUS = new AttributeName ("oldStatus");
	public static final AttributeName AN_STATUS = new AttributeName ("status");
	public static final String NONE = "None";
	public static final String ACCEPTED = "Accepted";
	public static final String PENDING = "Pending";
	public static final String REJECTED = "Rejected";
	public static final String PROCESSED = "Processed";
	String itemName;
	String fromActorName;
	String toActorName;
	ActorI.ActionStates oldStatus;
	// TODO Convert to an ENUM with options on Status.
	String status;

	public QueryOffer (String aItemName, String aFromActorName, String aToActorName, ActorI.ActionStates aOldState) {

		setItemName (aItemName);
		setFromActorName (aFromActorName);
		setToActorName (aToActorName);

		setOldState (aOldState);
		setStatus (PENDING);
	}

	public QueryOffer (XMLNode aChildNode) {
		XMLNode tPONode;
		NodeList tPurchaseOfferList;
		int tPOCount, tPOIndex;
		String tItemName;
		String tFromActorName;
		String tToActorName;
		String tOldStateName;
		ActorI.ActionStates tOldState;	
		GenericActor tGenericActor;
		String tStatus;

		tPurchaseOfferList = aChildNode.getChildNodes ();
		tPOCount = tPurchaseOfferList.getLength ();
		for (tPOIndex = 0; tPOIndex < tPOCount; tPOIndex++) {
			tPONode = new XMLNode (tPurchaseOfferList.item (tPOIndex));
			tItemName = tPONode.getThisAttribute (AN_ITEM_NAME);
			tFromActorName = tPONode.getThisAttribute (AN_FROM_ACTOR_NAME);
			tToActorName = tPONode.getThisAttribute (AN_TO_ACTOR_NAME);
			tOldStateName = tPONode.getThisAttribute (AN_OLD_STATUS);
			tStatus = tPONode.getThisAttribute (AN_STATUS);
			setItemName (tItemName);
			setFromActorName (tFromActorName);
			setToActorName (tToActorName);
			
			tGenericActor = new GenericActor ();
			tOldState = tGenericActor.getCorporationActionState (tOldStateName);
			setOldState (tOldState);
			setStatus (tStatus);
		}
	}
	
	public XMLElement getElements (XMLDocument aXMLDocument) {
		XMLElement tXMLELements;
		
		tXMLELements = getElements (aXMLDocument, EN_QUERY_OFFER);
		
		return tXMLELements;
	}

	public XMLElement getElements (XMLDocument aXMLDocument, ElementName aElementName) {
		XMLElement tXMLElement;
		
		tXMLElement = aXMLDocument.createElement (aElementName);
		tXMLElement.setAttribute (AN_ITEM_NAME, itemName);
		tXMLElement.setAttribute (AN_ITEM_NAME, fromActorName);
		tXMLElement.setAttribute (AN_FROM_ACTOR_NAME, fromActorName);
		tXMLElement.setAttribute (AN_TO_ACTOR_NAME, toActorName);
		tXMLElement.setAttribute (AN_OLD_STATUS, oldStatus.toString ());
		tXMLElement.setAttribute (AN_STATUS, status);

		return tXMLElement;
	}

	public void setStatus (String aStatus) {
		status = aStatus;
	}
	
	public boolean wasAccepted () {
		boolean tWasAccepted;
	
		tWasAccepted = false;
		if (status.equals (ACCEPTED)) {
			tWasAccepted = true;
		}
		
		return tWasAccepted;
	}
	
	public boolean wasRejected () {
		boolean tWasRejected;
	
		tWasRejected = false;
		if (status.equals (REJECTED)) {
			tWasRejected = true;
		}
		
		return tWasRejected;
	}
	
	public boolean isPending () {
		boolean tIsPending;
		
		tIsPending = false;
		if (status.equals (PENDING)) {
			tIsPending = true;
		}
		
		return tIsPending;
	}

	private void setItemName (String aItemName) {
		itemName = aItemName;
	}

	private void setFromActorName (String aFromActorName) {
		fromActorName = aFromActorName;
	}

	private void setToActorName (String aToActorName) {
		toActorName = aToActorName;
	}

	private void setOldState (ActorI.ActionStates aOldState) {
		oldStatus = aOldState;
	}

	public String getItemName () {
		return itemName;
	}

	public String getFromActorName () {
		return fromActorName;
	}

	public String getToActorName () {
		return toActorName;
	}

	public ActorI.ActionStates getOldStatus () {
		return oldStatus;
	}
	
	public abstract String getItemType ();
}
