package ge18xx.market;

import java.awt.Color;

public enum MarketRegion {
	Normal (false, false, true, false, Color.WHITE, Color.BLACK), 
	Yellow (false, false, false, false, Color.YELLOW, Color.BLACK), 
	Orange (false, true, false, false, Color.ORANGE, Color.BLACK), 
	Green (false, true, false, false, Color.GREEN, Color.BLACK), 
	Brown (false, true, false, true, new Color (139, 69, 19), Color.WHITE), 
	Ledge (false, false, true, false, Color.WHITE, Color.BLACK), 
	Closed (true, false, false, false, Color.DARK_GRAY, Color.BLACK), 
	Start (false, false, true, false, Color.WHITE, Color.BLACK), 
	Unused (false, false, false, false, Color.WHITE, Color.BLACK);

	// TODO: 1856 the Brown Area should be FALSE, TRUE, TRUE ??? Review Rules
	
	boolean closeCorporation; 				// Corporation Should Close
	boolean exceedPlayerCorpShareLimit; 	// Player can Hold shares in Excess of Corporation Limit
	boolean countAgainstCertificateLimit; 	// Shares do not count against Player Certificate Limit
	boolean notCountAsBuy;					// Shares purchased do not count as a Stock Buy in a Stock Turn
	Color color;
	Color textColor;
	
	MarketRegion (boolean aCloseCorporation, boolean aExceedPlayerCorpShareLimit, 
			boolean aCountAgainstCertificateLimit, boolean aNotCountAsBuy, 
			Color aColor, Color aTextColor) {
		setCloseCorporation (aCloseCorporation);
		setExceedPlayerCorpShareLimit (aExceedPlayerCorpShareLimit);
		setCountAgainstCertificateLimit (aCountAgainstCertificateLimit);
		setNotCountAsBuy (aNotCountAsBuy);
		setColor (aColor);
		setTextColor (aTextColor);
	}
	
	public Color getColor () {
		return color;
	}
	
	public boolean getCloseCorporation () {
		return closeCorporation;
	}
	
	public boolean getExceedPlayerCorpShareLimit () {
		return exceedPlayerCorpShareLimit;
	}
	
	public boolean getCountAgainstCertificateLimit () {
		return countAgainstCertificateLimit;
	}
	
	public boolean getNotCountAsBuy () {
		return notCountAsBuy;
	}
	
	public String getName () {
		return this.toString ();
	}
	
	public Color getTextColor () {
		return textColor;
	}
	
	public String getToolTip () {
		String tToolTip;
		
		tToolTip = "";
		if (getCloseCorporation ()) {
			tToolTip = "Close Corporation<br>";
		}
		if (getExceedPlayerCorpShareLimit ()) {
			tToolTip += "Player can exceed Corporaiton Share Limit<br>";
		}
		if (!getCountAgainstCertificateLimit ()) {
			tToolTip += "Does not count against Player Certificate Limit<br>";
		}
		if (getNotCountAsBuy ()) {
			tToolTip += "Does not count as a buy<br>";
		}
		
		return tToolTip;
	}
	
	public boolean isLedge () {
		return (this == Ledge);
	}
	
	public boolean isNormal () {
		return ((this == Normal) || isLedge () || isStart ());
	}
	
	public boolean isOpen () {
		return ((this != Closed) && (this != Unused));
	}
	
	public boolean isStart () {
		return (this == Start);
	}
	
	public boolean isUnused () {
		return (this == Unused);
	}
	
	public boolean isUsed () {
		return (this != Unused);
	}

	public void setColor (Color aColor) {
		color = aColor;
	}
	
	public void setCloseCorporation (boolean aCloseCorporation) {
		closeCorporation = aCloseCorporation;
	}
	
	public void setExceedPlayerCorpShareLimit (boolean aExceedPlayerCorpShareLimit) {
		exceedPlayerCorpShareLimit = aExceedPlayerCorpShareLimit;
	}
	
	public void setCountAgainstCertificateLimit (boolean aCountAgainstCertificateLimit) {
		countAgainstCertificateLimit = aCountAgainstCertificateLimit;
	}
	
	public void setNotCountAsBuy (boolean aNotCountAsBuy) {
		notCountAsBuy = aNotCountAsBuy;
	}
	
	public void setTextColor (Color aTextColor) {
		textColor = aTextColor;
	}
}