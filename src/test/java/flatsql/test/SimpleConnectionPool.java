package flatsql.test;

import flatsql.ConnectionPool;
import flatsql.exceptions.ConnectionPoolException;

import java.sql.Connection;

/**
 *
 *
 */
public class SimpleConnectionPool implements ConnectionPool {

	@Override
	public Connection getConnection() throws ConnectionPoolException {
		try {
			return TestHelper.getConnection();
		} catch (Exception ex) {
			return null;
		}
	}

}
