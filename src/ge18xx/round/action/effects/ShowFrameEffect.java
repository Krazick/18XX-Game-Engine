package ge18xx.round.action.effects;

import javax.swing.JFrame;

import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class ShowFrameEffect extends Effect {
	public final static String NAME = "Show Frame";
	final static AttributeName AN_JFRAME_TITLE = new AttributeName ("jFrameTitle");
	public final static JFrame NO_JFRAME = null;
	JFrame jFrame;
	
	public ShowFrameEffect () {
		this (NAME, ActorI.NO_ACTOR);
	}
	
	public ShowFrameEffect (String aName, ActorI aActor) {
		this (aName, aActor, NO_JFRAME);
	}

	public ShowFrameEffect (ActorI aActor, JFrame aJFrame) {
		this (NAME, aActor, aJFrame);
	}
	
	public ShowFrameEffect (String aName, ActorI aActor, JFrame aJFrame) {
		super (aName, aActor);
		setJFrame (aJFrame);
	}
	
	public ShowFrameEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);

		String tJFrameTitle = aEffectNode.getThisAttribute (AN_JFRAME_TITLE);
		JFrame tJFrame = aGameManager.getJFrameName (tJFrameTitle);
		setJFrame (tJFrame);
	}

	private void setJFrame (JFrame aJFrame) {
		jFrame = aJFrame;
	}
	
	public JFrame getJFrame () {
		return jFrame;
	}
	
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied = false;
		
		jFrame.setVisible (true);
		tEffectApplied = true;
		
		return tEffectApplied;
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;
		
		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);
		tEffectElement.setAttribute (AN_JFRAME_TITLE, jFrame.getTitle ());
		
		return tEffectElement;
	}

	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		return (REPORT_PREFIX + name + " for " + actor.getName () + " Show " + jFrame.getTitle () + ".");
	}
	
	@Override
	public void printEffectReport (RoundManager aRoundManager) {
		System.out.println (getEffectReport (aRoundManager));
	}
}
