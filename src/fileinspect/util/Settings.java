package fileinspect.util;

import java.io.Serializable;

public class Settings implements Serializable{
	static final long  serialVersionUID =1L;
	private transient SettingsChangeListener iChangeListener;
	private String iCurrentFileDirectory;
	private String iCurrentFileNameFilterRegExp;
	private String iCurrentLineFilterRegExp;
	
	
	public SettingsChangeListener getChangeListener() {
		return iChangeListener;
	}
	public void setChangeListener(SettingsChangeListener aChangeListener) {
		iChangeListener = aChangeListener;
	}
	public String getCurrentFileDirectory() {		
		return iCurrentFileDirectory;
	}
	public void setCurrentFileDirectory(String aCurrentLogDirectory) {
		iCurrentFileDirectory = aCurrentLogDirectory;
		iChangeListener.receivedChange();
	}
	public String getCurrentFileNameFilterRegExp() {
		return iCurrentFileNameFilterRegExp;
	}
	public void setCurrentFileNameFilterRegExp(String aCurrentFileNameFilterRegExp) {
		iCurrentFileNameFilterRegExp = aCurrentFileNameFilterRegExp;
		iChangeListener.receivedChange();
	}
	public String getCurrentLineFilterRegExp() {
		return iCurrentLineFilterRegExp;
	}
	public void setCurrentLineFilterRegExp(String aCurrentLineFilterRegExp) {
		iCurrentLineFilterRegExp = aCurrentLineFilterRegExp;
		iChangeListener.receivedChange();
	}
	
	
	
}
