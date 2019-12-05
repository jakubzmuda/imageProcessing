package app;

enum BorderOperationStrategy {
    NO_CHANGE("bez zmian"),
    DUPLICATE("powielenie"),
    EXISTING_ONLY("istniejące");

    private String description;

    BorderOperationStrategy(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
