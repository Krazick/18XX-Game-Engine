package ge18xx.tiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;

import ge18xx.map.Hex;
import ge18xx.map.Location;
import ge18xx.map.MapCell;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.ElementName;
import ge18xx.utilities.XMLDocument;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class Track implements Cloneable {
	public static final ElementName EN_TRACK = new ElementName ("Track");
	public static final AttributeName AN_ENTER = new AttributeName ("enter");
	public static final AttributeName AN_EXIT = new AttributeName ("exit");
	public static final AttributeName AN_GAUGE = new AttributeName ("gauge");
	static final int NO_SIDE = 0;
	static final int NO_TRAIN = 0;
	public static Track NO_TRACK = null;

	Location enter;
	Location exit;
	Gauge gauge;
	int trainNumberUsing;

	public Track () {
		this (NO_SIDE, NO_SIDE, Gauge.NO_TYPE);
	}

	public Track (int aEnter, int aExit, int aGaugeType) {
		Gauge tGauge = new Gauge (aGaugeType);
		setValues (aEnter, aExit, tGauge);
	}

	public Track (int aEnter, int aExit, Gauge aGauge) {
		setValues (aEnter, aExit, aGauge);
	}

	public Track (XMLNode aNode) {
		String tGaugeName;
		int tGaugeType;
		int tEnter, tExit;
		Gauge tGauge;

		tEnter = aNode.getThisIntAttribute (AN_ENTER);
		tExit = aNode.getThisIntAttribute (AN_EXIT);
		tGaugeName = aNode.getThisAttribute (AN_GAUGE);
		tGaugeType = Gauge.getGaugeFromName (tGaugeName);
		tGauge = new Gauge (tGaugeType);
		setValues (tEnter, tExit, tGauge);
	}

	public boolean canThisTrackExit (MapCell aThisMapCell, int aTileOrient) {
		boolean tCanThisTrackExit;
		boolean tCanTrackToEntrance = true;
		boolean tCanTrackToExit = true;
		Location tRotatedEntrance;
		Location tRotatedExit;

		if (enter.isSide ()) {
			tRotatedEntrance = enter.rotateLocation (aTileOrient);
			tCanTrackToEntrance = aThisMapCell.canTrackToSide (tRotatedEntrance.getLocation ());
		}

		if (exit.isSide ()) {
			tRotatedExit = exit.rotateLocation (aTileOrient);
			tCanTrackToExit = aThisMapCell.canTrackToSide (tRotatedExit.getLocation ());
		}

		tCanThisTrackExit = tCanTrackToEntrance && tCanTrackToExit;

		return tCanThisTrackExit;
	}

	/**
	 * 
	 * Test if the Gauge of the Track Segment is Useable
	 * 
	 * @return True if the Gauge is useable by a Train or not ('Track' Segments can
	 *         be OVERPASSes, or dashed components of a combination Track.
	 * 
	 */
	public boolean useableTrack () {
		return gauge.useableGauge ();
	}

	public boolean cityAtLocation (Location location) {
		boolean retValue;

		if (location.isSide ()) {
			return (false);
		} else {
			if ((enter == location) || (exit == location)) {
				retValue = true;
			} else {
				retValue = false;
			}
		}

		return (retValue);
	}

	public void clearTrainNumber () {
		setTrainNumber (NO_TRAIN);
	}

	@Override
	public Track clone () {
		try {
			Track tTrack = (Track) super.clone ();
			tTrack.enter = enter.clone ();
			tTrack.exit = exit.clone ();
			tTrack.gauge = gauge.clone ();
			tTrack.trainNumberUsing = trainNumberUsing;

			return tTrack;
		} catch (CloneNotSupportedException e) {
			throw new Error ("Track.clone Not Supported Exception");
		}
	}

	public XMLElement createElement (XMLDocument aXMLDocument) {
		XMLElement tElement;

		tElement = aXMLDocument.createElement (EN_TRACK);
		tElement.setAttribute (AN_ENTER, enter.toString ());
		tElement.setAttribute (AN_EXIT, exit.toString ());
		tElement.setAttribute (AN_GAUGE, gauge.getName ());

		return tElement;
	}

	public void draw (Graphics g, int X, int Y, int aTileOrient, Hex aHex, Paint aBaseColor,
			Feature2 aSelectedFeature) {
		Location tEnter;
		Location tExit;
		Area tNewClip;
		Area tHexClip;
		Shape tPreviousClip;
		Polygon tHexPolygon;
		Hex tHex;

		tPreviousClip = g.getClip ();
		tHex = new Hex (X, Y, Hex.getDirection (), Hex.getScale ());
		tHexPolygon = tHex.getHexPolygon ();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tHexPolygon);
		tNewClip.intersect (tHexClip);
		g.setClip (tNewClip);
		if ((enter.isCity () || exit.isCity ()) || (enter.isDeadEnd () || exit.isDeadEnd ())) {
			drawTrackToCity (g, X, Y, aTileOrient, tHex, aBaseColor);
		} else {
			if ((enter.isSide ()) && (exit.isSide ())) {
				tEnter = enter.rotateLocation (aTileOrient);
				tExit = exit.rotateLocation (aTileOrient);
				if (tEnter.getLocation () > tExit.getLocation ()) {
					drawTrackEdgeToEdge (g, X, Y, tExit, tEnter, tHex, aBaseColor);
				} else {
					drawTrackEdgeToEdge (g, X, Y, tEnter, tExit, tHex, aBaseColor);
				}
			}
		}
		g.setClip (tPreviousClip);
	}

	public void drawDeadEnd (Graphics g, int X, int Y, Location tEnter, Hex aHex, Paint aBaseColor) {
		int enterSide = tEnter.getLocation ();
		int x[], y[];
		int aTrackWidth = aHex.getTrackWidth ();
		int halfTrackWidth = aTrackWidth / 2;
		int Xd = aHex.getXd ();
		int Yd = aHex.getYd ();
		int depthY = (int) (Yd / 3);
		Paint trackColor;
		int maxXDisplacement = (int) (Xd * 1.57143);
		int minXDisplacement = (int) (Xd * 1.42857);
		int maxYDisplacement = (int) (Yd * 0.57143);
		int minYDisplacement = (int) (Yd * 0.42857);
		Graphics2D g2d = (Graphics2D) g;
		Stroke tCurrentStroke = g2d.getStroke ();
		BasicStroke tTrackStroke;

		x = new int [4];
		y = new int [4];
		if (Hex.getDirection ()) {
			enterSide += 6;
		}
		switch (enterSide) {
		case (0):
			x [0] = X - halfTrackWidth;
			y [0] = Y - Yd;
			x [1] = X + halfTrackWidth;
			y [1] = y [0];
			x [2] = X;
			y [2] = Y - (int) (depthY * 1.75);
			break;

		case (1):
			x [0] = X + minXDisplacement;
			y [0] = Y - maxYDisplacement;
			x [1] = X + maxXDisplacement;
			y [1] = Y - minYDisplacement;
			x [2] = X + Xd;
			y [2] = Y - depthY;
			break;

		case (2):
			x [0] = X + maxXDisplacement;
			y [0] = Y + minYDisplacement;
			x [1] = X + minXDisplacement;
			y [1] = Y + maxYDisplacement;
			x [2] = X + Xd;
			y [2] = Y + depthY;
			break;

		case (3):
			x [0] = X - halfTrackWidth;
			y [0] = Y + Yd;
			x [1] = X + halfTrackWidth;
			y [1] = y [0];
			x [2] = X;
			y [2] = Y + (int) (depthY * 1.75);
			break;

		case (4):
			x [0] = X - maxXDisplacement - 1;
			y [0] = Y + minYDisplacement;
			x [1] = X - minXDisplacement - 1;
			y [1] = Y + maxYDisplacement;
			x [2] = X - Xd;
			y [2] = Y + depthY;
			break;

		case (5):
			x [0] = X - maxXDisplacement - 1;
			y [0] = Y - minYDisplacement;
			x [1] = X - minXDisplacement - 1;
			y [1] = Y - maxYDisplacement;
			x [2] = X - Xd;
			y [2] = Y - depthY;
			break;

		case (6):
			x [0] = X - Yd;
			y [0] = Y - halfTrackWidth;
			x [1] = x [0];
			y [1] = Y + halfTrackWidth;
			x [2] = X - (int) (depthY * 1.75);
			y [2] = Y;
			break;

		case (7):
			x [0] = X - minYDisplacement;
			y [0] = Y - maxXDisplacement;
			x [1] = X - maxYDisplacement;
			y [1] = Y - minXDisplacement;
			x [2] = X - depthY;
			y [2] = Y - Xd;
			break;

		case (8):
			x [0] = X + maxYDisplacement;
			y [0] = Y - minXDisplacement;
			x [1] = X + minYDisplacement;
			y [1] = Y - maxXDisplacement;
			x [2] = X + depthY;
			y [2] = Y - Xd;
			break;

		case (9):
			x [0] = X + Yd;
			y [0] = Y - halfTrackWidth;
			x [1] = x [0];
			y [1] = Y + halfTrackWidth;
			x [2] = X + (int) (depthY * 1.75);
			y [2] = Y;
			break;

		case (10):
			x [0] = X + minYDisplacement;
			y [0] = Y + maxXDisplacement - 1;
			x [1] = X + maxYDisplacement;
			y [1] = Y + minXDisplacement - 1;
			x [2] = X + depthY;
			y [2] = Y + Xd;
			break;

		case (11):
			x [0] = X - minYDisplacement;
			y [0] = Y + maxXDisplacement - 1;
			x [1] = X - maxYDisplacement;
			y [1] = Y + minXDisplacement - 1;
			x [2] = X - depthY;
			y [2] = Y + Xd;
			break;
		}
		x [3] = x [0];
		y [3] = y [0];
		tTrackStroke = getTrackStroke (aHex);
		g2d.setStroke (tTrackStroke);
		trackColor = getColor (aBaseColor);
		g2d.setPaint (trackColor);
		g2d.fillPolygon (x, y, 3);
		g2d.setStroke (tCurrentStroke);
	}

	public void drawGentleTurn (Graphics g, int X, int Y, Location aEnter, Location aExit, Hex aHex, Paint aBaseColor) {
		int tTrackRadius, tTrackDiameter, topLeftX, topLeftY;
		int exitValue = aExit.getLocation ();
		Graphics2D g2d = (Graphics2D) g;
		Stroke tCurrentStroke = g2d.getStroke ();
		BasicStroke tTrackStroke;
		boolean tDraw;
		int Xd = aHex.getXd ();
		int Yd = aHex.getYd ();
		boolean direction = Hex.getDirection ();
		Paint trackColor;

		tTrackRadius = Hex.getWidth () + Xd;
		tTrackDiameter = tTrackRadius + tTrackRadius;
		tDraw = true;
		topLeftX = 0;
		topLeftY = 0;
		switch (aEnter.getLocation ()) {
		case (0):
			if (exitValue == 2) {
				if (direction) {
					topLeftX = X - Yd - tTrackRadius;
					topLeftY = Y - tTrackDiameter;
				} else {
					topLeftX = X;
					topLeftY = Y - Yd - tTrackRadius;
				}
			} else if (exitValue == 4) {
				if (direction) {
					topLeftX = X - Yd - tTrackRadius;
					topLeftY = Y;
				} else {
					topLeftX = X - tTrackDiameter;
					topLeftY = Y - Yd - tTrackRadius;
				}
			} else {
				tDraw = false;
			}
			break;

		case (1):
			if (exitValue == 3) {
				if (direction) {
					topLeftX = X + Yd - tTrackRadius;
					topLeftY = Y - tTrackDiameter;
				} else {
					topLeftX = X;
					topLeftY = Y + Yd - tTrackRadius;
				}
			} else if (exitValue == 5) {
				if (direction) {
					topLeftX = X - Yd * 2 - tTrackRadius;
					topLeftY = Y - tTrackRadius;
				} else {
					topLeftX = X - tTrackRadius;
					topLeftY = Y - Yd * 2 - tTrackRadius;
				}
			} else {
				tDraw = false;
			}
			break;

		case (2):
			if (direction) {
				topLeftX = X + Yd * 2 - tTrackRadius;
				topLeftY = Y - tTrackRadius;
			} else {
				topLeftX = X - tTrackRadius;
				topLeftY = Y + Yd * 2 - tTrackRadius;
			}
			break;

		case (3):
			if (direction) {
				topLeftX = X + Yd - tTrackRadius;
				topLeftY = Y;
			} else {
				topLeftX = X - tTrackDiameter;
				topLeftY = Y + Yd - tTrackRadius;
			}
			break;

		default:
			tDraw = false;
		}
		trackColor = getColor (aBaseColor);
		tTrackStroke = getTrackStroke (aHex);
		g2d.setStroke (tTrackStroke);
		g2d.setPaint (trackColor);
		if (tDraw) {
			g2d.drawOval (topLeftX, topLeftY, tTrackDiameter, tTrackDiameter);
		}
		g2d.setStroke (tCurrentStroke);
		// Draw Center line Track in Yellow - Debugging Only
		// g.setColor (Color.yellow);
		// if (tDraw) {
		// g.drawOval (topLeftX, topLeftY, tTrackDiameter, tTrackDiameter);
		// }
	}

	public void drawSharpTurn (Graphics g, int X, int Y, Location aEnter, Location aExit, Hex aHex, Paint aBaseColor) {
		int topLeftX, topLeftY, tTrackDiameter, tTrackRadius;
		boolean tDraw;
		int exitValue = aExit.getLocation ();
		Graphics2D g2d = (Graphics2D) g;
		Stroke tCurrentStroke = g2d.getStroke ();
		BasicStroke tTrackStroke;
		int Yd = aHex.getYd ();
		boolean direction = Hex.getDirection ();
		Paint trackColor;

		tTrackRadius = aHex.getXd ();
		tTrackDiameter = tTrackRadius + tTrackRadius;
		tDraw = true;
		topLeftX = 0;
		topLeftY = 0;
		switch (aEnter.getLocation ()) {
		case (0):
			if (exitValue == 1) {
				if (direction) {
					topLeftX = X - tTrackRadius - Yd;
					topLeftY = Y - tTrackDiameter;
				} else {
					topLeftX = X;
					topLeftY = Y - tTrackRadius - Yd;
				}
			} else if (exitValue == 5) {
				if (direction) {
					topLeftX = X - tTrackRadius - Yd;
					topLeftY = Y;
				} else {
					topLeftX = X - tTrackDiameter;
					topLeftY = Y - tTrackRadius - Yd;
				}
			} else {
				tDraw = false;
			}
			break;

		case (1):
			if (direction) {
				topLeftX = X - tTrackRadius;
				topLeftY = Y - Hex.getWidth () - tTrackRadius;
			} else {
				topLeftX = X + Hex.getWidth () - tTrackRadius;
				topLeftY = Y - tTrackRadius;
			}
			break;

		case (2):
			if (direction) {
				topLeftX = X + Yd - tTrackRadius;
				topLeftY = Y - tTrackDiameter;
			} else {
				topLeftX = X;
				topLeftY = Y + Yd - tTrackRadius;
			}
			break;

		case (3):
			if (direction) {
				topLeftX = X + Yd - tTrackRadius;
				topLeftY = Y;
			} else {
				topLeftX = X - tTrackDiameter;
				topLeftY = Y + Yd - tTrackRadius;
			}
			break;

		case (4):
			if (direction) {
				topLeftX = X - tTrackRadius;
				topLeftY = Y + Hex.getWidth () - tTrackRadius;
			} else {
				topLeftX = X - Hex.getWidth () - tTrackRadius;
				topLeftY = Y - tTrackRadius;
			}
			break;

		default:
			tDraw = false;
			break;
		}

		trackColor = getColor (aBaseColor);
		tTrackStroke = getTrackStroke (aHex);
		g2d.setStroke (tTrackStroke);
		g2d.setPaint (trackColor);
		if (tDraw) {
			g2d.drawOval (topLeftX, topLeftY, tTrackDiameter, tTrackDiameter);
		}
		g2d.setStroke (tCurrentStroke);

	}

	public void drawSlicePolygonFrame (Graphics g, Polygon sliceFrame, Color sliceColor) {
		g.setColor (sliceColor);
		g.fillPolygon (sliceFrame);
	}

	public void drawStraight (Graphics g, int X, int Y, Location aEnter, Hex aHex, Paint aBaseColor) {
		int tStartX, tStartY, tStopX, tStopY;
		int tEnterValue = aEnter.getLocation ();

		tStartX = aHex.midpointX (tEnterValue);
		tStartY = aHex.midpointY (tEnterValue);
		tStopX = aHex.midpointX (tEnterValue + 3);
		tStopY = aHex.midpointY (tEnterValue + 3);
		drawStraightSegment (g, tStartX, tStartY, tStopX, tStopY, aHex, aBaseColor);
	}

	public void drawStraightSegment (Graphics g, int X1, int Y1, int X2, int Y2, Hex aHex, Paint aBaseColor) {
		Graphics2D g2d = (Graphics2D) g;
		Stroke tCurrentStroke = g2d.getStroke ();
		BasicStroke tTrackStroke;
		Paint trackColor;

		trackColor = getColor (aBaseColor);
		tTrackStroke = getTrackStroke (aHex);
		g2d.setStroke (tTrackStroke);
		g2d.setPaint (trackColor);
		g2d.drawLine (X1, Y1, X2, Y2);
		g2d.setStroke (tCurrentStroke);
	}

	public void drawTrackEdgeToEdge (Graphics g, int X, int Y, Location aEnter, Location aExit, Hex aHex,
			Paint aBaseColor) {
		if ((aEnter.getLocation () + 3) == aExit.getLocation ()) {
			drawStraight (g, X, Y, aEnter, aHex, aBaseColor);
		} else if (((aEnter.getLocation () + 1) == aExit.getLocation ())
				|| ((aEnter.getLocation () == 0) && (aExit.getLocation () == 5))) {
			drawSharpTurn (g, X, Y, aEnter, aExit, aHex, aBaseColor);
		} else {
			drawGentleTurn (g, X, Y, aEnter, aExit, aHex, aBaseColor);
		}
	}

	public void drawTrackToCity (Graphics g, int X, int Y, int aTileOrient, Hex aHex, Paint aBaseColor) {
		int tStartX = 0;
		int tStartY = 0;
		int tStopX = 0;
		int tStopY = 0;
		int tStartSliceNum = 0;
		int tStopSliceNum = 0;
		int tempEnter, tempExit;
		Location tEnter, tExit, tSwap;
		Point displace;
		int tDraw = 0;
		Shape tPreviousClip;
		Polygon tHexPolygon;
		Area tNewClip;
		Area tHexClip;

		tEnter = enter.rotateLocation (aTileOrient);
		tExit = exit.rotateLocation (aTileOrient);
		tempEnter = tEnter.getLocation ();
		tempExit = tExit.getLocation ();
		if (tempEnter > tempExit) {
			tEnter.setValue (tempExit);
			tExit.setValue (tempEnter);
		}
		tDraw = 1;
		if (tExit.isCenterLocation ()) {
			tStopX = X;
			tStopY = Y;
		} else {
			displace = tExit.calcCenter (aHex);
			if (tExit.isClose (tEnter)) {
				tStopX = X + displace.x;
				tStopY = Y + displace.y;
			} else if (tExit.isFarOpposite (tEnter)) {
				tStopX = X + displace.x;
				tStopY = Y + displace.y;
			} else if (tExit.isAdjacent (tEnter)) {
				tStopX = X + displace.x;
				tStopY = Y + displace.y;
			} else if (tExit.isAdjacentForward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 1);
				tStartSliceNum = (tEnter.getLocation () * 2) % 12;
				tStopSliceNum = (tStartSliceNum + 1) % 12;
				tDraw = 2;
			} else if (tExit.isAdjacentBackward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 5);
				tStartSliceNum = (tEnter.getLocation () * 2) % 12;
				tStopSliceNum = (tStartSliceNum + 1) % 12;
				tDraw = 2;
			} else if (tExit.isForward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 2);
				tStartSliceNum = (tEnter.getLocation () * 2) % 12;
				tStopSliceNum = (tStartSliceNum + 2) % 12;
				tDraw = 3;
			} else if (tExit.isBackward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 4);
				tStopSliceNum = (tEnter.getLocation () * 2 + 1) % 12;
				tStartSliceNum = (tStopSliceNum + 10) % 12;
				tDraw = 3;
			} else if (tExit.isAdjacentFarForward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 2);
				tStartSliceNum = tEnter.getLocation () + 13;
				tStopSliceNum = tStartSliceNum;
				tDraw = 3;
			} else if (tExit.isAdjacentFarBackward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 4);
				tStartSliceNum = tEnter.getLocation () + 13;
				tStopSliceNum = tStartSliceNum;
				tDraw = 3;
			} else if (tExit.isFarAdjacentForward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 2);
				tStartSliceNum = ((tEnter.getLocation () + 2) % 6) + 19;
				tStopSliceNum = tStartSliceNum;
				tDraw = 4;
			} else if (tExit.isFarAdjacentBackward (tEnter)) {
				tExit = tEnter.rotateLocation ((int) 4);
				tStartSliceNum = ((tEnter.getLocation () + 4) % 6) + 19;
				tStopSliceNum = tStartSliceNum;
				tDraw = 4;
			} else if (!tExit.isSide () && !tEnter.isSide ()) {
				// If neither the Enter or Exit are sides, (city to city), draw straight segment
				// between
				displace = tEnter.calcCenter (aHex);
				tStartX = X + displace.x;
				tStartY = Y + displace.y;
				displace = tExit.calcCenter (aHex);
				tStopX = X + displace.x;
				tStopY = Y + displace.y;
				tDraw = 1;
			} else {
				if (tExit.isDeadEnd ()) {
					drawDeadEnd (g, X, Y, tEnter, aHex, aBaseColor);
					tDraw = 0;
				} else {
					// If nothing else above matches, draw just a straight Track Segment between
					// Locations.
					displace = tEnter.calcCenter (aHex);
					tStartX = X + displace.x;
					tStartY = Y + displace.y;
					displace = tExit.calcCenter (aHex);
					tStopX = X + displace.x;
					tStopY = Y + displace.y;
					tDraw = 1;

				}
			}
		}
		if (tDraw > 0) {
			if (tEnter.isSide ()) {
				tStartX = aHex.midpointX (tEnter.getLocation ());
				tStartY = aHex.midpointY (tEnter.getLocation ());
			} else {
				tDraw = 1;
			}
		}
		if (tEnter.GreaterThan (tExit)) {
			tSwap = tEnter;
			tEnter = tExit;
			tExit = tSwap;
		}
		switch (tDraw) {
		case (0):
			break;

		case (1):
			drawStraightSegment (g, tStartX, tStartY, tStopX, tStopY, aHex, aBaseColor);
			break;

		case (2):
			tPreviousClip = g.getClip ();
			tHexPolygon = aHex.getHexSlicesPolygon (tStartSliceNum, tStopSliceNum);
			tNewClip = new Area (tPreviousClip);
			tHexClip = new Area (tHexPolygon);
			tNewClip.intersect (tHexClip);
			g.setClip (tNewClip);
			drawSharpTurn (g, X, Y, tEnter, tExit, aHex, aBaseColor);
			g.setClip (tPreviousClip);
			break;

		case (3):
		case (4):
			tPreviousClip = g.getClip ();
			tHexPolygon = aHex.getHexSlicesPolygon (tStartSliceNum, tStopSliceNum);
			tNewClip = new Area (tPreviousClip);
			tHexClip = new Area (tHexPolygon);
			tNewClip.intersect (tHexClip);
			g.setClip (tNewClip);
			drawGentleTurn (g, X, Y, tEnter, tExit, aHex, aBaseColor);
			g.setClip (tPreviousClip);
			break;
		}
	}

	public Location getCityLocation () {
		Location aLocation = new Location ();

		if (enter.isSide ()) {
			if (exit.isSide ()) {

			} else {
				aLocation = exit;
			}
		} else {
			aLocation = enter;
		}

		return aLocation;
	}

	public Paint getColor (int aTrainNumber) {
		Paint color;

		switch (aTrainNumber) {
		case (1):
			color = Color.green;
			break;

		case (2):
			color = Color.orange;
			break;

		case (3):
			color = Color.red;
			break;

		case (4):
			color = Color.magenta;
			break;

		case (5):
			color = Color.cyan;
			break;

		case (6):
			color = Color.pink;
			break;

		default:
			color = Color.black;
			break;
		}

		return color;
	}

	public Paint getColor (Paint aBaseColor) {
		if (gauge.isOverpass () || gauge.isFerryBase ()) {
			return aBaseColor;
		} else {
			if (trainNumberUsing == NO_TRAIN) {
				return gauge.getColor ();
			} else {
				return (getColor (trainNumberUsing));
			}
		}
	}

	public int getEnterLocationInt () {
		return enter.getLocation ();
	}

	public int getExitLocationInt () {
		return exit.getLocation ();
	}

	public Location getEnterLocation () {
		return enter;
	}

	public Location getExitLocation () {
		return exit;
	}

	public Gauge getGauge () {
		return gauge;
	}

	public String getGaugeName () {
		return (gauge.getName ());
	}

	public BasicStroke getTrackStroke (Hex aHex) {
		BasicStroke tTrackStroke;
		boolean isDashed;
		float tTrackWidth = (float) aHex.getTrackWidth ();
		float dash1[] = { 4.0f };

		isDashed = gauge.isDashed ();
		if (gauge.isOverpass ()) {
			tTrackWidth += 4.0;
		}
		if (isDashed) {
			tTrackStroke = new BasicStroke (tTrackWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1,
					0.0f);
		} else {
			tTrackStroke = new BasicStroke (tTrackWidth);
		}

		return tTrackStroke;
	}

	/**
	 * Get the Current Train Number using this Track Segment
	 * 
	 * @return The Train Number using this Track
	 */
	public int getTrainNumber () {
		return (trainNumberUsing);
	}

	public boolean isTrackToSide (int aSide) {
		boolean tIsEnterSide;
		boolean tIsExitSide;

		tIsEnterSide = enter.isSide (aSide);
		if (tIsEnterSide) {
			return true;
		} else {
			tIsExitSide = exit.isSide (aSide);
			return tIsExitSide;
		}
	}

	public void printlog () {
		System.out.print ("Enter ");
		enter.printlog ();
		System.out.print ("Exit ");
		exit.printlog ();
		gauge.printlog ();
		System.out.println ("Train Number Using " + trainNumberUsing);
	}

	public void setTrainNumber (int aTrainNumber) {
		trainNumberUsing = aTrainNumber;
	}

	public void setValues (int aEnter, int aExit, Gauge aGauge) {
		enter = new Location (aEnter);
		exit = new Location (aExit);
		gauge = aGauge;
		trainNumberUsing = NO_TRAIN;
	}

	public boolean isTrackUsed () {
		return (trainNumberUsing > 0);
	}

	public boolean startsAt (Location aStartLocation) {
		boolean tStartsAt = false;

		if (aStartLocation.getLocation () == enter.getLocation ()) {
			tStartsAt = true;

		}
		return tStartsAt;
	}

	public boolean endsAt (Location aStartLocation) {
		boolean tEndsAt = false;

		if (aStartLocation.getLocation () == exit.getLocation ()) {
			tEndsAt = true;

		}
		return tEndsAt;
	}
}
