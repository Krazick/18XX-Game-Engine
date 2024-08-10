package ge18xx.round.action.effects;

import ge18xx.company.Certificate;
import ge18xx.game.GameManager;
import ge18xx.round.RoundManager;
import ge18xx.round.action.ActorI;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;
import geUtilities.xml.XMLNode;

public class DeleteCertificateEffect extends CreateNewCertificateEffect {
	public static final String NAME = "Delete Certificate";

	public DeleteCertificateEffect () {
		super ();
		setName (NAME);
	}

	public DeleteCertificateEffect (ActorI aFromActor, Certificate aCertificate, ActorI aToActor) {
		super (aFromActor, aCertificate, aToActor);
		setName (NAME);
	}

	public DeleteCertificateEffect (ActorI aFromActor, String aFromNickName, Certificate aCertificate, 
				ActorI aToActor, String aToNickName) {
		super (aFromActor, aFromNickName, aCertificate, aToActor, aToNickName);
		setName (NAME);
	}

	public DeleteCertificateEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
	}
	
	@Override
	public XMLElement getEffectElement (XMLDocument aXMLDocument, AttributeName aActorAN) {
		XMLElement tEffectElement;

		tEffectElement = super.getEffectElement (aXMLDocument, ActorI.AN_FROM_ACTOR_NAME);

		return tEffectElement;
	}
	@Override
	public String getEffectReport (RoundManager aRoundManager) {
		String tEffectReport;
		String tFromActorName;
		
		tEffectReport = "";
		if (certificate == Certificate.NO_CERTIFICATE) {
			tEffectReport = "No Certificate to Delete";
		} else {
			tEffectReport += REPORT_PREFIX + name + " of ";
			tEffectReport += certificate.getPercentage () + "% of " + certificate.getCompanyAbbrev ();
			if (certificate.isPresidentShare ()) {
				tEffectReport += " (President Share)";
			}
			tFromActorName = getFromDisplayName ();
			tEffectReport += " from " + tFromActorName + ".";
		}
		
		return tEffectReport;
	}

	@Override
	public boolean applyEffect (RoundManager aRoundManager) {
		boolean tEffectApplied;

		tEffectApplied = super.undoEffect (aRoundManager);
		if (! tEffectApplied) {
			setApplyFailureReason (getUndoFailureReason ());
			setUndoFailureReason (GUI.EMPTY_STRING);
		}
		
		return tEffectApplied;
	}

	@Override
	public boolean undoEffect (RoundManager aRoundManager) {
		boolean tEffectUndone;
		
		tEffectUndone = super.applyEffect (aRoundManager);
		if (! tEffectUndone) {
			setUndoFailureReason (getApplyFailureReason ());
			setApplyFailureReason (GUI.EMPTY_STRING);
		}

		return tEffectUndone;
	}

}
