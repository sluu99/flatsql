package flatsql.test;

import flatsql.ConnectionPool;

import java.sql.Connection;

/**
 *
 *
 */
public class SimpleConnectionPool implements ConnectionPool {

    @Override
    public Connection getConnection() {
        try {
            return TestHelper.getConnection();
        } catch (Exception ex) {
            return null;
        }
    }
}
