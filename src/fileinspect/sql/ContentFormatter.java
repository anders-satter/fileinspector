package fileinspect.sql;

import java.util.ArrayList;
import java.util.Iterator;

import fileinspect.ui.resource.Console;

/**
 * Formats the content of a ContentStruct class
 */

public class ContentFormatter {
	private int[] iFieldMaxLen;
	private String[][] iShowMatrix;
	private static final char kPADD_SIGN = '.';
	private static final char kDIV_SIGN = '|';
	private int iRowLen = -1;
	private Console iConsole;
	private boolean[] iIsNumber;
	private String iQueryText;
	private long iResultRows;

	private ArrayList iRowList;

	public void setRowList(ArrayList aRowList) {
		iRowList = aRowList;
	}

	public void calcMaxFieldLength() {
		String[] tRowType = (String[]) iRowList.get(1);

		iFieldMaxLen = new int[tRowType.length];
		iRowLen = tRowType.length;
		for (Iterator iter = iRowList.iterator(); iter.hasNext();) {
			String[] tRow = (String[]) iter.next();
			for (int i = 0; i < tRow.length; i++) {
				try {
					if (iFieldMaxLen[i] < tRow[i].length()+1) {
						iFieldMaxLen[i] = tRow[i].length() + 1;
					}
				} catch (Exception e) {
					int x = 2;
					x++;
				}
			}
		}
	}

	public void checkIfTypeIsNumber() {
		String[] tRow = (String[]) iRowList.get(2);
		iIsNumber = new boolean[tRow.length];
		for (int i = 0; i < tRow.length; i++) {
			if ((tRow[i].indexOf("BigDecimal") > -1)||(tRow[i].indexOf("Integer")> -1)) {
				iIsNumber[i] = true;
			} else {
				iIsNumber[i] = false;
			}
		}
	}

	public void createPaddedFields() {
		iShowMatrix = new String[iRowList.size()][iRowLen];
		int tRowNumber = -1;
		for (Iterator iter = iRowList.iterator(); iter.hasNext();) {
			String[] tRow = (String[]) iter.next();
			tRowNumber++;
			/*get the type*/

			for (int i = 0; i < tRow.length; i++) {
				if (tRowNumber == 0 || tRowNumber == 3 || tRowNumber == iRowList.size()-1){
					iShowMatrix[tRowNumber][i] = paddedString(tRow[i], iFieldMaxLen[i], '-', iIsNumber[i]);
				} else{
					iShowMatrix[tRowNumber][i] = paddedString(tRow[i], iFieldMaxLen[i], kPADD_SIGN, iIsNumber[i]);
				}
				
			}
		}
	}

	public void print() {

		StringBuffer tPrintBuff = new StringBuffer();
		tPrintBuff.append('\n');
		tPrintBuff.append("Statement:");
		tPrintBuff.append('\n');
		tPrintBuff.append("------------------------------------------------------");
		tPrintBuff.append('\n');
		tPrintBuff.append(iQueryText);
		tPrintBuff.append('\n');
		for (int i = 0; i < iShowMatrix.length; i++) {
			for (int j = 0; j < iRowLen; j++) {
				tPrintBuff.append(iShowMatrix[i][j]);
				tPrintBuff.append(kDIV_SIGN);
			}
			tPrintBuff.append('\n');
		}
		tPrintBuff.append(String.valueOf(iResultRows)+ " row(s) affected");
		iConsole.println(tPrintBuff.toString());
	}

	public static String paddedString(String aStr, int aPaddedLength, char aPadChar, boolean aLeftPadding) {
		if (aStr == null) {
			aStr = "";
		}

		StringBuffer tBuff = new StringBuffer(aStr);
		int tStrLen = aStr.length();
		if (aPaddedLength > 0 && aPaddedLength > tStrLen) {
			for (int i = 0; i < aPaddedLength; i++) {
				if (aLeftPadding) {
					if (i < aPaddedLength - tStrLen) {
						tBuff.insert(0, aPadChar);
					}
				} else {
					//rightpadding
					if (i > tStrLen) {
						tBuff.append(aPadChar);
					}
				}
			}
		}
		return tBuff.toString();
	}

	/**
	 * S�tter Console
	 * @param Console aConsole 
	 */
	public void setConsole(Console aConsole) {
		iConsole = aConsole;
	}

	/**
	 * S�tter QueryText
	 * @param String aQueryText 
	 */
	public void setQueryText(String aQueryText) {
		iQueryText = aQueryText;
	}

	/**
	 * S�tter ResultRows
	 * @param long aResultRows 
	 */
	public void setResultRows(long aResultRows) {
		iResultRows = aResultRows;
	}

}
