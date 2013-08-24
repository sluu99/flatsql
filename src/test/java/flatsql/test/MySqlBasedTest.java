package flatsql.test;

import flatsql.util.JdbcUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class MySqlBasedTest {

    protected Connection conn = null;

    @BeforeClass
    public void setup() {
        try {
            TestHelper.createTestDatabase();
            conn = TestHelper.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Unable to setup tests");
        }
    }

    @AfterClass
    public void tearDown() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            TestHelper.dropTestDatabase();
        } catch (Exception ex) {
        }
    }

    /**
     * Assert that a table exists
     * @param tableName
     */
    protected void assertTableExists(String tableName) {

        try {
            PreparedStatement stmt = conn.prepareStatement("SHOW TABLES LIKE ?");
            stmt.setString(1, tableName);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            JdbcUtil.close(rs, stmt);

            if (!exists) {
                Assert.fail(String.format("Table '%s' does not exist", tableName));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
}
