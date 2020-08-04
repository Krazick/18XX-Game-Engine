package ge18xx.utilities;

import org.w3c.dom.NodeList;

public class XMLNodeList {
	/* Supporting parsing an XML Node's Children -- Look for a specific Element Name and call the Parsing 
	 * Routine back 'foundItemMatch' with the Child Node that matches
	 */
	private ParsingRoutineI parsingRoutineI;
	private Object metaObject;
	
	public XMLNodeList (ParsingRoutineI aParsingRoutine) {
		this (aParsingRoutine, null);
	}
	
	public XMLNodeList (ParsingRoutineI aParsingRoutine, Object aMetaObject) {
		parsingRoutineI = aParsingRoutine;
		metaObject = aMetaObject;
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
		ParsingRoutineIO tParsingRoutine;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName)) {
			if (metaObject == null) {
				parsingRoutineI.foundItemMatchKey1 (aNode);
			} else {
				tParsingRoutine = (ParsingRoutineIO) parsingRoutineI;
				tParsingRoutine.foundItemMatchKey1 (aNode, metaObject);
			}
		}
	}
	
	private void testAndCallback2 (XMLNode aNode, ElementName aThisChildName) {
		String tChildName;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName.getString ())) {
			((ParsingRoutine2I) parsingRoutineI).foundItemMatchKey2 (aNode);
		}
	}
	
	private void testAndCallback3 (XMLNode aNode, ElementName aThisChildName) {
		String tChildName;
		
		tChildName = aNode.getNodeName ();
		if (tChildName.equals (aThisChildName.getString ())) {
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
				testAndCallback2 (tChildNode, aThisChildName2);
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
				testAndCallback2 (tChildNode, aThisChildName2);
				testAndCallback3 (tChildNode, aThisChildName3);
			}
		}
	}
}
