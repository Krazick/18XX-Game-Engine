package ge18xx.utilities;

public interface ParsingRoutine2IO extends ParsingRoutineIO {
	/*
	 * Support Callback Functions when a XML Node Name matches a specified value
	 * with a MetaObject Passed in
	 */

	public void foundItemMatchKey2 (XMLNode aChildNode);

	public void foundItemMatchKey2 (XMLNode aChildNode, Object aMetaObject);

}
