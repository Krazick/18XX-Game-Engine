package ge18xx.utilities;

//
//  DOMUtil.java
//  Game_18XX
//
//  Created by Mark Smith on 9/15/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

import org.w3c.dom.*;

public class DOMUtil {
	public static Element getFirstElement (Element element, String name) {
		NodeList n1 = element.getElementsByTagName (name);
		
		if (n1.getLength () < 1) {
			throw new RuntimeException ("Element: " + element + " does not contain: " + name);
		}
		
		return (Element) n1.item (0);
	}
	
	public static String getSimpleElementText (Element node, String name) {
		Element namedElement = getFirstElement (node, name);
		
		return getSimpleElementText (namedElement);
	}
	
	public static String getSimpleElementText (Element node) {
		StringBuffer sb = new StringBuffer ();
		NodeList children = node.getChildNodes ();
		
		for (int i = 0; i < children.getLength (); i++) {
			Node child = children.item (i);
			if (child instanceof Text) {
				sb.append (child.getNodeValue ());
			}
		}
		
		return sb.toString ();
	}
}
