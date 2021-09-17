package ge18xx.game;

//
//  MyResources.java
//  Game_18XX
//
//  Created by Mark Smith on 9/7/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import java.util.*;

public class MyResources extends ListResourceBundle {
	public Object [][] getContents () {
		return contents;
	}
	static final Object [][] contents = {
		// LOCALIZE THIS
			{"message", "18xx Game Engine"},
			{"fileMenu", "File"},
			{"editMenu", "Edit"},
			{"newItem", "New"},
			{"openItem", "Open..."},
			{"closeItem", "Close"},
			{"saveItem", "Save"},
			{"saveAsItem", "Save As..."},
			{"saveConfigItem", "Save Config"},
			{"exitItem", "Exit"},
			{"undoItem", "Undo"},
			{"cutItem", "Cut"},
			{"copyItem", "Copy"},
			{"pasteItem", "Paste"},
			{"clearItem", "Clear"},
			{"selectAllItem", "Select All"},
			{"gameMenu", "Game"},
			{"showMapItem", "Show Map"},
			{"showMarketItem", "Show Market"},
			{"showCitiesItem", "Show City List"},
			{"showTileTrayItem", "Show Tile Tray"},
			{"showPrivatesItem", "Show Privates List"},
			{"showCoalsItem", "Show Coal Companies List"},
			{"showMinorsItem", "Show Minor Companies List"},
			{"showShareCompaniesItem", "Show Share Companies List"},
			{"showChatClientItem", "Show Chat Client"},
			{"showRoundFrameItem", "Show Round Frame"},
			{"showAuditFrameItem", "Show Audit Frame"},
			{"showActionReportFrameItem", "Show Action Report Frame"},
			{"frameTitle", "18XX Game Engine"},
			{"version", "V 0.7.3 Beta"}
	};
}