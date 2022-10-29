package ge18xx.center;

import java.awt.Graphics;

import org.mockito.Mockito;

import ge18xx.game.GameTestFactory;
import ge18xx.utilities.UtilitiesTestFactory;
import ge18xx.utilities.XMLNode;

public class CenterTestFactory {
	private GameTestFactory gameTestFactory;
	private UtilitiesTestFactory utilitiesTestFactory;

	/**
	 * Builds the Center Test Factory by creating the gameTestFactory and get the Utilities Test Factory
	 *
	 */
	public CenterTestFactory () {
		gameTestFactory = new GameTestFactory ();
		utilitiesTestFactory = gameTestFactory.getUtilitiesTestFactory ();
	}

	public PrivateRailwayCenter buildPrivateRailwayCenter () {
		PrivateRailwayCenter tPrivateRailwayCenter;
		String tPrivateRailwayCenterXMLText = "<RevenueCenter id=\"0\" location=\"12\" name=\"CTF Private\" type=\"Private Railway\" />\n";
		XMLNode tPrivateRailwayCenterNode;

		tPrivateRailwayCenterNode = utilitiesTestFactory.buildXMLNode (tPrivateRailwayCenterXMLText);
		tPrivateRailwayCenter = (PrivateRailwayCenter) RevenueCenter.NO_CENTER;
		if (tPrivateRailwayCenterNode != XMLNode.NO_NODE) {
			tPrivateRailwayCenter = new PrivateRailwayCenter (tPrivateRailwayCenterNode);
		}

		return tPrivateRailwayCenter;
	}

	public CityInfo buildCityInfoMock () {
		CityInfo mCityInfo = Mockito.mock (CityInfo.class);

		Mockito.when (mCityInfo.getName ()).thenReturn ("MCityInfo");

		return mCityInfo;
	}

	public Graphics buildGraphicsMock () {
		Graphics mGraphics = Mockito.mock (Graphics.class);

		return mGraphics;
	}
}
