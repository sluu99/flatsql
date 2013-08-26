package flatsql;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import flatsql.exceptions.ConnectionPoolException;
import flatsql.util.JdbcUtil;

final class Storage {

	private ConnectionPool connPool = null;
	private TypeRegistry registry = null;

	public Storage(ConnectionPool connPool, TypeRegistry registry) {
		this.connPool = connPool;
		this.registry = registry;
	}

	/**
	 * Insert an entity into the database
	 * 
	 * @param entity
	 *            The entity
	 * @throws ConnectionPoolException
	 * @throws SQLException
	 */
	public void insert(Entity entity) throws ConnectionPoolException,
			SQLException {

		Connection conn = null;
		Class<? extends Entity> entityClass = entity.getClass();

		String tableName = registry.getEntityTableName(entityClass);

		if (tableName == null) {
			throw new NullPointerException("Table name is null. Perhaps you forgot to register the type?");
		}

		try {
			conn = connPool.getConnection();
			conn.setAutoCommit(false);

			insertEntity(entity, conn);
			persistAttributes(entity, conn);

			conn.commit();
		} catch (ConnectionPoolException | SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JdbcUtil.close(null, null, conn);
		}
	}

	/**
	 * Persist an entity's attribute based on the provided getters
	 * @param tableName
	 * @param conn
	 * @param entity
	 * @param getters
	 * @throws SQLException
	 */
	private void persistAttributes(String tableName, Connection conn, Entity entity, HashMap<String, Method> getters) throws SQLException {
		if (getters == null || getters.size() == 0) {
			return;
		}

		String attrSql = String.format(INSERT_ATTR_SQL, tableName);

		PreparedStatement deleteStmt = null;
		ArrayList<PreparedStatement> stmts = new ArrayList<>(); 

		try {
			// delete the old attributes
			deleteStmt = conn.prepareStatement(String.format(DELETE_ATTR_SQL, tableName));
			deleteStmt.setString(1, entity.id());
			deleteStmt.execute();

			// insert each of the new attributes
			Iterator<Entry<String, Method>> it = getters.entrySet().iterator();
			
			while (it.hasNext()) {
				Entry<String, Method> pair = it.next();				
				Method getter = pair.getValue();
				boolean isArray = getter.getReturnType().isArray(); 
				String values[] = getValues(entity, getter);
				
				// only arrays should have more than 1 value
				for (int i = 0; i < values.length; i++) {
					PreparedStatement stmt = conn.prepareStatement(attrSql);
					stmt.setString(1, entity.id());					
					stmt.setString(3, values[i]);
					stmt.setString(4, null);

					if (isArray) {
						stmt.setString(2, String.format("%s[%d]", pair.getKey(), i));
					} else {
						stmt.setString(2, pair.getKey());
					}
					
					stmt.execute();
					
					stmts.add(stmt);
				}
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtil.close(null, deleteStmt);
			
			for (PreparedStatement stmt : stmts) {
				JdbcUtil.close(null, stmt);
			}
		}
	}
	
	
	/**
	 * Persist an object's attribute into the database
	 * @param entity The entity
	 * @param conn Connection
	 * @throws SQLException
	 */
	private void persistAttributes(Entity entity, Connection conn) throws SQLException {

		Class<? extends Entity> entityClass = entity.getClass();		
		persistAttributes(
			registry.getAttrTableName(entityClass), 
			conn, 
			entity,
			registry.getAttributes(entityClass));
	}

	/**
	 * Insert a new entity into the database
	 * @param tableName
	 * @param entity
	 * @param conn
	 * @throws SQLException
	 */
	private void insertEntity(Entity entity, Connection conn) throws SQLException {
		
		String tableName = registry.getEntityTableName(entity.getClass());
		
		PreparedStatement stmt = null;
		long now = System.currentTimeMillis();

		entity.creationEpoch(now);
		entity.updateEpoch(now);

		try {
			stmt = conn.prepareStatement(String.format(INSERT_SQL, tableName));
			stmt.setString(1, entity.id());
			stmt.setString(2, entity.name());
			stmt.setLong(3, entity.creationEpoch());
			stmt.setLong(4, entity.updateEpoch());

			stmt.execute();
		} finally {
			JdbcUtil.close(null, stmt);
		}
	}

	// ======================================================================================
	// Static
	// ======================================================================================

	private static final String INSERT_SQL = "INSERT INTO %s (id, name, creation_epoch, update_epoch) "
			+ "VALUES (?, ?, ?, ?) ";
	private static final String DELETE_ATTR_SQL = "DELETE FROM %s WHERE entity_id=?";
	private static final String INSERT_ATTR_SQL = "INSERT INTO %s (entity_id, attr_key, attr_value, attr_meta) "
			+ "VALUES (?, ?, ?, ?)";

	/**
	 * Convert a value to String based on its type
	 */
	private static String convertToString(Object val, Class<?> fromType) {
		String value = null;
		if (fromType == String.class || fromType.isEnum()) {
			if (val != null) {
				value = String.valueOf(val);
			}
		} else if (fromType == int.class) {
			value = String.valueOf((int) val);
		} else if (fromType == long.class) {
			value = String.valueOf((long) val);
		} else if (fromType == float.class) {
			value = String.valueOf((float) val);
		} else if (fromType == double.class) {
			value = String.valueOf((double) val);
		} else if (fromType == boolean.class) {
			value = String.valueOf((boolean) val);
		}

		return value;
	}

	/**
	 * Get an entity's value as string from a provided method
	 * 
	 * @param entity
	 * @param m
	 * @return An array of values.
	 * 		If the method (m) does not return an array then the return value has length of 1.
	 * 		If the method returns an array but it's either empty or null, then the return value will be an empty array. 
	 */
	private static String[] getValues(Entity entity, Method m) {

		if (entity == null || m == null) {
			return null;
		}
		
		boolean isArray = false;
		Class<?> returnType = m.getReturnType();
		if (returnType.isArray()) {
			isArray = true;
			returnType = returnType.getComponentType();
		}
		
		Object objVal;

		try {
			objVal = m.invoke(entity);
			
			if (!isArray) {			
				return new String[] { convertToString(objVal, returnType) };
			} else { // returning an array
				if (objVal != null) {
					String[] values = new String[Array.getLength(objVal)];
					
					for (int i = 0; i < values.length; i++) {
						values[i] = convertToString(Array.get(objVal, i), returnType);
					}
					
					return values;
				} else { // the array-getter returns null
					return new String[0];
				}				
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			return null;
		}

	}
}
