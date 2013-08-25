package flatsql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import exceptions.ConnectionPoolException;
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
	 * @param entity The entity
	 * @throws ConnectionPoolException 
	 * @throws SQLException 
	 */
	public void insert(Entity entity) throws ConnectionPoolException, SQLException {
						
		Connection conn = null;
		Class<? extends Entity> entityClass = entity.getClass();
		
		String tableName = registry.getEntityTableName(entityClass);
		
		if (tableName == null) {
			throw new NullPointerException("Table name is null. Perhaps you forgot to register the type?");
		}
		
		try {
			conn = connPool.getConnection();
			conn.setAutoCommit(false);
			
			insertEntity(tableName, entity, conn);
			persistAttributes(
				registry.getAttrTableName(entityClass), 
				entity, 
				conn, 
				false);
			persistAttributes(
					registry.getLargeAttrTableName(entityClass), 
					entity, 
					conn, 
					true);
			
			conn.commit();
		} catch(ConnectionPoolException | SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JdbcUtil.close(null, null, conn);
		}
	}
	
	
	private void persistAttributes(String tableName, Entity entity, Connection conn, boolean largeAttr) throws SQLException {
		if (largeAttr)
			return;
		
		HashMap<String, Method> getters = registry.getGetters(entity.getClass());
		
		if (getters == null || getters.size() == 0)
			return;
		
		
		String attrSql = String.format(INSERT_ATTR_SQL, tableName);
		
		PreparedStatement deleteStmt = null;
		PreparedStatement[] stmts = new PreparedStatement[getters.size()]; 
		
		try {
			deleteStmt = conn.prepareStatement(String.format(DELETE_ATTR_SQL, tableName));
			deleteStmt.setString(1, entity.id());
			deleteStmt.execute();
			
			Iterator<Entry<String, Method>> it = getters.entrySet().iterator();			
			int i = 0;
			while (it.hasNext()) {
				Entry<String, Method> pair = it.next();
			
				stmts[i] = conn.prepareStatement(attrSql);
				stmts[i].setString(1, entity.id());
				stmts[i].setString(2, pair.getKey());
				stmts[i].setString(3, getValue(entity, pair.getValue()));
				stmts[i].setString(4, null);
				
				stmts[i].execute();
				i++;
			}
			
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtil.close(null, deleteStmt);
			for (int i = 0; i < stmts.length; i++) {
				JdbcUtil.close(null, stmts[i]);
			}
		}
	}
	
	
	private void insertEntity(String tableName, Entity entity, Connection conn) throws SQLException {
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
	
	//======================================================================================
	// Static
	//======================================================================================
	
	private static final String INSERT_SQL = 
		"INSERT INTO %s (id, name, creation_epoch, update_epoch) " +
		"VALUES (?, ?, ?, ?) ";
	private static final String DELETE_ATTR_SQL = "DELETE FROM %s WHERE entity_id=?";
	private static final String INSERT_ATTR_SQL = 
		"INSERT INTO %s (entity_id, attr_key, attr_value, attr_meta) " +
		"VALUES (?, ?, ?, ?)";
	
	/**
	 * Convert to a String
	 */
	private static String convertToString(Object val, Class<?> fromType) {
		String value = null;
		if (fromType == String.class || fromType.isEnum()) {
			if (val != null) {
				value = String.valueOf(val);
			}
		} else if (fromType == int.class) {
			value = String.valueOf((int)val);
		} else if (fromType == long.class) {
			value = String.valueOf((long)val);
		} else if (fromType == float.class) {
			value = String.valueOf((float)val);
		} else if (fromType == double.class) {
			value = String.valueOf((double)val);
		} else if (fromType == boolean.class) {
			value = String.valueOf((boolean)val);
		}
		
		return value;
	}
	
	/**
	 * Get an entity's value as string from a provided method
	 * @param entity
	 * @param m
	 * @return
	 */
	private static String getValue(Entity entity, Method m) {
		
		if (entity == null || m == null) {
			return null;
		}
		
		Class<?> returnType = m.getReturnType();		
		Object objVal;
		
		try {
			objVal = m.invoke(entity);
			return convertToString(objVal, returnType);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
		
	}
}
