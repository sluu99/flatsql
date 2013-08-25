package flatsql.test;

import exceptions.ConnectionPoolException;
import flatsql.ConnectionPool;

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
