package ge18xx.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName ("Portfolio Summary")
class PortfolioSummaryTest {
	PortfolioSummary sharePrezPortfolioSummary;
	PortfolioSummary sharePortfolioSummary;
	PortfolioSummary privatePortfolioSummary;
	PortfolioSummary minorPrezPortfolioSummary;
	PortfolioSummary minorPortfolioSummary;

	@BeforeEach
	void setUp () throws Exception {
		String tAbbrev;
		String tType;
		int tCount;
		int tPercentage;
		boolean tIsPresident;
		Border tColorBorder;
		String tNote;

		tAbbrev = "BnOTest";
		tType = "Share";
		tCount = 3;
		tPercentage = 30;
		tIsPresident = true;
		tNote = "Test Case Note";

		tColorBorder = BorderFactory.createLineBorder (Color.black, 1);

		sharePrezPortfolioSummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
				tColorBorder, tNote);

		tAbbrev = "NPrezTest";
		tType = "Share";
		tCount = 3;
		tPercentage = 30;
		tIsPresident = false;
		tNote = "Test Case Note Not Prez";

		tColorBorder = BorderFactory.createLineBorder (Color.black, 1);

		sharePortfolioSummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
				tColorBorder, tNote);

		tAbbrev = "PrivateTest";
		tType = "Private";
		tCount = 1;
		tPercentage = 100;
		tIsPresident = true;
		tNote = "Private Test Case Note";

		tColorBorder = BorderFactory.createLineBorder (Color.blue, 1);

		privatePortfolioSummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
				tColorBorder, tNote);

		tAbbrev = "MinorTest";
		tType = "Minor";
		tCount = 1;
		tPercentage = 50;
		tIsPresident = true;
		tNote = "Minor Test Case Note";

		tColorBorder = BorderFactory.createLineBorder (Color.blue, 1);

		minorPrezPortfolioSummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
				tColorBorder, tNote);

		tAbbrev = "MinorNPrezTest";
		tType = "Minor";
		tCount = 1;
		tPercentage = 50;
		tIsPresident = false;
		tNote = "Minor Prez Test Case Note";

		tColorBorder = BorderFactory.createLineBorder (Color.blue, 1);

		minorPortfolioSummary = new PortfolioSummary (tAbbrev, tType, tCount, tPercentage, tIsPresident,
				tColorBorder, tNote);
	}

	@Test
	@DisplayName ("Test adding Count to Share Company")
	void addCountTest () {
		String tSummary;

		sharePrezPortfolioSummary.addCount (2);
		tSummary = sharePrezPortfolioSummary.getSummary ();
		assertEquals ("BnOTest 5 Certs/30% Prez", tSummary);
		assertEquals (5, sharePrezPortfolioSummary.getCount ());
	}

	@Test
	@DisplayName ("Test adding Percentage to Share Company")
	void addPercentageTest () {
		String tSummary;

		sharePrezPortfolioSummary.addPercentage (20);
		tSummary = sharePrezPortfolioSummary.getSummary ();
		assertEquals ("BnOTest 3 Certs/50% Prez", tSummary);
	}

	@Test
	@DisplayName ("Test setting Prez for initia Non-Prez Share Company")
	void setPresidentTest () {
		String tSummary;

		sharePortfolioSummary.setIsPresident (true);
		tSummary = sharePortfolioSummary.getSummary ();
		assertEquals ("NPrezTest 3 Certs/30% Prez", tSummary);

		sharePortfolioSummary.setIsPresident (false);
		tSummary = sharePortfolioSummary.getSummary ();
		assertEquals ("NPrezTest 3 Certs/30% Prez", tSummary);
	}

	@Nested
	@DisplayName ("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName ("for Share Company")
		void sharePrezConstructorTests () {
			String tSummary;
			Border tFoundBorder;

			tSummary = sharePrezPortfolioSummary.getSummary ();
			assertEquals ("BnOTest 3 Certs/30% Prez", tSummary);

			assertEquals (3, sharePrezPortfolioSummary.getCount ());
			assertEquals ("Share", sharePrezPortfolioSummary.getType ());
			assertEquals ("Test Case Note", sharePrezPortfolioSummary.getNote ());

			tFoundBorder = sharePrezPortfolioSummary.getCorporateColorBorder ();
			assertNotNull (tFoundBorder);
		}

		@Test
		@DisplayName ("for Share Not Prez Company")
		void shareConstructorTests () {
			String tSummary;
			Border tFoundBorder;

			tSummary = sharePortfolioSummary.getSummary ();
			assertEquals ("NPrezTest 3 Certs/30%", tSummary);

			assertEquals ("Share", sharePortfolioSummary.getType ());
			assertEquals ("Test Case Note Not Prez", sharePortfolioSummary.getNote ());

			tFoundBorder = sharePortfolioSummary.getCorporateColorBorder ();
			assertNotNull (tFoundBorder);
		}

		@Test
		@DisplayName ("for Private Company")
		void privateConstructorTests () {
			String tSummary;
			Border tFoundBorder;

			tSummary = privatePortfolioSummary.getSummary ();
			assertEquals ("PrivateTest Private 1 Prez Cert", tSummary);

			assertEquals ("Private", privatePortfolioSummary.getType ());
			assertEquals ("Private Test Case Note", privatePortfolioSummary.getNote ());

			tFoundBorder = privatePortfolioSummary.getCorporateColorBorder ();
			assertNull (tFoundBorder);
		}

		@Test
		@DisplayName ("for Minor Prez Company")
		void minorPrezConstructorTests () {
			String tSummary;
			Border tFoundBorder;

			tSummary = minorPrezPortfolioSummary.getSummary ();
			assertEquals ("MinorTest Minor 1 Cert/50% Prez", tSummary);

			assertEquals ("Minor", minorPrezPortfolioSummary.getType ());
			assertEquals ("Minor Test Case Note", minorPrezPortfolioSummary.getNote ());

			tFoundBorder = minorPrezPortfolioSummary.getCorporateColorBorder ();
			assertNull (tFoundBorder);
		}

		@Test
		@DisplayName ("for Minor Company")
		void minorConstructorTests () {
			String tSummary;
			Border tFoundBorder;

			tSummary = minorPortfolioSummary.getSummary ();
			assertEquals ("MinorNPrezTest Minor 1 Cert/50%", tSummary);

			assertEquals ("Minor", minorPortfolioSummary.getType ());
			assertEquals ("Minor Prez Test Case Note", minorPortfolioSummary.getNote ());

			tFoundBorder = minorPortfolioSummary.getCorporateColorBorder ();
			assertNull (tFoundBorder);
		}
	}
}
