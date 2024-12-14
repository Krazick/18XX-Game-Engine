package ge18xx.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ge18xx.bank.Bank;
import ge18xx.bank.BankTestFactory;
import geUtilities.utilites.xml.UtilitiesTestFactory;

class LicenseTests {
	CouponTestFactory couponTestFactory;
	UtilitiesTestFactory utilitiesTestFactory;
	BankTestFactory bankTestFactory;
	Bank bank;
	License licensePort;
	License licenseOpenPort;
	License licenseClosedPort;
	License licenseBridge;
	License licenseTunnel;

	@BeforeEach
	void setUp () throws Exception {
		utilitiesTestFactory = new UtilitiesTestFactory ();
		couponTestFactory = new CouponTestFactory (utilitiesTestFactory);
		bankTestFactory = new BankTestFactory ();
		bank = bankTestFactory.buildBank ();

		licensePort = couponTestFactory.buildLicense ("Port", 50, 10);
		licenseOpenPort = couponTestFactory.buildLicense ("Open Port", 40, 20);
		licenseClosedPort = couponTestFactory.buildLicense ("Closed Port", 30, 30);
		licenseBridge = couponTestFactory.buildLicense ("Bridge", 50, 20);
		licenseTunnel = couponTestFactory.buildLicense ("Tunnel", 70, 30);
	}

	@Test
	@DisplayName ("Basic License Tests")
	void basicLicenseTests () {
		assertEquals (50, licensePort.getPrice ());
		assertEquals (10, licensePort.getBenefitValue ());
		assertTrue (licensePort.isPortLicense ());
		assertFalse (licensePort.isBridgeLicense ());
		assertEquals ("Port License", licensePort.getLicenseName ());
		assertEquals ("Port License Price 50", licensePort.getLicenseLabel ());
		assertTrue (licensePort.isLicenseOfType (License.LicenseTypes.PORT));
		assertFalse (licensePort.isLicenseOfType (License.LicenseTypes.BRIDGE));
		
		assertEquals (40, licenseOpenPort.getPrice ());
		assertEquals (20, licenseOpenPort.getBenefitValue ());
		assertTrue (licenseOpenPort.isPortLicense ());
		assertFalse (licenseOpenPort.isBridgeLicense ());
		assertEquals ("Open Port License", licenseOpenPort.getLicenseName ());
		assertEquals ("Open Port License Price 40", licenseOpenPort.getLicenseLabel ());
		assertTrue (licenseOpenPort.isLicenseOfType (License.LicenseTypes.PORT));
		assertFalse (licenseOpenPort.isLicenseOfType (License.LicenseTypes.BRIDGE));
		
		assertEquals (30, licenseClosedPort.getPrice ());
		assertEquals (30, licenseClosedPort.getBenefitValue ());
		assertTrue (licenseClosedPort.isPortLicense ());
		assertFalse (licenseClosedPort.isBridgeLicense ());
		assertEquals ("Closed Port License", licenseClosedPort.getLicenseName ());
		assertEquals ("Closed Port License Price 30", licenseClosedPort.getLicenseLabel ());
		assertTrue (licenseClosedPort.isLicenseOfType (License.LicenseTypes.PORT));
		assertFalse (licenseClosedPort.isLicenseOfType (License.LicenseTypes.BRIDGE));
		
		assertEquals (50, licenseBridge.getPrice ());
		assertEquals (20, licenseBridge.getBenefitValue ());
		assertFalse (licenseBridge.isPortLicense ());
		assertTrue (licenseBridge.isBridgeLicense ());
		assertEquals ("Bridge License", licenseBridge.getLicenseName ());
		assertEquals ("Bridge License Price 50", licenseBridge.getLicenseLabel ());
		assertTrue (licenseBridge.isLicenseOfType (License.LicenseTypes.BRIDGE));
		assertFalse (licenseBridge.isLicenseOfType (License.LicenseTypes.PORT));
		
		assertEquals (70, licenseTunnel.getPrice ());
		assertEquals (30, licenseTunnel.getBenefitValue ());
		assertFalse (licenseTunnel.isPortLicense ());
		assertTrue (licenseTunnel.isTunnelLicense ());
		assertEquals ("Tunnel License", licenseTunnel.getLicenseName ());
		assertEquals ("Tunnel License Price 70", licenseTunnel.getLicenseLabel ());
		assertTrue (licenseTunnel.isLicenseOfType (License.LicenseTypes.TUNNEL));
		assertFalse (licenseTunnel.isLicenseOfType (License.LicenseTypes.PORT));
	}
}
