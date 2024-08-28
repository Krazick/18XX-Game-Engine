package ge18xx.round.action.effects;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.xml.XMLFrame;
import geUtilities.xml.XMLNode;

public class HideFrameEffect extends ShowFrameEffect {
	public static final String NAME = "Hide Frame";

	public HideFrameEffect () {
		this (NAME, ActorI.NO_ACTOR);
	}

	public HideFrameEffect (String aName, ActorI aActor) {
		this (aName, aActor, XMLFrame.NO_XML_FRAME);
	}

	public HideFrameEffect (ActorI aActor, XMLFrame aXMLFrame) {
		this (NAME, aActor, aXMLFrame);
	}

	public HideFrameEffect (String aName, ActorI aActor, XMLFrame aXMLFrame) {
		super (aName, aActor, aXMLFrame);
	}

	public HideFrameEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		
		setName (NAME);
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.undoEffect (aRoundManager);
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.applyEffect (aRoundManager);

		return tEffectApplied;
	}
}
