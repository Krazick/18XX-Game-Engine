package ge18xx.round;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName ("Round Manager Test Suite")
@SelectPackages ({ 
		"ge18xx.round",
		"ge18xx.round.action",
		"ge18xx.round.action.effects"
		})
public class RoundPackageTestSuite {

}
