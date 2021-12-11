package ge18xx.round.action.effects;

import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class BenefitUsedEffect extends Effect {
	public final static String NAME = "Benefit Used";
	final static AttributeName AN_BENEFIT_NAME = new AttributeName ("name");
	Benefit benefitInUse;
	String benefitName;
	
	public BenefitUsedEffect () {
		super ();
		setName (NAME);
	}

	public BenefitUsedEffect (String aName) {
		super (aName);
	}

	public BenefitUsedEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public BenefitUsedEffect (ActorI aActor, Benefit aBenefitInUse) {
		super (NAME, aActor);
		setBenefit (aBenefitInUse);
	}
	
	public BenefitUsedEffect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		super (aName, aActor);

		setBenefit (aBenefitInUse);
	}

	public BenefitUsedEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
		String tBenefitName;
		Benefit tBenefit;
		
		tBenefitName = aEffectNode.getThisAttribute (AN_BENEFIT_NAME);
		tBenefit = aGameManager.findBenefit (tBenefitName);
		setBenefit (tBenefit);
	}
	
	protected void setBenefit (Benefit aBenefitInUse) {
		benefitInUse = aBenefitInUse;
		benefitName = aBenefitInUse.getName ();
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_BENEFIT_NAME, benefitName);
		
		return tEffectElement;
	}
	
	public String getBenefitName () {
		return benefitInUse.getName ();
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + benefitInUse.getPrivateCompany ().getAbbrev () + " " + 
					getBenefitName () + " made by "+  actor.getName () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
	
	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		
		benefitInUse.setUsed (true);
		tEffectApplied = true;
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = false;
		benefitInUse.undoUse ();
		tEffectUndone = true;
		
		return tEffectUndone;
	}
}
