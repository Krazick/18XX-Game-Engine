package ge18xx.round.action;

import ge18xx.company.Corporation;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI.ActionStates;
import ge18xx.round.action.effects.ClearATrainFromMapEffect;
import geUtilities.XMLNode;

/**
 * Will clear specific Trains from the map. Each Train to be cleared needs to be added as a individual
 * ClearATrainFromMapEffect
 *
 *
 */
public class ClearATrainFromMapAction extends Action {
	public final static String NAME = "Clear A Train";

	/**
	 * Basic No-Arg Constructor for the class
	 *
	 */
	public ClearATrainFromMapAction () {
		super (NAME);
	}

	/**
	 * Constructor with a Single Text String to override the Default NAME for the Action
	 *
	 * @param aName The Name to set to the Action
	 *
	 */
	public ClearATrainFromMapAction (String aName) {
		super (aName);
	}

	/**
	 * Constructor with the Round Type, the RoundID and the Actor performing the Action
	 *
	 * @param aRoundType identifies the type of Round where the Action is performed
	 * @param aRoundID identifies the Round ID when the Action is performed
	 * @param aActor identifies the Actor who is performing the Action
	 *
	 */
	public ClearATrainFromMapAction (ActionStates aRoundType, String aRoundID, ActorI aActor) {
		super (aRoundType, aRoundID, aActor);
		setName (NAME);
	}

	/**
	 * Constructor that extracts information from the provided XML Node to build the Action.
	 *
	 * @param aActionNode The XML Node that is used to create the Action. This node from either
	 * 						the Save Game File, or from the Network delivery from remote clients
	 * @param aGameManager The GameManager to be passed to the super class (Action) constructor
	 */
	public ClearATrainFromMapAction (XMLNode aActionNode, GameManager aGameManager) {
		super (aActionNode, aGameManager);
		setName (NAME);
	}

	/**
	 * Create a new ClearATrainFromMapEffect, and add to the list of Effects for this Action
	 *
	 * @param aCorporation The Corporation performing the Action
	 * @param aTrainIndex The Index of the Train for this Corporation that is to be applied for the Effect
	 */
	public void addClearATrainFromMapEffect (Corporation aCorporation, int aTrainIndex) {
		ClearATrainFromMapEffect tClearATrainFromMapEffect;

		tClearATrainFromMapEffect = new ClearATrainFromMapEffect (aCorporation, aTrainIndex);
		addEffect (tClearATrainFromMapEffect);
	}

	@Override
	/**
	 * Generate a Simple Action Report, a String that describes what this Action should do. This Overrides
	 * The super Class (Action) method
	 *
	 * @return String with the Action Report
	 */
	public String getSimpleActionReport () {
		String tSimpleActionReport = "";

		tSimpleActionReport = actor.getName () + " Clear A Train Route from the Map.";

		return tSimpleActionReport;
	}

}
