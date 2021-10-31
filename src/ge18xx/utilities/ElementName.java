package ge18xx.utilities;

public class ElementName extends NoSpaceString {
	public static final ElementName NO_ELEMENT_NAME = null;
	
	public ElementName (String aString) {
		super (aString);
		
		String tFirstChar;
		
		// Given the Element Value passed in is a valid String with No Spaces
		// Verify the first character of the attribute is Upper Case.
		if (string != NO_STRING) {
			tFirstChar = string.substring (0, 1);
			if ((tFirstChar.equals (tFirstChar.toLowerCase ()))) {
				string = NO_STRING;
			}
		}
	}
}
