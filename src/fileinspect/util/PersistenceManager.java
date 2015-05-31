package fileinspect.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class PersistenceManager implements SettingsChangeListener{
	private static final PersistenceManager kPERSISTENCE_MANAGER = new PersistenceManager();
	private static final String kSettingsFileName = "fileinspector.settings";
	private Settings iSettings;
	private Collection<SettingsListener> iSettingsListenerList;
	
	private PersistenceManager(){
		iSettingsListenerList  = new ArrayList<SettingsListener>();
	} 
	
	public static PersistenceManager getInstance(){
		return kPERSISTENCE_MANAGER;
	}
	
	public Settings getSettings(){
		return iSettings;
	}
	
	public void persist(){
		// Write to disk with FileOutputStream
		try {
			FileOutputStream f_out = new 
				FileOutputStream(kSettingsFileName);
			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new
				ObjectOutputStream (f_out);
			// Write object out to disk
			obj_out.writeObject ( iSettings );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void addSettingsListener(SettingsListener aListener){
		iSettingsListenerList.add(aListener);
	}
	
	private void propagateSettings(){
		for (SettingsListener tListener : iSettingsListenerList) {
			tListener.setFileDirectory(iSettings.getCurrentFileDirectory());
			tListener.setFileFilterRegExpString(iSettings.getCurrentFileNameFilterRegExp());
			tListener.setLineFilterRegExpString(iSettings.getCurrentLineFilterRegExp());
		}
	}
	
	public void unPersist(){
		try {
			// Read from disk using FileInputStream
			FileInputStream f_in = new 
				FileInputStream(kSettingsFileName);

			// Read object using ObjectInputStream
			ObjectInputStream obj_in = 
				new ObjectInputStream(f_in);

			// Read an object
			Object obj = obj_in.readObject();
			if (obj instanceof Settings){
				iSettings = (Settings)obj;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
		if (iSettings == null){
			iSettings = new Settings();
		}
		
		iSettings.setChangeListener(this);
		propagateSettings();
	}
	
	@Override
	public void receivedChange() {
		propagateSettings();		
	}
	
}
