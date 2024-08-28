package ge18xx.round.action.effects;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses ({ 	CashTransferEffectTestConstructor.class, 
					ChangeMapEffectTests.class, 
					EffectTestConstructor.class,
					ResponseOfferEffectTestConstructor.class, 
					ToEffectTestConstructor.class,
					TransferOwnershipEffectTestConstructor.class, 
					TransferTrainEffectTests.class })
@DisplayName ("All Effect Tests")
public class AllEffectTests {

}
