package ge18xx.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import geUtilities.ElementName;
import geUtilities.XMLDocument;
import geUtilities.XMLElement;
import geUtilities.XMLNode;

@DisplayName ("Movement Class Tests")
class MovementTests {
	Movement moveRight;
	Movement moveLeft;
	Movement moveUp;
	Movement moveDown;
	Movement moveDownRight;
	Movement alpha;

	@BeforeEach
	void setUp () throws Exception {
		alpha = new Movement ();
		moveDown = new Movement (1, 0);
		moveUp = new Movement (-1, 0);
		moveRight = new Movement (0, 1);
		moveLeft = new Movement (0, -1);
		moveDownRight = new Movement (1, 1);
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Constructor Tests")
	void MovementConstructorTests () {
		assertEquals (0, alpha.getRowAdjustment ());
		assertEquals (0, alpha.getColAdjustment ());

		assertEquals (0, moveRight.getRowAdjustment ());
		assertEquals (1, moveRight.getColAdjustment ());

		Movement tMoveRight = new Movement (0, 1);
		assertTrue (tMoveRight.equals (moveRight));
		assertTrue (moveRight.equals (tMoveRight));
		assertFalse (moveRight.equals (alpha));

		assertEquals (0, moveLeft.getRowAdjustment ());
		assertEquals (-1, moveLeft.getColAdjustment ());

		assertEquals (-1, moveUp.getRowAdjustment ());
		assertEquals (0, moveUp.getColAdjustment ());

		assertEquals (1, moveDown.getRowAdjustment ());
		assertEquals (0, moveDown.getColAdjustment ());

		Movement tMoveDown = new Movement (1, 0);
		assertTrue (tMoveDown.equals (moveDown));
		assertTrue (moveDown.equals (tMoveDown));
		assertFalse (tMoveDown.equals (moveUp));

		assertEquals (1, moveDownRight.getRowAdjustment ());
		assertEquals (1, moveDownRight.getColAdjustment ());
	}

	@Test
	@DisplayName ("Movement getting Movement Neighbor")
	void GetMovementNeighborTests () {
		assertEquals (MarketCell.NEIGHBOR_RIGHT, moveRight.getMoveNeighbor ());
		assertEquals (MarketCell.NEIGHBOR_LEFT, moveLeft.getMoveNeighbor ());
		assertEquals (MarketCell.NEIGHBOR_UP, moveUp.getMoveNeighbor ());
		assertEquals (MarketCell.NEIGHBOR_DOWN, moveDown.getMoveNeighbor ());
		assertEquals (MarketCell.NEIGHBOR_DOWN_RIGHT, moveDownRight.getMoveNeighbor ());

		Movement beta = new Movement (-1, -1);
		assertEquals (MarketCell.NEIGHBOR_NONE, alpha.getMoveNeighbor ());
		assertEquals (MarketCell.NEIGHBOR_NONE, beta.getMoveNeighbor ());
	}

	@Test
	@DisplayName ("Movement XML Node Creation and Parsing")
	void XMLNodeMovementTests () {
		XMLDocument tXMLDocument;
		XMLElement tXMLElement;
		XMLNode tXMLNode;
		ElementName tEN_Name = new ElementName ("MovementTest");
		Movement tParsedMovement;
		String tExpected = "<MovementTest colAdjust=\"0\" rowAdjust=\"-1\"/>\n";

		tXMLDocument = new XMLDocument ();
		tXMLElement = moveUp.createElement (tXMLDocument, tEN_Name);
		tXMLDocument.appendChild (tXMLElement);
		assertEquals (tExpected, tXMLDocument.toXMLString ());

		tXMLNode = tXMLDocument.getDocumentNode ();
		tParsedMovement = new Movement (tXMLNode);

		assertEquals (-1, tParsedMovement.getRowAdjustment ());
		assertEquals (0, tParsedMovement.getColAdjustment ());

	}
}
