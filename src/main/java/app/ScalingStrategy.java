package app;

enum ScalingStrategy {
    PROPORTIONAL("proporcjonalna"),
    TRI_VALUE("trójwartościowa"),
    CUTTING("obcinająca");

    private String description;

    ScalingStrategy(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
