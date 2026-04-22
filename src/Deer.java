class Deer extends Animal {
    public Deer(String name) {
        super(name);
    }

    @Override
    Food findFood(Environment env) {
        Food grass = env.findGrass();
        if (grass != null) return grass;
        return env.findFruitTree();
    }
}