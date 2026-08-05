package ge18xx.player.ComboBoxInTable;


public class ShareCompanyAbbrev {
	private String abbrev;
	
	public ShareCompanyAbbrev (String aAbbrev) {
		super ();
		setAbbrev (aAbbrev);
	}
	
	public String getAbbrev () {
		return abbrev;
	}
	
	public void setAbbrev (String aAbbrev) {
		abbrev = aAbbrev;
	}
	
	@Override
	public String toString () {
		return abbrev;
	}
}
