package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Testing Cleaning XML Format (removing whitespace)")
class UtilitiesTestCleaner {

	@BeforeEach
	void setUp () throws Exception {
	}

	@Test
	@DisplayName ("RegEx Replacements")
	void test () {
		String tXMLFormat;
		String tXMLFormatClean;

		tXMLFormat = "Game Activity <GA>    <Action actor=\"Dave\" chainPrevious=\"false\" class=\"ge18xx.round.action.BuyStockAction\" name=\"Buy Stock Action\" number=\"101\" roundID=\"1\" roundType=\"Stock Round\" totalCash=\"12000\">        <Effects>            <Effect cash=\"20\" class=\"ge18xx.round.action.effects.CashTransferEffect\" fromActor=\"Dave\" isAPrivate=\"false\" name=\"Cash Transfer\" toActor=\"Bank\"/>            <Effect class=\"ge18xx.round.action.effects.TransferOwnershipEffect\" companyAbbrev=\"SVN&amp;RR\" fromActor=\"Start Packet\" isAPrivate=\"false\" name=\"Transfer Ownership\" percentage=\"100\" president=\"true\" toActor=\"Dave\"/>            <Effect actor=\"SVN&amp;RR\" class=\"ge18xx.round.action.effects.StateChangeEffect\" isAPrivate=\"true\" name=\"State Change\" newState=\"Owned\" previousState=\"Unowned\"/>            <Effect actor=\"Dave\" class=\"ge18xx.round.action.effects.BoughtShareEffect\" isAPrivate=\"false\" name=\"Bought Share\"/>            <Effect actor=\"Dave\" class=\"ge18xx.round.action.effects.StateChangeEffect\" isAPrivate=\"false\" name=\"State Change\" newState=\"Bought\" previousState=\"No Action\"/>        </Effects>    </Action></GA>";

		tXMLFormatClean = tXMLFormat.replaceAll (">[ \t\n\f\r]+<", "><");

		System.out.println (tXMLFormatClean);
		assertEquals (1108, tXMLFormat.length ());
		assertEquals (1024, tXMLFormatClean.length ());
	}
}
