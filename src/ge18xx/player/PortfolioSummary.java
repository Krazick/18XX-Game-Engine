package ge18xx.player;

import javax.swing.border.Border;

public class PortfolioSummary {
	String abbrev;
	String type;
	int count;
	int percentage;
	boolean isPresident;
	Border corporateColorBorder;
	String note;
	public final static String PRIVATE_CORP_TYPE = "Private";
	public final static String MINOR_CORP_TYPE = "Minor";
	public final static String SHARE_CORP_TYPE = "Share";
	public final static Border NO_BORDER = null;

	PortfolioSummary (String aAbbrev, String aType, int aCount, int aPercentage, boolean aIsPresident,
			Border aCorporateColorBorder, String aNote) {
		abbrev = aAbbrev;
		count = aCount;
		percentage = aPercentage;
		isPresident = aIsPresident;
		if (SHARE_CORP_TYPE.equals (aType)) {
			corporateColorBorder = aCorporateColorBorder;
		} else {
			corporateColorBorder = NO_BORDER;
		}
		note = aNote;
		type = aType;
	}

	public String getAbbrev () {
		return abbrev;
	}

	public int getCount () {
		return count;
	}

	public int getPrecentage () {
		return percentage;
	}

	public String getType () {
		return type;
	}

	public boolean isPresident () {
		return isPresident;
	}

	public Border getCorporateColorBorder () {
		return corporateColorBorder;
	}

	public void addCount (int aCount) {
		count += aCount;
	}

	public void addPercentage (int aPercentage) {
		percentage += aPercentage;
	}

	public void setIsPresident (boolean aIsPresident) {
		if (aIsPresident) {
			isPresident = aIsPresident;
		}
	}

	public String getSummary () {
		String tOwnershipLabel;

//		tOwnershipLabel = getAbbrev () + "&nbsp;";
		tOwnershipLabel = getAbbrev () + " ";
		if (PRIVATE_CORP_TYPE.equals (type)) {
			tOwnershipLabel += type + " 1 Prez Cert";
		} else if (SHARE_CORP_TYPE.equals (type)) {
			tOwnershipLabel +=  getCertCountText ()  + getPercentageText ();
			if (isPresident ()) {
				tOwnershipLabel += " Prez";
			}
		} else {
			tOwnershipLabel += type + " " + getCertCountText ()  + getPercentageText ();
			if (isPresident ()) {
				tOwnershipLabel += " Prez";
			}
		}
//		tOwnershipLabel = "<html>" + tOwnershipLabel + "</html>";

		return tOwnershipLabel;
	}

	private String getPercentageText () {
		return getPrecentage () + "%";
	}
	
	private String getCertCountText () {
		String tCertCountText;

		tCertCountText = count + " Cert";
		if (count > 1) {
			tCertCountText += "s";
		}
		tCertCountText += "/";
		
		return tCertCountText;
	}
	
	public String getNote () {
		return note;
	}
}
