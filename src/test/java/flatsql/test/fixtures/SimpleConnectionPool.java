package flatsql.test.fixtures;

import flatsql.ConnectionPool;
import flatsql.test.TestHelper;

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
