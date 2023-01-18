package ge18xx.utilities;

import java.util.regex.Pattern;

public class WordWrapping {
	private String LINEBREAK = "\n"; // or "\r\n";

	public WordWrapping () {
		
	}
	
	public WordWrapping (String aLineBreak) {
		LINEBREAK = aLineBreak;
	}

	public String wrap(String string, int lineLength) {
	    StringBuilder b = new StringBuilder();
	    for (String line : string.split(Pattern.quote(LINEBREAK))) {
	        b.append(wrapLine(line, lineLength));
	    }
	    return b.toString();
	}

	private String wrapLine(String line, int lineLength) {
	    if (line.length() == 0) return LINEBREAK;
	    if (line.length() <= lineLength) return line + LINEBREAK;
	    String[] words = line.split(" ");
	    StringBuilder allLines = new StringBuilder();
	    StringBuilder trimmedLine = new StringBuilder();
	    for (String word : words) {
	        if (trimmedLine.length() + 1 + word.length() <= lineLength) {
	            trimmedLine.append(word).append(" ");
	        } else {
	            allLines.append(trimmedLine).append(LINEBREAK);
	            trimmedLine = new StringBuilder();
	            trimmedLine.append(word).append(" ");
	        }
	    }
	    if (trimmedLine.length() > 0) {
	        allLines.append(trimmedLine);
	    }
	    allLines.append(LINEBREAK);
	    return allLines.toString();
	}
}
