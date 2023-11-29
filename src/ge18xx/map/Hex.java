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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class Hex {
	public static final Hex NO_HEX = null;
	public static final String DIRECTION_NS = "NS";
	public static final String DIRECTION_EW = "EW";
	static final int NOT_VALID_SLICE = -9999;
	static final int NOT_VALID_POINT = -9998;
	static boolean direction = false;
	static int DEFAULT_SCALE = 8;
	static int DEFAULT_WIDTH = 5;
	static int scale = DEFAULT_SCALE;
	static int width = DEFAULT_WIDTH;
	int x[];
	int y[];
	private int Xt;
	private int Yt;
	private int displaceUpDown;
	private int displaceLeftRight;
	private int intDWidth;
	int Xc;
	int Yc;
	int cityWidth;
	int trackWidth;
	Polygon hexPolygon;
	static double dwidth;
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

	public Shape clipToHex (Graphics g, int Xo, int Yo) {
		int xp[];
		int yp[];
		int npnts = x.length;
		int index;
		Polygon tClipPolygon;
		Shape tPreviousClip;
		Area tNewClip;
		Area tHexClip;

		xp = new int [npnts];
		yp = new int [npnts];
		for (index = 0; index < npnts; index++) {
			xp [index] = x [index] + Xo;
			yp [index] = y [index] + Yo;
		}
		tClipPolygon = new Polygon (xp, yp, npnts);
		tPreviousClip = g.getClip ();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tClipPolygon);
		tNewClip.intersect (tHexClip);
		g.setClip (tNewClip);

		return tPreviousClip;
	}

	public boolean contains (Point2D.Double point, int aXc, int aYc) {
		Polygon offsetHexPolygon = buildOffsetPolygon (aXc, aYc);

		return (offsetHexPolygon.contains (point));
	}

	public boolean contains (Point point, int aXc, int aYc) {
		Polygon offsetHexPolygon = buildOffsetPolygon (aXc, aYc);

		return (offsetHexPolygon.contains (point));
	}

	private Polygon buildOffsetPolygon (int aXc, int aYc) {
		int xp[];
		int yp[];
		int npnts = x.length;
		int index;
		Polygon offsetHexPolygon;

		xp = new int [npnts];
		yp = new int [npnts];
		for (index = 0; index < npnts; index++) {
			xp [index] = x [index] + aXc;
			yp [index] = y [index] + aYc;
		}
		offsetHexPolygon = new Polygon (xp, yp, npnts);

		return offsetHexPolygon;
	}

	public boolean contains (Point2D.Double point) {
		return (hexPolygon.contains (point));
	}

	public boolean contains (Point point) {
		return (hexPolygon.contains (point));
	}

	public void drawBorders (Graphics g, int Xo, int Yo, boolean drawBorder, boolean aBlockedSides []) {
		int xp[];
		int yp[];
		int npnts = x.length;
		int index;
		Polygon tClipPolygon;
		Area tNewClip;
		Area tHexClip;
		Stroke tCurrentStroke;
		BasicStroke tBlockedStroke;
		Shape tPreviousClip;
		Graphics2D g2d = (Graphics2D) g;

		xp = new int [npnts];
		yp = new int [npnts];
		for (index = 0; index < npnts; index++) {
			xp [index] = x [index] + Xo;
			yp [index] = y [index] + Yo;
		}
		tClipPolygon = new Polygon (xp, yp, npnts);
		tPreviousClip = g.getClip ();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tClipPolygon);
		tNewClip.intersect (tHexClip);
		g.setClip (tNewClip);
		if (aBlockedSides != null) {
			g.setColor (Color.blue);
			tCurrentStroke = g2d.getStroke ();
			tBlockedStroke = new BasicStroke (trackWidth * 2);
			g2d.setStroke (tBlockedStroke);
			for (index = 0; index < npnts - 1; index++) {
				if (aBlockedSides [index]) {
					g.drawLine (xp [index], yp [index], xp [index + 1], yp [index + 1]);
				}
			}
			g2d.setStroke (tCurrentStroke);
		}
		if (drawBorder) {
			g.setColor (Color.black);
			g.drawPolygon (xp, yp, npnts);
		}
		g.setClip (tPreviousClip);
	}

	public void drawCoast (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawDeepCoast (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawHill (Graphics g, int Xc, int Yc, Paint aFillPaint) {
		double tDwidth6;
		double tDwidth5;
		int X1, X2, X3, Y1, Y2, Y3;

		tDwidth5 = dwidth / 6;
		tDwidth6 = dwidth / 10;
		X1 = Double.valueOf (-tDwidth5).intValue () + Xc;
		X2 = Xc;
		X3 = Double.valueOf (tDwidth5).intValue () + Xc;
		Y1 = Double.valueOf (tDwidth6).intValue () + Yc;
		Y2 = Double.valueOf (-tDwidth6).intValue () + Yc;
		Y3 = Y1;
		drawTriangle (g, X1, Y1, X2, Y2, X3, Y3, aFillPaint);
	}

	public void drawHimalaya (Graphics g, int Xc, int Yc, Paint aFillPaint) {
		int X1, X2, Y1, Y2;
		double dw6, dw5;

		dw5 = dwidth / 5;
		dw6 = dwidth / 6;
		X1 = Double.valueOf (-dw5).intValue () + Xc;
		Y1 = Double.valueOf (-dw6 / 2).intValue () + Yc;
		X2 = Double.valueOf (dw5).intValue () + Xc;
		Y2 = Y1;
		drawHill (g, X1, Y1, aFillPaint);
		drawHill (g, X2, Y2, aFillPaint);
		drawMountain (g, Xc, Yc, aFillPaint);
	}

	public void drawLargeRiver (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawMajorRiver (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawMountain (Graphics g, int Xc, int Yc, Paint aFillPaint) {
		double dw6, dw5;
		int X1, X2, X3, Y1, Y2, Y3;

		dw5 = dwidth / 5;
		dw6 = dwidth / 6;
		X1 = Double.valueOf (-dw5).intValue () + Xc;
		X2 = Xc;
		X3 = Double.valueOf (dw5).intValue () + Xc;
		Y1 = Double.valueOf (dw6).intValue () + Yc;
		Y2 = Double.valueOf (-dw6).intValue () + Yc;
		Y3 = Y1;
		drawTriangle (g, X1, Y1, X2, Y2, X3, Y3, aFillPaint);
	}

	public void drawMultipleRiver (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
		Y1 = Yc + trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawNeighbor (Graphics g, int aSide, int Xo, int Yo) {
		int X1, Y1, X2, Y2, X3, Y3;
		Shape tPreviousClip;

		tPreviousClip = clipToHex (g, Xo, Yo);
		X1 = x [aSide] + Xo;
		Y1 = y [aSide] + Yo;
		X2 = midpointX (aSide + 12) + Xo;
		Y2 = midpointY (aSide + 12) + Yo;
		X3 = x [(aSide + 1) % 6] + Xo;
		Y3 = y [(aSide + 1) % 6] + Yo;
		g.setColor (Color.green);
		g.drawLine (X1, Y1, X2, Y2);
		g.drawLine (X2, Y2, X3, Y3);
		g.setColor (Color.black);
		g.setClip (tPreviousClip);
	}
	
	public void drawCattle (Graphics g, int Xc, int Yc, Paint aCattlePaint) {
		// Draw a Cattle
	}
	
	public void drawLicenseToken (Graphics aGraphics, int Xc, int Yc, Paint aPaint, int aBenefitValue) {
		Graphics2D tGraphics2D;
		int x1;
		int y1;
		int tWidth;
		int tHeight;
		String tLabel;
		Font tCurrentFont;
		
		tGraphics2D = (Graphics2D) aGraphics;
		tCurrentFont = setRevenueFont (tGraphics2D);
		tLabel = "+" + aBenefitValue;
		tWidth = tGraphics2D.getFontMetrics ().stringWidth (tLabel) + 2;
		tHeight = tGraphics2D.getFontMetrics ().getHeight () + 2;
		x1 = Xc - tWidth/2;
		y1 = Yc - tHeight/2;
		tGraphics2D.setPaint (Color.white);
		tGraphics2D.fillRect (x1, y1, tWidth, tHeight);
		tGraphics2D.setPaint (aPaint);
		tGraphics2D.drawRect (x1, y1, tWidth, tHeight);
		x1 = Xc - tWidth/2;
		y1 = Yc + tHeight/2 - 2;

		tGraphics2D.drawString (tLabel, x1, y1);
		tGraphics2D.setFont (tCurrentFont);
	}

	private Font setRevenueFont (Graphics2D aGraphics2D) {
		Font tNewFont;
		Font tCurrentFont;
		
		tCurrentFont = aGraphics2D.getFont ();
		tNewFont = new Font ("Dialog", Font.PLAIN, 10);
		aGraphics2D.setFont (tNewFont);
		
		return tCurrentFont;
	}

	public void drawPort (Graphics g, int Xc, int Yc, Paint aPortPaint) {
		Graphics2D g2d = (Graphics2D) g;
		int x1, y1, x2, y2;
		int xtr, ytr, width, height;

		x1 = Xc;
		y1 = Double.valueOf (Yc - trackWidth * .5).intValue ();
		x2 = Xc;
		y2 = Double.valueOf (Yc + trackWidth * 2).intValue ();
		g2d.setPaint (aPortPaint);
		g2d.drawLine (x1, y1, x2, y2);
		width = trackWidth;
		height = width;
		xtr = Double.valueOf (x1 - width / 2).intValue ();
		ytr = Double.valueOf (y1 - width).intValue ();
		g2d.drawOval (xtr, ytr, width, height);
		xtr = x1 - trackWidth * 2;
		width = trackWidth * 4;
		height = Double.valueOf (trackWidth * 3.5).intValue ();
		x1 = Xc - trackWidth;
		y1 = Yc;
		x2 = Xc + trackWidth;
		y2 = Yc;
		g2d.drawLine (x1, y1, x2, y2);
		g2d.drawArc (xtr, ytr, width, height, 220, 100);
	}

	public void drawOctagon (Graphics g, int Xc, int Yc, Paint aPaint) {
		Polygon polygon = new Polygon ();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Stroke tOriginalStroke = g2d.getStroke ();
		Stroke tNewStroke = new BasicStroke (3);
		int R;

		R = cityWidth * 2;
		int [] [] xy = getPolygonArrays (Xc, Yc, R, 8);
		polygon = new Polygon (xy [0], xy [1], 8);
		g2d.setPaint (aPaint);
		g2d.setStroke (tNewStroke);
		g2d.draw (polygon);
		g2d.setStroke (tOriginalStroke);
	}

	private int [] [] getPolygonArrays (int cx, int cy, int R, int sides) {
		int [] x = new int [sides];
		int [] y = new int [sides];
		double thetaInc = 2 * Math.PI / sides;
		double theta = (sides % 2 == 0) ? thetaInc : -Math.PI / 2;

		theta += Math.PI / 8;
		for (int j = 0; j < sides; j++) {
			x [j] = (int) (cx + R * Math.cos (theta));
			y [j] = (int) (cy + R * Math.sin (theta));
			theta += thetaInc;
		}

		return new int [] [] { x, y };
	}

	public void drawRiver (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1, width, height, index;
		Graphics2D g2d = (Graphics2D) g;
		int halfTW = Double.valueOf (trackWidth / 2).intValue ();
		BasicStroke tRiverStroke = new BasicStroke (2);
		Stroke tCurrentStroke = g2d.getStroke ();

		width = trackWidth - 1;
		height = trackWidth - 1;
		X1 = Xc - halfTW - trackWidth - trackWidth;
		Y1 = Yc - trackWidth;
		g2d.setStroke (tRiverStroke);
		g2d.setPaint (aRiverPaint);
		for (index = 0; index < 3; index++) {
			g.drawArc (X1, Y1, width, height, 10, 160);
			X1 = X1 + trackWidth;
			g.drawArc (X1, Y1 - 1, width, height, 190, 160);
			X1 = X1 + trackWidth;
		}
		g.setColor (Color.black);
		g2d.setStroke (tCurrentStroke);
	}

	public void drawRotateRightArrow (Graphics g, int Xc, int Yc) {
		int x1, y1, width, height;
		int x2, y2, x3, y3, x4, y4;
		int tCircleRadius;
		int xArrowCenter, yArrowCenter;

		width = trackWidth * 3;
		tCircleRadius = width / 2;
		height = width;
		if (direction) {
			xArrowCenter = Xc + (x [2] + x [3]) / 2 + width;
			yArrowCenter = Yc - intDWidth;
		} else {
			xArrowCenter = Xc + (x [2] + x [3]) / 2 + width;
			yArrowCenter = Yc - (y [2] + y [3]) / 2 - height;
		}
		x1 = xArrowCenter - tCircleRadius;
		y1 = yArrowCenter - tCircleRadius;
		g.setColor (Color.black);
		g.drawArc (x1, y1, width, height, 180, -270);
		x2 = xArrowCenter;
		y2 = yArrowCenter + tCircleRadius;
		x3 = x2 + trackWidth / 2;
		y3 = y2 - trackWidth;
		g.drawLine (x2, y2, x3, y3);
		x4 = x3 + trackWidth;
		y4 = y2;
		g.drawLine (x2, y2, x4, y4);
	}

	public void drawShallowCoast (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	public void drawSmallRiver (Graphics g, int Xc, int Yc, Paint aRiverPaint) {
		int X1, Y1;

		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverPaint);
	}

	private void drawTriangle (Graphics g, int X1, int Y1, int X2, int Y2, int X3, int Y3, Paint aFillPaint) {
		Graphics2D g2d = (Graphics2D) g;
		Polygon tTriangle;
		int xp[] = new int [4];
		int yp[] = new int [4];

		xp [0] = X1;
		yp [0] = Y1;
		xp [1] = X2;
		yp [1] = Y2;
		xp [2] = X3;
		yp [2] = Y3;
		xp [3] = X1;
		yp [3] = Y1;
		tTriangle = new Polygon (xp, yp, 4);
		if (aFillPaint != null) {
			g2d.setPaint (aFillPaint);
			g2d.fillPolygon (tTriangle);
		}
		g.setColor (Color.black);
		g.drawPolygon (tTriangle);
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
		int retX = NOT_VALID_POINT;
		int actualSliceNum = aSliceNum;
		int p1, p2, s1, s2;

		if ((actualSliceNum >= 0) && (actualSliceNum < 12)) {
			if ((aPointNum >= 0) && (aPointNum < 4)) {
				if (aPointNum == 1) {
					retX = Xc;
				} else if (aPointNum == 2) {
					switch (actualSliceNum) {
					case (0):
						retX = midpointX (0);
						break;

					case (6):
						retX = midpointX (3);
						break;

					case (1):
						retX = x [1];
						break;

					case (5):
						retX = x [3];
						break;

					case (2):
						retX = midpointX (1);
						break;

					case (4):
						retX = midpointX (2);
						break;

					case (3):
						retX = x [2];
						break;

					case (7):
						retX = x [4];
						break;

					case (11):
						retX = x [0];
						break;

					case (8):
						retX = midpointX (4);
						break;

					case (10):
						retX = midpointX (5);
						break;

					case (9):
						retX = x [5];
						break;
					}
				} else {
					switch (actualSliceNum) {
					case (0):
						retX = x [0];
						break;

					case (8):
						retX = x [4];
						break;

					case (1):
						retX = midpointX (0);
						break;

					case (7):
						retX = midpointX (3);
						break;

					case (2):
						retX = x [1];
						break;

					case (6):
						retX = x [3];
						break;

					case (4):
						retX = x [2];
						break;

					case (3):
						retX = midpointX (1);
						break;

					case (5):
						retX = midpointX (2);
						break;

					case (10):
						retX = x [5];
						break;

					case (9):
						retX = midpointX (4);
						break;

					case (11):
						retX = midpointX (5);
						break;
					}

				}
			} else {
				retX = NOT_VALID_POINT;
			}
		} else {
			if ((actualSliceNum > 12) && (actualSliceNum <= 18)) {
				p1 = 0;
				p2 = 0;
				s1 = 0;
				s2 = 0;
				switch (actualSliceNum) {
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
					retX = x [p1];
					break;

				case (1):
					retX = midpointX (s1);
					break;

				case (2):
					retX = midpointX (s2);
					break;

				case (3):
					retX = x [p2];
					break;

				default:
					retX = NOT_VALID_POINT;
					break;
				}
			} else {
				if ((actualSliceNum > 18) && (actualSliceNum <= 24)) {
					p1 = 0;
					p2 = 0;
					s1 = 0;
					s2 = 0;
					switch (actualSliceNum) {
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
						retX = x [(p1 + aPointNum) % 6];
						break;

					case (6):
						retX = x [p1];
						break;

					case (4):
						retX = midpointX (s2);
						break;

					case (5):
						retX = midpointX (s1);
						break;

					default:
						retX = NOT_VALID_POINT;
						break;
					}
				} else {
					retX = NOT_VALID_SLICE;
				}
			}
		}

		return retX;
	}

	public int getHexSlicePointY (int aSliceNum, int aPointNum) {
		int retY = NOT_VALID_POINT;
		int actualSliceNum = aSliceNum;
		int p1, p2, s1, s2;

		if ((actualSliceNum >= 0) && (actualSliceNum < 12)) {
			if ((aPointNum >= 0) && (aPointNum < 4)) {
				if (aPointNum == 1) {
					retY = Yc;
				} else {
					switch (actualSliceNum) {
					case (0):
						if (aPointNum == 2) {
							retY = midpointY (0);
						} else {
							retY = y [0];
						}
						break;

					case (1):
						if (aPointNum == 2) {
							retY = y [1];
						} else {
							retY = midpointY (0);
						}
						break;

					case (2):
						if (aPointNum == 2) {
							retY = midpointY (1);
						} else {
							retY = y [1];
						}
						break;

					case (3):
						if (aPointNum == 2) {
							retY = y [2];
						} else {
							retY = midpointY (1);
						}
						break;

					case (4):
						if (aPointNum == 2) {
							retY = midpointY (2);
						} else {
							retY = y [2];
						}
						break;

					case (5):
						if (aPointNum == 2) {
							retY = y [3];
						} else {
							retY = midpointY (2);
						}
						break;

					case (6):
						if (aPointNum == 2) {
							retY = midpointY (3);
						} else {
							retY = y [3];
						}
						break;

					case (7):
						if (aPointNum == 2) {
							retY = y [4];
						} else {
							retY = midpointY (3);
						}
						break;

					case (8):
						if (aPointNum == 2) {
							retY = midpointY (4);
						} else {
							retY = y [4];
						}
						break;

					case (9):
						if (aPointNum == 2) {
							retY = y [5];
						} else {
							retY = midpointY (4);
						}
						break;

					case (10):
						if (aPointNum == 2) {
							retY = midpointY (5);
						} else {
							retY = y [5];
						}
						break;

					case (11):
						if (aPointNum == 2) {
							retY = y [0];
						} else {
							retY = midpointY (5);
						}
						break;
					}
				}
			} else {
				retY = NOT_VALID_POINT;
			}
		} else {
			if ((actualSliceNum > 12) && (actualSliceNum <= 18)) {
				p1 = 0;
				p2 = 0;
				s1 = 0;
				s2 = 0;
				switch (actualSliceNum) {
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
					retY = y [p1];
					break;

				case (1):
					retY = midpointY (s1);
					break;

				case (2):
					retY = midpointY (s2);
					break;

				case (3):
					retY = y [p2];
					break;

				default:
					retY = NOT_VALID_POINT;
					break;
				}
			} else {
				if ((actualSliceNum > 18) && (actualSliceNum <= 24)) {
					p1 = 0;
					p2 = 0;
					s1 = 0;
					s2 = 0;
					switch (actualSliceNum) {
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
						retY = y [(p1 + aPointNum) % 6];
						break;

					case (6):
						retY = y [p1];
						break;

					case (4):
						retY = midpointY (s2);
						break;

					case (5):
						retY = midpointY (s1);
						break;

					default:
						retY = NOT_VALID_POINT;
						break;
					}
				} else {
					retY = NOT_VALID_SLICE;
				}
			}
		}
		return retY;
	}

	public Polygon getHexSlicesPolygon (int aStartSlice, int aEndSlice) {
		int sliceX[], sliceY[], pointCount;
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
		return (trackWidth);
	}

	public static int getWidth () {
		return (Double.valueOf (dwidth).intValue ());
	}

	public int getXd () {
		return (displaceUpDown);
	}

	public int getYd () {
		return (displaceLeftRight);
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

	public void paintHex (Graphics g, int Xo, int Yo, Paint aFillPaint, boolean aDrawBorder, Paint aThickFrame,
			boolean aBlockedSides[]) {
		Stroke tCurrentStroke;
		BasicStroke tFrameStroke;
		Shape tPreviousClip;
		Graphics2D g2d = (Graphics2D) g;
		int xp[], yp[], npnts = x.length, index;

		tPreviousClip = clipToHex (g, Xo, Yo);
		xp = new int [npnts];
		yp = new int [npnts];
		for (index = 0; index < npnts; index++) {
			xp [index] = x [index] + Xo;
			yp [index] = y [index] + Yo;
		}

		g2d.setPaint (aFillPaint);
		try {
			g.fillPolygon (xp, yp, npnts - 1);
		} catch (ArrayIndexOutOfBoundsException exc) {
			System.err.println ("Oops, trying to fill polygon at " + Xo + ", " + Yo + ". Sorry");
		}
		if (aThickFrame != null) {
			g2d.setPaint (aThickFrame);
			tCurrentStroke = g2d.getStroke ();
			tFrameStroke = new BasicStroke (trackWidth * 2);
			g2d.setStroke (tFrameStroke);
			g2d.drawPolygon (xp, yp, npnts);
			g2d.setStroke (tCurrentStroke);
		}
		drawBorders (g, Xo, Yo, aDrawBorder, aBlockedSides);
		g.setClip (tPreviousClip);
	}

	public void paintSelected (Graphics g, int Xo, int Yo) {
		Shape tPreviousClip;
		int xp[], yp[], npnts = x.length, index;

		xp = new int [npnts];
		yp = new int [npnts];
		for (index = 0; index < npnts; index++) {
			xp [index] = x [index] + Xo;
			yp [index] = y [index] + Yo;
		}

		tPreviousClip = clipToHex (g, Xo, Yo);
		g.setColor (Color.red);
		g.drawPolygon (xp, yp, npnts);
		g.drawLine (xp [0], yp [0], xp [3], yp [3]);
		g.drawLine (xp [1], yp [1], xp [4], yp [4]);
		g.drawLine (xp [2], yp [2], xp [5], yp [5]);
		g.setClip (tPreviousClip);
	}

	public int rightEdgeDisplacement () {
		return getMaxX ();
	}

	public boolean rotateArrowContainingPoint (Point aPoint, int Xc, int Yc) {
		int x1, y1, width, height;
		int tCircleRadius;
		int xArrowCenter, yArrowCenter;
		Rectangle tRect;

		width = trackWidth * 3;
		tCircleRadius = width / 2;
		height = width;
		if (direction) {
			xArrowCenter = Xc + (x [2] + x [3]) / 2 + width;
			yArrowCenter = Yc - intDWidth;
		} else {
			xArrowCenter = Xc + (x [2] + x [3]) / 2 + width;
			yArrowCenter = Yc - (y [2] + y [3]) / 2 - height;
		}
		x1 = xArrowCenter - tCircleRadius;
		y1 = yArrowCenter - tCircleRadius;
		tRect = new Rectangle (x1, y1, width, height);

		return tRect.contains (aPoint.getX (), aPoint.getY ());
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
		int rectX, rectY, rectWidth, rectHeight;

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
