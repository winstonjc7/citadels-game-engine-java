package citadels;

public class DistrictCard {
    private final String name;
    private final String color;
    private final int cost;
    private final String text;

    public DistrictCard(String name, String color, int cost, String text) {
        this.name = name;
        this.color = color;
        this.cost = cost;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getCost() {
        return cost;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return name + " [" + color + cost + "]";
    }
}
