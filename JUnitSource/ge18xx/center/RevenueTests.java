/**
 *
 */
package ge18xx.center;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author marksmith
 *
 */
@DisplayName ("Revenue Tests")
class RevenueTests {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp () throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown () throws Exception {
	}

	@Test
	@DisplayName ("Revenue Constructor Test No Args")
	public void RevenueTestConstructorNoArgs () {
		Revenue tRevenue1;

		tRevenue1 = new Revenue ();
		assertEquals (0, tRevenue1.getPhase (), "Revenue Constructor -- No Args - All Phases");
		assertEquals ("0", tRevenue1.getPhaseToString (), "Revenue - All Phases String");
		assertEquals (0, tRevenue1.getValue (), "Revenue -- Zero Value ");
		assertEquals ("0", tRevenue1.getValueToString (), "Revenue -- Zero Value String");
	}

	@Test
	@DisplayName ("Revenue Constructor Test with Two Args (Value and Phase)")
	public void RevenueTestConstructorTwoArgs () {
		Revenue tRevenue2;

		tRevenue2 = new Revenue (20, 1);
		assertEquals (1, tRevenue2.getPhase (), "Revenue Constructor -- Phase 1, Value 20");
		assertEquals ("1", tRevenue2.getPhaseToString (), "Revenue - Phase String");
		assertEquals (20, tRevenue2.getValue (), "Revenue -- 20 Value");
		assertEquals ("20", tRevenue2.getValueToString (), "Revenue -- 20 Value String");
	}

	@Test
	@DisplayName ("Revenue Constructor Test from Existing Revenue")
	public void RevenueTestConstructorFromRevenue () {
		Revenue tRevenue2;
		Revenue tRevenue3;

		tRevenue2 = new Revenue (20, 1);
		tRevenue3 = new Revenue (tRevenue2);
		assertEquals (1, tRevenue3.getPhase (), "Revenue Constructor -- Revenue - Phase 1");
		assertEquals ("1", tRevenue3.getPhaseToString (), "Revenue - Phase String");
		assertEquals (20, tRevenue3.getValue (), "Revenue -- 20 Value");
		assertEquals ("20", tRevenue3.getValueToString (), "Revenue -- 20 Value String");
	}

	@Test
	@DisplayName ("Revenue Creation via Cloning existing Revenue and Clone is seperate Object")
	public void RevenueTestWithCloning () {
		Revenue tRevenue2;
		Revenue tRevenue3;
		Revenue tRevenue4;

		tRevenue2 = new Revenue (20, 1);
		tRevenue3 = new Revenue (tRevenue2);
		tRevenue4 = tRevenue2.clone ();
		tRevenue3.setValues (40, 2);
		assertEquals (2, tRevenue3.getPhase (), "Revenue  --Set Values");
		assertEquals ("2", tRevenue3.getPhaseToString (), "Revenue -- Phase String");
		assertEquals (40, tRevenue3.getValue (), "Revenue -- 40 Value");
		assertEquals ("40", tRevenue3.getValueToString (), "Revenue -- 40 Value String");
		assertEquals (1, tRevenue4.getPhase (), "Revenue Clone -- Phase 1, Value 40");
		assertEquals ("1", tRevenue4.getPhaseToString (), "Revenue - All Phases String");
		assertEquals (20, tRevenue4.getValue (), "Revenue -- 20 Value");
		assertEquals ("20", tRevenue4.getValueToString (), "Revenue -- 20 Value String");
	}

	@Test
	@DisplayName ("Revenue Constructor Test with Null Argument")
	public void RevenueTestWithNullArg () {
		Revenue tRevenue5;

		tRevenue5 = new Revenue (null);
		assertEquals (0, tRevenue5.getPhase (), "Revenue Clone -- null");
		assertEquals ("0", tRevenue5.getPhaseToString (), "Revenue - All Phases String");
		assertEquals (0, tRevenue5.getValue (), "Revenue -- Zero Value");
		assertEquals ("0", tRevenue5.getValueToString (), "Revenue -- Zero Value String");
	}
}
