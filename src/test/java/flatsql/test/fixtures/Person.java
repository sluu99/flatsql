package flatsql.test.fixtures;

import java.util.ArrayList;
import java.util.Arrays;

import flatsql.annotations.DataEntity;

@DataEntity(name = "Human")
public class Person extends LivingThing {

	private ArrayList<String> pets = null;
	
	public Person() {
		pets = new ArrayList<>();
	}

	public String[] getPets() {
		String[] petArray = new String[pets.size()];
		pets.toArray(petArray);
		return petArray;
	}

	public void setPets(String[] pets) {
		if (pets == null || pets.length == 0) {
			this.pets.clear();
		} else {			
			this.pets = new ArrayList<String>(Arrays.asList(pets));
		}
	}
	
	public void addPet(String pet) {
		pets.add(pet);
	}
	
	public void removePet(String pet) {
		pets.remove(pet);
	}
}
