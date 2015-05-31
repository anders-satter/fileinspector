package fileinspect.file;

import fileinspect.command.LineFilter;

/**
 * 
 * Brings back some information on the monitor run
 */
public class MonitorInfo {
	private boolean iChangesFound = false;
	private boolean iReportInfo = true;
	private boolean iFirstReportingWasDone = false;
	private LineFilter iLineFilter = null;


	/**
	 * @return
	 */
	public boolean isChangesFound() {
		return iChangesFound;
	}

	/**
	 * @param aB
	 */
	public void setChangesFound(boolean aB) {
		iChangesFound = aB;
	}

	public void reset() {
		iChangesFound = false;
		
	}

	/**
	 * @return
	 */
	public boolean isReportInfo() {
		return iReportInfo;
	}

	/**
	 * @param aB
	 */
	public void setReportInfo(boolean aB) {
		iReportInfo = aB;
	}

	/**
	 * @return
	 */
	public boolean isFirstReportingWasDone() {
		return iFirstReportingWasDone;
	}

	/**
	 * @param aB
	 */
	public void setFirstReportingWasDone(boolean aB) {
		iFirstReportingWasDone = aB;
	}

	/**
	 * 
	 * @return
	 */
	public boolean performReporting() {
		if (!iFirstReportingWasDone) {
			iFirstReportingWasDone = true;
			return true;
		} else {
			return iChangesFound;
		}
	}

	public LineFilter getLineFilter() {
		return iLineFilter;
	}

	public void setLineFilter(LineFilter aLineFilter) {
		iLineFilter = aLineFilter;
	}

}
