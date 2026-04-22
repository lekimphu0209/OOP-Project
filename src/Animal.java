abstract class Animal {
    String name;
    int hunger = 0;
    int thirst = 0;
    State state;

    public Animal(String name) {
        this.name = name;
        this.state = new WanderingState();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void act(Environment env) {
        hunger++;
        thirst++;
        state.handle(this, env);
    }

    abstract Food findFood(Environment env);

    public void eat(Food food) {
        System.out.println(name + " is eating...");
        food.beEaten();
    }
}