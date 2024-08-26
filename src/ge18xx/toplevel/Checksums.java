package ge18xx.toplevel;

import java.util.LinkedList;
import java.util.List;

import checksum.Checksum;
import geUtilities.GUI;
import geUtilities.xml.ElementName;

public class Checksums {
	public static final ElementName EN_CHECKSUMS = new ElementName ("Checksums");
	public static final Checksums NO_CHECKSUMS = null;
	public static final int NOT_FOUND = -1;
	List<Checksum> checksums;

	public Checksums () {
		checksums = new LinkedList<Checksum> ();
	}

	public void add (Checksum aChecksum) {
		checksums.add (aChecksum);
	}
	
	public Checksum get (int aIndex) {
		Checksum tChecksum;
		
		if ((aIndex < 0) || (aIndex >= checksums.size ())) {
			tChecksum = Checksum.NO_CHECKSUM;
		} else {
			tChecksum = checksums.get (aIndex);
		}
		
		return tChecksum;
	}
	
	public Checksum getLast () {
		Checksum tLastChecksum;
		
		tLastChecksum = get (checksums.size () - 1);
		
		return tLastChecksum;
	}
	
	public int findIndexFor (int aActionIndex) {
		int tIndex;
		int tCount;
		int tFoundIndex;
		Checksum tChecksum;
		
		tFoundIndex = NOT_FOUND;
		if (!checksums.isEmpty ()) {
			tCount = size ();
			for (tIndex = 0; tIndex < tCount; tIndex++) {
				tChecksum = checksums.get (tIndex);
				if (tChecksum.getActionIndex () == aActionIndex) {
					tFoundIndex = tIndex;
				}
			}
			
		}
		
		return tFoundIndex;
	}
	
	public void removeActionIndex (int aActionIndex) {
		int tIndex;
		
		tIndex = findIndexFor (aActionIndex);
		if (tIndex != NOT_FOUND) {
			remove (tIndex);
		}
	}
	
	public void remove (int aIndex) {
		checksums.remove (aIndex);
	}
	
	public int size () {
		return checksums.size ();
	}
	
	public String getDetailAllChecksums () {
		String tAllDetails;
		
		tAllDetails = GUI.EMPTY_STRING;
		if (!checksums.isEmpty ()) {
			for (Checksum tChecksum : checksums) {
				tAllDetails += tChecksum.getAllDetails ();
			}
		}
		
		return tAllDetails;
	}
}
