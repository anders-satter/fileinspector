/*
 * Created on 2005-okt-13
 */
package fileinspect.log;

/**
 * Representerar ett TDE logentry
 */
public class LogEntry {

	private String iLogEntry;
	private String iRef;
	/**
	 * Constructor.
	 * 
	 */
	public LogEntry() {
		super();
	}

	public void parse() {

	}

	/**
	 * S�tter LogEntry
	 * @param String aLogEntry 
	 */
	public void setLogEntry(String aLogEntry) {
		iLogEntry = aLogEntry;
	}

	/**
	 * H�mtar LogEntry
	 * @return String med LogEntry
	 */
	public String getLogEntry() {
		return iLogEntry;
	}
	public String getRef() {
		return iRef;
	}

	public void setRef(String aString) {
		iRef = aString;
	}

}
