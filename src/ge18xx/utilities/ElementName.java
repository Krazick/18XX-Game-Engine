package ge18xx.utilities;

public class ElementName extends NoSpaceString {
	public static final ElementName NO_ELEMENT_NAME = null;

	public ElementName (String aString) {
		super (aString);

		String tFirstChar;

		// Given the Element Value passed in is a valid String with No Spaces
		// Verify the first character of the attribute is Upper Case.
		if (string != NULL_STRING) {
			tFirstChar = string.substring (0, 1);
			if ((tFirstChar.equals (tFirstChar.toLowerCase ()))) {
				setString (NULL_STRING);
			}
		}
	}

	/**
	 * If the created ElementName has a Valid No-Space-String with the first
	 * Character as lowerCase, this will return true.
	 *
	 * @return If the String provided has been stored by the Constructor as Non-Null, return TRUE, otherwise FALSE
	 *
	 */
	public boolean validElementName () {
		boolean tValidElementName;

		tValidElementName = false;
		if (string != NULL_STRING) {
			tValidElementName = true;
		}

		return tValidElementName;
	}
}
