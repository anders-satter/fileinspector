package fileinspect.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import fileinspect.ui.resource.Console;


public class Sql {
  private String iDBURL = "";
  private String iDriver = "";
  private String iUser = "";
  private String iPassword = "";
  private Connection iConn = null;
  private Console iConsole;
  private String iQueryStr = "";
  private ResultSet iResultSet = null;
  private Statement iStatement = null;
  private boolean iReturnsResult = false;

  //"jdbc:oracle:thin:@ORATDB1.unix:1525:ORAST2"

  public void connect(boolean aVerbose) {
    try {
      Class.forName(iDriver);
      if (aVerbose) {
        iConsole.println("driver found...");
      }
      String tPassword = new String(iPassword.trim());
      if (tPassword == null || tPassword.equalsIgnoreCase("\"\"")) {
        iConn = DriverManager.getConnection(iDBURL, iUser, "");
      } else {
        iConn = DriverManager.getConnection(iDBURL, iUser, iPassword);
      }
      if (aVerbose) {
        iConsole.println("connection created");
      }
    } catch (SQLException tExc) {
      iConsole.println("SQLException:");
      iConsole.println("----------------");
      iConsole.println(tExc.getMessage());
    } catch (ClassNotFoundException tExc2) {
      iConsole.println("Exception:");
      iConsole.println("----------------");
      iConsole.println(tExc2.getMessage());
    }
  }

  public void dropConnection() {
    try {
      if (iConn != null) {
        if (!iConn.isClosed()){
         iConn.close();
         iConsole.println("connection closed");
        }
      }
    } catch (Exception tExc) {
      iConsole.println("connection could not be closed.");
      iConsole.println("Exception:");
      iConsole.println("----------------");
      iConsole.println(tExc.getMessage());
    }
  }

  public void showConnectionInfo() throws SQLException, Exception {
    iConsole.println("");
    iConsole.println("connection info");
    iConsole.println("----------");
    DatabaseMetaData tAskInfo = iConn.getMetaData();
    iConsole.println("driver: " + tAskInfo.getDriverName() + " "
        + tAskInfo.getDriverVersion());
    iConsole.println("calling url: " + tAskInfo.getURL());

  }

  public void setQueryString(String aQueryStr) {
    iQueryStr = aQueryStr;
  }

  public void createStatement() {
    try {
      if (iConn != null) {
        iStatement = iConn.createStatement();
      } else {
        iConsole.println("-----------Error-----------");
        iConsole.println("connection is not open");
      }

    } catch (SQLException e) {
      iConsole.println("-----------Error-----------");
      iConsole.println(e.getMessage());
    }
  }

  public void exec() {
    try {
      iConsole.println("executing...");
      iConn.setAutoCommit(true);
      /*
       * Is it a select statement or anything else (insert, update, delete or
       * DDL)
       */
      String tTestStr = new String(iQueryStr.trim().toLowerCase());
      if (tTestStr.indexOf("insert ") > -1 || tTestStr.indexOf("update ") > -1
          || tTestStr.indexOf("delete ") > -1 || tTestStr.indexOf("use ") > -1
          || tTestStr.indexOf("create ") > -1) {
        iReturnsResult = false;
        iStatement.executeUpdate(iQueryStr);

      } else {
        /* ok, a statement that returns a resultset */
        iReturnsResult = true;
        iResultSet = iStatement.executeQuery(iQueryStr);

      }
      iConsole.println("executing finished");
    } catch (SQLException e) {
      iConsole.println("-----------Error-----------");
      iConsole.println(e.getMessage());
    }
  }

  public void printResultSet() {
    if (!iReturnsResult) {
      return;
    }
    if (iResultSet != null) {
      try {
        ResultSetMetaData tResInfo = iResultSet.getMetaData();

        int cols = tResInfo.getColumnCount();

        /* Table Top line */
        String[] tRow = new String[cols];
        ArrayList tRowList = new ArrayList();
        for (int i = 1; i < cols + 1; i++) {
          tRow[i - 1] = "";
        }
        tRowList.add(tRow);

        /* Columnheaders */
        tRow = new String[cols];
        for (int i = 1; i < cols + 1; i++) {
          tRow[i - 1] = tResInfo.getColumnName(i);
        }
        tRowList.add(tRow);

        /* Column classes */
        tRow = new String[cols];
        for (int i = 1; i < cols + 1; i++) {
          tRow[i - 1] = "(" + tResInfo.getColumnClassName(i) + ")";
        }
        tRowList.add(tRow);

        /* Column header underlining */
        tRow = new String[cols];
        for (int i = 1; i < cols + 1; i++) {
          tRow[i - 1] = "";
        }
        tRowList.add(tRow);

        long tResultRowCounter = 0;
        while (iResultSet.next()) {
          //String s = "";
          tRow = new String[cols];
          for (int i = 1; i < cols + 1; i++) {
            Object tObject = iResultSet.getString(i);
            if (tObject == null) {
              tRow[i - 1] = "null";
            } else if (tObject instanceof String) {
              tRow[i - 1] = iResultSet.getString(i);
            } else {
              tRow[i - 1] = "Unidentified type";
            }
            tRow[i - 1] = iResultSet.getString(i);
          }
          tRowList.add(tRow);
          tResultRowCounter++;
        }

        /* Table end underlining */
        tRow = new String[cols];
        for (int i = 1; i < cols + 1; i++) {
          tRow[i - 1] = "";
        }
        tRowList.add(tRow);

        iConsole.println("formatting result...");
        ContentFormatter iContentFormatter = new ContentFormatter();

        iContentFormatter.setRowList(tRowList);
        iContentFormatter.setConsole(iConsole);
        iContentFormatter.calcMaxFieldLength();
        iContentFormatter.checkIfTypeIsNumber();
        iContentFormatter.createPaddedFields();
        iContentFormatter.setQueryText(iQueryStr);
        iContentFormatter.setResultRows(tResultRowCounter);
        iContentFormatter.print();

      } catch (SQLException e) {
        iConsole.println("-----------Error-----------");
        iConsole.println(e.getMessage());
      }
    } else {
      iConsole.println("no result found in database.");
    }
  }

  public boolean executeStatement(String aStatement) {
    boolean tRet = true;
    try {
      connect(false);
      Statement tStm = iConn.createStatement();
      ResultSet tRs = tStm.executeQuery(aStatement);

      ResultSetMetaData tResInfo = tRs.getMetaData();

      int cols = tResInfo.getColumnCount();
      while (tRs.next()) {
        String s = "";
        for (int i = 1; i < cols + 1; i++) {
          s += tRs.getString(i) + "|";
        }
        iConsole.println(s);
      }
    } catch (SQLException e) {
      iConsole.println("-----------Error-----------");
      iConsole.println(e.getMessage());
      tRet = false;
    } finally {
      dropConnection();
    }
    return tRet;
  }
  HashSet iTableNames = null;
  ArrayList iTableNameList = null;

  public HashSet getTableNames() {
    return iTableNames;
  }

  public ArrayList getTableNameList() {
    return iTableNameList;
  }

  //public ArrayList getColumnNames(String aTableName, boolean isCaseSensitive) {
  public ArrayList getColumnNames(String aTableName) {
    ArrayList tColNameList = null;
    try {
      connect(false);
      DatabaseMetaData tInfo = iConn.getMetaData();
      ResultSet tColumns = tInfo.getColumns(null, null, aTableName, null);
      tColNameList = fillColNames(tColumns);
      if (tColNameList.size() < 1) {
        tColumns = tInfo.getColumns(null, null, aTableName.toLowerCase(), null);
      }
      tColNameList = fillColNames(tColumns);
      if (tColNameList.size() < 1) {
        tColumns = tInfo.getColumns(null, null, aTableName.toUpperCase(), null);
      }
      tColNameList = fillColNames(tColumns);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return tColNameList;
  }

  private ArrayList fillColNames(ResultSet aRs) {
    ArrayList tReturn = new ArrayList();
    try {
      //ResultSetMetaData tColInfo = aRs.getMetaData();
      //int cols = tColInfo.getColumnCount();
      while (aRs.next()) {
        tReturn.add(aRs.getString(4));
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return tReturn;
  }

  public void showTableNames(String tSchemaPattern) {
    iConsole.println("");
    iConsole.println("Tables");
    iConsole.println("----------");

    try {
      connect(false);
      DatabaseMetaData askInfo = iConn.getMetaData();
      iConsole.println(askInfo.getDatabaseProductVersion());
      ResultSet tables = askInfo.getTables(null, tSchemaPattern, null, null);
      //askInfo.
      ResultSetMetaData tablesInfo = tables.getMetaData();
      int cols = tablesInfo.getColumnCount();

      if (cols > 0) {
        iTableNames = new HashSet();
        iTableNameList = new ArrayList();

        while (tables.next()) {
          String s = "";
          for (int i = 1; i < cols + 1; i++) {
            s += tables.getString(i) + "|";
            if (i == 3) {
              iTableNameList.add(tables.getString(i));
              //Add uppercase names
              iTableNames.add(tables.getString(i).toUpperCase());
              //Add lowercase names
              iTableNames.add(tables.getString(i).toLowerCase());
            }
          }
          iConsole.println(s);
        }
      }

    } catch (Exception e) {
      iConsole.println("-----------Error-----------");
      iConsole.println(e.getMessage());
    } finally {
      dropConnection();
    }
  }

  /**
   * S�tter DBURL
   * @param String aDBURL
   */
  public void setDBURL(String aDBURL) {
    iDBURL = aDBURL;
  }

  /**
   * S�tter Driver
   * @param String aDriver
   */
  public void setDriver(String aDriver) {
    iDriver = aDriver;
  }

  /**
   * H�mtar DBURL
   * @return String med DBURL
   */
  public String getDBURL() {
    return iDBURL;
  }

  /**
   * H�mtar Driver
   * @return String med Driver
   */
  public String getDriver() {
    return iDriver;
  }

  /**
   * S�tter User
   * @param String aUser
   */
  public void setUser(String aUser) {
    iUser = aUser;
  }

  /**
   * S�tter Password
   * @param String aPassword
   */
  public void setPassword(String aPassword) {
    iPassword = new String(aPassword.trim());
  }

  /**
   * H�mtar User
   * @return String med User
   */
  public String getUser() {
    return iUser;
  }

  /**
   * H�mtar Password
   * @return String med Password
   */
  public String getPassword() {
    return iPassword;
  }

  /**
   * S�tter Console
   * @param Console aConsole
   */
  public void setConsole(Console aConsole) {
    iConsole = aConsole;
  }

  /**
   * H�mtar Console
   * @return Console med Console
   */
  public Console getConsole() {
    return iConsole;
  }
}