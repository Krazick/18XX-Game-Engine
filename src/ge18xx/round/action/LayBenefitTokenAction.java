package ge18xx.round.action;

import ge18xx.company.benefit.MapBenefit;
import ge18xx.game.GameManager;
import ge18xx.map.MapCell;
import ge18xx.round.action.effects.LayBenefitTokenEffect;
import ge18xx.utilities.XMLNode;

public class LayBenefitTokenAction extends ChangeMapAction {
	public final static String NAME = "Lay Benefit Token";

	public LayBenefitTokenAction () {
		this (NAME);
	}

	public LayBenefitTokenAction (String aName) {
		super (aName);
	}

	public LayBenefitTokenAction (ActorI.ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	public LayBenefitTokenAction (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	public void addLayBenefitTokenEffect (ActorI aActor, MapCell aMapCell, String aTokenType, 
											MapBenefit aMapBenefit, int aTokenBonus) {
		LayBenefitTokenEffect tLayBenefitTokenEffect;

		tLayBenefitTokenEffect = new LayBenefitTokenEffect (aActor, aMapCell, aTokenType, aMapBenefit, aTokenBonus);
		addEffect (tLayBenefitTokenEffect);
	}

}
