package fileinspect.util;

public class Utils {

public static String kSPECIALCHARS = "\n\t"; 
 
	
	public static boolean isBufferFilled(StringBuilder aBuffer){
		return aBuffer != null && aBuffer.length() >0;
	}
	
	public static void wipeBuffer(StringBuilder aBuffer){
		if (aBuffer != null){
			aBuffer.delete(0, aBuffer.length());			
		}
	}
	
	/**
	 * Wipes the buffer from previous message and sets the
	 * new message.
	 * @param aBuffer
	 * @param aMessage
	 */
	public static void setBuffer(StringBuilder aBuffer, String aMessage){
		if (aBuffer != null){
			wipeBuffer(aBuffer);
			aBuffer.append(aMessage);
		}
	}
	
	public static boolean isNotEmpty(String aString){
		return aString != null && aString.trim().length() >0;
	}
	
	public static boolean containsSignificantInfo(String aString){
		if (isNotEmpty(aString)){
			for (int i = 0; i < aString.length(); i++) {			
				if (!kSPECIALCHARS.contains(String.valueOf(aString.charAt(i)))){
					return true;		
				}		
			} 
		}
		return false;
	}
	
	public static void main(String[] args) {
		String tText = "";
		System.out.println(containsSignificantInfo(tText));			
		
		tText = "swamp";
		System.out.println(containsSignificantInfo(tText));
		
		tText = null;	
		System.out.println(containsSignificantInfo(tText));
		
		tText = "\n\t";	
		System.out.println(containsSignificantInfo(tText));

		tText = "";	
		System.out.println(containsSignificantInfo(tText));

	}
	
}
