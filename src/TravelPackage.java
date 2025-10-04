import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class TravelPackage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int nextID = 1;
    private final int pkgID = nextID++;
    private int days;
    private Customer cust;
    private Accommodation acc;
    private LocalDate date;
    private LiftPass liftPass;
    private boolean hasLiftPass = false;
    private Lessons lessons;
    private boolean hasLessons = false;


    public TravelPackage() {

    }

    public TravelPackage(Customer cust, LocalDate date, int days) {
        this.cust = cust;
        this.date = date;
        this.days = days;
        cust.setHasPackage(); //set customer package status
    }


    public void attachAccommodation(Accommodation acc) {
        this.acc = acc;
        acc.setAvailable(false); //change attached accommodation to unavailable
    }

    public int getID() {
        return pkgID;
    }

    public Customer getCustomer() {
        return cust;
    }

    public Accommodation getAccommodation() {
        return acc;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public LiftPass getLiftPass() {
        return liftPass;
    }

    public void setLiftPass(LiftPass liftPass) {
        this.liftPass = liftPass;
        this.hasLiftPass = true; //set lesson
    }

    public boolean getHasLiftPass() {
        return hasLiftPass;
    }

    public void setLessons(Lessons lesson) {
        this.lessons = lesson;
        this.hasLessons = true;
    }

    public boolean getHasLessons() {
        return hasLessons;
    }


    public double getTotalCost() {
        double totalCost = 0;

        if (acc != null) {
            totalCost += acc.getPrice();
        }

        if (liftPass != null) {
            totalCost += liftPass.getCost();
        }

        if (lessons != null) {
            totalCost += lessons.getCost();
        }

        return totalCost;
    }

    @Override
    public String toString() {
        String result = String.format(
                "Package %d { customer: \"%s\", accommodation: %s, date: %s, days: %d, total cost: $%.2f }",
                pkgID,
                cust.getName(),
                acc.getType(),
                date,
                days, getTotalCost()
        );
        //append lift pass
        if (liftPass != null) {
            result += "\n   " + liftPass;
        } else {
            result += "\n   Lift Pass: None";
        }

        //append lessons
        if (lessons != null) {
            result += "\n   " + lessons;
        } else {
            result += "\n   Lessons: None";
        }

        return result;
    }
}