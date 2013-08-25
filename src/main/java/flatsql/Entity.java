package flatsql;

/**
 * Base Entity class
 *
 */
public abstract class Entity {

	private String id = null;
	private String entityName = null;
	private long creationEpoch = 0;
	private long updateEpoch = 0;
	
	/**
	 * Sets the ID for this entity
	 * @param id
	 */
	void id(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the ID for this entity
	 * @return The ID, or null if the entity hasn't been persisted
	 */
    public String id() {
		return this.id;
	}

    /**
     * Returns the name of this entity
     * @return Entity name
     */
    public String name() {
        return this.entityName;
    }

    /**
     * Sets the name for this entity
     * @param entityName Entity name
     */
    public void name(String entityName) {
        this.entityName = entityName;
    }
    
    /**
     * Sets creation epoch
     * @param epoch
     */
    void creationEpoch(long epoch) {
    	this.creationEpoch = epoch;
    }
    
    /**
     * Returns the creation epoch
     * @return
     */
    public long creationEpoch() {
    	return this.creationEpoch; 
    }
    
    /**
     * Sets the update epoch
     * @param epoch
     */
    void updateEpoch(long epoch) {
    	this.updateEpoch = epoch;
    }
    
    /**
     * Gets the update epoch
     * @return
     */
    public long updateEpoch() {
    	return this.updateEpoch;
    }
}
