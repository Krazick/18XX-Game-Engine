package ge18xx.bank;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import ge18xx.game.GameManager;
import ge18xx.player.Player;
import ge18xx.player.Portfolio;
import ge18xx.train.TrainPortfolio;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.ParsingRoutine2I;
import ge18xx.utilities.ParsingRoutineI;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;
import ge18xx.utilities.XMLNodeList;

//
//  BankPool.java
//  Game_18XX
//
//  Created by Mark Smith on 11/27/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class BankPool extends GameBank {
	public static final String NAME = "Bank Pool";
	public static final ElementName EN_BANK_POOL_STATE = new ElementName ("BankPool");
	public static final BankPool NO_BANK_POOL = null;

	public BankPool (GameManager aGameManager) {
		super (NAME, aGameManager);
	}

	public boolean isBankPool () {
		return true;
	}
	
	public JPanel buildPortfolioInfoJPanel (ItemListener aItemListener, Player aPlayer, 
			GameManager aGameManager) {
		return buildPortfolioInfoJPanel (aItemListener, aPlayer, aGameManager, 
				Player.BUY_LABEL);
	}

	public XMLElement getBankPoolStateElements (XMLDocument aXMLDocument) {
		XMLElement tXMLElement, tTrainPortfolioElements, tStockPortfolioElements;
		
		tXMLElement = aXMLDocument.createElement (EN_BANK_POOL_STATE);
		tStockPortfolioElements = getPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tStockPortfolioElements);
		tTrainPortfolioElements = getTrainPortfolioElements (aXMLDocument);
		tXMLElement.appendChild (tTrainPortfolioElements);
		
		return tXMLElement;
	}
	
	public void loadBankPoolState (XMLNode aBankPoolNode) {
		XMLNodeList tXMLNodeList;
		
		tXMLNodeList = new XMLNodeList (bankPoolParsingRoutine);
		tXMLNodeList.parseXMLNodeList (aBankPoolNode, Portfolio.EN_PORTFOLIO, 
				TrainPortfolio.EN_TRAIN_PORTFOLIO);
	}
	
	ParsingRoutineI bankPoolParsingRoutine  = new ParsingRoutine2I ()  {
		@Override
		public void foundItemMatchKey1 (XMLNode aChildNode) {
			loadPortfolio (aChildNode);
		}

		@Override
		public void foundItemMatchKey2 (XMLNode aChildNode) {
			loadTrainPortfolio (aChildNode);
		}
	};
}
