package ge18xx.player;

import ge18xx.phase.PhaseInfo;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLNode;

//
//  PlayerInfo.java
//  Game_18XX
//
//  Created by Mark Smith on 12/25/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class PlayerInfo {
	public static final AttributeName AN_COMPANIES = new AttributeName ("companies");
	public static final AttributeName AN_NUM_PLAYERS = new AttributeName ("numPlayers");
	public static final AttributeName AN_STARTING_CASH = new AttributeName ("startingCash");
	public static final AttributeName AN_CERTIFICATE_LIMIT = new AttributeName ("certificateLimit");
	static final int NO_PLAYERS = 0;
	static final int NO_CASH = 0;
	static final int NO_LIMIT = 0;
	static final String ALL_PHASES = "0";
	static final int NO_COMPANIES = 0;
	static final int COMPANIES_CLOSED = -1;
	public static final String EN_PLAYER_INFO = "PlayerInfo";
	int numberPlayers;
	int startingCash;
	int certificateLimit;
	String phases;
	int companyCount;

	public PlayerInfo (XMLNode aCellNode) {
		int tNumPlayers, tStartingCash, tCertificateLimit, tCompanyCount;
		String tPhases;

		tNumPlayers = aCellNode.getThisIntAttribute (AN_NUM_PLAYERS);
		tStartingCash = aCellNode.getThisIntAttribute (AN_STARTING_CASH);
		tCertificateLimit = aCellNode.getThisIntAttribute (AN_CERTIFICATE_LIMIT);
		tCompanyCount = aCellNode.getThisIntAttribute (AN_COMPANIES, NO_COMPANIES);
		tPhases = aCellNode.getThisAttribute (PhaseInfo.AN_PHASES, ALL_PHASES);
		setValues (tNumPlayers, tStartingCash, tCertificateLimit, tPhases, tCompanyCount);
	}

	public int getCertificateLimit () {
		return certificateLimit;
	}

	public int getNumPlayers () {
		return numberPlayers;
	}

	public int getStartingCash () {
		return startingCash;
	}

	public void setValues (int aNumPlayers, int aStartingCash, int aCertificateLimit, String aPhases,
			int aCompanyCount) {
		numberPlayers = aNumPlayers;
		startingCash = aStartingCash;
		certificateLimit = aCertificateLimit;
		phases = aPhases;
		companyCount = aCompanyCount;
	}
}