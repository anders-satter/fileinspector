/*
 * Created on 2005-maj-10
 */

package fileinspect.command;

public class CommandParser {
  private String iCommandString;
  private String[] iCommand;

  public CommandParser() {
    super();
  }

//  public CommandParser(String aCommandString) {
//    super();
//  }
  public void setCommandString(String aCommandString) {
    iCommandString = aCommandString;
  }
  public void parse(){
    iCommand = iCommandString.split(";");
  }

  public String[] getCommand(){
    return iCommand;
  }
  
  public static void main(String[] args) {
    CommandParser p = new CommandParser();
    p.setCommandString("jks;slsdkj;\n;alkjh");
    p.parse();
    String [] tCommand = p.getCommand();
    for (int i = 0; i < tCommand.length; i++) {
      System.out.println(tCommand[i]);
    }
  }
}