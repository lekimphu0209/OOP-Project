class WanderingState implements State {
    public void handle(Animal animal, Environment env) {
        System.out.println(animal.name + " is wandering...");

        if (animal.hunger > 5) {
            animal.setState(new HungryState());
        } else if (animal.thirst > 5) {
            animal.setState(new ThirstyState());
        }
    }
}