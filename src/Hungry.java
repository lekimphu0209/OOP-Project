class HungryState implements State {
    public void handle(Animal animal, Environment env) {
        System.out.println(animal.name + " is hungry...");

        Food food = animal.findFood(env);
        if (food != null) {
            animal.eat(food);
            animal.hunger = 0;
            animal.setState(new WanderingState());
        } else {
            System.out.println("No food found!");
        }
    }
}