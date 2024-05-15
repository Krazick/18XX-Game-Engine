package ge18xx.round.action;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NodeList;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.effects.CashTransferEffect;
import ge18xx.round.action.effects.Effect;
import ge18xx.round.action.effects.PayCashDividendEffect;
import ge18xx.round.action.effects.RefundEscrowEffect;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class Action {
	public final static ElementName EN_ACTIONS = new ElementName ("Actions");
	public final static ElementName EN_ACTION = new ElementName ("Action");
	public final static AttributeName AN_NUMBER = new AttributeName ("number");
	public final static AttributeName AN_TOTAL_CASH = new AttributeName ("totalCash");
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_NAME = new AttributeName ("name");
	public final static AttributeName AN_ROUND_TYPE = new AttributeName ("roundType");
	public final static AttributeName AN_ROUND_ID = new AttributeName ("roundID");
	public final static AttributeName AN_CHAIN_PREVIOUS = new AttributeName ("chainPrevious");
	public final static AttributeName AN_DATE_TIME = new AttributeName ("dateTime");
	public final static ActorI.ActionStates NO_ROUND_TYPE = ActorI.ActionStates.NoRound;
	public final static String NO_NAME = ">> NO ACTION NAME <<";
	public final static String NO_ROUND_ID = ">> NO ROUND ID <<";
	public final static String REPORT_PREFIX = "-";
	public final static Action NO_ACTION = null;
	public final static int NO_NUMBER = 0;
	ActorI.ActionStates roundType;
	protected ActorI actor;
	String name;
	String roundID;
	List<Effect> effects;
	Boolean chainToPrevious;	// Chain this Action to Previous Action --
							// If Undo This Action, Undo Previous Action as well - Default is FALSE;
	int number;
	long dateTime;

	public Action () {
		this (NO_NAME);
	}

	public Action (String aName) {
		setValues (aName, NO_ROUND_TYPE, NO_ROUND_ID, ActorI.NO_ACTOR, NO_NUMBER);
	}

	public Action (Action aAction) {
		setValues (aAction.getName (), aAction.getRoundType (), aAction.getRoundID (), 
					aAction.getActor (), aAction.getNumber ());
	}

	public Action (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		setValues (NO_NAME, aRoundType, aRoundID, aActor, NO_NUMBER);
	}

	private void setValues (String aName, ActorI.ActionStates aRoundType, String aRoundID, 
			ActorI aActor, int aNumber) {
		Date tNow;
		
		tNow = new Date ();
		setDateTime (tNow.getTime ());

		setName (aName);
		setNumber (aNumber);
		setActor (aActor);
		setRoundType (aRoundType);
		setRoundID (aRoundID);
		setChainToPrevious (false);
		effects = new LinkedList<> ();
	}
	
	public Action (XMLNode aActionNode, GameManager aGameManager) {
		String tActionName;
		String tRoundTypeString;
		String tRoundID;
		String tActorName;
		ActorI tActor;
		ActorI.ActionStates tRoundType;
		Boolean tChainToPrevious;
		int tNumber;
		long tDateTime;

		tActionName = aActionNode.getThisAttribute (AN_NAME);
		tNumber = aActionNode.getThisIntAttribute (AN_NUMBER);
		tDateTime = aActionNode.getThisLongAttribute (AN_DATE_TIME);
		tRoundTypeString = aActionNode.getThisAttribute (AN_ROUND_TYPE);
		tRoundID = aActionNode.getThisAttribute (AN_ROUND_ID);
		tActorName = aActionNode.getThisAttribute (ActorI.AN_ACTOR_NAME);
		tChainToPrevious = aActionNode.getThisBooleanAttribute (AN_CHAIN_PREVIOUS);
		tActor = aGameManager.getActor (tActorName);
		tRoundType = aGameManager.getRoundType (tRoundTypeString);

		setValues (tActionName, tRoundType, tRoundID, tActor, tNumber);

		setChainToPrevious (tChainToPrevious);
		setDateTime (tDateTime);
		parseActionNode (aActionNode, aGameManager, tActionName, tNumber);
	}
	
	private void setDateTime (long aDateTime) {
		dateTime = aDateTime;
	}

	private void parseActionNode (XMLNode aActionNode, GameManager aGameManager, String aActionName, int aNumber) {
		XMLNode tEffectsNode;
		XMLNode tEffectNode;
		NodeList tEffectsChildren;
		NodeList tEffectChildren;
		int tEffectNodeCount;
		int tEffectsNodeCount;
		int tEffectIndex;
		int tEffectsIndex;
		String tEffectsNodeName;
		String tEffectNodeName;
		String tClassName;
		Effect tEffect;
		Class<?> tEffectToLoad;
		Constructor<?> tEffectConstructor;

		tEffectsChildren = aActionNode.getChildNodes ();
		tEffectsNodeCount = tEffectsChildren.getLength ();
		tClassName = "NO-CLASS";
		try {
			for (tEffectsIndex = 0; tEffectsIndex < tEffectsNodeCount; tEffectsIndex++) {
				tEffectsNode = new XMLNode (tEffectsChildren.item (tEffectsIndex));
				tEffectsNodeName = tEffectsNode.getNodeName ();
				if (Effect.EN_EFFECTS.equals (tEffectsNodeName)) {
					tEffectChildren = tEffectsNode.getChildNodes ();
					tEffectNodeCount = tEffectChildren.getLength ();
					for (tEffectIndex = 0; tEffectIndex < tEffectNodeCount; tEffectIndex++) {
						tEffectNode = new XMLNode (tEffectChildren.item (tEffectIndex));
						tEffectNodeName = tEffectNode.getNodeName ();
						if (Effect.EN_EFFECT.equals (tEffectNodeName)) {
							// Use Reflections to identify the OptionEffect to create, and call the
							// constructor with the XMLNode and Game Manager
							tClassName = tEffectNode.getThisAttribute (Effect.AN_CLASS);
							tEffectToLoad = Class.forName (tClassName);
							tEffectConstructor = tEffectToLoad.getConstructor (tEffectNode.getClass (),
									aGameManager.getClass ());
							tEffect = (Effect) tEffectConstructor.newInstance (tEffectNode, aGameManager);
							addEffect (tEffect);
						}
					}
				}
			}
			postParse ();
		} catch (ClassNotFoundException tException) {
			System.err.println (
					"Could not find Class for Effect " + tClassName + " due to Rename and using old Save Game");
		} catch (Exception tException) {
			System.err.println ("Caught Exception with message ");
			System.err.println ("Class name " + tClassName + " Action Name " + aActionName + " Action Number " + aNumber);
			tException.printStackTrace ();
		}
	}

	private void postParse () {
		for (Effect tEffect : effects) {
			tEffect.postParse (actor);
		}
	}

	public void setNumber (int aNumber) {
		number = aNumber;
	}

	public int getNumber () {
		return number;
	}
	
	public boolean actorIsSet () {
		boolean tActorSet;

		tActorSet = false;
		if (actor != ActorI.NO_ACTOR) {
			tActorSet = true;
		}

		return tActorSet;
	}

	public void addEffect (Effect aEffect) {
		int tEffectCount;
		
		tEffectCount = effects.size ();
		aEffect.setOrder (tEffectCount);
		effects.add (aEffect);
	}

	public Effect getEffectNamed (String aEffectName) {
		Effect tFoundEffect;
		String tThisEffectName;

		tFoundEffect = Effect.NO_EFFECT;
		if (aEffectName != null) {
			for (Effect tEffect : effects) {
				tThisEffectName = tEffect.getName ();
				if (aEffectName.equals (tThisEffectName)) {
					tFoundEffect = tEffect;
				}
			}
		}

		return tFoundEffect;
	}

	public String getXMLFormat (ElementName aElementName) {
		XMLDocument tXMLDocument;
		XMLElement tActionElement;
		XMLElement tGameActivityElement;
		String tXMLFormat;
		String tXMLFormatClean;

		tXMLFormat = "";
		tXMLDocument = new XMLDocument ();
		tActionElement = getActionElement (tXMLDocument);
		tGameActivityElement = tXMLDocument.createElement (aElementName);
		tGameActivityElement.appendChild (tActionElement);
		tXMLDocument.appendChild (tGameActivityElement);
		tXMLFormat = tXMLDocument.toString ();
		tXMLFormatClean = tXMLFormat.replaceAll (">[ \t\n\f\r]+<", "><");

		return tXMLFormatClean;
	}

	/* Build XML Element to save the State */
	public XMLElement getActionElement (XMLDocument aXMLDocument) {
		XMLElement tActionElement;
		XMLElement tEffectsElement;
		XMLElement tEffectElement;
		String tActorName;

		if (actor.isACorporation ()) {
			tActorName = ((Corporation) actor).getAbbrev ();
		} else {
			tActorName = actor.getName ();
		}

		tActionElement = aXMLDocument.createElement (EN_ACTION);
		tActionElement.setAttribute (AN_CLASS, this.getClass ().getName ());
		tActionElement.setAttribute (AN_NAME, getName ());
		tActionElement.setAttribute (AN_NUMBER, getNumber ());
		tActionElement.setAttribute (AN_DATE_TIME, dateTime);
		tActionElement.setAttribute (AN_ROUND_TYPE, getRoundType ().toString ());
		tActionElement.setAttribute (AN_ROUND_ID, getRoundID ());
		tActionElement.setAttribute (ActorI.AN_ACTOR_NAME, tActorName);
		tActionElement.setAttribute (AN_CHAIN_PREVIOUS, getChainToPrevious ());
		tEffectsElement = aXMLDocument.createElement (Effect.EN_EFFECTS);
		for (Effect tEffect : effects) {
			tEffectElement = tEffect.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
			tEffectsElement.appendChild (tEffectElement);
		}

		tActionElement.appendChild (tEffectsElement);

		return tActionElement;
	}

	public ActorI getActor () {
		return actor;
	}

	public String getActorName () {
		return actor.getName ();
	}

	public Boolean getChainToPrevious () {
		return chainToPrevious;
	}

	public String getName () {
		return name;
	}

	public void setName (String aName) {
		name = createFullName (aName);
	}

	private String createFullName (String aName) {
		String tFullName;
		
		tFullName =  aName + " Action";
		
		return tFullName;
	}

	public String getRoundID () {
		return roundID;
	}

	public List<Effect> getEffects () {
		return effects;
	}

	public ActorI.ActionStates getRoundType () {
		return roundType;
	}

	public void printActionReport (RoundManager aRoundManager) {
		System.out.println (getActionReport (aRoundManager));
	}

	public String getActionReport (RoundManager aRoundManager) {
		String tActionReport;

		tActionReport = getBriefActionReport ();
		for (Effect tEffect : effects) {
			tActionReport += GUI.NEWLINE + tEffect.getEffectReport (aRoundManager);
		}

		return tActionReport;
	}

	public String getSimpleActionReport () {
		String tReport;

		tReport = getBriefActionReport ();
		tReport = "Brief [" + tReport + "]";

		return tReport;
	}

	public int getEffectCount () {
		return effects.size ();
	}

	public Effect getEffect (int aEffectIndex) {
		return effects.get (aEffectIndex);
	}

	public String getBriefActionReport () {
		String tReport;
		
		tReport =  number + ". " + roundType + " " + roundID + ": " + actor.getAbbrev () + " performed " + name
				+ " Chain to Previous [" + chainToPrevious + "]";
		
		return tReport;
	}

	public void printBriefActionReport () {
		System.out.println (getBriefActionReport ());
	}

	public void printUndoCompletion (boolean aActionUndone) {
		if (!aActionUndone) {
			System.err.println ("***Not all Effects Undone properly***");
		}
	}

	public void setActor (ActorI aActor) {
		actor = aActor;
	}

	public void setChainToPrevious (Boolean aChainToPrevious) {
		chainToPrevious = aChainToPrevious;
	}

	public void setRoundID (String aRoundID) {
		roundID = aRoundID;
	}

	public void setRoundType (ActorI.ActionStates aRoundType) {
		roundType = aRoundType;
	}

	public boolean undoAction (RoundManager aRoundManager) {
		boolean tActionUndone;
		boolean tEffectUndone;
		String tErrorReport;
		String tReport;
		String tUndoFailureReason;
		int tErrorCount;
		int tEffectsUndoneCount;

		tActionUndone = true;
		tErrorCount = 0;
		tEffectsUndoneCount = 0;
		for (Effect tEffect : effects) {
			tEffectUndone = tEffect.undoEffect (aRoundManager);
			if (tEffectUndone) {
				tEffectsUndoneCount++;
			} else {
				tErrorReport = "Failed trying to undo " + name + ". Effect: " + tEffect.getName () + GUI.NEWLINE;
				tUndoFailureReason = tEffect.getUndoFailureReason ();
				aRoundManager.appendErrorReport (tErrorReport);
				aRoundManager.appendErrorReport (tUndoFailureReason);

				tErrorCount++;
			}
			tActionUndone &= tEffectUndone;
		}

		aRoundManager.updateAllCorporationsBox ();
		if (tActionUndone) {
			if (tEffectsUndoneCount == 1) {
				tReport = "\nThere was 1 Effect that was successfully Undone\n";
			} else {
				tReport = "\nThere were " + tEffectsUndoneCount + " Effects that were successfully Undone\n";
			}
			aRoundManager.appendReport (tReport);
		} else {
			if (tErrorCount == 1) {
				tReport = "\nThere was 1 Effect that Failed the Undo Effect Step\n";
			} else {
				tReport = "\nThere were " + tErrorCount + " Effects that Failed the Undo Effect Steps\n";
			}
			aRoundManager.appendReport (tReport);
		}

		return tActionUndone;
	}

	public boolean wasLastActionStartAuction () {
		return false;
	}

	public boolean applyAction (RoundManager aRoundManager) {
		boolean tActionApplied;
		boolean tEffectApplied;
		String tErrorReport;
		String tApplyFailureReason;
		GameManager tGameManager;

		tActionApplied = true;
		tGameManager = aRoundManager.getGameManager ();
		tGameManager.activateAllBeans (false);
		tGameManager.showFormationPhaseFrame ();
		for (Effect tEffect : effects) {
			tEffectApplied = tEffect.applyEffect (aRoundManager);
			tActionApplied &= tEffectApplied;
			if (! tEffectApplied) {
				tErrorReport = "Failed to Apply a |" + name + "|, Effect " + tEffect.getName ();
				System.err.println (tErrorReport);
				tApplyFailureReason = tEffect.getApplyFailureReason ();
				aRoundManager.appendErrorReport (tErrorReport);
				aRoundManager.appendErrorReport (tApplyFailureReason);
			}
		}
		tGameManager.activateAllBeans (true);
		tGameManager.sendAllBeanMessages ();
		aRoundManager.updateAllCorporationsBox ();
		if (! tActionApplied) {
			System.err.println ("Applied All Effects " + tActionApplied);
		}

		return tActionApplied;
	}

	public int getEffectDebit (String aActorName) {
		int tDebit;
		CashTransferEffect tCashTransferEffect;

		tDebit = 0;
		for (Effect tEffect : effects) {
			if (tDebit == 0) {
				if (tEffect instanceof CashTransferEffect) {
					tCashTransferEffect = (CashTransferEffect) tEffect;
					tDebit = tCashTransferEffect.getEffectDebit (aActorName);
				}
			}
		}

		return tDebit;
	}

	public int getEffectCredit (String aActorName) {
		int tCredit;
		CashTransferEffect tCashTransferEffect;

		tCredit = 0;
		for (Effect tEffect : effects) {
			if (tCredit == 0) {
				if (tEffect instanceof CashTransferEffect) {
					tCashTransferEffect = (CashTransferEffect) tEffect;
					tCredit = tCashTransferEffect.getEffectCredit (aActorName);
				}
			}
		}

		return tCredit;
	}

	public boolean effectsThisActor (String aActorName) {
		boolean tEffectsThisActor;
		String tFoundActorName;

		tFoundActorName = actor.getName ();
		tEffectsThisActor = false;
		if (aActorName.equals (tFoundActorName)) {
			tEffectsThisActor = true;
		} else {
			for (Effect tEffect : effects) {
				if (aActorName.equals (tEffect.getActorName ())) {
					tEffectsThisActor = true;
				} else if (aActorName.equals (tEffect.getToActorName ())) {
					tEffectsThisActor = true;
				}
			}
		}

		return tEffectsThisActor;
	}

	public boolean effectsForActorAreCash (String aActorName) {
		boolean tEffectsThisActorAreCash;
		String tActorName;
		String tToActorName;

		tEffectsThisActorAreCash = false;
		for (Effect tEffect : effects) {
			tActorName = tEffect.getActorName ();
			tToActorName = tEffect.getToActorName ();
			if ((aActorName.equals (tActorName)) || 
				(aActorName.equals (tToActorName))) {
				if ((tEffect instanceof CashTransferEffect) || 
					(tEffect instanceof RefundEscrowEffect) ||
					(tEffect instanceof PayCashDividendEffect)) {
					tEffectsThisActorAreCash = true;
				}
			}
		}

		return tEffectsThisActorAreCash;
	}

	public boolean hasRefundEscrowEffect (String aActorName) {
		boolean tHasRefundEscrowEffect;

		tHasRefundEscrowEffect = false;
		for (Effect tEffect : effects) {
			if (tEffect instanceof RefundEscrowEffect) {
				tHasRefundEscrowEffect = true;
			}
		}

		return tHasRefundEscrowEffect;
	}

	public String getSimpleActionReport (String aActorName) {
		return getSimpleActionReport ();
	}

	public String getAuctionWinner () {
		String aAuctionWinner;

		aAuctionWinner = ActorI.NO_NAME;
		
		return aAuctionWinner;
	}

	public boolean allNullEffects () {
		boolean tAllNullEffects;

		tAllNullEffects = false;
		
		return tAllNullEffects;
	}
	
	public void reverseEffects () {
		Collections.sort (effects, new EffectComparator ());
	}
	
	public class EffectComparator implements java.util.Comparator<Effect> {
	    @Override
		public int compare (Effect aEffectA, Effect aEffectB) {
			return aEffectB.getOrder () - aEffectA.getOrder ();
		}
	}
}
