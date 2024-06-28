package ge18xx;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName ("Game Engine 18XX Game Package Test Suite")
@SelectPackages ({ "ge18xx.bank","ge18xx.center", "ge18xx.company", "ge18xx.company.benefit", "ge18xx.game", "ge18xx.map",
		"ge18xx.market", "ge18xx.network", "ge18xx.player", "ge18xx.round", "ge18xx.round.actions",
		"ge18xx.round.actions.effects", "ge18xx.tiles", "ge18xx.train", "ge18xx.utilities" })

class GE18XXFullTestSuite {

}
