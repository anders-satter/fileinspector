/*
 * Created on 2005-apr-20
 */
package fileinspect.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * Helper class for file communication.
 * 
 */
public class FileManager {

  /**
   * 
   */
  public FileManager() {
    super();
  }
  
  public static void writeStringToFile(String aStr, String aFilename, boolean aAppendMode){
    try {
      FileWriter fl = new FileWriter(aFilename, aAppendMode);
      PrintWriter out = new PrintWriter(new BufferedWriter(fl));

      if (fl != null) {
        out.println(aStr);
      }

      out.close();
    } catch (IOException e) {
    }

  }

  public static String readStringFromFile(String aFilename){
    
    StringBuffer tBuff ;
    String tLine;
    File tFile = new File(aFilename);

    if (tFile.exists()) {
      tBuff = new StringBuffer();
      try {
        BufferedReader bf = new BufferedReader(new FileReader(aFilename));

        while ((tLine = bf.readLine()) != null) {
          tBuff.append(tLine+'\n');
        }

        bf.close();
        
        return tBuff.toString();    
      } catch (FileNotFoundException e) { 
        return "File " + aFilename + " not found.";
      } catch (IOException e){
        return "IOExcpetion occured while reading " + aFilename;
      }
    } else{
      return "File " + aFilename + " does not exist.";
    }
  }
  
  
  
  public static void main(String[] args) {
    //FileManager tFm = new FileManager();
    String tStr = "I will write this to the file.\nThis is line two";
    FileManager.writeStringToFile(tStr,"file.txt", false);
    tStr = "This is the appended text";
    FileManager.writeStringToFile(tStr,"file.txt", true);
    
    String tReadString = FileManager.readStringFromFile("file.txt");
    System.out.println(tReadString);
  }
}
