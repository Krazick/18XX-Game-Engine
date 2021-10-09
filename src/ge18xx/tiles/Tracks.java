package ge18xx.tiles;

//
//  Tracks.java
//  Game_18XX
//
//  Created by Mark Smith on 3/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;

import java.awt.Color;
import java.awt.Graphics;

import java.util.List;
import java.util.LinkedList;

public class Tracks implements Cloneable {
	List<Track> segments;
	
	public Tracks () {
		segments = new LinkedList<Track> ();
	}
	
	private Tracks (List<Track> aSegments) {
		segments = aSegments;
	}
	
	public boolean add (Track aSegment) {
		return segments.add (aSegment);
	}
	
	public boolean canAllTracksExit (MapCell aThisMapCell, int aTileOrient) {
		boolean tCanAllTracksExit = true;
		boolean tTrackCanExit;
		
		for (Track tSegment : segments) {
			tTrackCanExit = tSegment.canThisTrackExit (aThisMapCell, aTileOrient);
			if (! tTrackCanExit) {
				tCanAllTracksExit = false;
			}
		}
		
		return tCanAllTracksExit;
	}
	
	public Tracks clone () {
		List<Track> tSegmentsCopy = new LinkedList<Track> ();
		for (Track tSegment : segments) {
			tSegmentsCopy.add (tSegment.clone ());
		}
		
		return new Tracks (tSegmentsCopy);
	}
	
	public XMLElement createElement (XMLDocument aXMLDocument, int aIndex) {
		XMLElement tElement;
		Track tTrack;
		
		tTrack = segments.get (aIndex);
		tElement = tTrack.createElement (aXMLDocument);
		
		return tElement;
	}
	
	public void draw (Graphics g, int XCenter, int YCenter, int aTileOrient, Hex aHex, Color aHexColor, Feature2 aSelectedFeature) {
		for (Track segment : segments) {
			segment.draw (g, XCenter, YCenter, aTileOrient, aHex, aHexColor, aSelectedFeature);
		}
	}
	
	public List<Track> getSegments () {
		return segments;
	}
	
	public String getToolTip () {
		String tTip = "";
		String tPriorTip = "X";
		String tThisTip = "";
		
		for (Track tSegment : segments) {
			if (tSegment.getGauge ().useNameInToolTip ()) {
				tThisTip = tSegment.getGaugeName ();
				if (tPriorTip != tThisTip) {
					if (tTip != "") {
						tTip += ", ";
					}
					tTip += tThisTip;
					tPriorTip = tThisTip;
				}
			}
		}
		
		if (! tTip.equals ("")) {
			tTip = "Gauge: " + tTip + "<br>";
		}
		
		return tTip;
	}
	
	public boolean isTrackOnSide (int aSide) {
		boolean tIsTrackOnSide = false;
		
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aSide)) {
				tIsTrackOnSide = true;
			}
		}
		
		return tIsTrackOnSide;
	}
	
	public boolean isTrackToSide (int aSide) {
		boolean tIsTrackToSide = false;
		
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aSide)) {
				tIsTrackToSide = true;
			}
		}
		
		return tIsTrackToSide;
	}
	
	public void printlog () {
		for (Track tSegment : segments) {
			tSegment.printlog ();
		}
	}
	
	public int size () {
		return segments.size ();
	}
	
	public String toString () {
		return segments.toString ();
	}

	public Track get (int aTrackIndex) {
		return segments.get(aTrackIndex);
	}
	
	public Track getTrackFromStartToEnd (int aStartLocation, int aEndLocation) {
		Track tTrack = Track.NO_TRACK;
		int tLocation1, tLocation2;
		
		for (Track tSegment : segments) {
			tLocation1 = tSegment.getEnterLocationInt ();
			tLocation2 = tSegment.getExitLocationInt ();
			if ((tLocation1 == aStartLocation) && (tLocation2 == aEndLocation)) {
				tTrack = tSegment;
			} else if ((tLocation2 == aStartLocation) && (tLocation1 == aEndLocation)) {
				tTrack = tSegment;
			}
		}
		
		return tTrack;
	}
	
	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack = Track.NO_TRACK;
		
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aSideLocation)) {
				tTrack = tSegment;
			}
		}
		
		return tTrack;
	}

	public int getTrackCountFromSide (Location aLocation) {
		int tTrackCount = 0;
		
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aLocation.getLocation ())) {
				tTrackCount++;
			}
		}

		return tTrackCount;
	}
	
	public int getTrackIndexBetween (Location aStartLocation, Location aEndLocation) {
		int tTrackIndex = 0;
		int tCounter = 0;
		
		for (Track tSegment : segments) {
			if ((tSegment.getEnterLocation ().getLocation () == aStartLocation.getLocation ()) &&
				(tSegment.getExitLocation ().getLocation () == aEndLocation.getLocation ())) {
				tTrackIndex = tCounter;
			} else if ((tSegment.getEnterLocation ().getLocation () == aEndLocation.getLocation ()) &&
					(tSegment.getExitLocation ().getLocation () == aStartLocation.getLocation ())) {
				tTrackIndex = tCounter;
			}
			tCounter++;
		}
		
		return tTrackIndex;
	}

	public Track getTrack (int aTrackIndex) {
		Track tTrack = Track.NO_TRACK;
		
		tTrack = segments.get (aTrackIndex);
		
		return tTrack;
	}

	public Track getTrackFromStartByIndex (Location aStartLocation, int aNextTrackIndex) {
		int tCounter = 0;
		Track tTrack = Track.NO_TRACK;
		
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aStartLocation.getLocation ())) {
				if (tCounter == aNextTrackIndex) {
					tTrack = tSegment;
				}
			}
			tCounter++;
		}
		
		return tTrack;
	}

}
