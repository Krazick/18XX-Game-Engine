package ge18xx.map;

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
import java.awt.Stroke;

public class Hex18XX extends Hex {
	public static final Hex18XX NO_HEX18XX = null;

	public Hex18XX () {
		this (direction);
	}

	public Hex18XX (boolean aHexDirection) {
		this (0, 0, aHexDirection);
	}

	public Hex18XX (int offsetX, int offsetY, boolean aHexDirection) {
		this (offsetX, offsetY, aHexDirection, DEFAULT_SCALE);
	}

	public Hex18XX (int offsetX, int offsetY, boolean aHexDirection, int aScale) {
		super (offsetX, offsetY, aHexDirection, aScale);
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

	protected void drawTriangle (Graphics g, int X1, int Y1, int X2, int Y2, int X3,
			int Y3, Paint aFillPaint) {
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

	public boolean rotateArrowContainingPoint (Point aPoint, int Xc, int Yc) {
		int x1;
		int y1;
		int width;
		int height;
		int tCircleRadius;
		int xArrowCenter;
		int yArrowCenter;
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

}
