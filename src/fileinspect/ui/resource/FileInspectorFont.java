/*
 * Created on 2005-jun-29
 */
package fileinspect.ui.resource;

import java.awt.Font;


public class FileInspectorFont extends Font {
	private static FileInspectorFont iInstance = new FileInspectorFont();

	public FileInspectorFont() {
		super("Courier New", Font.PLAIN, 11);
		//super("MS Sans Serif", Font.BOLD,11 );
	}

	public static FileInspectorFont getInstance() {
		return iInstance;
	}

	public static Font getConsoleFont() {
		return new Font("Courier New", Font.PLAIN, 11);
		//return new Font("Arial", Font.PLAIN, 11);
		//return new Font("Eurostile", Font.PLAIN, 10);
		//return new Font("Verdan", Font.PLAIN, 10);
		//return new Font("Times", Font.PLAIN, 10);
	}

	//	  public class ConsoleFont extends Font {

}
