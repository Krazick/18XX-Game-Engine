package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import geUtilities.GUI;

public class SoldCompanies {
	public static final JLabel NO_SOLD_COMPANIES = null;
	List<String> soldCompanies;

	SoldCompanies () {
		soldCompanies = new LinkedList<> ();
	}

	public void addSoldCompanies (String aCompanyAbbrev) {
		soldCompanies.add (aCompanyAbbrev);
	}

	public void clearAllSoldCompanies () {
		soldCompanies.clear ();
	}

	public boolean hasSoldCompany (String aCompanyAbbrev) {
		boolean tHasSoldCompany;

		tHasSoldCompany = false;
		for (String tSoldCompanyAbbrev : soldCompanies) {
			if (tSoldCompanyAbbrev.equals (aCompanyAbbrev)) {
				tHasSoldCompany = true;
			}
		}

		return tHasSoldCompany;
	}

	public void parse (String aDelimiter, String aSoldCompanies) {
		String [] tAllSoldCompanies;

		tAllSoldCompanies = aSoldCompanies.split (aDelimiter);
		for (String tASoldCompany : tAllSoldCompanies) {
			if (tASoldCompany.length () > 0) {
				soldCompanies.add (tASoldCompany);
			}
		}
	}

	public void printInfo () {
		System.out.print ("Sold Companies are [ ");		// PRINTLOG
		for (String tCompanyAbbrev : soldCompanies) {
			System.out.print (tCompanyAbbrev + " ");
		}
		System.out.println ("]");						// PRINTLOG
	}

	public String uniqueCompanies (String aDelimeter) {
		String tUniqueCompanies = "";

		for (String tCompanyAbbrev : soldCompanies) {
			if (!tUniqueCompanies.contains (tCompanyAbbrev)) {
				tUniqueCompanies += tCompanyAbbrev + " ";
			}
		}

		return tUniqueCompanies;
	}

	public String toString (String aDelimiter) {
		String tCompaniesSold = "";

		tCompaniesSold = String.join (aDelimiter, soldCompanies);

		return tCompaniesSold;
	}

	public void undoSoldCompany (String aCompanyAbbrev) {
		soldCompanies.remove (aCompanyAbbrev);
	}

	public void undoClearSoldCompany (String aDelimiter, String aSoldCompanies) {
		parse (aDelimiter, aSoldCompanies);
	}

	public JLabel buildSoldCompaniesLabel () {
		JLabel tSoldCompaniesLabel = NO_SOLD_COMPANIES;
		String tSoldCompanies;

		if (soldCompanies.size () > 0) {
			tSoldCompanies = "Sold: " + uniqueCompanies (GUI.COMMA_SPACE);
			tSoldCompaniesLabel = new JLabel (tSoldCompanies);
		}

		return tSoldCompaniesLabel;
	}
}
