package flatsql;

import flatsql.annotations.DataEntity;
import flatsql.annotations.Ignore;
import flatsql.exceptions.ConnectionPoolException;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

final class TypeRegistry {

	// Private fields
	// =======================================================================

	private ConnectionPool connPool = null;

	private HashMap<Class<? extends Entity>, String> tableNames = null;
	private HashMap<Class<? extends Entity>, HashMap<String, Method>> getters = null;

	// Constructors
	// =======================================================================

	public TypeRegistry(ConnectionPool connectionPool) {
		this.connPool = connectionPool;

		tableNames = new HashMap<>();
		getters = new HashMap<>();
	}

	// Private methods
	// =======================================================================

	/**
	 * Create SQL tables for a specific entity name
	 * 
	 * @param entityName
	 *            Entity name
	 * @throws SQLException
	 * @throws ConnectionPoolException
	 */
	private void createTables(String entityName) throws SQLException,
			ConnectionPoolException {

		final String tableQuery = String.format(TABLE_QUERY_TEMPLATE,
				entityName, entityName, entityName);
		final String attrQuery = String.format(ATTR_QUERY_TEMPLATE, entityName,
				entityName, entityName, entityName);
		final String largeAttrQuery = String.format(LARGE_ATTR_QUERY_TEMPLATE,
				entityName, entityName, entityName);

		Connection conn = this.connPool.getConnection();
		conn.setAutoCommit(false);

		try {
			conn.createStatement().execute(tableQuery);
			conn.createStatement().execute(attrQuery);
			conn.createStatement().execute(largeAttrQuery);
			conn.commit();
		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (SQLException ignored) {
			}
			throw ex;
		} finally {
			try {
				conn.close();
			} catch (SQLException ignored) {
			}
		}
	}

	// Public methods
	// =======================================================================

	/**
	 * Register a type in the database. Tables will be created automatically.
	 * 
	 * @param entityClass
	 *            The entity class
	 * @throws SQLException
	 *             Thrown when there's error creating the tables
	 * @throws ConnectionPoolException
	 */
	public void registerType(Class<? extends Entity> entityClass)
			throws SQLException, ConnectionPoolException {
		if (entityClass == null || tableNames.containsKey(entityClass)) {
			return;
		}

		String entityName = getEntityName(entityClass);
		tableNames.put(entityClass, entityName);

		Method[] methods = entityClass.getMethods();
		String attrName = null;
		HashMap<String, Method> getters = new HashMap<>();

		for (Method m : methods) {
			if (isGetter(m)) {
				attrName = getAttrNameFromSetter(m);
				getters.put(attrName, m);
			}
		}

		this.getters.put(entityClass, getters);

		this.createTables(entityName);
	}
	
	/**
	 * Checks if a particular entity type is registered
	 * @param entityClass The entity class
	 * @return true if type is registered, false otherwise
	 */
	public boolean isTypeRegistered(Class<? extends Entity> entityClass) {
		return tableNames.containsKey(entityClass);
	}

	/**
	 * Get the entity table name
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getEntityTableName(Class<? extends Entity> entityClass) {
		if (entityClass == null || !tableNames.containsKey(entityClass))
			return null;

		return tableNames.get(entityClass);
	}

	/**
	 * Get the attribute table name
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getAttrTableName(Class<? extends Entity> entityClass) {
		String tableName = getEntityTableName(entityClass);
		if (tableName != null) {
			tableName += "Attr";
		}
		return tableName;
	}

	/**
	 * Get the large attribute table name
	 * 
	 * @param entityClass
	 * @return
	 */
	public String getLargeAttrTableName(Class<? extends Entity> entityClass) {
		String tableName = getEntityTableName(entityClass);
		if (tableName != null) {
			tableName += "LargeAttr";
		}
		return tableName;
	}

	/**
	 * Get a map of attributes and getter methods for a specific entity class
	 * 
	 * @param entityClass
	 *            The entity class
	 * @return
	 */
	public HashMap<String, Method> getGetters(
			Class<? extends Entity> entityClass) {
		if (entityClass == null || !getters.containsKey(entityClass))
			return null;

		return getters.get(entityClass);
	}

	// Static fields
	// =======================================================================

	private static final String TABLE_QUERY_TEMPLATE = "CREATE TABLE IF NOT EXISTS %s ("
			+ "  id varchar(64) not null,"
			+ "  name varchar(256),"
			+ "  creation_epoch bigint not null,"
			+ "  update_epoch bigint not null,"
			+ "  PRIMARY KEY (id),"
			+ "  INDEX %s_ix_creation_epoch (creation_epoch),"
			+ "  INDEX %s_ix_update_epoch (update_epoch)" + ")ENGINE=InnoDB";

	private static final String ATTR_QUERY_TEMPLATE = "CREATE TABLE IF NOT EXISTS %sAttr ("
			+ "  entity_id varchar(65) not null,"
			+ "  attr_key varchar(64) not null,"
			+ "  attr_value varchar(512),"
			+ "  attr_meta varchar(64),"
			+ "  INDEX %sAttr_ix_attr_key(attr_key),"
			+ "  INDEX %sAttr_ix_attr_kv(attr_key, attr_value(128)),"
			+ "  FOREIGN KEY(entity_id) REFERENCES %s(id) ON DELETE CASCADE"
			+ ")ENGINE=InnoDB";

	private static final String LARGE_ATTR_QUERY_TEMPLATE = "CREATE TABLE IF NOT EXISTS %sLargeAttr ("
			+ "  entity_id varchar(65) not null,"
			+ "  attr_key varchar(64) not null,"
			+ "  attr_value text,"
			+ "  attr_meta varchar(64),"
			+ "  INDEX %sLarge_ix_attr_key(attr_key),"
			+ "  FOREIGN KEY(entity_id) REFERENCES %s(id) ON DELETE CASCADE"
			+ ")ENGINE=InnoDB";

	// Static methods
	// =======================================================================

	/**
	 * Returns true if a method is a getter, false otherwise
	 * 
	 * @param method
	 * @return
	 */
	private static boolean isGetter(Method method) {
		if (method == null)
			return false;

		Class<?> returnType = method.getReturnType();
		if (returnType != boolean.class && returnType != String.class
				&& returnType != int.class && returnType != long.class
				&& returnType != float.class && returnType != double.class
				&& !returnType.isEnum()) {
			return false;
		}

		String methodName = method.getName();
		if (!((methodName.startsWith("get") && methodName.length() > 3) || (methodName
				.startsWith("is") && methodName.length() > 2))) {
			return false;
		}
		
		if (method.getAnnotation(Ignore.class) != null) {
			return false;
		}

		return true;
	}

	private static String getAttrNameFromSetter(Method m) {
		String methodName = m.getName();
		if (methodName.startsWith("is"))
			return methodName.substring(2);
		else if (methodName.startsWith("get"))
			return methodName.substring(3);

		return null;
	}

	/**
	 * Get an entity class' name
	 * 
	 * @param entityClass
	 *            The entity class
	 * @return Name of the entity
	 */
	private static String getEntityName(Class<?> entityClass) {
		String name = entityClass.getSimpleName();
		DataEntity anno = entityClass.getAnnotation(DataEntity.class);
		if (anno != null && !anno.name().trim().isEmpty()) {
			name = anno.name().trim();
		}
		return name;
	}
}
