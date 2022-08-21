package ge18xx.center;

import java.awt.Graphics;

import ge18xx.map.Hex;
import ge18xx.tiles.Feature2;
import ge18xx.utilities.XMLNode;

public class PrivateRailwayCenter extends RevenueCenter {

	public PrivateRailwayCenter (XMLNode aNode) {
		super (aNode);
	}

	@Override
	public boolean cityOrTown () {
		return false;
	}

	@Override
	public void draw (Graphics g, int Xc, int Yc, int aTileOrient, Hex aHex, boolean onTile,
			Feature2 aSelectedFeature) {
		cityInfo.drawPrivateRailway (g, Xc, Yc, aHex);
	}

	@Override
	public boolean cityHasOpenStation () {
		return false;
	}
}
