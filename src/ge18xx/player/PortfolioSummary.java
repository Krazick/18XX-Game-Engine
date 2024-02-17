package ge18xx.player;

import javax.swing.border.Border;

import ge18xx.round.action.ActorI;
import geUtilities.GUI;

public class PortfolioSummary {
	public static final String PRIVATE_CORP_TYPE = "Private";
	public static final String MINOR_CORP_TYPE = "Minor";
	public static final String SHARE_CORP_TYPE = "Share";
	public static final Border NO_BORDER = null;
	String abbrev;
	String type;
	String note;
	int count;
	int percentage;
	int percentBought;
	boolean noTouchPass;
	boolean isPresident;
	boolean willFold;
	ActorI.ActionStates status;
	Border corporateColorBorder;

	PortfolioSummary (String aAbbrev, String aType, int aCount, int aPercentage, int aPercentBought, 
			boolean aIsPresident, Border aCorporateColorBorder, String aNote, boolean aNoTouchPass,
			boolean aWillFold, ActorI.ActionStates aStatus) {
		abbrev = aAbbrev;
		count = aCount;
		percentage = aPercentage;
		percentBought = aPercentBought;
		noTouchPass = aNoTouchPass;
		isPresident = aIsPresident;
		if (SHARE_CORP_TYPE.equals (aType)) {
			corporateColorBorder = aCorporateColorBorder;
		} else {
			corporateColorBorder = NO_BORDER;
		}
		note = aNote;
		type = aType;
		willFold = aWillFold;
		status = aStatus;
	}

	public String getAbbrev () {
		return abbrev;
	}

	public int getCount () {
		return count;
	}

	public int getPrecentage () {
		int tPercentage;
		
		if (status == ActorI.ActionStates.Unformed) {
			tPercentage = 100;
		} else {
			tPercentage = percentage;
		}

		return tPercentage;
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

	public void addPercentBought (int aPercentBought) {
		percentBought += aPercentBought;
	}
	
	public void setIsPresident (boolean aIsPresident) {
		isPresident = aIsPresident;
	}

	public String getSummary () {
		String tOwnershipLabel;

		tOwnershipLabel = getAbbrev () + " ";
		if (PRIVATE_CORP_TYPE.equals (type)) {
			tOwnershipLabel += type + " Cert";
		} else if (SHARE_CORP_TYPE.equals (type)) {
			tOwnershipLabel +=  getCertCountText () + getPercentageText ();
			if (isPresident ()) {
				tOwnershipLabel += " Prez";
			}
		} else {
			tOwnershipLabel += type + " " + getCertCountText () + getPercentageText ();
			if (isPresident ()) {
				tOwnershipLabel += " Prez";
			}
		}

		return tOwnershipLabel;
	}

	private String getPercentageText () {
		String tGetPercentageText;
		int tPercentCanSell;
		
		if (noTouchPass) {
			tPercentCanSell = percentage - percentBought;
			if (percentBought > 0) {
				tGetPercentageText = tPercentCanSell + "% + " + percentBought + "%";
			} else {
				tGetPercentageText = getPrecentage () + "%";
			}
		} else {
			tGetPercentageText = getPrecentage () + "%";
		}
		
		return tGetPercentageText;
	}

	private String getCertCountText () {
		String tCertCountText;

		if (status == ActorI.ActionStates.Unformed) {
			tCertCountText = GUI.EMPTY_STRING;
		} else {
			tCertCountText = count + " Cert";
			if (count > 1) {
				tCertCountText += "s";
			}
			tCertCountText += "/";
		}
		
		return tCertCountText;
	}

	public String getNote () {
		return note;
	}
	
	public boolean willFold () {
		return willFold;
	}
}
