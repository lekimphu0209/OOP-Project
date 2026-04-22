class ThirstyState implements State {
    public void handle(Animal animal, Environment env) {
        System.out.println(animal.name + " is thirsty...");

        if (env.hasWater) {
            System.out.println(animal.name + " is drinking water...");
            animal.thirst = 0;
            animal.setState(new WanderingState());
        } else {
            System.out.println("No water!");
        }
    }
}