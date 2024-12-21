package ge18xx.map;

//
//  Hex.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class Hex {
	public static final Hex NO_HEX = null;
	public static final String DIRECTION_NS = "NS";
	public static final String DIRECTION_EW = "EW";
	public static final int NOT_VALID_SLICE = -9999;
	public static final int NOT_VALID_POINT = -9998;
	public static int DEFAULT_SCALE = 8;
	public static int DEFAULT_WIDTH = 5;
	public static int scale = DEFAULT_SCALE;
	public static int width = DEFAULT_WIDTH;
	protected static boolean direction = false;
	protected int x [];
	protected int y [];
	private int Xt;
	private int Yt;
	private int displaceUpDown;
	private int displaceLeftRight;
	protected int intDWidth;
	int Xc;
	int Yc;
	protected int cityWidth;
	protected int trackWidth;
	protected static double dwidth;
	Polygon hexPolygon;
	Rectangle rectBounds;

	public Hex () {
		this (direction);
	}

	public Hex (boolean aHexDirection) {
		this (0, 0, aHexDirection);
	}

	public Hex (int offsetX, int offsetY, boolean aHexDirection) {
		this (offsetX, offsetY, aHexDirection, DEFAULT_SCALE);
	}

	public Hex (int offsetX, int offsetY, boolean aHexDirection, int aScale) {
		setDirection (aHexDirection);
		setScaleAndSize (aScale, offsetX, offsetY);
	}

	public int bottomEdgeDisplacement () {
		return getMaxY ();
	}

	public int getXt () {
		return Xt;
	}

	public int getYt () {
		return Yt;
	}

	public int getDisplaceUpDown () {
		return displaceUpDown;
	}

	public int getDisplaceLeftRight () {
		return displaceLeftRight;
	}

	public int getIntDWidth () {
		return intDWidth;
	}

	public void clipToHex (Graphics aGraphics, int aXo, int aYo) {
		Polygon tClipPolygon;
		Shape tPreviousClip;
		Area tNewClip;
		Area tHexClip;
		
		tClipPolygon = buildOffsetPolygon (aXo, aYo);
		
		tPreviousClip = aGraphics.getClip ();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tClipPolygon);
		tNewClip.intersect (tHexClip);
		aGraphics.setClip (tNewClip);

//		return tPreviousClip;
	}

	public boolean contains (Point2D.Double aPoint, int aXc, int aYc) {
		Polygon offsetHexPolygon;

		offsetHexPolygon = buildOffsetPolygon (aXc, aYc);
		
		return (offsetHexPolygon.contains (aPoint));
	}

	public boolean contains (Point aPoint, int aXc, int aYc) {
		Polygon offsetHexPolygon;

		offsetHexPolygon = buildOffsetPolygon (aXc, aYc);
		
		return (offsetHexPolygon.contains (aPoint));
	}

	private Polygon buildOffsetPolygon (int aXc, int aYc) {
		int xp [];
		int yp [];
		int tPointCount;
		int tIndex;
		Polygon tOffsetHexPolygon;

		tPointCount = x.length;
		xp = new int [tPointCount];
		yp = new int [tPointCount];
		for (tIndex = 0; tIndex < tPointCount; tIndex++) {
			xp [tIndex] = x [tIndex] + aXc;
			yp [tIndex] = y [tIndex] + aYc;
		}
		tOffsetHexPolygon = new Polygon (xp, yp, tPointCount);

		return tOffsetHexPolygon;
	}

	public boolean contains (Point2D.Double aPoint) {
		return (hexPolygon.contains (aPoint));
	}

	public boolean contains (Point aPoint) {
		return (hexPolygon.contains (aPoint));
	}

	public void drawBorders (Graphics2D aGraphics2D, int aXo, int aYo, boolean aDrawBorder, 
							boolean aBlockedSides []) {
		int xp [];
		int yp [];
		int tPointCount;
		int index;
		Polygon tClipPolygon;
		Area tNewClip;
		Area tHexClip;
		Stroke tCurrentStroke;
		BasicStroke tBlockedStroke;
		Shape tPreviousClip;

		tPointCount = x.length;
		tClipPolygon = buildOffsetPolygon (aXo, aYo);
		xp = tClipPolygon.xpoints;
		yp = tClipPolygon.ypoints;
		
		tPreviousClip = aGraphics2D.getClip ();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tClipPolygon);
		tNewClip.intersect (tHexClip);
		aGraphics2D.setClip (tNewClip);
		if (aBlockedSides != null) {
			aGraphics2D.setColor (Color.blue);
			tCurrentStroke = aGraphics2D.getStroke ();
			tBlockedStroke = new BasicStroke (trackWidth * 2);
			aGraphics2D.setStroke (tBlockedStroke);
			for (index = 0; index < tPointCount - 1; index++) {
				if (aBlockedSides [index]) {
					aGraphics2D.drawLine (xp [index], yp [index], xp [index + 1], yp [index + 1]);
				}
			}
			aGraphics2D.setStroke (tCurrentStroke);
		}
		if (aDrawBorder) {
			aGraphics2D.setColor (Color.black);
			aGraphics2D.drawPolygon (tClipPolygon);
		}
		aGraphics2D.setClip (tPreviousClip);
	}

	public void drawNeighborHighlight (Graphics aGraphics, int aSide, int aXOffset, int aYOffset) {
		int X1;
		int Y1;
		int X2;
		int Y2;
		int X3;
		int Y3;

		aGraphics.setColor (Color.green);
		clipToHex (aGraphics, aXOffset, aYOffset);
		X1 = x [aSide] + aXOffset;
		Y1 = y [aSide] + aYOffset;
		X2 = midpointX (aSide + 12) + aXOffset;
		Y2 = midpointY (aSide + 12) + aYOffset;
		X3 = x [(aSide + 1) % 6] + aXOffset;
		Y3 = y [(aSide + 1) % 6] + aYOffset;
		aGraphics.drawLine (X1, Y1, X2, Y2);
		aGraphics.drawLine (X2, Y2, X3, Y3);
	}
	
	protected Font setRevenueFont (Graphics2D aGraphics2D) {
		Font tNewFont;
		Font tCurrentFont;
		
		tCurrentFont = aGraphics2D.getFont ();
		tNewFont = new Font ("Dialog", Font.PLAIN, 10);
		aGraphics2D.setFont (tNewFont);
		
		return tCurrentFont;
	}

	protected int [] [] getPolygonArrays (int aXc, int aYc, int aRadius, int aSideCount) {
		int [] x;
		int [] y;
		double thetaInc;
		double theta;

		x = new int [aSideCount];
		y = new int [aSideCount];
		thetaInc = 2 * Math.PI / aSideCount;
		theta = (aSideCount % 2 == 0) ? thetaInc : -Math.PI / 2;
		
		theta += Math.PI / 8;
		for (int j = 0; j < aSideCount; j++) {
			x [j] = (int) (aXc + aRadius * Math.cos (theta));
			y [j] = (int) (aYc + aRadius * Math.sin (theta));
			theta += thetaInc;
		}

		return new int [] [] { x, y };
	}

	public Rectangle getBounds () {
		return rectBounds;
	}

	public int getCityWidth () {
		return cityWidth;
	}

	public static boolean getDirection () {
		return direction;
	}

	public Polygon getHexPolygon () {
		return hexPolygon;
	}

	public int getHexSlicePointX (int aSliceNum, int aPointNum) {
		int tReturnX;
		int tActualSliceNum;
		int p1;
		int p2;
		int s1;
		int s2;

		tReturnX = NOT_VALID_POINT;
		tActualSliceNum = aSliceNum;
		if ((tActualSliceNum >= 0) && (tActualSliceNum < 12)) {
			if ((aPointNum >= 0) && (aPointNum < 4)) {
				if (aPointNum == 1) {
					tReturnX = Xc;
				} else if (aPointNum == 2) {
					switch (tActualSliceNum) {
					case (0):
						tReturnX = midpointX (0);
						break;

					case (6):
						tReturnX = midpointX (3);
						break;

					case (1):
						tReturnX = x [1];
						break;

					case (5):
						tReturnX = x [3];
						break;

					case (2):
						tReturnX = midpointX (1);
						break;

					case (4):
						tReturnX = midpointX (2);
						break;

					case (3):
						tReturnX = x [2];
						break;

					case (7):
						tReturnX = x [4];
						break;

					case (11):
						tReturnX = x [0];
						break;

					case (8):
						tReturnX = midpointX (4);
						break;

					case (10):
						tReturnX = midpointX (5);
						break;

					case (9):
						tReturnX = x [5];
						break;
					}
				} else {
					switch (tActualSliceNum) {
					case (0):
						tReturnX = x [0];
						break;

					case (8):
						tReturnX = x [4];
						break;

					case (1):
						tReturnX = midpointX (0);
						break;

					case (7):
						tReturnX = midpointX (3);
						break;

					case (2):
						tReturnX = x [1];
						break;

					case (6):
						tReturnX = x [3];
						break;

					case (4):
						tReturnX = x [2];
						break;

					case (3):
						tReturnX = midpointX (1);
						break;

					case (5):
						tReturnX = midpointX (2);
						break;

					case (10):
						tReturnX = x [5];
						break;

					case (9):
						tReturnX = midpointX (4);
						break;

					case (11):
						tReturnX = midpointX (5);
						break;
					}

				}
			} else {
				tReturnX = NOT_VALID_POINT;
			}
		} else {
			if ((tActualSliceNum > 12) && (tActualSliceNum <= 18)) {
				p1 = 0;
				p2 = 0;
				s1 = 0;
				s2 = 0;
				switch (tActualSliceNum) {
				case (13):
					p1 = 0;
					s1 = 5;
					s2 = 1;
					p2 = 1;
					break;
				case (14):
					p1 = 1;
					s1 = 0;
					s2 = 2;
					p2 = 2;
					break;
				case (15):
					p1 = 2;
					s1 = 1;
					s2 = 3;
					p2 = 3;
					break;
				case (16):
					p1 = 3;
					s1 = 2;
					s2 = 4;
					p2 = 4;
					break;
				case (17):
					p1 = 4;
					s1 = 3;
					s2 = 5;
					p2 = 5;
					break;
				case (18):
					p1 = 5;
					s1 = 4;
					s2 = 0;
					p2 = 0;
					break;
				}
				switch (aPointNum) {
				case (0):
				case (4):
					tReturnX = x [p1];
					break;

				case (1):
					tReturnX = midpointX (s1);
					break;

				case (2):
					tReturnX = midpointX (s2);
					break;

				case (3):
					tReturnX = x [p2];
					break;

				default:
					tReturnX = NOT_VALID_POINT;
					break;
				}
			} else {
				if ((tActualSliceNum > 18) && (tActualSliceNum <= 24)) {
					p1 = 0;
					p2 = 0;
					s1 = 0;
					s2 = 0;
					switch (tActualSliceNum) {
					case (19):
						p1 = 2;
						s1 = 1;
						s2 = 5;
						p2 = 5;
						break;
					case (20):
						p1 = 3;
						s1 = 2;
						s2 = 0;
						p2 = 0;
						break;
					case (21):
						p1 = 4;
						s1 = 3;
						s2 = 1;
						p2 = 1;
						break;
					case (22):
						p1 = 5;
						s1 = 4;
						s2 = 2;
						p2 = 2;
						break;
					case (23):
						p1 = 0;
						s1 = 5;
						s2 = 3;
						p2 = 3;
						break;
					case (24):
						p1 = 1;
						s1 = 0;
						s2 = 4;
						p2 = 4;
						break;
					}
					switch (aPointNum) {
					case (0):
					case (1):
					case (2):
					case (3):
						tReturnX = x [(p1 + aPointNum) % 6];
						break;

					case (6):
						tReturnX = x [p1];
						break;

					case (4):
						tReturnX = midpointX (s2);
						break;

					case (5):
						tReturnX = midpointX (s1);
						break;

					default:
						tReturnX = NOT_VALID_POINT;
						break;
					}
				} else {
					tReturnX = NOT_VALID_SLICE;
				}
			}
		}

		return tReturnX;
	}

	public int getHexSlicePointY (int aSliceNum, int aPointNum) {
		int tReturnY;
		int tActualSliceNum;
		int p1;
		int p2;
		int s1;
		int s2;

		tReturnY = NOT_VALID_POINT;
		tActualSliceNum = aSliceNum;
		if ((tActualSliceNum >= 0) && (tActualSliceNum < 12)) {
			if ((aPointNum >= 0) && (aPointNum < 4)) {
				if (aPointNum == 1) {
					tReturnY = Yc;
				} else {
					switch (tActualSliceNum) {
					case (0):
						if (aPointNum == 2) {
							tReturnY = midpointY (0);
						} else {
							tReturnY = y [0];
						}
						break;

					case (1):
						if (aPointNum == 2) {
							tReturnY = y [1];
						} else {
							tReturnY = midpointY (0);
						}
						break;

					case (2):
						if (aPointNum == 2) {
							tReturnY = midpointY (1);
						} else {
							tReturnY = y [1];
						}
						break;

					case (3):
						if (aPointNum == 2) {
							tReturnY = y [2];
						} else {
							tReturnY = midpointY (1);
						}
						break;

					case (4):
						if (aPointNum == 2) {
							tReturnY = midpointY (2);
						} else {
							tReturnY = y [2];
						}
						break;

					case (5):
						if (aPointNum == 2) {
							tReturnY = y [3];
						} else {
							tReturnY = midpointY (2);
						}
						break;

					case (6):
						if (aPointNum == 2) {
							tReturnY = midpointY (3);
						} else {
							tReturnY = y [3];
						}
						break;

					case (7):
						if (aPointNum == 2) {
							tReturnY = y [4];
						} else {
							tReturnY = midpointY (3);
						}
						break;

					case (8):
						if (aPointNum == 2) {
							tReturnY = midpointY (4);
						} else {
							tReturnY = y [4];
						}
						break;

					case (9):
						if (aPointNum == 2) {
							tReturnY = y [5];
						} else {
							tReturnY = midpointY (4);
						}
						break;

					case (10):
						if (aPointNum == 2) {
							tReturnY = midpointY (5);
						} else {
							tReturnY = y [5];
						}
						break;

					case (11):
						if (aPointNum == 2) {
							tReturnY = y [0];
						} else {
							tReturnY = midpointY (5);
						}
						break;
					}
				}
			} else {
				tReturnY = NOT_VALID_POINT;
			}
		} else {
			if ((tActualSliceNum > 12) && (tActualSliceNum <= 18)) {
				p1 = 0;
				p2 = 0;
				s1 = 0;
				s2 = 0;
				switch (tActualSliceNum) {
				case (13):
					p1 = 0;
					s1 = 5;
					s2 = 1;
					p2 = 1;
					break;
				case (14):
					p1 = 1;
					s1 = 0;
					s2 = 2;
					p2 = 2;
					break;
				case (15):
					p1 = 2;
					s1 = 1;
					s2 = 3;
					p2 = 3;
					break;
				case (16):
					p1 = 3;
					s1 = 2;
					s2 = 4;
					p2 = 4;
					break;
				case (17):
					p1 = 4;
					s1 = 3;
					s2 = 5;
					p2 = 5;
					break;
				case (18):
					p1 = 5;
					s1 = 4;
					s2 = 0;
					p2 = 0;
					break;
				}
				switch (aPointNum) {
				case (0):
				case (4):
					tReturnY = y [p1];
					break;

				case (1):
					tReturnY = midpointY (s1);
					break;

				case (2):
					tReturnY = midpointY (s2);
					break;

				case (3):
					tReturnY = y [p2];
					break;

				default:
					tReturnY = NOT_VALID_POINT;
					break;
				}
			} else {
				if ((tActualSliceNum > 18) && (tActualSliceNum <= 24)) {
					p1 = 0;
					p2 = 0;
					s1 = 0;
					s2 = 0;
					switch (tActualSliceNum) {
					case (19):
						p1 = 2;
						s1 = 1;
						s2 = 5;
						p2 = 5;
						break;
					case (20):
						p1 = 3;
						s1 = 2;
						s2 = 0;
						p2 = 0;
						break;
					case (21):
						p1 = 4;
						s1 = 3;
						s2 = 1;
						p2 = 1;
						break;
					case (22):
						p1 = 5;
						s1 = 4;
						s2 = 2;
						p2 = 2;
						break;
					case (23):
						p1 = 0;
						s1 = 5;
						s2 = 3;
						p2 = 3;
						break;
					case (24):
						p1 = 1;
						s1 = 0;
						s2 = 4;
						p2 = 4;
						break;
					}
					switch (aPointNum) {
					case (0):
					case (1):
					case (2):
					case (3):
						tReturnY = y [(p1 + aPointNum) % 6];
						break;

					case (6):
						tReturnY = y [p1];
						break;

					case (4):
						tReturnY = midpointY (s2);
						break;

					case (5):
						tReturnY = midpointY (s1);
						break;

					default:
						tReturnY = NOT_VALID_POINT;
						break;
					}
				} else {
					tReturnY = NOT_VALID_SLICE;
				}
			}
		}
		return tReturnY;
	}

	public Polygon getHexSlicesPolygon (int aStartSlice, int aEndSlice) {
		int sliceX[];
		int sliceY[];
		int pointCount;
		int sliceCount;
		int sliceIndex;
		int slicePointIndex;
		int maxSliceNum = aEndSlice;

		if (aStartSlice == aEndSlice) {
			sliceCount = 1;
		} else if (aStartSlice < aEndSlice) {
			sliceCount = aEndSlice - aStartSlice + 1;
		} else {
			sliceCount = aEndSlice - aStartSlice + 13;
			maxSliceNum += 12;
		}

		if (aStartSlice < 13) {
			pointCount = 3 + sliceCount;
			sliceX = new int [pointCount];
			sliceY = new int [pointCount];
			sliceX [0] = getHexSlicePointX (aStartSlice, 0);
			sliceY [0] = getHexSlicePointY (aStartSlice, 0);
			sliceX [1] = getHexSlicePointX (aStartSlice, 1);
			sliceY [1] = getHexSlicePointY (aStartSlice, 1);
			slicePointIndex = 2;
			for (sliceIndex = maxSliceNum; sliceIndex >= aStartSlice; sliceIndex--) {
				sliceX [slicePointIndex] = getHexSlicePointX (sliceIndex % 12, 2);
				sliceY [slicePointIndex] = getHexSlicePointY (sliceIndex % 12, 2);
				slicePointIndex++;
			}
			sliceX [slicePointIndex] = getHexSlicePointX (aStartSlice, 3);
			sliceY [slicePointIndex] = getHexSlicePointY (aStartSlice, 3);
		} else if (aStartSlice <= 24) {
			if (aStartSlice < 19) {
				pointCount = 5;
			} else {
				pointCount = 7;
			}
			sliceX = new int [pointCount];
			sliceY = new int [pointCount];
			for (slicePointIndex = 0; slicePointIndex < pointCount; slicePointIndex++) {
				sliceX [slicePointIndex] = getHexSlicePointX (aStartSlice, slicePointIndex);
				sliceY [slicePointIndex] = getHexSlicePointY (aStartSlice, slicePointIndex);
			}
		} else {
			pointCount = 0;
			sliceX = new int [pointCount];
			sliceY = new int [pointCount];
		}

		return (new Polygon (sliceX, sliceY, pointCount));
	}

	public int getMaxX () {
		int maxX;

		if (direction) {
			maxX = x [3];
		} else {
			maxX = x [2];
		}

		return maxX;
	}

	public int getMaxY () {
		int maxY;

		if (direction) {
			maxY = y [5];
		} else {
			maxY = y [3];
		}

		return maxY;
	}

	public int getMinX () {
		int minX;

		if (direction) {
			minX = x [0];
		} else {
			minX = x [5];
		}

		return minX;
	}

	public int getMinY () {
		int minY;

		if (direction) {
			minY = y [2];
		} else {
			minY = y [1];
		}

		return minY;
	}

	public static int getScale () {
		return scale;
	}

	public int getTrackWidth () {
		return trackWidth;
	}

	public static int getWidth () {
		return (Double.valueOf (dwidth).intValue ());
	}

	public int getXd () {
		return displaceUpDown;
	}

	public int getYd () {
		return displaceLeftRight;
	}

	public int leftEdgeDisplacment () {
		return getMinX ();
	}

	public int midpointX (int aSide) {
		int midX = 0;
		int midX1;

		if (aSide > 11) {
			midX1 = midpointX (aSide - 11);
			midX = (Xc + midX1) / 2;
		} else {
			if (direction) {
				switch (aSide) {
				case (0):
					midX = x [0];
					break;

				case (1):
				case (5):
				case (6):
				case (7):
					midX = Xc - (Xc - x [1]) / 2;
					break;

				case (2):
				case (4):
				case (9):
				case (10):
					midX = Xc + (Xc - x [1]) / 2;
					break;

				case (3):
					midX = x [3];
					break;

				case (8):
				case (11):
					midX = Xc;
					break;
				}
			} else {
				switch (aSide) {
				case (0):
				case (3):
					midX = Xc;
					break;

				case (1):
				case (2):
					midX = Xc + (intDWidth - displaceUpDown) / 2 + displaceUpDown;
					break;

				case (4):
				case (5):
					midX = Xc - (intDWidth - displaceUpDown) / 2 - displaceUpDown;
					break;

				case (8):
					midX = x [1];
					break;

				case (11):
					midX = x [0];
					break;

				case (6):
				case (10):
					midX = x [0] + (Xc - x [0]) / 2;
					break;

				case (7):
				case (9):
					midX = Xc + (Xc - x [0]) / 2;
					break;
				}
			}
		}

		return midX;
	}

	public int midpointY (int aSide) {
		int midY = 0;
		int midY1;

		if (aSide > 11) {
			midY1 = midpointY (aSide - 11);
			midY = (Yc + midY1) / 2;
		} else {
			if (direction) {
				switch (aSide) {
				case (0):
				case (3):
					midY = Yc;
					break;

				case (1):
				case (2):
					midY = (y [1] + y [2]) / 2;
					break;

				case (4):
				case (5):
					midY = (y [4] + y [5]) / 2;
					break;

				case (6):
				case (10):
					midY = (y [0] + Yc) / 2;
					break;

				case (7):
				case (9):
					midY = (y [1] + Yc) / 2;
					break;

				case (8):
					midY = (y [2] + Yc) / 2;
					break;

				case (11):
					midY = (y [5] + Yc) / 2;
					break;
				}
			} else {
				switch (aSide) {
				case (0):
					midY = y [1];
					break;

				case (3):
					midY = y [3];
					break;

				case (1):
				case (5):
				case (6):
				case (7):
					midY = Yc - displaceLeftRight / 2;
					break;

				case (2):
				case (4):
				case (9):
				case (10):
					midY = Yc + displaceLeftRight / 2;
					break;

				case (8):
				case (11):
					midY = Yc;
					break;
				}
			}
		}

		return midY;
	}

	public void offsetHex (int offsetX, int offsetY) {
		Xc = offsetX;
		Yc = offsetY;
	}

	public void paintHex (Graphics g, int Xo, int Yo, Paint aFillPaint) {
		paintHex (g, Xo, Yo, aFillPaint, true, null);
	}

	public void paintHex (Graphics g, int Xo, int Yo, Paint aFillPaint, Paint aThickFrame) {
		paintHex (g, Xo, Yo, aFillPaint, true, aThickFrame);
	}

	public void paintHex (Graphics g, int Xo, int Yo, Paint aFillPaint, boolean aDrawBorder) {
		paintHex (g, Xo, Yo, aFillPaint, aDrawBorder, null);
	}

	public void paintHex (Graphics g, int Xo, int Yo, Paint aFillPaint, boolean aDrawBorder, Paint aThickFrame) {
		paintHex (g, Xo, Yo, aFillPaint, aDrawBorder, null, null);
	}

	public void paintHex (Graphics aGraphics, int aXo, int aYo, Paint aFillPaint, boolean aDrawBorder, 
							Paint aThickFrame, boolean aBlockedSides[]) {
		Stroke tCurrentStroke;
		BasicStroke tFrameStroke;
		Shape tPreviousClip;
		Graphics2D tGraphics2D;
		Polygon tPolygon;

		tGraphics2D = (Graphics2D) aGraphics;
		
		tPreviousClip = tGraphics2D.getClip ();
		clipToHex (tGraphics2D, aXo, aYo);

		tPolygon = buildOffsetPolygon (aXo, aYo);
		
		tGraphics2D.setPaint (aFillPaint);
		try {
			tGraphics2D.fillPolygon (tPolygon);
		} catch (ArrayIndexOutOfBoundsException exc) {
			System.err.println ("Oops, trying to fill polygon at " + aXo + ", " + aYo + ". Sorry");
		}
		if (aThickFrame != null) {
			tGraphics2D.setPaint (aThickFrame);
			tCurrentStroke = tGraphics2D.getStroke ();
			tFrameStroke = new BasicStroke (trackWidth * 2);
			tGraphics2D.setStroke (tFrameStroke);
			tGraphics2D.drawPolygon (tPolygon);
			tGraphics2D.setStroke (tCurrentStroke);
		}
		drawBorders (tGraphics2D, aXo, aYo, aDrawBorder, aBlockedSides);
		tGraphics2D.setClip (tPreviousClip);
	}

	public void paintSelected (Graphics aGraphics, int aXo, int aYo) {
		Shape tPreviousClip;
		Polygon tPolygon;
		
		tPolygon = buildOffsetPolygon (aXo, aYo);
		
		tPreviousClip = aGraphics.getClip ();
		
		clipToHex (aGraphics, aXo, aYo);
		aGraphics.setColor (Color.red);
		aGraphics.drawPolygon (tPolygon);
		aGraphics.drawLine (tPolygon.xpoints[0], tPolygon.ypoints[0], tPolygon.xpoints[3], tPolygon.ypoints[3]);
		aGraphics.drawLine (tPolygon.xpoints[1], tPolygon.ypoints[1], tPolygon.xpoints[4], tPolygon.ypoints[4]);
		aGraphics.drawLine (tPolygon.xpoints[2], tPolygon.ypoints[2], tPolygon.xpoints[5], tPolygon.ypoints[5]);
		
		aGraphics.setClip (tPreviousClip);
	}

	public int rightEdgeDisplacement () {
		return getMaxX ();
	}

	public static void setStaticDirection (boolean aDirection) {
		direction = aDirection;
	}

	public void setDirection (boolean aDirection) {
		setStaticDirection (aDirection);
	}

	public void setScale (int hexScale) {
		setScaleAndSize (hexScale, 0, 0);
	}

	private void setScaleAndSize (int hexScale, int Xoffset, int Yoffset) {
		scale = hexScale;
		setSize (Xoffset, Yoffset);
	}

	public void setSize (int offsetX, int offsetY) {
		double sSixth_pi = 0.5;
		double cSixth_pi = 0.866025;
		double sThird_pi = 0.866025;
		double cThird_pi = 0.5;
		int rectX;
		int rectY;
		int rectWidth;
		int rectHeight;

		dwidth = width * scale;

		double ssp_d = sSixth_pi * dwidth;
		double csp_d = cSixth_pi * dwidth;
		double stp_d = sThird_pi * dwidth;
		double ctp_d = cThird_pi * dwidth;

		displaceUpDown = Double.valueOf (ssp_d).intValue ();
		displaceLeftRight = Double.valueOf (csp_d).intValue ();
		Xt = Double.valueOf (stp_d).intValue () - 3;
		Yt = Double.valueOf (ctp_d).intValue () - 3;
		offsetHex (offsetX, offsetY);
		intDWidth = Double.valueOf (dwidth).intValue ();

		fillXandYPoints ();
		
		cityWidth = Double.valueOf (displaceLeftRight / 2.9).intValue ();
		trackWidth = Double.valueOf (displaceLeftRight / 7.25).intValue () + 1;
		hexPolygon = buildOffsetPolygon (0, 0);
		rectX = getMinX ();
		rectY = getMinY ();
		rectWidth = getMaxX () - rectX + 1;
		rectHeight = getMaxY () - rectY + 1;
		rectBounds = new Rectangle (rectX, rectY, rectWidth, rectHeight);
	}

	private void fillXandYPoints () {
		x = new int [7];
		y = new int [7];
		if (direction) {
			x [2] = Xc;
			y [2] = Yc - intDWidth;
			x [5] = Xc;
			y [5] = Yc + intDWidth;
			x [1] = Xc - displaceLeftRight;
			y [1] = Yc - displaceUpDown;
			x [3] = Xc + displaceLeftRight;
			y [3] = y [1];
			x [4] = x [3];
			y [4] = Yc + displaceUpDown;
			x [0] = x [1];
			y [0] = y [4];
			x [6] = x [0];
			y [6] = y [0];
		} else {
			x [2] = Xc + intDWidth;
			y [2] = Yc;
			x [5] = Xc - intDWidth;
			y [5] = Yc;
			x [1] = Xc + displaceUpDown;
			y [1] = Yc - displaceLeftRight;
			x [3] = x [1];
			y [3] = Yc + displaceLeftRight;
			x [4] = Xc - displaceUpDown;
			y [4] = y [3];
			x [0] = x [4];
			y [0] = y [1];
			x [6] = x [0];
			y [6] = y [0];
		}
	}

	public int [] getXArray () {
		return x;
	}

	public int [] getYArray () {
		return y;
	}

	public int topEdgeDisplacement () {
		return getMinY ();
	}
}
