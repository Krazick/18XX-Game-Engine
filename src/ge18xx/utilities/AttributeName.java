package ge18xx.utilities;

public class AttributeName extends NoSpaceString {

	public AttributeName (String aString) {
		super (aString);
		
		String tFirstChar;
		
		// Given the Attribute Value passed in is a valid String with No Spaces
		// Verify the first character of the attribute is lower case.
		if (string != NULL_STRING) {
			tFirstChar = string.substring (0, 1);
			if ((tFirstChar.equals (tFirstChar.toUpperCase ()))) {
				string = NULL_STRING;
			}
		}
	}
}
