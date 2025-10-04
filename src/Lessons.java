import java.io.Serial;
import java.io.Serializable;

public class Lessons implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int count;
    private double cost;

    public Lessons() {

    }

    public Lessons(String level, int count) {
        this.count = count;
        this.cost = count * lessonPrice(level);
    }

    public static double lessonPrice(String level) {
        return switch (level.toLowerCase()) {
            case "expert" -> 15.0;
            case "intermediate" -> 20.0;
            case "beginner" -> 25.0;
            default -> throw new IllegalArgumentException("Invalid level");
        };
    }

    public double getCost() {
        return cost;
    }

    public String toString() {
        return "Lessons: " + count + " x $" + (cost / count) + " = $" + cost;
    }
}