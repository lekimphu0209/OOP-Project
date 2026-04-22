class Rabbit extends Animal {
    public Rabbit(String name) {
        super(name);
    }

    @Override
    Food findFood(Environment env) {
        return env.findGrass();
    }
}