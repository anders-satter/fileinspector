package fileinspect.util;

public interface SettingsListener {
	public void setLineFilterRegExpString(String aLineFilterRegExpString);
	public void setFileFilterRegExpString(String aLineFilterRegExpString);
	public void setFileDirectory(String aFileDirectory);	
}
