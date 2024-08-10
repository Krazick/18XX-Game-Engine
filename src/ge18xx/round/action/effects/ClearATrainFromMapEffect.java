package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class ClearATrainFromMapEffect extends Effect {
	public final static String NAME = "Clear A Train From Map";
	public final static AttributeName AN_CLEAR_A_TRAIN = new AttributeName ("clearATrain");
	public final static AttributeName AN_TRAIN_INDEX = new AttributeName ("trainIndex");
	int trainIndex;

	public ClearATrainFromMapEffect () {
		super ();
		setName (NAME);
	}

	public ClearATrainFromMapEffect (ActorI aActor, int aTrainIndex) {
		super (NAME, aActor);
		setTrainIndex (aTrainIndex);
	}

	public ClearATrainFromMapEffect (String aName, ActorI aActor, int aTrainIndex) {
		super (aName, aActor);
		setTrainIndex (aTrainIndex);
	}

	public ClearATrainFromMapEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tTrainIndex;

		tTrainIndex = aEffectNode.getThisIntAttribute (AN_TRAIN_INDEX);
		setTrainIndex (tTrainIndex);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_ACTOR_NAME);
		tEffectElement.setAttribute (AN_TRAIN_INDEX, getTrainIndex ());

		return tEffectElement;
	}

	public void setTrainIndex (int aTrainIndex) {
		trainIndex = aTrainIndex;
	}

	public int getTrainIndex () {
		return trainIndex;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Corporation tCorporation;

		tCorporation = (Corporation) getActor ();

		return (REPORT_PREFIX + name + " for " + tCorporation.getAbbrev () + " Train Index " + (trainIndex + 1) + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tTrainCompany;

		if (actor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) actor;
			tTrainCompany.clearATrainFromMap (trainIndex, false);
			tEffectApplied = true;
		} else {
			tEffectApplied = false;
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
