package flatsql;

import static org.junit.Assert.*;

import org.testng.annotations.Test;

import flatsql.SimpleIdGenerator;

@Test
public class SimpleIdGeneratorTest {

	/**
	 * Default length of the generated ID should be 5
	 * @return 
	 */
	public void testDefaultIdLength() {
		
		SimpleIdGenerator gen = new SimpleIdGenerator();
		String id = null;
		
		for (int i = 0; i < 1000; i++) {
			id = gen.newId();
			assertNotNull(id);
			assertEquals(5, id.length());
		}		
	}
	
	
	public void testDuplicateId() {
		SimpleIdGenerator gen = new SimpleIdGenerator();
		String id = null;
		String diffId = null;
		
		for (int i = 0; i < 1000; i++) {
			id = gen.newId();
			diffId = gen.newId(id);
			
			assertNotNull(diffId);
			assertFalse(id == diffId);
		}
	}
}
