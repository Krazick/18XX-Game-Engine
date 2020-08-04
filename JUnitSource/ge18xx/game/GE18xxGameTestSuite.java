package ge18xx.game;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith (JUnitPlatform.class)
@SuiteDisplayName ("Game Engine 18XX Game Package Test Suite")
@SelectPackages ( { "ge18xx.game" } )

class GE18xxGameTestSuite {

}
