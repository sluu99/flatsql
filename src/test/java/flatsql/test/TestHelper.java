package flatsql.test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 *
 */
public class TestHelper {

    private static final String TABLE_NAME = String.format("flatsql_test_%d", System.currentTimeMillis());
    private static final String BASE_CONNECTION_STRING = "jdbc:mysql://localhost/?user=root";
    private static final String CONNECTION_STRING = String.format("jdbc:mysql://localhost/%s?user=root", TABLE_NAME);

    public static void createTestDatabase() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(BASE_CONNECTION_STRING);
        conn.createStatement().execute("CREATE DATABASE " + TABLE_NAME);
        conn.close();
    }

    public static void dropTestDatabase() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(BASE_CONNECTION_STRING);
        conn.createStatement().execute("DROP DATABASE " + TABLE_NAME);
        conn.close();
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(CONNECTION_STRING);
    }

}
