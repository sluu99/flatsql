package flatsql;

import java.sql.SQLException;

import exceptions.ConnectionPoolException;

/**
 * Starting point for using FlatSql
 * 
 */
public class FlatSql {

	private ConnectionPool connPool = null;
    private TypeRegistry registry = null;
    private IdGenerator idGen = null;
    private Storage storage = null;

    /**
     * Create a new instance with connection pool and id generator
     * @param connPool
     * @param idGen
     */
    public FlatSql(ConnectionPool connPool, IdGenerator idGen) {
    	this.connPool = connPool;
    	this.idGen = idGen;
    	
        this.registry = new TypeRegistry(connPool);
        this.storage = new Storage(connPool, registry);
    }
    
	/**
	 * Create a new instance with a specific connection pool
	 * 
	 * @param connPool
	 */
	public FlatSql(ConnectionPool connPool) {
		this(connPool, new SimpleIdGenerator());
	}

    /**
     * Register a type in the database.
     * Tables will be created automatically.
     * @param entityClass The entity class
     * @throws java.sql.SQLException Thrown when there's error creating the tables
     * @throws ConnectionPoolException 
     */
    public void registerType(Class<? extends Entity> entityClass) throws SQLException, ConnectionPoolException {
        registry.registerType(entityClass);
    }

    /**
     * Persist an entity into the database
     * @param entity The entity
     * @return true if the operation is successful,false otherwise
     * @throws SQLException 
     * @throws ConnectionPoolException 
     */
    public boolean persist(Entity entity) {
        return persist(entity, null);
    }

    /**
     * Persist an entity into the database
     * @param entity The entity
     * @param error The error message will be appended to the StringBuilder
     * @return true if the operation is successful,false otherwise
     */
    public boolean persist(Entity entity, StringBuilder error) {
    	
    	try {
    		if (entity.id() == null) {
            	entity.id(idGen.newId());
            	storage.insert(entity);
            }
							
		} catch (ConnectionPoolException | SQLException e) {
			if (error != null) {
				error.append(e.getMessage());
			}
			return false;
		}
                
        return true;
    }

    public ConnectionPool getConnectionPool() {
        return this.connPool;
    }

    public void setConnPool(ConnectionPool connPool) {
        this.connPool = connPool;
    }


}
