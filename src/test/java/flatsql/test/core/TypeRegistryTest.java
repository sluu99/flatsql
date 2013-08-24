package flatsql.test.core;

import flatsql.core.TypeRegistry;
import flatsql.test.MySqlBasedTest;
import flatsql.test.fixtures.ClassWithDataEntityAnno;
import flatsql.test.fixtures.ClassWithoutDataEntityAnno;
import flatsql.test.SimpleConnectionPool;
import org.testng.annotations.Test;

import java.sql.SQLException;

/**
 * Test class for TypeRegistry
 *
 */

@Test
public class TypeRegistryTest extends MySqlBasedTest {

    /**
     * This test ensure that registering a class without the DataEntity annotation
     * will use the class name as table name
     */
    public void testClassNameAsTableName() throws SQLException {
        TypeRegistry registry = new TypeRegistry(new SimpleConnectionPool());
        registry.registerType(ClassWithoutDataEntityAnno.class);

        assertTableExists("ClassWithoutDataEntityAnno");
        assertTableExists("ClassWithoutDataEntityAnnoAttr");
        assertTableExists("ClassWithoutDataEntityAnnoLargeAttr");
    }

    /**
     * Registering a class with DataEntity annotation will
     * create a table with the specified name
     * @throws SQLException
     */
    public void testDataAnnotationTableName() throws SQLException {
        TypeRegistry registry = new TypeRegistry((new SimpleConnectionPool()));
        registry.registerType(ClassWithDataEntityAnno.class);

        assertTableExists("CustomTableName");
        assertTableExists("CustomTableNameAttr");
        assertTableExists("CustomTableNameLargeAttr");
    }



}
