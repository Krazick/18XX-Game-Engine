package ge18xx.utilities;

public interface ParsingRoutineIO extends ParsingRoutineI {
	/* Support Callback Functions when a XML Node Name matches a specified value with a MetaObject 
	 * Passed in */
	
	public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject);
	public void foundItemMatchKey1 (XMLNode aChildNode, Object aMetaObject1, Object aMetaObject2);

}
