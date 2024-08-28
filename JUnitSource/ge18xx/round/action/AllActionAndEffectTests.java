package ge18xx.round.action;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses ({ 	WaitForResponseFrameTests.class,
					ge18xx.round.action.effects.AllEffectTests.class })

@DisplayName ("All Action and Effect Tests")
public class AllActionAndEffectTests {

}
