package flatsql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 */
public class JdbcUtil {

	/**
	 * Close a result set
	 * 
	 * @param resultSet
	 */
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException ex) {

			}
		}
	}

	public static void close(ResultSet resultSet, Statement stmt) {
		close(resultSet);
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
			}
		}
	}

	public static void close(ResultSet resultSet, Statement stmt,
			Connection conn) {
		close(resultSet, stmt);
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ex) {

			}
		}
	}

}
