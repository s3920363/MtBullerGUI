import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class MtBullerResort {
    ArrayList<Customer> customers = new ArrayList<>();
    ArrayList<Accommodation> accommodations = new ArrayList<>();
    ArrayList<TravelPackage> packages = new ArrayList<>();

    Scanner input = new Scanner(System.in);

    public void populateLists() {
        customers.add(new Customer("John", "john@gmail.com", "expert"));
        customers.add(new Customer("Alice", "alice@yahoo.com", "beginner"));
        customers.add(new Customer("Bob", "bob@hotmail.com", "intermediate"));
        customers.add(new Customer("Diana", "diana@outlook.com", "expert"));

        accommodations.add(new Accommodation("hotel", 300));
        accommodations.add(new Accommodation("apartment", 220));
        accommodations.add(new Accommodation("lodge", 180));
        accommodations.add(new Accommodation("cabin", 140));
        accommodations.add(new Accommodation("hotel", 400));
        accommodations.add(new Accommodation("apartment", 275));
        accommodations.add(new Accommodation("lodge", 210));
        accommodations.add(new Accommodation("cabin", 160));
        accommodations.add(new Accommodation("hotel", 350));
        accommodations.add(new Accommodation("apartment", 260));

        packages.add(new TravelPackage(customers.getFirst(), LocalDate.now(), 3));
        packages.getFirst().attachAccommodation(accommodations.getFirst());
    }

    public void run() {
        boolean running = true;
        System.out.println("------------ Welcome to Mount Buller Resort! ------------");

        while (running) {
            try {
                System.out.println("\n1. Display all accommodations");
                System.out.println("2. Display available accommodations");
                System.out.println("3. Add a customer");
                System.out.println("4. List all customers");
                System.out.println("5. Create a package");
                System.out.println("6. List all packages");
                System.out.println("7. Add a Lift Pass to a package");
                System.out.println("8. Add Lessons to a package");
                System.out.println("9. Save packages to a file");
                System.out.println("10. Read packages from a file");
                System.out.println("11. Quit");

                System.out.print("\nPlease choose an option: ");

                int option = input.nextInt();
                input.nextLine(); // consume leftover newline

                switch (option) {
                    case 1 -> listAllAccommodations();
                    case 2 -> listAvailableAccommodations();
                    case 3 -> addCustomer();
                    case 4 -> listCustomers();
                    case 5 -> addPackage();
                    case 6 -> listPackages();
                    case 7 -> addLiftPass();
                    case 8 -> addLessons();
                    case 9 -> writePackages();
                    case 10 -> readPackages();
                    case 11 -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                input.nextLine();
            }
        }
    }

    public void listCustomers() {
        System.out.println("\n----- Customers -----");
        for (Customer customer : customers) {
            System.out.println(customer);

        }
    }

    public void listAllAccommodations() {
        System.out.println("\n----- All Accommodations -----");
        for (Accommodation accommodation : accommodations) {
            System.out.println(accommodation);
        }
    }

    public void listAvailableAccommodations() {
        System.out.println("\n----- Available Accommodations -----");
        boolean foundAvailable = false;

        for (Accommodation accommodation : accommodations) {
            if (accommodation.isAvailable()) {
                System.out.println(accommodation);
                foundAvailable = true;
            }
        }

        if (!foundAvailable) {
            System.out.println("There is no available accommodation!");
        }
    }

    public int listAccommodationByType(String type) {
        System.out.println("\n----- Available Accommodations (" + type + ") -----");
        int count = 0;
        for (Accommodation accommodation : accommodations) {
            if (accommodation.getType().equalsIgnoreCase(type) && accommodation.isAvailable()) {
                System.out.println(accommodation);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No accommodations found for type: " + type);
        }
        return count;
    }

    public int listAccommodationByPrice(double price) {
        System.out.println("\n----- Available Accommodations (Max $" + price + ") -----");
        int count = 0;
        for (Accommodation accommodation : accommodations) {
            if (accommodation.getPrice() <= price && accommodation.isAvailable()) {
                System.out.println(accommodation);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No accommodations found under $" + price);
        }
        return count;
    }

    public void addCustomer() {
        boolean valid = false;

        while (!valid) {
            try {
                System.out.print("Enter customer name: ");
                String name = input.nextLine().trim();

                System.out.print("Enter customer email: ");
                String email = input.nextLine().trim();

                System.out.print("Enter skill level (Beginner / Intermediate / Expert): ");
                String skillLevel = input.nextLine().toLowerCase().trim();

                // empty check
                if (name.isEmpty() || email.isEmpty() || skillLevel.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled.");
                }

                //keep asking until valid skill level is entered
                if (!(skillLevel.equals("beginner") || skillLevel.equals("intermediate") || skillLevel.equals("expert"))) {
                    throw new IllegalArgumentException("Skill level must be Beginner, Intermediate, or Expert.");
                }

                Customer newCustomer = new Customer(name, email, skillLevel);
                customers.add(newCustomer);

                System.out.println("\nCustomer added successfully: " + newCustomer);
                valid = true;

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.\n");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                input.nextLine();
            }
        }
    }

    public void listPackages() {
        System.out.println("\n----- Packages -----");
        if (packages.isEmpty())
            System.out.println("There are no packages.");
        else {
            for (TravelPackage pkg : packages) {
                System.out.println(pkg);
            }
        }
    }

    public void addLiftPass() {
        TravelPackage selectedPkg = selectPackage(false);
        if (selectedPkg == null) {
            return;
        }

        LiftPass pass;

        System.out.println("----- Lift Pass Pricing -----");
        System.out.println("Full Day: $26");
        System.out.println("5 Day Pass: $117 (10% off)");
        System.out.println("Season: $200");
        System.out.println("-----------------------------");

        while (true) {
            try {
                System.out.print("Select Lift Pass type (Daily/Season): ");
                String type = input.nextLine().trim();

                if (type.equalsIgnoreCase("Season")) {
                    pass = new LiftPass("Season", 0);
                    break; // valid input, exit loop

                } else if (type.equalsIgnoreCase("Daily")) {
                    System.out.print("Enter number of days: ");
                    int days = Integer.parseInt(input.nextLine().trim());

                    if (days <= 0) {
                        throw new IllegalArgumentException("Days must be greater than 0.");
                    }

                    pass = new LiftPass("Daily", days);
                    break; // valid input, exit loop

                } else {
                    throw new IllegalArgumentException("Invalid type. Enter 'Daily' or 'Season'.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number for days.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

        //attach to the package
        selectedPkg.setLiftPass(pass);
        System.out.println("Lift pass added successfully!");
        System.out.println(selectedPkg);
    }

    public void addLessons() {
        TravelPackage selectedPkg = selectPackage(true);
        if (selectedPkg == null) {
            return;
        }

        String level = selectedPkg.getCustomer().getSkillLevel();
        System.out.printf("The selected customer has skill level '%s'. Lessons will be $%.0f each.\n", level, Lessons.lessonPrice(level));

        System.out.print("Enter number of lessons: ");
        int count = -1;
        while (count <= 0) {
            try {
                count = input.nextInt();
                input.nextLine();

                if (count <= 0) {
                    System.out.println("Please enter a positive number of lessons.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
                input.nextLine();
            }
        }

        Lessons lessons = new Lessons(level, count);

        // attach to the package
        selectedPkg.setLessons(lessons);

        System.out.println("Lessons added successfully!");
        System.out.println(selectedPkg);
    }

    public void addPackage() {
        //select customer object
        Customer selectedCustomer = selectCustomer();
        if (selectedCustomer == null) {
            return;
        }
        
        //select accommodation object
        Accommodation selectedAccommodation = selectAccommodation();
        if (selectedAccommodation == null) {
            return;
        }

        LocalDate travelDate = null;
        while (travelDate == null) {
            System.out.print("Enter travel date (YYYY-MM-DD) or 'now': ");
            String dateInput = input.next().trim();

            if (dateInput.equalsIgnoreCase("now")) {
                travelDate = LocalDate.now();
            } else {
                try {
                    travelDate = LocalDate.parse(dateInput);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format! Please try again.");
                }
            }
        }

        int days = -1;
        while (days < 0) {
            System.out.print("Enter number of days: ");

            try {
                days = input.nextInt();
                input.nextLine();

                if (days > 0) {
                    TravelPackage newPackage = new TravelPackage(selectedCustomer, travelDate, days);
                    newPackage.attachAccommodation(selectedAccommodation);
                    packages.add(newPackage);

                    System.out.println("Package created successfully!");
                    System.out.println(newPackage);
                } else {
                    System.out.println("Number of days must be positive!");
                    days = -1; // keep looping
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                input.nextLine(); // clear invalid input
                days = -1;
            }
        }
    }

    //save packages with name input
    public void writePackages() {
        System.out.print("Enter the file name (blank for packages.dat): ");
        String fileName = input.nextLine().trim();

        //default to packages.dat if no input
        if (fileName.isEmpty()) {
            fileName = "packages.dat";
        } else if (!fileName.endsWith(".dat")) {
            fileName += ".dat";
        }

        File file = new File(fileName);
        //overwrite confirmation
        if (file.exists()) {
            String choice;
            while (true) {
                System.out.print(fileName + " already exists. Overwrite? (y/n): ");
                choice = input.nextLine().trim().toLowerCase();
                if (choice.equals("y") || choice.equals("n")) {
                    break;
                }
                System.out.println("Please enter 'y' or 'n'.");
            }
            if (choice.equals("n")) {
                System.out.println("Save cancelled.");
                return;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(packages);
            System.out.println("Packages saved successfully to " + fileName);

        } catch (Exception e) {
            System.out.println("Error writing packages: " + e.getMessage());
        }
    }

    //read packages with name input
    public void readPackages() {
        //clear existing packages before loading from file
        packages.clear();
        System.out.print("Enter the file name (blank for packages.dat): ");
        String name = input.nextLine().trim().toLowerCase();

        //default to packages.dat if no input
        if (name.isEmpty()) {
            name = "packages.dat";
        } else if (!name.endsWith(".dat")) {
            name += ".dat";
        }

        File f = new File(name);
        if (!f.exists()) {
            System.out.println("No such file: " + name);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            packages = (ArrayList<TravelPackage>) ois.readObject();

            //update customer and accommodation status after loading packages

            for (TravelPackage pkg : packages) {
                Customer c = searchCustomerByID(pkg.getCustomer().getID());
                if (c != null) {
                    c.setHasPackage();
                } else {
                    //add customer if not in current list (does not replace existing id)
                    Customer newCustomer = pkg.getCustomer();
                    newCustomer.setHasPackage();
                    customers.add(newCustomer);
                    System.out.println("Added customer: " + newCustomer);
                }

                Accommodation a = searchAccommodationByID(pkg.getAccommodation().getID());
                if (a != null) a.setAvailable(false);
            }
            System.out.println("Packages loaded from " + f.getName());
            listPackages();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + name);
        } catch (Exception e) {
            System.out.println("Error reading packages: " + e.getMessage());
        }
    }

    //returns a valid customer
    public Customer selectCustomer() {
        Customer selected = null;
        //display customers
        listCustomers();

        //prompt until correct selection
        while (selected == null) {
            System.out.println("Enter customer ID (or 0 to exit):");
            try {
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("Package creation cancelled.");
                    return null;
                }

                //search customer by ID
                selected = searchCustomerByID(choice);

                if (selected == null) {
                    System.out.println("Customer not found! Please try again.");
                } else if (selected.inPackage()) { //customer can only have one active package

                    System.out.println("Customer already has a package! Please select a different customer.");
                    selected = null;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                input.nextLine();
            }
        }
        return selected;
    }

    //returns valid accommodation with filtering
    public Accommodation selectAccommodation() {
        Accommodation selected = null;

        //filter options
        System.out.println("Choose accommodation by:");
        System.out.println("1. Show all available");
        System.out.println("2. Filter by type");
        System.out.println("3. Filter by price");
        System.out.print("Enter option: ");

        int filterOption;
        try {
            filterOption = input.nextInt();
            input.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input, defaulting to show all.");
            input.nextLine();
            filterOption = 1;
        }

        // Apply chosen filter
        switch (filterOption) {
            case 1:
                listAvailableAccommodations();
                break;
            case 2:
                String type = "";
                boolean validType = false;

                while (!validType) {
                    System.out.print("Enter type (Hotel/Lodge/Apartment/Cabin) or 0 to exit: ");
                    type = input.nextLine().trim();

                    if (type.equals("0")) {
                        System.out.println("Package creation cancelled.");
                        return null;
                    }

                    if (type.equalsIgnoreCase("hotel") ||
                            type.equalsIgnoreCase("lodge") ||
                            type.equalsIgnoreCase("apartment") ||
                            type.equalsIgnoreCase("cabin")) {
                        validType = true;
                    } else {
                        System.out.println("Invalid type! Please enter one of the listed options.");
                    }
                }

                int typeCount = listAccommodationByType(type);
                if (typeCount == 0) {
                    return null; //if no matches, exit
                }
                break;


            case 3:
                double price = -1;
                while (price <= 0) {
                    try {
                        System.out.print("Enter max price (must be > 0): ");
                        price = input.nextDouble();
                        input.nextLine();

                        if (price <= 0) {
                            System.out.println("Price must be greater than 0. Try again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter a number.");
                        input.nextLine(); // clear invalid input
                        price = -1; // force loop to continue
                    }
                }
                int priceCount = listAccommodationByPrice(price);
                if (priceCount == 0) {
                    return null; //if no matches, exit
                }
                break;
            default:
                System.out.println("Invalid option, showing all.");
                listAvailableAccommodations();
        }


        while (selected == null) {
            System.out.println("Enter accommodation ID (or 0 to exit):");
            try {
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("Package creation cancelled.");
                    return null;
                }

                selected = searchAccommodationByID(choice);

                if (selected == null) {
                    System.out.println("Accommodation not found! Please try again.");
                } else if (!selected.isAvailable()) {
                    System.out.println("Accommodation is not available! Please try again.");
                    selected = null;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                input.nextLine();
            }
        }
        return selected;
    }

    //returns valid package for either lift pass or lessons
    public TravelPackage selectPackage(boolean forLessons) {
        TravelPackage selected = null;
        listPackages();

        while (selected == null) {
            System.out.print("Enter package number (or 0 to exit): ");
            try {
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("Action cancelled.");
                    return null;
                }

                selected = searchPackageByID(choice);
                if (selected == null) {
                    System.out.println("Package not found! Please try again.");
                    continue;
                }
                //check if package already has lift pass
                if (!forLessons && selected.getHasLiftPass()) {
                    System.out.println("Package already has a Lift Pass!");
                    selected = null;
                    continue;
                }
                //check if package already has lessons
                if (forLessons && selected.getHasLessons()) {
                    System.out.println("Package already has Lessons!");
                    selected = null;
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                input.nextLine();
            }
        }
        return selected;
    }

    public Customer searchCustomerByID(int ID) {
        for (Customer customer : customers) {
            if (ID == customer.getID()) {
                return customer;
            }
        }
        return null;
    }

    public Accommodation searchAccommodationByID(int ID) {
        for (Accommodation accommodation : accommodations) {
            if (ID == accommodation.getID()) {
                return accommodation;
            }
        }
        return null;
    }

    public TravelPackage searchPackageByID(int ID) {
        for (TravelPackage pkg : packages) {
            if (ID == pkg.getID()) {
                return pkg;
            }
        }
        return null;
    }
}