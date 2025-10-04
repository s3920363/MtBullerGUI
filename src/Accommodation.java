import java.io.Serial;
import java.io.Serializable;

public class Accommodation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int nextID = 101;
    private final int accID = nextID++;
    private String type;
    private double price;
    private boolean available = true;

    public Accommodation() {

    }

    public Accommodation(String type, int price) {
        this.type = type;
        this.price = price;

    }

    public int getID() {
        return accID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format(
                "{ id: %d, type: \"%s\", price: $%.2f, available: %b }",
                accID, type, price, available
        );
    }
}