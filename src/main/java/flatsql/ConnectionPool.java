package flatsql;

import java.sql.Connection;

import exceptions.ConnectionPoolException;

/**
 * An interface that allows FlatSql to get a new connection when necessary
 * 
 */
public interface ConnectionPool {

	/**
	 * Get a new connection
	 * 
	 * @return
	 */
	Connection getConnection() throws ConnectionPoolException;

}
