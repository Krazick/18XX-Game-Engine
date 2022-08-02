package ge18xx.company.benefit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.SetWaitStateAction;
import ge18xx.utilities.XMLNode;

public class QueryExchangeBenefit extends ExchangeBenefit {
	public final static String NAME = "QUERY EXCHANGE";
	SetWaitStateAction setWaitStateAction;

	public QueryExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
	}
	
	@Override
	public void configure (PrivateCompany aPrivateCompany, JPanel aButtonRow) {
		super.configure (aPrivateCompany, aButtonRow);
	}

	public void handleBenefit (JFrame aRoundFrame) {
		PortfolioHolderI tPortfolioHolder;
		
		if (! used ()) {
			tPortfolioHolder = privateCompany.getPresident ();
			if (tPortfolioHolder.isAPlayer ()) {
				if (hasShareInBank ()) {
					if (! playerAtShareLimit ()) {
						handleShowQueryDialog (aRoundFrame);
					}
				}
			}
		}
	}
	
	private void handleShowQueryDialog (JFrame aRoundFrame) {
		GameManager tGameManager;
		Player tPlayer;
		String tPlayerName;
		boolean tShowQueryDialog;
//		SetWaitStateAction tSetWaitStateAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		
		tGameManager = privateCompany.getGameManager ();
		tShowQueryDialog = false;
		if (tGameManager.isNetworkGame ()) {
			tPlayer = (Player) privateCompany.getPresident ();
			tPlayerName = tPlayer.getName ();
			if (tGameManager.isNetworkAndIsThisClient (tPlayerName)) {
				tShowQueryDialog = true;
				if (tGameManager.isOperatingRound ()) {
					tRoundType = ActorI.ActionStates.OperatingRound;
					tRoundID = tGameManager.getOperatingRoundID ();
				} if (tGameManager.isStockRound ()) {
					tRoundType = ActorI.ActionStates.StockRound;
					tRoundID = tGameManager.getStockRoundID ();
				} else {
					tRoundType = ActorI.ActionStates.AuctionRound;
					tRoundID = ">>NONE<<";
				}
				setWaitStateAction = new SetWaitStateAction (tRoundType, tRoundID, tPlayer);
				tGameManager.addAction (setWaitStateAction);
			}
		} else {
			tShowQueryDialog = true;
		}
		if (tShowQueryDialog) {
			showQueryDialog (aRoundFrame);
		}
	}
	
	
	private void showQueryDialog (JFrame aParentFrame) {
		GameManager tGameManager;
		Certificate tShareCertificate;
		String tQueryText;
		String tOwnerName;
		int tAnswer;
		SetWaitStateAction tResetWaitStateAction;
		
		tShareCertificate = getShareCertificate ();
		tOwnerName = privateCompany.getPresidentName ();
		tQueryText = tOwnerName + ", do you want to Exchange " + privateCompany.getAbbrev () + " for " + 
				certificatePercentage + "% of " + tShareCertificate.getCompanyAbbrev () + "?";

		tAnswer = JOptionPane.showConfirmDialog (aParentFrame, 
				tQueryText, "Exchange Private Share Benefit", 
		        JOptionPane.YES_NO_OPTION);

		if (tAnswer == JOptionPane.YES_OPTION) {
		  	handleExchangeCertificate ();
		}
		tGameManager = privateCompany.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tResetWaitStateAction = new SetWaitStateAction (setWaitStateAction);
			tResetWaitStateAction.resetPlayerStatesAfterWait (setWaitStateAction);
			tGameManager.addAction (setWaitStateAction);
		}
	}
}
