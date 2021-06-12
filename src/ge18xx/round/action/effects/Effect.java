package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.PrivateCompany;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Effect {
	public final static String NO_NAME = ">>NO EFFECT NAME<<";
	public final static ActorI NO_ACTOR = null;
	public static final ElementName EN_EFFECTS = new ElementName ("Effects");
	public static final ElementName EN_EFFECT = new ElementName ("Effect");
	public final static String REPORT_PREFIX = "--" + EN_EFFECT + ": ";
	public final static AttributeName AN_CLASS = new AttributeName ("class");
	public final static AttributeName AN_IS_A_PRIVATE = new AttributeName ("isAPrivate");
	static final AttributeName AN_NAME = new AttributeName ("name");
	final static AttributeName AN_BENEFIT_USED = new AttributeName ("benefitUsed");
	final static AttributeName AN_BENEFIT_NAME = new AttributeName ("benefitName");
	final static AttributeName AN_BENEFIT_PRIVATE_ABBREV = new AttributeName ("benefitPrivateAbbrev");
	final static Benefit NO_BENEFIT_IN_USE = null;

	String name;
	ActorI actor;
	boolean isAPrivate;
	boolean benefitUsed;
	String benefitName;
	String benefitPrivateAbbrev;

	Effect () {
		this (NO_NAME);
	}

	Effect (String aName) {
		this (aName, NO_ACTOR);
	}
	
	Effect (String aName, ActorI aActor) {
		this (aName, aActor, NO_BENEFIT_IN_USE);
	}
	
	Effect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		PrivateCompany tPrivateCompany;

		setName (aName);
		setActor (aActor);
		if (aBenefitInUse == NO_BENEFIT_IN_USE) {
			setNoBenefitInUse ();
		} else  if (aBenefitInUse.realBenefit ()) {
			setBenefitName (aBenefitInUse.getName ());
			tPrivateCompany = aBenefitInUse.getPrivateCompany ();
			setBenefitPrivateAbbrev (tPrivateCompany.getAbbrev ());
			setBenefitUsed (aBenefitInUse.used ());
		} else {
			setNoBenefitInUse ();
		}
	}

	private void setBenefitUsed (boolean aBenefitUsed) {
		benefitUsed = aBenefitUsed;
	}
	
	private void setBenefitPrivateAbbrev (String aAbbrev) {
		benefitPrivateAbbrev = aAbbrev;
	}

	private void setBenefitName (String aBenefitName) {
		benefitName = aBenefitName;
	}

	private String getBenefitName () {
		return benefitName;
	}
	
	private String getBenefitPrivateAbbrev () {
		return benefitPrivateAbbrev;
	}
	
	private boolean getBenefitUsed () {
		return benefitUsed;
	}
	
	protected String getBenefitEffectReport () {
		String tBenefitEffectReport = "";
		
		if (benefitName.length () > 0) {
			tBenefitEffectReport = " Used " + benefitName + " Benefit from " + benefitPrivateAbbrev + ".";
		}
		
		return tBenefitEffectReport;
	}
	
	private void setNoBenefitInUse () {
		setBenefitName ("");
		setBenefitPrivateAbbrev ("");
		setBenefitUsed (false);

	}

	Effect (XMLNode aEffectNode, GameManager aGameManager) {
		String tEffectName, tActorName;
		ActorI tActor;
		boolean tIsAPrivate;
		String tBenefitPrivateAbbrev, tBenefitName;
		boolean tBenefitUsed;
		
		tEffectName = aEffectNode.getThisAttribute (AN_NAME);
		tActorName = aEffectNode.getThisAttribute (ActorI.AN_ACTOR_NAME);
		tIsAPrivate = aEffectNode.getThisBooleanAttribute (AN_IS_A_PRIVATE);
		if (tActorName == null) {
			tActorName = aEffectNode.getThisAttribute (ActorI.AN_FROM_ACTOR_NAME);
		}
		
		tActor = aGameManager.getActor (tActorName, tIsAPrivate);
		if (tActor == null) {
			System.err.println ("No Actor Found -- Looking for [" + tActorName + "]");
		}
		setName (tEffectName);
		setActor (tActor);

		tBenefitPrivateAbbrev = aEffectNode.getThisAttribute (AN_BENEFIT_PRIVATE_ABBREV);
		tBenefitName = aEffectNode.getThisAttribute (AN_BENEFIT_NAME);
		tBenefitUsed = aEffectNode.getThisBooleanAttribute (AN_BENEFIT_USED);
		if (tBenefitName == null) {
			setNoBenefitInUse ();
		} else {
			setBenefitPrivateAbbrev (tBenefitPrivateAbbrev);
			setBenefitName (tBenefitName);
			setBenefitUsed (tBenefitUsed);
		}
	}
	
	public boolean actorIsSet () {
		boolean tActorSet;
		
		tActorSet = false;
		if (actor != NO_ACTOR) {
			tActorSet = true;
		}
		
		return tActorSet;
	}
	
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		String tActorName;
		
		tEffectElement = aXMLDocument.createElement (EN_EFFECT);
		tEffectElement.setAttribute (AN_CLASS, this.getClass ().getName ());
		if (actor.isACorporation ()) {
			tActorName = ((Corporation) actor).getAbbrev ();
		} else {
			tActorName = actor.getName ();
		}
		tEffectElement.setAttribute (AN_NAME, getName ());
		tEffectElement.setAttribute (aActorAN, tActorName);
		tEffectElement.setAttribute (AN_IS_A_PRIVATE, isAPrivate);
		
		if (benefitName != null) {
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
		return (REPORT_PREFIX + name + " for " + getActorName () + ".");
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

	public String getToActorName () {
		return ActorI.NO_NAME;
	}

	public String getActorName () {
		return actor.getName ();
	}
	
	public boolean undoEffect (RoundManager aRoundManager) {
		return false;
	}
	
	public boolean wasNewStateAuction () {
		return false;
	}

	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		tEffectApplied = false;
		
		return tEffectApplied;
	}
	
	public boolean nullEffect () {
		return false;
	}
}
