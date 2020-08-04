package ge18xx.game;

import ge18xx.toplevel.XMLFrame;
import ge18xx.utilities.AttributeName;
import ge18xx.utilities.XMLElement;
import ge18xx.utilities.XMLNode;

public class FrameInfo {
	public static final AttributeName AN_NAME = new AttributeName ("name");
	public static final AttributeName AN_WIDTH = new AttributeName ("width");
	public static final AttributeName AN_HEIGHT = new AttributeName ("height");
	public static final AttributeName AN_XLOCATION = new AttributeName ("xLocation");
	public static final AttributeName AN_YLOCATION= new AttributeName ("yLocation");
	public static final AttributeName AN_VISIBLE = new AttributeName ("visible");
	public static final FrameInfo NO_FRAME_INFO = null;
	public static final String NO_FRAME_NAME = null;
	int height;
	int width;
	int xLocation;
	int yLocation;
	boolean visible;
	String name;

	public FrameInfo (XMLFrame aXMLFrame) {
		String tFrameName;
		String tPattern = "^(.*) \\([A-Za-z][A-Za-z0-9_]*\\)$";
		
		height = aXMLFrame.getHeight ();
		width = aXMLFrame.getWidth ();
		xLocation = aXMLFrame.getLocation ().x;
		yLocation = aXMLFrame.getLocation ().y;
		visible = aXMLFrame.isVisible ();
		
		tFrameName = aXMLFrame.getTitle ();
		if (tFrameName.matches (tPattern)) {
			int tIndexLastSpace = tFrameName.lastIndexOf (" ");
			tFrameName = tFrameName.substring (0, tIndexLastSpace);
		}
		name = tFrameName;
	}
	
	public XMLElement appendXMLFrameAttributes (XMLElement aXMLFrameElement) {
		
		aXMLFrameElement.setAttribute (AN_NAME, name);
		aXMLFrameElement.setAttribute (AN_WIDTH, width);
		aXMLFrameElement.setAttribute (AN_HEIGHT, height);
		aXMLFrameElement.setAttribute (AN_XLOCATION, xLocation);
		aXMLFrameElement.setAttribute (AN_YLOCATION, yLocation);
		aXMLFrameElement.setAttribute (AN_VISIBLE, visible);
	
		return aXMLFrameElement;
	}

	public FrameInfo (XMLNode aFrameInfoNode) {
		height = aFrameInfoNode.getThisIntAttribute (AN_HEIGHT, 100);
		width = aFrameInfoNode.getThisIntAttribute (AN_WIDTH, 100);
		xLocation = aFrameInfoNode.getThisIntAttribute (AN_XLOCATION, 20);
		yLocation = aFrameInfoNode.getThisIntAttribute (AN_YLOCATION, 20);
		visible = aFrameInfoNode.getThisBooleanAttribute (AN_VISIBLE);
		name = aFrameInfoNode.getThisAttribute (AN_NAME);
	}

	public String toString () {
		String tFrameInfo;
		
		tFrameInfo = "Name: " + name + "\n";
		tFrameInfo += "Width: " + width + "\n";
		tFrameInfo += "Height: " + height + "\n";
		tFrameInfo += "X-Location: " + xLocation  + "\n";
		tFrameInfo += "Y-Location: " + yLocation+ "\n";
		tFrameInfo += "Visible: " + visible + "\n";

		return tFrameInfo;
	}
	
	public int getHeight () {
		return height;
	}

	public int getWidth () {
		return width;
	}

	public int getXLocation () {
		return xLocation;
	}

	public int getYLocation () {
		return yLocation;
	}
	
	public boolean getVisible () {
		return visible;
	}
	
	public String getName () {
		return name;
	}
}
