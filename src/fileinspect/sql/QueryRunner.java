package fileinspect.sql;

public class QueryRunner extends Thread {
	private Sql iSql;

	public void run() {
		iSql.exec();
		iSql.printResultSet();
		iSql.dropConnection();
	}

	/**
	 * S�tter Sql
	 * @param Sql aSql 
	 */
	public void setSql(Sql aSql) {
		iSql = aSql;
	}
}
