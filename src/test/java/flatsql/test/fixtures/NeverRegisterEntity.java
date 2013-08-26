package flatsql.test.fixtures;

import flatsql.Entity;

/**
 * This entity is used for testing to trap an exception. Never register this type
 *
 */
public class NeverRegisterEntity extends Entity {

	private String dummy;
	
	public String getDummy() {
		return dummy;
	}
	
	public void setDummu(String dummy) {
		this.dummy = dummy;
	}
	
}
