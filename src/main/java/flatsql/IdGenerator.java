package flatsql;

public interface IdGenerator {
	
	/**
	 * Returns a new ID
	 * @return a new ID
	 */
	String newId();
	
	/**
	 * Returns a new ID which must be different from the given ID
	 * @param mustBeDiff The new ID must be different from this ID
	 * @return a new ID
	 */
	String newId(String mustBeDiff);
}
