package ge18xx.round.action.effects;

import ge18xx.company.Corporation;
import ge18xx.company.TrainCompany;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.toplevel.MapFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class ClearTrainsFromMapEffect extends Effect {
	public final static String NAME = "Clear Trains From Map";
	public final static AttributeName AN_CLEAR_TRAINS = new AttributeName ("clearTrains");

	public ClearTrainsFromMapEffect () {
		super ();
		setName (NAME);
	}
	
	public ClearTrainsFromMapEffect (ActorI aActor) {
		super (NAME, aActor);
	}

	public ClearTrainsFromMapEffect (ActorI aActor, String aRoundID) {
		super (NAME, aActor);
	}

	public ClearTrainsFromMapEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		Corporation tCorporation;
		
		tCorporation = (Corporation) getActor ();

		return (REPORT_PREFIX + name + " for " + tCorporation.getAbbrev () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		MapFrame tMapFrame;
		TrainCompany tTrainCompany;
		ActorI tActor;
		
		tMapFrame = aRoundManager.getMapFrame ();
		tMapFrame.clearTrainsFromMap ();
		tActor = getActor ();
		if (tActor.isATrainCompany ()) {
			tTrainCompany = (TrainCompany) tActor;
			tTrainCompany.closeTrainRevenueFrame ();
		}
		tEffectApplied = true;
	
		return tEffectApplied;
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		return true;
	}
}
