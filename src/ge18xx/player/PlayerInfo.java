package ge18xx.player;

import ge18xx.phase.PhaseInfo;
import geUtilities.GUI;
import geUtilities.xml.AttributeName;
import geUtilities.xml.XMLNode;

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
	public static final String NO_PHASE = GUI.EMPTY_STRING;
	public static final String ALL_PHASES = "0";
	public static final String EN_PLAYER_INFO = "PlayerInfo";
	public static final XMLNode NO_PLAYER_INFO_NODE = null;
	public static final int NO_PLAYERS = 0;
	public static final int NO_CASH = 0;
	public static final int NO_LIMIT = 0;
	public static final int NO_COMPANIES = 0;
	public static final int COMPANIES_CLOSED = -1;
	int numberPlayers;
	int startingCash;
	int certificateLimit;
	String phases;
	int companyCount;

	public PlayerInfo (XMLNode aCellNode) {
		int tNumPlayers;
		int tStartingCash;
		int tCertificateLimit;
		int tCompanyCount;
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

	public int getCompanyCount () {
		return companyCount;
	}
	
	public String getPhases () {
		return phases;
	}
	
	public int getPhaseCount () {
		int tPhaseCount;
		String tPhases [];
		
		tPhaseCount = 1;
		if (! phases.equals (ALL_PHASES)) {

			tPhases = phases.split (",");
			tPhaseCount = tPhases.length;
		}
		
		return tPhaseCount;
	}
	
	public String getPhase (int aIndex) {
		String tPhase;
		String tPhases [];
		
		tPhases = phases.split (",");
		if ((aIndex < 0) || (aIndex > tPhases.length)) {
			tPhase = NO_PHASE;
		} else {
			tPhase = tPhases [0];
		}
		
		return tPhase;
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