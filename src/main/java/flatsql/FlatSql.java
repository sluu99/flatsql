package flatsql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import flatsql.annotations.DataEntity;

/**
 * Starting point for using FlatSql
 * 
 */
public class FlatSql {

	// -----------------------------------------------------------------------
	// Instance
	// -----------------------------------------------------------------------

	private ConnectionPool connPool;

	/**
	 * Create a new instance with a specific connection pool
	 * 
	 * @param connPool
	 */
	public FlatSql(ConnectionPool connPool) {
		this.connPool = connPool;
	}
	


	// -----------------------------------------------------------------------
	// Static
	// -----------------------------------------------------------------------

	// Cache class meta data
	private final static HashMap<Class<Entity>, String> tableNames = new HashMap<Class<Entity>, String>();
	private final static HashMap<Class<Entity>, Method[]> getters = new HashMap<Class<Entity>, Method[]>();
	private final static HashMap<Class<Entity>, Method[]> setters = new HashMap<Class<Entity>, Method[]>();

}
