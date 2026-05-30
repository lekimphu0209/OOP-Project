package ecosystem.environment;

/**
 * Manages season information and transitions.
 * Extracted from Environment to reduce file size.
 */
public class SeasonManager {
    public enum Season {
        SPRING("Mùa Xuân", 1.2, "Sinh sản nhiều (1.2x)", 60, 30, 50, 15, 25),
        SUMMER("Mùa Hạ", 1.0, "Bình thường (1.0x)", 60, 25, 40, 12, 20),
        AUTUMN("Mùa Thu", 0.8, "Giảm dần (0.8x)", 60, 20, 30, 10, 15),
        WINTER("Mùa Đông", 0.5, "Ít sinh sản (0.5x)", 60, 15, 20, 8, 10);

        private final String name;
        private final double populationMultiplier;
        private final String description;
        private final int duration; // ticks (1 tick = 500ms)
        private final int maxAnimals;
        private final int maxPlants;
        private final int minAnimals;
        private final int minPlants;

        Season(String name, double populationMultiplier, String description, int duration, int maxAnimals, int maxPlants, int minAnimals, int minPlants) {
            this.name = name;
            this.populationMultiplier = populationMultiplier;
            this.description = description;
            this.duration = duration;
            this.maxAnimals = maxAnimals;
            this.maxPlants = maxPlants;
            this.minAnimals = minAnimals;
            this.minPlants = minPlants;
        }

        public String getName() {
            return name;
        }

        public double getPopulationMultiplier() {
            return populationMultiplier;
        }

        public String getDescription() {
            return description;
        }

        public int getDuration() {
            return duration;
        }

        public int getMaxAnimals() {
            return maxAnimals;
        }

        public int getMaxPlants() {
            return maxPlants;
        }

        public int getMinAnimals() {
            return minAnimals;
        }

        public int getMinPlants() {
            return minPlants;
        }

        public Season next() {
            Season[] seasons = values();
            return seasons[(this.ordinal() + 1) % seasons.length];
        }
    }

    private Season currentSeason;
    private int ticksInCurrentSeason = 0;

    public SeasonManager() {
        this.currentSeason = Season.SPRING;
    }

    public Season getSeason() {
        return currentSeason;
    }

    public void setSeason(Season season) {
        this.currentSeason = season;
        this.ticksInCurrentSeason = 0;
    }

    public void nextSeason() {
        this.currentSeason = currentSeason.next();
        this.ticksInCurrentSeason = 0;
    }

    public void tick() {
        ticksInCurrentSeason++;
        if (ticksInCurrentSeason >= currentSeason.getDuration()) {
            nextSeason();
        }
    }

    public int getTicksInCurrentSeason() {
        return ticksInCurrentSeason;
    }
}
