package ge18xx.player;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

public class SoldCompanies {
	List<String> soldCompanies;

	SoldCompanies () {
		soldCompanies = new LinkedList<String> ();
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
		String[] tAllSoldCompanies;
		
		tAllSoldCompanies = aSoldCompanies.split (aDelimiter);
		for (String tASoldCompany : tAllSoldCompanies) {
			if (tASoldCompany.length () > 0) {
				soldCompanies.add (tASoldCompany);
			}
		}
	}
	
	public void printInfo () {
		System.out.print ("Sold Companies are [ " );
		for (String tCompanyAbbrev : soldCompanies) {
			System.out.print (tCompanyAbbrev + " ");
		}
		System.out.println ("]");
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
		JLabel tSoldCompaniesLabel = null;
		String tSoldCompanies;
		
		if (soldCompanies.size () > 0) {
			tSoldCompanies = "Sold: " + toString (", ");
			tSoldCompaniesLabel = new JLabel (tSoldCompanies);
		}
		
		return tSoldCompaniesLabel;
	}
}
