/*
 * Created on 2005-jun-30
 */
package fileinspect.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import fileinspect.command.LineFilter;
import fileinspect.ui.resource.Console;
import fileinspect.util.Utils;

/**
 * A monitored file. 
 */
public class MonitoredFile {

	private long iLength;
	private long iLastModified;
	private String iName;
	private static String kLastPrintedFileName;
	private boolean iHasBeenModified;
	private File iFile;
	private long iCursor = -1;
	private Console iConsole;
	private boolean iFileExists = true;
	private LineFilter iLineFilter;
	private StringBuilder iFileNameBuffer;

	/**
	 * Konstruktor.
	 *  
	 */
	public MonitoredFile() {
		super();
		iFileNameBuffer = new StringBuilder();
	}

	public LineFilter getLineFilter() {
		return iLineFilter;
	}

	public void setLineFilter(LineFilter aLineFilter) {
		iLineFilter = aLineFilter;
	}

	/**
	 * Used to position the the file cursor to 
	 * the correct place before starting scanning
	 * @param aFindLastRowInFile
	 */
	public void init(boolean aFindLastRowInFile) {
		iFile = new File(iName);
		if (iFile.exists()) {
			if (aFindLastRowInFile) {
				iLength = iFile.length();
			} else {
				iLength = 0;
			}

			iLastModified = iFile.lastModified();
			if (aFindLastRowInFile) {
				/*
				 * File is added during startup, we need to move the cursor to the last
				 * row of the file so as to avoid the whole file being read at first
				 * monitoring.
				 */
				try {
					BufferedReader bf = new BufferedReader(new FileReader(iName));
					moveCursorToEndOfFile(bf);
					bf.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				/* just in case, should always be -1 when init is run */
				iCursor = -1;
			}
		}
	}

	private void moveCursorToEndOfFile(BufferedReader bf) throws IOException {
		String tLine = "";
		while ((tLine = bf.readLine()) != null) {
			iCursor++;
		}
	}

	/**
	 * Check the file for new rows and file names and print them
	 * @param aMonitorInfo
	 */
	public synchronized void monitor(MonitorInfo aMonitorInfo) {
		if (aMonitorInfo.getLineFilter()!= null){
			iLineFilter = aMonitorInfo.getLineFilter(); 
		}
		long tLength = iFile.length();
		if (tLength != iLength) {
			/* the length of the file has been modified */
			if (iLength == 0) {
				/* file was probably just added on the fly, read the whole file */
				iConsole.printlnDebug("file added - first read.");
				iLength = tLength;
				try {
					BufferedReader bf = new BufferedReader(new FileReader(iName));
					printFileNameIfChanged();
					printChangedLines(aMonitorInfo, bf);
					bf.close();
				} catch (FileNotFoundException e) {
					iFileExists = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				iConsole.printlnDebug("file monitored.");
				iLength = tLength;
				long tNumLines = -1;
				try {
					BufferedReader bf = new BufferedReader(new FileReader(iName));
					tNumLines = moveToPreviousEndOfFile(tNumLines, bf);
					printFileNameIfChanged();
					printChangedLines(aMonitorInfo, bf);
					bf.close();
				} catch (FileNotFoundException e) {
					iFileExists = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//}
	}

	
	/**
	 * stega fram till det tidigare slutet av filen, eftersom vi har
	 * �ppnat filen p� nytt
	 * 
	 * @param tNumLines
	 * @param bf
	 * @return
	 * @throws IOException
	 */
	private long moveToPreviousEndOfFile(long tNumLines, BufferedReader bf)
			throws IOException {
		String tLine;
		while ((tNumLines++ < (iCursor)) && ((tLine = bf.readLine()) != null)){
			iConsole.printlnDebug("tNumLines " + String.valueOf(tNumLines));						
		}
		return tNumLines;
	}

	private void printFileNameIfChanged() {
		if (!iName.equalsIgnoreCase(kLastPrintedFileName)) {
			/*
			 * Reading from a new file here, print the name
			 */
			
			/*
			 * This should be added to a buffer and only printed when we know that
			 * printing is done because the line matches the line filter 
			 */
			
			//iConsole.println("--->[ " + iName + " ]", true);
			
			Utils.setBuffer(iFileNameBuffer, "--->[ " + iName + " ]");
			//kLastPrintedFileName = iName;
		}
	}

	private void printChangedLines(MonitorInfo aMonitorInfo, BufferedReader bf)
			throws IOException {
		String tLine;
		while ((tLine = bf.readLine()) != null) {
			aMonitorInfo.setChangesFound(true);
			iCursor++;
			iConsole.printlnDebug("iCursor " + String.valueOf(iCursor));
			if (iLineFilter != null){
				if (iLineFilter.receivedMatch(tLine.toLowerCase())){
					printFileName();
					iConsole.println(tLine);
				}
			} else {
				/*
				 * we have no linefilter
				 */
				iConsole.println(tLine);
			}			
		}
	}

	
	private void printFileName(){
		if (Utils.isBufferFilled(iFileNameBuffer)){
			iConsole.println(iFileNameBuffer.toString(), true);	
		}
		Utils.wipeBuffer(iFileNameBuffer);
		kLastPrintedFileName = iName;
	}
	
	
	/**
	 * S�tter Name
	 * @param String aName
	 */
	public void setName(String aName) {
		iName = aName;
	}

	/**
	 * H�mtar Length
	 * @return long med Length
	 */
	public long getLength() {
		return iLength;
	}

	/**
	 * H�mtar LastModified
	 * @return long med LastModified
	 */
	public long getLastModified() {
		return iLastModified;
	}

	/**
	 * H�mtar Name
	 * @return String med Name
	 */
	public String getName() {
		return iName;
	}

	/**
	 * H�mtar HasBeenModified
	 * @return boolean med HasBeenModified
	 */
	public boolean getHasBeenModified() {
		return iHasBeenModified;
	}

	/**
	 * S�tter Console
	 * @param Console aConsole
	 */
	public void setConsole(Console aConsole) {
		iConsole = aConsole;
	}

	/**
	 * H�mtar File
	 * @return File med File
	 */
	public File getFile() {
		return iFile;
	}

	/**
	 * S�tter Cursor
	 * @param long aCursor
	 */
	public void setCursor(long aCursor) {
		iCursor = aCursor;
	}

	/**
	 * H�mtar Cursor
	 * @return long med Cursor
	 */
	public long getCursor() {
		return iCursor;
	}

	/**
	 * H�mtar FileExists
	 * @return boolean med FileExists
	 */
	public boolean getFileExists() {
		return iFileExists;
	}
}