package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.round.action.ActorI;

@DisplayName ("Corporation Status Update Tests")
class CorporationStatusUpdateTests {
	ShareCompany alphaShareCompany;
	ShareCompany betaShareCompany;
	PrivateCompany gammaPrivateCompany;
	MinorCompany deltaMinorCompany;
	CompanyTestFactory companyTestFactory;
	
	@BeforeEach
	void setUp() throws Exception {
		companyTestFactory = new CompanyTestFactory ();
		alphaShareCompany = companyTestFactory.buildAShareCompany (1);
		betaShareCompany = companyTestFactory.buildAShareCompany (2);
		gammaPrivateCompany = companyTestFactory.buildAPrivateCompany (1);
		deltaMinorCompany = companyTestFactory.buildAMinorCompany (1);
	}

	@Test
	@DisplayName ("Update Status from Unowned to next State")
	void testUpdatingUnownedStatus () {
		assertEquals ("Unowned", alphaShareCompany.getStatusName ());
		alphaShareCompany.setStatus (ActorI.ActionStates.Owned);
		
		assertEquals ("Owned", alphaShareCompany.getStatusName ());
		betaShareCompany.setStatus (ActorI.ActionStates.Unformed);
		assertEquals ("Unformed", betaShareCompany.getStatusName ());
		
		assertEquals ("Unowned", gammaPrivateCompany.getStatusName ());
		gammaPrivateCompany.setStatus (ActorI.ActionStates.Owned);
		assertEquals ("Owned", gammaPrivateCompany.getStatusName ());

	}
	
	@Test
	@DisplayName ("Update Status from Owned to next State")
	void testUpdatingOwnedStatus () {
		alphaShareCompany.setStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.WaitingResponse);
		assertEquals ("Waiting for Response", alphaShareCompany.getStatusName ());
		betaShareCompany.setStatus (ActorI.ActionStates.Owned);
		betaShareCompany.setStatus (ActorI.ActionStates.MayFloat);
		assertEquals ("May Float", betaShareCompany.getStatusName ());
		
		alphaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.WillFloat);
		assertEquals ("Will Float", alphaShareCompany.getStatusName ());
		
		betaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		betaShareCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", betaShareCompany.getStatusName ());

		alphaShareCompany.forceSetStatus (ActorI.ActionStates.Owned);
		alphaShareCompany.setStatus (ActorI.ActionStates.NotOperated);
		assertNotEquals ("Not Operated", alphaShareCompany.getStatusName ());
		
		gammaPrivateCompany.setStatus (ActorI.ActionStates.Owned);
		gammaPrivateCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", gammaPrivateCompany.getStatusName ());

		deltaMinorCompany.setStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.Closed);
		assertEquals ("Closed", deltaMinorCompany.getStatusName ());

		deltaMinorCompany.forceSetStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.WillFloat);
		assertEquals ("Will Float", deltaMinorCompany.getStatusName ());
		
		deltaMinorCompany.forceSetStatus (ActorI.ActionStates.Owned);
		deltaMinorCompany.setStatus (ActorI.ActionStates.NotOperated);
		assertNotEquals ("Not Operated", deltaMinorCompany.getStatusName ());
}

}
