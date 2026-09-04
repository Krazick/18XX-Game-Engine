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
import geUtilities.xml.XMLNode;

public class QueryExchangeBenefit extends ExchangeBenefit {
	public static final String NAME = "Query Exchange";
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
		Player tPrivatePresident;
		Player tCurrentPlayer;
		String tTitle;
		boolean tShowQueryDialog;
		boolean tExchangeApproved;
		WaitForReponseFrame tWaitForReponseFrame;

		tShowQueryDialog = false;
		tExchangeApproved = false;
		tGameManager = privateCompany.getGameManager ();

		if (tGameManager.isNetworkGame ()) {
			tCurrentPlayer = tGameManager.getCurrentPlayer ();
			tPrivatePresident = (Player) privateCompany.getPresident ();
			if (tCurrentPlayer == tPrivatePresident) {
				tShowQueryDialog = true;
			} else {
				tellPlayerToQuery (tGameManager, tPrivatePresident);
				tTitle = tGameManager.createFrameTitle ("Waiting for a Response");
				tWaitForReponseFrame = new WaitForReponseFrame (tTitle, tPrivatePresident, tCurrentPlayer);
				tWaitForReponseFrame.waitForResponse ();
				tExchangeApproved = exchangePrivateQuery.wasAccepted ();
			}
		} else {
			tShowQueryDialog = true;
		}
		if (tShowQueryDialog) {
			tExchangeApproved = showQueryDialog (aRoundFrame);
		}
		if (tExchangeApproved) {
		  	handleExchangeCertificate ();
		}
	}

	/**
	 * Test the Player who is President of the Private with a Query Exchange Benefit to ask the question
	 * and if needed to perform the exchange.
	 *
	 * @param aGameManager 			The Game Manager, to retrieve info, and add the 
	 * 								QueryExchangeBenefitAction to be done
	 * @param aPrivatePresident 	the Player who is the President of the Private,
	 * 								This Player must answer the question of the Exchange.
	 *
	 */
	private void tellPlayerToQuery (GameManager aGameManager, Player aPrivatePresident) {
		QueryExchangeBenefitAction tQueryExchangeBenefitAction;
		ActorI.ActionStates tRoundType;
		ActorI.ActionStates tPlayerOldState;
		ActorI.ActionStates tPlayerNewState;
		String tRoundID;
		Player tCurrentPlayer;
		String tPresidentName;
		String tCurrentPlayerName;

		tRoundType = getRoundState (aGameManager);
		tRoundID = getRoundID (aGameManager);
		tCurrentPlayer = aGameManager.getCurrentPlayer ();
		tPlayerOldState = tCurrentPlayer.getPrimaryActionState ();
		tPresidentName = aPrivatePresident.getName ();
		tCurrentPlayerName = tCurrentPlayer.getName ();
		exchangePrivateQuery = new ExchangePrivateQuery ("Exchange Private Benefit", tCurrentPlayerName,
				tPresidentName, tPlayerOldState, privateCompany, NAME);
		aPrivatePresident.setQueryOffer (exchangePrivateQuery);
		tCurrentPlayer.setPrimaryActionState (ActorI.ActionStates.WaitingResponse);
		tPlayerNewState = tCurrentPlayer.getPrimaryActionState ();
		tQueryExchangeBenefitAction = new QueryExchangeBenefitAction (tRoundType, tRoundID, aPrivatePresident);
		tQueryExchangeBenefitAction.addQueryExchangeBenefitEffect (tCurrentPlayer, aPrivatePresident, privateCompany, this);
		tQueryExchangeBenefitAction.addStateChangeEffect (tCurrentPlayer, tPlayerOldState, tPlayerNewState);
		tQueryExchangeBenefitAction.setChainToPrevious (true);
		aGameManager.addAction (tQueryExchangeBenefitAction);
	}

	private ActorI.ActionStates getRoundState (GameManager aGameManager) {
		ActorI.ActionStates tRoundState;

		if (aGameManager.isOperatingRound ()) {
			tRoundState = ActorI.ActionStates.OperatingRound;
		} else if (aGameManager.isStockRound ()) {
			tRoundState = ActorI.ActionStates.StockRound;
		} else {
			tRoundState = ActorI.ActionStates.AuctionRound;
		}

		return tRoundState;
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
