public class Main {
    public static void main(String[] args) {
        Environment env = new Environment();

        env.grasses.add(new Grass());
        env.trees.add(new FruitTree());

        Animal rabbit = new Rabbit("Rabbit");
        Animal deer = new Deer("Deer");

        for (int i = 0; i < 5; i++) {
            System.out.println("---- Step " + i + " ----");
            rabbit.act(env);
            deer.act(env);
        }
    }
}