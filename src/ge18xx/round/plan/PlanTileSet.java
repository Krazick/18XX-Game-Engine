package ge18xx.round.plan;

import java.awt.Dimension;
import java.awt.Graphics;

import ge18xx.tiles.TileSet;
import ge18xx.toplevel.TileTrayFrame;

public class PlanTileSet extends TileSet {

	private static final long serialVersionUID = 1L;
	public static final int PLAN_TILES_PER_ROW = 1;

	public PlanTileSet (TileTrayFrame aTileTrayFrame) {
		super (aTileTrayFrame);
		setShowAllTiles (true);
		setScale (8);
	}

	public PlanTileSet (String aSetName) {
		super (aSetName);
		setShowAllTiles (true);
		setHex ("EW");
		setScale (8);
	}

	@Override
	public int getTilesPerRow () {
		return PLAN_TILES_PER_ROW;
	}

	@Override
	public void paintComponent (Graphics aGraphics) {
		super.paintComponent (aGraphics);
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension (300, 500); // Example size
    }
}
