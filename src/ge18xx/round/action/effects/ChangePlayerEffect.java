package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PlayerManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;

public class ChangePlayerEffect extends Effect {
	public final static String NAME = "Change Player";
	final static AttributeName AN_PREVIOUS_PLAYER = new AttributeName ("previousPlayer");
	final static AttributeName AN_NEW_PLAYER = new AttributeName ("newPlayer");
	int previousPlayerIndex;
	int newPlayerIndex;

	public ChangePlayerEffect () {
		super ();
		setName (NAME);
		setPreviousPlayer (PlayerManager.NO_PLAYER_INDEX);
		setNewPlayer (PlayerManager.NO_PLAYER_INDEX);
	}

	public ChangePlayerEffect (ActorI aActor, int aPreviousPlayer, int aNewPlayer) {
		super (NAME, aActor);
		setPreviousPlayer (aPreviousPlayer);
		setNewPlayer (aNewPlayer);
	}

	public ChangePlayerEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		int tPreviousPlayerIndex, tNewPlayerIndex;

		tPreviousPlayerIndex = aEffectNode.getThisIntAttribute (AN_PREVIOUS_PLAYER);
		tNewPlayerIndex = aEffectNode.getThisIntAttribute (AN_NEW_PLAYER);
		setPreviousPlayer (tPreviousPlayerIndex);
		setNewPlayer (tNewPlayerIndex);
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_PREVIOUS_PLAYER, previousPlayerIndex);
		tEffectElement.setAttribute (AN_NEW_PLAYER, newPlayerIndex);

		return tEffectElement;
	}

	public int getNewPlayer () {
		return newPlayerIndex;
	}

	public int getPreviousPlayer () {
		return previousPlayerIndex;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		PlayerManager tPlayerManager;
		Player tPreviousPlayer, tNewPlayer;

		tPlayerManager = aRoundManager.getPlayerManager ();
		tPreviousPlayer = tPlayerManager.getPlayer (previousPlayerIndex);
		tNewPlayer = tPlayerManager.getPlayer (newPlayerIndex);

		return (REPORT_PREFIX + name + " from " + tPreviousPlayer.getName () + " (" + previousPlayerIndex + ") to "
				+ tNewPlayer.getName () + " (" + newPlayerIndex + ").");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setNewPlayer (int aNewPlayer) {
		newPlayerIndex = aNewPlayer;
	}

	public void setPreviousPlayer (int aPreviousPlayer) {
		previousPlayerIndex = aPreviousPlayer;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;

		tEffectUndone = false;

		return tEffectUndone;
	}
}
