package ge18xx.utilities;

public class NoSpaceString {
	public final String NULL_STRING = GUI.NULL_STRING;
	String string;

	NoSpaceString () {
		string = NULL_STRING;
	}

	NoSpaceString (String aString) {
		if (aString == NULL_STRING) {
			string = aString;
		} else {
			if (aString.length () == 0) {
				string = NULL_STRING;
			} else if (aString.indexOf (" ") >= 0) {
				string = NULL_STRING;
			} else {
				string = aString;
			}
		}
	}

	public boolean hasValue () {
		return (string != NULL_STRING);
	}

	public boolean equals (String aString) {
		boolean tEquals;

		if (hasValue ()) {
			if (aString == NULL_STRING) {
				tEquals = false;
			} else {
				tEquals = string.equals (aString);
			}
		} else {
			tEquals = false;
		}

		return tEquals;
	}

	public String getString () {
		return string;
	}

	@Override
	public String toString () {
		return getString ();
	}
}
