/*
 * Created on 2005-maj-10
 */
package fileinspect.command;

import java.util.ArrayList;
import java.util.Collections;

import fileinspect.sql.Sql;
import fileinspect.ui.FileInspector;

public class CommandRunner extends Thread{
	private String[] iKeyWordList;
	private String[] iCommand;
	private FileInspector iOwner;
	private Sql iSql;

	public CommandRunner(FileInspector aOwner) {
		super();
		iOwner = aOwner;
		iKeyWordList = new String[10];
		iKeyWordList[0] = "$help";
		iKeyWordList[1] = "$url";
		iKeyWordList[2] = "$driver";
		iKeyWordList[3] = "$user";
		iKeyWordList[4] = "$pw";
		iKeyWordList[5] = "$showtables";
		iKeyWordList[6] = "$init";
	}

	public void setCommand(String[] aCommand) {
		iCommand = aCommand;
	}

	//  public void setSql(Sql aSql){
	//    iSql = aSql;
	//  }

	public void run() {
		for (int i = 0; i < iCommand.length; i++) {
		  
			//iOwner.normalizeEditor();
			String tSelectedText; 
			tSelectedText = iCommand[i]; //tSelectedText.trim();
			if (tSelectedText.trim().equalsIgnoreCase("")){
				return;
			}
			if (tSelectedText.equalsIgnoreCase("$init")) {
				//iOwner.initDataBase();
			} else if (tSelectedText.equalsIgnoreCase("$help")) {
				iOwner.println("First run:");
				iOwner.println("$url [jdbc database url]");
				iOwner.println("$driver [jdbc driver class name]");
				iOwner.println("$user [user]");
				iOwner.println("$pw [password]");
				iOwner.println("");
				iOwner.println("Now you can run sql queries, DDL and DML commands on the database.");
				iOwner.println("To run a command type it into the input editor (lower window), select it and press Ctrl-b");
				iOwner.println("To kill a command just press Ctrl-Del");

				iOwner.println("");
				iOwner.println("$showtables [Schema pattern] - shows all tables that match the schema.");
				iOwner.println("If no schema is supplied all tables will be shown (if the user has the right priviligies...)");

			} else if (tSelectedText.trim().toLowerCase().indexOf("$showtables") > -1) {
				String[] tArgs = tSelectedText.split(" ");
				if (tArgs.length < 2) {
					iOwner.getSql().showTableNames(null);
				} else {
					iOwner.getSql().showTableNames(tArgs[1]);
				}

				ArrayList iTableNameList = iOwner.getSql().getTableNameList();
				Collections.sort(iTableNameList);
				//iOwner.setTableNameList(iTableNameList);
				//find the tables
				//HashSet tTableNames = iOwner.getSql().getTableNames();
//				((SyntaxDocument) iOwner.getInputEditor().getDocument()).setTableSet(tTableNames);
//				iOwner.loadDefaultInputEditorText(true);

			} else if (tSelectedText.trim().toLowerCase().indexOf("$user") > -1) {
				String[] tArgs = tSelectedText.split(" ");
				iOwner.getSql().setUser(tArgs[1]);
				iOwner.println("user: " + tArgs[1]);
			} else if (tSelectedText.trim().toLowerCase().indexOf("$pw") > -1) {
				String[] tArgs = tSelectedText.split(" ");
				iOwner.getSql().setPassword(tArgs[1]);
				iOwner.println("password was registered.");
			} else if (tSelectedText.trim().toLowerCase().indexOf("$url") > -1) {
				String[] tArgs = tSelectedText.split(" ");
				iOwner.getSql().setDBURL(tArgs[1]);
				iOwner.println("database url: " + tArgs[1]);
			} else if (tSelectedText.trim().toLowerCase().indexOf("$tst") > -1) {
				/* This is a test command */

				//						Style = iOwner.getInputEditor().
//				Style tStyle = iOwner.getInputEditor().getLogicalStyle();
				//TabSet tTabSet = tStyle.get
				//new JTextPane().getLogicalStyle()

			} else if (tSelectedText.trim().toLowerCase().indexOf("$driver") > -1) {
				String[] tArgs = tSelectedText.split(" ");
				iOwner.getSql().setDriver(tArgs[1]);
				iOwner.println("driver: " + tArgs[1]);
			} else {
				iOwner.getSql().setQueryString(tSelectedText.trim());
				iOwner.getSql().connect(false);
				iOwner.getSql().createStatement();
				iOwner.getSql().exec();
				iOwner.getSql().printResultSet();
//				int tStart = iOwner.getInputEditor().getSelectionStart();
//				int tEnd = iOwner.getInputEditor().getSelectionEnd();
//				iOwner.getInputEditor().setCaretPosition(tStart + 1);
//				iOwner.getInputEditor().setCaretPosition(tStart);
//				iOwner.getInputEditor().setSelectionStart(tStart);
//				iOwner.getInputEditor().setSelectionEnd(tEnd);
			}			
		}
		System.out.println("dropConnection from CommandRunner");
		iOwner.getSql().dropConnection();
	}

	public static void main(String[] args) {
	}
}
