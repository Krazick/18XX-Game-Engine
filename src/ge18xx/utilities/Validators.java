package ge18xx.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validators {

	/**
	 * Test if a provided String is a Valid IPv4 or Domain Name. Test for an IPv4 first, then Domain Name
	 * 
	 * @param aIPorDomain the String to be tested
	 * 
	 * @return true if it meets the formatting for IPv4 or Domain Name
	 * 
	 */
	public static boolean isValidIPorDomain (String aIPorDomain) {
		boolean tIsValidIPorDomain = false;

		if (isValidIP (aIPorDomain)) {
			tIsValidIPorDomain = isValidIP (aIPorDomain);
		} else if (isValidDomain (aIPorDomain)) {
			tIsValidIPorDomain = isValidDomain (aIPorDomain);
		}

		return tIsValidIPorDomain;
	}

	/**
	 * Test if a provided IPAddress ios a valid IPv4 (###.###.###.###) where each Number is between 0 and 255
	 * 
	 * @param aIPAddress the IP Address to be tested.
	 * 
	 * @return true if appears to be a valid IP v 4 Address.
	 * 
	 */
	public static boolean isValidIP (String aIPAddress) {
		try {
			if (aIPAddress == null || aIPAddress.isEmpty ()) {
				return false;
			}

			String [] tParts = aIPAddress.split ("\\.");
			if (tParts.length != 4) {
				return false;
			}

			for (String tAPart : tParts) {
				int tPartIP = Integer.parseInt (tAPart);
				if ((tPartIP < 0) || (tPartIP > 255)) {
					return false;
				}
			}
			if (aIPAddress.endsWith (".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * Test if the provide String matches the pattern for a Domain Name
	 * 
	 * @param aPossibleDomainName the String to test 
	 * 
	 * @return True if it matches the domain name Regex Pattern
	 * 
	 */
	public static boolean isValidDomain (String aPossibleDomainName) {
		// Regex to check valid domain name.
		String tDomainRegex = "^((?!-)[A-Za-z0-9-]" + "{1,63}(?<!-)\\.)" + "+[A-Za-z]{2,6}";

		// Compile the ReGex
		Pattern tDomainPattern = Pattern.compile (tDomainRegex);

		// If the string is empty
		// return false
		if (aPossibleDomainName == null) {
			return false;
		}

		// Pattern class contains matcher()
		// method to find the matching
		// between the given string and
		// regular expression.
		Matcher tDomainMatcher = tDomainPattern.matcher (aPossibleDomainName);

		// Return if the string
		// matched the ReGex
		return tDomainMatcher.matches ();
	}

}
