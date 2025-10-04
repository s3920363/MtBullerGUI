import java.io.Serial;
import java.io.Serializable;

public class LiftPass implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String type;
    private int days;
    private double cost;

    public LiftPass() {

    }

    public LiftPass(String type, int days) {
        this.type = type;
        this.days = days;
        calculateCost();
    }

    public void calculateCost() {
        if (type.equalsIgnoreCase("Season")) {
            cost = 200;
        } else if (type.equalsIgnoreCase("Daily")) {
            cost = 26 * days;
            if (days == 5) {
                cost = cost * 0.9; // 10% discount
            }
        }
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        if (type.equalsIgnoreCase("Season")) {
            return "Lift Pass: Season ($200)";
        } else if (type.equalsIgnoreCase("Daily")) {
            return "Lift Pass: " + days + " days ($" + cost + ")";
        } else {
            return "Lift Pass: None";
        }
    }
}