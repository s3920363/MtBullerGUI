import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;

public class ResortGUI extends JFrame {
    private final JTextArea output = new JTextArea(18, 80);
    private final JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{"All", "Hotel", "Apartment", "Lodge", "Cabin"});
    private final JTextField maxPriceField = new JTextField(7);

    //customers tab
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(14);
    private final JComboBox<String> skillCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Expert"});

    //packages tab
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Accommodation> accommodationCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField(10); // "YYYY-MM-DD" or "now"
    private final JTextField daysField = new JTextField(5);

    //extras tab
    private final JComboBox<TravelPackage> packageForPassCombo = new JComboBox<>();
    private final JRadioButton dailyPassBtn = new JRadioButton("Daily", true);
    private final JRadioButton seasonPassBtn = new JRadioButton("Season");
    private final JTextField passDaysField = new JTextField(5);

    private final JComboBox<TravelPackage> packageForLessonsCombo = new JComboBox<>();
    private final JTextField lessonsCountField = new JTextField(5);


    MtBullerResort resort = new MtBullerResort();

    public ResortGUI() {
        super("Mount Buller Resort GUI");

        resort.populateLists();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accommodations", buildAccommodationsTab());
        tabs.addTab("Customers", buildCustomersTab());
        tabs.addTab("Packages", buildPackagesTab());
        tabs.addTab("Extras", buildExtrasTab());
        tabs.addTab("File", buildFileTab());


        //add components
        JScrollPane scroll = buildOutput();
        add(tabs, BorderLayout.NORTH);

        add(scroll, BorderLayout.CENTER);

        updateCombos();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public static void main(String[] args) {
        new ResortGUI();
    }


    private Component buildAccommodationsTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton listAllBtn = new JButton("List All");
        listAllBtn.addActionListener(e -> {
            output.setText("----- All Accommodations -----\n");
            for (Accommodation a : resort.accommodations) {
                output.append(a + "\n");
            }
        });

        JButton listAvaiBtn = new JButton("List Available");
        listAvaiBtn.addActionListener(e -> {
            output.setText("----- Available Accommodations -----\n");
            for (Accommodation a : resort.accommodations) {
                if (a.isAvailable()) {
                    output.append(a + "\n");
                }
            }
        });

        JButton filterTypeBtn = new JButton("Filter by Type");
        filterTypeBtn.addActionListener(e -> filterByType());

        JButton filterPriceBtn = new JButton("Filter by Price");
        filterPriceBtn.addActionListener(e -> filterByPrice());

        p.add(listAllBtn);
        p.add(listAvaiBtn);
        p.add(typeFilterCombo);
        p.add(filterTypeBtn);
        p.add(maxPriceField);
        p.add(filterPriceBtn);
        return p;
    }

    private Component buildCustomersTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton listBtn = new JButton("List Customers");
        listBtn.addActionListener(e -> {
            output.setText("----- Customers -----\n");
            for (Customer c : resort.customers) output.append(c + "\n");
        });

        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(this::handleAddCustomer);

        p.add(listBtn);
        p.add(new JLabel("Name:"));
        p.add(nameField);
        p.add(new JLabel("Email:"));
        p.add(emailField);
        p.add(new JLabel("Skill:"));
        p.add(skillCombo);
        p.add(addBtn);

        return p;
    }

    private Component buildPackagesTab() {
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton listBtn = new JButton("List Packages");
        listBtn.addActionListener(e -> listPackages());

        JButton createBtn = new JButton("Create Package");
        createBtn.addActionListener(e -> createPackage());

        p1.add(listBtn);
        p1.add(new JLabel("Customer:"));
        p1.add(customerCombo);
        p1.add(new JLabel("Accommodation:"));
        p1.add(accommodationCombo);

        p2.add(new JLabel("Date (YYYY-MM-DD or now):"));
        p2.add(dateField);
        p2.add(new JLabel("Days:"));
        p2.add(daysField);
        p2.add(createBtn);


        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // vertical stacking
        container.add(p1);
        container.add(p2);

        return container;
    }


    private Component buildExtrasTab() {
        //main panel
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        //lift pass panel
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup passGroup = new ButtonGroup();
        passGroup.add(dailyPassBtn);
        passGroup.add(seasonPassBtn);

        JButton addPassBtn = new JButton("Add Lift Pass");
        addPassBtn.addActionListener(e -> handleAddPass());

        passPanel.add(new JLabel("Lift Pass → Package:"));
        passPanel.add(packageForPassCombo);
        passPanel.add(dailyPassBtn);
        passPanel.add(new JLabel("Days:"));
        passPanel.add(passDaysField);
        passPanel.add(seasonPassBtn);
        passPanel.add(addPassBtn);

        //lessons panel
        JPanel lessonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addLessonsBtn = new JButton("Add Lessons");
        addLessonsBtn.addActionListener(e -> handleAddLessons());

        lessonsPanel.add(new JLabel("Lessons → Package:"));
        lessonsPanel.add(packageForLessonsCombo);
        lessonsPanel.add(new JLabel("Count:"));
        lessonsPanel.add(lessonsCountField);
        lessonsPanel.add(addLessonsBtn);

        container.add(passPanel);
        container.add(lessonsPanel);

        return container;
    }

    private void handleAddLessons() {
        TravelPackage pkg = (TravelPackage) packageForLessonsCombo.getSelectedItem();
        if (pkg == null) {
            JOptionPane.showMessageDialog(this, "Select a package.");
            return;
        }
        if (pkg.getHasLessons()) {
            JOptionPane.showMessageDialog(this, "This package already has Lessons.");
            return;
        }

        int count;
        try {
            count = Integer.parseInt(lessonsCountField.getText().trim());
            if (count <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a positive lessons count.");
            return;
        }
        String level = pkg.getCustomer().getSkillLevel();
        Lessons lessons = new Lessons(level, count);
        pkg.setLessons(lessons);
        output.setText("Lessons added successfully!\n" + pkg + "\n");
        updateCombos();
        lessonsCountField.setText("");
    }

    private void handleAddPass() {
        TravelPackage pkg = (TravelPackage) packageForPassCombo.getSelectedItem();
        if (pkg == null) {
            JOptionPane.showMessageDialog(this, "Select a package.");
            return;
        }
        if (pkg.getHasLiftPass()) {
            JOptionPane.showMessageDialog(this, "This package already has a Lift Pass.");
            return;
        }

        LiftPass pass;
        if (seasonPassBtn.isSelected()) {
            pass = new LiftPass("Season", 0);
        } else {
            int days;
            try {
                days = Integer.parseInt(passDaysField.getText().trim());
                if (days <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number of days.");
                return;
            }
            pass = new LiftPass("Daily", days);
        }
        pkg.setLiftPass(pass);
        output.setText("Lift pass added successfully!\n" + pkg + "\n");
        updateCombos();
        passDaysField.setText("");
    }

    private Component buildFileTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton saveBtn = new JButton("Save Packages...");
        saveBtn.addActionListener(e -> savePackages());

        JButton loadBtn = new JButton("Load Packages...");
        loadBtn.addActionListener(e -> readPackages());

        JButton listBtn = new JButton("List Packages");
        listBtn.addActionListener(e -> listPackages());

        p.add(saveBtn);
        p.add(loadBtn);
        p.add(listBtn);
        return p;
    }

    private void savePackages() {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name (leave blank for 'packages.dat'):", "Save Packages", JOptionPane.PLAIN_MESSAGE);

        if (fileName == null) {
            return;
        }

        fileName = fileName.trim();
        if (fileName.isEmpty()) {
            fileName = "packages.dat";
        }

        if (!fileName.toLowerCase().endsWith(".dat")) {
            fileName += ".dat";
        }

        File f = new File(fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(new ArrayList<>(resort.packages));
            output.setText("Packages saved to " + f.getName() + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }

    private void readPackages() {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name (leave blank for 'packages.dat'):", "Load Packages", JOptionPane.PLAIN_MESSAGE);

        if (fileName == null) {
            return;
        }

        //use default when blank
        fileName = fileName.trim();
        if (fileName.isEmpty()) {
            fileName = "packages.dat";
        }

        if (!fileName.toLowerCase().endsWith(".dat")) {
            fileName += ".dat";
        }

        File f = new File(fileName);

        //check exists
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this, "No such file.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            resort.packages = (ArrayList<TravelPackage>) ois.readObject();

            //restore customer and accommodation statuses
            for (TravelPackage pkg : resort.packages) {
                Customer c = resort.searchCustomerByID(pkg.getCustomer().getID());
                if (c != null) {
                    c.setHasPackage();
                } else {
                    Customer newC = pkg.getCustomer();
                    newC.setHasPackage();
                    resort.customers.add(newC);
                }

                Accommodation a = resort.searchAccommodationByID(pkg.getAccommodation().getID());
                if (a != null) a.setAvailable(false);
            }

            listPackages();
            output.append("\nPackages loaded from " + f.getName());
            updateCombos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
        }
    }


    private void createPackage() {
        Customer c = (Customer) customerCombo.getSelectedItem();
        Accommodation a = (Accommodation) accommodationCombo.getSelectedItem();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a customer.");
            return;
        }
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Select an accommodation.");
            return;
        }
        if (!a.isAvailable()) {
            JOptionPane.showMessageDialog(this, "Selected accommodation is not available.");
            return;
        }
        if (c.inPackage()) {
            JOptionPane.showMessageDialog(this, "Selected customer already has a package.");
            return;
        }

        LocalDate date;
        String dateTxt = dateField.getText().trim();
        if (dateTxt.equalsIgnoreCase("now")) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateTxt);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format.");
                return;
            }
        }
        int days;
        try {
            days = Integer.parseInt(daysField.getText().trim());
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a positive number of days.");
            return;
        }

        TravelPackage pkg = new TravelPackage(c, date, days);
        pkg.attachAccommodation(a);
        a.setAvailable(false);
        c.setHasPackage();
        resort.packages.add(pkg);

        output.setText("Package created successfully!\n" + pkg + "\n");
        updateCombos();
        dateField.setText("");
        daysField.setText("");
    }

    private void listPackages() {
        if (resort.packages.isEmpty()) {
            output.setText("There are no packages.\n");
        } else {
            output.setText("----- Packages -----\n");
            for (TravelPackage p : resort.packages) output.append(p + "\n");
        }
    }

    private void filterByPrice() {
        String txt = maxPriceField.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a max price.");
            return;
        }
        try {
            double max = Double.parseDouble(txt);
            if (max <= 0) throw new NumberFormatException();
            output.setText("----- Available Accommodations (Max $" + max + ") -----\n");
            int count = 0;
            for (Accommodation a : resort.accommodations) {
                if (a.isAvailable() && a.getPrice() <= max) {
                    output.append(a + "\n");
                    count++;
                }
            }
            if (count == 0) output.append("No accommodations found under $" + max + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid number greater than 0.");
        }
    }

    private void filterByType() {
        String sel = Objects.toString(typeFilterCombo.getSelectedItem(), "All");
        output.setText("----- Available Accommodations (" + sel + ") -----\n");
        int count = 0;
        for (Accommodation a : resort.accommodations) {
            boolean choice = sel.equalsIgnoreCase("All") || a.getType().equalsIgnoreCase(sel);
            if (choice && a.isAvailable()) {
                output.append(a + "\n");
                count++;
            }
        }
        if (count == 0) output.append("No accommodations found for type: " + sel + "\n");
    }

    private JScrollPane buildOutput() {
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        return new JScrollPane(output);
    }

    private void handleAddCustomer(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skill = Objects.toString(skillCombo.getSelectedItem(), "").toLowerCase();

        if (name.isEmpty() || email.isEmpty() || skill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        if (!(skill.equals("beginner") || skill.equals("intermediate") || skill.equals("expert"))) {
            JOptionPane.showMessageDialog(this, "Skill level must be Beginner, Intermediate, or Expert.");
            return;
        }
        Customer c = new Customer(name, email, skill);
        resort.customers.add(c);
        output.setText("Customer added successfully:\n" + c + "\n");
        updateCombos();
        nameField.setText("");
        emailField.setText("");
    }


    private void updateCombos() {
        //customers without package
        DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
        for (Customer c : resort.customers) if (!c.inPackage()) custModel.addElement(c);
        customerCombo.setModel(custModel);

        //available accommodations
        DefaultComboBoxModel<Accommodation> accModel = new DefaultComboBoxModel<>();
        for (Accommodation a : resort.accommodations) if (a.isAvailable()) accModel.addElement(a);
        accommodationCombo.setModel(accModel);

        //packages without pass
        DefaultComboBoxModel<TravelPackage> passModel = new DefaultComboBoxModel<>();
        for (TravelPackage p : resort.packages) if (!p.getHasLiftPass()) passModel.addElement(p);
        packageForPassCombo.setModel(passModel);

        //packages without lessons
        DefaultComboBoxModel<TravelPackage> lessonsModel = new DefaultComboBoxModel<>();
        for (TravelPackage p : resort.packages) if (!p.getHasLessons()) lessonsModel.addElement(p);
        packageForLessonsCombo.setModel(lessonsModel);

    }

}