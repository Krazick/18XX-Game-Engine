package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.ElementName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public abstract class Effect {
	public static final ElementName EN_EFFECTS = new ElementName ("Effects");
	public static final ElementName EN_EFFECT = new ElementName ("Effect");
	public static final AttributeName AN_CLASS = new AttributeName ("class");
	public static final AttributeName AN_IS_A_PRIVATE = new AttributeName ("isAPrivate");
	public static final AttributeName AN_ORDER = new AttributeName ("order");
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_FROM_NAME = new AttributeName ("fromName");
	public static final AttributeName AN_BENEFIT_USED = new AttributeName ("benefitUsed");
	public static final AttributeName AN_BENEFIT_NAME = new AttributeName ("benefitName");
	public static final AttributeName AN_BENEFIT_PRIVATE_ABBREV = new AttributeName ("benefitPrivateAbbrev");
	public static final Benefit NO_BENEFIT_IN_USE = null;
	public static final ActorI NO_ACTOR = null;
	public static final Effect NO_EFFECT = null;
	public static final String NO_NAME = ">>NO EFFECT NAME<<";
	public static final String REPORT_PREFIX = "--" + EN_EFFECT + ": ";

	ActorI actor;
	int order;
	String name;
	String fromName;
	String nickName;
	String benefitName;
	String benefitPrivateAbbrev;
	String applyFailureReason;
	String undoFailureReason;
	boolean isAPrivate;
	boolean benefitUsed;

	Effect () {
		this (NO_NAME);
	}

	Effect (String aName) {
		this (aName, NO_ACTOR);
	}

	Effect (String aName, ActorI aActor) {
		this (aName, aActor, NO_BENEFIT_IN_USE);
		setNames (aActor.getName ());
//		setFromName (aActor.getName ());
//		setNickName (aActor.getName ());
	}
	
	Effect (String aName, ActorI aActor, String aFromName) {
		this (aName, aActor, NO_BENEFIT_IN_USE);
		setNames (aFromName);
//		setFromName (aFromName);
//		setNickName (aFromName);
	}

	Effect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		PrivateCompany tPrivateCompany;

		setName (aName);
		setActor (aActor);
		if (aBenefitInUse == NO_BENEFIT_IN_USE) {
			setNoBenefitInUse ();
		} else if (aBenefitInUse.isRealBenefit ()) {
			setBenefitName (aBenefitInUse.getName ());
			tPrivateCompany = aBenefitInUse.getPrivateCompany ();
			setBenefitPrivateAbbrev (tPrivateCompany.getAbbrev ());
			setBenefitUsed (aBenefitInUse.used ());
		} else {
			setNoBenefitInUse ();
		}
		setOrder (1);
	}
	
	Effect (XMLNode aEffectNode, GameManager aGameManager) {
		String tEffectName;
		String tActorName;
		ActorI tActor;
		String tBenefitPrivateAbbrev;
		String tBenefitName;
		String tFromName;
		boolean tBenefitUsed;
		boolean tIsAPrivate;
		int tOrder;

		tOrder = aEffectNode.getThisIntAttribute (AN_ORDER, 0);
		tEffectName = aEffectNode.getThisAttribute (AN_NAME);
		tActorName = aEffectNode.getThisAttribute (ActorI.AN_ACTOR_NAME);
		tFromName = aEffectNode.getThisAttribute (AN_FROM_NAME, tActorName);
		setNames (tFromName);
		
		if (tActorName == ActorI.NO_NAME) {
			tActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		}
		
		tIsAPrivate = aEffectNode.getThisBooleanAttribute (AN_IS_A_PRIVATE);

		tActor = aGameManager.getActor (tActorName, tIsAPrivate);
		setName (tEffectName);
		setActor (tActor);
		setOrder (tOrder);

		tBenefitPrivateAbbrev = aEffectNode.getThisAttribute (AN_BENEFIT_PRIVATE_ABBREV);
		tBenefitName = aEffectNode.getThisAttribute (AN_BENEFIT_NAME);
		tBenefitUsed = aEffectNode.getThisBooleanAttribute (AN_BENEFIT_USED);
		if (tBenefitName == Benefit.NO_BENEFIT_NAME) {
			setNoBenefitInUse ();
		} else {
			setBenefitPrivateAbbrev (tBenefitPrivateAbbrev);
			setBenefitName (tBenefitName);
			setBenefitUsed (tBenefitUsed);
		}
	}

	private void setNames (String aFromName) {
		setFromName (aFromName);
		setNickName (aFromName);
	}
	
	/**
	 * This method should be called after the Action has been parsed by the Constructor given
	 * an XMLNode of the Data. This is to allow special case Effects to correct for not finding an object
	 * like a Train or a Certificate from the Action's Actor. This Method should be overriden by the
	 * Effect that is impacted.
	 *
	 * @param aActor The actor that should be searched for the object if it was not properly setup
	 */

	public void postParse (ActorI aActor) {

	}
	
	public String getNickName () {
		String tNickName;
		
		tNickName = GUI.NULL_STRING;
		if (nickName == GUI.NULL_STRING) {
			tNickName = GUI.NULL_STRING;
		} else if (nickName == GUI.EMPTY_STRING) {
			tNickName = GUI.NULL_STRING;		
		} else {
			tNickName = nickName;
		}
		
		return tNickName;
	}

	public void setNickName (String aNickName) {
		nickName = aNickName;
	}
	
	public String getFromDisplayName () {
		String tDisplayName;
		
		tDisplayName = getNickName ();
		
		if (tDisplayName == GUI.NULL_STRING) {
			if (actor == ActorI.NO_ACTOR) {
				tDisplayName = "NO-FROM-ACTOR";
			} else {
				tDisplayName = actor.getName ();
			}
		}

		return tDisplayName;
	}

	protected void setFromName (String aFromName) {
		fromName = aFromName;
	}
	
	protected String getFromName () {
		return fromName;
	}
	
	protected void setBenefitUsed (boolean aBenefitUsed) {
		benefitUsed = aBenefitUsed;
	}

	protected void setBenefitPrivateAbbrev (String aAbbrev) {
		benefitPrivateAbbrev = aAbbrev;
	}

	protected void setBenefitName (String aBenefitName) {
		benefitName = aBenefitName;
	}

	protected String getBenefitName () {
		return benefitName;
	}

	protected String getBenefitPrivateAbbrev () {
		return benefitPrivateAbbrev;
	}

	protected boolean getBenefitUsed () {
		return benefitUsed;
	}

	public void setOrder (int aOrder) {
		order = aOrder;
	}
	
	public int getOrder () {
		return order;
	}
	
	protected String getBenefitEffectReport () {
		String tBenefitEffectReport;
		String tUsed;

		tBenefitEffectReport = GUI.EMPTY_STRING;
		if (benefitName.length () > 0) {
			if (getBenefitUsed ()) {
				tUsed = " Used ";
			} else {
				tUsed = " ";
			}
			tBenefitEffectReport = tUsed + benefitName + " Benefit from " + benefitPrivateAbbrev + ".";
		}

		return tBenefitEffectReport;
	}

	private void setNoBenefitInUse () {
		setBenefitName (GUI.EMPTY_STRING);
		setBenefitPrivateAbbrev (GUI.EMPTY_STRING);
		setBenefitUsed (false);
	}

	private boolean benefitValid () {
		boolean tBenefitValid;
		
		if (benefitName == GUI.NULL_STRING) {
			tBenefitValid = false;
		} else if (benefitName.equals (GUI.EMPTY_STRING)) {
			tBenefitValid = false;
		} else {
			tBenefitValid = true;
		}
		
		return tBenefitValid;
	}
	
	protected void setBenefitUsed (RoundManager aRoundManager) {
		Benefit tBenefit;
		
		if (benefitValid ()) {
			tBenefit = getBenefitWithName (aRoundManager);

			tBenefit.setUsed (true);
		}
	}
	
	protected void setBenefitUnUsed (RoundManager aRoundManager) {
		Benefit tBenefit;
		
		if (benefitValid ()) {
			tBenefit = getBenefitWithName (aRoundManager);

			tBenefit.undoUse ();
		}
	}
	
	protected Benefit getBenefitWithName (RoundManager aRoundManager) {
		Benefit tBenefit;
		
		tBenefit = aRoundManager.getBenefitWithName (benefitPrivateAbbrev, benefitName);
		
		return tBenefit;
	}
	

	public boolean actorIsSet () {
		boolean tActorSet;

		if (actor != NO_ACTOR) {
			tActorSet = true;
		} else {
			tActorSet = false;
		}

		return tActorSet;
	}

	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		Class<? extends Effect> tClass;
		String tClassName;

		tEffectElement = aXMLDocument.createElement (EN_EFFECT);
		tClass = getClass ();
		tClassName = tClass.getName ();
		tEffectElement.setAttribute (AN_CLASS, tClassName);
		if (actor.isACorporation ()) {
			tActorName = ((Corporation) actor).getAbbrev ();
		} else {
			tActorName = getActorName ();
		}
		tEffectElement.setAttribute (AN_NAME, getName ());
		tEffectElement.setAttribute (AN_FROM_NAME, getFromName ());
		tEffectElement.setAttribute (aActorAN, tActorName);
		tEffectElement.setAttribute (AN_IS_A_PRIVATE, isAPrivate);
		tEffectElement.setAttribute (AN_ORDER, order);

		if (benefitName != Benefit.NO_BENEFIT_NAME) {
			if (benefitName.length () > 0) {
				tEffectElement.setAttribute (AN_BENEFIT_PRIVATE_ABBREV, getBenefitPrivateAbbrev ());
				tEffectElement.setAttribute (AN_BENEFIT_NAME, getBenefitName ());
				tEffectElement.setAttribute (AN_BENEFIT_USED, getBenefitUsed ());
			}
		}

		return tEffectElement;
	}

	public String getName () {
		return name;
	}

	public ActorI getActor () {
		return actor;
	}

	public String getEffectReport (RoundManager aRoundManager) {
		return (" " + REPORT_PREFIX + name + " for " + getActorName () + ".");
	}

	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setName (String aName) {
		name = aName;
	}

	public void setActor (ActorI aActor) {
		actor = aActor;
		if (aActor != NO_ACTOR) {
			isAPrivate = aActor.isAPrivateCompany ();
		}
	}

	public boolean isActor (String aActorName) {
		boolean tIsActor = false;

		if (actor != NO_ACTOR) {
			if (actor.getName ().equals (aActorName)) {
				tIsActor = true;
			}
		}

		return tIsActor;
	}

	public String getToActorName () {
		return ActorI.NO_NAME;
	}

	public String getFromActorName () {
		String tFromActorName;
		
		if (fromName == ActorI.NO_NAME) {
			tFromActorName = actor.getName ();
		} else {
			tFromActorName = fromName;
		}
		
		return tFromActorName;
	}

	public String getActorName () {
		String tFromActorName;
		
		tFromActorName = actor.getName ();
		
		return tFromActorName;
	}

	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		setApplyFailureReason ("Basic Effect undoEffect method should never be called directly\n" +
								" -- OR Reason not coded into the effect yet");
		tEffectUndone = false;

		return tEffectUndone;
	}

	public boolean wasNewStateAuction () {
		return false;
	}

	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = false;
		setApplyFailureReason ("Basic Effect applyEffect method should never be called directly\n" +
								" -- OR Reason not coded into the effect yet");

		return tEffectApplied;
	}

	protected void setApplyFailureReason (String aFailureReason) {
		applyFailureReason = aFailureReason;
	}

	protected void setUndoFailureReason (String aFailureReason) {
		undoFailureReason = aFailureReason;
	}

	/**
	 * Retrieve the Reason why the ApplyEffect call failed.
	 *
	 * @return The Apply Effect Failure Reason
	 */
	public String getApplyFailureReason () {
		return applyFailureReason;
	}

	/**
	 * Retrieve the Reason why the Undo Effect call failed.
	 *
	 * @return The Undo Effect Failure Reason
	 */
	public String getUndoFailureReason () {
		return undoFailureReason;
	}

	public boolean nullEffect () {
		return false;
	}
}
