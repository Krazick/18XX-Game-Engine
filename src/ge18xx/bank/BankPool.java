package ge18xx.bank;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.company.Coupon;
import ge18xx.game.GameInfo;
import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.train.TrainPortfolio;
import geUtilities.ElementName;
import geUtilities.ParsingRoutine2I;
import geUtilities.ParsingRoutineI;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.xml.XMLNode;
import geUtilities.xml.XMLNodeList;

//
//  BankPool.java
//  Game_18XX
//
//  Created by Mark Smith on 11/27/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class BankPool extends GameBank {
	public static final ElementName EN_BANK_POOL_STATE = new ElementName ("BankPool");
	public static final String NAME = "Bank Pool";
	public static final BankPool NO_BANK_POOL = null;
	
	ParsingRoutineI bankPoolParsingRoutine = new ParsingRoutine2I () {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadPortfolio (aChildNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			loadTrainPortfolio (aChildNode);
		}
	};

	public BankPool (GameManager aGameManager) {
		super (NAME, aGameManager);

		Bank tBank;
		GameInfo tGameInfo;
		String tBankPoolName;
		
		tGameInfo = gameManager.getActiveGame ();
		tBankPoolName = tGameInfo.getBankPoolName ();
		setName (tBankPoolName);
		tBank = aGameManager.getBank ();
		trainPortfolio.setPortfolioHolder (tBank);
	}
	
	@Override
	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer) {
		return buildPortfolioInfoJPanel (aItemListener, aPlayer, Player.BUY_LABEL);
	}

	@Override
	public String getAbbrev () {
		return NAME;
	}

//	public XMLElement getBankPoolStateElements (XMLDocument aXMLDocument) {
	@Override
	public XMLElement addElements (XMLDocument aXMLDocument, ElementName aEN_Type) {
		XMLElement tXMLElement;
		XMLElement tTrainPortfolioElements;
		XMLElement tStockPortfolioElements;
	
		tXMLElement = aXMLDocument.createElement (EN_BANK_POOL_STATE);
		tStockPortfolioElements = getPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tStockPortfolioElements);
		tTrainPortfolioElements = getTrainPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tTrainPortfolioElements);

		return tXMLElement;
	}
	
	public Coupon getNextAvailableTrain () {
		Coupon tTrain;

		tTrain = trainPortfolio.getNextAvailableTrain ();

		return tTrain;
	}

	@Override
	public boolean isABankPool () {
		return true;
	}

	public void loadBankPoolState (XMLNode aBankPoolNode) {
		XMLNodeList tXMLNodeList;

		tXMLNodeList = new XMLNodeList (bankPoolParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aBankPoolNode, Portfolio.EN_PORTFOLIO, TrainPortfolio.EN_TRAIN_PORTFOLIO);
	}
}
