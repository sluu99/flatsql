package flatsql;

import java.sql.Connection;

/**
 * An interface that allows FlatSql to get a new connection when necessary
 *
 */
public interface ConnectionPool {

	/**
	 * Get a new connection
	 * @return
	 */
	Connection getConnection();
	
}
