package ge18xx.company.benefit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ge18xx.company.Certificate;
import ge18xx.company.ExchangePrivateQuery;
import ge18xx.company.PrivateCompany;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.PortfolioHolderI;
import ge18xx.round.action.Action;
import ge18xx.round.action.ActorI;
import ge18xx.round.action.QueryExchangeBenefitAction;
import ge18xx.round.action.SetWaitStateAction;
import ge18xx.round.action.WaitForReponseFrame;
import ge18xx.utilities.XMLNode;

public class QueryExchangeBenefit extends ExchangeBenefit {
	public final static String NAME = "QUERY EXCHANGE";
	SetWaitStateAction setWaitStateAction;
	ExchangePrivateQuery exchangePrivateQuery;

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
		Player tCurrentPlayer;
		String tPlayerName;
		boolean tShowQueryDialog;
		boolean tExchangeApproved;
		SetWaitStateAction tResetWaitStateAction;
		WaitForReponseFrame tWaitForReponseFrame;
		
		tShowQueryDialog = false;
		tExchangeApproved = false;
		tGameManager = privateCompany.getGameManager ();
		if (tGameManager.isNetworkGame ()) {
			tPlayer = (Player) privateCompany.getPresident ();
			tPlayerName = tPlayer.getName ();
			tCurrentPlayer = tGameManager.getCurrentPlayer ();
			tResetWaitStateAction = tellOthersToWait (tGameManager, tPlayer);
			if (tGameManager.isNetworkAndIsThisClient (tPlayerName)) {
				tShowQueryDialog = true;
			} else {
				tellPlayerToQuery (tGameManager, tPlayer);
				tWaitForReponseFrame = new WaitForReponseFrame ("Waiting for a Response", tPlayer, tCurrentPlayer);
				tWaitForReponseFrame.waitForResponse ();
				tExchangeApproved = exchangePrivateQuery.wasAccepted ();
			}
		} else {
			tShowQueryDialog = true;
			tResetWaitStateAction = (SetWaitStateAction) Action.NO_ACTION;
		}
		if (tShowQueryDialog) {
			tExchangeApproved = showQueryDialog (aRoundFrame);
		}
		if (tExchangeApproved) {
		  	handleExchangeCertificate ();
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
		QueryExchangeBenefitAction tQueryExchangeBenefitAction;
		ActorI.ActionStates tRoundType;
		ActorI.ActionStates tOldPlayerState;
		ActorI.ActionStates tNewPlayerState;
		String tRoundID;
		Player tCurrentPlayer;
		
		tRoundType = getRoundType (aGameManager);
		tRoundID = getRoundID (aGameManager);
		tCurrentPlayer = aGameManager.getCurrentPlayer ();
		tOldPlayerState = tCurrentPlayer.getPrimaryActionState ();
		exchangePrivateQuery = new ExchangePrivateQuery ("Private Exchange Benefit", tCurrentPlayer.getName (), 
				aPlayer.getName (), tOldPlayerState, privateCompany, NAME);
		tCurrentPlayer.setQueryOffer (exchangePrivateQuery);
		tCurrentPlayer.setPrimaryActionState (ActorI.ActionStates.WaitingResponse);
		tNewPlayerState = tCurrentPlayer.getPrimaryActionState ();
		tQueryExchangeBenefitAction = new QueryExchangeBenefitAction (tRoundType, tRoundID, tCurrentPlayer);
		tQueryExchangeBenefitAction.addQueryExchangeBenefitEffect (tCurrentPlayer, aPlayer, privateCompany, this);
		tQueryExchangeBenefitAction.addStateChangeEffect (tCurrentPlayer, tOldPlayerState, tNewPlayerState);
		tQueryExchangeBenefitAction.setChainToPrevious (true);
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
		tResetWaitStateAction.setChainToPrevious (true);
		
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
	
	public boolean showQueryDialog (JFrame aParentFrame) {
		String tQueryText;
		int tAnswer;
		boolean tExchangeApproved;
		
		tQueryText = buildQueryText ();

		tAnswer = JOptionPane.showConfirmDialog (aParentFrame, 
				tQueryText, "Exchange Private Share Benefit", 
		        JOptionPane.YES_NO_OPTION);

		if (tAnswer == JOptionPane.YES_OPTION) {
			tExchangeApproved = true;
		} else {
			tExchangeApproved = false;
		}
		
		return tExchangeApproved;
	}

	public String buildActionText () {
		String tAction;
		Certificate tShareCertificate;
		
		tShareCertificate = getShareCertificate ();
		tAction = "Exchange " + privateCompany.getAbbrev () + " for " + 
				certificatePercentage + "% of " + tShareCertificate.getCompanyAbbrev ();
				
		return tAction;
	}
	
	public String buildQueryText () {
		String tQueryText;
		String tOwnerName;
		
		tOwnerName = privateCompany.getPresidentName ();
		tQueryText = tOwnerName + ", do you want to " + buildActionText ();
		
		return tQueryText;
	}
}
