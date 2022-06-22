package ge18xx.utilities;

public class NoSpaceString {
	public static final String NULL_STRING = GUI.NULL_STRING;
	String string;

	NoSpaceString () {
		setString (NULL_STRING);
	}

	NoSpaceString (String aString) {
		if (aString == NULL_STRING) {
			setString (aString);
		} else {
			if (aString.length () == 0) {
				setString (NULL_STRING);
			} else if (aString.indexOf (" ") >= 0) {
				setString (NULL_STRING);
			} else {
				setString (aString);
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

	public void setString (String aString) {
		string = aString;
	}
	
	public String getString () {
		return string;
	}

	@Override
	public String toString () {
		return getString ();
	}
}
