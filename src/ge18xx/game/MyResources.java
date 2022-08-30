package ge18xx.game;

import java.util.ListResourceBundle;

//
//  MyResources.java
//  Game_18XX
//
//  Created by Mark Smith on 9/7/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class MyResources extends ListResourceBundle {
	@Override
	public Object [] [] getContents () {
		return contents;
	}

	static final Object [] [] contents = {
			// Generic Resources
			{ "fileMenu", "File" }, { "editMenu", "Edit" }, { "newItem", "New" }, { "openItem", "Open..." },
			{ "closeItem", "Close" }, { "saveItem", "Save" }, { "saveAsItem", "Save As..." },
			{ "saveConfigItem", "Save Config" }, { "frameInfoItem", "Frame Info" }, { "exitItem", "Exit" },
			{ "undoItem", "Undo" }, { "cutItem", "Cut" }, { "copyItem", "Copy" }, { "pasteItem", "Paste" },
			{ "clearItem", "Clear" }, { "selectAllItem", "Select All" }, { "gameMenu", "Game" },
			// Game Specific Resources
			{ "message", "18xx Game Engine" }, 
			{ "showMapItem", "Show Map" }, 
			{ "showMarketItem", "Show Market" },
			{ "showCitiesItem", "Show City List" }, 
			{ "showTileTrayItem", "Show Tile Tray" },
			{ "showPrivatesItem", "Show Privates List" }, 
			{ "showMinorsItem", "Show Minor Companies List" },
			{ "showShareCompaniesItem", "Show Share Companies List" }, 
			{ "showChatClientItem", "Show Chat Client" },
			{ "showRoundFrameItem", "Show Round Frame" }, 
			{ "showAuditFrameItem", "Show Audit Frame" },
			{ "showActionReportFrameItem", "Show Action Report Frame" }, 
			{ "frameTitle", "18XX Game Engine" },
			{ "configDir", "18XX XML Data" }, 
			{ "iconImage", "images/GE18XX.png" },
			{ "GameSetXMLFile", "18xx Games.xml" }, 
			{ "version", "0.7.17 Beta" } };
}