package ge18xx.round.action.effects;

import ge18xx.bank.StartPacketItem;
import ge18xx.company.benefit.Benefit;
import ge18xx.game.GameManager;
import ge18xx.round.action.ActorI;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

public class RemoveStartPacketItemEffect extends Effect {
	public final static String NAME = "Remove Start Packet Item";
	final static AttributeName AN_COMPANY_ABBREV = new AttributeName ("companyAbbrev");
	final static AttributeName AN_ITEM_ROW = new AttributeName ("itemRow");
	final static AttributeName AN_ITEM_COL = new AttributeName ("itemCol");
	StartPacketItem startPacketItem;
	int itemRow;
	int itemCol;
	
	public RemoveStartPacketItemEffect () {
	}

	public RemoveStartPacketItemEffect (String aName) {
		super (aName);
	}

	public RemoveStartPacketItemEffect (String aName, ActorI aActor) {
		super (aName, aActor);
	}

	public RemoveStartPacketItemEffect (String aName, ActorI aActor, Benefit aBenefitInUse) {
		super (aName, aActor, aBenefitInUse);
		// TODO Auto-generated constructor stub
	}

	public RemoveStartPacketItemEffect (XMLNode aEffectNode, GameManager aGameManager) {
		super (aEffectNode, aGameManager);
		setName (NAME);
	}
	
	public RemoveStartPacketItemEffect (ActorI aActor, StartPacketItem aStartPacketItem, 
			int aItemRow, int aItemColumn) {
		super (NAME, aActor);
		setStartPacketItem (aStartPacketItem);
		setItemRow (aItemRow);
		setItemCol (aItemColumn);
	}
	
	public void setStartPacketItem (StartPacketItem aStartPacketItem) {
		startPacketItem = aStartPacketItem;
	}
	
	public StartPacketItem getStartPacketItem () {
		return startPacketItem;
	}
	
	public void setItemRow (int aItemRow) {
		itemRow = aItemRow;
	}
	
	public int getItemRow () {
		return itemRow;
	}
	
	public void setItemCol (int aItemCol) {
		itemCol = aItemCol;
	}
	
	public int getItemCol () {
		return itemCol;
	}
}
