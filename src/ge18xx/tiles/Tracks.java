package ge18xx.tiles;

import java.awt.Graphics;
import java.awt.Paint;
import java.util.LinkedList;
import java.util.List;

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
import geUtilities.GUI;
import geUtilities.xml.XMLDocument;
import geUtilities.xml.XMLElement;

public class Tracks implements Cloneable {
	List<Track> segments;
	public static final Tracks NO_TRACKS = null;

	public Tracks () {
		segments = new LinkedList<> ();
	}

	private Tracks (List<Track> aSegments) {
		segments = aSegments;
	}

	public boolean add (Track aSegment) {
		return segments.add (aSegment);
	}

	public boolean canAllTracksExit (MapCell aThisMapCell, int aTileOrient) {
		boolean tCanAllTracksExit;
		boolean tTrackCanExit;

		tCanAllTracksExit = true;
		for (Track tSegment : segments) {
			tTrackCanExit = tSegment.canThisTrackExit (aThisMapCell, aTileOrient);
			if (!tTrackCanExit) {
				tCanAllTracksExit = false;
			}
		}

		return tCanAllTracksExit;
	}

	/**
	 * Clear the Specified Train Number on every Track on the Tile.
	 *
	 * @param aTrainNumber the Train Number to test for on the Tracks
	 */
	public void clearTrain (int aTrainNumber) {
		for (Track tSegment : segments) {
			if (tSegment.getTrainNumber () == aTrainNumber) {
				tSegment.clearTrainNumber ();
			}
		}
	}

	/**
	 * Clear the All Train Numbers on every Track on the Tile.
	 *
	 */
	public void clearAllTrains () {
		for (Track tSegment : segments) {
			tSegment.clearTrainNumber ();
		}
	}

	@Override
	public Tracks clone () {
		List<Track> tSegmentsCopy = new LinkedList<> ();
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

	public void draw (Graphics g, int XCenter, int YCenter, int aTileOrient, Hex aHex, Paint aHexColor,
			Feature2 aSelectedFeature) {
		List<Track> drawLaterSegments;
		
		drawLaterSegments = new LinkedList<Track> ();
		for (Track tSegment : segments) {
			if (tSegment.isTrackUsed ()) {
				drawLaterSegments.add (tSegment);
			} else {
				tSegment.draw (g, XCenter, YCenter, aTileOrient, aHex, aHexColor, aSelectedFeature);
			}
		}
		for (Track tSegment : drawLaterSegments) {
			tSegment.draw (g, XCenter, YCenter, aTileOrient, aHex, aHexColor, aSelectedFeature);
		}
	}

	public List<Track> getSegments () {
		return segments;
	}

	public String getToolTip () {
		String tTip;
		String tPriorTip;
		String tThisTip;

		tTip = GUI.EMPTY_STRING;
		tPriorTip = "X";
		tThisTip = GUI.EMPTY_STRING;
		for (Track tSegment : segments) {
			if (tSegment.getGauge ().useNameInToolTip ()) {
				tThisTip = tSegment.getGaugeName ();
				if (tPriorTip != tThisTip) {
					if (tTip != GUI.EMPTY_STRING) {
						tTip += GUI.COMMA_SPACE;
					}
					tTip += tThisTip;
					tPriorTip = tThisTip;
				}
			}
		}

		if (!tTip.equals (GUI.EMPTY_STRING)) {
			tTip = "Gauge: " + tTip + "<br>";
		}

		return tTip;
	}

	public boolean isTrackOnSide (int aSide) {
		boolean tIsTrackOnSide;
		
		tIsTrackOnSide = false;
		for (Track tSegment : segments) {
			if (tSegment.isTrackToSide (aSide)) {
				tIsTrackOnSide = true;
			}
		}

		return tIsTrackOnSide;
	}

	public boolean isTrackToSide (int aSide) {
		boolean tIsTrackToSide;

		tIsTrackToSide = false;
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

	@Override
	public String toString () {
		return segments.toString ();
	}
//
//	public Track get (int aTrackIndex) {
//		return segments.get (aTrackIndex);
//	}

	public int getTrackCount () {
		return segments.size ();
	}
	
	public Track get (int aTrackIndex) {
		Track tTrack = Track.NO_TRACK;

		tTrack = segments.get (aTrackIndex);

		return tTrack;
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

	/**
	 * Return the First Track from the specified side Location that has a useable
	 * Track
	 *
	 * Note -- This is based upon the ordering of the Track Segments in the XML
	 * Track data for the Specified Tile. A Track is Useable if the Gauge is
	 * Useable.
	 *
	 * @param aSideLocation The integer "Side" Location on the tile from which we
	 *                      need to find a Track Segment
	 * @return The actual Track Object that is found, or "Track.NO_TRACK" Constant
	 *         meaning there is no Track
	 *
	 */
	public Track getTrackFromSide (int aSideLocation) {
		Track tTrack = Track.NO_TRACK;

		for (Track tSegment : segments) {
			if (tSegment.useableTrack ()) {
				if (tTrack == Track.NO_TRACK) {
					if (tSegment.isTrackToSide (aSideLocation)) {
						tTrack = tSegment;
					}
				}
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
			if ((tSegment.getEnterLocation ().getLocation () == aStartLocation.getLocation ())
					&& (tSegment.getExitLocation ().getLocation () == aEndLocation.getLocation ())) {
				tTrackIndex = tCounter;
			} else if ((tSegment.getEnterLocation ().getLocation () == aEndLocation.getLocation ())
					&& (tSegment.getExitLocation ().getLocation () == aStartLocation.getLocation ())) {
				tTrackIndex = tCounter;
			}
			tCounter++;
		}

		return tTrackIndex;
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

	public boolean areLocationsConnected (Location aLocation, int aRemoteLocationIndex) {
		boolean tAreLocationsConnected = false;
		int tLocationInt;

		if (aLocation != Location.NO_LOC) {
			tLocationInt = aLocation.getLocation ();
			if (Location.isValidLocation (aRemoteLocationIndex)) {
				if (tLocationInt != aRemoteLocationIndex) {
					for (Track tSegment : segments) {
						if ((tSegment.getEnterLocationInt () == tLocationInt)
								&& (tSegment.getExitLocationInt () == aRemoteLocationIndex)) {
							tAreLocationsConnected = true;
						} else if ((tSegment.getExitLocationInt () == tLocationInt)
								&& (tSegment.getEnterLocationInt () == aRemoteLocationIndex)) {
							tAreLocationsConnected = true;
						}

					}
				}
			}
		}

		return tAreLocationsConnected;
	}
}
