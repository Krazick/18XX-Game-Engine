package ge18xx.round.action.effects;

import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class SetHasLaidTileEffect extends ChangeBooleanFlagEffect {
	public final static String NAME = "Set Has Laid Tile";
	final static AttributeName AN_HAS_LAID_TILE = new AttributeName ("hasLaidTile");

	public SetHasLaidTileEffect () {
		super ();
		setName (NAME);
	}

	public SetHasLaidTileEffect (String aName) {
		super (aName);
	}

	public SetHasLaidTileEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public SetHasLaidTileEffect (ActorI aActor, boolean aHasLaidTile) {
		super (NAME, aActor, aHasLaidTile);
	}

	public SetHasLaidTileEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN, AN_HAS_LAID_TILE);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + ".");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		TrainCompany tTrainCompany;

		tEffectApplied = false;
		if (actor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) actor;
			tTrainCompany.setHasLaidTile (getBooleanFlag ());
			tEffectApplied = true;
		} else {
			setApplyFailureReason ("The provided Actor " + actor.getName () + " is not a Train Company");
		}

		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		TrainCompany tTrainCompany;

		tEffectUndone = false;
		if (actor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) actor;
			tTrainCompany.setHasLaidTile (! getBooleanFlag ());
		} else {
			setUndoFailureReason ("The provided Actor " + actor.getName () + " is not a Train Company");
		}
		tEffectUndone = true;

		return tEffectUndone;
	}
}
