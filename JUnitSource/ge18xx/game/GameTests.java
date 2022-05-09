package ge18xx.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Simple Tests for Java code")
class GameTests {

	@BeforeEach
	void setUp () throws Exception {
	}

	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@Disabled
	void test () {
		String tFrameName;
		String tAfterTruncation;
		String tPattern = "^(.*) \\([A-Za-z][A-Za-z0-9_]*\\)$";
		tFrameName = "1830 Action Report Frame (Z)";

		System.out.println ("Frame Title Before Truncating |" + tFrameName + "|");
		if (tFrameName.matches (tPattern)) {
			int tIndexLastSpace = tFrameName.lastIndexOf (" ");
			tAfterTruncation = tFrameName.substring (0, tIndexLastSpace);
		} else {
			tAfterTruncation = tFrameName;
		}
		System.out.println ("Frame Title After Truncating |" + tAfterTruncation + "|");

		tFrameName = "1830 Round Frame";

		System.out.println ("Frame Title Before Truncating |" + tFrameName + "|");
		if (tFrameName.matches (tPattern)) {
			int tIndexLastSpace = tFrameName.lastIndexOf (" ");
			tAfterTruncation = tFrameName.substring (0, tIndexLastSpace);
		} else {
			tAfterTruncation = tFrameName;
		}
		System.out.println ("Frame Title After Truncating |" + tAfterTruncation + "|");

	}

}
