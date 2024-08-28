package ge18xx.round;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses ({ 	RoundManagerTestConstructors.class, 
					SetRoundTypeTests.class,
					ge18xx.round.action.AllActionAndEffectTests.class})
@DisplayName ("Round Manager Test Suite")
public class AllRoundManagerTestSuite {

}
