package flatsql.test.fixtures;

public class Animal extends LivingThing {

    private AnimalType animalType = null;
    private boolean isPet = false;


    public AnimalType getAnimalType() {
        return animalType;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }

    public boolean isPet() {
        return isPet;
    }

    public void setPet(boolean pet) {
        isPet = pet;
    }
}
