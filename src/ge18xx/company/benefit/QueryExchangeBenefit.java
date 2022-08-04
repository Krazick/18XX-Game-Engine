package ge18xx.company.benefit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.QueryExchangeBenefitAction;
import ge18xx.round.action.SetWaitStateAction;
import ge18xx.utilities.XMLNode;

public class QueryExchangeBenefit extends ExchangeBenefit {
	public final static String NAME = "QUERY EXCHANGE";
	SetWaitStateAction setWaitStateAction;

	public QueryExchangeBenefit (XMLNode aXMLNode) {
		super (aXMLNode);
		setName (NAME);
		setWaitStateAction = (SetWaitStateAction) Action.NO_ACTION;
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
		SetWaitStateAction tResetWaitStateAction;
		
		tShowQueryDialog = false;
		tGameManager = privateCompany.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tPlayer = (Player) privateCompany.getPresident ();
			tPlayerName = tPlayer.getName ();
			tResetWaitStateAction = tellOthersToWait (tGameManager, tPlayer);
			if (tGameManager.isNetworkAndIsThisClient (tPlayerName)) {
				tShowQueryDialog = true;
			} else {
				tellPlayerToQuery (tGameManager, tPlayer);
			}
		} else {
			tShowQueryDialog = true;
			tResetWaitStateAction = (SetWaitStateAction) Action.NO_ACTION;
		}
		if (tShowQueryDialog) {
			showQueryDialog (aRoundFrame);
		}
		// After handling Query Dialog, then if we have told others to wait, run the reset Wait State Action
		if (tResetWaitStateAction != (SetWaitStateAction) Action.NO_ACTION) {
			tGameManager.addAction (tResetWaitStateAction);
		}
	}

	/**
	 * Test the Player who is President of the Private with a Query Exchange Benefit to ask the question
	 * and if needed to perform the exchange.
	 * 
	 * @param aGameManager The Game Manager, to retrieve info, and add the QueryExchangeBenefitAction to be done
	 * @param aPlayer the Player who needs to answer the question of the Exchange.
	 * 
	 */
	private void tellPlayerToQuery (GameManager aGameManager, Player aPlayer) {
		System.out.println ("Player " + aPlayer.getName () + 
				" must answer Query Exchange Question - Send Action requesting Response.");
		QueryExchangeBenefitAction tQueryExchangeBenefitAction;
		ActorI.ActionStates tRoundType;
		String tRoundID;
		Player tCurrentPlayer;
		
		tRoundType = getRoundType (aGameManager);
		tRoundID = getRoundID (aGameManager);
		tCurrentPlayer = aGameManager.getCurrentPlayer ();
		tQueryExchangeBenefitAction = new QueryExchangeBenefitAction (tRoundType, tRoundID, tCurrentPlayer);
		tQueryExchangeBenefitAction.addQueryExchangeBenefitEffect (tCurrentPlayer, aPlayer, privateCompany, this);
		aGameManager.addAction (tQueryExchangeBenefitAction);
	}
	
	/**
	 * This method will tell all Other Players to move into a Wait State, and return an action that will
	 * reset these same players to the state they had before being told to Wait.
	 * 
	 * @param aGameManager The Game Manager, to retrieve info, and add the setWaitAction to be done
	 * @param aPlayer The Player with the Private that has the Query Exchange Benefit all others should wait
	 * @return the ResetWaitStateAction to reset the players to the state before they were told to wait.
	 * 
	 */
	private SetWaitStateAction tellOthersToWait (GameManager aGameManager, Player aPlayer) {
		ActorI.ActionStates tRoundType;
		String tRoundID;
		SetWaitStateAction tResetWaitStateAction;

		tRoundType = getRoundType (aGameManager);
		tRoundID = getRoundID (aGameManager);
		setWaitStateAction = new SetWaitStateAction (tRoundType, tRoundID, aPlayer);
		aPlayer.setAllWaitStateEffects (setWaitStateAction);
		aGameManager.addAction (setWaitStateAction);
		tResetWaitStateAction = new SetWaitStateAction (setWaitStateAction);
		tResetWaitStateAction.resetPlayerStatesAfterWait (setWaitStateAction);
		
		return tResetWaitStateAction;
	}
	
	private ActorI.ActionStates getRoundType (GameManager aGameManager) {
		ActorI.ActionStates tRoundType;
		
		if (aGameManager.isOperatingRound ()) {
			tRoundType = ActorI.ActionStates.OperatingRound;
		} else if (aGameManager.isStockRound ()) {
			tRoundType = ActorI.ActionStates.StockRound;
		} else {
			tRoundType = ActorI.ActionStates.AuctionRound;
		}

		return tRoundType;
	}
	
	private String getRoundID (GameManager aGameManager) {
		String tRoundID;
		
		if (aGameManager.isOperatingRound ()) {
			tRoundID = aGameManager.getOperatingRoundID ();
		} else if (aGameManager.isStockRound ()) {
			tRoundID = aGameManager.getStockRoundID ();
		} else {
			tRoundID = ">>NONE<<";
		}

		return tRoundID;
	}
	
	private void showQueryDialog (JFrame aParentFrame) {
		Certificate tShareCertificate;
		String tQueryText;
		String tOwnerName;
		int tAnswer;
		
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
	}
}
