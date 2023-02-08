package ge18xx.company;

public class LicenseToken {
	License license;
	boolean active;
	
	public LicenseToken (License aLicense) {
		setLicense (aLicense);
		setActive (false);
	}

	public void setLicense (License aLicense) {
		license = aLicense;
	}
	
	public void setActive (boolean aActive) {
		active = aActive;
	}
	
	public License getLicense () {
		return license;
	}
	
	public boolean isActive () {
		return active;
	}
}
