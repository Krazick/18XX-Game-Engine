package ge18xx.company;

import geUtilities.xml.XMLNode;

public class LoadedCertificate {
	boolean isPresidentShare;
	int percentage;
	String companyAbbrev; // Abbreviation of Company used during Load Save Game

	public LoadedCertificate () {
		setIsPresidentShare (false);
		setPercentage (0);
		setCompanyAbbrev ("");
	}

	public LoadedCertificate (XMLNode aXMLNode) {
		setIsPresidentShare (aXMLNode.getThisBooleanAttribute (Certificate.AN_IS_PRESIDENT));
		setPercentage (aXMLNode.getThisIntAttribute (Certificate.AN_PERCENTAGE));
		setCompanyAbbrev (aXMLNode.getThisAttribute (Corporation.AN_ABBREV));
	}

	public boolean getIsPresidentShare () {
		return isPresidentShare;
	}

	public int getPercentage () {
		return percentage;
	}

	public String getCompanyAbbrev () {
		return companyAbbrev;
	}

	private void setIsPresidentShare (boolean aIsPresidentShare) {
		isPresidentShare = aIsPresidentShare;
	}

	private void setPercentage (int aPercentage) {
		percentage = aPercentage;
	}

	private void setCompanyAbbrev (String aCompanyAbbrev) {
		companyAbbrev = aCompanyAbbrev;
	}
}
