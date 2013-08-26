package flatsql;

import flatsql.TypeRegistry;
import flatsql.exceptions.ConnectionPoolException;
import flatsql.test.MySqlBasedTest;
import flatsql.test.fixtures.Animal;
import flatsql.test.fixtures.ClassWithDataEntityAnno;
import flatsql.test.fixtures.ClassWithoutDataEntityAnno;
import flatsql.test.fixtures.Person;
import flatsql.test.SimpleConnectionPool;

import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.HashMap;

import static org.testng.Assert.*;

import java.lang.reflect.*;

/**
 * Test class for TypeRegistry
 * 
 */

@Test
public class TypeRegistryTest extends MySqlBasedTest {

	/**
	 * This test ensure that registering a class without the DataEntity
	 * annotation will use the class name as table name
	 * 
	 * @throws ConnectionPoolException
	 */
	public void testClassNameAsTableName() throws SQLException,
			ConnectionPoolException {
		TypeRegistry registry = new TypeRegistry(new SimpleConnectionPool());
		registry.registerType(ClassWithoutDataEntityAnno.class);

		assertTableExists("ClassWithoutDataEntityAnno");
		assertTableExists("ClassWithoutDataEntityAnnoAttr");
		assertTableExists("ClassWithoutDataEntityAnnoLargeAttr");

		assertEquals(
				registry.getEntityTableName(ClassWithoutDataEntityAnno.class),
				"ClassWithoutDataEntityAnno");
		assertEquals(
				registry.getAttrTableName(ClassWithoutDataEntityAnno.class),
				"ClassWithoutDataEntityAnnoAttr");
		assertEquals(
				registry.getLargeAttrTableName(ClassWithoutDataEntityAnno.class),
				"ClassWithoutDataEntityAnnoLargeAttr");
	}

	/**
	 * Registering a class with DataEntity annotation will create a table with
	 * the specified name
	 * 
	 * @throws SQLException
	 * @throws ConnectionPoolException
	 */
	public void testDataAnnotationTableName() throws SQLException,
			ConnectionPoolException {
		TypeRegistry registry = new TypeRegistry((new SimpleConnectionPool()));
		registry.registerType(ClassWithDataEntityAnno.class);

		assertTableExists("CustomTableName");
		assertTableExists("CustomTableNameAttr");
		assertTableExists("CustomTableNameLargeAttr");

		assertEquals(
				registry.getEntityTableName(ClassWithDataEntityAnno.class),
				"CustomTableName");
		assertEquals(registry.getAttrTableName(ClassWithDataEntityAnno.class),
				"CustomTableNameAttr");
		assertEquals(
				registry.getLargeAttrTableName(ClassWithDataEntityAnno.class),
				"CustomTableNameLargeAttr");
	}

	/**
	 * Ensure that getters are registered properly
	 * @throws SQLException
	 * @throws ConnectionPoolException
	 */
	public void testGetters() throws SQLException, ConnectionPoolException {
		TypeRegistry registry = new TypeRegistry(new SimpleConnectionPool());
		registry.registerType(Animal.class);

		HashMap<String, Method> getters = registry.getGetters(Animal.class);

		assertTrue(getters.containsKey("Age"));
		assertTrue(getters.containsKey("Name"));
		assertTrue(getters.containsKey("AnimalType"));
		assertTrue(getters.containsKey("Pet"));
	}
	
	/**
	 * Ensure that isTypeRegistered returns correctly
	 * @return 
	 * @throws ConnectionPoolException 
	 * @throws SQLException 
	 */
	public void testIsRegistered() throws SQLException, ConnectionPoolException {
		TypeRegistry registry = new TypeRegistry(new SimpleConnectionPool());
		
		assertFalse(registry.isTypeRegistered(Animal.class));
		
		registry.registerType(Animal.class);
		
		assertFalse(registry.isTypeRegistered(Person.class));
		assertTrue(registry.isTypeRegistered(Animal.class));
		
		registry.registerType(Person.class);
		assertTrue(registry.isTypeRegistered(Person.class));		
	}

}
