import java.io.Serial;
import java.io.Serializable;

public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int nextID = 1;
    private final int custID = nextID++;
    private boolean hasPackage = false;
    private String name;
    private String email;
    private String skillLevel;

    public Customer() {
    }

    public Customer(String name, String email, String skillLevel) {
        this.name = name;
        this.email = email;
        this.skillLevel = skillLevel;
    }

    public int getID() {
        return custID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public void setHasPackage() {
        this.hasPackage = true;
    }

    public boolean inPackage() {
        return hasPackage;
    }

    @Override
    public String toString() {
        return String.format(
                "{ id: %d, name: \"%s\", email: \"%s\", skillLevel: \"%s\" }",
                custID, name, email, skillLevel
        );
    }


}