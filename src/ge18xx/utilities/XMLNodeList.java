package ge18xx.utilities;

import org.w3c.dom.NodeList;

public class XMLNodeList {
	private static Object NO_OBJECT = null;
	/* Supporting parsing an XML Node's Children -- Look for a specific Element Name and call the Parsing 
	 * Routine back 'foundItemMatch' with the Child Node that matches
	 */
	private ParsingRoutineI parsingRoutineI;
	private Object metaObject1;
	private Object metaObject2;
	
	public XMLNodeList (ParsingRoutineI aParsingRoutine) {
		this (aParsingRoutine, NO_OBJECT);
	}
	
	public XMLNodeList (ParsingRoutineI aParsingRoutine, Object aMetaObject1) {
		this (aParsingRoutine, aMetaObject1, NO_OBJECT);
	}
	
	public XMLNodeList (ParsingRoutineI aParsingRoutine, Object aMetaObject1, Object aMetaObject2) {
		parsingRoutineI = aParsingRoutine;
		metaObject1 = aMetaObject1;
		metaObject2 = aMetaObject2;
	}
	
	public int getChildCount (XMLNode aNode, ElementName aThisChildName) {
		XMLNode tChildNode;
		NodeList tChildren;
		String tChildName;
		int tChildrenCount;
		int tIndex, tChildCount;
		
		tChildren = aNode.getChildNodes ();
		tChildrenCount = tChildren.getLength ();
		tChildCount = 0;
		for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {
			tChildNode = new XMLNode (tChildren.item (tIndex));
			tChildName = tChildNode.getNodeName ();
			if (tChildName.equals (aThisChildName.getString ())) {
				tChildCount++;
			}
		}
		
		return tChildCount;		
	}

	private void testAndCallback1 (XMLNode aNode, String aThisChildName) {
		String tChildName;
		ParsingRoutineIO tParsingRoutineIO;
		ParsingRoutineIOO tParsingRoutineIOO;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName)) {
			if (metaObject1 == NO_OBJECT) {
				parsingRoutineI.foundItemMatchKey1 (aNode);
			} else if (metaObject2 == NO_OBJECT){
				tParsingRoutineIO = (ParsingRoutineIO) parsingRoutineI;
				tParsingRoutineIO.foundItemMatchKey1 (aNode, metaObject1);
			} else {
				tParsingRoutineIOO = (ParsingRoutineIOO) parsingRoutineI;
				tParsingRoutineIOO.foundItemMatchKey1 (aNode, metaObject1, metaObject2);
			}
		}
	}
	
	private void testAndCallback2 (XMLNode aNode, String aThisChildName) {
		String tChildName;
		ParsingRoutine2I tParsingRoutine2I;
		ParsingRoutine2IO tParsingRoutine2IO;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName)) {
			if (metaObject1 == NO_OBJECT) {
				tParsingRoutine2I = (ParsingRoutine2I) parsingRoutineI;
				tParsingRoutine2I.foundItemMatchKey2 (aNode);
			} else {
				tParsingRoutine2IO = (ParsingRoutine2IO) parsingRoutineI;
				tParsingRoutine2IO.foundItemMatchKey2 (aNode, metaObject1);
			}
		}
	}
	
	private void testAndCallback3 (XMLNode aNode, String aThisChildName) {
		String tChildName;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName)) {
			((ParsingRoutine3I) parsingRoutineI).foundItemMatchKey3 (aNode);
		}
	}
	
	public void parseXMLNodeList (XMLNode aNode, ElementName aThisChildName) {
		XMLNode tChildNode;
		NodeList tChildren;
		int tChildrenCount;
		int tIndex;
		
		tChildren = aNode.getChildNodes ();
		if (tChildren != null) {
			tChildrenCount = tChildren.getLength ();
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {				
				tChildNode = new XMLNode (tChildren.item (tIndex));
				testAndCallback1 (tChildNode, aThisChildName.getString ());
			}
		}
	}
	
	public void parseXMLNodeList (XMLNode aNode, ElementName aThisChildName1, ElementName aThisChildName2) {
		XMLNode tChildNode;
		NodeList tChildren;
		int tChildrenCount;
		int tIndex;
		
		tChildren = aNode.getChildNodes ();
		if (tChildren != null) {
			tChildrenCount = tChildren.getLength ();
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {				
				tChildNode = new XMLNode (tChildren.item (tIndex));
				testAndCallback1 (tChildNode, aThisChildName1.getString ());
				testAndCallback2 (tChildNode, aThisChildName2.getString ());
			}
		}
	}
	
	public void parseXMLNodeList (XMLNode aNode, ElementName aThisChildName1, ElementName aThisChildName2, ElementName aThisChildName3) {
		XMLNode tChildNode;
		NodeList tChildren;
		int tChildrenCount;
		int tIndex;
		
		tChildren = aNode.getChildNodes ();
		if (tChildren != null) {
			tChildrenCount = tChildren.getLength ();
			for (tIndex = 0; tIndex < tChildrenCount; tIndex++) {				
				tChildNode = new XMLNode (tChildren.item (tIndex));
				testAndCallback1 (tChildNode, aThisChildName1.getString ());
				testAndCallback2 (tChildNode, aThisChildName2.getString ());
				testAndCallback3 (tChildNode, aThisChildName3.getString ());
			}
		}
	}
}
