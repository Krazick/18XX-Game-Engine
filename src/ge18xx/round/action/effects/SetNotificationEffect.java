package ge18xx.round.action.effects;

import ge18xx.company.formation.FormationPhase;
import ge18xx.company.formation.TriggerClass;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.AttributeName;
import geUtilities.GUI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

public class SetNotificationEffect extends Effect {
	public final static String NAME = "Set Notification";
	final static AttributeName AN_NOTIFICATION_TEXT = new AttributeName ("notificationText");
	String notificationText;

	public SetNotificationEffect () {
		super ();
		setName (NAME);
	}

	public SetNotificationEffect (ActorI aActor, String aNotificationText) {
		super (NAME, aActor);
		setNotificationText (aNotificationText);
	}

	public SetNotificationEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);

		String tNotificationText;

		tNotificationText = aEffectNode.getThisAttribute (AN_NOTIFICATION_TEXT);
		setNotificationText (tNotificationText);
	}

	public String getNotificadtionText () {
		return notificationText;
	}

	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, aActorAN);
		tEffectElement.setAttribute (AN_NOTIFICATION_TEXT, notificationText);

		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + "Set Notification Text to [" + notificationText +  "].");
	}

	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}

	public void setNotificationText (String aNotificationText) {
		notificationText = aNotificationText;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;
		GameManager tGameManager;

		tEffectApplied = false;
		
		tGameManager = aRoundManager.getGameManager ();
		setNotificationText (tGameManager, notificationText);
		// set the Notification Text properly
		
		tEffectApplied = true;

		return tEffectApplied;
	}

	public void setNotificationText (GameManager aGameManager, String aNotificationText) {
		TriggerClass tTriggerClass;
		FormationPhase tFormationPbase;

		tTriggerClass = aGameManager.getTriggerClass ();
		if (tTriggerClass instanceof FormationPhase) {
			tFormationPbase = (FormationPhase) tTriggerClass;
			tFormationPbase.setNotificationText (aNotificationText);
			tFormationPbase.refreshPanel ();
		}
	}
	
	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		GameManager tGameManager;

		tEffectUndone = false;

		tGameManager = aRoundManager.getGameManager ();
		setNotificationText (tGameManager, GUI.EMPTY_STRING);
		// clear the Notification Text
		
		tEffectUndone = true;

		return tEffectUndone;
	}
}
