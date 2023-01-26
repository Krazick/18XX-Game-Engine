package ge18xx.utilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName ("Validator Tests")
class ValidatorsTests {

	@Test
	@DisplayName ("Test Valid IPv4 or Domain Name")
	void isValidIPorDomainTests () {
		String domainOrIP;
		
		domainOrIP = "geeksforgeeks.org";
		assertTrue (Validators.isValidIPorDomain (domainOrIP));
		
		domainOrIP = "geeksforgeeks.org";
		assertTrue (Validators.isValidIPorDomain (domainOrIP));
		
		domainOrIP = "-geeksforgeeks.org";
		assertFalse (Validators.isValidIPorDomain (domainOrIP));
		
		domainOrIP =  "192.168.1.21";
		assertTrue (Validators.isValidIPorDomain (domainOrIP));
		
		domainOrIP =  "192.168.1.21.7";
		assertFalse (Validators.isValidIPorDomain (domainOrIP));
	}
	
	@Test
	@DisplayName ("Test Valid and Invalid Domain Names")
	void domainNameTests () {
		// Test Case 1:
		String str1 = "geeksforgeeks.org";
		assertTrue (Validators.isValidDomain (str1));

		// Test Case 2:
		String str2 = "contribute.geeksforgeeks.org";
		assertTrue (Validators.isValidDomain (str2));

		// Test Case 3:
		String str3 = "-geeksforgeeks.org";
		assertFalse (Validators.isValidDomain (str3));

		// Test Case 4:
		String str4 = "geeksforgeeks.o";
		assertFalse (Validators.isValidDomain (str4));

		// Test Case 5:
		String str5 = ".org";
		assertFalse (Validators.isValidDomain (str5));

		str5 = "";
		assertFalse (Validators.isValidDomain (str5));

		str5 = null;
		assertFalse (Validators.isValidDomain (str5));
	}

	@Test
	@DisplayName ("Test Valid and Invalid IPv4 Addresses")
	void IPAddressesTests () {
		// Test Case 1:
		String str1 = "192.168.1.21";
		assertTrue (Validators.isValidIP (str1));

		String str2 = "127.0.0.0";
		assertTrue (Validators.isValidIP (str2));

		String str3 = "127.0.0.";
		assertFalse (Validators.isValidIP (str3));

		String str4 = "127.0.0";
		assertFalse (Validators.isValidIP (str4));

		String str5 = "127.0.33.222.22";
		assertFalse (Validators.isValidIP (str5));
		str5 = "127.0.33.222.";
		assertFalse (Validators.isValidIP (str5));

		String str6 = "192.168.1.257";
		assertFalse (Validators.isValidIP (str6));
		str6 = "192.168.256.2";
		assertFalse (Validators.isValidIP (str6));
		str6 = "192.303.1.250";
		assertFalse (Validators.isValidIP (str6));
		str6 = "555.168.1.2";
		assertFalse (Validators.isValidIP (str6));

		str6 = "-115.168.1.2";
		assertFalse (Validators.isValidIP (str6));
		str6 = "115.-168.1.2";
		assertFalse (Validators.isValidIP (str6));
		str6 = "115.168.-1.2";
		assertFalse (Validators.isValidIP (str6));
		str6 = "115.168.1.-2";
		assertFalse (Validators.isValidIP (str6));

		String str7 = "";
		assertFalse (Validators.isValidIP (str7));

		str7 = null;
		assertFalse (Validators.isValidIP (str7));

		str7 = "Not a Number";
		assertFalse (Validators.isValidIP (str7));
	}

}
