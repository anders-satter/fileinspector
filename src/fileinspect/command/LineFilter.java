package fileinspect.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Used to match expression in a line from a textfile
 */
public class LineFilter {
	private String iLineFilterMatchRegExp = "";
	private Pattern iCompiledPattern = null;
	
	public LineFilter(String aFileMatchRegExp){
		iLineFilterMatchRegExp = aFileMatchRegExp;
		if (iLineFilterMatchRegExp != null && iLineFilterMatchRegExp.length() > 0) {
			iCompiledPattern = Pattern.compile(iLineFilterMatchRegExp);
		}
	}	
	public boolean receivedMatch(String aString){		
		if (iCompiledPattern != null && aString.length() >0){
			Matcher tMatcher = iCompiledPattern.matcher(aString);
			return tMatcher.matches();
		} else {
			return true;
		}
		
		 
	}
}
