package flatsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import exceptions.ConnectionPoolException;
import flatsql.util.JdbcUtil;


final class Storage {

	private ConnectionPool connPool = null;
	
	public Storage(ConnectionPool connPool) {
		this.connPool = connPool;
	}
	
	/**
	 * Insert an entity into the database
	 * @param tableName Entity table name
	 * @param entity The entity
	 * @throws ConnectionPoolException 
	 * @throws SQLException 
	 */
	public void insertEntity(String tableName, Entity entity) throws ConnectionPoolException, SQLException {
						
		Connection conn = null;
		PreparedStatement stmt = null;
		long now = System.currentTimeMillis();
		
		entity.creationEpoch(now);
		entity.updateEpoch(now);
		
		try {
			conn = connPool.getConnection();
			stmt = conn.prepareStatement(String.format(INSERT_SQL, tableName));
			stmt.setString(1, entity.id());		
			stmt.setString(2, entity.name());
			stmt.setLong(3, entity.creationEpoch());
			stmt.setLong(4, entity.updateEpoch());
			
			stmt.execute();
		} finally {		
			JdbcUtil.close(null, stmt, conn);
		}
	}
	
	private static final String INSERT_SQL = 
		"INSERT INTO %s (id, name, creation_epoch, update_epoch) " +
		"VALUES (?, ?, ?, ?) ";
	
}
