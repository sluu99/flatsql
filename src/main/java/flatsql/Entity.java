package flatsql;

/**
 * Base Entity class
 *
 */
public abstract class Entity {

	protected String id = null;
	
	/**
	 * Sets the ID for this entity
	 * @param id
	 */
	void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the ID for this entity
	 * @return The ID, or null if the entity hasn't been persisted
	 */
	String getId() { 
		return this.id;
	}
}
