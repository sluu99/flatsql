package flatsql;

import exceptions.ConnectionPoolException;
import flatsql.FlatSql;
import flatsql.test.MySqlBasedTest;
import flatsql.test.SimpleConnectionPool;
import flatsql.test.fixtures.Animal;
import flatsql.test.fixtures.AnimalType;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;

@Test
public class FlatSqlTest extends MySqlBasedTest {

	FlatSql flatSql;

	@BeforeClass
	public void setup() {
		super.setup();

		flatSql = new FlatSql(new SimpleConnectionPool());
		try {
			flatSql.registerType(Animal.class);
		} catch (SQLException | ConnectionPoolException ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void testBasicInsert() {
		Animal duke = new Animal();
		duke.setAnimalType(AnimalType.DOG);
		duke.setPet(true);
		duke.setAge(12);
		duke.setName("Duke");
		duke.name("Duke");

		Assert.assertTrue(flatSql.persist(duke),
				"`persist` must return true if the operation is successful");
		Assert.assertNotNull(duke.id());

		// integrity check
		Assert.assertTrue(duke.isPet());
		Assert.assertEquals(duke.getName(), "Duke");
		Assert.assertEquals(duke.getAge(), 12);
		Assert.assertEquals(duke.getAnimalType(), AnimalType.DOG);

		assertRecordExists("Animal", duke.id(), duke.name());

		assertAttrExists("Animal", duke.id(), "Pet", "true", null);
		assertAttrExists("Animal", duke.id(), "Age", "12", null);
		assertAttrExists("Animal", duke.id(), "AnimalType", "DOG", null);
		assertAttrExists("Animal", duke.id(), "Name", "Duke", null);
	}

}
