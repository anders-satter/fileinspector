package fileinspect.ui.resource;

/**
 * Interface implemented by the class responsable for printing
 * strings.
 */
public interface Console {
	public void print(String aMessage);
	public void println(String aMessage);
	public void println(String aMessage, boolean aIsBold);
	public void printlnDebug(String aMessage);
	public void printlnVerbose(String aMessage);
	public void setConsoleTitle(String aTitle);
	public void decorateExceptions();
	public void manageDocLength();
	public void printReversibleMessage(String aMessage);
}
