package flatsql.test.fixtures;

import flatsql.annotations.DataEntity;

@DataEntity(name = "Human")
public class Person extends LivingThing {

	private String[] pets;

	public String[] getPets() {
		return pets;
	}

	public void setPets(String[] pets) {
		this.pets = pets;
	}
}
