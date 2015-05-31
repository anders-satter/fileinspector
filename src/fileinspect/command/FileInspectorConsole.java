package fileinspect.command;

import fileinspect.ui.resource.Console;

/**
 * This is the command line version of the FileInspector application.
 *
 */
public class FileInspectorConsole implements Console {

	private int iMonitorFrequency = 2000;
	private String iDirectory;
	private String iFileMatchRegExp;
	private final boolean cDEBUG = false;
	private boolean iVerbose = false;

	public static void main(String[] args) {
		FileInspectorConsole tFileInspectorConsole = new FileInspectorConsole();
		tFileInspectorConsole.setDirectory(args[0]);
		if (args.length > 1 ){
			tFileInspectorConsole.setFileMatchRegExp(args[1]);			
		}
		tFileInspectorConsole.startMonitor();

	}

	/**
	 * S�tter Directory
	 * @param String aDirectory
	 */
	public void setDirectory(String aDirectory) {
		iDirectory = aDirectory;
	}

	private InspectionPerformer iInspectionPerformer;
	public void startMonitor() {
		iInspectionPerformer = new InspectionPerformer();
		iInspectionPerformer.setConsole(this);
		iInspectionPerformer.setFileMatchRegExp(iFileMatchRegExp);
		iInspectionPerformer.init(iDirectory);
		iInspectionPerformer.setMonitorFrequency(iMonitorFrequency);				
		iInspectionPerformer.start();

	}

	public void stopMonitor() {
		iInspectionPerformer.setRunEnabled(false);

	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#decorateExceptions()
	 */
	public void decorateExceptions() {
		return;

	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#manageDocLength()
	 */
	public void manageDocLength() {
		return;

	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#print(java.lang.String)
	 */
	public void print(String aMessage) {
		handleReversibleConsolePrinting();
		System.out.print(aMessage);
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#println(java.lang.String, boolean)
	 */
	public void println(String aMessage, boolean aIsBold) {
		handleReversibleConsolePrinting();
		if (aIsBold) {
			/*
			 * if it is bold do a line feed instead.
			 */
			System.out.println();
		}
		System.out.println(aMessage);
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#println(java.lang.String)
	 */
	public void println(String aMessage) {
		handleReversibleConsolePrinting();
		System.out.println(aMessage);
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#printlnDebug(java.lang.String)
	 */
	public void printlnDebug(String aMessage) {
		handleReversibleConsolePrinting();
		if (cDEBUG) {
			System.out.println(aMessage);
		}
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#printlnVerbose(java.lang.String)
	 */
	public void printlnVerbose(String aMessage) {
		handleReversibleConsolePrinting();
		if (iVerbose) {
			System.out.println(aMessage);
		}
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#setConsoleTitle(java.lang.String)
	 */
	public void setConsoleTitle(String aTitle) {
		return;
	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#printErasableMessage()
	 */
	private String iPrintedReversibleMessage;

	public void printReversibleMessage(String aMessage) {
		iPrintedReversibleMessage = aMessage;
		System.out.print(aMessage + '\r');
	}

	private void handleReversibleConsolePrinting() {
		if (iPrintedReversibleMessage != null && iPrintedReversibleMessage.length() > 0) {
			int tLen = iPrintedReversibleMessage.length();
			char[] tRevMessage = new char[tLen];
			for (int i = 0; i < tLen; i++) {
				tRevMessage[i] = ' ';
			}
			System.out.print(String.valueOf(tRevMessage) + '\r');
			iPrintedReversibleMessage = "";
		}
	}

	/**
	 * @return
	 */
	public String getFileMatchRegExp() {
		return iFileMatchRegExp;
	}

	/**
	 * @param aString
	 */
	public void setFileMatchRegExp(String aString) {
		iFileMatchRegExp = aString;
	}

}
