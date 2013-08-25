package flatsql.test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 *
 */
public class TestHelper {

	private static String DB_NAME;
	private static String DB_USER;
	private static String BASE_CONNECTION_STRING;
	private static String CONNECTION_STRING;
	private static boolean IS_TRAVIS = false;

	static {
		// support for travis CI
		if (System.getenv("TRAVIS") != null) {
			IS_TRAVIS = true;
			DB_NAME = "myapp_test";
			DB_USER = "travis";
		} else {
			DB_NAME = String.format("flatsql_test_%d",
					System.currentTimeMillis());
			DB_USER = "root";
		}
		BASE_CONNECTION_STRING = String.format(
				"jdbc:mysql://127.0.0.1/?user=%s", DB_USER);
		CONNECTION_STRING = String.format("jdbc:mysql://127.0.0.1/%s?user=%s",
				DB_NAME, DB_USER);
	}

	public static void createTestDatabase() throws Exception {
		if (IS_TRAVIS) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(BASE_CONNECTION_STRING);
		conn.createStatement().execute("CREATE DATABASE " + DB_NAME);
		conn.close();
	}

	public static void dropTestDatabase() throws Exception {
		if (IS_TRAVIS) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(BASE_CONNECTION_STRING);
		conn.createStatement().execute("DROP DATABASE " + DB_NAME);
		conn.close();
	}

	public static Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(CONNECTION_STRING);
	}

}
