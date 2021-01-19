package ge18xx.map;

//
//  Hex.java
//  18XX_JAVA
//
//  Created by Mark Smith on 11/3/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.geom.*;

public class Hex {
	static final int NOT_VALID_SLICE = -9999;
	static final int NOT_VALID_POINT = -9998;
	static boolean direction = false;
	static int DEFAULT_SCALE = 8;
	static int DEFAULT_WIDTH = 5;
	static int scale = DEFAULT_SCALE;
	static int width = DEFAULT_WIDTH;
    int x [], y [];
    private int Xt, Yt;
	private int displaceUpDown, displaceLeftRight;
	private int intDWidth;
	int Xc, Yc;
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
		int xp [], yp [], npnts = x.length, index;
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
		int xp [], yp [], index, npnts = x.length;
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
        int xp [], yp[], npnts = x.length, index;
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
		tPreviousClip = g.getClip();
		tNewClip = new Area (tPreviousClip);
		tHexClip = new Area (tClipPolygon);
		tNewClip.intersect(tHexClip);
		g.setClip (tNewClip);
		if (aBlockedSides != null) {
			g.setColor (Color.blue);
			tCurrentStroke = g2d.getStroke ();
			tBlockedStroke = new BasicStroke (trackWidth*2);
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
			try {
				for (index = 0; index < npnts - 1; index++) {
					g.drawLine (xp [index], yp [index], xp [index + 1], yp [index + 1]);
				}
			} catch (ArrayIndexOutOfBoundsException exc) {
				System.err.println ("Oops, trying to draw lines at " + Xo + ", " + Yo + ". Sorry");
			}
		}
		g.setClip (tPreviousClip);
	}
	
	public void drawCoast (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
	public void drawDeepCoast (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
    public void drawHill (Graphics g, int Xc, int Yc, Color aFillColor) {
    		double tDwidth6, tDwidth5;
		int X1, X2, X3, Y1, Y2, Y3;
		
		tDwidth5 = dwidth/6;
		tDwidth6 = dwidth/10;
		X1 = new Double (-tDwidth5).intValue () + Xc;
		X2 = Xc;
		X3 = new Double (tDwidth5).intValue () + Xc;
		Y1 = new Double (tDwidth6).intValue () + Yc;
		Y2 = new Double (-tDwidth6).intValue () + Yc;
		Y3 = Y1;
		drawTriangle (g, X1, Y1, X2, Y2, X3, Y3, aFillColor);
	}
	
	public void drawHimalaya (Graphics g, int Xc, int Yc, Color aFillColor) {
		int X1, X2, Y1, Y2;
		double dw6, dw5;
		
		dw5 = dwidth/5;
		dw6 = dwidth/6;
		X1 = new Double (-dw5).intValue () + Xc;
		Y1 = new Double (-dw6/2).intValue () + Yc;
		X2 = new Double (dw5).intValue () + Xc;
		Y2 = Y1;
		drawHill (g, X1, Y1, aFillColor);
		drawHill (g, X2, Y2, aFillColor);
		drawMountain (g, Xc, Yc, aFillColor);
	}
	
	public void drawLargeRiver (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
	public void drawMajorRiver (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
	public void drawMountain (Graphics g, int Xc, int Yc, Color aFillColor) {
		double dw6, dw5;
		int X1, X2, X3, Y1, Y2, Y3;
		
		dw5 = dwidth/5;
		dw6 = dwidth/6;
		X1 = new Double (-dw5).intValue () + Xc;
		X2 = Xc;
		X3 = new Double (dw5).intValue () + Xc;
		Y1 = new Double (dw6).intValue () + Yc;
		Y2 = new Double (-dw6).intValue () + Yc;
		Y3 = Y1;
		drawTriangle (g, X1, Y1, X2, Y2, X3, Y3, aFillColor);
	}
	
	public void drawMultipleRiver (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
		Y1 = Yc + trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
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
	
	public void drawPort (Graphics g, int Xc, int Yc, Color aPortColor) {
		int x1, y1, x2, y2;
		int xtr, ytr, width, height;
		
		x1 = Xc;
		y1 = new Double (Yc - trackWidth * .5).intValue ();
		x2 = Xc;
		y2 = new Double (Yc + trackWidth * 2).intValue ();
		g.setColor (aPortColor);
		g.drawLine (x1, y1, x2, y2);
		width = trackWidth;
		height = width;
		xtr = new Double (x1 - width/2).intValue ();
		ytr = new Double (y1 - width).intValue ();
		g.drawOval (xtr, ytr, width, height);
		xtr = x1 - trackWidth * 2;
		width = trackWidth * 4;
		height = new Double (trackWidth * 3.5).intValue ();
		x1 = Xc - trackWidth;
		y1 = Yc;
		x2 = Xc + trackWidth;
		y2 = Yc;
		g.drawLine (x1, y1, x2, y2);
		g.drawArc (xtr, ytr, width, height, 220, 100);
	}
	
	public void drawRiver (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1, width, height, index;
		Graphics2D g2d = (Graphics2D) g;
		int halfTW = new Double (trackWidth/2).intValue ();
		BasicStroke tRiverStroke = new BasicStroke (2);
		Stroke tCurrentStroke = g2d.getStroke ();
		
		width = trackWidth - 1;
		height = trackWidth - 1;
		X1 = Xc - halfTW - trackWidth - trackWidth;
		Y1 = Yc - trackWidth;
		g2d.setStroke (tRiverStroke);
		g.setColor (aRiverColor);
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
		tCircleRadius = width/2;
		height = width;
		if (direction) {
			xArrowCenter = Xc + (x[2] + x[3])/2 + width;
			yArrowCenter = Yc - intDWidth;
		} else {
			xArrowCenter = Xc + (x[2] + x[3])/2 + width;
			yArrowCenter = Yc - (y[2] + y[3])/2 - height;
		}
		x1 = xArrowCenter - tCircleRadius;
		y1 = yArrowCenter - tCircleRadius;
		g.setColor (Color.black);
		g.drawArc (x1, y1, width, height, 180, -270);
		x2 = xArrowCenter;
		y2 = yArrowCenter + tCircleRadius;
		x3 = x2 + trackWidth/2;
		y3 = y2 - trackWidth;
		g.drawLine (x2, y2, x3, y3);
		x4 = x3 + trackWidth;
		y4 = y2;
		g.drawLine (x2, y2, x4, y4);
	}
	
	public void drawShallowCoast (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
	public void drawSmallRiver (Graphics g, int Xc, int Yc, Color aRiverColor) {
		int X1, Y1;
		
		X1 = Xc;
		Y1 = Yc - trackWidth;
		drawRiver (g, X1, Y1, aRiverColor);
	}
	
	private void drawTriangle (Graphics g, int X1, int Y1, int X2, int Y2, int X3, int Y3, Color aFillColor) {
		Polygon tTriangle;
		int xp [] = new int [4];
		int yp [] = new int [4];
		
		xp [0] = X1;
		yp [0] = Y1;
		xp [1] = X2;
		yp [1] = Y2;
		xp [2] = X3;
		yp [2] = Y3;
		xp [3] = X1;
		yp [3] = Y1;
		tTriangle = new Polygon (xp, yp, 4);
		if (aFillColor != null) {
			g.setColor (aFillColor);
			g.fillPolygon (tTriangle);
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
								retY =  midpointY (4);
							}
							break;
							
						case (10):
							if (aPointNum == 2) {
								retY = midpointY (5);
							} else {
								retY =  y [5];
							}
							break;
							
						case (11):
							if (aPointNum == 2) {
								retY = y [0];
							} else {
								retY =  midpointY (5);
							}
							break;
					}
				}
			} else {
				retY = NOT_VALID_POINT;
			}
		} else {
			if ((actualSliceNum > 12) && (actualSliceNum <=18)) {
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
		int sliceX [], sliceY [], pointCount;
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
			maxX = x[3];
		} else {
			maxX = x[2];
		}
		
		return maxX;
	}
	
	public int getMaxY () {
		int maxY;
		
		if (direction) {
			maxY = y[5];
		} else {
			maxY = y[3];
		}
		
		return maxY;
	}
	
	public int getMinX () {
		int minX;
		
		if (direction) {
			minX = x[0];
		} else {
			minX = x[5];
		}
		
		return minX;
	}
	
	public int getMinY () {
		int minY;
		
		if (direction) {
			minY = y[2];
		} else {
			minY = y[1];
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
		return (new Double (dwidth).intValue ());
	}
	
	public int getXd () {
		return (displaceUpDown);
	}
	
	public int getYd () {
		return (displaceLeftRight);
	}
	
	public int leftEdgeDisplacment () {
		return getMinX();
	}
	
	public int midpointX (int aSide) {
		int midX = 0;
		int midX1;
		
		if (aSide > 11) {
			midX1 = midpointX (aSide - 11);
			midX = (Xc + midX1)/2;
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
						midX = Xc - (Xc - x [1])/2;
						break;
						
					case (2):
					case (4):
					case (9):
					case (10):
						midX = Xc + (Xc - x [1])/2;
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
						midX = Xc + (intDWidth - displaceUpDown)/2 + displaceUpDown;
						break;
						
					case (4):
					case (5):
						midX = Xc - (intDWidth - displaceUpDown)/2 - displaceUpDown;
						break;
						
					case (8):
						midX = x[1];
						break;
						
					case (11):
						midX = x[0];
						break;
						
					case (6):
					case (10):
						midX = x[0] + (Xc - x [0])/2;
						break;
						
					case (7):
					case (9):
						midX = Xc + (Xc - x [0])/2;
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
			midY = (Yc + midY1)/2;
		} else {
			if (direction) {
				switch (aSide) {
					case (0):
					case (3):
						midY = Yc;
						break;
						
					case (1):
					case (2):
						midY = (y [1] + y [2])/2;
						break;
						
					case (4):
					case (5):
						midY = (y [4] + y [5])/2;
						break;
						
					case (6):
					case (10):
						midY = (y [0] + Yc)/2;
						break;
						
					case (7):
					case (9):
						midY = (y [1] + Yc)/2;
						break;
						
					case (8):
						midY = (y [2] + Yc)/2;
						break;
						
					case (11):
						midY = (y [5] + Yc)/2;
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
						midY = Yc - displaceLeftRight/2;
						break;
						
					case (2):
					case (4):
					case (9):
					case (10):
						midY = Yc + displaceLeftRight/2;
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
	
	public void paintHex (Graphics g, int Xo, int Yo, Color fill_color) {
		paintHex (g, Xo, Yo, fill_color, true, null);
	}
	
    public void paintHex (Graphics g, int Xo, int Yo, Color fill_color, Color thickFrame) {
		paintHex (g, Xo, Yo, fill_color, true, thickFrame);
	}
	
    public void paintHex (Graphics g, int Xo, int Yo, Color fillColor, boolean drawBorder) {
		paintHex (g, Xo, Yo, fillColor, drawBorder, null);
	}
	
    public void paintHex (Graphics g, int Xo, int Yo, Color fillColor, boolean drawBorder, Color thickFrame) {
		paintHex (g, Xo, Yo, fillColor, drawBorder, null, null);
	}

    public void paintHex (Graphics g, int Xo, int Yo, Color fillColor, boolean drawBorder, Color thickFrame, 
						  boolean aBlockedSides []) {
		Stroke tCurrentStroke;
		BasicStroke tFrameStroke;
		Shape tPreviousClip;
		Graphics2D g2d = (Graphics2D) g;
		int xp [], yp [], npnts = x.length, index;
		
		tPreviousClip = clipToHex (g, Xo, Yo);
	    xp = new int [npnts];
	    yp = new int [npnts];
	    for (index = 0; index < npnts; index++) {
	    		xp [index] = x [index] + Xo;
	    		yp [index] = y [index] + Yo;
    		}
		
    		g.setColor (fillColor);
 		try {
 			g.fillPolygon (xp, yp, npnts - 1);
 		} catch (ArrayIndexOutOfBoundsException exc) {
 			System.err.println ("Oops, trying to fill polygon at " + Xo + ", " + Yo + ". Sorry");
 		}
		if (thickFrame != null) {
			g.setColor (thickFrame);
			tCurrentStroke = g2d.getStroke();
			tFrameStroke = new BasicStroke (trackWidth*2);
			g2d.setStroke (tFrameStroke);
			try {
				for (index = 0; index < npnts - 1; index++) {
					g.drawLine (xp [index], yp [index], xp [index + 1], yp [index + 1]);
				}
			} catch (ArrayIndexOutOfBoundsException exc) {
				System.err.println ("Oops, trying to draw lines at " + Xo + ", " + Yo + ". Sorry");
			}
			
			g2d.setStroke (tCurrentStroke);
		}
		drawBorders (g, Xo, Yo, drawBorder, aBlockedSides);
		g.setClip (tPreviousClip);
    }
    
	public void paintSelected (Graphics g, int Xo, int Yo) {
		Shape tPreviousClip;
		int xp [], yp [], npnts = x.length, index;
		
	    xp = new int [npnts];
	    yp = new int [npnts];
	    for (index = 0; index < npnts; index++) {
	    		xp [index] = x [index] + Xo;
	    		yp [index] = y [index] + Yo;
	    }
		
		tPreviousClip = clipToHex (g, Xo, Yo);
		g.setColor (Color.red);
		for (index = 0; index < npnts - 1; index++) {
			g.drawLine (xp [index], yp [index], xp [index + 1], yp [index + 1]);
		}
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
		tCircleRadius = width/2;
		height = width;
		if (direction) {
			xArrowCenter = Xc + (x[2] + x[3])/2 + width;
			yArrowCenter = Yc - intDWidth;
		} else {
			xArrowCenter = Xc + (x[2] + x[3])/2 + width;
			yArrowCenter = Yc - (y[2] + y[3])/2 - height;
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
        
        double ssp_d = sSixth_pi * dwidth, csp_d = cSixth_pi * dwidth;
        double stp_d = sThird_pi * dwidth, ctp_d = cThird_pi * dwidth;
        
        displaceUpDown = new Double (ssp_d).intValue ();
        displaceLeftRight = new Double (csp_d).intValue ();
        Xt = new Double (stp_d).intValue () - 3;
        Yt = new Double (ctp_d).intValue () - 3;
        offsetHex (offsetX, offsetY);
        intDWidth = new Double (dwidth).intValue ();
		
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
		cityWidth = new Double (displaceLeftRight/2.9).intValue ();
		trackWidth = new Double (displaceLeftRight/7.25).intValue () + 1;
		hexPolygon = buildOffsetPolygon (0, 0);
		rectX = getMinX ();
		rectY = getMinY ();
		rectWidth = getMaxX () - rectX + 1;
		rectHeight = getMaxY () - rectY + 1;
		rectBounds = new Rectangle (rectX, rectY, rectWidth, rectHeight);
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
