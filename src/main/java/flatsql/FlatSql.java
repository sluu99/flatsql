package flatsql;

/**
 * Starting point for using FlatSql
 * 
 */
public class FlatSql {

	private ConnectionPool connPool;

	/**
	 * Create a new instance with a specific connection pool
	 * 
	 * @param connPool
	 */
	public FlatSql(ConnectionPool connPool) {
		this.connPool = connPool;
	}

    //<editor-fold desc="Getters & setters">

    public ConnectionPool getConnectionPool() {
        return this.connPool;
    }

    public void setConnPool(ConnectionPool connPool) {
        this.connPool = connPool;
    }

    //</editor-fold>
}
