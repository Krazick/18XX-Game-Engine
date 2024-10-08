package ge18xx.center;

import java.awt.Graphics;

import ge18xx.map.Hex;
import ge18xx.tiles.Feature2;
import geUtilities.xml.XMLNode;

public class PrivateRailwayCenter extends RevenueCenter {

	public PrivateRailwayCenter (XMLNode aNode) {
		super (aNode);
	}

	@Override
	public boolean cityOrTown () {
		return false;
	}

	@Override
	public void draw (Graphics aGraphics, int aXc, int aYc, int aTileOrient, Hex aHex, boolean onTile,
			Feature2 aSelectedFeature) {
		if (cityInfo != CityInfo.NO_CITY_INFO) {
			cityInfo.drawPrivateRailway (aGraphics, aXc, aYc, aHex);
		}
	}

	@Override
	public boolean cityHasOpenStation () {
		return false;
	}
}
