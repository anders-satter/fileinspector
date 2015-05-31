/*
 * Created on 2005-sep-21
 */
package fileinspect.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fileinspect.file.MonitorInfo;
import fileinspect.file.MonitoredFile;
import fileinspect.ui.resource.Console;

/**
 * Perform the inspection in a separate thread.
 */
public class InspectionPerformer extends Thread {

	private Console iConsole;
	private int iMonitorFrequency;
	private Collection<MonitoredFile> iMonitoredFileList;
	private String iFileMatchRegExp = "";
	private String iDirectory;
	private Pattern iCompiledPattern = null;
	private boolean iNonExistingFilesAreMonitored = false;
	private boolean iVerbose = false;
	private boolean iRunEnabled = true;
	private final static int MONITOR_INTERVAL = 2000;
	private static final MonitorInfo kMonitorInfo = new MonitorInfo();
	private String iLineFilterRegExp;
	private LineFilter iLineFilter;

	public void init(String aDirectory) {
		iDirectory = aDirectory;
		/*
		 * empty the list
		 */
		iMonitoredFileList = null;
		iConsole.setConsoleTitle("FileInspector " + iDirectory);
		iConsole.println("initializing...");
		iConsole.println("monitoring directory " + iDirectory);
		//startMonitor();
		if (iFileMatchRegExp != null && iFileMatchRegExp.length() > 0) {
			iCompiledPattern = Pattern.compile(iFileMatchRegExp);
		}
		if (iLineFilterRegExp != null){
			iLineFilter = new LineFilter(iLineFilterRegExp.toLowerCase());
			kMonitorInfo.setLineFilter(iLineFilter);
		}
		kMonitorInfo.reset();
		kMonitorInfo.setFirstReportingWasDone(false);
		iIsInitScanning = true;
	}

	public void run() {
		long tNumberOfMonitoredFiles = 0;
		while (iRunEnabled) {
			scanDirectory();
			if (iMonitoredFileList != null && iMonitoredFileList.size() > 0) {
				if (iMonitoredFileList.size() != tNumberOfMonitoredFiles){
					tNumberOfMonitoredFiles = iMonitoredFileList.size(); 
					iConsole.printlnVerbose("Number of monitored files: " + String.valueOf(tNumberOfMonitoredFiles));
				}
				kMonitorInfo.reset();				
				for (Iterator<MonitoredFile> iter = iMonitoredFileList.iterator(); iter.hasNext();) {
					MonitoredFile element = iter.next();
					if (element.getFileExists()) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// empty
						}						
						element.monitor(kMonitorInfo);
					} else {
						iNonExistingFilesAreMonitored = true;
					}
				}
			}

			if (iNonExistingFilesAreMonitored) {
				removeNonExistingFiles();
			}
			iConsole.decorateExceptions();
			if (kMonitorInfo.performReporting()) {
				int tNumMatchingFiles = 0;
				if (iMonitoredFileList!=null){
					tNumMatchingFiles = iMonitoredFileList.size();
				}
				iConsole.printReversibleMessage(
					" === FileInspector v0.9 | dir: "
						+ iDirectory
						+ " | regexp: "
						+ iFileMatchRegExp
						+ " | "
						+ tNumMatchingFiles
						+ " files matched"
						+ " ===");
				kMonitorInfo.setReportInfo(false);
				
			}
			try {
				Thread.sleep(MONITOR_INTERVAL);
			} catch (InterruptedException e) {
				// empty
			}
			iConsole.manageDocLength();
		}
	}

	public void removeNonExistingFiles() {
		Collection<MonitoredFile> tTempExistentFiles = new ArrayList<MonitoredFile>();
		for (Iterator<MonitoredFile> iter = iMonitoredFileList.iterator(); iter.hasNext();) {
			MonitoredFile element = iter.next();
			if (element.getFileExists()) {
				tTempExistentFiles.add(element);
			} else {
				if (iVerbose) {
					iConsole.printlnVerbose("removing nonexistent file from monitoring: " + element.getName() + ".");
				}
			}
		}
		iMonitoredFileList.clear();
		for (Iterator<MonitoredFile> iter = tTempExistentFiles.iterator(); iter.hasNext();) {
			MonitoredFile element = iter.next();
			iMonitoredFileList.add(element);
		}
		tTempExistentFiles = null;
		iNonExistingFilesAreMonitored = false;

		if (iVerbose) {
			StringBuffer tMinFiles = new StringBuffer();
			tMinFiles.append("\n");
			tMinFiles.append("the following files are currently monitored:\n");
			if (iMonitoredFileList.size() < 1) {
				tMinFiles.append("[no files found]");
			}
			for (Iterator<MonitoredFile> iter = iMonitoredFileList.iterator(); iter.hasNext();) {
				MonitoredFile element = iter.next();
				tMinFiles.append(element.getName() + '\n');
			}
			iConsole.printlnVerbose(tMinFiles.toString());
		}
	}

	public void addToMonitoredFileList(File aFile) {
		//boolean tIsFirstInspection = false;
		if (iMonitoredFileList == null) {
			//tIsFirstInspection = true;
			iMonitoredFileList = new ArrayList<MonitoredFile>();
		} else {
			//check if file already is in list
			for (Iterator<MonitoredFile> iter = iMonitoredFileList.iterator(); iter.hasNext();) {
				MonitoredFile element = iter.next();
				try {
					if (aFile.getCanonicalPath().equals(element.getFile().getCanonicalPath())) {
						/* file in list - let's bail out! */
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/* file was not in the list, let's add it! */
		MonitoredFile tFile = new MonitoredFile();
		try {
			tFile.setName(aFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		tFile.setConsole(iConsole);
		//if (tIsFirstInspection){
		if (iIsInitScanning) {
			tFile.init(true);
		} else {
			/*
			 * on the fly inspection, the file was added to monitored directory during
			 * monitoring.
			 */
			iConsole.printlnDebug("");
			iConsole.printlnDebug("---> ON-THE-FLY scanning, whole file should be read");
			iConsole.printlnDebug("---------------------------------------------------");
			tFile.init(false); /* false - whole file will be read at next monitoring */
		}
		//iConsole.printlnVerbose("file " + tFile.getName() + " is now monitored.");
		/* perform inital monitoring */
		tFile.monitor(kMonitorInfo);
		iMonitoredFileList.add(tFile);
	}

	/**
	 * true means its the first scanning
	 */
	boolean iIsInitScanning = false;

	public void scanDirectory() {
		File tDir = new File(iDirectory);

		if (iCompiledPattern == null) {
			if (tDir.isDirectory()) {
				File[] tFile = tDir.listFiles();
				for (int i = 0; i < tFile.length; i++) {
					if (!tFile[i].isDirectory()) {
						addToMonitoredFileList(tFile[i]);
					}
				}
			}
		} else {
			if (tDir.isDirectory()) {
				File[] tFile = tDir.listFiles();
				for (int i = 0; i < tFile.length; i++) {
					if (!tFile[i].isDirectory()) {
						Matcher tMatcher = iCompiledPattern.matcher(tFile[i].getName());
						if (tMatcher.matches()) {
							addToMonitoredFileList(tFile[i]);
						}
					}
				}
			}
		}
		iIsInitScanning = false;
	}

	/**
	 * Constructor.
	 * 
	 */
	public InspectionPerformer() {
		super();

	}

	/**
	 * Constructor.
	 * @param target
	 */
	public InspectionPerformer(Runnable target) {
		super(target);
	}

	/**
	 * Constructor.
	 * @param group
	 * @param target
	 */
	public InspectionPerformer(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * Constructor.
	 * @param name
	 */
	public InspectionPerformer(String name) {
		super(name);
	}

	/**
	 * Constructor.
	 * @param group
	 * @param name
	 */
	public InspectionPerformer(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * Constructor.
	 * @param target
	 * @param name
	 */
	public InspectionPerformer(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * Constructor.
	 * @param group
	 * @param target
	 * @param name
	 */
	public InspectionPerformer(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	/**
	 * Constructor.
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	public InspectionPerformer(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	/**
	 * S�tter Console
	 * @param Console aConsole 
	 */
	public void setConsole(Console aConsole) {
		iConsole = aConsole;
	}

	/**
	 * H�mtar Console
	 * @return Console med Console
	 */
	public Console getConsole() {
		return iConsole;
	}

	/**
	 * S�tter MonitorFrequency
	 * @param int aMonitorFrequency 
	 */
	public void setMonitorFrequency(int aMonitorFrequency) {
		iMonitorFrequency = aMonitorFrequency;
	}

	/**
	 * H�mtar MonitorFrequency
	 * @return int med MonitorFrequency
	 */
	public int getMonitorFrequency() {
		return iMonitorFrequency;
	}

	/**
	 * S�tter RunEnabled
	 * @param boolean aRunEnabled 
	 */
	public void setRunEnabled(boolean aRunEnabled) {
		iRunEnabled = aRunEnabled;
	}

	/**
	 * H�mtar RunEnabled
	 * @return boolean med RunEnabled
	 */
	public boolean getRunEnabled() {
		return iRunEnabled;
	}
	/**
	 * @param aString
	 */
	public void setFileMatchRegExp(String aString) {
		iFileMatchRegExp = aString;
	}
	
	public void setLineFilterRegExp(String aLineFilterRegExp){
		iLineFilterRegExp = aLineFilterRegExp;
	}

}
